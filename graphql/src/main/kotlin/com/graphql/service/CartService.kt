package com.graphql.service

import com.graphql.entity.cart.Cart
import com.graphql.entity.user.User
import com.graphql.repository.Database
import org.apache.coyote.BadRequestException
import org.springframework.stereotype.Service
import java.util.*

@Service
class CartService {

    fun getUserCart(userId: String): Cart {
        return Database.carts.firstOrNull { it.user.id == userId }
            ?.also { cart -> cart.items = Database.cartItems.filter { it.cart.id == cart.id } }
            ?.also { cart -> cart.totalAmount = cart.items.sumOf { it.product.price * it.quantity } }
            ?: throw BadRequestException("Cart not found")
    }

    fun addUserCart(user: User): Cart {
        return Cart(
            id = UUID.randomUUID().toString().substring(0, 5),
            user = user
        ).also { cart -> Database.carts.add(cart) }
    }

}