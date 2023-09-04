package com.simplemobiletools.smsmessenger.extensions

import android.content.Context
import com.simplemobiletools.smsmessenger.activities.ConversationDetailsActivity

fun isReverseMessageSwitchActive(context: Context): Boolean {
    val sharedPrefs = context.getSharedPreferences(ConversationDetailsActivity.KEY_ALIAS, Context.MODE_PRIVATE)
    return sharedPrefs.getBoolean(ConversationDetailsActivity.REVERSE_MESSAGE_SWITCH, false)
}
