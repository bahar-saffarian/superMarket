package com.market.supermarket.controller

import com.market.supermarket.dto.ShopProductPutRequest
import com.market.supermarket.dto.ShopProductResponse
import com.market.supermarket.model.Category
import com.market.supermarket.model.ShopProduct
import com.market.supermarket.service.ShopProductService
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import javax.validation.Valid

@RestController
@RequestMapping("api/v1/shopProduct")
class ShopProductController(
    private val shopProductService: ShopProductService
) {

    @PostMapping
    fun registerShopProduct(@Valid @RequestBody shopProductRequest: ShopProductPutRequest): ResponseEntity<ShopProductResponse> =
        ResponseEntity(shopProductService.registerShopProduct(shopProductRequest), HttpStatus.CREATED)

    @GetMapping("/{shopProductId}")
    fun getShopProduct(@PathVariable shopProductId: Long) =
        ResponseEntity.ok(shopProductService.getShopProduct(shopProductId))

    @GetMapping("/vendor/{vendorId}")
    fun getVendorShopProducts(@PathVariable vendorId: Long, page: Pageable): Page<ShopProductResponse> =
        shopProductService.getVendorShopProducts(vendorId, page)

    @Cacheable(value = ["shopProducts"], key = "#vendorId")
    @GetMapping("/cacheable/vendor/{vendorId}")
    fun getVendorShopProductsWithCaching(@PathVariable vendorId: Long): Page<ShopProductResponse> =
        shopProductService.getVendorShopProducts(vendorId, null)

    @GetMapping("/vendor/groupByCategory/{vendorId}")
    fun getVendorShopProductsGroupByCategory(@PathVariable vendorId: Long, page: Pageable): ResponseEntity<Map<Long, List<ShopProductResponse>>> =
        ResponseEntity.ok(
            shopProductService.getVendorShopProductsGroupByCategory(vendorId, page)
        )

    @GetMapping("/nearby")
    fun getNearbyShopProducts(@RequestParam lat: BigDecimal, @RequestParam lon:BigDecimal, page: Pageable): Page<ShopProductResponse> =
        shopProductService.getNearbyShopProducts(lat, lon, page)

    @CacheEvict(value = ["shopProducts"], allEntries = true) //TODO: This preserves correctness but it should be changed to have a better caching performance
    @PutMapping("/purchase/{shopProductId}")
    fun purchaseProduct(@PathVariable shopProductId: Long) =
        ResponseEntity.ok(shopProductService.purchaseShopProduct(shopProductId))
}