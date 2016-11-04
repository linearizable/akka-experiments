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

class TokenizerActor(pipe: ActorRef) extends Actor with ActorLogging {
//class TokenizerActor extends Actor with ActorLogging {

    def receive = {
        case document: Document =>
            val tokens = scala.collection.mutable.Map[String, List[String]]()
            document.fields.foreach { case(k, v) =>
                tokens(k) = tokenize(v)
            }
            val tokenizedDocument = document.copy(tokens = tokens.toMap)
            //println(tokenizedDocument)
            pipe ! tokenizedDocument
    }

    def tokenize(s: String) = {
        s.split(" ").toList.filter(_.nonEmpty)
    }
}

case class Document(id: String, fields: Map[String, String], tokens: Map[String, List[String]])
