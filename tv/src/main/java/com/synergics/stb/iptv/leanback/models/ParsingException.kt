package com.synergics.stb.iptv.leanback.models

class ParsingException : RuntimeException {
    var line: Int
        private set

    constructor(line: Int, message: String) : super("$message at line $line") {
        this.line = line
    }

    constructor(line: Int, message: String, cause: Exception?) : super(
        "$message at line $line",
        cause
    ) {
        this.line = line
    }
}