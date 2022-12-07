package com.market.supermarket.repository

import com.market.supermarket.model.Vendor
import org.assertj.core.api.AssertionsForClassTypes
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import java.math.BigDecimal

@DataJpaTest
class VendorRepositoryTest {
    @Autowired
    private var underTest: VendorRepository? = null

    @BeforeEach
    fun tearDown() = underTest!!.deleteAll()

    @Test
    fun itShouldSaveVendor() {
        //given
        val vendor = Vendor().apply { this.title = "title"
            this.lon = BigDecimal(35.111234)
            this.lat = BigDecimal(51.111234)
        }

        //when
        underTest!!.saveAndFlush(vendor)

        //then
        assertThat(vendor.id).isNotNull
        assertThat(vendor.createdDate).isNotNull
        val loadedVendor = underTest!!.findById(vendor.id!!)
        assertThat(loadedVendor.isPresent).isTrue
        assertThat(loadedVendor.get()).isEqualTo(vendor)
    }

    @Test
    fun itShouldKeepVendorTitleUnique() {
        //given
        val vendor = Vendor().apply { this.title = "title"
            this.lon = BigDecimal(35.111234)
            this.lat = BigDecimal(51.111234)
        }
        val duplicatedVendor = Vendor().apply { this.title = "title"
            this.lon = BigDecimal(35.111234)
            this.lat = BigDecimal(51.111234)
        }

        //when
        underTest!!.save(vendor)

        //then
        AssertionsForClassTypes.assertThatThrownBy { underTest!!.saveAndFlush(duplicatedVendor) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @ParameterizedTest
    @CsvSource(
        ",,",
        ", 51.111234, 51.111234",
        ", 51.111234,",
        ", , 51.111234",
        "title,,",
        "title,51.113456,",
        "title,,51.113456"
    )
    fun itShouldRejectNullValueForRequiredFields(title: String?, lon: BigDecimal?, lat: BigDecimal?) {
        //given
        val vendor = Vendor().apply {
            this.title = title
            this.lon = lon
            this.lat = lat
        }

        AssertionsForClassTypes.assertThatThrownBy { underTest!!.saveAndFlush(vendor) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    fun itShouldGetAllSavedVendors() {
        //given
        val vendor = Vendor().apply { this.title = "title"
            this.lon = BigDecimal(51.111234)
            this.lat = BigDecimal(51.111234)
        }
        val vendor1 = Vendor().apply { this.title = "title1"
            this.lon = BigDecimal(51.111234)
            this.lat = BigDecimal(51.111234)
        }
        val vendor2 = Vendor().apply { this.title = "title2"
            this.lon = BigDecimal(51.111234)
            this.lat = BigDecimal(51.111234)
        }
        underTest!!.save(vendor)
        underTest!!.save(vendor1)
        underTest!!.save(vendor2)
        underTest!!.flush()

        //when
        val allCats = underTest!!.findAll()

        //then
        assertThat(allCats.stream().anyMatch{it.equals(vendor)}).isTrue
        assertThat(allCats.stream().anyMatch{it.equals(vendor1)}).isTrue
        assertThat(allCats.stream().anyMatch{it.equals(vendor2)}).isTrue
        assertThat(allCats.size).isEqualTo(3)
    }

}