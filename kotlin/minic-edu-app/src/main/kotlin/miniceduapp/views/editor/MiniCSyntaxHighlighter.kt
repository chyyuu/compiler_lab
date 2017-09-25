package miniceduapp.views.editor

import miniceduapp.views.styles.CodeHighlightStyles
import java.util.regex.Pattern

class MiniCSyntaxHighlighter : RegexSyntaxHighlighter(PATTERN, CodeHighlightStyles()) {

    companion object {
        private val KEYWORDS = arrayOf("break", "continue", "while", "else", "if")
        private val TYPES = arrayOf("bool", "double", "int", "string")

        private val KEYWORD_PATTERN = "\\b(" + KEYWORDS.joinToString("|") + ")\\b"
        private val DATATYPE_PATTERN = "\\b(" + TYPES.joinToString("|") + ")\\b"
        private val NUMBER_PATTERN = "\\d+\\.?\\d*"
        private val BOOLVALUE_PATTERN = "\\b(true|false)\\b"
        private val PAREN_PATTERN = "\\(|\\)"
        private val BRACE_PATTERN = "\\{|\\}"
        private val BRACKET_PATTERN = "\\[|\\]"
        private val SEMICOLON_PATTERN = "\\;"
        private val STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\""
        private val COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"

        private val PATTERN = Pattern.compile(
                "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                        + "|(?<DATATYPE>" + DATATYPE_PATTERN + ")"
                        + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
                        + "|(?<BOOLVALUE>" + BOOLVALUE_PATTERN + ")"
                        + "|(?<PAREN>" + PAREN_PATTERN + ")"
                        + "|(?<BRACE>" + BRACE_PATTERN + ")"
                        + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                        + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                        + "|(?<STRING>" + STRING_PATTERN + ")"
                        + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
        )
    }
}
