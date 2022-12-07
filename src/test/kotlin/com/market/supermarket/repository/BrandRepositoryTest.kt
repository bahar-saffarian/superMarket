package com.market.supermarket.repository

import com.market.supermarket.model.Brand
import org.assertj.core.api.AssertionsForClassTypes
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException

@DataJpaTest
class BrandRepositoryTest {
    @Autowired
    private var underTest: BrandRepository? = null

    @BeforeEach
    fun tearDown() = underTest!!.deleteAll()

    @Test
    fun itShouldSaveBrand() {
        //given
        val brand = Brand().apply { this.titleFa = "برند" }

        //when
        underTest!!.saveAndFlush(brand)

        //then
        assertThat(brand.id).isNotNull
        assertThat(brand.createdDate).isNotNull
        val loadedBrand = underTest!!.findById(brand.id!!)
        assertThat(loadedBrand.isPresent).isTrue
        assertThat(loadedBrand.get()).isEqualTo(brand)
    }

    @Test
    fun itShouldKeepBrandTitleUnique() {
        //given
        val brand = Brand().apply { this.titleFa = "برند" }
        val duplicatedBrand = Brand().apply { this.titleFa = "برند" }

        //when
        underTest!!.save(brand)

        //then
        assertThatThrownBy { underTest!!.saveAndFlush(duplicatedBrand) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    fun itShouldRejectNullValueForTitle() {

        assertThatThrownBy { underTest!!.saveAndFlush(Brand()) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    fun itShouldGetAllSavedBrands() {
        //given
        val brand = Brand().apply { this.titleFa = "برند" }
        val brand1 = Brand().apply { this.titleFa = "برند۱" }
        val brand2 = Brand().apply { this.titleFa = "برند۲" }
        underTest!!.save(brand)
        underTest!!.save(brand1)
        underTest!!.save(brand2)
        underTest!!.flush()

        //when
        val allCats = underTest!!.findAll()

        //then
        assertThat(allCats.stream().anyMatch{it.equals(brand)}).isTrue
        assertThat(allCats.stream().anyMatch{it.equals(brand1)}).isTrue
        assertThat(allCats.stream().anyMatch{it.equals(brand2)}).isTrue
        assertThat(allCats.size).isEqualTo(3)
    }

}