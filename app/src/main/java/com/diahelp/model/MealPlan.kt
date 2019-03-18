package com.diahelp.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MealPlan (
    @PrimaryKey
    open var Id : Int = 0,
    open var MealName: String = "",
    open var CarbsInMeal: Double = 0.0,
    open var Quantity: Double = 0.0,
    open var Unit: String = "",
    open var Repast: String = "", // Yemek yenilen ana ekrandan seçilen Öğün
    open var isFavourite: Boolean = false
) : RealmObject()