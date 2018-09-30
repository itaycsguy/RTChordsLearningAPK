package app.itaycsguy.musiciansaidb

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.*


fun getImagePathFromInputStreamUri(context : Context, uri: Uri): String? {
    var inputStream: InputStream? = null
    var filePath = ""

    if (uri.authority != null) {
        try {
            inputStream = context.contentResolver.openInputStream(uri) // context needed
            val photoFile = createTemporalFileFrom(inputStream, context)

            filePath = photoFile!!.path

        } catch (e: FileNotFoundException) {
            // log
        } catch (e: IOException) {
            // log
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    return filePath
}

@Throws(IOException::class)
private fun createTemporalFileFrom(inputStream: InputStream?, context: Context): File? {
    var targetFile: File? = null

    if (inputStream != null) {
        var read: Int
        val buffer = ByteArray(8 * 1024)

        targetFile = createTemporalFile(context)
        val outputStream = FileOutputStream(targetFile)

        read = inputStream.read(buffer)
        while (read != -1) {
            outputStream.write(buffer, 0, read)
            read = inputStream.read(buffer)
        }
        outputStream.flush()

        try {
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    return targetFile
}

private fun createTemporalFile(context : Context): File {
    return File(Environment.getExternalStorageDirectory(),
    "chord_${System.currentTimeMillis()}.jpg")
//    return File(context.externalCacheDir, "tempFile.jpg") // context needed
}

val PASSWORD_LENGTH = 8

fun isValidPassword(password: String):Boolean{
    return password.length >= PASSWORD_LENGTH
}

fun isCorrectEmailFormat(email: String): Boolean {
    val pattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
    val match : MatchResult? = pattern.toRegex(setOf(RegexOption.IGNORE_CASE,RegexOption.DOT_MATCHES_ALL)).find(email)
    match?.let { return true }
    return false
}