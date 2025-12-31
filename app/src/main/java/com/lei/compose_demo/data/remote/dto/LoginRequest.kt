package com.lei.compose_demo.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 登录请求体。
 *
 * @param username 用户名。
 * @param password 密码。
 */
@Serializable
data class LoginRequest(
    // 用户名。
    @SerialName("username") val username: String,
    // 密码。
    @SerialName("password") val password: String,
)
