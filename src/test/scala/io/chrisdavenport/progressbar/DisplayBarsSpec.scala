package io.chrisdavenport.progressbar

import cats.implicits._
import org.specs2.mutable.Specification
import org.specs2.ScalaCheck
import org.scalacheck._

class DisplaybarsSpec extends Specification with ScalaCheck {
  "Ascii display" should {
    "all progress bars - be expected size for any size bar" >> prop { (size : Int, ratio: (Int, Int)) => 
      DisplayBars.displayProgressBar(size, ratio._1, ratio._2, ProgressBarStyle.ASCII)
        .size must_=== size
    }.setGen1(Gen.posNum[Int].map(_ + 2))
      .setGen2(Gen.posNum[Int].flatMap(total => Gen.choose(0, total).map((_, total))))

    "final progressbar - be expected size for any size bar" >> prop { size: Int => 
      DisplayBars.displayProgressBarFinal(size, ProgressBarStyle.ASCII).size must_=== size
    }.setGen(Gen.posNum[Int].map(_ + 2)) // Minimum Size 2 Characters

    "partial progressbar - be expected size for any size bar" >> prop { (size : Int, ratio: (Int, Int)) => 
      DisplayBars.displayProgressBarIncomplete(size, ratio._1, ratio._2, ProgressBarStyle.ASCII)
        .size must_=== size
    }.setGen1(Gen.posNum[Int].map(_ + 2))
      .setGen2(Gen.posNum[Int].flatMap(total => Gen.choose(0, total).map((_, total))))


  }
  "Unicode block display" should {
    "all progress bars - be expected size for any size bar" >> prop { (size : Int, ratio: (Int, Int)) => 
      DisplayBars.displayProgressBar(size, ratio._1, ratio._2, ProgressBarStyle.UNICODE_BLOCK)
        .size must_=== size
    }.setGen1(Gen.posNum[Int].map(_ + 2))
      .setGen2(Gen.posNum[Int].flatMap(total => Gen.choose(0, total).map((_, total))))

    "final progressbar - be expected size for any size bar" >> prop { size: Int => 
      val output = DisplayBars.displayProgressBarFinal(size, ProgressBarStyle.UNICODE_BLOCK)
      output.size must_=== size
    }.setGen(Gen.posNum[Int].map(_ + 2)) // Minimum Size 2 Characters

    "partial progressbar - be expected size for any size bar" >> prop { (size : Int, ratio: (Int, Int)) => 
      DisplayBars.displayProgressBarIncomplete(size, ratio._1, ratio._2, ProgressBarStyle.UNICODE_BLOCK)
        .size must_=== size
    }.setGen1(Gen.posNum[Int].map(_ + 2))
      .setGen2(Gen.posNum[Int].flatMap(total => Gen.choose(0, total).map((_, total))))
  }

  // "Unicode colorful display" should {
  //   "all progress bars - be expected size for any size bar" >> prop { (size : Int, ratio: (Int, Int)) => 
  //     val style = ProgressBarStyle.COLORFUL_UNICODE_BLOCK 
  //     val output = DisplayBars.displayProgressBar(size, ratio._1, ratio._2, style)
  //     (output.size - style.leftBracket.size - style.rightBracket.size + 2) must_=== size
  //   }.setGen1(Gen.posNum[Int].map(_ + 2))
  //     .setGen2(Gen.posNum[Int].flatMap(total => Gen.choose(0, total).map((_, total))))

  //   "final progressbar - be expected size for any size bar" >> prop { size: Int => 
  //     // Tricky Because Left and Right Bracket are final size 1 but use manipulation which adds to string
  //     // size. So we will remove all those characters from the size and add 2 to make sure output size is correct
  //     val style = ProgressBarStyle.COLORFUL_UNICODE_BLOCK 
  //     val output = DisplayBars.displayProgressBarFinal(size, style)
  //     (output.size - style.leftBracket.size - style.rightBracket.size + 2) must_=== size
  //   }.setGen(Gen.posNum[Int].map(_ + 2)) // Minimum Size 2 Characters

  //   "partial progressbar - be expected size for any size bar" >> prop { (size : Int, ratio: (Int, Int)) => 
  //     val style = ProgressBarStyle.COLORFUL_UNICODE_BLOCK 
  //     val output = DisplayBars.displayProgressBarIncomplete(size, ratio._1, ratio._2, style)
  //     (output.size - style.leftBracket.size - style.rightBracket.size + 2) must_=== size
  //   }.setGen1(Gen.posNum[Int].map(_ + 2))
  //     .setGen2(Gen.posNum[Int].flatMap(total => Gen.choose(0, total).map((_, total))))
  // }
}