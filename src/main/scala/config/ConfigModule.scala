package config

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

trait ConfigModule {
  def config: Config
  def actorSystem: ActorSystem
}

trait AkkaConfigModule extends ConfigModule {
  lazy val config = ConfigFactory.load()
  lazy val actorSystem: ActorSystem = ActorSystem("main-actor-system", config)
}

