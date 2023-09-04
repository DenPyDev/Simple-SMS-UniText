package com.simplemobiletools.smsmessenger.dialogs

import GoogleTranslate
import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.smsmessenger.R
import com.simplemobiletools.smsmessenger.activities.SimpleActivity
import com.simplemobiletools.smsmessenger.databinding.DialogGoogleApiKeyBinding
import com.simplemobiletools.smsmessenger.extensions.config

class ChangeApiKeyDialog(
    private val activity: SimpleActivity,
    private val googleTranslate: GoogleTranslate, // Add this
//    private val callback: (apiKey: String) -> Unit,
) {
    private val config = activity.config

    init {
        val binding = DialogGoogleApiKeyBinding.inflate(activity.layoutInflater).apply {
            dialogGoogleApiKey.setText(googleTranslate.retrieveApiKey()) // Load the API key when initialized
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(com.simplemobiletools.commons.R.string.ok, null)
            .setNegativeButton(com.simplemobiletools.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, R.string.export_messages) { alertDialog ->
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val apiKey = binding.dialogGoogleApiKey.value
                        when {
                            apiKey.isEmpty() -> activity.toast(com.simplemobiletools.commons.R.string.empty_name)
                            else -> {
                                googleTranslate.saveApiKey(apiKey) // Save the API key when confirmed
//                                callback(apiKey)
                                alertDialog.dismiss()
                            }
                        }
                    }
                }
            }
    }
}
