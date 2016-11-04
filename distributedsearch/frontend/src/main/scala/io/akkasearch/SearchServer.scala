package io.akkasearch

import akka.pattern.ask
import akka.util.Timeout

import akka.actor.Props
import akka.serialization._
import com.typesafe.config.ConfigFactory

import lib._
import actors.RemoteLookupProxyActor

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
object SearchServer {

    def indexDocument(requestDocument: RequestDocument) = {
        implicit val timeout = Timeout(5 seconds)
        //val indexerActor = Akka.system.actorSelection(getPath("IndexerActor"))
        val proxy = RemoteLookupProxyActor.getActor(getPath("IndexerActor"), "IndexerActor")
        proxy ! requestDocument
    }

    def search(query: String) = {
        implicit val timeout = Timeout(5 seconds)
        val searchActor = Akka.system.actorSelection(getPath("SearchActor"))
        searchActor.ask(Search(query)).mapTo[IndexedDocuments]
    }

    def startup = {
        RemoteLookupProxyActor.getActor(getPath("IndexerActor"), "IndexerActor")
        RemoteLookupProxyActor.getActor(getPath("SearchActor"), "SearchActor")
    }

    def getPath(actorName: String) : String = {
        val config = ConfigFactory.load("application").getConfig("backend")
        val host = config.getString("host")
        val port = config.getString("port")
        val protocol = config.getString("protocol")
        val system = config.getString("system")
        val actor = "user/"+actorName
        s"$protocol://$system@$host:$port/$actor"
    }
}
