package io.akkasearch

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

trait RestRoutes extends AkkaSearchApi with EventMarshalling with Directives {
    import StatusCodes._

    def routes: Route = indexingRoute ~ searchRoute
    //def routes: Route = indexingRoute

    def indexingRoute =
    pathPrefix("index") {
        pathEndOrSingleSlash {
            post {
                entity(as[RequestDocument]) { document =>
                    onSuccess(indexDocument(document)) {
                        complete(Created, "Document Indexed")
                        // case BoxOffice.EventCreated(event) => complete(Created, event)
                        // case BoxOffice.EventExists =>
                        //     val err = Error(s"$event event exists already.")
                        //     complete(BadRequest, err) //<co id="ch02_complete_request_with_bad_request"/>
                    }
                }
            }
        }
    }

    def searchRoute =
    pathPrefix("search" / Segment) { keyword =>
      pathEndOrSingleSlash {
        get {
          // GET /events
          onSuccess(search(keyword)) { documents =>
            //_.fold(complete(NotFound))(documents => complete(OK, documents))
            complete(OK, documents)
          }
        }
      }
    }
}

trait AkkaSearchApi {
    implicit def executionContext: ExecutionContext
    implicit def requestTimeout: Timeout

    //val sStartUp = SessionServer.startup

    def indexDocument(document: RequestDocument) = Future { SearchServer.indexDocument(document) }

    def search(keyword: String) = SearchServer.search(keyword)
}
