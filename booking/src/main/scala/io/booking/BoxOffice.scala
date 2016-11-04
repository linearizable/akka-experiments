package io.booking

import akka.pattern.ask
import akka.util.Timeout

import io.booking.actors._

import io.booking.lib.Akka
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import net.liftweb.json._

import java.util.UUID
import java.io.InputStream
import org.joda.time._;
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter;

import scala.concurrent.Future

object BoxOffice extends App {
    def create = {
        val cinemas = loadCinemas
        cinemas.foreach(c => CinemaManager.cinemaManager ! AddCinema(c))
        Thread.sleep(2000)
        val shows = loadShows
        shows.foreach(s => CinemaManager.cinemaManager ! AddShow(s))
    }

    def getCinemas = {
        implicit val timeout = Timeout(5 seconds)
        //CinemaManager.cinemaManager.ask(GetCinemas).mapTo[Cinemas]
        CinemaManager.cinemaManager.ask(GetCinemas).mapTo[Cinemas]
        //val b = Future(List(Cinema("A", "B", List())))
        //b
    }

    def loadCinemas : Seq[Cinema] = {
        implicit val formats = DefaultFormats

        val stream : InputStream = getClass.getResourceAsStream("/cinemas.json")
        val cinemaData = scala.io.Source.fromInputStream(stream).getLines.mkString("\n")

        val jsonAST = parse(cinemaData)
        val cinemaChildren = (jsonAST \\ "cinemas").children

        val cinemas : Seq[Cinema] = for(c <- cinemaChildren) yield c.extract[Cinema]
        cinemas
    }

    def loadShows : Seq[Show] = {
        implicit val formats = DefaultFormats

        val stream : InputStream = getClass.getResourceAsStream("/movies.json")
        val movieData = scala.io.Source.fromInputStream(stream).getLines.mkString("\n")

        val jsonAST = parse(movieData)
        val x = jsonAST.asInstanceOf[JObject].values

        val shows = x.map { case (movieName, cinemaList) => {
            val cinemas = cinemaList.asInstanceOf[List[Map[String, Any]]].head
            cinemas.map { case (cinemaId, hallList) => {
                val halls = hallList.asInstanceOf[Map[String, List[String]]]
                halls.map { case (hallId, showTimings) => {
                    showTimings.map(showTime => Show(Movie(movieName), cinemaId, hallId, showDateTime(showTime)))
                }}.flatten
            }}.flatten
        }}.flatten

        shows.toList
    }

    def showDateTime(showTime: String) = {
        val today = new LocalDate
        DateTime.parse(today.toString+" "+showTime, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm"))
    }
}
