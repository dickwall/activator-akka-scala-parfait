package sample

import akka.actor.ActorRef
import config.ConfigModule

trait AuditModule {
  def auditCompanion: ActorRef
  def auditBus: ActorRef
}

trait StandardAuditModule extends AuditModule { this: ConfigModule =>
  lazy val auditCompanion: ActorRef = actorSystem.actorOf(AuditCompanion.props(this), AuditCompanion.name)
  lazy val auditBus: ActorRef = actorSystem.actorOf(AuditBus.props, AuditBus.name)
}

/**
 * A Guice module for the audit actors.
 *
 * This module provides top level actors for wiring into other actor constructors. Top level actors should be
 * used sparingly and only to wire-up few top-level components.
 */
/*class StandardAuditModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  override def configure() {
    bind[Actor].annotatedWith(Names.named(AuditCompanion.name)).to[AuditCompanion]
    bind[Actor].annotatedWith(Names.named(AuditBus.name)).to[AuditBus]
  }

  @Provides
  @Named(AuditCompanion.name)
  def provideAuditCompanionRef(@Inject() system: ActorSystem): ActorRef = provideActorRef(system, AuditCompanion.name)

  @Provides
  @Singleton
  @Audit
  def provideAuditBusRef(@Inject() system: ActorSystem): ActorRef = provideActorRef(system, AuditBus.name)

}*/
