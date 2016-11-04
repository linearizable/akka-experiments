package io.booking.actors

import akka.actor.Actor
import akka.actor.Props
import akka.pattern.{ask, pipe}
import io.booking.lib.Akka

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.util.Timeout
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

class CinemaManager() extends Actor {
    def receive = {
        case AddCinema(cinema: Cinema) =>
            context.child(cinema.id).getOrElse {
                context.actorOf(Props(classOf[CinemaActor], cinema), cinema.id)
            }
        case GetCinemas =>
            implicit val timeout = Timeout(5 seconds)
            def getCinemas = context.children.map(_.ask(GetCinema).mapTo[Cinema])
            val s = sender()
            Future.sequence(getCinemas).map(c => s ! Cinemas(c.toList))

        case GetAllMovies =>
            implicit val timeout = Timeout(5 seconds)
            def getMovies = context.children.map(_.ask(GetMovies).mapTo[Cinema])
            val s = sender()
            Future.sequence(getMovies).map(c => s ! Cinemas(c.toList))

        case h @ GetHall(hallId, cinemaId) =>
            context.child(cinemaId) match {
                case Some(x) => x.forward(h)
                case None => sender() ! None
            }
        case a @ AddShow(show) =>
            context.child(show.cinemaId) match {
                case Some(x) => x.forward(a)
                case None =>
            }
        case b @ BookTickets(show, numTickets) =>
            context.child(show.cinemaId) match {
                case Some(x) => x.forward(b)
                case None =>
            }
    }
}

object CinemaManager {
    def name = "CinemaManager"
    def props = Props[CinemaManager]

    val cinemaManager = Akka.system.actorOf(props, name)
}

case class Hall(id: String, name: String, seats: Int)
case class Halls(halls: List[Hall])
case class Cinema(id: String, name: String, halls: List[Hall])
case class Cinemas(cinemas: List[Cinema])
case class AddCinema(cinema: Cinema)
case object GetCinemas
case class GetHall(hallId: String, cinemaId: String)
case object GetCinema
case class Movie(name: String)
case class Show(movie: Movie, cinemaId: String, hallId: String, showTime: DateTime)
case class AddShow(show: Show)
case class BookTickets(show: Show, numTickets: Int)
case object GetAllMovies
case object GetMovies
