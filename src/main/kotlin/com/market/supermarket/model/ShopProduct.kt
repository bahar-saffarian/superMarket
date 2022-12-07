package com.market.supermarket.model

import com.market.supermarket.dto.ShopProductResponse
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(uniqueConstraints= [UniqueConstraint(columnNames=arrayOf("product_id", "vendor_id", "category_id", "brand_id"))])
class ShopProduct: BaseEntity() {
    @Column(nullable = false)
    var titleFa: String? = null
    var titleEn: String? = null
    var description: String? = null
    var rating: Int = 0
    @Column(nullable = false)
    var price: BigDecimal? = null
    @ManyToOne
    var category: Category? = null
    @ManyToOne
    var product: Product? = null
    @ManyToOne
    var vendor: Vendor? = null
    @ManyToOne
    var brand: Brand? = null
    @Column(nullable = false)
    var stock: Long? = null
}

fun ShopProduct.toDto() = ShopProductResponse(
    this.id,
    this.titleFa,
    this.titleEn,
    this.description,
    this.rating,
    this.price,
    this.category?.id,
    this.category?.title,
    this.product?.id,
    this.product?.title,
    this.vendor?.id,
    this.vendor?.lat,
    this.vendor?.lon,
    this.brand?.id,
    this.brand?.titleFa,
    this.stock
)