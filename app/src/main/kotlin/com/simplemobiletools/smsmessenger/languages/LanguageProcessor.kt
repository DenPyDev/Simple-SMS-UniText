import android.content.Context
import com.simplemobiletools.smsmessenger.languages.TransliterationMaps
import com.simplemobiletools.smsmessenger.languages.Transliterator

object LanguageConfig {
    val languageMap = mapOf(
        "English" to "en",
        "Georgian" to "ka",
        "Armenian" to "hy",
        "Kazakh" to "kk",
        "Serbian" to "sr",
        "Russian" to "ru"
    )

    val transliterationMap = mapOf(
        "EnGeorgian" to TransliterationMaps.Georgian.Standard,
        "EnArmenian" to TransliterationMaps.Armenian.EasternArmenian,
        "EnKazakh" to TransliterationMaps.Kazakh.Standard,
        "EnSerbian" to TransliterationMaps.Serbian.Standard,
        "EnRussian" to TransliterationMaps.Russian.Standard
    )

    val possibleSources = languageMap.keys + transliterationMap.keys
    val possibleTargets = languageMap.keys
}

class Processor(private val context: Context) {
    private val transliterator = Transliterator()

    fun process(text: String, sourceLang: String?, targetLang: String?): String {
        if (sourceLang == null || targetLang == null) return text
        if (sourceLang !in LanguageConfig.possibleSources || targetLang !in LanguageConfig.possibleTargets) return text

        val translitMap = LanguageConfig.transliterationMap[sourceLang]
        var result = if (sourceLang == "EnGeorgian") text.lowercase() else text

        translitMap?.let {
            result = transliterator.transliterate(result, it)
        }

        val sourceLangAbbr = LanguageConfig.languageMap[sourceLang] ?: LanguageConfig.languageMap[removeLeadingEn(sourceLang)]
        val targetLangAbbr = LanguageConfig.languageMap[targetLang]

        if (sourceLangAbbr != null && targetLangAbbr != null) {
            result = GoogleTranslate(context).translate(text = result, sourceLang = sourceLangAbbr, targetLang = targetLangAbbr)
        }

        return result
    }

    private fun removeLeadingEn(str: String): String {
        return if (str.startsWith("En")) str.drop(2) else str
    }
}
