package org.hertsig.query

import org.parboiled.scala.testing.ParboiledTest
import org.scalatest.testng.TestNGSuiteLike
import org.testng.Assert._
import org.testng.annotations.Test

class QueryParserTest extends ParboiledTest with TestNGSuiteLike {
  val parser = new QueryParser

  @Test
  def testColorCondition() {
    val result = parser.parse("c=w")
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
  def testSingleNot() {
    val result = parser.parse("not:name")
    assertEquals(result, QueryNode(List(NotConditionNode(name("name")))))
  }

  @Test
  def testOr() {
    val result = parser.parse("(a or b)")
    assertEquals(result, QueryNode(List(OrNode(List(name("a"), name("b"))))))
  }

  @Test
  def testNotOr() {
    val result = parser.parse("not:(a or b)")
    assertEquals(result, QueryNode(List(NotConditionNode(OrNode(List(name("a"), name("b")))))))
  }

  @Test
  def testPowerToughness() {
    val result = parser.parse("pow>4 tou<=3 t:creature")
    assertEquals(result, QueryNode(List(PowerConditionNode(CompareNode(">"), 4), ToughnessConditionNode(CompareNode("<="), 3), TypeConditionNode(StringNode("creature")))))
  }

  @Test
  def testNegativeNumber() = {
    val result = parser.parse("cmc>-1")
    assertEquals(result, QueryNode(List(CmcConditionNode(CompareNode(">"), -1))))
  }

  private def name(name: String): NameConditionNode = {
    NameConditionNode(StringNode(name))
  }
  private def color(color: String): ColorConditionNode = {
    ColorConditionNode(CompareNode("="), ColorNode(color))
  }
}
