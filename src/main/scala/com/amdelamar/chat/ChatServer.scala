package com.amdelamar.chat

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

object ChatServer {

  implicit val system = ActorSystem("app")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(16))

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
