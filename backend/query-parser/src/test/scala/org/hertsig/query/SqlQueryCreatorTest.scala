package org.hertsig.query

import org.parboiled.scala.testing.ParboiledTest
import org.scalatest.testng.TestNGSuiteLike
import org.testng.annotations.Test
import org.testng.Assert._

class SqlQueryCreatorTest extends ParboiledTest with TestNGSuiteLike {
  @Test
  def testNot() {
    val result = SqlQueryCreator.toPostgres(QueryNode(List(NotConditionNode(NameConditionNode(StringNode("a")))))).query.substring(SqlQueryCreator.PREFIX.length)
    assertEquals(result, "NOT (normalizedname ILIKE :arg0)")
  }
}
