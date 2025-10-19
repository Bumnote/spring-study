package com.graphql.input

class AddCartItemInput(
    val userId: String,
    val productId: String,
    val quantity: Int
) {
}