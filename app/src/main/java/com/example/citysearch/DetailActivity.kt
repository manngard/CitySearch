package com.example.citysearch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailpage)

        val escapeHatch2 = findViewById<Button>(R.id.escapeHatch2)
        escapeHatch2.setOnClickListener{goToMainPage()}

        val state = intent.getSerializableExtra("State") as State
        prepareContent(state)
    }

    fun goToMainPage() {
        val intent = Intent(this@DetailActivity, MainActivity::class.java)
        startActivity(intent)
    }

    fun prepareContent(state: State){
        val detailTitle = findViewById<TextView>(R.id.detailTitle)

        when(state){
            State.CITYVIEW -> detailTitle.text = "Paris" //TODO get this dynamically from API
            State.COUNTRYVIEW -> detailTitle.text = "France" //TODO get this dynamically from API
        }
    }
}