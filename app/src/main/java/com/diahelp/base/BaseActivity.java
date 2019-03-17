package com.diahelp.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.diahelp.R;
import com.diahelp.model.FavouriteMeals;
import com.diahelp.ui.toast.EmptyValue;
import com.diahelp.ui.toast.SuccessToast;
import io.realm.Realm;

public abstract class BaseActivity extends AppCompatActivity {
    Context context;
    private InputMethodManager imm;
    public Realm mRealm;
    private int nextId;

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

    public int getMaxID(final String name) {
        // TODO make this IntDef and make ID a constant
        Number currentIdNum = null;
        switch (name) {
            /*case "food":
                currentIdNum = mRealm.where(Foods.class).max("Id");
                break;*/
            case "meal":
                //currentIdNum = mRealm.where(MealPlan.class).max("Id");
                break;
            /*case "bloodGlucose":
                currentIdNum = mRealm.where(BloodGlucose.class).max("Id");
                break;*/
            case "fav":
                currentIdNum = mRealm.where(FavouriteMeals.class).max("Id");
                break;
        }

        if (currentIdNum == null)
            nextId = 1;
        else {
            nextId = currentIdNum.intValue() + 1;
        }

        return nextId;
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
