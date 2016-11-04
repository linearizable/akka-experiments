package io.akkasearch.lib

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object Akka {
    val config = ConfigFactory.load("application")
    val system = ActorSystem("AkkaSearchActorSystem", config)
}

case class IndexedDocument(id: String, fields: Map[String, String])
case class AddDocument(document: IndexedDocument, tokens: Map[String, List[String]])
case class IndexedDocuments(documents: List[IndexedDocument])
case class GetDocuments(keyword: String)
case class RequestDocument(id: String, title: String, description: String)
case class Search(query: String)
