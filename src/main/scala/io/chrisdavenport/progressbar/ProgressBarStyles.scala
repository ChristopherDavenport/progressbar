package io.chrisdavenport.progressbar

import cats.data.NonEmptyList

sealed trait ProgressBarStyle {
  val leftBracket: Char
  val rightBracket: Char
  val block: Char
  val space: Char
  val fractionSymbols: NonEmptyList[Char]
}

object ProgressBarStyle {
  // case object COLORFUL_UNICODE_BLOCK extends ProgressBarStyle {
  //   val leftBracket: String = "\u001b[33m│"
  //   val rightBracket: String = "│\u001b[0m"
  //   val block: Char = '█'
  //   val space: Char = ' '
  //   val fractionSymbols: String = " ▏▎▍▌▋▊▉"
  // }
  case object UNICODE_BLOCK extends ProgressBarStyle {
    val leftBracket: Char = '│'
    val rightBracket: Char = '│'
    val block: Char = '█'
    val space: Char = ' '
    val fractionSymbols: NonEmptyList[Char] = NonEmptyList.of('▏', '▎', '▍', '▌', '▋', '▊', '▉')
  }
  case object ASCII extends ProgressBarStyle {
    val leftBracket: Char = '['
    val rightBracket: Char = ']'
    val block: Char = '='
    val space: Char = ' '
    val fractionSymbols: NonEmptyList[Char] = NonEmptyList.of('>')
  }
}