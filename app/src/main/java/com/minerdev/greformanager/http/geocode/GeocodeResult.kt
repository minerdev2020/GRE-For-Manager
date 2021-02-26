package com.minerdev.greformanager.http.geocode

import kotlinx.serialization.Serializable
import java.util.*

/**
 * y는 latitude, 즉 위도,
 * x는 longitude, 즉 경도.
 */
@Serializable
data class GeocodeResult(
        var status: String = "",
        var meta: Meta,
        var addresses: List<Address>?,
        var errorMessage: String = ""
) {
    @Serializable
    data class Meta(
            var totalCount: Int = 0,
            var page: Int = 0,
            var count: Int = 0
    )

    @Serializable
    data class Address(
            var roadAddress: String = "",
            var jibunAddress: String = "",
            var englishAddress: String = "",
            var addressElements: ArrayList<AddressElement>? = null,
            // latitude
            var x: String = "",
            // longitude
            var y: String = "",
            var distance: Double = 0.0,
    )

    @Serializable
    data class AddressElement(
            var types: ArrayList<String>? = null,
            var longName: String = "",
            var shortName: String = "",
            var code: String = ""
    )
}