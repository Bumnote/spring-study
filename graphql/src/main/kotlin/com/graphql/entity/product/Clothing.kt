package com.graphql.entity.product

import com.graphql.entity.product.ProductType.CLOTHING

data class Clothing(
    override val id: String,
    override val name: String,
    override val price: Double,
    val size: String
) : Product {

    override val productType: ProductType = CLOTHING
}
