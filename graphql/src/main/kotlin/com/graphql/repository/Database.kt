package com.graphql.repository

import com.graphql.entity.cart.Cart
import com.graphql.entity.cart.CartItem
import com.graphql.entity.product.Clothing
import com.graphql.entity.product.Electronics
import com.graphql.entity.product.Product
import com.graphql.entity.user.User
import java.time.OffsetDateTime

object Database {

    val products = arrayListOf<Product>(
        Electronics(
            id = "1",
            name = "nao book air",
            price = 1000.0,
            warrantyPeriod = "3years"
        ), Electronics(
            id = "2",
            name = "iphone 19",
            price = 2000.0,
            warrantyPeriod = "2years"
        ), Electronics(
            id = "3",
            name = "samsung tv",
            price = 3000.0,
            warrantyPeriod = "1year"
        ),
        Clothing(
            id = "4",
            name = "T-Shirt",
            price = 300.0,
            size = "M"
        ), Clothing(
            id = "5",
            name = "Jeans",
            price = 200.0,
            size = "L"
        ), Clothing(
            id = "6",
            name = "Dress",
            price = 100.0,
            size = "S"
        )
    )

    val users = arrayListOf<User>(
        User(
            id = "1",
            name = "Burger",
            email = "burger@goopang.com",
            createdAt = OffsetDateTime.now().minusDays(30),
        ), User(
            id = "2",
            name = "Jane",
            email = "jane@goopang.com",
            createdAt = OffsetDateTime.now().minusDays(60),
        )
    )

    val carts = arrayListOf<Cart>(
        Cart(
            id = "1",
            user = users.first { it.id == "1" }
        ),
        Cart(
            id = "2",
            user = users.first { it.id == "2" }
        )
    )

    val cartItems = arrayListOf<CartItem>(
        CartItem(
            id = "1",
            quantity = 1,
            product = products.first { it.id == "1" },
            cart = carts.first { it.id == "1" }
        ),
        CartItem(
            id = "2",
            quantity = 3,
            product = products.first { it.id == "3" },
            cart = carts.first { it.id == "1" }
        ),
        CartItem(
            id = "3",
            quantity = 2,
            product = products.first { it.id == "5" },
            cart = carts.first { it.id == "1" }
        ),
        CartItem(
            id = "4",
            quantity = 1,
            product = products.first { it.id == "6" },
            cart = carts.first { it.id == "2" }
        ),
        CartItem(
            id = "5",
            quantity = 2,
            product = products.first { it.id == "2" },
            cart = carts.first { it.id == "2" }
        )
    )
}

