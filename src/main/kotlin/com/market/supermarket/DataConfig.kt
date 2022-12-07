package com.market.supermarket

import com.market.supermarket.model.*
import com.market.supermarket.repository.*
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.math.BigDecimal

@Configuration
class DataConfig {
    @Bean
    fun baseDataInitializer(
        shopProductRepository: ShopProductRepository,
        brandRepository: BrandRepository,
        categoryRepository: CategoryRepository,
        productRepository: ProductRepository,
        vendorRepository: VendorRepository,
    ) = CommandLineRunner {
        val brand = Brand().apply { this.titleFa = "لاواتزا" }
        val brand2 = Brand().apply { this.titleFa = "استارباکس" }
        brandRepository.saveAndFlush(brand)
        brandRepository.saveAndFlush(brand2)

        val category = Category().apply { this.title = "قهوه" }
        val category2 = Category().apply { this.title = "ماگ" }
        categoryRepository.saveAndFlush(category)
        categoryRepository.saveAndFlush(category2)

        val product = Product().apply { this.title = "پودر قهوه اسپرسو" }
        val product2 = Product().apply { this.title = "پودر قهوه فوری" }
        productRepository.saveAndFlush(product)
        productRepository.saveAndFlush(product2)

        val vendor = Vendor().apply { this.title = "دارک مارکت"; this.lon = BigDecimal(3.4); this.lat = BigDecimal(4.4) }
        val vendor2 = Vendor().apply { this.title = "فروشگاه رفاه"; this.lon = BigDecimal(3.39); this.lat = BigDecimal(4.3) }
        vendorRepository.saveAndFlush(vendor)
        vendorRepository.saveAndFlush(vendor2)

        shopProductRepository.saveAndFlush(
        ShopProduct().apply {
            this.titleFa = "قهوه"
            this.rating = 3
            this.price = BigDecimal(200000)
            this.stock = 2
            this.vendor = vendor
            this.brand = brand
            this.category = category
            this.product = product
        }
        )
        shopProductRepository.saveAndFlush(
            ShopProduct().apply {
                this.titleFa = "2قهوه"
                this.rating = 5
                this.price = BigDecimal(200000)
                this.stock = 20
                this.vendor = vendor
                this.brand = brand2
                this.category = category2
                this.product = product2
            }
        )
        shopProductRepository.saveAndFlush(
            ShopProduct().apply {
                this.titleFa = "3قهوه"
                this.rating = 1
                this.price = BigDecimal(200000)
                this.stock = 20
                this.vendor = vendor
                this.brand = brand2
                this.category = category
                this.product = product2
            }
        )
        shopProductRepository.saveAndFlush(
            ShopProduct().apply {
                this.titleFa = "4قهوه"
                this.rating = 3
                this.price = BigDecimal(200000)
                this.stock = 20
                this.vendor = vendor2
                this.brand = brand
                this.category = category
                this.product = product
            }
        )
        shopProductRepository.saveAndFlush(
            ShopProduct().apply {
                this.titleFa = "قهوه5"
                this.rating = 3
                this.price = BigDecimal(200000)
                this.stock = 20
                this.vendor = vendor2
                this.brand = brand
                this.category = category2
                this.product = product
            }
        )
    }

}