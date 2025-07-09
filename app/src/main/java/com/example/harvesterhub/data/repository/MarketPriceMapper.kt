package com.example.harvesterhub.data.repository

import MarketPriceResponse
import com.example.harvesterhub.data.models.MarketPriceEntity
import java.text.SimpleDateFormat
import java.util.Locale

fun MarketPriceResponse.toEntity(): MarketPriceEntity {
//    val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
//    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//    val parsedDate = inputFormat.parse(arrivalDate)   // arrivalDate = "15-06-2025"
//    val normalizedDate = outputFormat.format(parsedDate!!)

    return MarketPriceEntity(
        state = state,
        district = district,
        market = market,
        commodity = commodity,
        variety = variety,
        grade = grade,
        arrivalDate = arrivalDate,
        minPrice = minPrice,
        maxPrice = maxPrice,
        modalPrice = modalPrice,
        commodityCode = commodityCode
    )
}

fun MarketPriceEntity.toResponse(): MarketPriceResponse {
    return MarketPriceResponse(
        state = state,
        district = district,
        market = market,
        commodity = commodity,
        variety = variety,
        grade = grade,
        arrivalDate = arrivalDate,
        minPrice = minPrice,
        maxPrice = maxPrice,
        modalPrice = modalPrice,
        commodityCode = commodityCode
    )
}

