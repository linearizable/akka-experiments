package io.akkasearch.lib

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object Akka {
    val config = ConfigFactory.load("application")
    val system = ActorSystem("AkkaSearchFrontEndActorSystem", config)
}

case class RequestDocument(id: String, title: String, description: String)
case class IndexedDocument(id: String, fields: Map[String, String])
case class IndexedDocuments(documents: List[IndexedDocument])
case class AddDocument(document: IndexedDocument, tokens: Map[String, List[String]])
case class GetDocuments(keyword: String)
case class Search(query: String)
case class Document(id: String, fields: Map[String, String], tokens: Map[String, List[String]])
