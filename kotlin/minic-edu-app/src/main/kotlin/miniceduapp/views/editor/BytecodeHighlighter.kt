package miniceduapp.views.editor

import miniceduapp.views.styles.BytecodeHighlightStyles
import java.util.regex.Pattern

class BytecodeHighlighter : RegexSyntaxHighlighter(PATTERN, BytecodeHighlightStyles()) {

    companion object {
        private val LABEL_PATTERN = "L\\d+\n"


        private val PATTERN = Pattern.compile(
                "(?<BYTECODELABEL>" + LABEL_PATTERN + ")"
        )
    }
}
