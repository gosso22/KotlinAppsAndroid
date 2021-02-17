package com.kassim.foodapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.kassim.foodapp.databinding.ActivityMainBinding
import com.kassim.foodapp.databinding.GridItemBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var listOfFood = arrayListOf<Food>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        listOfFood.add(Food("Coffee", "Coffee is a brewed drink prepared from " +
                "roasted coffee beans, the seeds of berries from certain Coffea species. When " +
                "coffee berries turn from green to bright red in color – indicating ripeness – " +
                "they are picked, processed, and dried. Dried coffee seeds are roasted to varying " +
                "degrees, depending on the desired flavor.", R.drawable.coffee_pot))

        listOfFood.add(Food("Espresso","Espresso is a coffee-brewing method of " +
                "Italian origin, in which a small amount of nearly boiling water is forced " +
                "under 9–10 bars of atmospheric pressure through finely-ground coffee beans. " +
                "Espresso coffee can be made with a wide variety of coffee beans and roast degrees.", R.drawable.espresso))

        listOfFood.add(Food("French Fries", "French fries, or simply fries, chips, " +
                "finger chips, or French-fried potatoes, are batonnet or allumette-cut deep-fried potatoes.", R.drawable.french_fries))

        listOfFood.add(Food("Honey", "Honey is a sweet, viscous food substance made " +
                "by honey bees and some related insects. Bees produce honey from the sugary " +
                "secretions of plants or from secretions of other insects, by regurgitation, " +
                "enzymatic activity, and water evaporation. Bees store honey in wax structures " +
                "called honeycombs.", R.drawable.honey))

        listOfFood.add(Food("Strawberry Ice cream", "Strawberry ice cream is a " +
                "flavor of ice cream made with strawberry or strawberry flavoring. It is made by " +
                "blending in fresh strawberries or strawberry flavoring with the eggs, cream, " +
                "vanilla and sugar used to make ice cream. Most strawberry ice cream is " +
                "colored pink or light red.", R.drawable.strawberry_ice_cream))

        listOfFood.add(Food("Sugar Cubes", "Sugar is the generic name for " +
                "sweet-tasting, soluble carbohydrates, many of which are used in food. " +
                "Table sugar, granulated sugar, or regular sugar, refers to sucrose, a " +
                "disaccharide composed of glucose and fructose.", R.drawable.sugar_cubes))

        var adapter = MyAdapter(listOfFood, this)
        binding.gridView.adapter = adapter
    }

    class MyAdapter: BaseAdapter {

        var listOfFood = arrayListOf<Food>()
        var context: Activity? = null
        lateinit var itemBinding: GridItemBinding

        constructor(listOfFood: ArrayList<Food>, context: Activity): super() {

            this.listOfFood = listOfFood
            this.context = context

        }

        override fun getCount(): Int {
            return listOfFood.size
        }

        override fun getItem(position: Int): Any {
            return listOfFood[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            itemBinding = GridItemBinding.inflate(context!!.layoutInflater)
            var food = listOfFood[position]
            var myView = itemBinding.root
            itemBinding.tvFoodName.text = food.name
            itemBinding.imageView.setImageResource(food.image!!)

            itemBinding.imageView.setOnClickListener {
                var intent = Intent(context, FoodDescription::class.java)
                intent.putExtra("name", food.name)
                intent.putExtra("image", food.image)
                intent.putExtra("description", food.description)

                context!!.startActivity(intent)
            }

            return myView

        }
    }
}