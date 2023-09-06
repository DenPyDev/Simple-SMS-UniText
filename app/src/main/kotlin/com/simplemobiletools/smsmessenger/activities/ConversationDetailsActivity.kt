package com.simplemobiletools.smsmessenger.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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

open class ConversationDetailsActivity : SimpleActivity() {

    var threadId: Long = 0L
    var conversation: Conversation? = null
    private lateinit var participants: ArrayList<SimpleContact>

    val binding by viewBinding(ActivityConversationDetailsBinding::inflate)

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


class ConversationDetailsActivityWithSpinners : ConversationDetailsActivity() {

    private val sourceSpinnerList: List<String?> = listOf(null) + LanguageConfig.possibleSources
    private val targetSpinnerList: List<String?> = listOf(null) + LanguageConfig.possibleTargets

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ensureBackgroundThread {
            runOnUiThread {
                setupSpinners()
            }
        }
    }

    private fun getParticipants(): ArrayList<SimpleContact> {
        return if (conversation != null && conversation!!.isScheduled) {
            val message = messagesDB.getThreadMessages(conversation!!.threadId).firstOrNull()
            message?.participants ?: arrayListOf()
        } else {
            getThreadParticipants(threadId, null)
        }
    }

    private fun setupSpinner(spinner: Spinner, dataList: List<String?>, initialSelectedValue: String?, onItemSelected: (String?) -> Unit) {
        val displayList = dataList.map { it ?: "---" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, displayList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(displayList.indexOf(initialSelectedValue ?: "---"))
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedLang = if (displayList[position] == "---") null else dataList[position]
                onItemSelected(selectedLang)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                onItemSelected(null)
            }
        }
    }

    private fun setupSpinners() {
        setupSpinner(binding.spinnerSourceLang, sourceSpinnerList, conversation?.sourceLang) { selectedLang ->
            ensureBackgroundThread {
                conversation = changeConversationSourceLang(conversation!!, selectedLang)
            }
        }

        setupSpinner(binding.spinnerTargetLang, targetSpinnerList, conversation?.targetLang) { selectedLang ->
            ensureBackgroundThread {
                conversation = changeConversationTargetLang(conversation!!, selectedLang)
            }
        }
    }
}
