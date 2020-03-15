package com.amdelamar.chat

import akka.actor.ActorRef

object ChatMessages {

  sealed trait UserEvent

  case class UserJoined(name: String, userActor: ActorRef) extends UserEvent {
    override def equals(that: Any): Boolean = this.asInstanceOf[UserJoined].equals(that)
  }

  case class UserLeft(name: String) extends UserEvent {
    override def equals(that: Any): Boolean = this.asInstanceOf[UserLeft].equals(that)
  }

  case class UserSaid(name: String, message: String) extends UserEvent {
    override def equals(that: Any): Boolean = this.asInstanceOf[UserSaid].equals(that)
  }
}
