package com.example.hideit.jsonclasses

data class AnalysizedResponseItem(
    val analysis_explanation: AnalysisExplanation,
    val end: Int,
    val entity_type: String,
    val score: Double,
    val start: Int
)