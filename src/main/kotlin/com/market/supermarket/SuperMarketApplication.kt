package com.market.supermarket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class SuperMarketApplication

fun main(args: Array<String>) {
    runApplication<SuperMarketApplication>(*args)
}
