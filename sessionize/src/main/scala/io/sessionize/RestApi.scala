package io.sessionize

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
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

class RestApi(system: ActorSystem, timeout: Timeout) extends RestRoutes {
    implicit val requestTimeout = timeout
    implicit def executionContext = system.dispatcher
}

trait RestRoutes extends SessionizeApi with EventMarshalling with Directives {
    import StatusCodes._

    def routes: Route = requestProcessorRoute ~ sessionRoute

    def requestProcessorRoute =
    pathPrefix("processRequest") {
        pathEndOrSingleSlash {
            post {
                entity(as[RequestDescription]) { r =>
                    onSuccess(processRequest(r)) {
                        complete(Created, "request")
                        // case BoxOffice.EventCreated(event) => complete(Created, event)
                        // case BoxOffice.EventExists =>
                        //     val err = Error(s"$event event exists already.")
                        //     complete(BadRequest, err) //<co id="ch02_complete_request_with_bad_request"/>
                    }
                }
            }
        }
    }

    def sessionRoute =
    pathPrefix("sessions" / Segment) { ip =>
      pathEndOrSingleSlash {
        get {
          // GET /events
          onSuccess(getSessions(ip)) { sessions =>
            complete(OK, sessions)
          }
        }
      }
    }
}

trait SessionizeApi {
    implicit def executionContext: ExecutionContext
    implicit def requestTimeout: Timeout

    val sStartUp = SessionServer.startup

    def processRequest(r: RequestDescription) = Future { SessionServer.processRequest(r) }

    def getSessions(ip: String) = SessionServer.getSessions(ip)
}
