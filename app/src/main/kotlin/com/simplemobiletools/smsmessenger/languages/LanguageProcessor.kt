package com.simplemobiletools.smsmessenger.languages

import GoogleTranslate
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


data class TransliterationKey(val target: String, val trMap: Map<String, String>)

fun String.removeLeadingEn(): String {
    return replace(Regex("^En([A-Z])"), "$1")
}


object LanguageTransliterationManager {
    val languageMap = mapOf(
        "English" to "en",
        "Georgian" to "ka",
        "Armenian" to "hy",
        "Kazakh" to "kk",
        "Serbian" to "sr",
        "Ukrainian" to "uk",
        "Russian" to "ru",
        "Original" to "Original"
    )

    val transliterationMap = mapOf(
        "EnGeorgian" to TransliterationKey("ka", TransliterationMaps.Georgian.Standard),
        "EnArmenian" to TransliterationKey("hy", TransliterationMaps.Armenian.EasternArmenian),
        "EnKazakh" to TransliterationKey("kk", TransliterationMaps.Kazakh.Standard),
        "EnSerbian" to TransliterationKey("sr", TransliterationMaps.Serbian.Standard),
        "EnUkrainian" to TransliterationKey("uk", TransliterationMaps.Ukrainian.Standard),
        "EnRussian" to TransliterationKey("ru", TransliterationMaps.Russian.Standard),
    )

    private fun sortListByRemovingLeadingEn(list: List<String?>): List<String?> {
        return list.sortedBy { it?.removeLeadingEn() }
    }

    val sourceList: List<String?> = sortListByRemovingLeadingEn((languageMap.keys + transliterationMap.keys).toList())
    val targetList: List<String?> = sortListByRemovingLeadingEn(languageMap.keys.toList())
}


class Processor(private val context: Context) {
    private val transliterator = Transliterator()

    fun process(text: String, sourceLang: String?, targetLang: String?): String {
        if (sourceLang.isNullOrEmpty() || targetLang.isNullOrEmpty() || sourceLang == "Original" || targetLang == "Original") {
            runBlocking {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Rollback, reenter chat", Toast.LENGTH_LONG).show()
                }
            }
            return text
        }
        val transliteratedText = LanguageTransliterationManager.transliterationMap[sourceLang]
            ?.let { transliterator.transliterate(text, it.trMap) } ?: text
        val sourceLangAbbr = LanguageTransliterationManager.languageMap[sourceLang.removeLeadingEn()]
        val targetLangAbbr = LanguageTransliterationManager.languageMap[targetLang]

        return if (sourceLangAbbr != null && targetLangAbbr != null) {
            GoogleTranslate(context).translate(
                text = transliteratedText,
                sourceLang = sourceLangAbbr,
                targetLang = targetLangAbbr)
        } else {
            runBlocking {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Transliterated, reenter chat", Toast.LENGTH_LONG).show()
                }
            }
            transliteratedText
        }
    }
}
