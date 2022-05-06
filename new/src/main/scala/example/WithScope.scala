package example

import zio._

import java.io.IOException
import scala.io.{BufferedSource, Source}

object WithScope extends ZIOAppDefault {

  val fileReader: ZIO[Scope, IOException, BufferedSource] = ZIO.fromAutoCloseable(
    ZIO.attemptBlockingIO(Source.fromFile("build.sbt"))
  )
  val fileReader2: ZIO[Scope,IOException, BufferedSource] = ZIO.acquireRelease(ZIO.attemptBlockingIO(Source.fromFile("build.sbt")))(s => ZIO.succeedBlocking(s.close()))

  val fileLayer: ZLayer[Scope, IOException, Iterator[String]] = ZLayer.fromZIO(fileReader.map(_.getLines()))
  val fileLayer2: ZLayer[Scope, IOException, Iterator[String]] =
    ZLayer.fromZIO(
      ZIO.acquireRelease(ZIO.attemptBlockingIO(Source.fromFile("build.sbt")))(f => ZIO.succeedBlocking(f.close))
    ).project(_.getLines())

  val fileLayer3: ZLayer[Any, IOException, Iterator[String]] = ZLayer.scoped(fileReader.map(_.getLines()))

  val read: ZIO[Any, IOException, Int] = ZIO.scoped { fileReader.flatMap(f => ZIO.attemptBlockingIO(f.getLines().size))}

  val countLinesProgram: ZIO[Iterator[String], IOException, Int] = for {
    data <- ZIO.service[Iterator[String]]
    size <- ZIO.attemptBlockingIO(data.size)
  } yield size

  override def run: ZIO[Any, IOException, Int] = {
    for {
      data <- ZIO.scoped { countLinesProgram.provideLayer(fileLayer) }
      _ <- Console.printLine(s"$data")
    } yield data
  }
}
