package com.simplemobiletools.smsmessenger.extensions
import android.content.Context


const val TRANSLATE_MESSAGE_SWITCH = "reverse_message_switch"
const val KEY_ALIAS = "your_preference_key_here"


const val SELECTOR_SOURCE_LANG_VALUE = "SELECTOR_SOURCE_LANG"
const val SELECTOR_TARGET_LANG_VALUE = "SELECTOR_TARGET_LANG"

fun isTranslateMessageSwitchActive(context: Context): Boolean {
    val sharedPrefs = context.getSharedPreferences(KEY_ALIAS, Context.MODE_PRIVATE)
    return sharedPrefs.getBoolean(TRANSLATE_MESSAGE_SWITCH, false)
}

