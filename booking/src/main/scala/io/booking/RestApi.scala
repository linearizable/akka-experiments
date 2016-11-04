package io.booking

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

class RestApi(system: ActorSystem, timeout: Timeout) extends RestRoutes {
    implicit val requestTimeout = timeout
    implicit def executionContext = system.dispatcher

    def createBoxOffice = {
        //system.actorOf(BoxOffice.props, BoxOffice.name)
        BoxOffice.create
    }
}

trait RestRoutes extends CinemaApi with EventMarshalling with Directives {
    import StatusCodes._

    def routes: Route = cinemasRoute

    def cinemasRoute =
    pathPrefix("cinemas") {
        pathEndOrSingleSlash {
            get {
                // GET /events
                onSuccess(getCinemas()) { cinemas =>
                    complete(OK, cinemas)
                }
            }
        }
    }
}

//<start id="ch02_boxoffice_api"/>
trait CinemaApi {

  def createBoxOffice(): Unit

  implicit def executionContext: ExecutionContext
  implicit def requestTimeout: Timeout

  //lazy val boxOffice = createBoxOffice()
  createBoxOffice()

  // def createEvent(event: String, nrOfTickets: Int) =
  //   boxOffice.ask(CreateEvent(event, nrOfTickets))
  //     .mapTo[EventResponse]

  def getCinemas() =
    BoxOffice.getCinemas

  // def getEvent(event: String) =
  //   boxOffice.ask(GetEvent(event))
  //     .mapTo[Option[Event]]
  //
  // def cancelEvent(event: String) =
  //   boxOffice.ask(CancelEvent(event))
  //     .mapTo[Option[Event]]
  //
  // def requestTickets(event: String, tickets: Int) =
  //   boxOffice.ask(GetTickets(event, tickets))
  //     .mapTo[TicketSeller.Tickets]
}
// //<end id="ch02_boxoffice_api"/>
