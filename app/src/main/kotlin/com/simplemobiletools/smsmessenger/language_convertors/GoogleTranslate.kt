import android.content.Context
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class GoogleTranslate(private val context: Context) {
    companion object {
        private const val KEY_ALIAS = "GoogleTranslateKey"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val AES_KEY_SIZE = 256
        private const val GCM_TAG_SIZE = 128
    }

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE)

    init {
        keyStore.load(null)
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateSecretKey()
        }
    }

    fun translate(originalText: String): String {
        return try {
            val apiKey = retrieveApiKey()
            val translate = TranslateOptions.newBuilder().setApiKey(apiKey).build().service
            val translation = translate.translate(
                originalText,
                Translate.TranslateOption.sourceLanguage("ka"),
                Translate.TranslateOption.targetLanguage("ru")
            )
            translation.translatedText
        } catch (e: Exception) {
            e.printStackTrace()
            originalText
        }
    }

    fun saveApiKey(apiKey: String) {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val encryptedData = cipher.doFinal(apiKey.toByteArray(Charset.forName("UTF-8")))
        val sharedPrefs = context.getSharedPreferences(KEY_ALIAS, Context.MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putString("encryptedData", android.util.Base64.encodeToString(encryptedData, android.util.Base64.DEFAULT))
            putString("iv", android.util.Base64.encodeToString(iv, android.util.Base64.DEFAULT))
        }.apply()
    }

    private fun retrieveApiKey(): String {
        val sharedPrefs = context.getSharedPreferences(KEY_ALIAS, Context.MODE_PRIVATE)
        val encryptedData = sharedPrefs.getString("encryptedData", null)
        val iv = sharedPrefs.getString("iv", null)
        if (encryptedData != null && iv != null) {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), GCMParameterSpec(GCM_TAG_SIZE, android.util.Base64.decode(iv, android.util.Base64.DEFAULT)))
            val decryptedData = cipher.doFinal(android.util.Base64.decode(encryptedData, android.util.Base64.DEFAULT))
            return String(decryptedData, Charset.forName("UTF-8"))
        }
        return ""
    }

    private fun generateSecretKey() {
        val keyGenerator = KeyGenerator.getInstance("AES", ANDROID_KEYSTORE)
        keyGenerator.init(AES_KEY_SIZE)
        keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey {
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }
}
