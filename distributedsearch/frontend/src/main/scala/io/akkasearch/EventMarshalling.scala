package io.akkasearch

import lib._

import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

case class Error(message: String)

trait EventMarshalling  extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val requestDescriptionFormat = jsonFormat3(RequestDocument)
  implicit val documentFormat = jsonFormat2(IndexedDocument)
  implicit val documentsFormat = jsonFormat1(IndexedDocuments)
}
