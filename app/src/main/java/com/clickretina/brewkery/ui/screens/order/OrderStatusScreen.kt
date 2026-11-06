package com.clickretina.brewkery.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clickretina.brewkery.data.repository.BrewkeryRepository
import com.clickretina.brewkery.ui.theme.AmberBadgeBg
import com.clickretina.brewkery.ui.theme.AmberBadgeText
import com.clickretina.brewkery.ui.theme.Crema
import com.clickretina.brewkery.ui.theme.CremaBorder
import com.clickretina.brewkery.ui.theme.CremaSoft
import com.clickretina.brewkery.ui.theme.CremaSurface
import com.clickretina.brewkery.ui.theme.EmeraldDark
import com.clickretina.brewkery.ui.theme.Espresso
import com.clickretina.brewkery.ui.theme.Terracotta
import com.clickretina.brewkery.ui.theme.TextMuted

@Composable
fun OrderStatusScreen(
    repository: BrewkeryRepository,
    onBackToMenu: () -> Unit
) {
    val activeOrder by repository.activeOrder.collectAsState()

    Scaffold(
        containerColor = Crema
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Mug Badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(CremaSoft)
                        .border(2.dp, Terracotta, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "☕",
                        fontSize = 36.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ORDER DISPATCHED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Terracotta,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Brewing in Progress!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Espresso
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Your ticket was dispatched to our barista.",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Ticket Card matching prototype lines 415-445
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(CremaSurface)
                        .border(1.dp, CremaBorder, RoundedCornerShape(18.dp))
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ORDER TICKET",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = activeOrder?.orderId ?: "#BK-10001",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Espresso,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(AmberBadgeBg)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = activeOrder?.status ?: "PREPARING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberBadgeText
                            )
                        }
                    }

                    HorizontalDivider(color = CremaBorder.copy(alpha = 0.6f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Estimated Wait:", fontSize = 12.sp, color = TextMuted)
                        Text(
                            text = activeOrder?.estimatedWait ?: "20 - 30 minutes",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Terracotta
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Items Ordered:", fontSize = 12.sp, color = TextMuted)
                        Text(
                            text = "${activeOrder?.itemCount ?: 1} Item(s)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Espresso
                        )
                    }

                    HorizontalDivider(color = CremaBorder.copy(alpha = 0.6f))

                    Column {
                        Text(
                            text = "Status:",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Barista accepted your order!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldDark
                        )
                    }
                }
            }

            // Return to Menu CTA
            Button(
                onClick = onBackToMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Espresso)
            ) {
                Text(
                    text = "Back to Menu",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}
