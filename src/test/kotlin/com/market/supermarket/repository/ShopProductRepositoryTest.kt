package com.market.supermarket.repository

import com.market.supermarket.model.*
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.math.BigDecimal

@DataJpaTest
class ShopProductRepositoryTest() {
    @Autowired private val underTest: ShopProductRepository? = null
    @Autowired private val brandRepository: BrandRepository? = null
    @Autowired private val categoryRepository: CategoryRepository? = null
    @Autowired private val productRepository: ProductRepository? = null
    @Autowired private val vendorRepository: VendorRepository? = null

    lateinit var brand: Brand
    lateinit var category: Category
    lateinit var product: Product
    lateinit var vendor: Vendor

    fun init() {
        brand = brandRepository!!.saveAndFlush(Brand().apply { this.titleFa = "لاواتزا" })
        category = categoryRepository!!.saveAndFlush(Category().apply { this.title = "قهوه" })
        product = productRepository!!.saveAndFlush(Product().apply { this.title = "پودر قهوه اسپرسو" })
        vendor = vendorRepository!!.saveAndFlush(Vendor().apply { this.title = "دارک مارکت"; this.lon = BigDecimal(3.4); this.lat = BigDecimal(4.4) })
    }

    fun tearDown() {
        underTest!!.deleteAll()
        brandRepository!!.deleteAll()
        categoryRepository!!.deleteAll()
        productRepository!!.deleteAll()
        vendorRepository!!.deleteAll()
    }

    @BeforeEach
    fun beforeEach() {
        tearDown()
        init()
    }

    @Test
    fun itShouldSaveShopProduct() {
        //given
        val shopProduct = getShopProduct()

        //when
        underTest!!.saveAndFlush(shopProduct)

        //then
        assertThat(shopProduct.id).isNotNull
        assertThat(shopProduct.createdDate).isNotNull
        val loadedShopProduct = underTest.findById(shopProduct.id!!)
        assertThat(loadedShopProduct.isPresent).isTrue
        assertThat(loadedShopProduct.get()).isEqualTo(shopProduct)
    }

    private fun getShopProduct() =
        ShopProduct().apply {
            this.titleFa = "پودر قهوه اسپرسو لاواتزا"
            this.price = BigDecimal(10000)
            this.stock = 100
            this.rating = 5
            this.brand = this@ShopProductRepositoryTest.brand
            this.category = this@ShopProductRepositoryTest.category
            this.product = this@ShopProductRepositoryTest.product
            this.vendor = this@ShopProductRepositoryTest.vendor
        }

