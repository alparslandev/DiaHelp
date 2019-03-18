package com.diahelp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.diahelp.addfood.AddFoodActivity
import com.diahelp.ui.DayView
import com.diahelp.ui.MealTimeView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_REPAST = "EXTRA_TYPE"
        const val EXTRA_CHOSEN_DATE = "EXTRA_CHOSEN_DATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refreshSumofCarbs(0)
        dv_date.onClickListener(object : DayView.Listener {
            override fun onClickBackward(date: Calendar) {
                // TODO Realm query by Date and Repast
                refreshSumofCarbs(90)
            }

            override fun onClickForward(date: Calendar) {
                // TODO Realm query by Date and Repast
            }
        })

        mv_snakes_breakfast.setOnClickListener { v: View? -> redirectToAddFoods(v) }
        mv_snakes_dining.setOnClickListener { v: View? -> redirectToAddFoods(v) }
        mv_snakes_lunch.setOnClickListener { v: View? -> redirectToAddFoods(v) }
        mv_before_bed.setOnClickListener { v: View? -> redirectToAddFoods(v) }
        mv_breakfast.setOnClickListener { v: View? -> redirectToAddFoods(v) }
        mv_dining.setOnClickListener { v: View? -> redirectToAddFoods(v) }
        mv_lunch.setOnClickListener { v: View? -> redirectToAddFoods(v) }
        mv_night.setOnClickListener { v: View? -> redirectToAddFoods(v) }
        mv_other.setOnClickListener { v: View? -> redirectToAddFoods(v) }


    }

    fun redirectToAddFoods(view : View?) {
        val text = (view as MealTimeView).getText()
        val intent = Intent(this, AddFoodActivity::class.java)
        intent.putExtra(EXTRA_REPAST, text)
        intent.putExtra(EXTRA_CHOSEN_DATE, dv_date.getDateStr())
        startActivity(intent)
    }

    fun refreshSumofCarbs(sum : Int) {
        var text = resources.getString(R.string.no_carb)
        if (sum > 0) text = String.format(resources.getString(R.string.carb_quantity), sum.toString())
        tv_summary.text = text
    }
}