package com.graphql.entity.cart

import com.graphql.entity.user.User

data class Cart(
    val id: String,
    val user: User
) {
    var totalAmount: Double = 0.0
    var items: List<CartItem> = listOf()
}

