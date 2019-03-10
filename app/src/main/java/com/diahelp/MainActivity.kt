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

        dv_date.onClickListener(object : DayView.Listener {
            override fun onClickBackward(date: Calendar) {

            }

            override fun onClickForward(date: Calendar) {

            }})
    }
}
