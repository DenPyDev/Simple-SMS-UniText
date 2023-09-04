package com.simplemobiletools.smsmessenger.activities

import android.content.Context
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.NavigationIcon
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.commons.models.SimpleContact
import com.simplemobiletools.smsmessenger.adapters.ContactsAdapter
import com.simplemobiletools.smsmessenger.databinding.ActivityConversationDetailsBinding
import com.simplemobiletools.smsmessenger.dialogs.RenameConversationDialog
import com.simplemobiletools.smsmessenger.extensions.*
import com.simplemobiletools.smsmessenger.helpers.THREAD_ID
import com.simplemobiletools.smsmessenger.models.Conversation
import com.simplemobiletools.smsmessenger.receivers.SmsReceiver
import com.simplemobiletools.smsmessenger.extensions.isReverseMessageSwitchActive

private fun isMessageToBeReversed(context: Context): Boolean {
    return isReverseMessageSwitchActive(context)
}


class ConversationDetailsActivity : SimpleActivity() {
    companion object {
        const val REVERSE_MESSAGE_SWITCH = "reverse_message_switch"
        const val KEY_ALIAS = "your_preference_key_here"  // Add this line
    }

    private var threadId: Long = 0L
    private var conversation: Conversation? = null
    private lateinit var participants: ArrayList<SimpleContact>

    private val binding by viewBinding(ActivityConversationDetailsBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        isMaterialActivity = true
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        updateMaterialActivityViews(
            mainCoordinatorLayout = binding.conversationDetailsCoordinator,
            nestedView = binding.participantsRecyclerview,
            useTransparentNavigation = true,
            useTopSearchMenu = false
        )
        setupMaterialScrollListener(scrollingView = binding.participantsRecyclerview, toolbar = binding.conversationDetailsToolbar)


        fun setReverseMessageSwitchState(context: Context, isActive: Boolean) {
            val sharedPrefs = context.getSharedPreferences(KEY_ALIAS, Context.MODE_PRIVATE)
            with(sharedPrefs.edit()) {
                putBoolean(ConversationDetailsActivity.REVERSE_MESSAGE_SWITCH, isActive)
                apply()
            }
        }

        binding.reverseMessageSwitch.setOnCheckedChangeListener { _, isChecked ->
            setReverseMessageSwitchState(this, isChecked)
        }

        fun isReverseMessageSwitchActive(context: Context): Boolean {
            val sharedPrefs = context.getSharedPreferences(KEY_ALIAS, Context.MODE_PRIVATE)
            return sharedPrefs.getBoolean(ConversationDetailsActivity.REVERSE_MESSAGE_SWITCH, false)
        }


        binding.reverseMessageSwitch.isChecked = isReverseMessageSwitchActive(this)

        threadId = intent.getLongExtra(THREAD_ID, 0L)
        ensureBackgroundThread {
            conversation = conversationsDB.getConversationWithThreadId(threadId)
            participants = if (conversation != null && conversation!!.isScheduled) {
                val message = messagesDB.getThreadMessages(conversation!!.threadId).firstOrNull()
                message?.participants ?: arrayListOf()
            } else {
                getThreadParticipants(threadId, null)
            }
            runOnUiThread {
                setupTextViews()
                setupParticipants()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupToolbar(binding.conversationDetailsToolbar, NavigationIcon.Arrow)
        updateTextColors(binding.conversationDetailsHolder)

        val primaryColor = getProperPrimaryColor()
        binding.conversationNameHeading.setTextColor(primaryColor)
        binding.membersHeading.setTextColor(primaryColor)
    }

    private fun setupTextViews() {
        binding.conversationName.apply {
            ResourcesCompat.getDrawable(resources, com.simplemobiletools.commons.R.drawable.ic_edit_vector, theme)?.apply {
                applyColorFilter(getProperTextColor())
                setCompoundDrawablesWithIntrinsicBounds(null, null, this, null)
            }

            text = conversation?.title
            setOnClickListener {
                RenameConversationDialog(this@ConversationDetailsActivity, conversation!!) { title ->
                    text = title
                    ensureBackgroundThread {
                        conversation = renameConversation(conversation!!, newTitle = title)
                    }
                }
            }
        }
    }

    private fun setupParticipants() {
        val adapter = ContactsAdapter(this, participants, binding.participantsRecyclerview) {
            val contact = it as SimpleContact
            val address = contact.phoneNumbers.first().normalizedNumber
            getContactFromAddress(address) { simpleContact ->
                if (simpleContact != null) {
                    startContactDetailsIntent(simpleContact)
                }
            }
        }
        binding.participantsRecyclerview.adapter = adapter
    }
}
