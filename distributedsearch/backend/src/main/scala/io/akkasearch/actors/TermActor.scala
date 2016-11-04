package io.akkasearch.actors

import akka.actor._
import akka.persistence._
import akka.actor.Props
import io.akkasearch.lib._
import scala.util.hashing.{MurmurHash3=>MH3}
import org.joda.time.DateTime
import org.joda.time.Seconds
import org.joda.time.format.DateTimeFormat
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.UUID

class TermActor(term: String) extends Actor with ActorLogging {

    var docList = scala.collection.mutable.ListBuffer.empty[IndexedDocument]
    var termFreq = scala.collection.mutable.Map[String, Int]()

    def receive = {
        case d @ AddDocument(document: IndexedDocument, tokens: Map[String, List[String]]) =>
            if(docList.filter(_.id == document.id).length == 0) processDocument(d)

        case GetDocuments(keyword: String) =>
            println(termFreq)
            sender() ! IndexedDocuments(docList.toList)
    }

    def processDocument(d: AddDocument) = {
        docList += d.document
        val count = d.tokens.foldLeft(0)((x, kv) => x + kv._2.count(_ == term))
        termFreq += (d.document.id -> count)
    }
}
