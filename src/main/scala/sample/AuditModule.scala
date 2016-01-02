package sample

import akka.actor.ActorRef
import config.ConfigModule

trait AuditCompanionModule {
  def auditCompanion: ActorRef
}

trait AuditBusModule {
  def auditBus: ActorRef
}

trait StandardAuditBusModule extends AuditBusModule{ this: ConfigModule =>
  lazy val auditBus: ActorRef = actorSystem.actorOf(AuditBus.props, AuditBus.name)
}

trait StandardAuditCompanionModule extends AuditCompanionModule{ this: ConfigModule with AuditBusModule =>
  lazy val auditCompanion: ActorRef = actorSystem.actorOf(AuditCompanion.props(this), AuditCompanion.name)
}