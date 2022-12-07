package com.market.supermarket.repository

import com.market.supermarket.model.Category
import org.assertj.core.api.AssertionsForClassTypes.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException

@DataJpaTest
class CategoryRepositoryTest {
    @Autowired
    private var underTest: CategoryRepository? = null

    @BeforeEach
    fun tearDown() = underTest!!.deleteAll()

    @Test
    fun itShouldSaveCategory() {
        //given
        val category = Category().apply { this.title = "title" }

        //when
        underTest!!.saveAndFlush(category)

        //then
        assertThat(category.id).isNotNull
        assertThat(category.createdDate).isNotNull
        val retrievedCategory = underTest!!.findById(category.id!!)
        assertThat(retrievedCategory.isPresent).isTrue
        assertThat(retrievedCategory.get()).isEqualTo(category)
    }


    @Test
    fun itShouldKeepCategoryTitleUnique() {
        //given
        val category = Category().apply { this.title = "title" }
        val duplicatedCategory = Category().apply { this.title = "title" }

        //when
        underTest!!.save(category)

        //then
        assertThatThrownBy { underTest!!.saveAndFlush(duplicatedCategory) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    fun itShouldRejectNullValueForTitle() {

        assertThatThrownBy { underTest!!.saveAndFlush(Category()) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    fun itShouldGetAllSavedCategories() {
        //given
        val cat = Category().apply { this.title = "title" }
        val cat1 = Category().apply { this.title = "title1" }
        val cat2 = Category().apply { this.title = "title2" }
        underTest!!.save(cat)
        underTest!!.save(cat1)
        underTest!!.save(cat2)
        underTest!!.flush()

        //when
        val allCats = underTest!!.findAll()

        //then
        assertThat(allCats.stream().anyMatch{it.equals(cat)}).isTrue
        assertThat(allCats.stream().anyMatch{it.equals(cat1)}).isTrue
        assertThat(allCats.stream().anyMatch{it.equals(cat2)}).isTrue
        assertThat(allCats.size).isEqualTo(3)
    }

}