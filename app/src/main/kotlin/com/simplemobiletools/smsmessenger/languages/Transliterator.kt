package com.simplemobiletools.smsmessenger.languages

class Transliterator {

    private val specialChars = listOf('%', '#', '^', '&', '*', '~', '_', ':', '@', '$', '+', '=')

    private fun generateRandomString(length: Int): String = (1..length).map { specialChars.random() }.joinToString("")

    fun transliterate(input: String, translitMap: Map<String, String>): String {
        val placeholder = generateRandomString(10)

        val urlPattern = """(https?://\S+|www\.\S+|\S+\.\S+)""".toRegex()
        val urls = mutableListOf<String>()

        // Replace URLs with placeholders
        val placeholderText = urlPattern.replace(input) { matchResult ->
            urls.add(matchResult.value)
            "${placeholder}${urls.size - 1}"
        }

        // Perform the transliteration
        var result = placeholderText
        for (entry in translitMap) {
            result = result.replace(entry.key, entry.value)
        }

        // Replace placeholders with original URLs
        urls.forEachIndexed { index, url ->
            result = result.replace("${placeholder}$index", url)
        }

        return result
    }
}
