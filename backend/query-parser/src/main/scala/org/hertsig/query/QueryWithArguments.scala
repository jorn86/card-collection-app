package org.hertsig.query

import org.intellij.lang.annotations.Language

import scala.collection.mutable

case class QueryWithArguments(@Language("SQL") query: String, values: mutable.Map[String, Object]) {

}
