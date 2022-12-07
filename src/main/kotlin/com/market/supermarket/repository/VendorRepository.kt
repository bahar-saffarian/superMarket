package com.market.supermarket.repository

import com.market.supermarket.model.Vendor
import org.springframework.data.jpa.repository.JpaRepository

interface VendorRepository: JpaRepository<Vendor, Long> {
}