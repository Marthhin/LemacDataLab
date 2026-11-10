package com.example.lemacdatalab.datos

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class AlmacenSeguro(context: Context) {

    private val preferencias =
        context.getSharedPreferences("almacen_seguro_lemac", Context.MODE_PRIVATE)

    private val almacenClaves = KeyStore.getInstance(PROVEEDOR).apply { load(null) }

    private fun clave(): SecretKey {
        (almacenClaves.getKey(ALIAS_CLAVE, null) as? SecretKey)?.let { return it }

        val generador = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVEEDOR)
        generador.init(
            KeyGenParameterSpec.Builder(
                ALIAS_CLAVE,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generador.generateKey()
    }

    fun guardar(nombre: String, valor: String) {
        val cifrador = Cipher.getInstance(TRANSFORMACION)
        cifrador.init(Cipher.ENCRYPT_MODE, clave())
        val cifrado = cifrador.doFinal(valor.toByteArray(Charsets.UTF_8))
        val paquete = cifrador.iv + cifrado
        preferencias.edit().putString(nombre, Base64.encodeToString(paquete, Base64.NO_WRAP)).apply()
    }

    fun leer(nombre: String): String? {
        val guardado = preferencias.getString(nombre, null) ?: return null
        return try {
            val paquete = Base64.decode(guardado, Base64.NO_WRAP)
            val iv = paquete.copyOfRange(0, LARGO_IV)
            val cifrado = paquete.copyOfRange(LARGO_IV, paquete.size)
            val cifrador = Cipher.getInstance(TRANSFORMACION)
            cifrador.init(Cipher.DECRYPT_MODE, clave(), GCMParameterSpec(BITS_ETIQUETA, iv))
            String(cifrador.doFinal(cifrado), Charsets.UTF_8)
        } catch (error: Exception) {
            Log.e("AlmacenSeguro", "No se pudo descifrar $nombre", error)
            null
        }
    }

    fun borrar(vararg nombres: String) {
        preferencias.edit().apply { nombres.forEach { remove(it) } }.apply()
    }

    private companion object {
        const val PROVEEDOR = "AndroidKeyStore"
        const val ALIAS_CLAVE = "lemac_clave_principal"
        const val TRANSFORMACION = "AES/GCM/NoPadding"
        const val LARGO_IV = 12
        const val BITS_ETIQUETA = 128
    }
}
