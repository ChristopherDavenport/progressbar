package io.chrisdavenport.progressbar

import _root_.cats.data.NonEmptyList
import _root_.cats.syntax.all._

sealed trait ProgressBarStyle {
  val leftBracket: String
  val rightBracket: String
  val block: Char
  val space: Char
  val fractionSymbols: NonEmptyList[Char]
}

object ProgressBarStyle {
  case object COLORFUL_UNICODE_BLOCK extends ProgressBarStyle {
    val leftBracket: String = "\u001b[33m│"
    val rightBracket: String = "│\u001b[0m"
    val block: Char = '█'
    val space: Char = ' '
    val fractionSymbols: NonEmptyList[Char] = NonEmptyList.fromList("▏▎▍▌▋▊▉".toCharArray().toList).get
  }
  case object UNICODE_BLOCK extends ProgressBarStyle {
    val leftBracket: String = '│'.toString
    val rightBracket: String = '│'.toString
    val block: Char = '█'
    val space: Char = ' '
    val fractionSymbols: NonEmptyList[Char] = NonEmptyList.of('▏', '▎', '▍', '▌', '▋', '▊', '▉')
  }
  case object ASCII extends ProgressBarStyle {
    val leftBracket: String = '['.toString()
    val rightBracket: String = ']'.toString()
    val block: Char = '='
    val space: Char = ' '
    val fractionSymbols: NonEmptyList[Char] = NonEmptyList.of('>')
  }

  case object ASCII_SPIN extends ProgressBarStyle {
    val leftBracket: String = '['.toString()
    val rightBracket: String = ']'.toString()
    val block: Char = '='
    val space: Char = ' '
    val fractionSymbols: NonEmptyList[Char] = NonEmptyList.of('|', '/', '-', '\\')
  }

}