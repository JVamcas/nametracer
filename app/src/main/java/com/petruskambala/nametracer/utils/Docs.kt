package com.petruskambala.nametracer.utils

enum class Docs(private val value: String) {
    ACCOUNTS("Accounts"),
    VISITS("Visits"),
    STATS("stats"),
    CASES("cases");

    override fun toString(): String {
        return value
    }
}