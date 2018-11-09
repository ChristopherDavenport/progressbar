package io.chrisdavenport.progressbar

// import cats.implicits._
import DomainRefinements._

object DisplayBars {

  // This will not be correct for barSizeInChars < 2
  // AND 0 <= increment <= totalIncrements
  // AND 0 < totalIncrements
  def displayProgressBar(barSizeInChars: Int, increment: Int, totalIncrements: Int, style: ProgressBarStyle): String = 
    if (increment < totalIncrements) displayProgressBarIncomplete(barSizeInChars, increment, totalIncrements, style)
    else displayProgressBarFinal(barSizeInChars, style)

  def displayProgressBarCorrect(barSize: GreaterThan2, increments: NonNegativeRationalLessThan1, style: ProgressBarStyle): String =
    displayProgressBar(barSize.value, increments.increment, increments.totalIncrements, style)


  private[progressbar] def displayProgressBarIncomplete(barSizeInChars: Int, increment: Int, totalIncrements: Int, style: ProgressBarStyle): String = {
    val ratio : Double = increment.toDouble / totalIncrements.toDouble
    val progressSectionSize = barSizeInChars - 3

    val sizeOfFillDouble = progressSectionSize * ratio
    val sizeOfFill = sizeOfFillDouble.toInt

    val sizeOfEmpty = progressSectionSize - sizeOfFill

    val fractionSymbolToShow = style.fractionSymbols.head

    // Additional Reasoning On Sub Information
    // charAt is 0 based
    // If Fraction Symbol is size 1 then always use that symbol, 
    // Given Increment I, TotalIncrements Int, give the progress between
    // the current sizeOfFill and the nextSizeOfFill
    // val fractionSymbolToShow: Char = {
    //   val x = sizeOfFill
    //   val y = progressSectionSize
    //   val m = increment
    //   val n = totalIncrements
    //   // val d = 2 - (y / n)

    //   val m__ = (((x -1)* n) / y)
    //   val m_ = (((x + 1) * n) / y)
    //   val d = m_ - m__
    //   val d_ = m - m__

    //   val p = d_.toDouble / d.toDouble
    //   val s = (p * (style.fractionSymbols.size)).toInt
    //   // println(s"x: $x, y: $y, n: $n, d: $d, d_: $d_, m__: $m__, m: $m, m_: $m_, p: $p, s: $s")
    //   style.fractionSymbols.toList(s - 1)
    // }

    style.leftBracket + 
    List.fill(sizeOfFill)(style.block).mkString + 
    fractionSymbolToShow +
    List.fill(sizeOfEmpty)(style.space).mkString +
    style.rightBracket
  }


  private[progressbar] def displayProgressBarFinal(barSizeInChars: Int, style: ProgressBarStyle): String = 
    style.leftBracket + List.fill(barSizeInChars - 2)(style.block).mkString + style.rightBracket

}