package com.diahelp.addfood

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.annotation.StringDef
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.diahelp.MainActivity
import com.diahelp.R
import com.diahelp.base.BaseActivity
import com.diahelp.model.*
import com.diahelp.tools.wrappers.ChildEventListenerWrapper
import com.diahelp.tools.wrappers.TextWatcherWrapper
import com.google.firebase.database.*
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_add_food.*
import com.diahelp.tools.Number
import com.diahelp.tools.StringRealm
import io.realm.Realm
import io.realm.kotlin.where
import java.util.*

class AddFoodActivity : BaseActivity(), FoodsAdapter.FoodClickListener {

    private var carb_value = ""
    private var choosenFood = ""
    private var totalCarbs = 0.0
    private val units = ArrayList<String>()
    private val foods = ArrayList<String>()
    private var mealList = RealmList<MealPlan>()
    private var rvAdapter = FoodsAdapter(mealList, this)
    private lateinit var foodDBAdapter: ArrayAdapter<String>
    private lateinit var unitListAdapter: ArrayAdapter<String>

    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    var myRef: DatabaseReference

    init {
        myRef = database.getReference(Const.DB_NAME)
        myRef.addChildEventListener(object : ChildEventListenerWrapper {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                foods.add(dataSnapshot.key.toString())
                edt_choose_food.setAdapter(foodDBAdapter)
            }
        })
    }

    private fun getExtras(type : String): String { // Önceki sayfada seçilen öğün
        return if (intent != null && intent.extras != null && intent.getStringExtra(type) != null)
            intent.getStringExtra(type) else ""
    }

    private val favouriteList: List<FavouriteMeals> get() { return mRealm.where<FavouriteMeals>().findAll() }

    fun initComponents() {
        foodDBAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, foods)
        unitListAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, units)
        spinner_unit.isEnabled = false
        spinner_unit.adapter = unitListAdapter

        setFavButtonEnable()
        edt_choose_food.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            choosenFood = adapterView.getItemAtPosition(i).toString()
            spinner_unit.isEnabled = true
            units.clear()
            val choosenFoodRef = myRef.child(choosenFood)
            choosenFoodRef.addChildEventListener(object : ChildEventListenerWrapper {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val unit = dataSnapshot.key
                    if (!units.contains(unit))
                        units.add(unit.toString())

                    edt_add_food_quantity.requestFocus()
                    spinner_unit.isEnabled = units.size != 1
                    unitListAdapter.notifyDataSetChanged()
                    setTxtInformer(units[0], choosenFood, carb_value, View.VISIBLE)
                }
            })
        }

        btn_add_to_meal_plan.setOnClickListener {
            if (!TextUtils.isEmpty(choosenFood)) {
                if (foods.contains(edt_choose_food.text.toString())) {
                    val txt = edt_add_food_quantity.text.toString()
                    if (!TextUtils.isEmpty(txt)) {
                        val model = MealPlan()
                        model.CarbsInMeal = carb_value.toDouble() * txt.toDouble()
                        model.MealName = choosenFood
                        model.Unit = spinner_unit.selectedItem.toString()
                        model.Id = getMaxID(TableType.MEAL)
                        model.Quantity = txt.toDouble()
                        model.isFavourite = isFavourite(model)
                        updateMealList(model)
                    } else {
                        showEmptyValueToast(getString(R.string.empty_quantity))
                    }
                } else {
                    showEmptyValueToast(getString(R.string.choose_meal_from_list))
                }
            } else {
                showEmptyValueToast(getString(R.string.choose_meal))
            }
        }

        spinner_unit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val choosenunitRef = myRef.child(choosenFood).child(units[position])
                choosenunitRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        carb_value = dataSnapshot.value!!.toString()
                        setTxtInformer(units[position], choosenFood, carb_value, View.VISIBLE)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
        btn_clear_all.setOnClickListener {
            clearViewItems(false)
            val it = mealList.iterator()
            while (it.hasNext()) {
                // Silmeyiniz. meal objesi boşuna değildir. it.next çalışmaktadır.
                it.next()
                it.remove()
            }
            setTxtCarbCounter(View.GONE)
            totalCarbs = 0.0
        }

        edt_choose_food.addTextChangedListener(object : TextWatcherWrapper {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count < 1) clearViewItems(true)
            }
        })

        btn_save_meal.setOnClickListener {
            if (mealList.size <= 0) return@setOnClickListener
            mRealm = Realm.getDefaultInstance()
            mRealm.executeTransaction { realm ->
                Collections.reverse(mealList)
                val food = realm.createObject(Foods::class.java, getMaxID(TableType.FOOD))
                food.mealPlans = RealmList<MealPlan>().apply { addAll(realm.copyToRealm(mealList)) }
                food.totalCarbsOfRepast = totalCarbs
                food.Repast = getExtras(MainActivity.EXTRA_REPAST)
                food.foodDate = getExtras(MainActivity.EXTRA_CHOSEN_DATE)
            }
            finish()
            startActivity(Intent(it.context, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)
        setSupportActionBar(toolbar_add_food)
        initComponents()
        rv_meals.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_meals.adapter = rvAdapter
        rv_meals.isNestedScrollingEnabled = false

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.bg_line_divider))
        rv_meals.addItemDecoration(dividerItemDecoration)
    }

    private fun updateMealList(mealPlan: MealPlan) {
        mealList.add(0, mealPlan)
        totalCarbs += mealPlan.CarbsInMeal
        setTxtCarbCounter(View.VISIBLE)
        clearViewItems(false)
    }

    private fun setFavButtonEnable() {
        btn_add_food_open_favs.isEnabled = favouriteList.size != 0
        btn_add_food_open_favs.setOnClickListener { setFavDialog() }
    }

    fun clearViewItems(isChoosenIncluded: Boolean) {
        if (edt_add_food_quantity != null)
            edt_add_food_quantity.setText("")
        if (!isChoosenIncluded) {
            edt_choose_food.setText("")
            closeSoftKeyboard(edt_choose_food)
        }
        setTxtInformer(null, null, null, View.GONE)
        units.clear()
        unitListAdapter.notifyDataSetChanged()
        rvAdapter.notifyDataSetChanged()
    }

    fun setTxtCarbCounter(Visibility: Int) {
        btn_save_meal.visibility = Visibility
        txt_total_carbs.visibility = Visibility
        txt_total_carbs.text = if (Visibility == View.GONE) ""
        else String.format(getString(R.string.total_carb), Number.format(totalCarbs))
    }

    fun setTxtInformer(unit: String?, meal: String?, carb: String?, visibility: Int) {
        txt_informer.text = if (!TextUtils.isEmpty(unit))
            String.format(getString(R.string.carb_quantity_in_meal), unit, meal, carb) else getString(R.string.choose_unit)
        pnl_select_unit.visibility = visibility
        pnl_quantity_add_food.visibility = visibility
    }

    inner class FavMealsHandler(internal var context: Context) : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val model = msg.obj as FavouriteMeals
            val mealPlan = MealPlan()
            mealPlan.CarbsInMeal = model.CarbsInMeal
            mealPlan.MealName = model.MealName
            mealPlan.Quantity = model.Quantity
            mealPlan.isFavourite = true
            mealPlan.Unit = model.Unit
            mealPlan.Id = model.Id
            updateMealList(mealPlan)
        }
    }

    /*private fun setBloodGlucoseDialog() {
        val bloodGlucoseDialog = BloodGlucoseDialog(this, BloodGlucoseHandler(this))
        bloodGlucoseDialog.show()
        bloodGlucoseDialog.setCancelable(true)
        bloodGlucoseDialog.setCanceledOnTouchOutside(true)
    }
    inner class BloodGlucoseHandler(internal var context: Context) : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            // TODO: Kan şekeri değeri buraya gelmektedir. Buradan veri tabanına iletilmesi gerekmektedir.
            val bloodSugar = msg.obj as String
        }
    }*/

    private fun setFavDialog() {
        val favMealsDialog = FavouriteMealsDialog(this, FavMealsHandler(this), favouriteList)
        favMealsDialog.show()
        favMealsDialog.setCancelable(true)
        favMealsDialog.setCanceledOnTouchOutside(true)
    }

    private fun getFavourite(meal: MealPlan): Int {
        val realmFav = mRealm.where<FavouriteMeals>()
            .equalTo(FieldNames.CARBSINMEAL, meal.CarbsInMeal)
            .equalTo(FieldNames.MEALNAME, meal.MealName)
            .equalTo(FieldNames.QUANTITY, meal.Quantity)
            .equalTo(FieldNames.UNIT, meal.Unit)
            .findAll()
        return if (realmFav.size < 1) -1 else realmFav.get(0)!!.Id
    }

    private fun isFavourite(meal: MealPlan): Boolean {
        val realmFav = mRealm.where<FavouriteMeals>()
            .equalTo(FieldNames.CARBSINMEAL, meal.CarbsInMeal)
            .equalTo(FieldNames.MEALNAME, meal.MealName)
            .equalTo(FieldNames.QUANTITY, meal.Quantity)
            .equalTo(FieldNames.UNIT, meal.Unit)
            .findAll()
        return realmFav.size > 0
    }

    override fun onDeleteClickListener(mealPlan: MealPlan) {
        if (mealList.size > 1) {
            totalCarbs -= mealPlan.CarbsInMeal
            setTxtCarbCounter(View.VISIBLE)
        } else {
            totalCarbs = 0.0
            setTxtCarbCounter(View.GONE)
        }
        mealList.remove(mealPlan)
        rvAdapter.notifyDataSetChanged()
    }

    override fun onAddFavClickListener(mealPlan: MealPlan) {
        mRealm.executeTransaction(Realm.Transaction { realm ->
            val fav = realm.createObject(FavouriteMeals::class.java, getMaxID(TableType.FAV))
            fav.Unit = mealPlan.Unit
            fav.Quantity = mealPlan.Quantity
            fav.MealName = mealPlan.MealName
            fav.CarbsInMeal = mealPlan.CarbsInMeal
        })
        updateMeals(mealPlan)
        showSuccessToast(getString(R.string.meal_added_to_favourites))
    }

    override fun onRemoveFavClickListener(mealPlan: MealPlan) {
        val id = getFavourite(mealPlan)
        mRealm.executeTransaction(Realm.Transaction { realm ->
            val results = realm.where<FavouriteMeals>().equalTo(FieldNames.ID, id).findFirst()
            results!!.deleteFromRealm()
        })
        updateMeals(mealPlan)
        showSuccessToast(getString(R.string.meal_removed_from_favourites))
    }

    private fun updateMeals(model : MealPlan) {
        for (m in mealList) {
            if (model.CarbsInMeal == m.CarbsInMeal && model.MealName == m.MealName
                && model.Unit == m.Unit && model.Quantity == m.Quantity) {
                mealList.set(mealList.indexOf(m), model)
            }
        }
        rvAdapter.notifyDataSetChanged()
    }

    override fun refreshFavButton() {
        setFavButtonEnable()
    }
}