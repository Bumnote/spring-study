package com.graphql.entity.cart

import com.graphql.entity.product.Product

data class CartItem(
    val id: String,
    val quantity: Int,
    val product: Product,
    val cart: Cart
)
