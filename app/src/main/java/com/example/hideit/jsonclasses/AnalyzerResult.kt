package com.example.hideit.jsonclasses

data class AnalyzerResult(
    val start: Int,
    val end: Int,
    val score: Double,
    val entity_type: String
)