package io.booking.lib

import akka.actor.ActorSystem

object Akka {
    val system = ActorSystem("BookingActorSystem")
}
