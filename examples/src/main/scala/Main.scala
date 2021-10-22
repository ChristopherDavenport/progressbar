import cats.implicits._
import cats.effect._
import scala.concurrent.duration._
import io.chrisdavenport.progressbar._

import _root_.org.http4s.client.Client
import _root_.org.http4s._
import org.http4s.syntax.all._
import _root_.scodec.bits._
import cats.effect.std.Console
import org.http4s.ember.client.EmberClientBuilder
import java.text.DecimalFormat
import _root_.fs2.{Stream, Chunk}

object Main extends IOApp {
  val newLineBased = false // ScalaJS only outputs on newline, so we adjust down a line.



  implicit val console: Console[IO] = Console.make[IO]
  def run(args: List[String]): IO[ExitCode] = for{
    _ <- IO.println("")

    // _ <- (0 to 700).toList.traverse{i => 
    //   val percent = i.toDouble / 700
    //   val bar = DisplayBars.renderBar(80 - 8, percent, ProgressBarStyle.UNICODE_BLOCK)
    //   if (!newLineBased){
    //     IO.print(bar ++ s" % ${(percent * 100).toInt}") >> IO.sleep(0.025.seconds) >> {
    //       if (i != 700) IO.print(left(5000)) else IO.print("\n")
    //     }
    //   } else {
    //     val prefix = if (i != 0) AnsiCodes.upAndStart(1) else ""
    //     IO.println(prefix ++ bar ++ s" % ${(percent * 100).toInt}") >> IO.sleep(0.025.seconds)
    //   }
    // }
    _ <- EmberClientBuilder.default[IO].build.use{client => 
      val resources = List(
        uri"https://repo1.maven.org/maven2/org/http4s/http4s-core_2.13/0.23.6/http4s-core_2.13-0.23.6.jar",
        uri"https://repo1.maven.org/maven2/co/fs2/fs2-core_2.13/3.1.5/fs2-core_2.13-3.1.5.jar",
        uri"https://repo1.maven.org/maven2/org/http4s/http4s-ember-core_2.13/0.23.6/http4s-ember-core_2.13-0.23.6.jar",
        uri"https://repo1.maven.org/maven2/org/http4s/http4s-client_2.13/0.23.6/http4s-client_2.13-0.23.6.jar",
        uri"https://repo1.maven.org/maven2/org/http4s/http4s-ember-client_2.13/0.23.6/http4s-ember-client_2.13-0.23.6.jar",
        uri"http://ipv4.download.thinkbroadband.com/10MB.zip",
      )
      resources.traverse_{uri => 
        Console[IO].println(uri.toString()) >>
        downloadAndShow(client, Request[IO](Method.GET, uri))
      }
    }
  } yield ExitCode.Success


  // Should be run synchronously to not run into issues
  def downloadAndShow[F[_]: Temporal: Console](client: Client[F], req: Request[F], width: Int = 80, style: ProgressBarStyle = ProgressBarStyle.UNICODE_BLOCK): F[Response[F]] = {
    client.run(req).use{resp => 
      if (resp.status.isSuccess) {
        (Ref[F].of(0L), Temporal[F].realTime, resp.contentLength.toRight(new Throwable("No Content-Length")).liftTo[F]).tupled.flatMap{
          case (progress, start, length) => 
            resp.body.chunks.zipWithIndex
              .evalTap{ case (chunk, index) => 
                for {
                  now <- Temporal[F].realTime
                  currentLength <- progress.modify(old => (old + chunk.size, old + chunk.size))
                  percent = currentLength.toDouble / length
                  totalTime = now - start
                  rate = currentLength /  Math.max(totalTime.toMillis, 1) * 1000
                  bar = DisplayBars.renderBar(width - 28, percent, style)
                  prefix = if (index != 0) AnsiCodes.upAndStart(1) ++ AnsiCodes.clearLine else ""
                  _ <- Console[F].println(prefix ++ bar ++ s" ${HumanReadable.decimalBased(currentLength)} / ${HumanReadable.decimalBased(length)} (${HumanReadable.decimalBased(rate)} / s)")
                } yield ()
              }.map(_._1)
              .compile
              .foldMonoid
              .map(_.toByteVector)
              .map(bv => resp.copy(body = Stream.chunk(Chunk.byteVector(bv))))

            
        }
      } else Temporal[F].raiseError(new Throwable(s"Download Failure for $req"))
    }
  }

}

object AnsiCodes {
  // ANSI Movement Codes
  def up(n: Int) = s"\u001b[${n}A"
  def down(n: Int) = s"\u001b[${n}B"
  def right(n: Int) = s"\u001b[${n}C"
  def left(n: Int) = s"\u001b[${n}D"

  def downAndStart(n: Int) = s"\u001b[${n}E"
  def upAndStart(n: Int) = s"\u001b[${n}F"
  def setColumn(n: Int) = s"\u001b[${n}G"
  def setPosition(row: Int, column: Int) = s"\u001b[${row};${column}H"

  def clearRight = "\u001b[1K"
  def clearLeft = "\u001b[2K"
  def clearLine = "\u001b[3K"
}

object HumanReadable {
  private val KILO = 1000L // 1000 power 1 (10 power 3)
  private val KIBI = 1024L // 1024 power 1 (2 power 10)
  private val MEGA = KILO * KILO // 1000 power 2 (10 power 6)
  private val MEBI = KIBI * KIBI // 1024 power 2 (2 power 20)
  private val GIGA = MEGA * KILO // 1000 power 3 (10 power 9)
  private val GIBI = MEBI * KIBI // 1024 power 3 (2 power 30)
  private val TERA = GIGA * KILO // 1000 power 4 (10 power 12)
  private val TEBI = GIBI * KIBI // 1024 power 4 (2 power 40)
  private val PETA = TERA * KILO // 1000 power 5 (10 power 15)
  private val PEBI = TEBI * KIBI // 1024 power 5 (2 power 50)
  private val EXA = PETA * KILO // 1000 power 6 (10 power 18)
  private val EXBI = PEBI * KIBI // 1024 power 6 (2 power 60)
  private val df = new DecimalFormat("#.##")

  def binaryBased(sizeI: Long): String =  {
    val size = Math.abs(sizeI)
    if (size < KIBI) {
      df.format(size).concat(" B")
    } else if (size < MEBI) {
      df.format(size / KIBI).concat(" KiB")
    } else if (size < GIBI) {
      df.format(size / MEBI).concat(" MiB")
    } else if (size < TEBI) {
      df.format(size / GIBI).concat(" GiB")
    } else if (size < PEBI) {
      df.format(size / TEBI).concat(" TiB")
    } else if (size < EXBI) {
      df.format(size / PEBI).concat(" PiB")
    } else {
      df.format(size / EXBI).concat(" EiB")
    }
  }

  def decimalBased(sizeI: Long): String = {
    val size = Math.abs(sizeI)
    if (size < KILO) {
      df.format(size).concat(" B")
    } else if (size < MEGA) {
      df.format(size / KILO).concat(" KB")
    } else if (size < GIGA) {
      df.format(size / MEGA).concat(" MB")
    } else if (size < TERA) {
      df.format(size / GIGA).concat(" GB")
    } else if (size < PETA) {
      df.format(size / TERA).concat(" TB")
    } else if (size < EXA) {
      df.format(size / PETA).concat(" PB")
    } else {
      df.format(size / EXA).concat(" EB")
    }
  }
}

