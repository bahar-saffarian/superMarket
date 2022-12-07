package com.market.supermarket.sevice

import com.market.supermarket.SupermarketProps
import com.market.supermarket.dto.ShopProductPutRequest
import com.market.supermarket.exception.ShopProductBusinessException
import com.market.supermarket.model.*
import com.market.supermarket.repository.*
import com.market.supermarket.service.ShopProductServiceImpl
import org.assertj.core.api.AssertionsForClassTypes
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.never
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.data.domain.PageRequest
import java.math.BigDecimal
import java.util.*
import java.util.stream.Stream


@ExtendWith(MockitoExtension::class)
class ShopProductServiceTest {

    @Mock private var shopProductRepository: ShopProductRepository? = null
    @Mock private var brandRepository: BrandRepository? = null
    @Mock private var categoryRepository: CategoryRepository? = null
    @Mock private var productRepository: ProductRepository? = null
    @Mock private var vendorRepository: VendorRepository? = null
    @Mock private var supermarketProps: SupermarketProps? = null

    private var underTest: ShopProductServiceImpl? = null

    @BeforeEach
    fun setUp() {
        underTest = ShopProductServiceImpl(
            shopProductRepository!!,
            brandRepository!!,
            categoryRepository!!,
            productRepository!!,
            vendorRepository!!,
            supermarketProps!!,
        )
    }

    @Test
    fun canRegisterShopProduct() {
        //give
        val request = getShopProductRequest()
        given(categoryRepository!!.findById(request.categoryId!!)).willReturn(Optional.of(Category()))
        given(brandRepository!!.findById(request.brandId!!)).willReturn(Optional.of(Brand()))
        given(productRepository!!.findById(request.productId!!)).willReturn(Optional.of(Product()))
        given(vendorRepository!!.findById(request.vendorId!!)).willReturn(Optional.of(Vendor()))
        given(shopProductRepository!!.save(any())).willReturn(ShopProduct())

        //when
        underTest!!.validateAndRegisterShopProduct(request)

        //then
        verify(shopProductRepository)!!.save(any())
    }

    private fun getShopProductRequest() =
        ShopProductPutRequest().apply {
            this.titleFa = "پودر قهوه اسپرسو"
            this.description = "توضیحات"
            this.rating = 5
            this.price = BigDecimal(1000)
            this.stock = 10
            this.productId = 1
            this.brandId = 1
            this.categoryId = 1
            this.vendorId = 1
        }

    internal class RegisterArgumentsProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(null, Brand(), Product(), Vendor()),
                Arguments.of(Category(), null, Product(), Vendor()),
                Arguments.of(Category(), Brand(), null, Vendor()),
                Arguments.of(Category(), Brand(), Product(), null),
            )
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RegisterArgumentsProvider::class)
    fun shouldRejectWrongObjectsId(category: Category?, brand: Brand?, product: Product?, vendor: Vendor?) {
        //give
        val request = getShopProductRequest()
        given(categoryRepository!!.findById(request.categoryId!!)).willReturn(Optional.ofNullable(category))
        if (category != null)
            given(brandRepository!!.findById(request.brandId!!)).willReturn(Optional.ofNullable(brand))
        if (category != null && brand != null)
            given(productRepository!!.findById(request.productId!!)).willReturn(Optional.ofNullable(product))
        if (category != null && brand != null && product != null)
            given(vendorRepository!!.findById(request.vendorId!!)).willReturn(Optional.ofNullable(vendor))

        //then
        AssertionsForClassTypes.assertThatThrownBy { underTest!!.registerShopProduct(request) }
            .isInstanceOf(ShopProductBusinessException::class.java)
        verify(shopProductRepository, never())!!.save(any())
    }

    @Test
    fun shouldRejectDuplicatedRecord() {
        //give
        val request = getShopProductRequest()
        val category = Category()
        val brand = Brand()
        val product = Product()
        val vendor = Vendor()

        given(categoryRepository!!.findById(request.categoryId!!)).willReturn(Optional.of(category))
        given(brandRepository!!.findById(request.brandId!!)).willReturn(Optional.of(brand))
        given(productRepository!!.findById(request.productId!!)).willReturn(Optional.of(product))
        given(vendorRepository!!.findById(request.vendorId!!)).willReturn(Optional.of(vendor))
        given(shopProductRepository!!.findByProductAndBrandAndCategoryAndVendor(product, brand, category, vendor))
            .willReturn(ShopProduct())

        //then
        AssertionsForClassTypes.assertThatThrownBy { underTest!!.registerShopProduct(request) }
            .isInstanceOf(ShopProductBusinessException::class.java)
        verify(shopProductRepository, never())!!.save(any())
    }

    @Test
    fun itShouldGetShopProduct() {
        //given
        val shopProductId = 1L
        given(shopProductRepository!!.findById(shopProductId)).willReturn(Optional.of(ShopProduct()))

        //when
        underTest!!.getShopProduct(shopProductId)

        //then
        verify(shopProductRepository)!!.findById(shopProductId)
    }

    @Test
    fun itShouldNotGetNotExistedShopProduct() {
        //given
        val shopProductId = 1L
        given(shopProductRepository!!.findById(shopProductId)).willReturn(Optional.ofNullable(null))

        //then
        AssertionsForClassTypes.assertThatThrownBy { underTest!!.getShopProduct(shopProductId) }
            .isInstanceOf(ShopProductBusinessException::class.java)
    }

    @Test
    fun itShouldNotGetByVendorForNotExistedVendorId() {
        //given
        val vendorId = 1L
        given(vendorRepository!!.findById(vendorId)).willReturn(Optional.ofNullable(null))

        //then
        AssertionsForClassTypes.assertThatThrownBy { underTest!!.getVendorShopProducts(vendorId, PageRequest.of(0,5)) }
            .isInstanceOf(ShopProductBusinessException::class.java)
    }

    @Test
    fun itShouldGetAVendorShopProducts() {
        //given
        val vendorId = 1L
        val page = PageRequest.of(0, 5)
        given(vendorRepository!!.findById(vendorId)).willReturn(Optional.of(Vendor()))

        //when
        underTest!!.validateAndGetVendorShopProducts(vendorId, page)

        //then
        verify(shopProductRepository)!!.findAllByVendorId(vendorId, page)
    }

    @Test
    fun itShouldNotGetByVendorGroupByCategoryForNotExistedVendorId() {
        //given
        val vendorId = 1L
        given(vendorRepository!!.findById(vendorId)).willReturn(Optional.ofNullable(null))

        //then
        AssertionsForClassTypes.assertThatThrownBy { underTest!!.getVendorShopProductsGroupByCategory(vendorId, PageRequest.of(0,5)) }
            .isInstanceOf(ShopProductBusinessException::class.java)
    }

    @Test
    fun itShouldPurchaseFromShopProduct() {
        //given
        val shopProductId = 1L
        val shopProduct = ShopProduct().apply { stock = 2 }
        given(shopProductRepository!!.findByIdAndLock(shopProductId)).willReturn(shopProduct)
        given(shopProductRepository!!.save(shopProduct)).willReturn(shopProduct)
        //when
        val purchasedShopProduct = underTest!!.purchaseShopProduct(shopProductId)
        //then
        verify(shopProductRepository)!!.save(any())
        assertThat(purchasedShopProduct.stock).isEqualTo(1)
    }
}