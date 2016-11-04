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

class IndexerActor extends Actor with ActorLogging {

    /**
     * Indexing pipeline
     * Tokenizer -> Lower case filter -> Stop work filter -> Inverted Index
     */
    val stopWordFilterActor = context.actorOf(Props(classOf[StopWordFilterActor], InvertedIndexActor.invertedIndexActor), "StopWordFilterActor")
    val lowercaseFilterActor = context.actorOf(Props(classOf[LowercaseFilterActor], stopWordFilterActor), "LowercaseFilterActor")
    val tokenizerActor = context.actorOf(Props(classOf[TokenizerActor], lowercaseFilterActor), "TokenizerActor")

    def receive = {
        case document: Document =>
            tokenizerActor ! document
    }
}

object IndexerActor {
    def name = "IndexerActor"
    def props = Props[IndexerActor]

    val indexerActor = Akka.system.actorOf(props, name)
}
