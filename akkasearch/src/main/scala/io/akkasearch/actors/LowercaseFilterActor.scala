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

class LowercaseFilterActor(pipe: ActorRef) extends Actor with ActorLogging {

    def receive = {
        case document: Document =>
            val lowercaseTokens = scala.collection.mutable.Map[String, List[String]]()
            document.tokens.foreach { case(k, v) =>
                lowercaseTokens(k) = v.map(_.toLowerCase)
            }
            val filteredDocument = document.copy(tokens = lowercaseTokens.toMap)
            //println(filteredDocument)
            pipe ! filteredDocument
    }
}
