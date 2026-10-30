# Recipe Helper & AI Chef Assistant 🍳✨

[![Android CI Pipeline](https://github.com/username/recipe_helper/actions/workflows/android.yml/badge.svg)](https://github.com/username/recipe_helper/actions)
![Android SDK](https://img.shields.io/badge/API-24%2B-brightgreen.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-orange.svg)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVI--MVVM-blue.svg)

A production-ready Android application built with **Jetpack Compose**, **Clean Architecture**, **Hilt Dependency Injection**, **Offline-First Room & Retrofit Caching**, and an **AI Chef Assistant** for smart ingredient-based recipe creation.

---

## 🌟 Key Features

- 🤖 **AI Chef Assistant:** Enter available pantry ingredients (e.g., *Eggs, Spinach, Cheese*), and the AI Chef will generate a custom recipe with cooking instructions and tips.
- 🌐 **Online Meal Discovery:** Live REST API integration with **TheMealDB** to fetch, search, and filter global recipes by cuisine and category.
- 📦 **Offline-First Synchronization:** Room local database caches remote and AI-generated recipes for seamless offline playback.
- 📱 **Modern Material 3 Design:** Responsive Compose UI, step-by-step cooking progress tracker, and favorite recipe management.
- 🧪 **Comprehensive Automated Testing:** Unit tested ViewModels and Repositories using **MockK** and **Turbine**.
- ⚙️ **CI/CD Pipeline:** Automated GitHub Actions workflow running unit tests and debug APK assembly on pull requests.

---

## 🏗️ Architecture & Tech Stack

This project strictly adheres to **Google's Recommended App Architecture** (Clean Architecture + Unidirectional Data Flow):

```
┌─────────────────────────────────────────────────────────┐
│              Presentation Layer (UI & ViewModels)        │
│    HomeScreen | AiChefScreen | DetailScreen | AddRecipe │
└───────────────────────────┬─────────────────────────────┘
                            │ StateFlow / UiState
┌───────────────────────────▼─────────────────────────────┐
│                   Domain Layer (Use Cases)              │
│            Recipe | Ingredient | RecipeRepository       │
└───────────────────────────┬─────────────────────────────┘
                            │ Clean Abstraction
┌───────────────────────────▼─────────────────────────────┐
│             Data Layer (Offline-First Repository)        │
│    RecipeRepositoryImpl ──► Room DB (Local)            │
│                          └──► TheMealDB API (Remote)     │
└─────────────────────────────────────────────────────────┘
```

- **UI Framework:** Jetpack Compose + Material 3 + Type-Safe Navigation
- **Dependency Injection:** Hilt (Dagger)
- **Async & Reactive Data:** Kotlin Coroutines + `StateFlow` + `Flow`
- **Networking:** Retrofit 2 + Gson Converter
- **Local Persistence:** Room Database + KSP
- **Image Loading:** Coil Compose
- **Testing:** JUnit4, MockK, Turbine (Flow testing), Coroutines Test

---

## 🛠️ Local Building Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/username/recipe_helper.git
   ```
2. Open the project in **Android Studio Ladybug (or newer)**.
3. Sync Gradle and run the `:app` configuration on an emulator or Android device (API 24+).
4. Run unit tests via terminal:
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## 📄 Resume Highlights

> **Recipe Helper — Modern Android App**
> - Architected an Android application following **Clean Architecture** (Data, Domain, Presentation) with **MVI/MVVM** and **Hilt** dependency injection.
> - Implemented an **offline-first data layer** combining **Retrofit REST API** with **Room DB caching** using Kotlin `Coroutines` & `Flow`.
> - Engineered an interactive **AI Chef Assistant** feature generating custom recipes from pantry ingredients.
> - Built a reactive UI with **Jetpack Compose** and **Type-Safe Navigation**, backed by automated unit tests (**MockK**, **Turbine**) and **GitHub Actions CI/CD**.
