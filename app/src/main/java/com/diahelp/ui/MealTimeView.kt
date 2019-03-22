package com.diahelp.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.diahelp.R
import com.diahelp.tools.Number
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
        img_button_meal_icon.setOnClickListener { callOnClick() }
        /*val outValue = TypedValue()
        getContext().theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        setBackgroundResource(outValue.resourceId)*/
        a.recycle()
    }

    fun setTotalCarb(text : Double) {
        img_button_meal_icon.visibility = View.GONE
        tv_repast_total_carb.visibility = View.VISIBLE
        tv_repast_total_carb.text = String.format(context.getString(R.string.gram), Number.format(text))
    }

    fun getText() : String {
        return txt_meal.text.toString()
    }

    fun refresh() {
        img_button_meal_icon.visibility = View.VISIBLE
        tv_repast_total_carb.visibility = View.GONE
        tv_repast_total_carb.text = ""
    }
}