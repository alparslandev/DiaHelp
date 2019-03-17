package com.diahelp.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

public open class FavouriteMeals(
    @PrimaryKey
    public open var Id : Int = 0,
    public open var MealName : String = "",
    public open var CarbsInMeal: Double = 0.0,
    public open var Quantity: Double = 0.0,
    public open var Unit: String = ""
) : RealmObject()