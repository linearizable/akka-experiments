package io.sessionize

import akka.pattern.ask
import akka.util.Timeout

import actors._
import lib._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import net.liftweb.json._

import java.util.UUID
import java.io.InputStream
import org.joda.time._;
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter;

import scala.concurrent.Future

//object SessionServer extends App {
object SessionServer {

    def processRequest(r: RequestDescription) = {
        try {
            val request = ApacheAccessLogRequest(r.request)
            SessionizeTime.t = request.datetime
            SessionManager.sessionManager ! AddRequest(ApacheAccessLogRequest(r.request))
        } catch {
            case ex: Exception => println("Log parse error")
        }
    }

    def getSessions(ip: String) = {
        implicit val timeout = Timeout(5 seconds)
        SessionManager.sessionManager.ask(GetSessions(ip)).mapTo[Sessions]
    }

    def startup = {
        SessionManager.sessionManager ! GetResult("122.168.173.32")
    }
}
