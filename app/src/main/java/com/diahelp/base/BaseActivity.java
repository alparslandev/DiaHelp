package com.diahelp.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.diahelp.R;
import com.diahelp.model.FavouriteMeals;
import com.diahelp.model.FieldNames;
import com.diahelp.model.Foods;
import com.diahelp.model.MealPlan;
import com.diahelp.ui.toast.EmptyValue;
import com.diahelp.ui.toast.SuccessToast;
import io.realm.Realm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class BaseActivity extends AppCompatActivity {
    Context context;
    private InputMethodManager imm;
    public Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mRealm = Realm.getDefaultInstance();
    }

    //Klavyeyi Tüm activity lerden ulaşarak kapatabilmek için.
    public void closeSoftKeyboard(View view) {
        try {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            showEmptyValueToast(getString(R.string.unexpected_error));
        }
    }

    @StringDef({TableType.FAV, TableType.FOOD, TableType.MEAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TableType {
        String FOOD = "food";
        String MEAL = "meal";
        String FAV = "fav";
    }

    public int getMaxID(@TableType String name) {
        Number currentIdNum = 1;
        switch (name) {
            case TableType.FOOD:
                currentIdNum = mRealm.where(Foods.class).max(FieldNames.ID);
                break;
            case TableType.MEAL:
                currentIdNum = mRealm.where(MealPlan.class).max(FieldNames.ID);
                break;
            /*case "bloodGlucose":
                currentIdNum = mRealm.where(BloodGlucose.class).max(FieldNames.Companion.getID());
                break;*/
            case TableType.FAV:
                currentIdNum = mRealm.where(FavouriteMeals.class).max(FieldNames.ID);
                break;
        }
        return currentIdNum == null ? 1 : (currentIdNum.intValue() + 1);
    }

    public void showEmptyValueToast(String text) {
        EmptyValue value = new EmptyValue(this);
        value.showToast(text);
    }

    public void showSuccessToast(String text) {
        SuccessToast value = new SuccessToast(this);
        value.showToast(text);
    }
}
