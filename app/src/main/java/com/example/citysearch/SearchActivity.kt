package com.example.citysearch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.searchpage)

        val escapeHatch = findViewById<Button>(R.id.escapeHatch)
        escapeHatch.setOnClickListener{goToMainPage()}

        val title = intent.getSerializableExtra("State") as State
        prepareContent(title)
    }

    fun goToMainPage() {
        val intent = Intent(this@SearchActivity, MainActivity::class.java)
        startActivity(intent)
        setContentView(R.layout.mainpage)
    }

    fun prepareContent(state: State){
        val pageTitle = findViewById<TextView>(R.id.pageTitle)

        when(state){
            State.CITYVIEW -> pageTitle.text = "Search by city"
            State.COUNTRYVIEW -> pageTitle.text = "Search by country"
        }
    }
}