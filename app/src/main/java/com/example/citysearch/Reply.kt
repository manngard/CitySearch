package com.example.citysearch

/**
 * Data class holding the information of a Reply-object
 */

data class Reply (val totalResultsCount: Int, val geonames: List<CityInfo> )