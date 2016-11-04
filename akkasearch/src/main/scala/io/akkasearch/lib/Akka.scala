package io.akkasearch.lib

import akka.actor.ActorSystem

object Akka {
    val system = ActorSystem("AkkaSearchActorSystem")
}
