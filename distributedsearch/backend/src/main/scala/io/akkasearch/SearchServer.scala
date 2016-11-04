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

object SessionServer extends App {

    val indexerActor = IndexerActor.indexerActor
    val searchActor = SearchActor.searchActor
}
