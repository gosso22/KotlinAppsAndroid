package com.kassim.zooapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kassim.zooapp.databinding.ActivityAnimalInfoBinding

class AnimalInfo : AppCompatActivity() {

    lateinit var binding: ActivityAnimalInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAnimalInfoBinding.inflate(layoutInflater)
        val rootView = binding.root

        setContentView(rootView)

        val b: Bundle? = intent.extras
        if (b != null) {

            val name = b.getString("name")
            val desc = b.getString("description")
            val image = b.getInt("image")

            binding.ivAnimaImage.setImageResource(image)
            binding.tvAnimaDesc.text = desc
            binding.tvAnimalName.text = name

        }
    }
}