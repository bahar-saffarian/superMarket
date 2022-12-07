package com.market.supermarket.repository

import com.market.supermarket.model.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal
import javax.persistence.LockModeType

interface ShopProductRepository: JpaRepository<ShopProduct, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select shp from ShopProduct shp where shp.id = :id")
    fun findByIdAndLock(@Param("id") id: Long): ShopProduct?

    fun findByProductAndBrandAndCategoryAndVendor(product: Product, brand: Brand, category: Category, vendor: Vendor): ShopProduct?

    fun findAllByVendorId(vendor: Long, pageable: Pageable?): Page<ShopProduct>
    @Query(
        "SELECT * FROM ( \n" +
        "SELECT " +
                "shopProduct.*, " +
                "(\n" +
                "      6371 * acos (\n" +
                "      cos ( radians(:userLat) )\n" +
                "      * cos( radians( vendor.lat ) )\n" +
                "      * cos( radians( vendor.lon ) - radians(:userLon) )\n" +
                "      + sin ( radians(:userLat) )\n" +
                "      * sin( radians( vendor.lat ) )\n" +
                "    )\n" +
                ") AS distance \n" +
                "FROM Shop_Product shopProduct inner join Vendor vendor on(shopProduct.vendor_id = vendor.id)\n" +
                "GROUP BY (shopProduct.id) \n" +
                "HAVING distance < :maxDistance\n" +
        ") AS shopProduct",
        countQuery = "select count(*) from \n" +
                "(SELECT " +
                "shopProduct.id, " +
                "(\n" +
                "      6371 * acos (\n" +
                "      cos ( radians(:userLat) )\n" +
                "      * cos( radians( vendor.lat ) )\n" +
                "      * cos( radians( vendor.lon ) - radians(:userLon) )\n" +
                "      + sin ( radians(:userLat) )\n" +
                "      * sin( radians( vendor.lat ) )\n" +
                "    )\n" +
                ") AS distance \n" +
                "FROM Shop_Product shopProduct inner join Vendor vendor on(shopProduct.vendor_id = vendor.id)\n" +
                "GROUP BY (shopProduct.id) \n" +
                "HAVING distance < :maxDistance)\n",
        nativeQuery = true
    )
    fun getNearby(@Param("userLat") userLat:BigDecimal, @Param("userLon") userLon:BigDecimal,
                  @Param("maxDistance") maxDistance: BigDecimal, pageable: Pageable): Page<ShopProduct>
}