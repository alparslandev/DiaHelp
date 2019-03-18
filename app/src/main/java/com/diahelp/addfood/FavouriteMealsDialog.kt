package com.diahelp.addfood

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Message
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.diahelp.R
import com.diahelp.model.FavouriteMeals
import com.diahelp.tools.Number
import kotlinx.android.synthetic.main.dialog_fav_meals.*

class FavouriteMealsDialog(context: Context, addHandler: AddFoodActivity.FavMealsHandler,
                           mealList: List<FavouriteMeals>) : Dialog(context) {
    init {
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_fav_meals)
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btn_dialog_fav_cancel.setOnClickListener { dismiss() }

        val ary = arrayOfNulls<String>(mealList.size)
        for (i in mealList.indices) {
            val model = mealList[i]
            ary[i] = context.getString(R.string.favourite_meal_info, model.MealName,
                Number.format(model.Quantity), model.Unit, Number.format(model.CarbsInMeal))
        }

        dialog_list_fav.adapter = ArrayAdapter<String>(context, R.layout.item_fav_dialog, ary)
        dialog_list_fav.setOnItemClickListener { parent,_, position,_-> run {
                val msj = Message()
                msj.obj = mealList[position]
                addHandler.sendMessage(msj)
                dismiss()
            }
        }
    }
}