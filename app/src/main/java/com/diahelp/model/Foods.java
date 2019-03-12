package com.diahelp.model;

import com.diahelp.tools.CustomDateManager;
import com.diahelp.tools.StringRealm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.Date;

/**
 * Created by AlparslanSelçuk on 22.11.2016.
 */

public class Foods extends RealmObject {
    @PrimaryKey
    public int Id;
    public String Meal;
    private Date foodDate;
    public RealmList<StringRealm> mealIds;

    public Foods() {
        foodDate = CustomDateManager.getTimeOfDay();
    }

    public Date getFoodDate() {
        return foodDate;
    }
}