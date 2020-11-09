package com.example.citysearch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

/**
 * Class responsible for containing the functionality for the layout mainpage.xml
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainpage)

        val countrySearchButton = findViewById<Button>(R.id.countrySearchButton)
        countrySearchButton.setOnClickListener{searchForCountry()}

        val citySearchButton = findViewById<Button>(R.id.citySearchButton)
        citySearchButton.setOnClickListener{searchForCity()}
    }

    /**
     * Goes to new SearchActivity for cities
     */

    private fun searchForCity(){
        goToSearchPage(State.CITYVIEW)
    }

    /**
     * Goes to new SearchActivity for countries
     */

    private fun searchForCountry(){
        goToSearchPage(State.COUNTRYVIEW)
    }

    /**
     * Goes to new SearchActivity with state
     *
     * @param state - The state representing either searching for a country or a city
     */
    private fun goToSearchPage(state: State){
        val intent = Intent(this@MainActivity,SearchActivity::class.java)
        intent.putExtra("State", state)
        startActivity(intent)
        finish()
    }
}