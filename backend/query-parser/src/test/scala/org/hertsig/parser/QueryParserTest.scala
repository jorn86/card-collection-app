package org.hertsig.parser

import org.parboiled.scala.testing.ParboiledTest
import org.scalatest.testng.TestNGSuiteLike
import org.testng.annotations.Test

class QueryParserTest extends ParboiledTest with TestNGSuiteLike {
  val parser = new QueryParser()

  @Test
  def testColorCondition() {
    val result = parser.parse("c:w")
    println(result)
  }

  @Test
  def testTypeCondition() {
    var result = parser.parse("t:creature t:land")
    println(result)
    result = parser.parse("t:\"creature land\"")
    println(result)
  }

  @Test
  def testSubcondition() {
    val result = parser.parse("c:w (c:u c:b) c:r")
    println(result)
  }

  @Test
  def testOrSubcondition() {
    val result = parser.parse("c:w (c:u or c:b) c:r")
    println(result)
  }

  @Test
  def testPowerToughness() {
    val result = parser.parse("pow>4 tou<=3 t:creature")
    println(result)
  }
}