    @Test
    fun itShouldRejectDuplicationForUniqueConstraints() {
        //given
        val shopProduct = getShopProduct()
        val shopProduct1 = getShopProduct()

        //when
        underTest!!.save(shopProduct)

        //then
        assertThatThrownBy { underTest.saveAndFlush(shopProduct1) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @ParameterizedTest
    @CsvSource(
        ",",
        ",2",
        "10000,"
    )
    fun itShouldRejectNullForRequiredValues(price: BigDecimal?, stock: Long?) {
        //given
        val shopProduct = getShopProduct()
        shopProduct.price = price
        shopProduct.stock = stock

        //then
        assertThatThrownBy { underTest!!.saveAndFlush(shopProduct) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    fun itShouldBeFindByUniqueElements() {
        //given
        val shopProduct = getShopProduct()
        underTest!!.saveAndFlush(shopProduct)

        //when
        val loadedShopProduct =
            underTest.findByProductAndBrandAndCategoryAndVendor(product, brand, category, vendor)

        //then
        assertThat(shopProduct).isEqualTo(loadedShopProduct)
    }

    @Test
    fun itShouldGetAllByVendor() {
        //given
        val brand2 = brandRepository!!.saveAndFlush(Brand().apply { this.titleFa = "لاواتزا2" })
        val category2 = categoryRepository!!.saveAndFlush(Category().apply { this.title = "2قهوه" })
        val product2 = productRepository!!.saveAndFlush(Product().apply { this.title = "پودر قهوه اسپرسو2" })
        val vendor2 = vendorRepository!!.saveAndFlush(Vendor().apply { this.title = "دارک مارکت2"; this.lon = BigDecimal(3.4); this.lat = BigDecimal(4.4) })
        val vendor3 = vendorRepository!!.saveAndFlush(Vendor().apply { this.title = "دارک مارکت3"; this.lon = BigDecimal(3.4); this.lat = BigDecimal(4.4) })
        val shopProductV1 = getShopProduct()
        val shopProduct2V1 = getShopProduct().apply { this.brand = brand2}
        val shopProduct3V1 = getShopProduct().apply { this.category = category2}
        val shopProduct4V1 = getShopProduct().apply { this.product = product2}
        val shopProductV2 = getShopProduct().apply { this.vendor = vendor2 }
        underTest!!.save(shopProductV1)
        underTest.save(shopProduct2V1)
        underTest.save(shopProduct3V1)
        underTest.save(shopProduct4V1)
        underTest.save(shopProductV2)

        //when
        val vendorShopProducts = underTest.findAllByVendorId(vendor.id!!, PageRequest.of(0,10)).toList()
        val vendor2ShopProducts = underTest.findAllByVendorId(vendor2.id!!, PageRequest.of(0,10)).toList()
        val vendor3ShopProducts = underTest.findAllByVendorId(vendor3.id!!, PageRequest.of(0,10)).toList()

        //then
        assertThat(vendorShopProducts.size).isEqualTo(4)
        assertThat(vendorShopProducts.contains(shopProductV1))
        assertThat(vendorShopProducts.contains(shopProduct2V1))
        assertThat(vendorShopProducts.contains(shopProduct3V1))
        assertThat(vendorShopProducts.contains(shopProduct4V1))

        assertThat(vendor2ShopProducts.size).isEqualTo(1)
        assertThat(vendor2ShopProducts.contains(shopProductV2))

        assertThat(vendor3ShopProducts.size).isEqualTo(0)

    }

    @Test
    fun itShouldGetAllByVendorWithoutPaging() {
        //given
        val brand2 = brandRepository!!.saveAndFlush(Brand().apply { this.titleFa = "لاواتزا2" })
        val category2 = categoryRepository!!.saveAndFlush(Category().apply { this.title = "2قهوه" })
        val product2 = productRepository!!.saveAndFlush(Product().apply { this.title = "پودر قهوه اسپرسو2" })
        val shopProductV1 = getShopProduct()
        val shopProduct2V1 = getShopProduct().apply { this.brand = brand2}
        val shopProduct3V1 = getShopProduct().apply { this.category = category2}
        val shopProduct4V1 = getShopProduct().apply { this.product = product2}
        underTest!!.save(shopProductV1)
        underTest.save(shopProduct2V1)
        underTest.save(shopProduct3V1)
        underTest.save(shopProduct4V1)

        //when
        val vendorShopProducts = underTest.findAllByVendorId(vendor.id!!, null).toList()


        //then
        assertThat(vendorShopProducts.size).isEqualTo(4)
        assertThat(vendorShopProducts.contains(shopProductV1))
        assertThat(vendorShopProducts.contains(shopProduct2V1))
        assertThat(vendorShopProducts.contains(shopProduct3V1))
        assertThat(vendorShopProducts.contains(shopProduct4V1))

    }


    @Test
    fun itShouldGetShopProductsByVendorOrderByRating() {
        //given
        val brand2 = brandRepository!!.saveAndFlush(Brand().apply { this.titleFa = "لاواتزا2" })
        val category2 = categoryRepository!!.saveAndFlush(Category().apply { this.title = "2قهوه" })
        val product2 = productRepository!!.saveAndFlush(Product().apply { this.title = "پودر قهوه اسپرسو2" })
        val shopProductRate1 = getShopProduct().apply { this.rating = 1 }
        val shopProductRate3 = getShopProduct().apply { this.product = product2; this.rating = 3}
        val shopProductRate4 = getShopProduct().apply { this.category = category2; this.rating = 4}
        val shopProductRate5 = getShopProduct().apply { this.brand = brand2; this.rating = 5}
        underTest!!.save(shopProductRate1)
        underTest.save(shopProductRate5)
        underTest.save(shopProductRate4)
        underTest.save(shopProductRate3)
        //when
        val shopProductsOrderByRating = underTest.
            findAllByVendorId(vendor.id!!, PageRequest.of(0,10, Sort.by("rating").descending())).toList()
        //then
        assertThat(shopProductsOrderByRating.size).isEqualTo(4)
        assertThat(shopProductsOrderByRating[0]).isEqualTo(shopProductRate5)
        assertThat(shopProductsOrderByRating[1]).isEqualTo(shopProductRate4)
        assertThat(shopProductsOrderByRating[2]).isEqualTo(shopProductRate3)
        assertThat(shopProductsOrderByRating[3]).isEqualTo(shopProductRate1)
    }

    @Test
    fun itShouldGetShopProductByVendorByPagination() {
        //given
        val brand2 = brandRepository!!.saveAndFlush(Brand().apply { this.titleFa = "لاواتزا2" })
        val category2 = categoryRepository!!.saveAndFlush(Category().apply { this.title = "2قهوه" })
        val product2 = productRepository!!.saveAndFlush(Product().apply { this.title = "پودر قهوه اسپرسو2" })
        val shopProductV1 = getShopProduct()
        val shopProduct2V1 = getShopProduct().apply { this.brand = brand2}
        val shopProduct3V1 = getShopProduct().apply { this.category = category2}
        val shopProduct4V1 = getShopProduct().apply { this.product = product2}
        underTest!!.save(shopProductV1)
        underTest.save(shopProduct2V1)
        underTest.save(shopProduct3V1)
        underTest.save(shopProduct4V1)

        //when
        val shopProducts = underTest.findAllByVendorId(vendor.id!!, PageRequest.of(0, 2)).toList()
        val shopProducts2 = underTest.findAllByVendorId(vendor.id!!, PageRequest.of(1, 2)).toList()

        //then
        assertThat(shopProducts.size).isEqualTo(2)
        assertThat(shopProducts2.size).isEqualTo(2)
        assertThat(shopProducts.stream().allMatch {
                shopProduct ->
                    shopProducts2.stream().allMatch {
                        !it.equals(shopProduct)
                    }
        }).isTrue
    }

    @Test
    fun itShouldGetShopProductsByVendorOrderByCategory() {
        //given
        val brand2 = brandRepository!!.saveAndFlush(Brand().apply { this.titleFa = "لاواتزا2" })
        val category2 = categoryRepository!!.saveAndFlush(Category().apply { this.title = "2قهوه" })
        val product2 = productRepository!!.saveAndFlush(Product().apply { this.title = "پودر قهوه اسپرسو2" })
        val shopProductCategory1 = getShopProduct()
        val shopProduct2Category1 = getShopProduct().apply { this.product = product2}
        val shopProductCategory2 = getShopProduct().apply { this.product = product2; this.category = category2;}
        val shopProduct2Category2 = getShopProduct().apply { this.brand = brand2; this.category = category2;}
        underTest!!.save(shopProductCategory1)
        underTest.save(shopProductCategory2)
        underTest.save(shopProduct2Category1)
        underTest.save(shopProduct2Category2)
        //when
        val shopProductsOrderByCategory = underTest.
            findAllByVendorId(vendor.id!!, PageRequest.of(0,10, Sort.by("category"))).toList()
        //then
        assertThat(shopProductsOrderByCategory.size).isEqualTo(4)
        assertThat(shopProductsOrderByCategory[0].category).isEqualTo(category)
        assertThat(shopProductsOrderByCategory[1].category).isEqualTo(category)
        assertThat(shopProductsOrderByCategory[2].category).isEqualTo(category2)
        assertThat(shopProductsOrderByCategory[3].category).isEqualTo(category2)
    }

    @Test
    fun itShouldFindAndLockShopProduct() {
        //given
        val shopProduct = getShopProduct()
        underTest!!.saveAndFlush(shopProduct)
        //when
        val loadedShopProduct = underTest.findByIdAndLock(shopProduct.id!!)
        //then
        assertThat(shopProduct).isEqualTo(loadedShopProduct)
    }

    @Test
    fun itShouldNotFindAndLockShopProductForNotSavedProduct() {
        //when
        val loadedShopProduct = underTest!!.findByIdAndLock(2)
        //then
        assertThat(loadedShopProduct).isNull()
    }

    @Test
    fun itShouldGetNearByProducts() {
        //given
        val vendor2 = vendorRepository!!.saveAndFlush(Vendor()
            .apply { this.title = "رفاه"; this.lat = BigDecimal(3.3); this.lon = BigDecimal(4.3) })
        val shopProduct1 = getShopProduct()
        val shopProduct2 = getShopProduct().apply { this.vendor = vendor2}
        underTest!!.saveAndFlush(shopProduct1)
        underTest.saveAndFlush(shopProduct2)
        //when
        val nearbyShopProducts = underTest.getNearby(
            vendor2.lat!!, vendor2.lon!!,
            BigDecimal(1000), PageRequest.of(0, 10, Sort.by("distance"))
        ).toList()
        //then
        assertThat(nearbyShopProducts.size).isEqualTo(2)
        assertThat(nearbyShopProducts[0].id).isEqualTo(shopProduct2.id)
    }
}