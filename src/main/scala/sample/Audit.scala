package sample

import akka.actor.{Props, Actor}

object AuditBus {
  val name = "AuditBus"
  def props = Props(new AuditBus)

  case class AuditEvent(auditCompanionCreated: Long, msg: Any)
}


class AuditBus extends Actor {

  def receive = {
    case AuditBus.AuditEvent(companionCreated, msg) =>
      println(s"[AuditBus:${self.hashCode()}] Message '$msg' received from '$sender'. AuditCompanion created at '$companionCreated'.")
  }
}


object AuditCompanion {
  val name = "AuditCompanion"
  def props(implicit auditBusModule: AuditBusModule) = Props(new AuditCompanion)
}


class AuditCompanion(implicit auditBusModule: AuditBusModule) extends Actor {
  val created = System.currentTimeMillis

  val auditBus = auditBusModule.auditBus
  def receive = {
    case msg => auditBus forward AuditBus.AuditEvent(created, msg)
  }
}


