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
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

/**
 * Class responsible for containing the functionality for the layout detailpage.xml
 */

class DetailActivity : AppCompatActivity(){
    private lateinit var adapter: ArrayAdapter<String>
    private val itemList: MutableList<String> = mutableListOf()
    private val populationMap: MutableMap<String, Long> = hashMapOf()

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

        fun fetchJson(request: Request) {
            val client = OkHttpClient()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Error: $response")
                val body = response.body?.string()
                val gson = GsonBuilder().create()
                val parsedResponse = gson.fromJson(body, Reply::class.java)
                if (parsedResponse.totalResultsCount == 0){
                    invalidQuery(state)
                    finish()
                }
                for (CityInfo in parsedResponse.geonames){
                    itemList.add(CityInfo.name.toUpperCase())
                    populationMap[CityInfo.name.toUpperCase()] = CityInfo.population
                }
            }
        }

        when(state){
            State.COUNTRYVIEW -> {
                flowPane.setOnItemClickListener { adapterView, view, i, l ->
                    val city = adapterView.getItemAtPosition(i).toString()
                    val population = populationMap[city]
                    goToCityDetails(city,population.toString())
                }
                fetchJson(countrySearchRequest())
            }
            State.CITYVIEW ->{
                var population = intent.getStringExtra("Population")
                if (population == null){ //lookathis
                    val city = intent.getStringExtra("ItemCategory") as String
                    fetchJson(citySearchRequest(city))
                    population = populationMap[city.toUpperCase()].toString()
                }
                itemList.add("Population\n$population")
            }
        }

        adapter.notifyDataSetChanged()


    }

    fun countrySearchRequest(): Request{
        return Request.Builder().url(HttpUrl.Builder()
                    .scheme("https")
                    .host("secure.geonames.org")
                    .addPathSegment("search")
                    .addQueryParameter("country", intent.getStringExtra("ItemCategory"))
                    .addQueryParameter("featureClass", "P")
                    .addQueryParameter("maxRows", "5")
                    .addQueryParameter("type", "json")
                    .addQueryParameter("orderby", "population")
                    .addQueryParameter("username", "weknowit").build().toString()).build()
    }

    fun citySearchRequest(city: String): Request{
        return Request.Builder().url(HttpUrl.Builder()
                .scheme("https")
                .host("secure.geonames.org")
                .addPathSegment("search")
                .addQueryParameter("name_equals", city)
                .addQueryParameter("featureClass", "P")
                .addQueryParameter("maxRows", "1")
                .addQueryParameter("type", "json")
                .addQueryParameter("orderby", "population")
                .addQueryParameter("username", "weknowit").build().toString()).build()
    }

    fun goToMainPage() {
        val intent = Intent(this@DetailActivity, MainActivity::class.java)
        startActivity(intent)
    }

    fun goToCityDetails(city: String, population: String){
        val intent = Intent(this@DetailActivity, DetailActivity::class.java)
        intent.putExtra("State", State.CITYVIEW)
        intent.putExtra("City", city)
        intent.putExtra("Population", population)
        startActivity(intent)
    }
    fun invalidQuery(state: State){
        val intent = Intent(this@DetailActivity, SearchActivity::class.java)
        intent.putExtra("State", state)
        val errorMessage = when(state){
            State.COUNTRYVIEW -> "Your chosen country was not found"
            State.CITYVIEW -> "Your chosen city was not found"
        }
        intent.putExtra("Error", errorMessage)
        startActivity(intent)
    }

    fun setTitle(state: State){
        val detailTitle = findViewById<TextView>(R.id.detailTitle)
        when(state){
            State.CITYVIEW -> detailTitle.text = intent.getStringExtra("City")
            State.COUNTRYVIEW -> detailTitle.text = intent.getStringExtra("ItemCategory")
        }
    }
}