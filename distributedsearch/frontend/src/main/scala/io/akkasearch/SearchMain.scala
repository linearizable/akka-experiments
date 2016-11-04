
object SearchMain extends App {

val conf = """
akka {
    actor {
        provider = "akka.remote.RemoteActorRefProvider"
    }
    remote {
        enabled-transports = ["akka.remote.netty.tcp"]
        netty.tcp {
            hostname = "0.0.0.0"
            port = 2552
        }
    }
}
"""
import com.typesafe.config._
import akka.actor._
val config = ConfigFactory.parseString(conf)
val frontend= ActorSystem("frontend", config)

val path = "akka.tcp://AkkaSearchActorSystem@0.0.0.0:2551/user/IndexerActor"
val simple = frontend.actorSelection(path)

simple ! "hello"    
}
