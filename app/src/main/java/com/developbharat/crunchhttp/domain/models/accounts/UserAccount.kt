package com.developbharat.crunchhttp.domain.models.accounts


data class UserAccount(
    val id: String,
    val name: String,
    val mobile: String,
    val authToken: String,
    val accountActivatedAt: Long? = null,
)