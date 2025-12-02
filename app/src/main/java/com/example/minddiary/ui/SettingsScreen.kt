package com.example.minddiary.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.minddiary.R

@Composable
fun SettingsScreen(
    navController: NavController
) {
    val bgColor = Color(0xFFF9F2EF)

    // 테마 선택 더미 상태
    var selectedTheme by remember { mutableStateOf("light") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            // ───── 상단 바: 뒤로가기 + 제목 ─────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_left),
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "설정",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // 오른쪽 자리는 비워두기(아이콘 균형 맞추려고)
                Spacer(modifier = Modifier.size(48.dp))
            }

            // ───── 테마 + 데이터 관리 카드 ─────
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // 테마
                    Text(
                        text = "테마",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ThemeRadioRow(
                        label = "라이트",
                        value = "light",
                        selectedTheme = selectedTheme,
                        onSelected = { selectedTheme = it }
                    )
                    ThemeRadioRow(
                        label = "다크",
                        value = "dark",
                        selectedTheme = selectedTheme,
                        onSelected = { selectedTheme = it }
                    )
                    ThemeRadioRow(
                        label = "시스템 설정",
                        value = "system",
                        selectedTheme = selectedTheme,
                        onSelected = { selectedTheme = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Divider(color = Color(0xFFE0E0E0))

                    Spacer(modifier = Modifier.height(12.dp))

                    // 데이터 관리
                    Text(
                        text = "데이터 관리",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_download_pdf),
                                contentDescription = "Export PDF",
                                tint = Color(0xFF777777),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PDF로 내보내기",
                                fontSize = 13.sp,
                                color = Color(0xFF777777)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 하단 여유 공간
            Spacer(modifier = Modifier.weight(1f, fill = true))

            // ───── 로그아웃 버튼 ─────
            Button(
                onClick = { /* TODO: 로그아웃 로직 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF29A5A),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "로그아웃",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ThemeRadioRow(
    label: String,
    value: String,
    selectedTheme: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selectedTheme == value,
            onClick = { onSelected(value) }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 13.sp
        )
    }
}
