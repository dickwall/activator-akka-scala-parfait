package sample

import akka.actor.{ActorRef, Actor}
import config.ConfigModule


trait SampleModule {
  val countingActor: ActorRef
  def countingService: CountingService
}

/**
 * A config module for the counting actor and service.
 */
trait StandardSampleModule extends SampleModule { this: AuditModule with ConfigModule =>

  lazy val countingActor: ActorRef =
    actorSystem.actorOf(CountingActor.props(this), CountingActor.name)

  def countingService: CountingService = new CountingService()(this)
}
