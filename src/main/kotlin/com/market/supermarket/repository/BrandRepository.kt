package com.market.supermarket.repository

import com.market.supermarket.model.Brand
import org.springframework.data.jpa.repository.JpaRepository

interface BrandRepository: JpaRepository<Brand, Long> {
}