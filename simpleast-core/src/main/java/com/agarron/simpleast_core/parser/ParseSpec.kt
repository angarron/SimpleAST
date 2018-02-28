package com.agarron.simpleast_core.parser

import com.agarron.simpleast_core.node.Node

/**
 * Facilitates fast parsing of the source text.
 *
 *
 * For nonterminal subtrees, the provided root will be added to the main, and text between
 * startIndex (inclusive) and endIndex (exclusive) will continue to be parsed into Nodes and
 * added as children under this root.
 *
 *
 * For terminal subtrees, the root will simply be added to the tree and no additional parsing will
 * take place on the text.
 */
class ParseSpec<T : Node> {
  val root: T?
  val isTerminal: Boolean
  var startIndex: Int = 0
  var endIndex: Int = 0

  constructor(root: T?, startIndex: Int, endIndex: Int) {
    this.root = root
    this.isTerminal = false
    this.startIndex = startIndex
    this.endIndex = endIndex
  }

  constructor(root: T?) {
    this.root = root
    this.isTerminal = true
  }

  fun applyOffset(offset: Int) {
    startIndex += offset
    endIndex += offset
  }

  companion object {

    fun <T : Node> createNonterminal(node: T?, startIndex: Int, endIndex: Int): ParseSpec<T> {
      return ParseSpec(node, startIndex, endIndex)
    }

    fun <T : Node> createTerminal(node: T?): ParseSpec<T> {
      return ParseSpec(node)
    }
  }
}

