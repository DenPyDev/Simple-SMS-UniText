package com.simplemobiletools.smsmessenger.receivers

public class SmsTranslator {
    private val georgianMap = mapOf(
        "a" to "ა",
        "b" to "ბ",
        "g" to "გ",
        "d" to "დ",
        "e" to "ე",
        "v" to "ვ",
        "w" to "ვ",
        "z" to "ზ",
        "t" to "თ",
        "i" to "ი",
        "ქ'" to "კ",
        "l" to "ლ",
        "m" to "მ",
        "n" to "ნ",
        "o" to "ო",
        "ფ'" to "პ",
        "ზჰ" to "ჟ",
        "r" to "რ",
        "s" to "ს",
        "თ'" to "ტ",
        "u" to "უ",
        "p" to "ფ",
        "f" to "ფ",
        "k" to "ქ",
        "გჰ" to "ღ",
        "q" to "ყ",
        "სჰ" to "შ",
        "ცჰ" to "ჩ",
        "თს" to "ც",
        "c" to "ც",
        "დზ" to "ძ",
        "ც'" to "წ",
        "ჩ'" to "ჭ",
        "ქჰ" to "ხ",
        "x" to "ხ",
        "j" to "ჯ",
        "h" to "ჰ",
        "--" to "—"
    )

    fun transliterateToGeorgian(input: String): String {
        var result = input
        georgianMap.entries.sortedByDescending { it.key.length }.forEach { entry ->
            result = result.replace(entry.key, entry.value)
        }
        return result
    }
}

//    fun main() {
//        val translator = SmsTranslator()
//        val testString = "kotlins"
//        println(translator.transliterateToGeorgian(testString))
//    }
