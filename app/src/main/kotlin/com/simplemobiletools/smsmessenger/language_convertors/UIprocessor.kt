package com.simplemobiletools.smsmessenger.language_convertors

import GoogleTranslate
import com.simplemobiletools.smsmessenger.language_convertors.lang_maps.Geo
import android.content.Context

class UIprocessor {

    private val langHashMap: HashMap<String, String> = hashMapOf(
        "English" to "en",
        "Georgian" to "ka",
        "Armenian" to "hy",
        "Kazakh" to "kk",
        "Serbian" to "sr",
        "Russian" to "ru"
    )

    private val translitHashMap: HashMap<String, String> = hashMapOf(
        "EnGeorgian" to "ka",
        "EnArmenian" to "hy",
        "EnKazakh" to "kk",
        "EnSerbian" to "sr",
        "EnRussian" to "ru"
    )

    private val transliteratorHashMap: HashMap<String, Map<String, String>> = hashMapOf(
        "EnGeorgian" to Geo.translitMap,
        "EnArmenian" to Geo.translitMap,
        "EnKazakh" to Geo.translitMap,
        "EnSerbian" to Geo.translitMap,
        "EnRussian" to Geo.translitMap,
    )

    val dataListSourceLang: List<String> = listOf("-")  + langHashMap.keys.toList() + translitHashMap.keys.toList()
    val dataListTargetLang: List<String> = listOf("-")  +  langHashMap.keys.toList()
    val transliterator = Transliterator()

    fun Process(context: Context, sourceLang: String, targetLang: String, text: String): String {
        var result = text

        val tr_source_g_lang = transliteratorHashMap[sourceLang]

        if (tr_source_g_lang != null) {
            result = transliterator.transliterate(result, tr_source_g_lang)
        }


            val source_g_lang = langHashMap[sourceLang] ?: translitHashMap[sourceLang]
            val target_g_lang = langHashMap[targetLang]



            if (source_g_lang == null || target_g_lang == null) {
                return result
            }
        val googleTranslate = GoogleTranslate(context)
            result = googleTranslate.translate(result, sourceLang = source_g_lang, targetLang = target_g_lang)
        return result

    }
}
