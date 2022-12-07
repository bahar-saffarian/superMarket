package com.market.supermarket

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import javax.validation.constraints.Min

@Component
@ConfigurationProperties(prefix = "supermarket.properties")
class SupermarketProps {
    @Min(value = 0)
    var maxDistance = 100000
}