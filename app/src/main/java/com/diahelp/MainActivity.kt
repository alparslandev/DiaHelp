package com.diahelp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.diahelp.ui.DayView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refreshSumofCarbs(0)
        dv_date.onClickListener(object : DayView.Listener {
            override fun onClickBackward(date: Calendar) {
                refreshSumofCarbs(90)
            }

            override fun onClickForward(date: Calendar) {

            }})
    }
    fun refreshSumofCarbs(sum : Int) {
        var text = resources.getString(R.string.no_carb)
        if (sum > 0) text = String.format(resources.getString(R.string.carb_quantity), sum.toString())
        tv_summary.text = text
    }
}