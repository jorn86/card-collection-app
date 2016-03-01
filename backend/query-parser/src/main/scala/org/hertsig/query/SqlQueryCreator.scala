package org.hertsig.query

import com.google.common.escape.{Escapers, Escaper}
import org.intellij.lang.annotations.Language

import scala.collection.mutable

object SqlQueryCreator {
  private val LIKE_ESCAPER: Escaper = Escapers.builder.addEscape('_', "\\_").addEscape('%', "\\%").build

  def toPostgres(node: QueryNode): QueryWithArguments = {
    val values: mutable.Map[String, Object] = mutable.Map()
    val query = "SELECT * FROM searchview WHERE " + toPostgres(node.conditions, values)
    QueryWithArguments(query, values)
  }

  private def toPostgres(nodes: List[ConditionNode], values: mutable.Map[String, Object]): String = {
    nodes.map(toPostgres(_, values)).mkString(" AND ")
  }

  private def toPostgres(node: ConditionNode, values: mutable.Map[String, Object]): String = {
    node match {
      case SubconditionNode(condition) => "(" + toPostgres(condition, values) + ")"
      case OrSubconditionNode(conditions) => "(" + conditions.map(toPostgres(_, values)).mkString(" OR ") + ")"
      case NameConditionNode(name) => "normalizedname LIKE " + arg(values, escape(name) + "%")
      case OracleConditionNode(text) => "text LIKE " + arg(values, "%" + escape(text) + "%")
      case FlavorTextConditionNode(text) => "flavortext LIKE " + "%" + arg(values, escape(text) + "%")
      case TypeConditionNode(condition) => "type LIKE " + arg(values, condition)
      case ColorConditionNode(color) => ""
      case PowerConditionNode(condition, amount) => s"power $condition $amount"
      case ToughnessConditionNode(condition, amount) => s"toughness $condition $amount"
      case LoyaltyConditionNode(condition, amount) => s"loyalty $condition $amount"
      case CmcConditionNode(condition, amount) => s"cmc $condition $amount"
      case FormatConditionNode(format) => "format = " + arg(values, format)
    }
  }

  private def escape(text: StringNode): String = {
    LIKE_ESCAPER.escape(text.text)
  }

  private def arg(values: mutable.Map[String, Object], value: Object): String = {
    val argName = ":arg" + values.size
    values.put(argName, value)
    argName
  }
}
