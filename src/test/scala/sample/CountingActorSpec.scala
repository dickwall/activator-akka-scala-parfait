package sample

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import config.AkkaConfigModule
import org.scalatest.{BeforeAndAfterAll, Matchers, OneInstancePerTest, WordSpecLike}
import sample.CountingActor.{Count, Get}

import scala.concurrent.Await
import scala.concurrent.duration._

class CountingActorSpec(_system: ActorSystem) extends TestKit(_system)
    with ImplicitSender with WordSpecLike with OneInstancePerTest
    with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("CountingActorSpec"))

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "a Parfait-managed count actor" must {
    "send the correct count to its counting service" in {
      val testConfig: SystemModule =
        new SystemModule with StandardCountingModule 
            with StandardAuditModule with AkkaConfigModule {
          override lazy val actorSystem: ActorSystem = _system
          override lazy val countingService = new TestCountingService()(this)
        }

      val counter = testConfig.countingActor

      // tell it to count three times
      counter ! Count
      counter ! Count
      counter ! Count

      // check that it has counted correctly
      val duration = 3.seconds
      val result = counter.ask(Get)(duration).mapTo[Int]
      Await.result(result, duration) should be (3)

      // check that it called the sample.TestCountingService the right number of times
      val testService = testConfig.countingService.asInstanceOf[TestCountingService]
      testService.getNumberOfCalls should be(3)
    }

    "send messages to its audit companion" in {
      val auditCompanionProbe: TestProbe = new TestProbe(_system)
      val testConfig: SystemModule =
        new SystemModule with StandardCountingModule with StandardAuditModule with AkkaConfigModule {
          override lazy val actorSystem: ActorSystem = _system
          override lazy val auditCompanion = auditCompanionProbe.ref
        }

      val counter = testConfig.countingActor

      counter ! Count
      auditCompanionProbe.expectMsgClass(classOf[String])
    }
  }

}
