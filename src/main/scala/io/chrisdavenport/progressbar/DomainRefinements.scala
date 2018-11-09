package io.chrisdavenport.progressbar 

object DomainRefinements {
    /**
    * Logically Indicates Any Value of Increment and Total Increments is
    * larger than zero and the  current increment is less than or equal to
    * the total increments
    */
  class NonNegativeRationalLessThan1 private(val increment: Int, val totalIncrements: Int)
  object NonNegativeRationalLessThan1{
    def build(increment: Int, totalIncrements: Int): Option[NonNegativeRationalLessThan1]  = 
      if (increment > 0 && totalIncrements > 0 && increment <= totalIncrements)
        Option(new NonNegativeRationalLessThan1(increment, totalIncrements))
      else None 
  }

  // Value is Larger Than 2
  class GreaterThan2 private(val value: Int)
  object GreaterThan2 {
    def build(i: Int): Option[GreaterThan2] = 
      if (i >= 2) Option(new GreaterThan2(i))
      else None
  }
}