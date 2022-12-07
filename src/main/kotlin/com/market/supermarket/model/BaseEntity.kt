package com.market.supermarket.model

import java.io.Serializable
import java.util.*
import javax.persistence.*

@MappedSuperclass
abstract class BaseEntity: Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id:Long? = null
    open var createdDate: Date? = null
    open var modifiedDate: Date? = null

    @PrePersist
    internal fun onPrePersist() {
        this.createdDate = Date()
        this.modifiedDate = Date()
    }

    @PreUpdate
    internal fun onPreUpdate() {
        this.modifiedDate = Date()
    }
}