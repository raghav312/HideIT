package com.example.hideit.jsonclasses

data class AnalysisExplanation(
    val recognizer: String,
    val pattern_name: String,
    val pattern: String,
    val original_score: Double,
    val score: Double,
    val textual_explanation: Any,
    val score_context_improvement: Double,
    val supportive_context_word: String,
    val validation_result: Any
)