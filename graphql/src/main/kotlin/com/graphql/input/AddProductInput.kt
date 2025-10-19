package com.graphql.input

import com.graphql.entity.product.ProductType

class AddProductInput(
    val name: String,
    val price: Double,
    val productType: ProductType,

    val warrantyPeriod: String?,
    val size: String?
) {
}