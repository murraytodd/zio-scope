package example

import zio._
import zio.stream._

object ScopedStream extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = ???
}
