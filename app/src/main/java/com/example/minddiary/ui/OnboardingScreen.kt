package com.example.minddiary.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minddiary.R
import kotlinx.coroutines.delay
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
@Composable
fun OnboardingScreen(onFinished: (String) -> Unit) {
    var showNameCard by remember { mutableStateOf(false) }

    // 2초 뒤에 true로 바뀌면서 애니메이션 시작
    LaunchedEffect(Unit) {
        delay(2000)
        showNameCard = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F2EF))
    ) {
        // 가운데 로고
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.onboarding_logo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.8f)   // 화면 가로의 70%까지 늘리기
                    .aspectRatio(412f / 917f) // 원본 비율에 맞추고 싶으면 (대략 값 넣기)
            )

            Spacer(modifier = Modifier.height(8.dp))

        }

        // 아래에서 위로 슬라이드 인 되는 이름 입력 카드
        AnimatedVisibility(
            visible = showNameCard,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn()
        ) {
            NameInputCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp) ,
                onNext = onFinished   // 🔸 바깥에서 받은 콜백 그대로 전달
            )
        }
    }
}

@Composable
fun NameInputCard(modifier: Modifier = Modifier,
                  onNext: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {

            // 인사 문구
            Text(
                text = "안녕하세요 :)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF000000) // 검은색
            )
            Text(
                text = "마인드 다이어리입니다.",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF000000) // 검은색
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ⭐ Figma 스타일 TextField
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "이름 입력",
                        color = Color(0xFFBDBDBD)
                    )
                },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color(0xFF000000) // 입력 텍스트 검은색
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF29A5A),      // 포커스 시 주황색
                    unfocusedBorderColor = Color(0xFFE0E0E0),    // 기본 연회색 테두리
                    focusedContainerColor = Color.White,          // 배경 완전 흰색
                    unfocusedContainerColor = Color.White,
                    cursorColor = Color(0xFFF29A5A),
                    focusedTextColor = Color(0xFF000000),        // 포커스 시 텍스트 색상
                    unfocusedTextColor = Color(0xFF000000)       // 비포커스 시 텍스트 색상
                )
            )

            Spacer(modifier = Modifier.height(26.dp))

            // ⭐ 피그마 PNG 버튼 적용
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.name_button),
                    contentDescription = "Next Button",
                    modifier = Modifier
                        .size(65.dp)       // PNG 크기에 맞춰 조절
                        .clickable {
                            onNext(name)   // 🔸 여기서 이름 넘김 (지금은 써도 되고 안 써도 됨)
                        }
                )
            }
        }
    }
}



