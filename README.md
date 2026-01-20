# Compose Demo（音乐播放器）

这是一个 Android Jetpack Compose 学习项目 Demo，旨在展示一个具有商业质感的音乐播放器应用。

## 最新特性
- **商业级 UI**: 沉浸式深色主题，包含主列表页、播放详情页及个人中心。
- **底部导航**: 实现首页与个人中心的 Tab 切换，播放条全局悬浮。
- **氛围动效**: 播放页背景实时律动波形动画（Glassmorphism 磨砂感）。
- **共享元素**: 实现封面图在列表与详情页间的平滑转场动画。
- **简洁分层架构**: 严格遵循数据 (data) / 状态 (state) / 界面 (ui) 三层设计。
- **技术栈**: Retrofit + OkHttp + DataStore + kotlinx.serialization + Navigation Compose。

## 项目目标
- 代码简洁、结构清晰、易于学习与扩展。
- 每个类、变量、参数都提供详细的中文注释。

## 架构设计（简化分层）
```
com.lei.compose_demo
├── data                 # 数据层
│   ├── local            # 本地存储（Token, 音乐扫描）
│   ├── remote           # 网络请求（API 定义, DTO, Repository）
│   └── Track/PlayerState 等领域模型
├── state                # 状态与事件
│   ├── MusicUiState     # UI 状态定义
│   ├── MusicEvent       # 用户交互事件
│   └── MusicViewModel   # 业务逻辑处理
└── ui                   # 界面层
    ├── MainScreen       # 主页容器（底部导航 + 全局播放条）
    ├── MusicScreen      # 首页音乐列表
    ├── ProfileScreen    # 个人中心（菜单 + 统计）
    ├── PlayerDetailScreen # 播放详情页
    ├── player           # 播放页相关组件（波形动画、歌词、控制台）
    ├── theme            # 主题配置（颜色、字体、形状）
    └── AppNav           # 路由配置入口
```

## 页面结构
- **主容器**: 采用底部 Tab 导航，承载首页与“我的”，并持有全局常驻的迷你播放条。
- **首页**: 顶部品牌区 + 每日精选卡片 + 扫描本地音乐 + 热门歌曲列表。
- **个人中心**: 简约商业风，包含用户统计（收藏/粉丝）和功能菜单列表（我的喜欢/设置/关于）。
- **播放详情页**: 
    - **背景层**: 具有磨砂质感的氛围波形动画，随播放状态律动。
    - **内容层**: 共享元素封面 + 歌曲信息 + 进度条控制 + 左右滑动的歌词/控制页。

## 导航设计
使用 `Navigation Compose` 构建：
- `main`: 应用主入口（底部导航逻辑）。
- `player`: 播放详情页。
- `search`: 搜索页面。

## 运行方式
1. 确保环境配置了 `JAVA_HOME`。
2. Android Studio 同步 Gradle。
3. 修改 `data/remote/NetworkConfig.kt` 中的 `BASE_URL`。
4. 运行 App。

## 备注
- 本项目为学习 Demo，音频播放目前采用模拟逻辑，可结合 `Media3` 扩展为真实播放器。
- 所有的界面交互均经过平滑度优化，适合作为 Compose UI 学习参考。

## 未来可扩展方向
- 集成 `Media3 / ExoPlayer` 实现真实音频流播放。
- 实现 K 歌功能（歌词同步渲染、低延迟录音）。
- 接入真实后端的登录与个人数据同步。
- 更加丰富的交互手势与转场效果。