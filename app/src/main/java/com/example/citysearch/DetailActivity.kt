package com.example.citysearch

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.citysearch.dataclasses.APIHandler
import java.lang.IndexOutOfBoundsException

/**
 * Class responsible for containing the functionality for the layout detailpage.xml
 */

class DetailActivity : AppCompatActivity(){
    private val apiHandler = APIHandler()
    private lateinit var adapter: ArrayAdapter<String>
    private val itemList: MutableList<String> = mutableListOf()
    private val populationMap: MutableMap<String, String> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailpage)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val escapeHatch2 = findViewById<Button>(R.id.escapeHatch2)
        escapeHatch2.setOnClickListener{goToMainPage()}

        val state = intent.getSerializableExtra("State") as State
        setTitle(state)

        val flowPane = findViewById<GridView>(R.id.flowPane)

        adapter = object : ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,itemList) {
            override fun getView(
                    position: Int, convertView: View?,
                    parent: ViewGroup
            ): View {
                return (super.getView(position, convertView, parent) as TextView)
                        .apply {
                            text = itemList[position]
                            gravity = Gravity.CENTER
                            setTextColor(Color.WHITE)
                            setBackgroundColor(Color.parseColor("#6200EA"))
                        }
            }
        }

        flowPane.adapter = adapter

        var query = ""
        when(state) {
            State.COUNTRYVIEW ->{
                val countryCode = intent.getStringExtra("CountryCode")
                if (countryCode != null){
                    query = countryCode
                    flowPane.setOnItemClickListener { adapterView, view, i, l ->
                        val city = adapterView.getItemAtPosition(i).toString()
                        val population = populationMap[city]
                        goToCityDetails(city,population.toString())
                    }
                }
                else{
                    invalidQuery(state)
                }
            }
            State.CITYVIEW ->{
                itemList.add("Population")
                if (intent.getStringExtra("Population") == null){
                    query = intent.getStringExtra("ItemCategory") as String
                } else{
                    itemList.add(intent.getStringExtra("Population") as String)
                }

            }
        }

        val dataPairs = apiHandler.fetchContent(state,query)

        if (query != ""){
            when(state){
                State.COUNTRYVIEW -> for (Pair in dataPairs){
                    itemList.add(Pair.first)
                    populationMap[Pair.first] = Pair.second
                }
                State.CITYVIEW ->{
                    try {
                        itemList.add(dataPairs[0].second)
                    }
                    catch (e: IndexOutOfBoundsException){
                        invalidQuery(state)
                    }
                }

            }
        }
        adapter.notifyDataSetChanged()
    }

    /**
     * Switches page to a new MainActivity
     */

    private fun goToMainPage() {
        val intent = Intent(this@DetailActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Switches page to a new DetailActivity
     *
     * @param city - The city shown in the DetailActivity created
     * @param population - The population of the city shown in the created DetailActivity
     */

    private fun goToCityDetails(city: String, population: String){
        val intent = Intent(this@DetailActivity, DetailActivity::class.java)
        intent.putExtra("State", State.CITYVIEW)
        intent.putExtra("City", city)
        intent.putExtra("Population", population)
        startActivity(intent)
        finish()
    }

    /**
     * Switches page back to a SearchActivity displaying the appropriate error
     *
     * @param state - The state of the application before the invalid query was searched
     */

    private fun invalidQuery(state: State){
        val intent = Intent(this@DetailActivity, SearchActivity::class.java)
        intent.putExtra("State", state)
        val errorMessage = when(state){
            State.COUNTRYVIEW -> "Your chosen country was not found"
            State.CITYVIEW -> "Your chosen city was not found"
        }
        intent.putExtra("Error", errorMessage)
        startActivity(intent)
        finish()
    }

    /**
     * Sets the title of current page
     *
     * @param state - The state of current application page
     */

    private fun setTitle(state: State){
        val detailTitle = findViewById<TextView>(R.id.detailTitle)
        when(state){
            State.CITYVIEW -> detailTitle.text = if(intent.getStringExtra("City") == null){
                intent.getStringExtra("ItemCategory")
            }
            else{
                intent.getStringExtra("City")
            }
            State.COUNTRYVIEW -> detailTitle.text = intent.getStringExtra("ItemCategory")
        }
    }
}