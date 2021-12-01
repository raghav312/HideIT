package com.example.hideit.jsonclasses

data class AnalysizedResponseItem(
    val entity_type: String,
    val start: Int,
    val end: Int,
    val score: Double,
    val analysis_explanation: AnalysisExplanation
)