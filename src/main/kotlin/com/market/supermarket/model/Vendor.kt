package com.market.supermarket.model

import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class Vendor:BaseEntity() {
    @Column(unique = true, nullable = false)
    var title:String? = null
    @Column(nullable = false)
    var lat: BigDecimal? = null
    @Column(nullable = false)
    var lon: BigDecimal? = null


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vendor

        if (id != other.id) return false
        if (title != other.title) return false
        if (lon != other.lon) return false
        if (lat != other.lat) return false

        return true
    }

}