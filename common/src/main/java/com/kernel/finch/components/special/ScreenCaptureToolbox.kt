package com.kernel.finch.components.special

import com.kernel.finch.common.contracts.Finch
import com.kernel.finch.common.contracts.component.ExpandableComponent
import com.kernel.finch.common.models.Text

data class ScreenCaptureToolbox(
    val title: Text = Text.CharSequence(DEFAULT_TITLE),
    val imageText: Text? = Text.CharSequence(DEFAULT_IMAGE_TEXT),
    val videoText: Text? = Text.CharSequence(DEFAULT_VIDEO_TEXT),
    val galleryText: Text = Text.CharSequence(DEFAULT_GALLERY_TEXT),
    override val isExpandedInitially: Boolean = DEFAULT_IS_EXPANDED_INITIALLY
) : ExpandableComponent<ScreenCaptureToolbox> {

    override val id: String = ID

    override fun getHeaderTitle(finch: Finch) = title

    companion object {
        const val ID = "screenCaptureToolbox"
        private const val DEFAULT_TITLE = "Screen capture tools"
        private const val DEFAULT_IMAGE_TEXT = "Take a screenshot"
        private const val DEFAULT_VIDEO_TEXT = "Record a video"
        private const val DEFAULT_GALLERY_TEXT = "Open the gallery"
        private const val DEFAULT_IS_EXPANDED_INITIALLY = false
    }
}
