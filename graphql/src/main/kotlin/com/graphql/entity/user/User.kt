package com.graphql.entity.user

import com.graphql.entity.cart.Cart
import java.time.OffsetDateTime

data class User(

    val id: String,
    val name: String,
    val email: String,
    val createdAt: OffsetDateTime,
) {
    var cart: Cart? = null;
}