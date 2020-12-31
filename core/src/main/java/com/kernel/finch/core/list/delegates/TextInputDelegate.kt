package com.kernel.finch.core.list.delegates

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.kernel.finch.FinchCore
import com.kernel.finch.common.contracts.component.Cell
import com.kernel.finch.components.TextInput
import com.kernel.finch.core.R
import com.kernel.finch.core.list.cells.TextCell
import com.kernel.finch.core.list.delegates.shared.ValueWrapperComponentDelegate
import com.kernel.finch.core.util.extension.applyTheme
import com.kernel.finch.core.util.extension.text

internal class TextInputDelegate : ValueWrapperComponentDelegate.String<TextInput>() {

    override fun createCells(component: TextInput): List<Cell<*>> =
        (DialogInterface.OnClickListener { dialog, button ->
            if (button == DialogInterface.BUTTON_POSITIVE && !component.areRealTimeUpdatesEnabled) {
                setNewValue(
                    component,
                    (dialog as AlertDialog).findViewById<EditText>(R.id.finch_edit_text)?.text?.toString()
                        .orEmpty()
                )
            }
            dialog.dismiss()
        }).let { onDialogButtonPressed ->
            listOf(
                TextCell(
                    id = component.id,
                    text = if (component.shouldRequireConfirmation && hasPendingChanges(component))
                        component.text(
                            getUiValue(component)
                        ).with("*")
                    else
                        component.text(getUiValue(component)),
                    isEnabled = component.isEnabled,
                    isSectionHeader = false,
                    icon = null,
                    onItemSelected = {
                        FinchCore.implementation.currentActivity?.applyTheme()?.run {
                            AlertDialog.Builder(this)
                                .setView(R.layout.finch_view_text_input_dialog)
                                .setPositiveButton(text(component.doneText), onDialogButtonPressed)
                                .apply {
                                    if (!component.areRealTimeUpdatesEnabled) {
                                        setNegativeButton(
                                            text(component.cancelText),
                                            onDialogButtonPressed
                                        )
                                    }
                                }
                                .create()
                                .apply {
                                    setOnShowListener {
                                        findViewById<EditText>(R.id.finch_edit_text)?.initialize(
                                            this,
                                            component
                                        )
                                    }
                                }
                                .show()
                        }
                    })
            )
        }

    private fun setNewValue(component: TextInput, newValue: kotlin.String) {
        if (component.validator(newValue)) {
            setUiValue(component, newValue)
        }
    }

    private fun EditText.initialize(dialog: Dialog, component: TextInput) {
        getCurrentValue(component).let { currentValue ->
            setText(currentValue)
            setSelection(currentValue.length)
            requestFocus()
            post {
                (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
                    this,
                    0
                )
            }
            setOnEditorActionListener { _, actionId, _ ->
                true.also {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (!component.areRealTimeUpdatesEnabled) {
                            setNewValue(component, text?.toString().orEmpty())
                        }
                        dialog.dismiss()
                    }
                }
            }
        }
        if (component.areRealTimeUpdatesEnabled) {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    setNewValue(component, s?.toString().orEmpty())
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
                    Unit
            })
        }
    }
}
