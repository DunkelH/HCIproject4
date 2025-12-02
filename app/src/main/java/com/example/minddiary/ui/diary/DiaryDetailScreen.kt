package com.example.minddiary.ui.diary

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.minddiary.R
import com.example.minddiary.data.DiaryEntry
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// 색상 정의
private val BackgroundColor = Color(0xFFF0F2F7)
private val CardBackgroundColor = Color.White
private val TextDark = Color(0xFF1B2023)
private val TextGrey = Color(0xFF6B7280)
private val TextLightGrey = Color(0xFF9CA3AF)
private val BorderColor = Color(0xFFE5E7EB)
private val ButtonTextColor = Color(0xFF4B5563)

// 감정별 색상
private val EmotionHappy = Color(0xFFF9E28C)
private val EmotionSad = Color(0xFF6B9BD2)
private val EmotionAngry = Color(0xFFFFC7C3)
private val EmotionUnrest = Color(0xFFD2C4E8)
private val EmotionTired = Color(0xFFB2B2B2)
private val EmotionExcitement = Color(0xFFFFC0CB)

@Composable
fun DiaryDetailScreen(
    navController: NavController,
    diaryId: Long,
    viewModel: DiaryViewModel
) {
    val context = LocalContext.current
    var diary by remember { mutableStateOf<DiaryEntry?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    // diaryId로 일기 조회
    LaunchedEffect(diaryId) {
        diary = viewModel.getDiaryById(diaryId)
        isLoading = false
        
        // 일기를 찾을 수 없으면 뒤로가기
        if (diary == null) {
            navController.popBackStack()
        }
    }
    
    // 로딩 중이거나 일기가 없으면 아무것도 표시하지 않음
    val currentDiary = diary
    if (isLoading || currentDiary == null) {
        return
    }
    
    val scrollState = rememberScrollState()
    
    // 날짜 포맷팅
    val diaryDate = Instant.ofEpochMilli(currentDiary.createdAt)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.getDefault())
    val formattedDate = diaryDate.format(dateFormatter)
    
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
            // 상단바
            TopBar(
                date = formattedDate,
                onBackClick = { navController.popBackStack() }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 일기 카드
            DiaryDetailCard(
                diary = currentDiary,
                onEditClick = {
                    // 편집 화면으로 이동
                    navController.navigate("diaryEdit/${currentDiary.id}")
                },
                onDeleteClick = {
                    // 삭제 확인 후 삭제
                    viewModel.deleteDiary(currentDiary)
                    Toast.makeText(context, "삭제되었습니다", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                onShareClick = {
                    // 공유 기능 (나중에 구현)
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TopBar(
    date: String,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBackgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = "뒤로가기",
                    modifier = Modifier.size(24.dp),
                    tint = TextDark
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = date,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = TextDark
            )
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun DiaryDetailCard(
    diary: DiaryEntry,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 사진
            val photoUris = diary.photoUris.split(",").filter { it.isNotBlank() }
            if (photoUris.isNotEmpty()) {
                AsyncImage(
                    model = photoUris.first(),
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
                    contentDescription = "기본 사진",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            
            // 감정 태그
            EmotionTagDetail(emotion = diary.emotion)
            
            // 일기 내용
            Text(
                text = diary.content,
                fontSize = 14.sp,
                color = Color(0xFF374151),
                modifier = Modifier.fillMaxWidth()
            )
            
            // 단어 수
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    color = Color.Transparent
                ) {
                    Text(
                        text = "${diary.wordCount}개 단어",
                        fontSize = 14.sp,
                        color = TextGrey,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            
            // 날씨/위치 정보
            WeatherLocationInfoDetail(
                weather = diary.weather,
                temperature = diary.temperature,
                location = diary.location
            )
            
            // 버튼들
            ActionButtons(
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onShareClick = onShareClick
            )
        }
    }
}

@Composable
private fun EmotionTagDetail(emotion: String) {
    val (backgroundColor, label) = when (emotion.uppercase()) {
        "HAPPY" -> EmotionHappy to "행복"
        "SAD" -> EmotionSad to "슬픔"
        "ANGRY" -> Color(0xFFE57373) to "화남"
        "UNREST" -> EmotionUnrest to "불안"
        "TIRED" -> EmotionTired to "피곤"
        "EXCITEMENT" -> EmotionExcitement to "설렘"
        else -> EmotionHappy to "행복"
    }
    
    val emotionIcon = getEmotionIcon(emotion)
    
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(id = emotionIcon),
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

private fun getEmotionIcon(emotion: String): Int {
    return when (emotion.uppercase()) {
        "HAPPY" -> R.drawable.ic_happy
        "SAD" -> R.drawable.ic_sad
        "ANGRY" -> R.drawable.ic_mad
        "UNREST" -> R.drawable.ic_anxious
        "TIRED" -> R.drawable.ic_tired
        "EXCITEMENT" -> R.drawable.ic_excited
        else -> R.drawable.ic_happy
    }
}

@Composable
private fun WeatherLocationInfoDetail(
    weather: String,
    temperature: String,
    location: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 날씨 정보
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = CardBackgroundColor,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "☁️", fontSize = 16.sp)
                Text(
                    text = weather,
                    fontSize = 14.sp,
                    color = TextLightGrey
                )
                Text(
                    text = temperature,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextLightGrey
                )
            }
        }
        
        // 위치 정보
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = CardBackgroundColor,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "📍", fontSize = 16.sp)
                val locationParts = location.split(" ")
                if (locationParts.size >= 2) {
                    Text(
                        text = locationParts[0],
                        fontSize = 14.sp,
                        color = TextLightGrey
                    )
                    Text(
                        text = locationParts[1],
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextLightGrey
                    )
                } else {
                    Text(
                        text = location,
                        fontSize = 14.sp,
                        color = TextLightGrey
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 편집 버튼
        ActionButton(
            icon = R.drawable.button_fix,
            text = "편집",
            onClick = onEditClick
        )
        
        Spacer(modifier = Modifier.width(34.dp))
        
        // 삭제 버튼
        ActionButton(
            icon = R.drawable.button_del,
            text = "삭제",
            onClick = onDeleteClick
        )
        
        Spacer(modifier = Modifier.width(34.dp))
        
        // 공유 버튼
        ActionButton(
            icon = R.drawable.button_share,
            text = "공유",
            onClick = onShareClick
        )
    }
}

@Composable
private fun ActionButton(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = text,
            modifier = Modifier
                .size(40.dp)
                .clickable(onClick = onClick)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = ButtonTextColor,
            textAlign = TextAlign.Center
        )
    }
}

