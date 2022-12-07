package com.market.supermarket.dto

import java.io.Serializable
import java.math.BigDecimal

data class ShopProductResponse(
    val id: Long?,
    val titleFa: String?,
    val titleEn: String?,
    val description: String?,
    val rating: Int,
    val price: BigDecimal?,
    val categoryId: Long?,
    val categoryTitle: String?,
    val productId: Long?,
    val productTitle: String?,
    val vendorId: Long?,
    val vendorLat: BigDecimal?,
    val vendorLon: BigDecimal?,
    val brandId: Long?,
    val brand_fa: String?,
    val stock: Long?,
): Serializable {
}