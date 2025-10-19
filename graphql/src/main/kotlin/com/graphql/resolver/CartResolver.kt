package com.graphql.resolver

import com.graphql.entity.cart.Cart
import com.graphql.input.AddCartItemInput
import com.graphql.input.DeleteCartItemInput
import com.graphql.service.CartService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class CartResolver(
    private val cartService: CartService
) {

    @QueryMapping
    fun getUserCart(
        @Argument userId: String
    ): Cart {
        return cartService.getUserCart(userId)
    }

    @MutationMapping
    fun addCartItem(
        @Argument addCartItemInput: AddCartItemInput
    ): Cart {
        return cartService.addCartItem(addCartItemInput)
    }

    @MutationMapping
    fun deleteCartItem(
        @Argument deleteCartItemInput: DeleteCartItemInput
    ): Cart {
        return cartService.deleteCartItem(deleteCartItemInput)
    }
}