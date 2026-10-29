Tiffin --- Android Developer Intern Assignment
Discover home kitchens. Find your next favourite meal.

Tiffin is an Android app prototype built with Kotlin and Jetpack
Compose. It lets users browse home kitchens, explore weekly menus, and
experience a simulated subscription flow.

This project was built for the Propel Spark Android Developer Intern
take-home assignment. The implementation is intentionally scoped to the
requirements: local kitchen data, three core screens, persisted
subscription state, and a lightweight Android UI.
Features
- Browse kitchens loaded from a bundled JSON asset.
- Search and filter the kitchen list.
- View kitchen details, cuisine, pricing, rating, dietary category,
  delivery time, description, and weekly menu.
- Represent loading, empty, and error states.
- Show a subscription paywall with an introductory ₹1 charge and a
  stated ₹249 charge 24 hours later.
- Simulate a purchase without a payment SDK.
- Persist app state locally.
- Use a custom TIFFIN launcher icon and a cream/forest-green visual
  theme.
  Tech stack
- Language: Kotlin
- UI: Jetpack Compose and Material 3
- State: ViewModel and Kotlin Flow
- Data: Local JSON asset
- Persistence: SharedPreferences
- IDE/build: Android Studio and Gradle
  Project structure
  Tiffin/
  ├── app/
  │   └── src/
  │       ├── main/
  │       │   ├── assets/
  │       │   │   └── kitchens.json
  │       │   ├── java/com/propel/tiffin/
  │       │   │   ├── MainActivity.kt
  │       │   │   ├── data/
  │       │   │   ├── viewmodel/
  │       │   │   └── ui/theme/
  │       │   └── res/
  │       └── test/
  ├── gradle/
  ├── build.gradle.kts
  ├── settings.gradle.kts
  └── README.md
  Run locally
  Requirements
- Android Studio
- A compatible JDK and Android SDK
- An Android emulator or physical Android device
  Steps
1. Clone this repository and open the project in Android Studio.
2. Allow Gradle to sync.
3. Confirm that app/src/main/assets/kitchens.json exists.
4. Select an emulator or connected Android device.
5. Run the app configuration.
   Build a debug APK on Windows from the project root:
   .\gradlew.bat assembleDebug
   Expected output:
   app/build/outputs/apk/debug/app-debug.apk
   For a release APK, use Build → Generate Signed Bundle / APK → APK
   and follow the signing steps. Keep signing credentials private.
   Task 1 --- Ship it
   Implementation overview
   The app separates kitchen data loading, UI state, and screen rendering.
   The repository reads the bundled JSON, the ViewModel exposes the state
   to Compose, and the screens render the kitchen list, details, and
   paywall. MainActivity coordinates navigation and the simulated
   purchase flow.

The subscription flow is a demo only. No real payment is taken. The UI
states the introductory charge and renewal terms; local persistence is
used to restore state between launches.
What I cut and why
- Live backend: Kitchen data is bundled in a local JSON asset
  because the assignment explicitly calls for local data and no
  backend.
- Real payment processing: The purchase is simulated because the
  assignment excludes a payment SDK.
- Delivery-slot booking and order tracking: These require product
  and backend behavior outside the requested three-screen scope.
- Kitchen-owner tools and account management: These are not
  necessary to demonstrate the required browse, detail, and paywall
  journeys.
- Production subscription infrastructure: Server-side billing
  validation and payment reconciliation are outside this prototype's
  scope.
  These cuts keep the work focused on the required experience instead of
  adding incomplete infrastructure.
  Analytics requirement
  The assignment requires an analytics.kt file containing a single
  analytics interface and a Logcat implementation, called at four
  meaningful moments. Before submission, verify that this file exists,
  that the implementation is wired into the app, and that the four chosen
  events appear in Logcat. Suggested events are:
