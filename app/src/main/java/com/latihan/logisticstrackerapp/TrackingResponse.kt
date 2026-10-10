package com.latihan.logisticstrackerapp

import com.google.gson.annotations.SerializedName

// Model Data Transfer Object (DTO) Respons Pelacakan Paket
data class TrackingResponse(
    @SerializedName("tracking_number")
    val trackingNumber: String,

    @SerializedName("courier_name")
    val courierName: String,

    @SerializedName("service_type")
    val serviceType: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("last_location")
    val lastLocation: String,

    @SerializedName("estimated_delivery")
    val estimatedDelivery: String,

    @SerializedName("recipient_name")
    val recipientName: String
)
