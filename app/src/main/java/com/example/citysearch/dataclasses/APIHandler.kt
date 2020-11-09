package com.example.citysearch.dataclasses

import com.example.citysearch.State
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Class that makes API requests to GeoNames API (http://secure.geonames.org/search) and parses
 * the data for use by DetailActivity
 */

class APIHandler {
    private fun fetchJson(request:Request): List<Pair<String,String>> {
        val itemList = mutableListOf<Pair<String,String>>()
        val client = OkHttpClient()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Error: $response")
            val body = response.body?.string()
            val gson = GsonBuilder().create()
            val parsedResponse = gson.fromJson(body, Reply::class.java)
            if (parsedResponse.totalResultsCount == 0){
                return emptyList()
            }
            for (CityInfo in parsedResponse.geonames){
                itemList.add(Pair(CityInfo.name.toUpperCase(), CityInfo.population.toString()))
            }
            return itemList
        }
    }

    fun fetchContent(state: State, query: String): List<Pair<String,String>>{
        return when(state){
            State.CITYVIEW -> fetchJson(citySearchRequest(query))
            State.COUNTRYVIEW -> fetchJson(countrySearchRequest(query))
        }
    }

    private fun countrySearchRequest(countryCode: String): Request{
        return Request.Builder().url(HttpUrl.Builder()
                .scheme("https")
                .host("secure.geonames.org")
                .addPathSegment("search")
                .addQueryParameter("country", countryCode)
                .addQueryParameter("featureClass", "P")
                .addQueryParameter("maxRows", "5")
                .addQueryParameter("type", "json")
                .addQueryParameter("orderby", "population")
                .addQueryParameter("username", "weknowit").build().toString()).build()
    }

    private fun citySearchRequest(city: String): Request{
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
}