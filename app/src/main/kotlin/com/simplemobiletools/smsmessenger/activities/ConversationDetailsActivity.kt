package com.simplemobiletools.smsmessenger.activities

import android.R
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.res.ResourcesCompat
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.NavigationIcon
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.commons.models.SimpleContact
import com.simplemobiletools.smsmessenger.adapters.ContactsAdapter
import com.simplemobiletools.smsmessenger.databinding.ActivityConversationDetailsBinding
import com.simplemobiletools.smsmessenger.language_convertors.UIprocessor
import com.simplemobiletools.smsmessenger.dialogs.RenameConversationDialog
import com.simplemobiletools.smsmessenger.extensions.*
import com.simplemobiletools.smsmessenger.helpers.THREAD_ID
import com.simplemobiletools.smsmessenger.models.Conversation
import android.view.View
import android.widget.AdapterView

class ConversationDetailsActivity : SimpleActivity() {

    private var threadId: Long = 0L
    private var conversation: Conversation? = null
    private lateinit var participants: ArrayList<SimpleContact>

    private val binding by viewBinding(ActivityConversationDetailsBinding::inflate)

    private fun setTranslateMessageSwitchState(context: Context, isActive: Boolean) {
        val sharedPrefs = context.getSharedPreferences(KEY_ALIAS, Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putBoolean(TRANSLATE_MESSAGE_SWITCH, isActive)
            apply()
        }
    }

    private fun saveSpinnerValueToSharedPrefs(key: String, value: String) {
        val sharedPrefs = getSharedPreferences(KEY_ALIAS, Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString(key, value)
            apply()
        }
    }


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

        binding.translateMessageSwitch.setOnCheckedChangeListener { _, isChecked ->
            setTranslateMessageSwitchState(this, isChecked)
        }


//        val dataListSourceLang: List<String> = mutableListOf("-", "en", "ka","en_ka", "ar", "en_ar", "kz","en_kz", "srb","en_srb", "ru", "en_ru", "arab", "en_arab")
//        val dataListTargetLang: List<String> = mutableListOf("-","en", "ka", "ar", "kz", "srb", "ru", "arab")
        val uiProcessor = UIprocessor()
        val adapterSourceLang = ArrayAdapter(this, R.layout.simple_spinner_item, uiProcessor.dataListSourceLang)
        val adapterTargetLang = ArrayAdapter(this, R.layout.simple_spinner_item, uiProcessor.dataListTargetLang)

        adapterSourceLang.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        adapterTargetLang.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        val selectorSourceLang = binding.selectorSourceLang
        val selectorTargetLang = binding.selectorTargetLang

        selectorSourceLang.adapter = adapterSourceLang
        selectorTargetLang.adapter = adapterTargetLang


        fun getSpinnerValueFromSharedPrefs(key: String): String? {
            val sharedPrefs = getSharedPreferences(KEY_ALIAS, Context.MODE_PRIVATE)
            return sharedPrefs.getString(key, null)
        }

        selectorSourceLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedValue = parent.getItemAtPosition(position).toString()
                saveSpinnerValueToSharedPrefs(SELECTOR_SOURCE_LANG_VALUE, selectedValue)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing here
            }
        }

        selectorTargetLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedValue = parent.getItemAtPosition(position).toString()
                saveSpinnerValueToSharedPrefs(SELECTOR_TARGET_LANG_VALUE, selectedValue)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing here
            }
        }


        val savedSourceLangValue = getSpinnerValueFromSharedPrefs(SELECTOR_SOURCE_LANG_VALUE)
        val savedTargetLangValue = getSpinnerValueFromSharedPrefs(SELECTOR_TARGET_LANG_VALUE)

        selectorSourceLang.setSelection(adapterSourceLang.getPosition(savedSourceLangValue))
        selectorTargetLang.setSelection(adapterTargetLang.getPosition(savedTargetLangValue))



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
