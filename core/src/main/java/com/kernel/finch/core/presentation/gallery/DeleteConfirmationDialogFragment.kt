package com.kernel.finch.core.presentation.gallery

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.kernel.finch.core.R
import com.kernel.finch.core.util.extension.applyTheme
import com.kernel.finch.core.util.extension.text

internal class DeleteConfirmationDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        requireContext().applyTheme().let { context ->
            AlertDialog.Builder(context)
                .setMessage(context.text(R.string.finch_delete_confirmation_message))
                .setPositiveButton(context.text(R.string.finch_delete)) { _, _ ->
                    ((parentFragment as? OnPositiveButtonClickedListener?)
                        ?: (activity as? OnPositiveButtonClickedListener?))?.onPositiveButtonClicked()
                    dismiss()
                }
                .setNegativeButton(context.text(R.string.finch_cancel)) { _, _ -> dismiss() }
                .create()
        }

    interface OnPositiveButtonClickedListener {
        fun onPositiveButtonClicked()
    }

    companion object {

        fun show(fragmentManager: FragmentManager) =
            DeleteConfirmationDialogFragment()
                .run { show(fragmentManager, tag) }
    }
}
