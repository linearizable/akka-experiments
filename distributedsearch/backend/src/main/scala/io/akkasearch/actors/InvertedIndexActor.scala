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
import scala.util.hashing.MurmurHash3

class InvertedIndexActor extends Actor with ActorLogging {

    def receive = {
        case document: Document =>
            document.tokens.foreach { case(field, tokens) =>
                tokens.foreach (token => {
                    context.child(termActorName(token)).getOrElse {
                        context.actorOf(Props(classOf[TermActor], token), termActorName(token))
                    } ! AddDocument(IndexedDocument(document.id, document.fields), document.tokens)
                })
            }

        case g @ GetDocuments(keyword: String) =>
            context.child(termActorName(keyword)) match {
                case Some(x) => x.forward(g)
                case None => sender() ! IndexedDocuments(List.empty)
            }
    }

    def termActorName(token: String) = MurmurHash3.stringHash(token).toString
}

object InvertedIndexActor {
    def name = "InvertedIndexActor"
    def props = Props[InvertedIndexActor]

    val invertedIndexActor = Akka.system.actorOf(props, name)
}
