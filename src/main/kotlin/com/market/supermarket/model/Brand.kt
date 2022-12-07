package com.market.supermarket.model

import javax.persistence.Column
import javax.persistence.Entity

@Entity
class Brand: BaseEntity() {
    @Column(unique = true, nullable = false)
    var titleFa:String? = null


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Brand

        if (id != other.id) return false
        if (titleFa != other.titleFa) return false

        return true
    }

}