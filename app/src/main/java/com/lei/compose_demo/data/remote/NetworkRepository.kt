package com.lei.compose_demo.data.remote

import com.lei.compose_demo.data.Track
import com.lei.compose_demo.data.local.TokenStore
import com.lei.compose_demo.data.remote.dto.LoginRequest
import com.lei.compose_demo.data.remote.dto.toDomain

/**
 * 网络数据仓库。
 *
 * @param apiService 接口服务。
 * @param tokenStore Token 存储。
 */
class NetworkRepository(
    // 接口服务。
    private val apiService: ApiService,
    // Token 存储。
    private val tokenStore: TokenStore,
) {
    /**
     * 登录并保存 Token。
     *
     * @param username 用户名。
     * @param password 密码。
     */
    suspend fun login(username: String, password: String) {
        // 登录请求体。
        val request = LoginRequest(username = username, password = password)
        // 登录响应体。
        val response = apiService.login(request)
        // 保存 Token。
        tokenStore.saveToken(response.token)
    }

    /**
     * 获取歌曲列表（需要 Token）。
     */
    suspend fun fetchTracks(): List<Track> {
        // 当前 Token。
        val token = tokenStore.getToken()
        // 拼接认证头。
        val authorization = if (token.isNullOrBlank()) "" else "Bearer $token"
        // 远端歌曲列表。
        val remoteTracks = apiService.getTracks(authorization)
        // 转换为领域模型。
        val tracks = remoteTracks.map { trackDto ->
            // DTO 转领域模型。
            trackDto.toDomain()
        }
        return tracks
    }
}
