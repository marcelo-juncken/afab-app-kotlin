package feature_auth.data.local.platform

import com.sun.jna.platform.win32.Crypt32
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.Win32Exception
import com.sun.jna.platform.win32.WinCrypt
import com.sun.jna.Pointer
import com.sun.jna.Memory
import feature_auth.data.local.DataProtection
import java.nio.charset.StandardCharsets
import java.util.*

class WindowsDataProtection  : DataProtection {

    private val CRYPTPROTECT_UI_FORBIDDEN = 0x01

    override fun protectData(data: String): String {
        val dataBytes = data.toByteArray(StandardCharsets.UTF_8)
        val pBlob = createDataBlob(dataBytes)

        val pBlobOut = WinCrypt.DATA_BLOB()

        if (!Crypt32.INSTANCE.CryptProtectData(
                pBlob,
                null,
                null,
                Pointer.NULL,
                null,
                CRYPTPROTECT_UI_FORBIDDEN,
                pBlobOut
            )
        ) {
            throw Win32Exception(Kernel32.INSTANCE.GetLastError())
        }

        val protectedData = pBlobOut.toByteArray()
        Kernel32.INSTANCE.LocalFree(pBlobOut.pbData)

        return Base64.getEncoder().encodeToString(protectedData)
    }

    override fun unprotectData(protectedData: String): String {
        val protectedDataBytes = Base64.getDecoder().decode(protectedData)
        val pBlob = createDataBlob(protectedDataBytes)

        val pBlobOut = WinCrypt.DATA_BLOB()

        if (!Crypt32.INSTANCE.CryptUnprotectData(
                pBlob,
                null,
                null,
                Pointer.NULL,
                null,
                CRYPTPROTECT_UI_FORBIDDEN,
                pBlobOut
            )
        ) {
            return ""
        }

        val dataBytes = pBlobOut.toByteArray()
        Kernel32.INSTANCE.LocalFree(pBlobOut.pbData)

        return String(dataBytes, StandardCharsets.UTF_8)
    }

    private fun createDataBlob(dataBytes: ByteArray): WinCrypt.DATA_BLOB {
        val pBlob = WinCrypt.DATA_BLOB()
        pBlob.cbData = dataBytes.size
        pBlob.pbData = Memory(dataBytes.size.toLong()).apply {
            write(0, dataBytes, 0, dataBytes.size)
        }
        return pBlob
    }

    private fun WinCrypt.DATA_BLOB.toByteArray(): ByteArray {
        val bytes = ByteArray(cbData)
        pbData.read(0, bytes, 0, cbData)
        return bytes
    }
}
