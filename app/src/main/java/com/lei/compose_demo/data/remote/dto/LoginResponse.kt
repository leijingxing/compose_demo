package com.lei.compose_demo.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 登录响应体。
 *
 * @param token 登录后返回的令牌。
 */
@Serializable
data class LoginResponse(
    // 登录后返回的令牌。
    @SerialName("token") val token: String,
)
