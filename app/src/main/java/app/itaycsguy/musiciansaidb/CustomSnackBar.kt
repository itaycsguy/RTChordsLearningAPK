package app.itaycsguy.musiciansaidb

import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView

class CustomSnackBar {
    companion object {
        private val color = Color.parseColor("#064fba") // -> (R=6,G=79,B=186)
        private val duration = Snackbar.LENGTH_LONG
        private val gravity = Gravity.BOTTOM

        fun make(_act : AppCompatActivity,text : CharSequence,color : Int = CustomSnackBar.color,duration : Int = CustomSnackBar.duration,gravity: Int = CustomSnackBar.gravity) {
            val mSnackBar = Snackbar.make(_act.window.decorView.findViewById(android.R.id.content), text,duration)
            val view = mSnackBar.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = gravity
            view.layoutParams = params
            view.setBackgroundColor(color)
            val mainTextView = view.findViewById(android.support.design.R.id.snackbar_text) as TextView
            mainTextView.setTextColor(Color.WHITE)
            mSnackBar.show()
        }
    }
}