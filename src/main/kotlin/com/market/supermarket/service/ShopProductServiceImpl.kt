package com.market.supermarket.service

import com.market.supermarket.SupermarketProps
import com.market.supermarket.dto.ShopProductPutRequest
import com.market.supermarket.dto.ShopProductResponse
import com.market.supermarket.exception.ShopProductBusinessException
import com.market.supermarket.model.ShopProduct
import com.market.supermarket.model.toDto
import com.market.supermarket.repository.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.stream.Collectors.groupingBy
import javax.transaction.Transactional

@Service
class ShopProductServiceImpl(
    private val shopProductRepository: ShopProductRepository,
    private val brandRepository: BrandRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val vendorRepository: VendorRepository,
    private val properties: SupermarketProps
): ShopProductService {
    @Transactional
    override fun registerShopProduct(shopProductRequest: ShopProductPutRequest): ShopProductResponse {
        return validateAndRegisterShopProduct(shopProductRequest).toDto()
    }

    internal fun validateAndRegisterShopProduct(shopProductRequest: ShopProductPutRequest): ShopProduct {
        val category = categoryRepository.findById(shopProductRequest.categoryId!!)
            .orElseThrow { ShopProductBusinessException("Category is not valid") }
        val brand = brandRepository.findById(shopProductRequest.brandId!!)
            .orElseThrow { ShopProductBusinessException("Brand is not valid") }
        val product = productRepository.findById(shopProductRequest.productId!!)
            .orElseThrow { ShopProductBusinessException("Product is not valid") }
        val vendor = validateVendor(shopProductRequest.vendorId!!)
        shopProductRepository.findByProductAndBrandAndCategoryAndVendor(product, brand, category, vendor)?.let {
            throw ShopProductBusinessException("Record is already existed")
        }

        val shopProduct = ShopProduct().apply {
            this.product = product
            this.brand = brand
            this.category = category
            this.vendor = vendor
            this.price = shopProductRequest.price
            this.stock = shopProductRequest.stock
            this.titleEn = shopProductRequest.titleEn
            this.titleFa = shopProductRequest.titleFa
            this.description = shopProductRequest.description
        }
        return shopProductRepository.save(shopProduct)
    }

    override fun getShopProduct(shopProductId: Long): ShopProductResponse {
        return shopProductRepository.findById(shopProductId).orElseThrow {
            ShopProductBusinessException("ShopProduct does not exist")
        }.toDto()
    }

    override fun getVendorShopProducts(vendorId: Long, page: Pageable?): Page<ShopProductResponse> {
        return validateAndGetVendorShopProducts(vendorId, page).map { it.toDto() }
    }

    internal fun validateAndGetVendorShopProducts(vendorId: Long, page: Pageable?): Page<ShopProduct> {
        validateVendor(vendorId)
        return shopProductRepository.findAllByVendorId(vendorId, page)
    }

    private fun validateVendor(vendorId: Long) =
        vendorRepository.findById(vendorId)
            .orElseThrow { ShopProductBusinessException("Vendor is not valid") }

    override fun getVendorShopProductsGroupByCategory(
        vendorId: Long,
        page: Pageable
    ): Map<Long, List<ShopProductResponse>> {
        validateVendor(vendorId)
        val shopProductsOrderByCategory = shopProductRepository.findAllByVendorId(
            vendorId,
            PageRequest.of(page.pageNumber, page.pageSize, Sort.by(ShopProduct::category.name))
        ).toList()

        return shopProductsOrderByCategory.stream().map { it.toDto() }.collect(groupingBy(ShopProductResponse::categoryId))
    }

    override fun getNearbyShopProducts(lat: BigDecimal, lon: BigDecimal, page: Pageable): Page<ShopProductResponse> =
        shopProductRepository.getNearby(lat, lon, BigDecimal(properties.maxDistance), page).map { it.toDto() }

    @Transactional
    override fun purchaseShopProduct(shopProductId: Long): ShopProductResponse {
        val shopProduct = shopProductRepository.findByIdAndLock(shopProductId)?:
                            throw ShopProductBusinessException("ShopProduct does not exist")
        if (shopProduct.stock!! == 0L) throw ShopProductBusinessException("ShopProduct is out of stock")
        shopProduct.stock = shopProduct.stock!! - 1
        return shopProductRepository.save(shopProduct).toDto()
    }



}