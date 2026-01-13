# Gemini Context: Android Compose Demo (Music Player)

## 1. 项目概述 (Project Overview)
本项目是一个 Android Jetpack Compose 学习型 Demo，实现了一个商业风格的音乐播放器。
核心目标是保持代码简洁、结构清晰，便于初学者理解和扩展。

## 2. 技术栈 (Tech Stack)
- **UI 框架**: Jetpack Compose (Material Design)
- **导航**: Navigation Compose
- **架构**: MVVM (Model-View-ViewModel) + 单向数据流 (UDF)
- **网络**: Retrofit + OkHttp
- **序列化**: kotlinx.serialization (JSON 解析)
- **本地存储**: DataStore Preferences (用于 Token 存储)

## 3. 架构设计 (Architecture)
项目严格遵循简化的分层架构：

### 目录结构 `com.lei.compose_demo`
- **`data` (数据层)**: 负责数据获取与存储，不包含 UI 逻辑。
    - `local`: 本地数据源 (如 `TokenStore`)。
    - `remote`: 网络相关 (API 定义, Retrofit Client, DTOs)。
    - `repository`: 数据仓库 (如 `NetworkRepository`)，统一数据访问。
    - *实体类*: 直接存放于 `data` 根目录下 (如 `Track`, `PlayerState`)。
- **`state` (状态层)**: 连接 UI 与 Data。
    - `MusicUiState`: 定义 UI 所需的所有状态。
    - `MusicEvent`: 定义 UI 可能触发的所有事件（用户交互）。
    - `MusicViewModel`: 处理业务逻辑，持有 StateFlow，响应 Event。
- **`ui` (界面层)**: 纯 UI 实现。
    - `MusicScreen`: 主页。
    - `PlayerDetailScreen`: 详情页。
    - `AppNav`: 导航图入口。

## 4. 编码与注释规范 (Coding Standards)
**重要规则：**
1.  **全中文注释**:
    - **类 (Class)**: 必须添加中文说明。
    - **变量 (Variables)**: 成员变量、局部变量必须有中文注释。
    - **参数 (Parameters)**: 函数参数、构造函数参数必须有中文注释。
    - **Lambda**: Lambda 参数（如 `it`）若含义不清，需重命名并添加注释。
2.  **文档风格**: Compose 函数建议使用 KDoc (`/** ... */`) 格式。
3.  **代码风格**:
    - 保持简洁，避免过度封装。
    - 优先使用语义化命名。

## 5. 关键业务流程
- **导航**: 单 Activity (`MainActivity`) 托管 `AppNav`，通过路由字符串 (`music`, `player`) 跳转。
- **网络流**: `ViewModel` -> `Repository` -> `Retrofit` -> `Backend`。
- **Token**: 登录成功 -> `TokenStore.saveToken` -> 后续请求读取。

## 6. 文件指引
- **API 配置**: `data/remote/NetworkConfig.kt`
- **导航定义**: `ui/AppNav.kt`
- **主题颜色**: `ui/theme/Color.kt`
