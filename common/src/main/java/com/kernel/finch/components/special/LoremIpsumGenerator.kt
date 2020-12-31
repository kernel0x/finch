package com.kernel.finch.components.special

import androidx.annotation.DrawableRes
import com.kernel.finch.common.contracts.component.Component
import com.kernel.finch.common.models.Text
import com.kernel.finch.components.Label

data class LoremIpsumGenerator(
    val text: Text = Text.CharSequence(DEFAULT_TEXT),
    val minimumWordCount: Int = DEFAULT_MINIMUM_WORD_COUNT,
    val maximumWordCount: Int = DEFAULT_MAXIMUM_WORD_COUNT,
    val shouldStartWithLoremIpsum: Boolean = DEFAULT_SHOULD_START_WITH_LOREM_IPSUM,
    val shouldGenerateSentence: Boolean = DEFAULT_SHOULD_GENERATE_SENTENCE,
    val type: Label.Type = DEFAULT_TYPE,
    @DrawableRes val icon: Int? = DEFAULT_ICON,
    val isEnabled: Boolean = DEFAULT_IS_ENABLED,
    val onLoremIpsumReady: (generatedText: String) -> Unit
) : Component<LoremIpsumGenerator> {

    override val id: String = ID

    companion object {
        const val ID = "loremIpsumGenerator"
        private const val DEFAULT_TEXT = "Generate Lorem Ipsum"
        private const val DEFAULT_MINIMUM_WORD_COUNT = 5
        private const val DEFAULT_MAXIMUM_WORD_COUNT = 20
        private const val DEFAULT_SHOULD_START_WITH_LOREM_IPSUM = true
        private const val DEFAULT_SHOULD_GENERATE_SENTENCE = false
        private val DEFAULT_TYPE = Label.Type.BUTTON
        private val DEFAULT_ICON: Int? = null
        private const val DEFAULT_IS_ENABLED = true
    }
}
