package com.example.citysearch.dataclasses

/**
 * Data class holding the information of a Reply-object - the response received from API call to
 * GeoNames API (http://secure.geonames.org/search)
 */

data class Reply (val totalResultsCount: Int, val geonames: List<CityInfo> )