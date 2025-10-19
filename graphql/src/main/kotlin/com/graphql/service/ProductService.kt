package com.graphql.service

import com.graphql.entity.product.Product
import com.graphql.repository.Database
import org.apache.coyote.BadRequestException
import org.springframework.stereotype.Service

@Service
class ProductService {

    fun getProduct(productId: String): Product {
        return Database.products.firstOrNull { it.id == productId }
            ?: throw BadRequestException("Product not found")
    }
}