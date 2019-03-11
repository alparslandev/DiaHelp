package com.diahelp.addfood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.diahelp.R;
import com.diahelp.base.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;

public class AddFoodActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private ArrayList<String> fooddb_ArrayList = new ArrayList<>(); // bu arraylist yiyecek listesi için
    private ArrayList<String> unit_ArrayList = new ArrayList<>();
    private ArrayAdapter<String> FoodDBArrayAdapter;
    private String carb_value, choosenFood = "", mealName = null;
    private double totalCarbs = 0.0;

    private AutoCompleteTextView edtChooseFood;
    private EditText edtQuantity;
    private Spinner selectUnit;
    private RealmList<MealPlan> mealList;

    private RecyclerViewAdapter rvAdapter;
    private ArrayAdapter unitListAdapter;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        initializeUiComponents();
        init_Firebase();
        RecyclerView recyclerViewMealPlan = (RecyclerView) findViewById(R.id.rv_meals);
        recyclerViewMealPlan.setLayoutManager(new LinearLayoutManager(AddFoodActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerViewMealPlan.setAdapter(rvAdapter);
        recyclerViewMealPlan.setNestedScrollingEnabled(false);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.bg_line_divider));
        recyclerViewMealPlan.addItemDecoration(dividerItemDecoration);
    }

    private void updateMealList(MealPlan mealPlan) {
        mealList.add(0, mealPlan);
        totalCarbs += mealPlan.CarbsInMeal * mealPlan.Quantity;
        setTxtCarbCounter(View.VISIBLE);
        clearViewItems(false);
    }

    private void initializeUiComponents() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_add_food);
        setSupportActionBar(myToolbar);

        selectUnit = (Spinner) findViewById(R.id.spinner_unit);
        edtQuantity = (EditText) findViewById(R.id.edt_add_food_quantity);
        edtChooseFood = (AutoCompleteTextView) findViewById(R.id.edt_choose_food);

        mealList = new RealmList<>();
        rvAdapter = new RecyclerViewAdapter(mealList);
        unitListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, unit_ArrayList);
        watchUserTypingEvent(edtChooseFood);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("food_db");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String my_key = dataSnapshot.getKey();
                fooddb_ArrayList.add(my_key);
                FoodDBArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.styled_autocompletetextview, fooddb_ArrayList);
                edtChooseFood.setAdapter(FoodDBArrayAdapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        selectUnit.setEnabled(false);
        unitListAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
        selectUnit.setAdapter(unitListAdapter);

        setFavButtonEnable();
        edtChooseFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                choosenFood = adapterView.getItemAtPosition(i).toString();
                selectUnit.setEnabled(true);
                unit_ArrayList.clear();
                DatabaseReference choosenFoodRef = myRef.child(choosenFood);
                choosenFoodRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String unit = dataSnapshot.getKey();
                        if (!unit_ArrayList.contains(unit))
                            unit_ArrayList.add(unit);

                        edtQuantity.requestFocus();
                        selectUnit.setEnabled(unit_ArrayList.size() == 1 ? false : true);
                        unitListAdapter.notifyDataSetChanged();
                        setTxtInformer("Bir " + unit_ArrayList.get(0) + " " + choosenFood
                                + " " + carb_value + " gr karbonhidrat içerir.", View.VISIBLE);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        selectUnit.setOnItemSelectedListener(this);
    }

    private void setFavButtonEnable() {
        CustomIconButton button = (CustomIconButton) findViewById(R.id.btn_add_food_open_favs);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFavouriteList().size() > 0)
                    setFavDialog();
            }
        });
        button.setEnabled(getFavouriteList().size() != 0);
    }

    public void clearViewItems(boolean isChoosenIncluded) {
        if (edtQuantity != null)
            edtQuantity.setText("");
        if (!isChoosenIncluded) {
            edtChooseFood.setText("");
            closeSoftKeyboard(edtChooseFood);
        }
        setTxtInformer("Birim Seçiniz", View.GONE);
        unit_ArrayList.clear();
        unitListAdapter.notifyDataSetChanged();
        rvAdapter.notifyDataSetChanged();
    }

    public void setTxtCarbCounter(int Visibility) {
        ((FloatingActionButton) findViewById(R.id.btn_save_meal)).setVisibility(Visibility);

        ((TextView) findViewById(R.id.txt_total_carbs)).setVisibility(Visibility);
        ((TextView) findViewById(R.id.txt_total_carbs)).setText(Visibility == View.GONE ? "" :
                "Toplam Karbonhidrat: " + NumberFormatManager.getFormattedNumber(totalCarbs));
    }

    private void watchUserTypingEvent(final AutoCompleteTextView edt) {
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (count < 1)
                    clearViewItems(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void setTxtInformer(String text, int visibility) {
        ((TextView) findViewById(R.id.txt_informer)).setText(text);
        findViewById(R.id.pnl_select_unit).setVisibility(visibility);
        findViewById(R.id.pnl_quantity_add_food).setVisibility(visibility);
    }

    private List<FavouriteMeals> getFavouriteList() {
        List<FavouriteMeals> modelList = new ArrayList<>();
        RealmResults<FavouriteMeals> list = mRealm.where(FavouriteMeals.class).findAll();
        for (FavouriteMeals model : list) {
            modelList.add(model);
        }
        return modelList;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
        DatabaseReference choosenFoodRef = myRef.child(choosenFood);
        DatabaseReference choosenunitRef = choosenFoodRef.child(unit_ArrayList.get(position));
        choosenunitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                carb_value = dataSnapshot.getValue().toString();

                setTxtInformer("Bir " + unit_ArrayList.get(position) + " " + choosenFood
                        + " " + carb_value + " gr karbonhidrat içerir.", View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public class FavMealsHandler extends Handler {
        Context context;

        public FavMealsHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String id = (String) msg.obj;
            FavouriteMeals model = getFavouriteList().get(Integer.parseInt(id));
            MealPlan mealPlan = new MealPlan();
            mealPlan.Meal = mealName;
            mealPlan.CarbsInMeal = model.CarbsInMeal;
            mealPlan.MealName = model.MealName;
            mealPlan.Quantity = model.Quantity;
            mealPlan.Unit = model.Unit;
            mealPlan.Id = model.Id;
            updateMealList(mealPlan);
        }
    }

    private void setBloodGlucoseDialog() {
        BloodGlucoseDialog bloodGlucoseDialog = new BloodGlucoseDialog(this, new BloodGlucoseHandler(this));
        bloodGlucoseDialog.show();
        bloodGlucoseDialog.setCancelable(true);
        bloodGlucoseDialog.setCanceledOnTouchOutside(true);
    }

    public class BloodGlucoseHandler extends Handler {
        Context context;

        public BloodGlucoseHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // TODO: Kan şekeri değeri buraya gelmektedir. Buradan veri tabanına iletilmesi gerekmektedir.
            String bloodSugar = (String) msg.obj;
        }
    }

    private void setFavDialog() {
        FavouriteMealsDialog favMealsDialog = new FavouriteMealsDialog(this, new FavMealsHandler(this), getFavouriteList());
        favMealsDialog.show();
        favMealsDialog.setCancelable(true);
        favMealsDialog.setCanceledOnTouchOutside(true);
    }

    public void AddFoodOnClickListener(View view) {
        switch (view.getId()) {
            /*case R.id.btn_open_meal_type:
                mealTypeDialog();
                break;*/
            case R.id.btn_add_food_blood_glucose:
                setBloodGlucoseDialog();
                break;
            case R.id.btn_clear_all:
                clearViewItems(false);
                Iterator<MealPlan> it = mealList.iterator();
                while (it.hasNext()) {
                    // Silmeyiniz. meal objesi boşuna değildir. it.next çalışmaktadır.
                    MealPlan meal = it.next();
                    it.remove();
                }
                setTxtCarbCounter(View.GONE);
                totalCarbs = 0.0;
                break;
            case R.id.btn_save_meal:
                if (mealList != null && mealList.size() > 0 && mealName != null) {
                    mRealm = Realm.getDefaultInstance();
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Collections.reverse(mealList);
                            RealmList<StringRealm> mealFoodListToRealm = new RealmList<>();

                            for (int i = 0; i < mealList.size(); i++) {
                                int Id = getMaxID("meal");
                                MealPlan meal = realm.createObject(MealPlan.class, Id);
                                meal.Unit = mealList.get(i).Unit;
                                meal.Quantity = mealList.get(i).Quantity;
                                meal.MealName = mealList.get(i).MealName;
                                meal.CarbsInMeal = mealList.get(i).CarbsInMeal;
                                meal.Meal = mealList.get(i).Meal;
                                StringRealm stringRealm = realm.createObject(StringRealm.class);
                                stringRealm.mealId = String.valueOf(Id);
                                mealFoodListToRealm.add(stringRealm);
                            }
                            Foods food = realm.createObject(Foods.class, getMaxID("food"));
                            food.mealIds = mealFoodListToRealm;
                            food.Meal = mealName;
                        }
                    });
                    finish();
                    startActivity(new Intent(view.getContext(), MenuActivity.class));
                }
                break;
            case R.id.btn_add_to_meal_plan:
                if (!TextUtils.isEmpty(choosenFood)) {
                    if (fooddb_ArrayList.contains(edtChooseFood.getText().toString())) {
                        if (!TextUtils.isEmpty(edtQuantity.getText().toString())) {
                            MealPlan model = new MealPlan();
                            model.CarbsInMeal = Double.parseDouble(carb_value) * Double.parseDouble(edtQuantity.getText().toString());
                            model.MealName = choosenFood;
                            model.Unit = selectUnit.getSelectedItem().toString();
                            model.Id = getMaxID("meal");
                            model.Quantity = Double.parseDouble(edtQuantity.getText().toString());
                            model.Meal = mealName;
                            updateMealList(model);
                        } else {
                            showEmptyValueToast("Lütfen miktarı giriniz.");
                        }
                    } else {
                        showEmptyValueToast("Lütfen listeden yiyecek seçiniz.");
                    }
                } else {
                    showEmptyValueToast("Lütfen yiyecek seçiniz.");
                }
                break;
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
        private List<MealPlan> horizontalListadapter;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView txtQuantity, txtName, txtCarbs;
            private ImageButton btnDeleteFood, btnFav;

            public MyViewHolder(View view) {
                super(view);
                btnDeleteFood = (ImageButton) view.findViewById(R.id.btn_delete_item_from_meal);
                txtQuantity = (TextView) view.findViewById(R.id.item_meal_txt_quantity);
                txtCarbs = (TextView) view.findViewById(R.id.item_meal_txt_food_carbs);
                txtName = (TextView) view.findViewById(R.id.item_meal_txt_food_name);
                btnFav = (ImageButton) view.findViewById(R.id.btn_add_favourites);
            }
        }

        public RecyclerViewAdapter(List<MealPlan> horizontalList) {
            this.horizontalListadapter = horizontalList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_plan, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final MealPlan model = mealList.get(holder.getAdapterPosition());
            holder.txtName.setText(horizontalListadapter.get(holder.getAdapterPosition()).MealName);
            holder.txtCarbs.setText("Karbonhidrat " + NumberFormatManager.getFormattedNumber(model.CarbsInMeal));
            holder.txtQuantity.setText(NumberFormatManager.getFormattedNumber(horizontalListadapter.get(holder.getAdapterPosition()).Quantity) + " " +
                    horizontalListadapter.get(holder.getAdapterPosition()).Unit);
            holder.btnDeleteFood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mealList.size() > 1) {
                        totalCarbs -= horizontalListadapter.get(holder.getAdapterPosition()).CarbsInMeal;
                        setTxtCarbCounter(View.VISIBLE);
                    } else {
                        totalCarbs = 0.0;
                        setTxtCarbCounter(View.GONE);
                    }
                    mealList.remove(mealList.get(holder.getAdapterPosition()));
                    rvAdapter.notifyDataSetChanged();
                }
            });

            holder.btnFav.setColorFilter(isFavourite(mealList.get(holder.getAdapterPosition())) ?
                    ContextCompat.getColor(AddFoodActivity.this, R.color.color_fav) :
                    ContextCompat.getColor(AddFoodActivity.this, R.color.colorBackground));

            holder.btnFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isFavourite(model)) {
                        holder.btnFav.setColorFilter(ContextCompat.getColor(AddFoodActivity.this, R.color.color_fav));
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                FavouriteMeals fav = realm.createObject(FavouriteMeals.class, getMaxID("fav"));
                                fav.Unit = model.Unit;
                                fav.Quantity = model.Quantity;
                                fav.MealName = model.MealName;
                                fav.CarbsInMeal = model.CarbsInMeal;
                            }
                        });
                        showSuccessToast("Yiyecek Favorilere Eklendi");
                    } else {
                        final int id = getFavourite(model);
                        holder.btnFav.setColorFilter(ContextCompat.getColor(AddFoodActivity.this, R.color.colorBackground));
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                FavouriteMeals results = realm.where(FavouriteMeals.class).equalTo("Id", id).findFirst();
                                results.deleteFromRealm();
                            }
                        });
                        showSuccessToast("Yiyecek Favorilerden Çıkarıldı");
                    }
                    setFavButtonEnable();
                }
            });
        }

        private int getFavourite(MealPlan meal) {
            RealmResults<FavouriteMeals> realmFav = mRealm.where(FavouriteMeals.class)
                    .equalTo("MealName", meal.MealName)
                    .equalTo("CarbsInMeal", meal.CarbsInMeal)
                    .equalTo("Quantity", meal.Quantity)
                    .equalTo("Unit", meal.Unit)
                    .findAll();
            return realmFav.size() < 1 ? -1 : realmFav.get(0).Id;
        }

        private boolean isFavourite(MealPlan meal) {
            RealmResults<FavouriteMeals> realmFav = mRealm.where(FavouriteMeals.class)
                    .equalTo("MealName", meal.MealName)
                    .equalTo("CarbsInMeal", meal.CarbsInMeal)
                    .equalTo("Quantity", meal.Quantity)
                    .equalTo("Unit", meal.Unit)
                    .findAll();
            return realmFav.size() > 0;
        }

        @Override
        public int getItemCount() {
            return mealList.size();
        }
    }

    private void init_Firebase() {
    }
}