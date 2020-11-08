package com.example.citysearch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import java.util.*

/**
 * Class responsible for containing the functionality for the layout searchpage.xml
 */

class SearchActivity : AppCompatActivity(){
    private lateinit var state: State
    private lateinit var searchBar: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.searchpage)

        val escapeHatch = findViewById<Button>(R.id.escapeHatch)
        escapeHatch.setOnClickListener{goToMainPage()}

        val searchButton = findViewById<Button>(R.id.searchButton)
        searchButton.setOnClickListener{goToDetailPage()}

        val errorMessage = findViewById<TextView>(R.id.errorMessage)

        if (intent.getStringExtra("Error") != null){
            errorMessage.text = intent.getStringExtra("Error")
        }

        state = intent.getSerializableExtra("State") as State
        prepareContent(state)

        searchBar = findViewById(R.id.searchBar)
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(s: String): Boolean {
                //do nothing
                return true
            }

            override fun onQueryTextSubmit(s: String): Boolean {
                goToDetailPage()
                return true
            }
        })
    }

    fun getCountryCode(countryName: String) =
            Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }

    fun goToMainPage() {
        val intent = Intent(this@SearchActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun goToDetailPage(){
        val intent = Intent(this@SearchActivity, DetailActivity::class.java)
        val query = searchBar.query.toString()
        intent.putExtra("State", state)
        intent.putExtra("ItemCategory", query.capitalizeAllWords())
        intent.putExtra("CountryCode", getCountryCode(query.capitalizeAllWords()))
        startActivity(intent)
        finish()
    }

    fun prepareContent(state: State){
        val pageTitle = findViewById<TextView>(R.id.pageTitle)

        when(state){
            State.CITYVIEW -> pageTitle.text = "Search by city"
            State.COUNTRYVIEW -> pageTitle.text = "Search by country"
        }
    }

    fun String.capitalizeAllWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")
}