package com.simplemobiletools.smsmessenger.language_convertors

import com.simplemobiletools.smsmessenger.language_convertors.lang_maps.Geo

class Transliterator {
    fun transliterate(input: String, translitMap: Map<String, String>): String {
        var result = input
        for (entry in translitMap) {
            result = result.replace(entry.key, entry.value)
        }
        return result
    }
}

//fun main() {
//    val translator = Transliterator()
//    val testString = "kotlins"
//    println(translator.transliterate(testString, Geo.translitMap))
//}
