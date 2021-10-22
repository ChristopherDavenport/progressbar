package io.chrisdavenport.progressbar

// import cats.implicits._

object DisplayBars {


  def renderBar(spaces: Int, percent: Double, style: ProgressBarStyle): String = {
    style.leftBracket ++ renderProgress(spaces, percent, style) ++ style.rightBracket
  }

  def renderProgress(spaces: Int, percent: Double, style: ProgressBarStyle): String = {
    require(percent >= 0 && percent <= 1.0)

    if (percent == 1.0) {
      new String(Array.fill(spaces)(style.block))
    } else if (percent == 0) {
      new String(Array.fill(spaces)(style.space))
    } else {
      val symbolsPerSpace = style.fractionSymbols.size
      val totalSymbols = spaces * symbolsPerSpace

      val spaceProgress = spaces * percent
      
      val blockCount = Math.floor(spaceProgress).toInt
      val spaceCount = spaces - 1 - blockCount

      val progressPercent = Math.ceil(totalSymbols * percent).toInt
      val progressModulo = progressPercent % style.fractionSymbols.size
      val progress = style.fractionSymbols.toList(progressModulo)

      val s = {
        val sb =  new StringBuilder()
        sb.appendAll(Array.fill(blockCount)(style.block))
        sb.append(progress) 
        sb.appendAll(Array.fill(spaceCount)(style.space))
        sb.toString
      }

      s
    }
  }
}