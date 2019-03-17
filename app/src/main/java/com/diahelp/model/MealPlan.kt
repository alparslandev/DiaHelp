package com.diahelp.model

data class MealPlan (
    @JvmField var MealName: String = "",
    @JvmField var CarbsInMeal: Double = 0.0,
    @JvmField var Quantity: Double = 0.0,
    @JvmField var Unit: String = "",
    @JvmField var Repast: String = "", // Yemek yenilen ana ekrandan seçilen Öğün
    @JvmField var isFavourite: Boolean = false
) : Identity(0)