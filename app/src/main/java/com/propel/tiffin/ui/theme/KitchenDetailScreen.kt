
package com.propel.tiffin.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.propel.tiffin.data.Kitchen

private val Forest = Color(0xFF245C45)
private val Cream = Color(0xFFFAF8F3)
private val Ink = Color(0xFF202820)
private val Muted = Color(0xFF777A70)

@Composable
fun KitchenDetailScreen(
    kitchen: Kitchen,
    isSubscribed: Boolean,
    onBack: () -> Unit,
    onSubscribe: () -> Unit
) {
    Scaffold(
        containerColor = Cream,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text("‹  Back", color = Forest, fontSize = 16.sp)
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text = "KITCHEN",
                    color = Forest,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        bottomBar = {
            Surface(
                shadowElevation = 12.dp,
                color = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 14.dp,
                            bottom = 12.dp
                        )
                ) {
                    Button(
                        onClick = onSubscribe,
                        enabled = !isSubscribed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (isSubscribed) {
                                "Subscribed ✓"
                            } else {
                                "Subscribe to weekly meals →"
                            },
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = 22.dp,
                end = 22.dp,
                bottom = 48.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .background(
                            Color(0xFFE9E1D0),
                            RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (kitchen.isVeg) "🥗" else "🍛",
                        fontSize = 76.sp
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    kitchen.name,
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    kitchen.cuisine,
                    fontSize = 15.sp,
                    color = Muted
                )

                Spacer(Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DetailPill("★ ${kitchen.rating}")
                    DetailPill(kitchen.deliveryTime)
                    DetailPill(if (kitchen.isVeg) "Vegetarian" else "Non-vegetarian")
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    kitchen.description,
                    fontSize = 15.sp,
                    lineHeight = 23.sp,
                    color = Muted
                )

                Spacer(Modifier.height(22.dp))

                Text(
                    "This week's menu",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    "A little homemade goodness, every day.",
                    fontSize = 13.sp,
                    color = Muted
                )
            }

            items(kitchen.menu, key = { it.day }) { meal ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    Color(0xFFDCECE0),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                meal.day.take(1),
                                color = Forest,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(Modifier.width(13.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                meal.day.uppercase(),
                                color = Forest,
                                fontSize = 10.sp,
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                meal.mealName,
                                color = Ink,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(Modifier.height(5.dp))

                            Text(
                                meal.description,
                                color = Muted,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFDCECE0)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(
                            "Made with care",
                            color = Forest,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "Discover everyday meals from independent home kitchens.",
                            color = Ink,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailPill(text: String) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text,
            modifier = Modifier.padding(
                horizontal = 11.dp,
                vertical = 8.dp
            ),
            fontSize = 12.sp,
            color = Ink,
            fontWeight = FontWeight.Medium
        )
    }
}
