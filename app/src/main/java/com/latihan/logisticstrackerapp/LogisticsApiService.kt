package com.latihan.logisticstrackerapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LogisticsApiService {

    // Mengambil data pelacakan berdasarkan nomor resi
    @GET("api/v1/tracking/{resi}")
    suspend fun getTrackingDetail(
        @Path("resi") resiNumber: String
    ): Response<TrackingResponse>

    // Endpoint pencarian menggunakan query parameter
    @GET("api/v1/tracking/search")
    suspend fun searchTracking(
        @Query("query") keyword: String
    ): Response<List<TrackingResponse>>
}
