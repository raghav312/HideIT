package com.example.hideit.jsonclasses

data class Item(
    val operator: String,
    val entity_type: String,
    val start: Int,
    val end: Int,
    val text: String
)