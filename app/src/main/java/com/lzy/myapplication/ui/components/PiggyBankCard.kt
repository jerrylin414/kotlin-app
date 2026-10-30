package com.lzy.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lzy.myapplication.data.PiggyBank
import com.lzy.myapplication.ui.theme.*

@Composable
fun PiggyBankCard(
    piggyBank: PiggyBank,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (piggyBank.targetAmount > 0) {
        (piggyBank.currentAmount / piggyBank.targetAmount).toFloat().coerceIn(0f, 1f)
    } else 0f
    val isComplete = piggyBank.currentAmount >= piggyBank.targetAmount
    val progressColor = if (isComplete) Color(0xFFFFD700) else SoftMint

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji 圖標
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(piggyBank.color).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(piggyBank.emoji, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = piggyBank.name,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (isComplete) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🎉", fontSize = 18.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${String.format("%.0f", piggyBank.currentAmount)} / ${String.format("%.0f", piggyBank.targetAmount)}",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                // 百分比
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isComplete) Color(0xFFFFD700) else TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 進度條
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFF0F0F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(progressColor)
                )
            }
        }
    }
}
