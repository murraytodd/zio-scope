package example

import zio._

import scala.io.{BufferedSource, Source}

object WithScope extends ZIOAppDefault {

  val fileReader: ZIO[Scope, Throwable, BufferedSource] = ZIO.fromAutoCloseable(Task.attempt(Source.fromFile("build.sbt")))
  val fileReader2: ZIO[Scope, Throwable, BufferedSource] = ZIO.acquireRelease(ZIO.attemptBlockingIO(Source.fromFile("build.sbt")))(s => ZIO.succeed(s.close()))

  val fileLayer = fileReader.map(_.getLines()).toLayer

  val countLinesProgram: ZIO[Iterator[String], Throwable, Int] = for {
    data <- ZIO.service[Iterator[String]]
    size <- ZIO.attempt(data.size)
  } yield size

  override def run: ZIO[Scope, Throwable, Int] = {
  //  ZIO.scoped {
      for {
        data <- fileReader2.flatMap(f => Task.attempt(f.getLines().size))   // fileReader.use(f => Task.attempt(f.getLines().size))
        data2 <- countLinesProgram.provideLayer(fileLayer)
        data3 <- countLinesProgram.provideLayer(fileLayer.fresh)
        _ <- Console.printLine(s"$data, $data2, $data3")
      } yield data
  //  }
  }
}
