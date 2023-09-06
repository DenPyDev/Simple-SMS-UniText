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

class ConversationDetailsActivity : SimpleActivity() {

    private var threadId: Long = 0L
    private var conversation: Conversation? = null
    private lateinit var participants: ArrayList<SimpleContact>

    private val binding by viewBinding(ActivityConversationDetailsBinding::inflate)

    private val dataListSourceLang: List<String?> = mutableListOf(null, "en", "ka", "en_ka", "ar", "en_ar", "kz", "en_kz", "srb", "en_srb", "ru", "en_ru", "arab", "йцуйцу")
    private val dataListTargetLang: List<String?> = mutableListOf(null, "en", "ka", "ar", "kz", "srb", "ru", "arab", "arab", "arab", "arab", "arab", "arab", "arab", "arab", "arab", "arab", "arab", "arab", "arab", "arab", "arab", "arab")

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
            participants = getParticipants()
            runOnUiThread {
                setupTextViews()
                setupParticipants()
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
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dataList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(dataList.indexOf(initialSelectedValue))
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedLang = dataList[position]
                onItemSelected(selectedLang)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                onItemSelected(null)
            }
        }
    }


    private fun setupSpinners() {

        setupSpinner(binding.spinnerSourceLang, dataListSourceLang, conversation?.sourceLang) { selectedLang ->
            ensureBackgroundThread {
                conversation = changeConversationSourceLang(conversation!!, selectedLang)
            }
        }

        setupSpinner(binding.spinnerTargetLang, dataListTargetLang, conversation?.targetLang) { selectedLang ->
            ensureBackgroundThread {
                conversation = changeConversationTargetLang(conversation!!, selectedLang)
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
