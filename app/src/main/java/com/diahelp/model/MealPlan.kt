package com.diahelp.model

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey

open class MealPlan : RealmObject() {
    @PrimaryKey
    var Id : Int = 0
    var MealName: String = ""
    var CarbsInMeal: Double = 0.0
    var Quantity: Double = 0.0
    var Unit: String = ""
    var isFavourite: Boolean = false
    @LinkingObjects("mealPlans")
    val owners: RealmResults<Foods>? = null
}