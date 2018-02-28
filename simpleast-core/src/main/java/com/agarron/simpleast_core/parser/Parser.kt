package com.agarron.simpleast_core.parser

import android.util.Log
import com.agarron.simpleast_core.node.Node
import java.util.*

class Parser<T : Node> @JvmOverloads constructor(private val enableDebugging: Boolean = false) {

  private val rules = ArrayList<Rule<T>>()

  fun addRule(rule: Rule<T>): Parser<T> {
    rules.add(rule)
    return this
  }

  fun addRules(rules: Collection<Rule<T>>): Parser<T> {
    for (rule in rules) {
      addRule(rule)
    }
    return this
  }

  @JvmOverloads
  fun parse(source: CharSequence?, isNested: Boolean = false): List<T> {
    val remainingParses = Stack<ParseSpec<T>>()
    val topLevelNodes = ArrayList<T>()

    if (source != null && !source.isEmpty()) {
      remainingParses.add(ParseSpec<T>(null, 0, source.length))
    }

    while (!remainingParses.isEmpty()) {
      val builder = remainingParses.pop()

      if (builder.startIndex >= builder.endIndex) {
        break
      }

      val inspectionSource = source?.subSequence(builder.startIndex, builder.endIndex) ?: continue
      val offset = builder.startIndex

      var foundRule = false
      for (rule in rules) {
        if (isNested && !rule.applyOnNestedParse) {
          continue
        }

        val matcher = rule.matcher.reset(inspectionSource)

        if (matcher.find()) {
          logMatch(rule, inspectionSource)
          val matcherSourceEnd = matcher.end() + offset
          foundRule = true

          val newBuilder = rule.parse(matcher, this, isNested)
          val parent = builder.root

          parent?.addChild(newBuilder.root) ?: topLevelNodes.add(newBuilder.root)

          // In case the last match didn't consume the rest of the source for this subtree,
          // make sure the rest of the source is consumed.
          if (matcherSourceEnd != builder.endIndex) {
            remainingParses.push(ParseSpec.createNonterminal(parent, matcherSourceEnd, builder.endIndex))
          }

          // We want to speak in terms of indices within the source string,
          // but the Rules only see the matchers in the context of the substring
          // being examined. Adding this offset addresses that issue.
          if (!newBuilder.isTerminal) {
            newBuilder.applyOffset(offset)
            remainingParses.push(newBuilder)
          }

          println("source: $inspectionSource -- depth: ${remainingParses.size}")

          break
        } else {
          logMiss(rule, inspectionSource)
        }
      }

      if (!foundRule) {
        throw RuntimeException("failed to find rule to match source: \"$inspectionSource\"")
      }
    }

    return topLevelNodes
  }

  private fun logMatch(rule: Rule<*>, source: CharSequence) {
    if (enableDebugging) {
      Log.i(TAG, "MATCH: with rule with pattern: " + rule.matcher.pattern().toString() + " to source: " + source)
    }
  }

  private fun logMiss(rule: Rule<*>, source: CharSequence) {
    if (enableDebugging) {
      Log.i(TAG, "MISS: with rule with pattern: " + rule.matcher.pattern().toString() + " to source: " + source)
    }
  }

  companion object {

    private val TAG = "Parser"
  }
}
