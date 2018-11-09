package io.chrisdavenport.progressbar

import cats.implicits._
import cats.effect._
import cats.effect.concurrent._
import fs2._
// import org.jline.terminal.Terminal
// import org.jline.terminal.TerminalBuilder
import scala.concurrent.duration._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = for{
    term <- IO(org.jline.terminal.TerminalBuilder.terminal())

    // size <- term.get.flatMap(term => IO(term.getWidth))
    // _ <- term.get.flatMap(_ => runTimer[IO](size, ProgressBarStyle.COLORFUL_UNICODE_BLOCK).compile.drain)
    tracker <- ProgressTracker.of[IO](100)
    _ <- Stream(
      runProgressBar[IO](0.05.seconds, IO(term.getWidth), tracker, ProgressBarStyle.ASCII),
      Stream.awakeEvery[IO](1.second).evalMap(_ => tracker.increment).drain
    ).parJoinUnbounded
      .take(1)
      .compile
      .drain 
    // tracker2 <- ProgressTracker.of[IO](100)
    // _ <- Stream(
    //   runProgressBar[IO](0.05.seconds, IO(term.getWidth), tracker2, ProgressBarStyle.COLORFUL_UNICODE_BLOCK),
    //   Stream.awakeEvery[IO](0.05.second).evalMap(_ => tracker2.increment).drain
    // ).parJoinUnbounded
    //   .take(1)
    //   .compile
    //   .drain 
    // tracker3 <- ProgressTracker.of[IO](100)
    // _ <- Stream(
    //   runProgressBar[IO](0.05.seconds, IO(term.getWidth), tracker3, ProgressBarStyle.UNICODE_BLOCK),
    //   Stream.awakeEvery[IO](0.05.second).evalMap(_ => tracker3.increment).drain
    // ).parJoinUnbounded
    //   .take(1)
    //   .compile
    //   .drain 
    
  } yield ExitCode.Success

  final case class ProgressTracker[F[_]](totalIncrements: Int, increment: F[Unit], getCurrent: F[Int])
  object ProgressTracker {
    def of[F[_]: Sync](int: Int): F[ProgressTracker[F]] =  Ref.of[F, Int](0).map{ref => 
      ProgressTracker(int, ref.update(i => if (i === int) i else i + 1), ref.get)
    }

  }

  def runProgressBar[F[_]: Sync : Timer](refreshTime: FiniteDuration, sizeF: F[Int],progress: ProgressTracker[F], style: ProgressBarStyle) : Stream[F, Unit] = {
    val charsToRemove = style.leftBracket.size + style.rightBracket.size + 1
    Stream.repeatEval(progress.getCurrent)
      .takeWhile(_ < progress.totalIncrements, true)
      .zipLeft(Stream.awakeEvery[F](refreshTime)) // Artificial Slowdown
      .covary[F]
      .evalMap{ i => 
        for {
          size <- sizeF
          ratio = i.toDouble / progress.totalIncrements.toDouble
          iRatio = (ratio * size)
          maxSize = size - charsToRemove
          string = if (i < progress.totalIncrements){
            style.leftBracket + List.fill(iRatio.toInt)(style.block).mkString + style.fractionSymbols + 
            List.fill(maxSize - iRatio.toInt)(style.space).mkString + 
            style.rightBracket
          } else {
            style.leftBracket + List.fill(iRatio.toInt - 2 - style.fractionSymbols.size)(style.block).mkString +
            style.rightBracket
          }
          _ <- Sync[F].delay(print("\r"))
          _ <- Sync[F].delay(print(string))
        } yield ()
      }.drain ++ Stream.eval(Sync[F].delay(println))
  }

    // def runTimer[F[_]: Sync : Timer](size: F[Int], progress: ProgressTracker[F], style: ProgressBarStyle) : Stream[F, Unit] = {
  //   val charsToRemove = style.leftBracket.size + style.rightBracket.size + 1
  //   // val maxSize = size - charsToRemove
  //   // Stream.eval(size)

  //   Stream.eval(progress.current)
  //     .zipLeft(Stream.awakeEvery[F](0.05.seconds)) // Artificial Slowdown
  //     .covary[F]
  //     .evalMap{ i => 
        

  //       val string = if (i < progress.totalIncrements){
  //         style.leftBracket + List.fill(i)(style.block).mkString + style.fractionSymbols + 
  //         List.fill(maxSize - i)(style.space).mkString + 
  //         style.rightBracket
  //       } else {
  //         style.leftBracket + List.fill(i + style.fractionSymbols.size)(style.block).mkString +
  //         style.rightBracket
  //       }


  //       Sync[F].delay(print("\r")) >>
  //       Sync[F].delay(print(string))

  //     }.drain ++ Stream.eval(Sync[F].delay(println))
  // }


}
