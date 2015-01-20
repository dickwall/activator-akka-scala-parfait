package sample

import java.util.concurrent.atomic.AtomicInteger

import config.ConfigModule

class TestCountingService(implicit config: ConfigModule) extends CountingService {
  private val called = new AtomicInteger(0)

  override def increment(count: Int) = {
    called.incrementAndGet()
    super.increment(count)
  }

  def getNumberOfCalls: Int = called.get()
}
