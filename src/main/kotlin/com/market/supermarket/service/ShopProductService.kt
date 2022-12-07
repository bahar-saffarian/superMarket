package com.market.supermarket.service

import com.market.supermarket.dto.ShopProductPutRequest
import com.market.supermarket.dto.ShopProductResponse
import com.market.supermarket.model.Category
import com.market.supermarket.model.ShopProduct
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

interface ShopProductService {
    fun registerShopProduct(shopProductRequest: ShopProductPutRequest):ShopProductResponse

    fun getShopProduct(shopProductId: Long):ShopProductResponse

    fun getVendorShopProducts(vendorId: Long, page: Pageable?): Page<ShopProductResponse>

    fun getVendorShopProductsGroupByCategory(vendorId: Long, page: Pageable): Map<Long, List<ShopProductResponse>>

    fun getNearbyShopProducts(lat: BigDecimal, lon: BigDecimal, page: Pageable): Page<ShopProductResponse>

    fun purchaseShopProduct(shopProductId: Long):ShopProductResponse
}