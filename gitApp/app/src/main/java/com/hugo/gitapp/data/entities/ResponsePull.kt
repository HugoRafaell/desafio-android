package com.hugo.gitapp.data.entities

import java.io.Serializable

class ResponsePull(
    val url: String? = null,
    val id: Int? = null,
    val node_id: String? = null,
    val html_url: String? = null,
    val diff_url: String? = null,
    val patch_url: String? = null,
    val issue_url: String? = null,
    val number: Int? = null,
    val state: String? = null,
    val title: String? = null,
    val user: User? = null,
    val body: String? = null
) : Serializable {
    class User(
        val login: String? = null,
        val id: Int? = null,
        val avatar_url: String? = null
    ) : Serializable
}