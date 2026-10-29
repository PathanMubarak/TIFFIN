# Tiffin 🍱

**Discover home kitchens. Find your next favourite meal.**

Tiffin is an Android application built with Kotlin and Jetpack Compose that helps users discover home kitchens, explore their weekly menus, and subscribe to meal plans through a simple, intuitive experience.

The idea behind Tiffin is straightforward: finding everyday meals should feel personal, convenient, and effortless.

## Features

- **Kitchen discovery** — Browse a curated list of home kitchens and explore their cuisines, ratings, delivery times, pricing, and other details.
- **Search and filtering** — Find kitchens based on your preferences.
- **Kitchen details** — View descriptions, dietary information, and weekly menus before making a decision.
- **Subscription flow** — Explore the subscription offer, review the pricing, and confirm a simulated purchase.
- **Subscription persistence** — Save subscription information locally so the app can restore the relevant state after being closed and reopened.
- **Launch-based paywall** — Demonstrate a paywall that can appear on the third app launch.
- **Thoughtful UI** — A clean interface built around a warm cream background, forest-green accents, and simple layouts.

## Tech stack

| Technology | Purpose |
|---|---|
| Kotlin | Application logic |
| Jetpack Compose | Declarative UI |
| Material 3 | UI components and styling |
| ViewModel | UI state management |
| Kotlin Coroutines and Flow | Asynchronous state collection |
| Local JSON assets | Kitchen and menu data |
| SharedPreferences | Persisting launch and subscription state |
| Android Studio | Development and debugging |

## How the app works

### 1. Discover kitchens

The home screen loads kitchen information from a local JSON asset. Users can browse the available kitchens and open a kitchen to learn more about it.

### 2. Explore a kitchen

The detail screen presents information about the selected kitchen, including its cuisine, rating, delivery time, dietary category, description, and weekly menu.

### 3. Subscribe

Selecting the subscription button opens the paywall. The offer presents a simulated introductory charge of ₹1 today, followed by a ₹249 charge after 24 hours, along with the expected renewal date.

Confirming the purchase simulates a successful subscription. No real payment is processed.

### 4. Restore subscription state

Subscription information is stored locally using SharedPreferences. The application checks the saved state to determine whether a kitchen should display its subscribed status.

The project also includes a launch-count-based paywall flow for demonstrating the third-launch experience.

## Architecture

The project separates kitchen data, UI state, and screen rendering to keep the code easier to understand and maintain.

```text
com.propel.tiffin
├── data
│   ├── Kitchen.kt
│   └── KitchenRepository.kt
├── viewmodel
│   └── KitchenViewModel.kt
├── ui.theme
│   ├── KitchenListScreen.kt
│   ├── KitchenDetailScreen.kt
│   ├── PaywallScreen.kt
│   └── Theme files
└── MainActivity.kt

app/src/main/assets
└── kitchens.json
```

The repository reads the local kitchen data, the ViewModel exposes the UI state, and the Compose screens render the corresponding interface. `MainActivity` coordinates navigation and the subscription flow.

## Getting started

### Prerequisites

- Android Studio
- A compatible Android SDK
- An Android emulator or physical Android device

### Run locally

1. Clone the repository or open the supplied project in Android Studio.
2. Allow Gradle to sync and download the required dependencies.
3. Confirm that `kitchens.json` exists under `app/src/main/assets/`.
4. Select an emulator or connected Android device.
5. Build and run the `app` configuration.

### Build the APK

In Android Studio, select **Build → Build APK(s)** for a debug APK.

For a release APK, select **Build → Generate Signed Bundle / APK → APK**, then follow the signing steps. Keep signing credentials private.

## Analytics

The assignment's analytics implementation should use an analytics interface with a Logcat implementation so important user actions can be observed during development without integrating a real analytics backend.

Relevant events include kitchen selection, paywall display, and simulated subscription confirmation.

## Testing notes

The main flows to verify are:

- Kitchen data loads and the list can be browsed.
- Selecting a kitchen opens the correct detail screen.
- The subscription button opens the paywall.
- The displayed price and renewal date are correct.
- Confirming a simulated purchase shows a success state.
- Subscription state behaves correctly after app restarts.
- The third-launch paywall behaves as intended.
- The release APK installs and runs successfully.

Any temporary testing duration should be restored to the required 24-hour duration before the final submission.

## Scope and limitations

This project demonstrates a subscription experience for an Android assignment. Purchases are simulated; there is no real payment processing, billing-provider integration, or actual ₹249 charge.

Kitchen data is loaded locally from JSON rather than a live backend.

## Author

**Mubarak Pathan**

Built with Kotlin and Jetpack Compose.