1. kitchen_selected
2. kitchen_detail_viewed
3. paywall_shown
4. subscription_purchase_confirmed
   Only keep these event names in the final description if they match the
   actual implementation.
   Subscription requirements to verify before submission
- The paywall appears on the third app launch and when Subscribe is
  tapped.
- The paywall explicitly says ₹1 is charged now and ₹249 is charged on
  a named date 24 hours later.
- A simulated purchase is persisted.
- Once the user is marked paid, the paywall does not appear again.
- State remains correct after rotation, process recreation,
  force-stop, and relaunch.
  Important: The intended assignment behavior is a persisted paid
  state that suppresses the paywall after purchase. If the current
  implementation uses separate subscriptions per kitchen or a temporary
  one-minute expiry, reconcile that behavior with the assignment and
  restore the required 24-hour wording/duration before submission.
  Task 2 --- Fix what the agent wrote
  The supplied code has four core problems.
  Problem 1: The list is not observable Compose state
  mutableListOf<Kitchen>() is an ordinary mutable list. Mutating it does
  not notify Compose that the UI should recompose.

What breaks: The list can remain empty on screen even after data is
added.

Correction: Expose list data through observable UI state, such as
StateFlow, from the ViewModel and collect it in the composable.
Problem 2: The LaunchedEffect key is unstable
The items list is created inside the composable and then used as the
effect key. Recomposition can create a new list instance and restart the
effect.

What breaks: Data loading may run more often than intended, leading
to repeated work and inconsistent state.

Correction: Put loading in the ViewModel/repository. If a composable
effect genuinely needs to run once for that composition, use a stable
key such as Unit, not a newly created collection.
Problem 3: A secret is hardcoded in the URL
The code embeds a string that resembles a live API key directly in
source code.

What breaks: A real credential in an Android client can be extracted
from source or the compiled APK and abused, potentially causing
unauthorized access or costs.

Correction: Revoke any real credential that has been exposed. Do not
ship privileged secrets in a client app; move privileged operations
behind a backend. This assignment does not need this key because the
data should be local.
Problem 4: Blocking network I/O is performed from the UI effect
URL(...).readText() is a blocking call and can stall the main thread.

What breaks: The UI may freeze, become unresponsive, or trigger an
application-not-responding error.

Correction: For a network-backed app, perform I/O on an appropriate
background dispatcher inside a repository and expose
loading/success/error state. For this assignment, remove the network
call and load the bundled JSON asset instead.
Corrected list-screen pattern
The following illustrates the state-driven rendering approach.
KitchenRow and the KitchenUiState declarations must match the actual
project definitions.
@Composable
fun KitchenList(
vm: KitchenViewModel,
onKitchenClick: (Kitchen) -> Unit
) {
val uiState by vm.uiState.collectAsState()

    when (val state = uiState) {
        is KitchenUiState.Loading -> {
            CircularProgressIndicator()
        }

        is KitchenUiState.Success -> {
            if (state.kitchens.isEmpty()) {
                Text("No kitchens available.")
            } else {
                LazyColumn {
                    items(
                        items = state.kitchens,
                        key = { it.id }
                    ) { kitchen ->
                        KitchenRow(
                            kitchen = kitchen,
                            onClick = { onKitchenClick(kitchen) }
                        )
                    }
                }
            }
        }

        is KitchenUiState.Empty -> {
            Text("No kitchens available.")
        }

        is KitchenUiState.Error -> {
            Text("Unable to load kitchens. Please try again.")
        }
    }
}
This is a corrected pattern, not a claim that these exact function
signatures are already present. The important changes are observable
state, stable list keys, and data loading outside the composable. The
assignment's local-JSON requirement means no URL or API key is needed.
Task 3 --- Release note
Tiffin Android: Release readiness
Before publishing, I would manually test the primary journeys on a
small-screen emulator, a larger-screen emulator, and at least one
physical Android device. I would verify kitchen loading, search and
filtering, opening a kitchen, reading its weekly menu, subscribing,
dismissing the paywall, and reopening the app. I would also test
rotation, process recreation, force-stop/relaunch, empty data, and
malformed JSON. Subscription checks would cover the introductory price,
the named renewal date, persisted purchase state, and suppression of the
paywall after payment.

