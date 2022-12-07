package com.market.supermarket.repository

import com.market.supermarket.model.Product
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private var underTest: ProductRepository? = null

    @BeforeEach
    fun tearDown() = underTest!!.deleteAll()

    @Test
    fun itShouldSaveAProduct() {
        //given
        val product = Product().apply { this.title = "Product" }

        //when
        underTest!!.saveAndFlush(product)

        //then
        assertThat(product.id).isNotNull
        assertThat(product.createdDate).isNotNull
        val loadedProduct = underTest!!.findById(product.id!!)
        assertThat(loadedProduct.isPresent).isTrue
        assertThat(loadedProduct.get()).isEqualTo(product)
    }

    @Test
    fun itShouldKeepProductTitleUnique() {
        //given
        val product = Product().apply { this.title = "Product" }
        val duplicatedProduct = Product().apply { this.title = "Product" }
        underTest!!.save(product)

        //then
        assertThatThrownBy { underTest!!.saveAndFlush(duplicatedProduct) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    fun itShouldRejectNullValueForTitle() {

        //then
        assertThatThrownBy { underTest!!.saveAndFlush(Product()) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    fun itShouldRetrieveAllSavedProducts() {
        //give
        val product = Product().apply { this.title = "Product" }
        val product1 = Product().apply { this.title = "Product1" }
        val product2 = Product().apply { this.title = "Product2" }
        underTest!!.save(product)
        underTest!!.save(product1)
        underTest!!.save(product2)
        underTest!!.flush()

        //when
        val allProducts = underTest!!.findAll()

        //then
        assertThat(allProducts.contains(product)).isTrue
        assertThat(allProducts.contains(product1)).isTrue
        assertThat(allProducts.contains(product2)).isTrue
        assertThat(allProducts.size).isEqualTo(3)

    }

}