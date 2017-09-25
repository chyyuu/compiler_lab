package miniceduapp.views.editor

import miniceduapp.views.styles.ProgramExecutionHighlightStyles
import java.util.regex.Pattern

class ProgramExecutionHighlighter : RegexSyntaxHighlighter(PATTERN, ProgramExecutionHighlightStyles()) {

    companion object {
        private val COMMAND_PATTERN = "> .+?\n"
        private val EXCEPTION_PATTERN = "((Exception in thread)|(\tat )).+"

        private val PATTERN = Pattern.compile(
                "(?<COMMAND>" + COMMAND_PATTERN + ")"
                        + "|(?<EXCEPTION>" + EXCEPTION_PATTERN + ")"
        )
    }
}
