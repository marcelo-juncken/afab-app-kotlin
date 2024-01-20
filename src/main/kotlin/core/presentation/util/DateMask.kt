package core.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import core.util.Constants.MAX_DATE_LENGTH


class DateMask : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return dateFilter(text)
    }
}

fun dateFilter(text: AnnotatedString): TransformedText {

    val trimmed = text.text.take(MAX_DATE_LENGTH)
    val out = buildString {
        trimmed.forEachIndexed { index, char ->
            append(char)
            if (index % 2 == 1 && index < 4) {
                append('/')
            }
        }
    }

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return when (offset) {
                in 0..1 -> offset
                in 2..3 -> offset + 1
                in 4..8 -> offset + 2
                else -> 10
            }
        }

        override fun transformedToOriginal(offset: Int): Int {
            return when (offset) {
                in 0..2 -> offset
                in 3..5 -> offset - 1
                in 6..10 -> offset - 2
                else -> 8
            }
        }
    }

    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
}

val VisualTransformation.Companion.DateMask: VisualTransformation
    get() = DateMask()