package com.diahelp.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Foods : RealmObject() {
    @PrimaryKey
    var Id: Int = 0
    var Repast: String? = null
    var foodDate: String? = null
    var totalCarbsOfRepast: Double? = null
    var mealPlans: RealmList<MealPlan>? = null
}