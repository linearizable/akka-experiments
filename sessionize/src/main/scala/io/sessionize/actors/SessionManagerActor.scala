package io.sessionize.actors

import akka.actor.Actor
import akka.actor.Props
import akka.pattern.{ask, pipe}
import io.sessionize.lib._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.util.Timeout
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

class SessionManager extends Actor {
    def receive = {
        case r @ AddRequest(request: ApacheAccessLogRequest) =>
            context.child(request.ip).getOrElse {
                //println("creating actor "+request.ip)
                context.actorOf(Props(classOf[SessionActor]), request.ip)
            //} ! forward(r)
        } ! Add(request)
        case g @ GetSessions(ip: String) =>
            context.child(ip) match {
                case Some(x) => x.forward(g)
                case None => sender() ! None
            }
        case r @ GetResult(ip) =>
            println("getting result")
            implicit val timeout = Timeout(5 seconds)
            context.child(ip).getOrElse {
                //println("creating actor "+request.ip)
                context.actorOf(Props(classOf[SessionActor]), ip)
            }.ask(GetNumRequests).map(println)
    }
}

object SessionManager {
    def name = "SessionManager"
    def props = Props[SessionManager]

    val sessionManager = Akka.system.actorOf(props, name)
}

case class AddRequest(request: ApacheAccessLogRequest)
case class GetSessions(ip: String)
case class GetResult(ip: String)
