package org.hertsig.query

import com.google.common.escape.{Escaper, Escapers}

import scala.collection.mutable

object DbiQueryCreator {
  val PREFIX: String = "SELECT id, name, fulltype, supertypes, types, subtypes, cost, cmc, text, power, toughness, loyalty, " +
    "multiverseid, rarity, multiverseidBack, setcode FROM searchview WHERE "
  private val LIKE_ESCAPER: Escaper = Escapers.builder.addEscape('_', "\\_").addEscape('%', "\\%").build

  def toPostgres(node: QueryNode): QueryWithArguments = {
    val values: mutable.Map[String, Object] = mutable.Map()
    val query =  PREFIX + toPostgres(node.conditions, values)
    QueryWithArguments(query, values)
  }

  private def toPostgres(nodes: List[ConditionNode], values: mutable.Map[String, Object]): String = {
    nodes.map(toPostgres(_, values)).mkString(" AND ")
  }

  private def toPostgres(node: ConditionNode, values: mutable.Map[String, Object]): String = {
    node match {
      case NotConditionNode(conditions) => "NOT (" + toPostgres(conditions, values) + ")"
      case OrNode(conditions) => "(" + conditions.map(toPostgres(_, values)).mkString(" OR ") + ")"
      case NameConditionNode(name) => "normalizedname ILIKE " + arg(values, "%" + escape(name) + "%")
      case OracleConditionNode(text) => "text ILIKE " + arg(values, "%" + escape(text) + "%")
      case EditionConditionNode(code) => arg(values, escape(code)) + " ILIKE ANY(setcodes)"
      case FlavorTextConditionNode(text) => "flavortext ILIKE " + "%" + arg(values, "%" + escape(text) + "%")
      case TypeConditionNode(condition) => "fulltype ILIKE " + arg(values, '%' + escape(condition) + '%')
      case ColorConditionNode(condition, color) => parseColor(condition, color, "c_")
      case ColorIdentityConditionNode(condition, color) => parseColor(condition, color, "ci_")
      case PowerConditionNode(condition, amount) => s"power ${condition.condition} $amount"
      case ToughnessConditionNode(condition, amount) => s"toughness ${condition.condition} $amount"
      case LoyaltyConditionNode(condition, amount) => s"loyalty ${condition.condition} $amount"
      case CmcConditionNode(condition, amount) => s"cmc ${condition.condition} $amount"
      case FormatConditionNode(format) => arg(values, format) + " ILIKE ANY(formats)"
      case LayoutConditionNode(layout) => "layout ILIKE " + arg(values, escape(layout))
    }
  }

  private def parseColor(condition: CompareNode, color: ColorNode, columnPrefix: String): String = {
    val col = color.colors
    val included: mutable.MutableList[String] = mutable.MutableList()
    val excluded: mutable.MutableList[String] = mutable.MutableList()

    List("w", "u", "b", "r", "g")
      .foreach(c => (if (col.contains(c)) included else excluded) += (columnPrefix + c))

    condition.condition match {
      case ">=" => included.mkString(" AND ")
      case "<=" => excluded.map(c => "NOT " + c).mkString(" AND ")
      case "=" => (included ++ excluded.map(c => "NOT " + c)).mkString(" AND ")
    }
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
