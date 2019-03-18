package com.diahelp.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class FavouriteMeals(
    @PrimaryKey
    open var Id : Int = 0,
    open var MealName : String = "",
    open var CarbsInMeal: Double = 0.0,
    open var Quantity: Double = 0.0,
    open var Unit: String = ""
) : RealmObject()