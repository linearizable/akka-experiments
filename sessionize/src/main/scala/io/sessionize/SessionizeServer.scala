package io.sessionize

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.{ Config, ConfigFactory }
import akka.util.Timeout
import scala.io.StdIn

object WebServer extends RequestTimeout {
    def main(args: Array[String]) {

        val config = ConfigFactory.load()

        implicit val system = ActorSystem("my-system")
        implicit val materializer = ActorMaterializer()
        // needed for the future flatMap/onComplete in the end
        implicit val executionContext = system.dispatcher

        val api = new RestApi(system, requestTimeout(config)).routes
        val bindingFuture = Http().bindAndHandle(api, "localhost", 8080)

        println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
        StdIn.readLine() // let it run until user presses return
        bindingFuture
            .flatMap(_.unbind()) // trigger unbinding from the port
            .onComplete(_ => system.terminate()) // and shutdown when done
    }
}

trait RequestTimeout {
    import scala.concurrent.duration._
    def requestTimeout(config: Config): Timeout = { //<co id="ch02_timeout_spray_can"/>
        val t = config.getString("akka.http.server.request-timeout")
        val d = Duration(t)
        FiniteDuration(d.length, d.unit)
    }
}
