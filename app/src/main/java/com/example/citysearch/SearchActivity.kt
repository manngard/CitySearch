package com.example.citysearch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import java.util.*

/**
 * Class responsible for containing the functionality for the layout searchpage.xml
 */

class SearchActivity : AppCompatActivity(){
    private lateinit var state: State
    private lateinit var searchBar: SearchView
    private lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.searchpage)

        val escapeHatch = findViewById<Button>(R.id.escapeHatch)
        escapeHatch.setOnClickListener{goToMainPage()}

        loadingIndicator = findViewById(R.id.loadingIndicator)

        val searchButton = findViewById<Button>(R.id.searchButton)
        searchButton.setOnClickListener{goToDetailPage()}

        val errorMessage = findViewById<TextView>(R.id.errorMessage)

        if (intent.getStringExtra("Error") != null){
            errorMessage.text = intent.getStringExtra("Error")
        }

        state = intent.getSerializableExtra("State") as State
        setTitle(state)

        searchBar = findViewById(R.id.searchBar)
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(s: String): Boolean {
                errorMessage.visibility = View.GONE
                return true
            }

            override fun onQueryTextSubmit(s: String): Boolean {
                goToDetailPage()
                return true
            }
        })
    }


    /**
     * Switches page to a new MainActivity
     */

    private fun goToMainPage() {
        val intent = Intent(this@SearchActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Shows loadingIndicator and when content for new DetailActivity is loaded goes to that page
     */

    private fun goToDetailPage(){
        loadingIndicator.visibility = View.VISIBLE
        val intent = Intent(this@SearchActivity, DetailActivity::class.java)
        val query = searchBar.query.toString()
        intent.putExtra("State", state)
        intent.putExtra("ItemCategory", query.toLowerCase().capitalizeAllWords())
        intent.putExtra("CountryCode", getCountryCode(query.toLowerCase().capitalizeAllWords()))
        startActivity(intent)
        finish()
    }

    /**
     * Sets the title of current page
     *
     * @param state - The state of current application page
     */

    private fun setTitle(state: State){
        val pageTitle = findViewById<TextView>(R.id.pageTitle)

        when(state){
            State.CITYVIEW -> pageTitle.text = "Search by city"
            State.COUNTRYVIEW -> pageTitle.text = "Search by country"
        }
    }

    /**
     * @param countryName - The name of country that ISO-3166 country code is wanted for
     * @return - The ISO-3166 country code for countryName
     */

    private fun getCountryCode(countryName: String) =
            Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }


    /**
     * Extension function of class String that capitalizes first letter of all words
     * in the given String
     */

    private fun String.capitalizeAllWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")
}