package io.chrisdavenport.progressbar

import cats.implicits._
import cats.effect._
import scala.concurrent.duration._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = for{
    _ <- IO.println("")
    _ <- (0 to 200).toList.traverse{i => 
      val percent = i.toDouble / 200
      val bar = DisplayBars.renderBar(80 - 8, percent, ProgressBarStyle.UNICODE_BLOCK)
      IO.print(bar ++ s" % ${(percent * 100).toInt}") >> IO.sleep(0.025.seconds) >> {
        if (i != 200) IO.print("\u001b[1000D") else IO.print("\n")
      }
    }
  } yield ExitCode.Success

}
