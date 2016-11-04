package io.sessionize.lib

import akka.actor.ActorSystem

object Akka {
    val system = ActorSystem("SessionizeActorSystem")
}
