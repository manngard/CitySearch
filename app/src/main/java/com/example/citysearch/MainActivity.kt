package com.example.citysearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainpage)

        val countrySearchButton = findViewById<Button>(R.id.countrySearchButton)
        countrySearchButton.setOnClickListener{setContentView(R.layout.searchpage)}

        val citySearchButton = findViewById<Button>(R.id.citySearchButton)
        citySearchButton.setOnClickListener{setContentView(R.layout.searchpage)}
    }
}