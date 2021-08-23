package com.worldremit.util

import java.util.regex.Pattern
import java.util.stream.Collectors

private val PATTERN_UPPER = Pattern.compile("(?=\\p{Upper})")

fun upperCamelCaseToUpperUnderscore(text: String): String = PATTERN_UPPER
    .splitAsStream(text)
    .map(String::uppercase)
    .collect(Collectors.joining("_"))