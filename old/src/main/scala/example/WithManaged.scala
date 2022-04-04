package example

import zio._

import scala.io.{BufferedSource, Source}

object WithManaged extends ZIOAppDefault {

  val fileReader: ZManaged[Any, Throwable, BufferedSource] = ZManaged.acquireReleaseAttemptWith{println("reading"); Source.fromFile("build.sbt")}(_.close())

  val fileLayer: ZLayer[Any, Throwable, Iterator[String]] = fileReader.map(_.getLines()).toLayer

  val countLinesProgram: ZIO[Iterator[String], Throwable, Int] = for {
    data <- ZIO.service[Iterator[String]]
    size <- ZIO.attempt(data.size)
  } yield size

  override def run: ZIO[ZEnv with ZIOAppArgs, Any, Any] = {
    for {
      data <- fileReader.use(f => Task.attempt(f.getLines().size))
      data2 <- countLinesProgram.provideLayer(fileLayer)
      data3 <- countLinesProgram.provideLayer(fileLayer.fresh)
      _ <- Console.printLine(s"$data, $data2, $data3")
    } yield data
  }

}
