package com.lzy.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lzy.myapplication.ui.theme.*

/**
 * 可愛的動物頭像組件 - 用 emoji 做卡通動物圖案
 */
@Composable
fun AnimalAvatar(
    emoji: String,
    backgroundColor: Color,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = (size.value * 0.5f).sp
        )
    }
}

/**
 * 小動物裝飾 - 用於空狀態或頁面裝飾
 */
@Composable
fun CuteAnimalDecoration(
    emoji: String = "🐷",
    size: Dp = 80.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(PeachPink.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = (size.value * 0.55f).sp
        )
    }
}

/**
 * 浮動小動物背景裝飾
 */
@Composable
fun FloatingAnimals(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Text("🐱", fontSize = 24.sp, modifier = Modifier.align(Alignment.TopStart))
        Text("🐶", fontSize = 20.sp, modifier = Modifier.align(Alignment.TopEnd))
        Text("🐰", fontSize = 22.sp, modifier = Modifier.align(Alignment.BottomStart))
        Text("🦊", fontSize = 18.sp, modifier = Modifier.align(Alignment.BottomEnd))
    }
}
