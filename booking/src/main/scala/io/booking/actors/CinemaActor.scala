package io.booking.actors

import akka.actor.Actor
import akka.actor.Props
import io.booking.lib.Akka
import scala.util.hashing.{MurmurHash3=>MH3}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

class CinemaActor(cinema: Cinema) extends Actor {
    def receive = {
        case GetCinema =>
            sender() ! cinema
        case h @ GetHall(hallId, cinemaId) =>
            context.child(hallId) match {
                case Some(x) => x.forward(h)
                case None => sender() ! None
            }
        case AddShow(show) =>
            val hall = cinema.halls.filter(_.id == show.hallId).head
            context.actorOf(Props(classOf[ShowActor], show.movie, hall, show.showTime), generateShowId(show))
        case b @ BookTickets(show, numTickets) =>
            val showId = generateShowId(show)
            context.child(showId) match {
                case Some(x) => x.forward(b)
                case None => sender() ! None
            }
    }

    def generateShowId(show: Show) = {
        val seed = 12345
        val fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")
        val showKey = show.movie.name+"#"+show.hallId+fmt.print(show.showTime)
        val showId = MH3.stringHash(showKey, seed).toString
        showId
    }
}

case class MovieShow(movie: Movie, hall: Hall, showTime: DateTime, seatsAvailable: Int)
