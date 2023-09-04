package com.simplemobiletools.smsmessenger.extensions
import android.content.Context


const val REVERSE_MESSAGE_SWITCH = "reverse_message_switch"
const val KEY_ALIAS = "your_preference_key_here"

fun isReverseMessageSwitchActive(context: Context): Boolean {
    val sharedPrefs = context.getSharedPreferences(KEY_ALIAS, Context.MODE_PRIVATE)
    return sharedPrefs.getBoolean(REVERSE_MESSAGE_SWITCH, false)
}

