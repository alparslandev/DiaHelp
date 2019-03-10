package com.diahelp.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.diahelp.R
import kotlinx.android.synthetic.main.view_meal_time.view.*

class MealTimeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                             defStyleAttr: Int = 0):  LinearLayout(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_meal_time, this, true)
        orientation = VERTICAL
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.MealTimeView, 0, 0)
        txt_meal.text = a.getString(R.styleable.MealTimeView_text)
        img_button_meal_icon.setImageResource(a.getResourceId(R.styleable.MealTimeView_icon, R.drawable.ic_meal_dining))
        isClickable = true
        isFocusable = true
        a.recycle()
    }
}