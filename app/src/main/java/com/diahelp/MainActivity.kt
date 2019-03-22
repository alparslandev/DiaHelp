package com.diahelp

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.diahelp.addfood.AddFoodActivity
import com.diahelp.base.BaseActivity
import com.diahelp.model.Const
import com.diahelp.model.Foods
import com.diahelp.tools.Number
import com.diahelp.ui.DayView
import com.diahelp.ui.MealTimeView
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity() {

    companion object {
        const val EXTRA_REPAST = "EXTRA_TYPE"
        const val EXTRA_CHOSEN_DATE = "EXTRA_CHOSEN_DATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refreshRepastsByDate()
        dv_date.onClickListener(object : DayView.Listener {
            override fun onClickBackward(date: Calendar) {
                refreshRepastsByDate()
            }

            override fun onClickForward(date: Calendar) {
                refreshRepastsByDate()
            }
        })

        // todo make this a recyclerView
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

    override fun onRestart() {
        super.onRestart()
        refreshRepastsByDate()
    }

    private fun clearRepasts() {
        mv_snakes_breakfast.refresh()
        mv_snakes_dining.refresh()
        mv_snakes_lunch.refresh()
        mv_before_bed.refresh()
        mv_breakfast.refresh()
        mv_dining.refresh()
        mv_lunch.refresh()
        mv_night.refresh()
        mv_other.refresh()
    }

    private fun refreshRepastsByDate() {
        clearRepasts()
        var totalCarbsOfDay = 0.0
        var carbsOfRepast: Double
        for (repast in resources.getStringArray(R.array.repasts)) {
            val model = mRealm.where<Foods>()
                .equalTo(Const.FOOD_DATE, dv_date.getDateStr())
                .equalTo(Const.REPAST, repast)
                .findFirst() ?: continue
            carbsOfRepast = model.totalCarbsOfRepast!!
            totalCarbsOfDay += carbsOfRepast
            if (mv_snakes_breakfast.getText() == repast && carbsOfRepast > 0.0) {
                mv_snakes_breakfast.setTotalCarb(carbsOfRepast)
            }

            if (mv_snakes_dining.getText() == repast && carbsOfRepast > 0.0) {
                mv_snakes_dining.setTotalCarb(carbsOfRepast)
            }

            if (mv_snakes_lunch.getText() == repast && carbsOfRepast > 0.0) {
                mv_snakes_lunch.setTotalCarb(carbsOfRepast)
            }

            if (mv_before_bed.getText() == repast && carbsOfRepast != 0.0) {
                mv_before_bed.setTotalCarb(carbsOfRepast)
            }

            if (mv_breakfast.getText() == repast && carbsOfRepast != 0.0) {
                mv_breakfast.setTotalCarb(carbsOfRepast)
        }

            if (mv_dining.getText() == repast && carbsOfRepast != 0.0) {
                mv_dining.setTotalCarb(carbsOfRepast)
            }

            if (mv_lunch.getText() == repast && carbsOfRepast != 0.0) {
                mv_lunch.setTotalCarb(carbsOfRepast)
            }

            if (mv_night.getText() == repast && carbsOfRepast != 0.0) {
                mv_night.setTotalCarb(carbsOfRepast)
            }

            if (mv_other.getText() == repast && carbsOfRepast != 0.0) {
                mv_other.setTotalCarb(carbsOfRepast)
            }
        }
        refreshSumofCarbs(totalCarbsOfDay)
    }

    private fun redirectToAddFoods(view : View?) {
        val text = (view as MealTimeView).getText()
        val intent = Intent(this, AddFoodActivity::class.java)
        intent.putExtra(EXTRA_REPAST, text)
        intent.putExtra(EXTRA_CHOSEN_DATE, dv_date.getDateStr())
        startActivity(intent)
    }

    private fun refreshSumofCarbs(sum : Double) {
        var text = resources.getString(R.string.no_carb)
        if (sum > 0) text = String.format(resources.getString(R.string.carb_quantity), Number.format(sum))
        tv_summary.text = text
    }
}