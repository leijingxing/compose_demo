package com.lei.compose_demo

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 仪器测试示例，在 Android 设备/模拟器上运行。
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    /**
     * 校验应用上下文包名是否正确。
     */
    @Test
    fun useAppContext() {
        // 被测应用的上下文对象。
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.lei.compose_demo", appContext.packageName)
    }
}
