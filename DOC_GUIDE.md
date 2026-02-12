# 项目文档与注释规范指南 (Documentation & Comment Standards)

为了保持代码的可维护性与易读性，本项目要求所有代码必须遵循以下注释规范。所有注释必须使用 **中文**。

## 1. 核心原则
- **Why > What**: 注释应侧重于解释“为什么这么做”（设计意图、背景），而不仅仅是“做了什么”（代码翻译）。
- **同步更新**: 修改代码时，必须同步更新相关注释。
- **简洁明了**: 避免冗长的废话，直击重点。

## 2. 类与接口 (Classes & Interfaces)
每个类、接口、Object 都必须包含 KDoc 格式的注释。

```kotlin
/**
 * 描述该类的主要职责。
 * 
 * 若涉及复杂逻辑，请简述其工作原理。
 */
class ExampleClass { ... }
```

## 3. 成员变量与属性 (Properties)
数据类 (Data Class) 推荐在类头部的 KDoc 中使用 `@property` 描述，普通类推荐在变量上方添加注释。

**示例 (Data Class):**
```kotlin
/**
 * 状态信息封装。
 *
 * @property id 唯一标识符。
 * @property name 用户显示名称。
 */
data class UserState(
    val id: String,
    val name: String
)
```

**示例 (普通成员变量):**
```kotlin
class MyService {
    // 标记是否已初始化
    private var isInitialized = false
}
```

## 4. 函数与方法 (Functions)
公有函数和复杂的私有函数必须添加 KDoc。

```kotlin
/**
 * 执行数据同步。
 *
 * @param force 是否强制覆盖本地数据。
 * @return 同步是否成功的布尔值。
 */
fun syncData(force: Boolean): Boolean { ... }
```

## 5. Jetpack Compose 组件
Compose 函数通常包含大量 UI 逻辑，建议简述其 UI 职责及关键参数。

```kotlin
/**
 * 播放器控制栏组件。
 *
 * @param track 当前播放的音轨。
 * @param onPlayPauseClick 点击播放/暂停的回调。
 * @param modifier 外部传入的布局修饰符。
 */
@Composable
fun PlayerBar(
    track: Track,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) { ... }
```

## 6. 局部变量与 Lambda
- **局部变量**: 复杂的计算逻辑旁应有说明。
- **Lambda**: 若 `it` 含义不明，应显式命名。

```kotlin
list.map { userId ->
    // 根据 ID 获取详细信息并转换
    fetchDetail(userId)
}
```

## 7. 特殊标记
- `TODO`: 待办事项，需注明原因。
- `FIXME`: 已知问题，待修复。
