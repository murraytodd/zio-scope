package example

import zio._

import java.io.IOException
import scala.io.{BufferedSource, Source}

object WithManaged extends ZIOAppDefault {

  val fileReader: ZManaged[Any, IOException, BufferedSource] = ZManaged.acquireReleaseAttemptWith(Source.fromFile("build.sbt"))(_.close()).refineToOrDie[IOException]
  val fileReader2: ZManaged[Any, IOException, BufferedSource] = ZManaged.fromAutoCloseable(ZIO.attemptBlockingIO(Source.fromFile("build.sbt")))

  val fileLayer: ZLayer[Any, IOException, Iterator[String]] = fileReader.map(_.getLines()).toLayer

  val getLineCount: ZIO[Any, IOException, Int] = fileReader.use(f => Task.attemptBlocking(f.getLines().size).refineToOrDie[IOException])

  val countLinesProgram: ZIO[Iterator[String], IOException, Int] = for {
    data <- ZIO.service[Iterator[String]]
    size <- ZIO.attemptBlockingIO(data.size)
  } yield size

  override def run: ZIO[ZEnv with ZIOAppArgs, IOException, Int] = {
    for {
      data <- fileReader.use(f => Task.attemptBlocking(f.getLines().size).refineToOrDie[IOException])
      data2 <- countLinesProgram.provideLayer(fileLayer)
      _ <- Console.printLine(s"$data, $data2")
    } yield data
  }

}
