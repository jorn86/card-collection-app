package org.hertsig.query

import org.parboiled.scala.testing.ParboiledTest
import org.scalatest.testng.TestNGSuiteLike
import org.testng.annotations.Test
import org.testng.Assert._

class QueryParserTest extends ParboiledTest with TestNGSuiteLike {
  val parser = new QueryParser()

  @Test
  def testColorCondition() {
    val result = parser.parse("c:w")
    assertEquals(result, QueryNode(List(color("w"))))
  }

  @Test
  def testTypeCondition() {
    val result = parser.parse("t:creature t:land")
    assertEquals(result, QueryNode(List(TypeConditionNode(StringNode("creature")), TypeConditionNode(StringNode("land")))))
  }

  @Test
  def testCombinedTypeCondition() {
    val result = parser.parse("t:\"creature land\"")
    assertEquals(result, QueryNode(List(TypeConditionNode(StringNode("creature land")))))
  }

  @Test
  def testAnd() {
    val result = parser.parse("c:w (c:u c:b) c:r")
    assertEquals(result, QueryNode(List(color("w"), SubconditionNode(not = false, AndNode(List(color("u"), color("b")))), color("r"))))
  }

  @Test
  def testOr() {
    val result = parser.parse("c:w (c:u or c:b) c:r")
    assertEquals(result, QueryNode(List(color("w"), SubconditionNode(not = false, OrNode(List(color("u"), color("b")))), color("r"))))
  }

  @Test
  def testPowerToughness() {
    val result = parser.parse("pow>4 tou<=3 t:creature")
    assertEquals(result, QueryNode(List(PowerConditionNode(AmountTypeNode(">"), 4), ToughnessConditionNode(AmountTypeNode("<="), 3), TypeConditionNode(StringNode("creature")))))
  }

  @Test
  def testNegativeNumber() = {
    val result = parser.parse("cmc>-1")
    assertEquals(result, QueryNode(List(CmcConditionNode(AmountTypeNode(">"), -1))))
  }

  private def color(color: String): ColorConditionNode = {
    ColorConditionNode(AmountTypeNode("="), ColorNode(color))
  }
}
