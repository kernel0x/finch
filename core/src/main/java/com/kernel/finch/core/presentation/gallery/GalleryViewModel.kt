package com.kernel.finch.core.presentation.gallery

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kernel.finch.core.manager.ScreenCaptureManager
import com.kernel.finch.core.presentation.gallery.list.GalleryListItem
import com.kernel.finch.core.presentation.gallery.list.ImageViewHolder
import com.kernel.finch.core.presentation.gallery.list.SectionHeaderViewHolder
import com.kernel.finch.core.presentation.gallery.list.VideoViewHolder
import com.kernel.finch.core.util.extension.getScreenCapturesFolder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

internal class GalleryViewModel : ViewModel() {

    private val _items = MutableLiveData<List<GalleryListItem>>()
    val items: LiveData<List<GalleryListItem>> = _items
    private val _isInSelectionMode = MutableLiveData(false)
    val isInSelectionMode: LiveData<Boolean> = _isInSelectionMode
    var selectedItemIds = emptyList<String>()
        private set(value) {
            if (field != value) {
                field = value
                _isInSelectionMode.postValue(value.isNotEmpty())
            }
        }
    private var files = emptyList<File>()

    init {
        _isInSelectionMode.observeForever {
            if (!it) {
                selectedItemIds = emptyList()
            }
        }
    }

    fun loadMedia(context: Context) {
        viewModelScope.launch {
            files = context.getScreenCapturesFolder().listFiles().orEmpty().toList()
            refresh()
        }
    }

    fun isSectionHeader(position: Int) = try {
        _items.value?.get(position) is SectionHeaderViewHolder.UiModel
    } catch (_: IndexOutOfBoundsException) {
        false
    }

    private fun refresh() {
        viewModelScope.launch {
            _items.value = files.sortedByDescending { it.lastModified() }.mapNotNull { file ->
                file.name.let { fileName ->
                    when {
                        fileName.endsWith(ScreenCaptureManager.IMAGE_EXTENSION) -> ImageViewHolder.UiModel(
                            fileName,
                            selectedItemIds.contains(fileName),
                            file.lastModified()
                        )
                        fileName.endsWith(ScreenCaptureManager.VIDEO_EXTENSION) -> VideoViewHolder.UiModel(
                            fileName,
                            selectedItemIds.contains(fileName),
                            file.lastModified()
                        )
                        else -> null
                    }
                }
            }.let { mediaUiModels ->
                mutableListOf<GalleryListItem>().apply {
                    mediaUiModels.forEachIndexed { index, mediaUiModel ->
                        val midnight = mediaUiModel.lastModified.midnightOfThatDay()
                        if (index == 0 || mediaUiModels[index - 1].lastModified.midnightOfThatDay() != midnight) {
                            add(SectionHeaderViewHolder.UiModel(midnight))
                        }
                        add(mediaUiModel)
                    }
                }
            }
            selectedItemIds = selectedItemIds.filterNot { id -> files.none { it.name == id } }
        }
    }

    private fun Long.midnightOfThatDay() = Calendar.getInstance().apply {
        timeInMillis = this@midnightOfThatDay
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    fun selectItem(id: String) {
        selectedItemIds = if (selectedItemIds.contains(id)) {
            selectedItemIds.filterNot { it == id }
        } else {
            (selectedItemIds + id).distinct()
        }
        refresh()
    }

    fun exitSelectionMode() {
        _isInSelectionMode.postValue(false)
        selectedItemIds = emptyList()
        refresh()
    }

    fun deleteSelectedItems() {
        GlobalScope.launch {
            files.filter { selectedItemIds.contains(it.name) }.forEach { it.delete() }
            files = files.filterNot { selectedItemIds.contains(it.name) }
            exitSelectionMode()
        }
    }
}
