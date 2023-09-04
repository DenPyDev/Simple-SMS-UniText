package com.simplemobiletools.smsmessenger.dialogs


import GoogleTranslate
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.commons.extensions.getAlertDialogBuilder
import com.simplemobiletools.commons.extensions.setupDialogStuff
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.smsmessenger.R
import com.simplemobiletools.smsmessenger.activities.SimpleActivity
import com.simplemobiletools.smsmessenger.databinding.DialogGoogleApiKeyBinding
import com.simplemobiletools.smsmessenger.extensions.config

class ChangeApiKeyDialog(
    private val activity: SimpleActivity,
    private val googleTranslate: GoogleTranslate,
) {
    private val config = activity.config

    init {
        val binding = DialogGoogleApiKeyBinding.inflate(activity.layoutInflater).apply {
            dialogGoogleApiKey.setText(googleTranslate.retrieveApiKey())

            hyperlinkButton.text = "How to get API key?"
            hyperlinkButton.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.hyperlink_url)))

                activity.startActivity(browserIntent)


            }
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(com.simplemobiletools.commons.R.string.ok, null)
            .setNegativeButton(com.simplemobiletools.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, R.string.export_messages) { alertDialog ->
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val apiKey = binding.dialogGoogleApiKey.text.toString()
                        when {
                            apiKey.isEmpty() -> activity.toast(com.simplemobiletools.commons.R.string.empty_name)
                            else -> {
                                googleTranslate.saveApiKey(apiKey) // Save the API key when confirmed
                                alertDialog.dismiss()
                            }
                        }
                    }
                }
            }
    }
}
