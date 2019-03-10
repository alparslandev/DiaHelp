package com.diahelp.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.diahelp.R
import kotlinx.android.synthetic.main.day_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class DayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                        defStyleAttr: Int = 0):  LinearLayout(context, attrs, defStyleAttr) {

    var date = Calendar.getInstance()
    val sdf = SimpleDateFormat("dd MMM EEE", Locale.getDefault())

    init {
        LayoutInflater.from(context).inflate(R.layout.day_view, this, true)
        orientation = HORIZONTAL

        txt_date.gravity = Gravity.CENTER
        img_button_forward.isEnabled = false

        txt_date.text = sdf.format(Date(date.timeInMillis))

        img_button_backward.setOnClickListener {
            if (!img_button_forward.isEnabled) img_button_forward.isEnabled = true

        }

        img_button_forward.setOnClickListener {

        }
    }

    fun getDateStr() : String {
        return txt_date.toString()
    }

    /*fun getDate() : Calendar {

    }*/
}