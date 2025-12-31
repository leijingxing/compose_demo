package com.lei.compose_demo.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Token 本地存储。
 *
 * @param context 应用上下文。
 */
class TokenStore(
    // 应用上下文。
    private val context: Context,
) {
    /**
     * 保存 Token。
     *
     * @param token 需要保存的 Token。
     */
    suspend fun saveToken(token: String) {
        context.tokenDataStore.edit { preferences ->
            // 保存到 DataStore。
            preferences[TOKEN_KEY] = token
        }
    }

    /**
     * 获取 Token 流。
     */
    fun getTokenFlow(): Flow<String?> {
        return context.tokenDataStore.data.map { preferences ->
            // 读取 Token。
            preferences[TOKEN_KEY]
        }
    }

    /**
     * 获取 Token（挂起函数）。
     */
    suspend fun getToken(): String? {
        // 当前偏好数据。
        val preferences = context.tokenDataStore.data.first()
        // 读取 Token。
        val token = preferences[TOKEN_KEY]
        return token
    }

    /**
     * 清除 Token。
     */
    suspend fun clearToken() {
        context.tokenDataStore.edit { preferences ->
            // 删除 Token。
            preferences.remove(TOKEN_KEY)
        }
    }

    private companion object {
        // Token 存储 Key。
        val TOKEN_KEY: Preferences.Key<String> = stringPreferencesKey("auth_token")
    }
}

// DataStore 实例。
private val Context.tokenDataStore by preferencesDataStore(name = "token_store")
