# Cozy Fitness App

A minimalist fitness tracking app for Android with aerobic exercise focus.

## Features
- **Home**: Daily overview with workout summary and quick stats
- **Workout**: Plan library with My Plans and Discover sections
- **Track**: Real-time exercise tracking with rest timers
- **Stats**: Weekly/monthly progress with charts and achievements
- **Profile**: Customizable goals and preferences

## Tech Stack
- Kotlin + Jetpack Compose
- Material Design 3
- MVVM + Clean Architecture
- Hilt for dependency injection
- Room for local storage

## Design
- **Style**: Clean and fresh (简约清新)
- **Colors**: Sage green primary, sky blue secondary, coral peach accent
- **Typography**: System default with clear hierarchy

## Running the App

1. Open the project in Android Studio
2. Sync Gradle files
3. Run on emulator or device: `Shift + F10`

## Project Structure
```
app/src/main/java/com/cozyfitness/
├── CozyFitnessApp.kt         # Application class
├── MainActivity.kt           # Main activity
├── domain/model/             # Data models
├── ui/
│   ├── theme/               # Colors, typography, theme
│   ├── components/          # Reusable UI components
│   ├── navigation/          # Bottom nav and routing
│   ├── home/                # Home screen
│   ├── workout/             # Workout plans screen
│   ├── tracking/            # Exercise tracking screen
│   ├── stats/               # Statistics screen
│   └── profile/             # Profile screen
└── data/                    # Data layer (to be implemented)
```

## Screens Preview
- **Home**: Greeting, today's workout card, daily stats, quick start buttons
- **Workout**: Tabbed view (My Plans / Discover), workout cards with difficulty badges
- **Tracking**: Large timer display, current exercise info, heart rate, rest timer, controls
- **Stats**: Week/Month tabs, summary card, activity chart, achievements
- **Profile**: Avatar, goal sliders, preferences toggles, settings items