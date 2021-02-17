package com.kassim.zooapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat.startActivity
import com.kassim.zooapp.databinding.ActivityMainBinding
import com.kassim.zooapp.databinding.AnimalKillerTicketBinding
import com.kassim.zooapp.databinding.AnimalTicketBinding

class MainActivity : AppCompatActivity() {

    var listOfAnimal = arrayListOf<Animal>()
    var adapter:AnimalAdapter? = null
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val rootView = binding.root

        setContentView(rootView)

        // load animals
        listOfAnimal.add(Animal("Baboon", "Baboons are primates comprising the genus Papio, " +
                "one of the 23 genera of Old World monkeys. " +
                "There are five species of baboons, commonly known as hamadryas baboon, " +
                "Guinea baboon, olive baboon, yellow baboon and chacma baboon. " +
                "Each species is native to one of five areas of Africa and the hamadryas baboon is " +
                "also native to part of the Arabian Peninsula", R.drawable.baboon, false))

        listOfAnimal.add(Animal("Bulldog","The Bulldog, also known as the English " +
                "Bulldog or British Bulldog, is a medium-sized dog breed. It is a muscular, hefty " +
                "dog with a wrinkled face and a distinctive pushed-in nose. The Kennel Club, the " +
                "American Kennel Club, and the United Kennel Club oversee breeding records.", R.drawable.bulldog, false))

        listOfAnimal.add(Animal("Panda", "The giant panda, also known as the panda " +
                "bear or simply the panda, is a bear native to south central China. It is " +
                "characterised by large, black patches around its eyes, over the ears, and across " +
                "its round body. The name \"giant panda\" is sometimes used to distinguish it " +
                "from the red panda, a neighboring musteloid.", R.drawable.panda, true))

        listOfAnimal.add(Animal("Swallow Bird", "The swallows, martins, and " +
                "saw-wings, or Hirundinidae, are a family of passerine birds found around the " +
                "world on all continents, including occasionally in Antarctica. Highly adapted " +
                "to aerial feeding, they have a distinctive appearance. The term \"swallow\" is " +
                "used colloquially in Europe as a synonym for the barn swallow.", R.drawable.swallow_bird, false))

        listOfAnimal.add(Animal("White Tiger", "The white tiger or bleached tiger " +
                "is a leucistic pigmentation variant of the Bengal tigers, Siberian Tiger and " +
                "man-made hybrids between the two, which is reported in the wild from time to " +
                "time in the Indian states of Madhya Pradesh, Assam, West Bengal, Bihar and " +
                "Odisha in the Sunderbans region and especially in the former State of Rewa. " +
                "Such a tiger has the black stripes typical of the Bengal tiger, but carries a " +
                "white or near-white coat.", R.drawable.white_tiger, true))

        listOfAnimal.add(Animal("Zebra", "Zebras are African equines with " +
                "distinctive black-and-white striped coats. There are three extant species: " +
                "the Grévy's zebra, plains zebra, and the mountain zebra. Zebras share the " +
                "genus Equus with horses and asses, the three groups being the only living " +
                "members of the family Equidae.", R.drawable.zebra, false))


        adapter = AnimalAdapter(listOfAnimal, this)
        binding.tvListAnimal.adapter = adapter
    }

    inner class AnimalAdapter: BaseAdapter {

        var listOfAnimal = arrayListOf<Animal>()
        var context: Activity? = null
        lateinit var myViewBinding: AnimalTicketBinding
        lateinit var killerTicketBinding: AnimalKillerTicketBinding
        constructor(listOfAnimal: ArrayList<Animal>, context: Activity): super() {

            this.listOfAnimal = listOfAnimal
            this.context = context

        }

        override fun getCount(): Int {
            return listOfAnimal.size
        }

        override fun getItem(position: Int): Any {
            return listOfAnimal[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var animal = listOfAnimal[position]
            var myView: View? = null

            if (animal.isKiller!!) {
                killerTicketBinding = AnimalKillerTicketBinding.inflate(context?.layoutInflater!!) as AnimalKillerTicketBinding
                myView = killerTicketBinding.root
                killerTicketBinding.tvName.text = animal.name
                killerTicketBinding.tvDescription.text = animal.description
                killerTicketBinding.ivAnimal.setImageResource(animal.image!!)
                killerTicketBinding.ivAnimal.setOnClickListener {
                    val intent = Intent(context, AnimalInfo::class.java)
                    intent.putExtra("name", animal.name)
                    intent.putExtra("description", animal.description)
                    intent.putExtra("image", animal.image)
                    context!!.startActivity(intent)
                }
            } else {
                myViewBinding = AnimalTicketBinding.inflate(context?.layoutInflater!!) as AnimalTicketBinding
                myView = myViewBinding.root
                myViewBinding.tvName.text = animal.name
                myViewBinding.tvDescription.text = animal.description
                myViewBinding.ivAnimal.setImageResource(animal.image!!)
                myViewBinding.ivAnimal.setOnClickListener {
                    val intent = Intent(context, AnimalInfo::class.java)
                    intent.putExtra("name", animal.name)
                    intent.putExtra("description", animal.description)
                    intent.putExtra("image", animal.image)
                    context!!.startActivity(intent)
                }
            }

            return myView
        }

    }

    fun removeAt(index: Int) {

        listOfAnimal.removeAt(index)
        adapter!!.notifyDataSetChanged()

    }

}