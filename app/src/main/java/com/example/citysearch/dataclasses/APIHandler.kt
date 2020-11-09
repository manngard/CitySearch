package com.example.citysearch.dataclasses

import com.example.citysearch.State
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Class that makes API requests to GeoNames API (http://secure.geonames.org/search) and parses
 * the data for use by DetailActivity
 */

class APIHandler {

    /**
     * Fetches the wanted request as JSON-file and parses this file
     *
     * @param request - The Request-object representing the wanted API-request
     * @return - The List of Pairs where Pair.first is name of city and Pair.second is
     *           population of city or emptyList() if no results were found for the given Request
     */

    private fun fetchJson(request:Request): List<Pair<String,String>> {
        val itemList = mutableListOf<Pair<String,String>>()
        val client = OkHttpClient()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful){
                return emptyList()
            }
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

    /**
     * Fits the searched query into correct Request based on the state
     *
     * @param state - The state of page that is fetching content
     * @param query - The searched query given by user
     * @return - The List of Pairs where Pair.first is name of city and Pair.second is
     *           population of city or emptyList() if no results were found for the given query
     */

    fun fetchContent(state: State, query: String): List<Pair<String,String>>{
        return when(state){
            State.CITYVIEW -> fetchJson(citySearchRequest(query))
            State.COUNTRYVIEW -> fetchJson(countrySearchRequest(query))
        }
    }

    /**
     * @param countryCode - The countryCode representing the wanted country
     * @return - An Request to get the 5 most populated cities ordered by population
     *           in the country with countryCode
     */

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

    /**
     * @param city - The city more details is wanted for
     * @return - An Request to get the details of city
     */

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