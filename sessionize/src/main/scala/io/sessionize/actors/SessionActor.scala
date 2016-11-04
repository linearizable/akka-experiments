package io.sessionize.actors

import akka.actor._
import akka.persistence._
import akka.actor.Props
import io.sessionize.lib._
import scala.util.hashing.{MurmurHash3=>MH3}
import org.joda.time.DateTime
import org.joda.time.Seconds
import org.joda.time.format.DateTimeFormat
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.UUID

class SessionActor extends PersistentActor with ActorLogging {

    var requests = scala.collection.mutable.ListBuffer.empty[ApacheAccessLogRequest]
    //var sessions = scala.collection.mutable.ListBuffer.empty[Session]

    def persistenceId = self.path.name

    val receiveRecover: Receive = {
        case event: Event => updateState(event)
        case RecoveryCompleted =>  {
            //log.info("Calculator recovery completed") //<co id="recovery_completed"/>
        }
    }

    val receiveCommand: Receive = {
        case Add(request)    => persist(Added(request))(updateState)
        case PrintResult     => println(s"the result is: ${requests.length}")
        case GetNumRequests  => sender() ! requests.length
        case Clear           => persist(Reset)(updateState)
    }

    val updateState: Event => Unit = {
        case Reset             => requests = scala.collection.mutable.ListBuffer.empty[ApacheAccessLogRequest]
        case Added(request)    => {
            println("Restoring:: "+request)
            requests += request
        }
    }

    /**
     * Send a tick every minute to check if session is complete
     */
    //val sessionTick = context.system.scheduler.schedule(5000.millis, 5000.millis, self, CheckSessionCompleted)

    // def receive = {
    //     case AddRequest(request: ApacheAccessLogRequest) =>
    //         requests = requests += request
    //     case CheckSessionCompleted =>
    //         if(requests.length > 0) {
    //             val lastRequest = requests.last
    //             val inactivityTime = Seconds.secondsBetween(lastRequest.datetime, SessionizeTime.t).getSeconds()
    //             if(inactivityTime > 300) {
    //
    //                 /**
    //                  * Store session
    //                  */
    //                 val sessionId =  UUID.randomUUID().toString
    //                 val session = Session(sessionId, requests)
    //                 sessions = sessions += session
    //
    //                 /**
    //                  * Empty the requests buffer to create new session
    //                  */
    //                 requests = scala.collection.mutable.ListBuffer.empty[ApacheAccessLogRequest]
    //             }
    //         }
    //     case GetSessions(ip: String) =>
    //         sender() ! Sessions(sessions.toSeq)
    // }
}

case object CheckSessionCompleted
case class Session(id: String, ip: String, duration: Int, numPageViews: Int,
                   bounce: Int, landingPage: String, exitPage: String)

object Session {
    def apply(sessionId: String, requests: Seq[ApacheAccessLogRequest]) = {
        val landingPage = requests.head
        val exitPage = requests.last
        val numRequests = requests.length
        val bounce = if(numRequests == 1) 1 else 0
        val sessionDuration = Seconds.secondsBetween(landingPage.datetime, exitPage.datetime).getSeconds()
        new Session(sessionId, landingPage.ip, sessionDuration, numRequests, bounce, landingPage.url, exitPage.url)
    }
}

case class Sessions(sessions: Seq[Session])

sealed trait Command
case object Clear extends Command
case class Add(request: ApacheAccessLogRequest) extends Command
case object PrintResult extends Command
case object GetNumRequests extends Command

sealed trait Event //<co id="persistence-calc_event"/>
case object Reset extends Event
case class Added(request: ApacheAccessLogRequest) extends Event