The production issue I would watch most closely is inconsistent
subscription state---for example, a user being shown the introductory
paywall again after a successful purchase. I would instrument the
paywall, purchase-confirmation, and state-restoration paths with the
required Logcat analytics events, then use those logs to narrow down
where the state diverges. For a production release, I would add crash
reporting and privacy-appropriate monitoring rather than relying on user
reports alone.

If a serious regression appeared at 11 PM, I would first confirm its
scope and reproduce it. If a staged rollout were in progress, I would
halt it while investigating. I would prepare and verify a fix, then
release an updated version. I would not assume that an already-installed
app could be remotely rolled back instantly.

My next three improvements would be:
1. Subscription correctness: Centralize the paid state and add
   tests for purchase persistence, process death, relaunch, and paywall
   suppression.
2. Automated coverage: Add repository/ViewModel unit tests and UI
   tests for the main user journeys.
3. Production readiness: Improve crash/error visibility,
   accessibility, and layout testing across screen sizes and font
   scales.
   I would consider the release ready only after the required journeys pass
   on the release build and the implementation matches the behavior
   documented here.
   Task 4 --- How I used AI
   AI assisted my development workflow with implementation suggestions,
   debugging, and documentation. I used ChatGPT to help develop Compose
   screens, reason through navigation and persistence, investigate UI
   behavior, and refine the project README. I integrated the suggestions
   into Android Studio and used builds and app testing to check the result.
   Tools and contribution
- AI assistant: ChatGPT for code suggestions, debugging guidance,
  and documentation.
- Development tools: Android Studio, Gradle, and the Android
  emulator.
- AI contribution: State the approximate percentage based on the
  code actually retained in the final project. Do not guess a
  percentage simply to fill this section.
  Best prompt
  The following is a reconstructed prompt based on the work done on this
  project. Because the assignment asks for a prompt pasted exactly as
  sent, replace this with the original wording from your chat history if
  you can retrieve it.


PROMPT : 

  I'm building an Android app called Tiffin using Kotlin and Jetpack
  Compose for an Android Developer Intern assignment. The app should
  have a kitchen listing screen loaded from a local JSON asset, a
  kitchen detail screen with the weekly menu, and a subscription
  paywall. I want the UI to look clean and polished, with a consistent
  cream and forest-green theme. The subscription flow should show ₹1
  charged today and ₹249 after 24 hours, with the renewal date displayed
  clearly. Since this is a demo, simulate the purchase without
  integrating a payment SDK, but persist the subscription state so it
  survives app restarts and the paywall doesn't appear again after
  successful payment. Keep the code organized into separate screens, a
  repository, and a ViewModel. Include proper loading, empty, and error
  states, and make sure navigation and state handling work correctly.
  Give me complete Kotlin files with the correct package names and
  imports, and explain exactly where each file belongs in the Android
  Studio project. Avoid unnecessary complexity and keep the
  implementation aligned with the assignment requirements.

One issue AI initially missed
During subscription testing, I noticed that saving a renewal timestamp
did not automatically update the Subscribe button when the test expiry
time passed. The initial implementation stored the timestamp, but that
alone did not guarantee that the UI would refresh at expiry.

I investigated the state flow and added expiry checks and a periodic UI
refresh for testing. This highlighted the difference between persisting
a timestamp and enforcing the intended behavior in the interface. The
temporary one-minute test duration needs to be restored to the
assignment's 24-hour requirement before submission.
A decision I made myself
I kept kitchen data in a bundled JSON asset instead of adding a backend
or network request. This was a deliberate scope decision because the
assignment specifically asks for local data. It kept the app
self-contained and avoided spending time on infrastructure that was not
required.
