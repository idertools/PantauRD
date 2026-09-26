package com.example

import com.example.security.CryptoEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun test_crypto_engine_encryption_and_decryption() {
        val originalPayload = """{"lat":-6.2088,"lon":106.8456,"battery":85}"""
        val envelope = CryptoEngine.encryptPayload(originalPayload)

        assertNotNull(envelope.ciphertext)
        assertNotNull(envelope.iv)
        assertNotEquals(originalPayload, envelope.ciphertext)

        val decrypted = CryptoEngine.decryptPayload(envelope)
        assertEquals(originalPayload, decrypted)
    }
}
