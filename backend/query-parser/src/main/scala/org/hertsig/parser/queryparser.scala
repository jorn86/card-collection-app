package org.hertsig.parser

import org.parboiled.errors.{ErrorUtils, ParsingException}
import org.parboiled.scala._

sealed abstract class AstNode

case class QueryNode(conditions: List[ConditionNode]) extends AstNode

sealed abstract class ConditionNode() extends AstNode
case class SubconditionNode(conditions: List[ConditionNode]) extends ConditionNode
case class OrSubconditionNode(conditions: List[ConditionNode]) extends ConditionNode
case class NameConditionNode(name: StringNode) extends ConditionNode
case class OracleConditionNode(text: StringNode) extends ConditionNode
case class FlavorTextConditionNode(text: StringNode) extends ConditionNode
case class PowerConditionNode(condition: AmountTypeNode, amount: Int) extends ConditionNode
case class ToughnessConditionNode(condition: AmountTypeNode, amount: Int) extends ConditionNode
case class LoyaltyConditionNode(condition: AmountTypeNode, amount: Int) extends ConditionNode
case class CmcConditionNode(condition: AmountTypeNode, amount: Int) extends ConditionNode
case class FormatConditionNode(format: String) extends ConditionNode
case class ColorConditionNode(condition: String) extends ConditionNode
case class TypeConditionNode(condition: StringNode) extends ConditionNode

case class AmountTypeNode(condition: String) extends AstNode
case class StringNode(text: String) extends AstNode

class QueryParser extends Parser {
  def Query: Rule1[QueryNode] = rule { zeroOrMore(" ") ~ oneOrMore(Condition, oneOrMore(" ")) ~~> QueryNode ~ zeroOrMore(" ") ~ EOI }

  def Condition: Rule1[ConditionNode] = rule {
    OrSubcondition |
    Subcondition |
    OracleCondition |
    FlavorTextCondition |
    TypeCondition |
    PowerCondition |
    ToughnessCondition |
    LoyaltyCondition |
    CmcCondition |
    ColorCondition |
    FormatCondition |
    NameCondition
  }

  def Subcondition: Rule1[SubconditionNode] = rule { "(" ~ oneOrMore(Condition, oneOrMore(" ")) ~~> SubconditionNode ~ ")" }
  def OrSubcondition: Rule1[OrSubconditionNode] = rule { "(" ~ oneOrMore(Condition, OrSeparator) ~~> OrSubconditionNode ~ ")" }
  def NameCondition: Rule1[NameConditionNode] = rule { StringValue ~~> NameConditionNode }
  def OracleCondition: Rule1[OracleConditionNode] = rule { "o:" ~ StringValue ~~> OracleConditionNode }
  def FlavorTextCondition: Rule1[FlavorTextConditionNode] = rule { "ft:" ~ StringValue ~~> FlavorTextConditionNode }
  def TypeCondition: Rule1[TypeConditionNode] = rule { "t:" ~ StringValue ~~> TypeConditionNode }
  def PowerCondition: Rule1[PowerConditionNode] = rule { "pow" ~ AmountType ~ Number ~~> PowerConditionNode }
  def ToughnessCondition: Rule1[ToughnessConditionNode] = rule { "tou" ~ AmountType ~ Number ~~> ToughnessConditionNode }
  def LoyaltyCondition: Rule1[LoyaltyConditionNode] = rule { "loyalty" ~ AmountType ~ Number ~~> LoyaltyConditionNode }
  def CmcCondition: Rule1[CmcConditionNode] = rule { "cmc" ~ AmountType ~ Number ~~> CmcConditionNode }
  def ColorCondition: Rule1[ColorConditionNode] = rule { "c:" ~ oneOrMore(anyOf("wubrgc")) ~> ColorConditionNode }
  def FormatCondition: Rule1[FormatConditionNode] = rule { "f:" ~ ("All" | "Vintage" | "Legacy" | "Extended" | "Modern" | "Standard" | "Commander" | "MTGO") ~> FormatConditionNode }

  def AmountType: Rule1[AmountTypeNode] = rule { (">=" | ">" | "=" | "<=" | "<") ~> AmountTypeNode }
  def Number: Rule1[Int] = rule { oneOrMore("0"-"9") ~> (_.toInt) }
  def StringValue: Rule1[StringNode] = rule { oneOrMore("a"-"z" | "A"-"Z") ~> StringNode | "\"" ~ oneOrMore(!anyOf("\"") ~ ANY) ~> StringNode ~ "\"" }

  def OrSeparator: Rule0 = rule { oneOrMore(" ") ~ "or" ~ oneOrMore(" ") }

  def parse(input: String): QueryNode = {
    val parsingResult = ReportingParseRunner(Query).run(input)
    parsingResult.result match {
      case Some(task) => task
      case None => throw new ParsingException("Invalid input:\n" + ErrorUtils.printParseErrors(parsingResult))
    }
  }
}
