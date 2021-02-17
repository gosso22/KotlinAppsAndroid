package com.kassim.foodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kassim.foodapp.databinding.ActivityFoodDescriptionBinding

class FoodDescription : AppCompatActivity() {

    lateinit var binding: ActivityFoodDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFoodDescriptionBinding.inflate(layoutInflater)

        val rootView = binding.root

        var bundle: Bundle = intent.extras!!

        if (bundle != null) {

            var name = bundle.getString("name")
            var image = bundle.getInt("image")
            var desc = bundle.getString("description")

            binding.ivFoodImage.setImageResource(image)
            binding.tvfoodDesc.text = desc
            binding.foodName.text = name

        }

        setContentView(rootView)
    }
}