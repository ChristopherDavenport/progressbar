package io.chrisdavenport.progressbar


final case class ProgressBarStyle(
  leftBracket: String,
  rightBracket: String,
  block: Char,
  space: Char,
  fractionSymbols: String
)

object ProgressBarStyle {
  val COLORFUL_UNICODE_BLOCK = ProgressBarStyle("\u001b[33m│", "│\u001b[0m", '█', ' ', " ▏▎▍▌▋▊▉")

    /** Use Unicode block characters to draw the progress bar. */
  val UNICODE_BLOCK = ProgressBarStyle("│", "│", '█', ' ', " ▏▎▍▌▋▊▉")

    /** Use only ASCII characters to draw the progress bar. */
  val ASCII = ProgressBarStyle("[", "]", '=', ' ', ">")
}