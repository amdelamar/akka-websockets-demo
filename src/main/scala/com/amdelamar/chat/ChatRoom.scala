package com.amdelamar.chat

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import com.amdelamar.chat.ChatMessages.{UserJoined, UserLeft, UserSaid}
import org.reactivestreams.Publisher
import scala.collection.mutable

class ChatRoom()(implicit system: ActorSystem, mat: Materializer) {
  private val roomActor = system.actorOf(Props(classOf[ChatRoomActor]))

  def websocketFlow(name: String): Flow[Message, Message, Any] = {
    val (actorRef: ActorRef, publisher: Publisher[TextMessage.Strict]) =
      Source.actorRef[String](16, OverflowStrategy.fail)
        .map(msg => TextMessage.Strict(msg))
        .toMat(Sink.asPublisher(false))(Keep.both).run()

    // Announce the user has joined
    roomActor ! UserJoined(name, actorRef)

    val sink: Sink[Message, Any] = Flow[Message]
      .map {
        case TextMessage.Strict(msg) =>
          // Incoming message from ws
          roomActor ! UserSaid(name, msg)
      }
      .to(Sink.onComplete( _ =>
        // Announce the user has left
        roomActor ! UserLeft(name)
      ))

    // Pair sink and source
    Flow.fromSinkAndSource(sink, Source.fromPublisher(publisher))
  }
}

class ChatRoomActor() extends Actor {
  val users: mutable.Map[String, ActorRef] = mutable.Map.empty[String, ActorRef]

  override def receive: Receive = {
    case UserJoined(name, actorRef) =>
      users.put(name, actorRef)
      println(s"$name joined the chatroom.")
      broadcast(s"$name joined the chatroom.")

    case UserLeft(name) =>
      users.remove(name)
      println(s"$name left the chatroom.")
      broadcast(s"$name left the chatroom.")

    case UserSaid(name, msg) =>
      println(s"$name: $msg")
      broadcast(s"$name: $msg")
  }

  /** Broadcast the message to all other users */
  def broadcast(msg: String): Unit =
    users.values.foreach(_ ! msg)
}

