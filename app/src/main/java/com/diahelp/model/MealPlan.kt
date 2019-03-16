package com.diahelp.model

data class MealPlan (
    @JvmField var Id: Int = 0,
    @JvmField var MealName: String = "",
    @JvmField var CarbsInMeal: Double = 0.0,
    @JvmField var Quantity: Double = 0.0,
    @JvmField var Unit: String = "",
    @JvmField var Meal: String = "",
    @JvmField var isFavourite: Boolean = false
)