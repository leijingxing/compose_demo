# Compose Demo（音乐播放器）

这是一个 Android Jetpack Compose 学习项目 Demo，包含：
- 商业风音乐播放器 UI（主列表页 + 播放详情页）
- 简洁分层架构（data / state / ui）
- 网络请求 + Token 保存 + JSON 解析（Retrofit + OkHttp + DataStore + kotlinx.serialization）
- 真实导航（Navigation Compose）

## 项目目标
- 代码简洁、结构清晰、易于学习与扩展
- 每个类、变量、参数都提供中文注释

## 架构设计（简化分层）
```
com.lei.compose_demo
├── data                 # 数据层
│   ├── local            # 本地存储（Token）
│   ├── remote           # 网络请求
│   │   └── dto          # 网络数据模型（DTO）
│   └── Track/PlayerState 等领域模型
├── state                # 状态与事件
│   ├── MusicUiState     # UI 状态
│   ├── MusicEvent       # UI 事件
│   └── MusicViewModel   # 业务逻辑
└── ui                   # 页面与组件
    ├── MusicScreen      # 主列表页
    ├── PlayerDetailScreen # 播放详情页
    ├── PlayerBar        # 底部播放条
    └── AppNav           # 导航入口
```

### 设计说明
- **data**：只负责数据与接口，不关心 UI。
- **state**：ViewModel 维护 `MusicUiState`，通过 `MusicEvent` 驱动。
- **ui**：纯 UI 组件与页面组合，读取状态并触发事件。

## 页面结构
- 主列表页：顶部品牌区 + 推荐卡片 + 热门歌曲列表 + 底部播放条
- 播放详情页：封面区域 + 歌曲信息 + 进度条 + 控制按钮

## 导航设计
使用 `Navigation Compose` 构建真实导航：
- `music`：主列表页
- `player`：播放详情页

导航入口：`app/src/main/java/com/lei/compose_demo/ui/AppNav.kt`

## 网络请求与数据流
### 依赖组件
- Retrofit + OkHttp：网络请求
- kotlinx.serialization：JSON 解析
- DataStore Preferences：Token 保存

### 关键文件
- `data/remote/ApiService.kt`：接口定义
- `data/remote/ApiClient.kt`：Retrofit 构建
- `data/remote/NetworkRepository.kt`：数据仓库
- `data/local/TokenStore.kt`：Token 存储

### 使用流程（示例）
1. 登录获取 Token：`NetworkRepository.login(...)`
2. 保存 Token：`TokenStore.saveToken(...)`
3. 访问受保护接口：`NetworkRepository.fetchTracks()`

## 运行方式
1. 修改 `NetworkConfig.BASE_URL` 为你的后端地址
2. Android Studio 同步 Gradle
3. 运行 App

## 备注
- 本项目为学习 Demo，接口路径为示例，需根据实际后端调整。
- 若需要真实图片/音频播放，可进一步扩展。

## 未来可扩展方向
- 播放进度动态更新（协程 + 定时器）
- 真实音频播放（Media3 / ExoPlayer）
- 真实后端与分页加载
- UI 动画与过渡效果
