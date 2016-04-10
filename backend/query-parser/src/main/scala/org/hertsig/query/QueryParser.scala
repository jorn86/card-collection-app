package org.hertsig.query

import org.parboiled.errors.{ErrorUtils, ParsingException}
import org.parboiled.scala._

sealed abstract class AstNode

case class QueryNode(conditions: List[ConditionNode]) extends AstNode

sealed abstract class ConditionNode() extends AstNode
case class NotConditionNode(condition: ConditionNode) extends ConditionNode
case class OrNode(conditions: List[ConditionNode]) extends ConditionNode

case class NameConditionNode(name: StringNode) extends ConditionNode
case class OracleConditionNode(text: StringNode) extends ConditionNode
case class EditionConditionNode(text: StringNode) extends ConditionNode
case class FlavorTextConditionNode(text: StringNode) extends ConditionNode
case class PowerConditionNode(condition: CompareNode, amount: Int) extends ConditionNode
case class ToughnessConditionNode(condition: CompareNode, amount: Int) extends ConditionNode
case class LoyaltyConditionNode(condition: CompareNode, amount: Int) extends ConditionNode
case class CmcConditionNode(condition: CompareNode, amount: Int) extends ConditionNode
case class FormatConditionNode(format: String) extends ConditionNode
case class ColorConditionNode(condition: CompareNode, color: ColorNode) extends ConditionNode
case class ColorIdentityConditionNode(condition: CompareNode, color: ColorNode) extends ConditionNode
case class TypeConditionNode(condition: StringNode) extends ConditionNode

case class CompareNode(condition: String) extends AstNode
case class StringNode(text: String) extends AstNode
case class ColorNode(colors: String) extends AstNode

class QueryParser extends Parser {
  def Query: Rule1[QueryNode] = rule { zeroOrMore(" ") ~ oneOrMore(Condition, oneOrMore(" ")) ~~> QueryNode ~ zeroOrMore(" ") ~ EOI }

  def Condition: Rule1[ConditionNode] = rule {
    NotCondition |
    OrCondition |
    OracleCondition |
    EditionCondition |
    FlavorTextCondition |
    TypeCondition |
    PowerCondition |
    ToughnessCondition |
    LoyaltyCondition |
    CmcCondition |
    ColorCondition |
    ColorIdentityCondition |
    FormatCondition |
    NameCondition
  }

  def NotCondition: Rule1[NotConditionNode] = rule { ignoreCase("not:") ~ Condition } ~~> NotConditionNode
  def OrCondition: Rule1[OrNode] = rule { "(" ~ oneOrMore(Condition, OrSeparator) ~ ")" ~~> OrNode }

  def NameCondition: Rule1[NameConditionNode] = rule { StringValue ~~> NameConditionNode }
  def OracleCondition: Rule1[OracleConditionNode] = rule { ignoreCase("o:") ~ StringValue ~~> OracleConditionNode }
  def EditionCondition: Rule1[EditionConditionNode] = rule { ignoreCase("e:") ~ StringValue ~~> EditionConditionNode }
  def FlavorTextCondition: Rule1[FlavorTextConditionNode] = rule { ignoreCase("ft:") ~ StringValue ~~> FlavorTextConditionNode }
  def TypeCondition: Rule1[TypeConditionNode] = rule { ignoreCase("t:") ~ StringValue ~~> TypeConditionNode }
  def PowerCondition: Rule1[PowerConditionNode] = rule { ignoreCase("pow") ~ AmountType ~ Number ~~> PowerConditionNode }
  def ToughnessCondition: Rule1[ToughnessConditionNode] = rule { ignoreCase("tou") ~ AmountType ~ Number ~~> ToughnessConditionNode }
  def LoyaltyCondition: Rule1[LoyaltyConditionNode] = rule { ignoreCase("loyalty") ~ AmountType ~ Number ~~> LoyaltyConditionNode }
  def CmcCondition: Rule1[CmcConditionNode] = rule { ignoreCase("cmc") ~ AmountType ~ Number ~~> CmcConditionNode }
  def ColorCondition: Rule1[ColorConditionNode] = rule { ignoreCase("c") ~ AmountType ~ Color ~~> ColorConditionNode }
  def ColorIdentityCondition: Rule1[ColorIdentityConditionNode] = rule { ignoreCase("ci") ~ AmountType ~ Color ~~> ColorIdentityConditionNode }
  def FormatCondition: Rule1[FormatConditionNode] = rule { ignoreCase("f:") ~
    (ignoreCase("Vintage") | ignoreCase("Legacy") | ignoreCase("Modern") | ignoreCase("Standard") | ignoreCase("Commander")) ~> FormatConditionNode }

  def OrSeparator: Rule0 = rule { oneOrMore(" ") ~ ignoreCase("or") ~ oneOrMore(" ") }

  def AmountType: Rule1[CompareNode] = rule { (">=" | ">" | "=" | "<=" | "<") ~> CompareNode }
  def Color: Rule1[ColorNode] = rule {oneOrMore(anyOf("wubrgc") | anyOf("WUBRGC")) ~> ColorNode}
  def Number: Rule1[Int] = rule { "-" ~ oneOrMore("0"-"9") ~> (-_.toInt) | oneOrMore("0"-"9") ~> (_.toInt)}
  def StringValue: Rule1[StringNode] = rule { oneOrMore("a"-"z" | "A"-"Z") ~> StringNode | "\"" ~ oneOrMore(!anyOf("\"") ~ ANY) ~> StringNode ~ "\"" }


  @throws(classOf[ParsingException])
  def parse(input: String): QueryNode = {
    val parsingResult = ReportingParseRunner(Query).run(input)
    parsingResult.result match {
      case Some(task) => task
      case None => throw new ParsingException("Invalid input:\n" + ErrorUtils.printParseErrors(parsingResult))
    }
  }
}
