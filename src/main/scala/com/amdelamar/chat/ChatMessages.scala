package com.amdelamar.chat

import akka.actor.ActorRef

object ChatMessages {

  sealed trait UserEvent

  case class UserJoined(name: String, userActor: ActorRef) extends UserEvent

  case class UserLeft(name: String) extends UserEvent

  case class UserSaid(name: String, message: String) extends UserEvent

}
