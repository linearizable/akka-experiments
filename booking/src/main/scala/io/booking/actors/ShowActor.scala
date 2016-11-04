package io.booking.actors

import akka.actor.Actor
import akka.actor.Props
import io.booking.lib.Akka
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

class ShowActor(movie: Movie, hall: Hall, showTime: DateTime) extends Actor {
    val totalTickets = hall.seats
    var ticketsAvailable = totalTickets

    def receive = {
        case BookTickets(show, numTickets) =>
            if(numTickets > ticketsAvailable) {
                sender() ! TicketsNotAvailable
            } else {
                ticketsAvailable = ticketsAvailable-numTickets
                sender() ! TicketsSuccessfullyBooked
            }
    }
}

case object TicketsNotAvailable
case object TicketsSuccessfullyBooked
