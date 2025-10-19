package com.graphql.resolver

import com.graphql.entity.search.SearchResult
import com.graphql.service.ProductService
import com.graphql.service.UserService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class SearchResolver(
    private val productService: ProductService,
    private val userService: UserService
) {

    @QueryMapping
    fun search(
        @Argument keyword: String
    ): List<SearchResult> {
        return productService.getProducts().filter { it.name.contains(keyword) } +
                userService.getUsers().filter { it.name.contains(keyword) }
    }

}