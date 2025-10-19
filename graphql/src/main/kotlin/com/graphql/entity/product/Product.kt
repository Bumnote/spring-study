package com.graphql.entity.product

import com.graphql.entity.search.SearchResult

interface Product : SearchResult {
    val id: String
    val name: String
    val price: Double
    val productType: ProductType
}