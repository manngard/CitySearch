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
    private val populationMap: MutableMap<String,Int> = hashMapOf()

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

        fun fetchJson() {
            val request = getRequest()
            val client = OkHttpClient()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Error: $response")
                val body = response.body?.string()
                val gson = GsonBuilder().create()
                for (CityInfo in gson.fromJson(body, Reply::class.java).geonames){
                    itemList.add(CityInfo.name.toUpperCase())
                    populationMap[CityInfo.name.toUpperCase()] = CityInfo.population
                }
            }
        }

        when(intent.getSerializableExtra("State")){
            State.COUNTRYVIEW -> {
                flowPane.setOnItemClickListener { adapterView, view, i, l ->
                    val city = adapterView.getItemAtPosition(i).toString()
                    val population = populationMap[city]
                    goToCityDetails(city,population.toString())
                }
                fetchJson()
            }
            State.CITYVIEW ->{
                itemList.add("Population" + "\n" + intent.getStringExtra("Population"))
            }
        }

        adapter.notifyDataSetChanged()


    }

    fun getRequest(): Request{
        return Request.Builder().url(HttpUrl.Builder()
                    .scheme("https")
                    .host("secure.geonames.org")
                    .addPathSegment("search")
                    .addQueryParameter("country", intent.getStringExtra("ItemCategory"))
                    .addQueryParameter("maxRows", "5")
                    .addQueryParameter("type", "json")
                    .addQueryParameter("orderby", "relevance")
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

    fun setTitle(state: State){
        val detailTitle = findViewById<TextView>(R.id.detailTitle)
        when(state){
            State.CITYVIEW -> detailTitle.text = intent.getStringExtra("City")
            State.COUNTRYVIEW -> detailTitle.text = intent.getStringExtra("ItemCategory")
        }
    }
}