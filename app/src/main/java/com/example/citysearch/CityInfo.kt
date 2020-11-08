package com.example.citysearch

/**
 * Data class holding the information of a CityInfo-object
 */

data class CityInfo(val adminCode1: Int, val lng: Double, val geonameId: Int, val toponymName: String,
                    val countryId: Int, val fcl: String, val population: Long, val countryCode: String,
                    val name: String, val fclName: String, val adminCodes1: AdminCodes1,
                    val countryName: String, val fcodeName: String, val adminName1: String,
                    val lat: Double, val fcode: String )