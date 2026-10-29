
package com.propel.tiffin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.propel.tiffin.data.Kitchen
import com.propel.tiffin.ui.theme.KitchenDetailScreen
import com.propel.tiffin.ui.theme.KitchenListScreen
import com.propel.tiffin.ui.theme.PaywallScreen
import com.propel.tiffin.ui.theme.TiffinTheme
import com.propel.tiffin.viewmodel.KitchenUiState
import com.propel.tiffin.viewmodel.KitchenViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val prefs by lazy {
        getSharedPreferences("tiffin_preferences", MODE_PRIVATE)
    }

    private fun getSubscribedKitchenIds(): Set<String> {
        return prefs.getStringSet(
            "subscribed_kitchen_ids",
            emptySet()
        )?.toSet() ?: emptySet()
    }

    private fun isKitchenSubscribed(kitchenId: String): Boolean {
        if (kitchenId.isBlank()) return false

        val subscribedIds = getSubscribedKitchenIds()

        if (kitchenId !in subscribedIds) return false

        val renewalTime = prefs.getLong(
            "renewal_time_$kitchenId",
            0L
        )

        // Remove the subscription when its expiry time is reached.
        if (
            renewalTime == 0L ||
            System.currentTimeMillis() >= renewalTime
        ) {
            val updatedIds = subscribedIds.toMutableSet()
            updatedIds.remove(kitchenId)

            prefs.edit()
                .putStringSet("subscribed_kitchen_ids", updatedIds)
                .remove("purchase_time_$kitchenId")
                .remove("renewal_time_$kitchenId")
                .apply()

            return false
        }

        return true
    }

    private fun saveSubscribedKitchen(kitchenId: String) {
        val updatedIds = getSubscribedKitchenIds().toMutableSet()
        updatedIds.add(kitchenId)

        val purchaseTime = System.currentTimeMillis()

        prefs.edit()
            .putStringSet("subscribed_kitchen_ids", updatedIds)
            .putLong("purchase_time_$kitchenId", purchaseTime)
            // TESTING: expire after one minute.
            .putLong(
                "renewal_time_$kitchenId",
                purchaseTime + 24 * 60 * 60 * 1000L
            )
            .apply()
    }

    private fun formatRenewalDate(time: Long): String {
        return SimpleDateFormat(
            "dd MMMM yyyy",
            Locale.ENGLISH
        ).format(Date(time))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val launches = prefs.getInt("launch_count", 0) + 1
            prefs.edit()
                .putInt("launch_count", launches)
                .apply()
        }

        val viewModel = ViewModelProvider(this)[KitchenViewModel::class.java]
        val launchCount = prefs.getInt("launch_count", 0)

        val initialScreen = if (
            launchCount == 3 &&
            getSubscribedKitchenIds().isEmpty()
        ) {
            "PAYWALL"
        } else {
            "HOME"
        }

        setContent {
            TiffinTheme {

                var screen by rememberSaveable {
                    mutableStateOf(initialScreen)
                }

                var previousScreen by rememberSaveable {
                    mutableStateOf("HOME")
                }

                var selectedKitchenId by rememberSaveable {
                    mutableStateOf("")
                }

                var successKitchenName by rememberSaveable {
                    mutableStateOf("")
                }

                var renewalDate by rememberSaveable {
                    mutableStateOf(
                        formatRenewalDate(
                            System.currentTimeMillis() + 24 * 60 * 60 * 1000L
                        )
                    )
                }

                // Tick once per second while viewing a kitchen.
                // This refreshes the subscription button after expiry.
                var subscriptionTick by remember {
                    mutableIntStateOf(0)
                }

                LaunchedEffect(screen, selectedKitchenId) {
                    if (screen == "DETAIL") {
                        while (true) {
                            delay(1000L)
                            subscriptionTick++
                        }
                    }
                }

                val uiState by viewModel.uiState.collectAsState()

                val kitchens = (uiState as? KitchenUiState.Success)
                    ?.kitchens
                    .orEmpty()

                val selectedKitchen: Kitchen? =
                    kitchens.firstOrNull {
                        it.id == selectedKitchenId
                    }

                // Recalculate whenever the selected kitchen or timer changes.
                val selectedKitchenIsSubscribed = remember(
                    selectedKitchenId,
                    subscriptionTick
                ) {
                    isKitchenSubscribed(selectedKitchenId)
                }

                BackHandler(enabled = screen != "HOME") {
                    screen = when (screen) {
                        "DETAIL" -> "HOME"
                        "PAYWALL" -> previousScreen
                        "SUCCESS" -> "DETAIL"
                        else -> "HOME"
                    }
                }

                when (screen) {

                    "DETAIL" -> {
                        if (selectedKitchen != null) {

                            KitchenDetailScreen(
                                kitchen = selectedKitchen,
                                isSubscribed = selectedKitchenIsSubscribed,

                                onBack = {
                                    screen = "HOME"
                                },

                                onSubscribe = {
                                    if (
                                        isKitchenSubscribed(
                                            selectedKitchen.id
                                        )
                                    ) {
                                        successKitchenName =
                                            selectedKitchen.name
                                        screen = "SUCCESS"
                                    } else {
                                        selectedKitchenId =
                                            selectedKitchen.id

                                        previousScreen = "DETAIL"

                                        renewalDate = formatRenewalDate(
                                            System.currentTimeMillis() +
                                                    60 * 1000L
                                        )

                                        screen = "PAYWALL"
                                    }
                                }
                            )

                        } else {
                            KitchenListScreen(
                                viewModel = viewModel,
                                onKitchenClick = { kitchen ->
                                    selectedKitchenId = kitchen.id
                                    screen = "DETAIL"
                                }
                            )
                        }
                    }

                    "PAYWALL" -> {
                        PaywallScreen(
                            renewalDate = renewalDate,

                            onPurchase = {
                                val kitchen = kitchens.firstOrNull {
                                    it.id == selectedKitchenId
                                }

                                if (kitchen != null) {
                                    saveSubscribedKitchen(kitchen.id)

                                    successKitchenName = kitchen.name
                                    screen = "SUCCESS"
                                } else {
                                    // The launch-based paywall has no
                                    // kitchen selected yet.
                                    screen = "HOME"
                                }
                            },

                            onClose = {
                                screen = previousScreen
                            }
                        )
                    }

                    "SUCCESS" -> {
                        SubscriptionSuccessScreen(
                            kitchenName = successKitchenName,

                            onViewKitchen = {
                                screen = "DETAIL"
                            },

                            onViewAllKitchens = {
                                screen = "HOME"
                            }
                        )
                    }

                    else -> {
                        KitchenListScreen(
                            viewModel = viewModel,
                            onKitchenClick = { kitchen ->
                                selectedKitchenId = kitchen.id
                                screen = "DETAIL"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscriptionSuccessScreen(
    kitchenName: String,
    onViewKitchen: () -> Unit,
    onViewAllKitchens: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "✓",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Subscription successful!",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (kitchenName.isNotBlank()) {
                "You're subscribed to $kitchenName."
            } else {
                "Your subscription is confirmed."
            },
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onViewKitchen,
            enabled = kitchenName.isNotBlank()
        ) {
            Text("View kitchen")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onViewAllKitchens) {
            Text("View all kitchens")
        }
    }
}
