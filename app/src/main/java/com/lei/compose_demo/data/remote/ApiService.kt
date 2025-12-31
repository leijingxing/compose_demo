package com.lei.compose_demo.data.remote

import com.lei.compose_demo.data.remote.dto.LoginRequest
import com.lei.compose_demo.data.remote.dto.LoginResponse
import com.lei.compose_demo.data.remote.dto.TrackDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * 网络接口定义。
 */
interface ApiService {
    /**
     * 登录接口。
     *
     * @param request 登录请求体。
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): LoginResponse

    /**
     * 获取歌曲列表接口。
     *
     * @param authorization 请求头中的鉴权信息。
     */
    @GET("tracks")
    suspend fun getTracks(
        @Header("Authorization") authorization: String,
    ): List<TrackDto>
}
