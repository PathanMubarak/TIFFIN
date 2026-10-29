
package com.propel.tiffin.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.propel.tiffin.data.Kitchen
import com.propel.tiffin.viewmodel.KitchenUiState
import com.propel.tiffin.viewmodel.KitchenViewModel

private val Cream = Color(0xFFFAF8F3)
private val ForestGreen = Color(0xFF245C45)
private val DarkText = Color(0xFF202820)
private val MutedText = Color(0xFF777A70)
private val BorderColor = Color(0xFFE4E1D8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenListScreen(
    viewModel: KitchenViewModel,
    onKitchenClick: (Kitchen) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var search by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val kitchens = (uiState as? KitchenUiState.Success)
        ?.kitchens.orEmpty()

    val filteredKitchens = kitchens.filter { kitchen ->
        val matchesSearch =
            kitchen.name.contains(search, ignoreCase = true) ||
                    kitchen.cuisine.contains(search, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Veg" -> kitchen.isVeg
            "Non-veg" -> !kitchen.isVeg
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "TIFFIN",
                            fontSize = 12.sp,
                            letterSpacing = 3.sp,
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Homemade meals, made with care.",
                            fontSize = 12.sp,
                            color = MutedText
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(42.dp)
                            .background(
                                Color(0xFFDCECE0),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🍱", fontSize = 23.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Cream
                )
            )
        },
        containerColor = Cream
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .padding(top = 12.dp, bottom = 12.dp)
            ) {
                Text(
                    text = "A better kind of",
                    fontSize = 16.sp,
                    color = DarkText
                )

                Text(
                    text = "everyday lunch.",
                    fontSize = 22.sp,
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(22.dp))

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Search kitchens or cuisines")
                    },
                    leadingIcon = {
                        Text("⌕", fontSize = 27.sp)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    listOf("All", "Veg", "Non-veg").forEach { filter ->
                        val selected = selectedFilter == filter

                        Surface(
                            modifier = Modifier.clickable {
                                selectedFilter = filter
                            },
                            shape = RoundedCornerShape(50),
                            color = if (selected) ForestGreen
                            else Color.White,
                            border = if (selected) null
                            else BorderStroke(1.dp, BorderColor)
                        ) {
                            Text(
                                text = filter,
                                modifier = Modifier.padding(
                                    horizontal = 18.dp,
                                    vertical = 10.dp
                                ),
                                color = if (selected) Color.White
                                else Color(0xFF50554D),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(22.dp))

                Text(
                    text = "Your neighbourhood kitchens",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Freshly prepared. Ready for your day.",
                    fontSize = 12.sp,
                    color = MutedText
                )
            }

            when (val state = uiState) {
                KitchenUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ForestGreen)
                    }
                }

                KitchenUiState.Empty -> {
                    MessageState(
                        title = "No kitchens available",
                        message = "Please check back soon.",
                        buttonText = "Try again",
                        onClick = viewModel::loadKitchens
                    )
                }

                is KitchenUiState.Error -> {
                    MessageState(
                        title = "We couldn't load your kitchens",
                        message = state.message,
                        buttonText = "Retry",
                        onClick = viewModel::loadKitchens
                    )
                }

                is KitchenUiState.Success -> {
                    if (filteredKitchens.isEmpty()) {
                        MessageState(
                            title = "No matching kitchens",
                            message = "Try another search or filter.",
                            buttonText = "Clear filters",
                            onClick = {
                                search = ""
                                selectedFilter = "All"
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 20.dp,
                                end = 20.dp,
                                top = 2.dp,
                                bottom = 24.dp
                            ),
                            verticalArrangement =
                                Arrangement.spacedBy(14.dp)
                        ) {
                            items(
                                items = filteredKitchens,
                                key = { it.id }
                            ) { kitchen ->
                                KitchenCard(
                                    kitchen = kitchen,
                                    onClick = {
                                        onKitchenClick(kitchen)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun KitchenCard(
    kitchen: Kitchen,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BorderColor),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = kitchen.name,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = kitchen.cuisine,
                        fontSize = 13.sp,
                        color = MutedText
                    )
                }

                Surface(
                    color = Color(0xFFFFF3D8),
                    shape = RoundedCornerShape(9.dp)
                ) {
                    Text(
                        text = "★ ${kitchen.rating}",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 7.dp
                        ),
                        color = Color(0xFF795719),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = if (kitchen.isVeg) "●  Pure vegetarian"
                else "●  Veg & non-veg",
                fontSize = 12.sp,
                color = if (kitchen.isVeg) ForestGreen
                else Color(0xFFAD573F)
            )

            if (kitchen.tags.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))

                Text(
                    text = kitchen.tags.joinToString("  ·  "),
                    fontSize = 12.sp,
                    color = MutedText
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "₹${kitchen.price}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )

                    Text(
                        text = "per meal",
                        fontSize = 11.sp,
                        color = MutedText
                    )
                }

                Text(
                    text = "◷  ${kitchen.deliveryTime}",
                    fontSize = 12.sp,
                    color = MutedText
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "View weekly menu  →",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ForestGreen
            )
        }
    }
}

@Composable
private fun MessageState(
    title: String,
    message: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = message,
                fontSize = 14.sp,
                color = MutedText
            )

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForestGreen
                )
            ) {
                Text(buttonText)
            }
        }
    }
}
