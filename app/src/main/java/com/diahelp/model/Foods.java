package com.diahelp.model;

import com.diahelp.tools.StringRealm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by AlparslanSelçuk on 22.11.2016.
 */

public class Foods extends RealmObject {
    @PrimaryKey
    public int Id;
    public String Repast;
    public String foodDate;
    public Double totalCarbsOfRepast;
    public RealmList<StringRealm> mealIds;
}