package com.example.hideit.jsonclasses

data class AnalyzerResult(
    val end: Int,
    val entity_type: String,
    val score: Double,
    val start: Int
)