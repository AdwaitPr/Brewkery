package com.clickretina.brewkery.data.remote

import com.clickretina.brewkery.data.remote.dto.MenuItemDto
import com.clickretina.brewkery.data.remote.dto.MenuResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface BrewkeryApiService {

    @GET("data.json")
    suspend fun getMenuData(): MenuResponseDto

    @GET("api/items/{id}.json")
    suspend fun getItemDetail(@Path("id") id: Int): MenuItemDto

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com/VivekShah138/Brewkery/main/"
    }
}
