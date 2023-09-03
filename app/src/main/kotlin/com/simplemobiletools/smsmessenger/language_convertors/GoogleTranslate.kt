package com.simplemobiletools.smsmessenger.language_convertors

import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions

public class GoogleTranslate {
    companion object {
        public fun translate(originalText: String): String {
            return try {
                val token = "AIzaSyAubu13-vn3Ju6W0gh5tCwy66-CKuWprcI"
                val translate = TranslateOptions.newBuilder().setApiKey(token).build().service
                val translation = translate.translate(originalText, Translate.TranslateOption.sourceLanguage("ka"), Translate.TranslateOption.targetLanguage("ru"))
                translation.translatedText
            } catch (e: Exception) {
                e.printStackTrace()
                originalText
            }
        }
    }
}
