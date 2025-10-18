package com.graphql.entity.product

import com.graphql.entity.product.ProductType.ELECTRONICS

data class Electronics(
    override val id: String,
    override val name: String,
    override val price: Double,
    val warrantyPeriod: String
) : Product {

    override val productType: ProductType = ELECTRONICS
}
