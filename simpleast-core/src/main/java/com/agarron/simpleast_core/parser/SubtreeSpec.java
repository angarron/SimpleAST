package com.agarron.simpleast_core.parser;

import com.agarron.simpleast_core.node.Node;

/**
 * Facilitates fast parsing of the source text.
 * <p>
 * For nonterminal subtrees, the provided root will be added to the main, and text between
 * startIndex (inclusive) and endIndex (exclusive) will continue to be parsed into Nodes and
 * added as children under this root.
 * <p>
 * For terminal subtrees, the root will simply be added to the tree and no additional parsing will
 * take place on the text.
 */
public class SubtreeSpec<T extends Node> {
  final T root;
  final boolean isTerminal;
  int startIndex;
  int endIndex;

  public static <T extends Node> SubtreeSpec<T> createNonterminal(T node, int startIndex, int endIndex) {
    return new SubtreeSpec<>(node, startIndex, endIndex);
  }

  public static <T extends Node> SubtreeSpec<T> createTerminal(T node) {
    return new SubtreeSpec<>(node);
  }

  SubtreeSpec(T root, int startIndex, int endIndex) {
    this.root = root;
    this.isTerminal = false;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
  }

  SubtreeSpec(T root) {
    this.root = root;
    this.isTerminal = true;
  }

  void applyOffset(final int offset) {
    startIndex += offset;
    endIndex += offset;
  }
}

