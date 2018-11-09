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
    _ <- IO(println(term.getWidth))

    tracker <- ProgressTracker.of[IO](1246)
    
    // _ <- tracker.incrementBy(1)
    _ <- Stream(
      runProgressBar[IO](0.05.seconds, IO(term.getWidth), tracker, ProgressBarStyle.UNICODE_BLOCK),
      Stream.awakeEvery[IO](0.05.second).evalMap(_ => tracker.incrementBy(1)).drain
    ).parJoinUnbounded
      .take(1)
      .compile // Stream[F, A] => F[Something]
      .drain // F[Unit]
    
  } yield ExitCode.Success

  final case class ProgressTracker[F[_]](totalIncrements: Int, incrementBy: Int =>  F[Unit], getCurrent: F[Int])
  object ProgressTracker {
    def of[F[_]: Sync](int: Int): F[ProgressTracker[F]] =  Ref.of[F, Int](0).map{ref => 
      ProgressTracker(int, incrementBy => ref.update(i => if (i === int) i else Math.min(int, i + incrementBy)), ref.get)
    }
  }

  def runProgressBar[F[_]: Sync : Timer](
    refreshTime: FiniteDuration, 
    sizeF: F[Int],
    progress: ProgressTracker[F], 
    style: ProgressBarStyle
  ) : Stream[F, Unit] = {
    // val charsToRemove = style.leftBracket.size + style.rightBracket.size + 1
    Stream.repeatEval(progress.getCurrent) // (1 - 100) (100 ...)
      .takeWhile(_ < progress.totalIncrements, true)
      .zipLeft(Stream.awakeEvery[F](refreshTime)) // Artificial Slowdown
      .covary[F]
      .evalMap{ i => 
        for {
          size <- sizeF
          string = DisplayBars.displayProgressBar(size, i, progress.totalIncrements, style)
          _ <- Sync[F].delay(print("\r"))
          _ <- Sync[F].delay(print(string))
          // _ <- Sync[F].delay(string)
        } yield 57
      }.drain ++ Stream.eval(Sync[F].delay(println))
  }


}
