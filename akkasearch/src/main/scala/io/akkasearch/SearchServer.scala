package io.akkasearch

import akka.pattern.ask
import akka.util.Timeout

import akka.actor.Props

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
object SearchServer {

    def indexDocument(requestDocument: RequestDocument) = {

        val document = Document(
            requestDocument.id,
            Map("title" -> requestDocument.title, "description" -> requestDocument.description),
            Map()
        )
        IndexerActor.indexerActor ! document
    }

    def search(query: String) = {
        implicit val timeout = Timeout(5 seconds)
        //SearchActor.searchActor ! Search(query)
        //InvertedIndexActor.invertedIndexActor.ask(GetDocuments(query)).mapTo[IndexedDocuments]
        SearchActor.searchActor.ask(Search(query)).mapTo[IndexedDocuments]
    }

    // def startup = {
    //     SessionManager.sessionManager ! GetResult("122.168.173.32")
    // }
}
