import android.content.Context
import com.simplemobiletools.smsmessenger.languages.TransliterationMaps
import com.simplemobiletools.smsmessenger.languages.Transliterator

const val ORIGINAL = "Original"
const val ENGLISH = "English"

data class TransliterationKey(val source: String, val target: String)


fun String.removeLeadingEn(): String {
    return replace(Regex("^En([A-Z])"), "$1")
}


object LanguageTransliterationManager {
    val languageMap = mapOf(
        ENGLISH to "en",
        "Georgian" to "ka",
        "Armenian" to "hy",
        "Kazakh" to "kk",
        "Serbian" to "sr",
        "Ukrainian" to "uk",
        "Russian" to "ru",
        ORIGINAL to ORIGINAL
    )

    val transliterationMap = mapOf(
        TransliterationKey(ENGLISH, "Georgian") to TransliterationMaps.Georgian.Standard,
        TransliterationKey(ENGLISH, "Armenian") to TransliterationMaps.Armenian.EasternArmenian,
        TransliterationKey(ENGLISH, "Kazakh") to TransliterationMaps.Kazakh.Standard,
        TransliterationKey(ENGLISH, "Serbian") to TransliterationMaps.Serbian.Standard,
        TransliterationKey(ENGLISH, "Ukrainian") to TransliterationMaps.Ukrainian.Standard,
        TransliterationKey(ENGLISH, "Russian") to TransliterationMaps.Russian.Standard
    )

    private fun sortListByRemovingLeadingEn(list: List<String?>): List<String?> {
        return list.sortedBy { it?.removeLeadingEn() }
    }

    val sourceList: List<String?> = sortListByRemovingLeadingEn(
        (languageMap.keys + transliterationMap.keys.map { it.target }).toList())
    val targetList: List<String?> = sortListByRemovingLeadingEn(languageMap.keys.toList())
}


class Processor(private val context: Context) {
    private val transliterator = Transliterator()

    fun process(text: String, sourceLang: String?, targetLang: String?): String {
        if (sourceLang.isNullOrEmpty() || targetLang.isNullOrEmpty() || sourceLang == ORIGINAL || targetLang == ORIGINAL) return text

        val transliteratedText = LanguageTransliterationManager.transliterationMap[TransliterationKey(ENGLISH, sourceLang)]
            ?.let { transliterator.transliterate(text, it) } ?: text

        val sourceLangAbbr = LanguageTransliterationManager.languageMap[sourceLang]
        val targetLangAbbr = LanguageTransliterationManager.languageMap[targetLang]

        return if (sourceLangAbbr != null && targetLangAbbr != null) {
            GoogleTranslate(context).translate(text = transliteratedText, sourceLang = sourceLangAbbr, targetLang = targetLangAbbr)
        } else {
            transliteratedText
        }
    }
}
