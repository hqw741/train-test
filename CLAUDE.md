# Cozy Fitness App

健身辅助应用，聚焦有氧运动训练。

## 项目信息

- **平台**: Android (Kotlin)
- **最低SDK**: 26 (Android 8.0)
- **目标SDK**: 34

## 技术栈

- Jetpack Compose + Material Design 3
- MVVM + Clean Architecture
- Hilt (依赖注入)
- Room (数据库)
- Navigation Compose (导航)
- Coroutines + Flow (异步)

## 目录结构

```
app/src/main/java/com/cozyfitness/
├── CozyFitnessApp.kt         # Application class (Hilt)
├── MainActivity.kt            # 主入口
├── domain/model/
│   └── Models.kt             # 数据模型 (User, WorkoutPlan, Exercise, etc.)
└── ui/
    ├── theme/                 # 颜色/字体/主题
    ├── navigation/            # 底部导航
    ├── home/                  # HomeScreen
    ├── workout/               # WorkoutScreen
    ├── tracking/              # TrackingScreen
    ├── stats/                 # StatsScreen
    └── profile/               # ProfileScreen

app/src/main/res/
├── drawable/                  # 图标资源
├── mipmap-*/                  # 应用图标
└── values/                    # strings.xml, themes.xml
```

## 屏幕说明

| 屏幕 | 路由 | 功能 |
|------|------|------|
| Home | `home` | 每日概览、训练卡片、统计数据 |
| Workout | `workout` | 训练计划列表 (My Plans / Discover) |
| Track | `track` | 运动追踪、计时器、心率 |
| Stats | `stats` | 周/月统计、图表、成就 |
| Profile | `profile` | 目标设置、偏好配置 |

## 主题色

- Primary: Sage Green `#4CAF7A`
- Secondary: Sky Blue `#5B9FDF`
- Accent: Coral Peach `#FF8A65`
- Background: Soft White `#FAFBFC`

## 运行

1. 用 Android Studio 打开 `D:\train`
2. 等待 Gradle 同步
3. Run: Shift + F10

## 注意事项

- Gradle 同步需要网络连接，或配置国内镜像
- 使用 Hilt 的 @AndroidEntryPoint 需要在所有 Jetpack Compose 入口 Activity 上标注
- Compose BOM 版本: 2024.02.00
- Kotlin 版本: 1.9.22
- Compose Compiler 版本: 1.5.8

## 待实现功能

- Room 数据库持久化
- ViewModel + StateFlow 状态管理
- 运动历史记录存储
- 成就系统解锁逻辑