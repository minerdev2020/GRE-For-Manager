package com.minerdev.greformanager

import java.util.*

/**
 * y는 latitude, 즉 위도,
 * x는 longitude, 즉 경도.
 */
class GeocodeResult {
    var status: String = ""
    var meta: Meta = Meta()
    var addresses: ArrayList<Address>? = null
    var errorMessage: String = ""

    class Meta {
        var totalCount: Int = 0
        var page: Int = 0
        var count: Int = 0
    }

    class Address {
        var roadAddress: String = ""
        var jibunAddress: String = ""
        var englishAddress: String = ""
        var addressElements: ArrayList<AddressElement>? = null
        var x // latitude
                : String = ""
        var y // longitude
                : String = ""
        var distance: Double = 0.0
    }

    class AddressElement {
        var types: ArrayList<String>? = null
        var longName: String = ""
        var shortName: String = ""
        var code: String = ""
    }
}