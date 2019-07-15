package com.amdelamar.chat

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

object ChatServer {

  implicit val system = ActorSystem("app")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  private val chatroom = new ChatRoom()

  def main(args: Array[String]): Unit = {
    val webRoute: Route =
      pathEndOrSingleSlash {
        getFromResource("web/index.html")
      } ~
        getFromResourceDirectory("web")

    val apiRoute: Route =
      path( "api" / "chat") {
        get {
          parameters("name") { name =>
            handleWebSocketMessages(chatroom.websocketFlow(name))
          }
        }
      }

    Http().bindAndHandle(apiRoute ~ webRoute, "localhost", 8080)
      .map { _ =>
        println(s"Server is running at http://localhost:8080/")
      }
  }

}
