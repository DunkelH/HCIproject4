package com.example.minddiary.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.minddiary.R

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String
) {
    Surface(
        // tonalElevation과 shadowElevation을 주어 약간 떠 있는 느낌을 줍니다.
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                // 시스템 네비게이션 바 영역을 존중하도록 패딩을 적용합니다.
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(vertical = 8.dp), // 기존의 수직 패딩
            horizontalArrangement = Arrangement.SpaceAround, // 아이템을 균등하게 배치합니다.
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. 캘린더 탭 (홈 화면)
            BottomNavItem(
                iconRes = R.drawable.ic_nav_calendar,
                isSelected = currentRoute == "home",
                onClick = {
                    if (currentRoute != "home") {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )

            // 2. 감정 리포트 탭
            BottomNavItem(
                iconRes = R.drawable.ic_nav_stats,
                isSelected = currentRoute == "report",
                onClick = {
                    if (currentRoute != "report") {
                        navController.navigate("report") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )

            // 3. 일기 탭
            BottomNavItem(
                iconRes = R.drawable.ic_nav_book,
                isSelected = currentRoute == "diary",
                onClick = {
                    if (currentRoute != "diary") {
                        navController.navigate("diary") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isSelected) Color(0xFFF29A5A) else Color(0xFF999999)

    Image(
        painter = painterResource(id = iconRes),
        contentDescription = null,
        modifier = Modifier
            .size(24.dp)
            .clickable { onClick() },
        colorFilter = ColorFilter.tint(tint)
    )
}
