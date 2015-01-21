import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import config.{AkkaConfigModule, ConfigModule}
import sample.CountingActor.{Count, Get}
import sample._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * A main class to start up the application.
 */
object Main extends App {

  implicit val config: SystemModule =
    new SystemModule with StandardCountingModule 
    with StandardAuditModule with AkkaConfigModule {}

  // this could be called inside a supervisor actor to create a supervisor hierarchy
  val counter = config.countingActor

  // tell it to count three times
  counter ! Count
  counter ! Count
  counter ! Count

  // Create a second counter to demonstrate that `AuditCompanion` is injected under Prototype
  // scope, which means that every `CountingActor` will get its own instance of `AuditCompanion`.
  // However `AuditBus` is injected under Singleton scope. Therefore every `AuditCompanion`
  // will get a reference to the same `AuditBus`.
  val counter2 = config.countingActor
  counter2 ! Count
  counter2 ! Count

  // print the result
  for {
    actor <- Seq(counter, counter2)
    result <- actor.ask(Get)(3.seconds).mapTo[Int]
  } {
    println(s"Got back $result from $counter")
  }

  config.actorSystem.shutdown()
  config.actorSystem.awaitTermination()
}
