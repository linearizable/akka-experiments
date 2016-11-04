package io.akkasearch.actors

import akka.actor._
import akka.actor.ActorIdentity
import akka.actor.Identify
import io.akkasearch.lib._

import scala.concurrent.duration._

class RemoteLookupProxyActor(path: String) extends Actor with ActorLogging {
    context.setReceiveTimeout(3 seconds)

    sendIdentifyRequest()

    def sendIdentifyRequest() = {
        println("Sending identify request to "+path)
        val selection = context.actorSelection(path)
        selection ! Identify(path)
    }

    def receive = identify

    def identify : Receive = {
        case ActorIdentity(`path`, Some(actor)) =>
            println("Received successful identity "+path)
            context.setReceiveTimeout(Duration.Undefined)
            context.become(active(actor))
            context.watch(actor)

        case ActorIdentity(`path`, None) =>
            println(s"Remote actor with path $path is not available.")
            log.error(s"Remote actor with path $path is not available.")

        case ReceiveTimeout =>
            log.error(s"Receive timed out")
            sendIdentifyRequest()

        case msg : Any =>
            println(s"Ignoring message $msg, remote actor is not ready yet.")
            log.error(s"Ignoring message $msg, remote actor is not ready yet.")
    }

    def active(actor: ActorRef) : Receive = {
        case Terminated(actorRef) =>
            context.become(identify)
            context.setReceiveTimeout(3 seconds)
            sendIdentifyRequest()

        case msg : Any =>
            println(s"Forwarding $msg")
            actor forward msg
    }
}

object RemoteLookupProxyActor {
    var actors = scala.collection.mutable.Map[String, ActorRef]()
    def getActor(path: String, name: String) : ActorRef = {
        val actor = actors.get(path) match {
            case Some(a) => a
            case None => {
                println("Creating actor "+name+" for path "+path)
                val newActor = Akka.system.actorOf(Props(classOf[RemoteLookupProxyActor], path), name)
                actors += (path -> newActor)
                newActor
            }
        }
        actor
    }
}
