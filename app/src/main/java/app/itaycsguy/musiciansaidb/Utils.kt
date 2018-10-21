package app.itaycsguy.musiciansaidb

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Environment
import android.support.v4.content.ContextCompat.getSystemService
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import java.io.*
import java.lang.Exception
import java.util.regex.Pattern


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

val PASSWORD_LENGTH = 6

fun isValidPassword(password: String):Boolean{
    return password.length >= PASSWORD_LENGTH
}

fun isCorrectEmailFormat(email: String): Boolean {
    return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
    ).matcher(email).matches()
}

fun hideKeyboard(act : Activity) {
    val imm = act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(act.currentFocus.windowToken, 0)
}

fun startProgressBar(act : Activity,pBar : Int) : ProgressBar {
    val progressBar : ProgressBar = act.findViewById(pBar)
    progressBar.indeterminateDrawable.setColorFilter(Color.DKGRAY, android.graphics.PorterDuff.Mode.MULTIPLY)
    progressBar.visibility = View.VISIBLE  //To show ProgressBar
    return progressBar
}

fun stopProgressBar(progressBar: ProgressBar){
    progressBar.visibility = View.INVISIBLE  //To show ProgressBar
}

fun isNetworkAvailable(act : Activity): Boolean {
    val connectivityManager = act.getSystemService(Context.CONNECTIVITY_SERVICE)
    return if (connectivityManager is ConnectivityManager) {
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        networkInfo?.isConnected ?: false
    } else false
}
