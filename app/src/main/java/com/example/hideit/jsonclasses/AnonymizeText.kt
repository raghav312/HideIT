package com.example.hideit.jsonclasses

data class AnonymizeText(
    val text: String,
    val analyzer_results: List<AnalyzerResult>
//    val anonymizers: Anonymizers,
)