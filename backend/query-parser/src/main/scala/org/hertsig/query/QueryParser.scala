package org.hertsig.query

import org.parboiled.errors.{ErrorUtils, ParsingException}
import org.parboiled.scala._

sealed abstract class AstNode

case class QueryNode(conditions: List[ConditionNode]) extends AstNode

sealed abstract class ConditionNode() extends AstNode
case class SubconditionNode(not: Boolean, subcondition: ConditionNode) extends ConditionNode

case class AndNode(conditions: List[ConditionNode]) extends ConditionNode
case class OrNode(conditions: List[ConditionNode]) extends ConditionNode

case class NameConditionNode(name: StringNode) extends ConditionNode
case class OracleConditionNode(text: StringNode) extends ConditionNode
case class FlavorTextConditionNode(text: StringNode) extends ConditionNode
case class PowerConditionNode(condition: AmountTypeNode, amount: Int) extends ConditionNode
case class ToughnessConditionNode(condition: AmountTypeNode, amount: Int) extends ConditionNode
case class LoyaltyConditionNode(condition: AmountTypeNode, amount: Int) extends ConditionNode
case class CmcConditionNode(condition: AmountTypeNode, amount: Int) extends ConditionNode
case class FormatConditionNode(format: String) extends ConditionNode
case class ColorConditionNode(condition: AmountTypeNode, color: ColorNode) extends ConditionNode
case class ColorIdentityConditionNode(color: ColorNode) extends ConditionNode
case class TypeConditionNode(condition: StringNode) extends ConditionNode

case class AmountTypeNode(condition: String) extends AstNode
case class StringNode(text: String) extends AstNode
case class ColorNode(colors: String) extends AstNode

class QueryParser extends Parser {
  def Query: Rule1[QueryNode] = rule { zeroOrMore(" ") ~ oneOrMore(Condition, oneOrMore(" ")) ~~> QueryNode ~ zeroOrMore(" ") ~ EOI }

  def Condition: Rule1[ConditionNode] = rule {
    Subcondition |
    OracleCondition |
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

  def Subcondition: Rule1[SubconditionNode] = rule { optional("not") ~> (!_.isEmpty) ~ "(" ~ (AndSubcondition | OrSubcondition) ~ ")" ~~> SubconditionNode }
  def AndSubcondition: Rule1[AndNode] = rule { oneOrMore(Condition, oneOrMore(" ")) ~~> AndNode }
  def OrSubcondition: Rule1[OrNode] = rule { oneOrMore(Condition, OrSeparator) ~~> OrNode }

  def NameCondition: Rule1[NameConditionNode] = rule { StringValue ~~> NameConditionNode }
  def OracleCondition: Rule1[OracleConditionNode] = rule { "o:" ~ StringValue ~~> OracleConditionNode }
  def FlavorTextCondition: Rule1[FlavorTextConditionNode] = rule { "ft:" ~ StringValue ~~> FlavorTextConditionNode }
  def TypeCondition: Rule1[TypeConditionNode] = rule { "t:" ~ StringValue ~~> TypeConditionNode }
  def PowerCondition: Rule1[PowerConditionNode] = rule { "pow" ~ AmountType ~ Number ~~> PowerConditionNode }
  def ToughnessCondition: Rule1[ToughnessConditionNode] = rule { "tou" ~ AmountType ~ Number ~~> ToughnessConditionNode }
  def LoyaltyCondition: Rule1[LoyaltyConditionNode] = rule { "loyalty" ~ AmountType ~ Number ~~> LoyaltyConditionNode }
  def CmcCondition: Rule1[CmcConditionNode] = rule { "cmc" ~ AmountType ~ Number ~~> CmcConditionNode }
  def ColorCondition: Rule1[ColorConditionNode] = rule { "c" ~ AmountType ~ Color ~~> ColorConditionNode }
  def ColorIdentityCondition: Rule1[ColorIdentityConditionNode] = rule { "ci:" ~ Color ~~> ColorIdentityConditionNode }
  def FormatCondition: Rule1[FormatConditionNode] = rule { "f:" ~ ("All" | "Vintage" | "Legacy" | "Extended" | "Modern" | "Standard" | "Commander" | "MTGO") ~> FormatConditionNode }

  def AmountType: Rule1[AmountTypeNode] = rule { (">=" | ">" | "=" | "<=" | "<") ~> AmountTypeNode }
  def Color: Rule1[ColorNode] = rule {oneOrMore(anyOf("wubrgc")) ~> ColorNode}
  def Number: Rule1[Int] = rule { optional("-") ~ oneOrMore("0"-"9") ~> (_.toInt) }
  def StringValue: Rule1[StringNode] = rule { oneOrMore("a"-"z" | "A"-"Z") ~> StringNode | "\"" ~ oneOrMore(!anyOf("\"") ~ ANY) ~> StringNode ~ "\"" }

  def OrSeparator: Rule0 = rule { oneOrMore(" ") ~ "or" ~ oneOrMore(" ") }

  @throws(classOf[ParsingException])
  def parse(input: String): QueryNode = {
    val parsingResult = ReportingParseRunner(Query).run(input)
    parsingResult.result match {
      case Some(task) => task
      case None => throw new ParsingException("Invalid input:\n" + ErrorUtils.printParseErrors(parsingResult))
    }
  }
}
