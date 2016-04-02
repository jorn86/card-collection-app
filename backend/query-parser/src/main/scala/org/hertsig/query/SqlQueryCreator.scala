package org.hertsig.query

import com.google.common.escape.{Escaper, Escapers}

import scala.collection.mutable

object SqlQueryCreator {
  private val LIKE_ESCAPER: Escaper = Escapers.builder.addEscape('_', "\\_").addEscape('%', "\\%").build

  def toPostgres(node: QueryNode): QueryWithArguments = {
    val values: mutable.Map[String, Object] = mutable.Map()
    val query = "SELECT id, name, fulltype, supertypes, types, subtypes, cost, cmc, text, power, toughness, loyalty, " +
      "multiverseid, rarity, multiverseidBack, setcode" +
      " FROM searchview WHERE " + toPostgres(node.conditions, values)
    QueryWithArguments(query, values)
  }

  private def toPostgres(nodes: List[ConditionNode], values: mutable.Map[String, Object]): String = {
    nodes.map(toPostgres(_, values)).mkString(" AND ")
  }

  private def toPostgres(node: ConditionNode, values: mutable.Map[String, Object]): String = {
    node match {
      case SubconditionNode(not, conditions) => (if (not) "NOT " else "") + s"(" + toPostgres(conditions, values) + ")"

      case OrNode(conditions) => "(" + conditions.map(toPostgres(_, values)).mkString(" OR ") + ")"
      case AndNode(conditions) => "(" + conditions.map(toPostgres(_, values)).mkString(" AND ") + ")"

      case NameConditionNode(name) => "normalizedname ILIKE " + arg(values, "%" + escape(name) + "%")
      case OracleConditionNode(text) => "text ILIKE " + arg(values, "%" + escape(text) + "%")
      case FlavorTextConditionNode(text) => "flavortext ILIKE " + "%" + arg(values, "%" + escape(text) + "%")
      case TypeConditionNode(condition) => "fulltype ILIKE " + arg(values, '%' + escape(condition) + '%')
      case ColorConditionNode(condition, color) => parseColor(condition, color)
      case ColorIdentityConditionNode(color) => parseColorIdentity(color)
      case PowerConditionNode(condition, amount) => s"power ${condition.condition} $amount"
      case ToughnessConditionNode(condition, amount) => s"toughness ${condition.condition} $amount"
      case LoyaltyConditionNode(condition, amount) => s"loyalty ${condition.condition} $amount"
      case CmcConditionNode(condition, amount) => s"cmc ${condition.condition} $amount"
      case FormatConditionNode(format) => "format = " + arg(values, format)
    }
  }

  private def parseColor(condition: AmountTypeNode, color: ColorNode): String = {
    val col = color.colors
    val included: mutable.MutableList[String] = mutable.MutableList()
    val excluded: mutable.MutableList[String] = mutable.MutableList()

    List("w", "u", "b", "r", "g")
      .foreach(c => (if (col.contains(c)) included else excluded) += ("c_" + c))

    condition.condition match {
      case ">=" => included.mkString(" AND ")
      case "<=" => excluded.map(c => "NOT " + c).mkString(" AND ")
      case "=" => (included ++ excluded.map(c => "NOT " + c)).mkString(" AND ")
    }
  }

  private def parseColorIdentity(color: ColorNode): String = {
    var col = color.colors
    if (col.contains("c")) col = ""

    val result: mutable.MutableList[String] = mutable.MutableList()
    if (!col.contains("w")) result += "NOT ci_w"
    if (!col.contains("u")) result += "NOT ci_u"
    if (!col.contains("b")) result += "NOT ci_b"
    if (!col.contains("r")) result += "NOT ci_r"
    if (!col.contains("g")) result += "NOT ci_g"
    result.mkString(" AND ")
  }

  private def escape(text: StringNode): String = {
    LIKE_ESCAPER.escape(text.text)
  }

  private def arg(values: mutable.Map[String, Object], value: Object): String = {
    val argName = "arg" + values.size
    values.put(argName, value)
    ":" + argName
  }
}
