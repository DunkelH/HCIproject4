package com.example.minddiary.ui.diary

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.minddiary.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 색상 정의
private val BackgroundColor = Color(0xFFF0F2F7)
private val CardBackgroundColor = Color.White
private val PrimaryBlue = Color(0xFF537FF1)
private val LightBlue = Color(0xFFDCE8FF)
private val TextDark = Color(0xFF4B5563)
private val TextGrey = Color(0xFF6B7280)
private val TextLightGrey = Color(0xFF9CA3AF)
private val BorderColor = Color(0xFFE5E7EB)
private val ContentCardBg = Color(0x4DFFF6F1)  // rgba(255, 246, 241, 0.3)
private val EmotionTagColor = Color(0xFFD2C4E8)
private val DiaryTextColor = Color(0xFF374151)
private val DateTimeColor = Color(0xFF757575)

@Composable
fun DiaryCompleteScreen(
    navController: NavController,
    viewModel: DiaryViewModel
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    
    // ViewModel에서 데이터 가져오기
    val selectedEmotion = viewModel.selectedEmotion
    val selectedPhotos = viewModel.selectedPhotos
    val diaryContent = viewModel.diaryContent.ifEmpty { 
        "계획이 잘 안 풀려서 조금 불안했다. 그래도 다시 정리해보니 괜찮을 것 같다. 내일은 좀 더 차분히 접근해보려고 한다." 
    }
    
    // 현재 시간
    val currentDateTime = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
    }
    
    // 단어 수 계산
    val wordCount = diaryContent.split(" ", "\n").filter { it.isNotBlank() }.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // 상단 헤더 (프로그레스 100%)
            TopHeaderComplete(progress = 1.0f)

            Spacer(modifier = Modifier.height(16.dp))

            // 메인 콘텐츠 카드
            DiaryCompleteCard(
                diaryContent = diaryContent,
                wordCount = wordCount,
                dateTime = currentDateTime,
                selectedEmotion = selectedEmotion,
                selectedPhotos = selectedPhotos
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 하단 버튼들
            CompleteButtons(
                onEditClick = {
                    // 수정 - DiaryWriteStep2Screen으로 이동
                    navController.navigate("diaryWriteStep2") {
                        popUpTo("diaryComplete") { inclusive = true }
                    }
                },
                onSaveClick = {
                    // 일기 저장
                    viewModel.saveDiary(
                        weather = "흐림",
                        temperature = "16℃",
                        location = "서울 광진구",
                        onSuccess = {
                            // 토스트 메시지 표시
                            Toast.makeText(context, "일기가 저장되었습니다", Toast.LENGTH_SHORT).show()
                            // 홈으로 이동
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = false }
                            }
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TopHeaderComplete(progress: Float) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 흰색 타이틀바 (뒤로가기 버튼 없음)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            color = Color.White
        ) {
            // 빈 타이틀바
        }

        // 프로그레스 바 (100%)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = PrimaryBlue,
            trackColor = LightBlue,
        )
    }
}

@Composable
private fun DiaryCompleteCard(
    diaryContent: String,
    wordCount: Int,
    dateTime: String,
    selectedEmotion: EmotionType?,
    selectedPhotos: List<android.net.Uri>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 제목
            Text(
                text = "일기 저장",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 부제목
            Text(
                text = "소중한 하루를 기록해주셔서 감사해요",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = TextDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 내부 콘텐츠 카드 (연한 주황색 배경)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ContentCardBg),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 사진 (선택된 첫 번째 사진 또는 기본 이미지)
                    if (selectedPhotos.isNotEmpty()) {
                        AsyncImage(
                            model = selectedPhotos.first(),
                            contentDescription = "일기 사진",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.samplepic),
                            contentDescription = "일기 사진",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 감정 태그 (선택된 감정 표시)
                    EmotionTagComplete(selectedEmotion = selectedEmotion)

                    Spacer(modifier = Modifier.height(16.dp))

                    // 일기 내용
                    Text(
                        text = diaryContent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = DiaryTextColor,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 단어 수 + 날짜/시간
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 날짜/시간
                        Text(
                            text = dateTime,
                            fontSize = 14.sp,
                            color = DateTimeColor
                        )

                        // 단어 수
                        Text(
                            text = "${wordCount}개 단어",
                            fontSize = 14.sp,
                            color = TextGrey,
                            textAlign = TextAlign.End
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 날씨/위치 정보
                    WeatherLocationInfoComplete()
                }
            }
        }
    }
}

// 감정별 색상 및 아이콘 정보
private fun getEmotionColor(emotion: EmotionType): Color {
    return when (emotion) {
        EmotionType.HAPPY -> Color(0xFFFFD966)      // 노란색
        EmotionType.SAD -> Color(0xFF6B9BD2)        // 파란색
        EmotionType.ANGRY -> Color(0xFFE57373)      // 빨간색
        EmotionType.UNREST -> Color(0xFFD2C4E8)     // 보라색
        EmotionType.TIRED -> Color(0xFF90A4AE)      // 회색
        EmotionType.EXCITEMENT -> Color(0xFFFFC0CB) // 핑크색
    }
}

private fun getEmotionIcon(emotion: EmotionType): Int {
    return when (emotion) {
        EmotionType.HAPPY -> R.drawable.ic_happy
        EmotionType.SAD -> R.drawable.ic_sad
        EmotionType.ANGRY -> R.drawable.ic_mad
        EmotionType.UNREST -> R.drawable.ic_anxious
        EmotionType.TIRED -> R.drawable.ic_tired
        EmotionType.EXCITEMENT -> R.drawable.ic_excited
    }
}

@Composable
private fun EmotionTagComplete(selectedEmotion: EmotionType?) {
    // 감정이 선택되지 않았으면 표시하지 않음
    if (selectedEmotion == null) return
    
    val emotionColor = getEmotionColor(selectedEmotion)
    val emotionIcon = getEmotionIcon(selectedEmotion)
    
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(100.dp),
            color = emotionColor
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = emotionIcon),
                    contentDescription = selectedEmotion.label,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Text(
                    text = selectedEmotion.label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun WeatherLocationInfoComplete() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 날씨 정보
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "☁️", fontSize = 16.sp)
                Text(
                    text = "흐림",
                    fontSize = 14.sp,
                    color = TextLightGrey
                )
                Text(
                    text = "16℃",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextLightGrey
                )
            }
        }

        // 위치 정보
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "📍", fontSize = 16.sp)
                Text(
                    text = "서울",
                    fontSize = 14.sp,
                    color = TextLightGrey
                )
                Text(
                    text = "광진구",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextLightGrey
                )
            }
        }
    }
}

@Composable
private fun CompleteButtons(
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 수정 버튼
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            shape = RoundedCornerShape(9999.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor),
            shadowElevation = 4.dp,
            onClick = onEditClick
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "수정",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )
            }
        }

        // 저장하기 버튼
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            shape = RoundedCornerShape(9999.dp),
            color = PrimaryBlue,
            shadowElevation = 4.dp,
            onClick = onSaveClick
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "저장하기",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


