package com.diahelp.addfood

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.diahelp.MainActivity
import com.diahelp.R
import com.diahelp.base.BaseActivity
import com.diahelp.base.BaseMvpActivity
import com.diahelp.model.FavouriteMeals
import com.diahelp.model.Foods
import com.diahelp.model.MealPlan
import com.diahelp.tools.StringRealm
import com.diahelp.ui.CustomIconButton
import com.google.firebase.database.*
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_add_food.*
import kotlinx.android.synthetic.main.item_meal_plan.*
import tools.NumberFormatManager

import java.util.ArrayList
import java.util.Collections

class AddFoodActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    private val fooddb_ArrayList = ArrayList<String>() // bu arraylist yiyecek listesi için
    private val unit_ArrayList = ArrayList<String>()
    private var FoodDBArrayAdapter: ArrayAdapter<String>? = null
    private var carb_value: String? = null
    private var choosenFood = ""
    private val mealName: String? = null
    private var totalCarbs = 0.0
    private var mealList: RealmList<MealPlan>? = null

    private var rvAdapter: RecyclerViewAdapter? = null
    private var unitListAdapter: ArrayAdapter<*>? = null

    var database : FirebaseDatabase = FirebaseDatabase.getInstance()
    var myRef : DatabaseReference
    init {
        // todo database referance
        myRef = database.getReference("")
    }

    private val favouriteList: List<FavouriteMeals>
        get() {
            val modelList = ArrayList<FavouriteMeals>()
            /*val list = mRealm.where(FavouriteMeals::class.java).findAll()
            for (model in list) {
                modelList.add(model)
            }*/
            return modelList
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)
        initializeUiComponents()
        init_Firebase()
        val recyclerViewMealPlan = findViewById<View>(R.id.rv_meals) as RecyclerView
        recyclerViewMealPlan.layoutManager =
            LinearLayoutManager(this@AddFoodActivity, LinearLayoutManager.VERTICAL, false)
        recyclerViewMealPlan.adapter = rvAdapter
        recyclerViewMealPlan.isNestedScrollingEnabled = false

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.bg_line_divider))
        recyclerViewMealPlan.addItemDecoration(dividerItemDecoration)
    }

    private fun updateMealList(mealPlan: MealPlan) {
        mealList!!.add(0, mealPlan)
        totalCarbs += mealPlan.CarbsInMeal * mealPlan.Quantity
        setTxtCarbCounter(View.VISIBLE)
        clearViewItems(false)
    }

    private fun initializeUiComponents() {
        val myToolbar = findViewById<View>(R.id.toolbar_add_food) as Toolbar
        setSupportActionBar(myToolbar)


        mealList = RealmList()
        rvAdapter = RecyclerViewAdapter(mealList!!)
        unitListAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, unit_ArrayList)
        watchUserTypingEvent(edt_choose_food!!)


        myRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                fooddb_ArrayList.add(dataSnapshot.key.toString())
                FoodDBArrayAdapter =
                    ArrayAdapter(applicationContext, R.layout.styled_autocompletetextview, fooddb_ArrayList)
                edt_choose_food.setAdapter<ArrayAdapter<String>>(FoodDBArrayAdapter)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        spinner_unit!!.isEnabled = false
        unitListAdapter!!.setDropDownViewResource(R.layout.simple_spinner_item)
        spinner_unit!!.adapter = unitListAdapter

        setFavButtonEnable()
        edt_choose_food!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            choosenFood = adapterView.getItemAtPosition(i).toString()
            spinner_unit!!.isEnabled = true
            unit_ArrayList.clear()
            val choosenFoodRef = myRef.child(choosenFood)
            choosenFoodRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val unit = dataSnapshot.key
                    if (!unit_ArrayList.contains(unit))
                        unit_ArrayList.add(unit.toString())

                    edt_add_food_quantity!!.requestFocus()
                    spinner_unit!!.isEnabled = if (unit_ArrayList.size == 1) false else true
                    unitListAdapter!!.notifyDataSetChanged()
                    setTxtInformer(
                        "Bir " + unit_ArrayList[0] + " " + choosenFood
                                + " " + carb_value + " gr karbonhidrat içerir.", View.VISIBLE
                    )
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

        spinner_unit!!.onItemSelectedListener = this
    }

    private fun setFavButtonEnable() {
        val button = findViewById<CustomIconButton>(R.id.btn_add_food_open_favs)
        button.setOnClickListener {
           /* if (favouriteList.size > 0)
                setFavDialog()*/
        }
        button.isEnabled = favouriteList.size != 0
    }

    fun clearViewItems(isChoosenIncluded: Boolean) {
        if (edt_add_food_quantity != null)
            edt_add_food_quantity!!.setText("")
        if (!isChoosenIncluded) {
            edt_choose_food!!.setText("")
            closeSoftKeyboard(edt_choose_food)
        }
        setTxtInformer("Birim Seçiniz", View.GONE)
        unit_ArrayList.clear()
        unitListAdapter!!.notifyDataSetChanged()
        rvAdapter!!.notifyDataSetChanged()
    }

    fun setTxtCarbCounter(Visibility: Int) {
        findViewById<View>(R.id.btn_save_meal).visibility = Visibility

        (findViewById<View>(R.id.txt_total_carbs) as TextView).visibility = Visibility
        (findViewById<View>(R.id.txt_total_carbs) as TextView).text = if (Visibility == View.GONE)
            ""
        else
            "Toplam Karbonhidrat: " + NumberFormatManager.getFormattedNumber(totalCarbs)
    }

    private fun watchUserTypingEvent(edt: AutoCompleteTextView) {
        edt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                if (count < 1)
                    clearViewItems(true)
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    fun setTxtInformer(text: String, visibility: Int) {
        (findViewById<View>(R.id.txt_informer) as TextView).text = text
        findViewById<View>(R.id.pnl_select_unit).visibility = visibility
        findViewById<View>(R.id.pnl_quantity_add_food).visibility = visibility
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val choosenFoodRef = myRef.child(choosenFood)
        val choosenunitRef = choosenFoodRef.child(unit_ArrayList[position])
        choosenunitRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                carb_value = dataSnapshot.value!!.toString()

                setTxtInformer(
                    "Bir " + unit_ArrayList[position] + " " + choosenFood
                            + " " + carb_value + " gr karbonhidrat içerir.", View.VISIBLE
                )
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}

    inner class FavMealsHandler(internal var context: Context) : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val id = msg.obj as String
            val (Id, MealName, CarbsInMeal, Quantity, Unit) = favouriteList[Integer.parseInt(id)]
            val mealPlan = MealPlan()
            mealPlan.Meal = mealName.toString()
            mealPlan.CarbsInMeal = CarbsInMeal
            mealPlan.MealName = MealName
            mealPlan.Quantity = Quantity
            mealPlan.Unit = Unit
            mealPlan.Id = Id
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
    }

    private fun setFavDialog() {
        val favMealsDialog = FavouriteMealsDialog(this, FavMealsHandler(this), favouriteList)
        favMealsDialog.show()
        favMealsDialog.setCancelable(true)
        favMealsDialog.setCanceledOnTouchOutside(true)
    }*/

    fun AddFoodOnClickListener(view: View) {
        when (view.id) {
            /*case R.id.btn_open_meal_type:
                mealTypeDialog();
                break;*/
            //R.id.btn_add_food_blood_glucose -> setBloodGlucoseDialog()
            R.id.btn_clear_all -> {
                clearViewItems(false)
                val it = mealList!!.iterator()
                while (it.hasNext()) {
                    // Silmeyiniz. meal objesi boşuna değildir. it.next çalışmaktadır.
                    val (Id, MealName, CarbsInMeal, Quantity, Unit, Meal) = it.next()
                    it.remove()
                }
                setTxtCarbCounter(View.GONE)
                totalCarbs = 0.0
            }
            R.id.btn_save_meal -> if (mealList != null && mealList!!.size > 0 && mealName != null) {
                /*mRealm = Realm.getDefaultInstance()
                mRealm.executeTransaction(Realm.Transaction { realm ->
                    Collections.reverse(mealList)
                    val mealFoodListToRealm = RealmList<StringRealm>()

                    for (i in mealList!!.indices) {
                        val Id = getMaxID("meal")
                        val meal = realm.createObject(MealPlan::class.java, Id)
                        meal.Unit = mealList!![i]!!.Unit
                        meal.Quantity = mealList!![i]!!.Quantity
                        meal.MealName = mealList!![i]!!.MealName
                        meal.CarbsInMeal = mealList!![i]!!.CarbsInMeal
                        meal.Meal = mealList!![i]!!.Meal
                        val stringRealm = realm.createObject(StringRealm::class.java!!)
                        stringRealm.mealId = Id.toString()
                        mealFoodListToRealm.add(stringRealm)
                    }
                    val food = realm.createObject(Foods::class.java!!, getMaxID("food"))
                    food.mealIds = mealFoodListToRealm
                    food.Meal = mealName
                })*/
                finish()
                startActivity(Intent(view.context, MainActivity::class.java))
            }
            R.id.btn_add_to_meal_plan -> if (!TextUtils.isEmpty(choosenFood)) {
                if (fooddb_ArrayList.contains(edt_choose_food!!.text.toString())) {
                    if (!TextUtils.isEmpty(edt_add_food_quantity!!.text.toString())) {
                        val model = MealPlan()
                        model.CarbsInMeal =
                            java.lang.Double.parseDouble(carb_value!!) * java.lang.Double.parseDouble(edt_add_food_quantity!!.text.toString())
                        model.MealName = choosenFood
                        model.Unit = spinner_unit!!.selectedItem.toString()
                        model.Id = getMaxID("meal")
                        model.Quantity = java.lang.Double.parseDouble(edt_add_food_quantity!!.text.toString())
                        model.Meal = mealName.toString()
                        updateMealList(model)
                    } else {
                        showEmptyValueToast("Lütfen miktarı giriniz.")
                    }
                } else {
                    showEmptyValueToast("Lütfen listeden yiyecek seçiniz.")
                }
            } else {
                showEmptyValueToast("Lütfen yiyecek seçiniz.")
            }
        }
    }

    inner class RecyclerViewAdapter(private val horizontalListadapter: List<MealPlan>) :
        RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

        inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val txtQuantity: TextView
            private val txtName: TextView
            private val txtCarbs: TextView
            private val btnDeleteFood: ImageButton
            private val btnFav: ImageButton

            init {
                btnDeleteFood = view.findViewById(R.id.btn_delete_item_from_meal) as ImageButton
                txtQuantity = view.findViewById(R.id.item_meal_txt_quantity) as TextView
                txtCarbs = view.findViewById(R.id.item_meal_txt_food_carbs) as TextView
                txtName = view.findViewById(R.id.item_meal_txt_food_name) as TextView
                btnFav = view.findViewById(R.id.btn_add_favourites) as ImageButton
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_meal_plan, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val model = mealList!![holder.adapterPosition]
            item_meal_txt_food_name.text = horizontalListadapter[holder.adapterPosition].MealName
            item_meal_txt_food_carbs.text = "Karbonhidrat " + NumberFormatManager.getFormattedNumber(model!!.CarbsInMeal)
            item_meal_txt_quantity.text =
                NumberFormatManager.getFormattedNumber(horizontalListadapter[holder.adapterPosition].Quantity) + " " +
                        horizontalListadapter[holder.adapterPosition].Unit
            btn_delete_item_from_meal.setOnClickListener {
                if (mealList!!.size > 1) {
                    totalCarbs -= horizontalListadapter[holder.adapterPosition].CarbsInMeal
                    setTxtCarbCounter(View.VISIBLE)
                } else {
                    totalCarbs = 0.0
                    setTxtCarbCounter(View.GONE)
                }
                mealList!!.remove(mealList!![holder.adapterPosition])
                rvAdapter!!.notifyDataSetChanged()
            }

            btn_add_favourites.setColorFilter(
                if (isFavourite(mealList!![holder.adapterPosition]!!))
                    ContextCompat.getColor(this@AddFoodActivity, R.color.color_fav)
                else
                    ContextCompat.getColor(this@AddFoodActivity, R.color.colorBackground)
            )

            btn_add_favourites.setOnClickListener {
                if (!isFavourite(model)) {
                    btn_add_favourites.setColorFilter(ContextCompat.getColor(this@AddFoodActivity, R.color.color_fav))
                    /*mRealm.executeTransaction(Realm.Transaction { realm ->
                        val fav = realm.createObject(FavouriteMeals::class.java, getMaxID("fav"))
                        fav.Unit = model.Unit
                        fav.Quantity = model.Quantity
                        fav.MealName = model.MealName
                        fav.CarbsInMeal = model.CarbsInMeal
                    })*/
                    showSuccessToast("Yiyecek Favorilere Eklendi")
                } else {
                    val id = getFavourite(model)
                    btn_add_favourites.setColorFilter(ContextCompat.getColor(this@AddFoodActivity, R.color.colorBackground))
                    /*mRealm.executeTransaction(Realm.Transaction { realm ->
                        val results = realm.where(FavouriteMeals::class.java).equalTo("Id", id).findFirst()
                        results!!.deleteFromRealm()
                    })*/
                    showSuccessToast("Yiyecek Favorilerden Çıkarıldı")
                }
                setFavButtonEnable()
            }
        }

        private fun getFavourite(meal: MealPlan): Int {
            /*val realmFav = mRealm.where(FavouriteMeals::class.java)
                .equalTo("MealName", meal.MealName)
                .equalTo("CarbsInMeal", meal.CarbsInMeal)
                .equalTo("Quantity", meal.Quantity)
                .equalTo("Unit", meal.Unit)
                .findAll()
            return if (realmFav.size < 1) -1 else realmFav.get(0)!!.Id*/
            return 1
        }

        private fun isFavourite(meal: MealPlan): Boolean {
            /*val realmFav = mRealm.where(FavouriteMeals::class.java)
                .equalTo("MealName", meal.MealName)
                .equalTo("CarbsInMeal", meal.CarbsInMeal)
                .equalTo("Quantity", meal.Quantity)
                .equalTo("Unit", meal.Unit)
                .findAll()
            return realmFav.size > 0*/
            return true
        }

        override fun getItemCount(): Int {
            return mealList!!.size
        }
    }
    private fun init_Firebase() {}
}