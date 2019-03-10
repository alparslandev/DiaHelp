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

    private var date = Calendar.getInstance()
    private val sdf = SimpleDateFormat("dd MMM EEE", Locale.getDefault())
    private lateinit var onClickListener : Listener

    init {
        LayoutInflater.from(context).inflate(R.layout.day_view, this, true)
        orientation = HORIZONTAL

        txt_date.gravity = Gravity.CENTER
        img_button_forward.isEnabled = false
        refreshDate()

        img_button_backward.setOnClickListener {
            date.add(Calendar.DAY_OF_YEAR, -1)
            if (!img_button_forward.isEnabled) img_button_forward.isEnabled = true
            refreshDate()
            onClickListener.onClickBackward(date)
        }

        img_button_forward.setOnClickListener {
            date.add(Calendar.DAY_OF_YEAR, 1)
            if (isToday()) img_button_forward.isEnabled = false
            refreshDate()
            onClickListener.onClickForward(date)
        }
    }

    fun isToday () : Boolean {
        val today = Calendar.getInstance()
        return (date.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                && date.get(Calendar.YEAR) == today.get(Calendar.YEAR))
    }

    private fun refreshDate() {
        txt_date.text = sdf.format(Date(date.timeInMillis))
    }

    fun getDateStr() : String {
        return txt_date.toString()
    }

    fun getDateCalendar() : Calendar {
        return date
    }

    fun onClickListener(listener : Listener) {
        onClickListener = listener
    }

    interface Listener {
        fun onClickForward(date : Calendar)
        fun onClickBackward(date : Calendar)
    }
}