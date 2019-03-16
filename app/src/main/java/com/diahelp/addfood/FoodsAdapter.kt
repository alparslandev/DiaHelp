package com.diahelp.addfood

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.diahelp.R
import com.diahelp.model.MealPlan
import com.diahelp.tools.Number
import com.diahelp.tools.inflate
import kotlinx.android.synthetic.main.item_meal_plan.view.*

class FoodsAdapter(private val mealList: List<MealPlan>, private val listener : FoodClickListener) :
    RecyclerView.Adapter<FoodsAdapter.FoodViewHolder>() {

    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        context = parent.context
        val itemView = parent.inflate(R.layout.item_meal_plan, false)
        return FoodViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val pos = holder.adapterPosition
        val model = mealList[pos]
        holder.itemView.item_meal_txt_food_name.text = mealList[pos].MealName
        holder.itemView.item_meal_txt_food_carbs.text = String.format(
            context.getString(R.string.carb_of_meal), Number.format(model.CarbsInMeal)
        )
        holder.itemView.item_meal_txt_quantity.text = String.format(
            context.getString(R.string.quantity_unit), Number.format(model.Quantity), model.Unit
        )

        holder.itemView.btn_delete_item_from_meal.setOnClickListener {
            listener.onDeleteClickListener(model)
        }

        refreshFavColorFilter(model, holder)

        holder.itemView.btn_add_favourites.setOnClickListener {
            if (!model.isFavourite) {
                listener.onDeleteClickListener(model)
            } else {
                listener.onAddFavClickListener(model)
            }
            listener.refreshFavButton()
        }
    }

    private fun refreshFavColorFilter(model : MealPlan, holder: FoodViewHolder) {
        holder.itemView.btn_add_favourites.setColorFilter(
            ContextCompat.getColor(context, if (model.isFavourite) R.color.color_fav else R.color.colorBackground)
        )
    }

    override fun getItemCount() = mealList.size
    inner class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view)
    interface FoodClickListener {
        fun refreshFavButton()
        fun onDeleteClickListener(mealPlan : MealPlan)
        fun onAddFavClickListener(mealPlan : MealPlan)
        fun onRemoveFavClickListener(mealPlan : MealPlan)
    }
}