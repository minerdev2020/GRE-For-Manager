package com.minerdev.greformanager

class HouseWrapper(val data: House) {
    val id: String
        get() = data.id.toString()

    val createdAt: String
        get() = AppHelper.instance.getDiffTimeMsg(data.created_at)

    val updatedAt: String
        get() = AppHelper.instance.getDiffTimeMsg(data.updated_at)

    val address: String
        get() = data.address ?: ""

    val number: String
        get() = data.number ?: ""

    val paymentType: String
        get() = Constants.instance.PAYMENT_TYPE[data.house_type - 1][data.payment_type.toInt()]

    val houseType: String
        get() = Constants.instance.HOUSE_TYPE[data.house_type.toInt()]

    val isFacility: Boolean
        get() = data.facility.toInt() == 1

    val price: String
        get() {
            return if (data.price == 0) {
                parsePrice(data.deposit) + "/" + parsePrice(data.monthly_rent)
            } else {
                parsePrice(data.price)
            }
        }

    val premium: String
        get() = parsePrice(data.premium)

    val manageFee: String
        get() = parsePrice(data.manage_fee)

    val manageFeeContains: String
        get() = data.manage_fee_contains ?: ""

    val areaMeter: String
        get() = data.area_meter.toString() + "㎡"

    val areaPyeong: String
        get() = String.format("%.2f", data.area_meter * Constants.instance.METER_TO_PYEONG) + "평"

    val rentAreaMeter: String
        get() = data.rent_area_meter.toString() + "㎡"

    val rentAreaPyeong: String
        get() = String.format("%.2f", data.rent_area_meter * Constants.instance.METER_TO_PYEONG) + "평"

    val area: String
        get() = "$areaMeter=$areaPyeong"

    val rentArea: String
        get() = "$rentAreaMeter=$rentAreaPyeong"

    val buildingFloor: String
        get() = data.building_floor.toString() + "층"

    val floor: String
        get() = if (data.floor > 0)
            data.floor.toString() + "층"
        else
            "반지하"

    val structure: String
        get() = Constants.instance.STRUCTURE.get(data.structure.toInt())

    val bathroom: String
        get() = Constants.instance.BATHROOM.get(data.bathroom.toInt())

    val bathroomLocation: String
        get() = if (data.bathroom_location.toInt() == 0)
            "외부"
        else
            "내부"

    val direction: String
        get() = Constants.instance.DIRECTION.get(data.direction.toInt())

    val builtDate: String
        get() = data.built_date ?: ""

    val moveDate: String
        get() = if (data.move_date == null || data.move_date == "")
            "즉시 입주 가능"
        else
            data.move_date!!

    val options: String
        get() = data.options ?: ""

    val detailInfo: String
        get() = data.detail_info ?: ""

    val phone: String
        get() = data.phone ?: ""

    var state: Boolean
        get() = data.state.toInt() == Constants.instance.SOLD
        set(isSoldOut) {
            data.state = (if (isSoldOut)
                Constants.instance.SOLD.toByte()
            else
                Constants.instance.SALE.toByte())
        }

    val briefInfo: String
        get() {
            var result = "전용 면적: $areaPyeong"
            if (data.manage_fee > 0) {
                result += " | 관리비: " + manageFee + "만원"
            }
            if (data.structure > 0) {
                result += " | 구조: $structure"
            }
            return result
        }

    val houseInfo: String
        get() = houseType + " | 관리비 " + manageFee + "만원 | " + structure + " | " + floor

    private fun parsePrice(price: Int): String {
        var result: String
        val frontNumber = price / 10000
        val backNumber = price - frontNumber * 10000

        if (frontNumber > 0) {
            result = frontNumber.toString() + "억"
            result += if (backNumber == 0) "" else " $backNumber"

        } else {
            result = price.toString()
        }

        return result
    }
}