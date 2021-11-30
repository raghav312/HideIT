package com.example.hideit.jsonclasses

data class AnonymizeText(
    val analyzer_results: List<AnalyzerResult>,
//    val anonymizers: Anonymizers,
    val text: String
)