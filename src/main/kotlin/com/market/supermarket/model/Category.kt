package com.market.supermarket.model

import javax.persistence.Column
import javax.persistence.Entity

@Entity
class Category: BaseEntity() {
    @Column(unique = true, nullable = false)
    var title:String? = null


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Category

        if (id != other.id) return false
        if (title != other.title) return false

        return true
    }

    override fun toString(): String {
        return "Category:{id=$id, title=$title}"
    }

}