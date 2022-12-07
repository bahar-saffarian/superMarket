package com.market.supermarket.dto

import java.math.BigDecimal
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

class ShopProductPutRequest {
    @NotNull
    var titleFa: String? = null
    var titleEn: String? = null
    var description: String? = null
    var rating: Int = 0
    @NotNull(message = "price is required")
    var price: BigDecimal? = null
    @NotNull(message = "categoryId is required")
    var categoryId: Long? = null
    @NotNull(message = "productId is required")
    var productId: Long? = null
    @NotNull(message = "vendorId is required")
    var vendorId: Long? = null
    @NotNull(message = "brandId is required")
    var brandId: Long? = null
    @NotNull(message = "stock is required")
    @Min(0)
    var stock: Long? = null
}
