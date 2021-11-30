package com.example.hideit.jsonclasses

data class AnalysisExplanation(
    val original_score: Double,
    val pattern: String,
    val pattern_name: String,
    val recognizer: String,
    val score: Double,
    val score_context_improvement: Double,
    val supportive_context_word: String,
    val textual_explanation: Any,
    val validation_result: Any
)