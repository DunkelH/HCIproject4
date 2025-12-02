package com.example.minddiary.ui.diary

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

// 색상 정의
private val BackgroundColor = Color(0xFFF0F2F7)
private val PrimaryBlue = Color(0xFF537FF1)
private val LightBlue = Color(0xFFDCE8FF)
private val TextDark = Color(0xFF000000)

@Composable
fun DiaryLoadingScreen(navController: NavController) {
    // 5초 후 다음 화면으로 이동
    LaunchedEffect(Unit) {
        delay(5000L)
        navController.navigate("diaryWriteStep2") {
            popUpTo("diaryLoading") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // 상단 헤더 (타이틀바 + 프로그레스바)
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 흰색 타이틀바
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                color = Color.White
            ) {
                // 빈 타이틀바 (로딩 중에는 뒤로가기 버튼 없음)
            }

            // 프로그레스 바 (40% 진행)
            LinearProgressIndicator(
                progress = { 0.4f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = PrimaryBlue,
                trackColor = LightBlue,
            )
        }

        // 중앙 로딩 콘텐츠
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 회전하는 원형 로딩 스피너
            RotatingCircularProgressIndicator()

            Spacer(modifier = Modifier.height(40.dp))

            // 로딩 텍스트
            Text(
                text = "AI가 일기를 작성하고 있어요",
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = TextDark,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RotatingCircularProgressIndicator() {
    // 무한 회전 애니메이션
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(140.dp)
            .rotate(rotation),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(140.dp),
            color = PrimaryBlue,
            strokeWidth = 6.dp,
            trackColor = LightBlue,
            strokeCap = StrokeCap.Round
        )
    }
}


