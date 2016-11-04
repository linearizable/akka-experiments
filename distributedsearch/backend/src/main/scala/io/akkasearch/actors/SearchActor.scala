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
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.UUID
import akka.util.Timeout
import akka.pattern.ask

class SearchActor extends Actor with ActorLogging {
    def receive = {
        case Search(query: String) =>
            implicit val timeout = Timeout(5 seconds)
            val terms = query.toLowerCase.split(" ").toList
            def getDocuments = terms.map(term => {
                InvertedIndexActor.invertedIndexActor.ask(GetDocuments(term)).mapTo[IndexedDocuments]
            })

            val s = sender()

            Future.sequence(getDocuments).
            map(c => c.map(_.documents)).
            map(_.flatten).
            map(c => s ! IndexedDocuments(c))
    }
}

object SearchActor {
    def name = "SearchActor"
    def props = Props[SearchActor]

    val searchActor = Akka.system.actorOf(props, name)
}
