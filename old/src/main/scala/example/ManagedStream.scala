package example

import zio._
import zio.stream._

object ManagedStream extends ZIOAppDefault {
  override def run: ZIO[ZEnv with ZIOAppArgs, Any, Any] = ???
}
