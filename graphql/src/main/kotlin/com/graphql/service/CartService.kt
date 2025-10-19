package com.graphql.service

import com.graphql.entity.cart.Cart
import com.graphql.entity.cart.CartItem
import com.graphql.entity.user.User
import com.graphql.input.AddCartItemInput
import com.graphql.input.DeleteCartItemInput
import com.graphql.repository.Database
import org.apache.coyote.BadRequestException
import org.springframework.stereotype.Service
import java.util.*

@Service
class CartService(
    private val productService: ProductService
) {

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

    fun addCartItem(addCartItemInput: AddCartItemInput): Cart {
        CartItem(
            id = UUID.randomUUID().toString().substring(0, 5),
            quantity = addCartItemInput.quantity,
            product = productService.getProduct(addCartItemInput.userId),
            cart = getUserCart(addCartItemInput.userId)
        ).also { Database.cartItems.add(it) }

        return getUserCart(addCartItemInput.userId)
    }

    fun deleteCartItem(deleteCartItemInput: DeleteCartItemInput): Cart {
        Database.cartItems.removeIf { it.id == deleteCartItemInput.cartItemId }

        return getUserCart(deleteCartItemInput.userId)
    }

}