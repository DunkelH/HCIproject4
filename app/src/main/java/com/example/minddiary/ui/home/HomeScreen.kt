package com.example.minddiary.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.minddiary.R
import com.example.minddiary.data.DiaryEntry
import com.example.minddiary.ui.diary.DiaryViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// 색상 정의
private val BackgroundColor = Color(0xFFFFF6F1)
private val CardBackgroundColor = Color.White
private val TextDark = Color(0xFF3A3F49)
private val TextGrey = Color(0xFF9CA3AF)
private val TextMediumGrey = Color(0xFF6F787D)
private val BorderColor = Color(0xFFE5E7EB)
private val TodayBorderColor = Color(0xFFF67828)
private val SelectedDayColor = Color(0xFFF9E28C)

// 감정별 색상
private val EmotionHappy = Color(0xFFF9E28C)
private val EmotionSad = Color(0xFF6B9BD2)
private val EmotionAngry = Color(0xFFFFC7C3)
private val EmotionUnrest = Color(0xFFD2C4E8)
private val EmotionTired = Color(0xFFB2B2B2)
private val EmotionExcitement = Color(0xFFFFC0CB)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: DiaryViewModel? = null
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.from(LocalDate.now())) }
    
    // ViewModel에서 저장된 일기 목록 가져오기
    val allDiaries by viewModel?.allDiaries?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }
    
    // 선택된 날짜의 일기 필터링
    val selectedDateDiaries = allDiaries.filter { diary ->
        val diaryDate = Instant.ofEpochMilli(diary.createdAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        diaryDate == selectedDate
    }
    
    // 날짜별 감정 맵 생성
    val emotionByDate = allDiaries.associate { diary ->
        val diaryDate = Instant.ofEpochMilli(diary.createdAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        diaryDate to diary.emotion
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단바
            item {
                TopBar()
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
            
            // 캘린더 카드
            item {
                CalendarCard(
                    currentMonth = currentMonth,
                    selectedDate = selectedDate,
                    emotionByDate = emotionByDate,
                    diaryCount = allDiaries.size,
                    onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                    onNextMonth = { currentMonth = currentMonth.plusMonths(1) },
                    onDateSelected = { date -> selectedDate = date }
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // 선택한 날짜 섹션 헤더
            item {
                SelectedDateHeader(selectedDate = selectedDate)
            }
            
            item { Spacer(modifier = Modifier.height(12.dp)) }
            
            // 선택된 날짜의 일기 목록
            if (selectedDateDiaries.isEmpty()) {
                item {
                    EmptyDiaryCard()
                }
            } else {
                items(selectedDateDiaries) { diary ->
                    SavedDiaryCard(
                        diary = diary,
                        onClick = {
                            navController.navigate("diaryDetail/${diary.id}")
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

        // 플로팅 버튼
        FloatingWriteButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            onClick = { navController.navigate("diaryWrite") }
        )
    }
}

@Composable
private fun TopBar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        color = CardBackgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App logo",
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                painter = painterResource(id = R.drawable.text_home),
                contentDescription = "Home",
                modifier = Modifier
                    .height(24.dp)
                    .widthIn(min = 80.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarCard(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    emotionByDate: Map<LocalDate, String>,
    diaryCount: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 17.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 제목
            Text(
                text = "오늘의 마음을 기록해볼까요?",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 연속 기록 표시
            Text(
                text = "지금까지 ${diaryCount}개의 일기 기록중",
                fontSize = 14.sp,
                color = TextGrey
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 월 네비게이션
            MonthNavigator(
                currentMonth = currentMonth,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 캘린더 그리드
            CalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                emotionByDate = emotionByDate,
                onDateSelected = onDateSelected
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthNavigator(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        IconButton(
            onClick = onPreviousMonth,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                contentDescription = "이전 달",
                tint = TextDark
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_cal),
                contentDescription = "캘린더",
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "${currentMonth.year}년 ${currentMonth.monthValue}월",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )
        }
        
        IconButton(
            onClick = onNextMonth,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "다음 달",
                tint = TextDark
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    emotionByDate: Map<LocalDate, String>,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val weekDays = listOf("일", "월", "화", "수", "목", "금", "토")
    
    Column {
        // 요일 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDays.forEach { day ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
        
        // 날짜 그리드
        val firstDayOfMonth = currentMonth.atDay(1)
        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDayIndex = (firstDayOfMonth.dayOfWeek.value % 7)
        
        val rawDays: List<LocalDate?> =
            List(firstDayIndex) { null } + (1..daysInMonth).map { day -> currentMonth.atDay(day) }
        
        val days: List<LocalDate?> =
            if (rawDays.size % 7 == 0) rawDays
            else rawDays + List(7 - rawDays.size % 7) { null }
        
        days.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { date ->
                    CalendarDayCell(
                        date = date,
                        isToday = date == today,
                        isSelected = date == selectedDate,
                        emotion = date?.let { emotionByDate[it] },
                        onDateSelected = { date?.let { onDateSelected(it) } },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate?,
    isToday: Boolean,
    isSelected: Boolean,
    emotion: String?,
    onDateSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clickable(enabled = date != null) { onDateSelected() },
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            // 감정이 있으면 색상 배경과 함께 이모티콘 표시
            if (emotion != null) {
                val emotionIcon = getEmotionIcon(emotion)
                val emotionColor = getEmotionBackgroundColor(emotion)
                
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(emotionColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = emotionIcon),
                        contentDescription = emotion,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
            } else {
                // 감정이 없으면 날짜 표시
                val backgroundColor = when {
                    isSelected -> SelectedDayColor
                    else -> Color.Transparent
                }
                
                val borderColor = when {
                    isToday -> TodayBorderColor
                    else -> Color.Transparent
                }
                
                val textColor = when {
                    date.monthValue != date.monthValue -> TextGrey
                    else -> if (date.isBefore(LocalDate.now())) TextMediumGrey else Color(0xFF1B2023)
                }
                
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(backgroundColor, CircleShape)
                        .then(
                            if (isToday) Modifier.background(Color.Transparent, CircleShape)
                                .padding(1.dp)
                                .background(Color.Transparent, CircleShape)
                            else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isToday) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = Color.Transparent,
                            border = BorderStroke(1.dp, TodayBorderColor)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    fontSize = 14.sp,
                                    color = textColor
                                )
                            }
                        }
                    } else {
                        Text(
                            text = date.dayOfMonth.toString(),
                            fontSize = 14.sp,
                            color = textColor
                        )
                    }
                }
            }
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

private fun getEmotionBackgroundColor(emotion: String): Color {
    return when (emotion.uppercase()) {
        "HAPPY" -> EmotionHappy      // #F9E28C
        "SAD" -> EmotionSad          // #6B9BD2
        "ANGRY" -> EmotionAngry      // #FFC7C3
        "UNREST" -> EmotionUnrest    // #D2C4E8
        "TIRED" -> EmotionTired      // #B2B2B2
        "EXCITEMENT" -> EmotionExcitement // #FFB74D
        else -> EmotionHappy
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SelectedDateHeader(selectedDate: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Text(
            text = "선택한 날짜",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextDark
        )
        Text(
            text = selectedDate.format(formatter),
            fontSize = 14.sp,
            color = TextDark
        )
    }
}

@Composable
private fun EmptyDiaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "이 날짜에 작성된 일기가 없습니다",
                fontSize = 14.sp,
                color = TextGrey
            )
        }
    }
}

@Composable
private fun SavedDiaryCard(
    diary: DiaryEntry,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dateString = dateFormat.format(diary.createdAt)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 사진
            val photoUris = diary.photoUris.split(",").filter { it.isNotBlank() }
            if (photoUris.isNotEmpty()) {
                AsyncImage(
                    model = photoUris.first(),
                    contentDescription = "일기 사진",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.samplepic),
                    contentDescription = "기본 사진",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            
            // 내용
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 감정 태그와 날짜를 같은 Row에 배치
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EmotionTag(emotion = diary.emotion)
                    Text(
                        text = dateString,
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 일기 내용
                Text(
                    text = diary.content,
                    fontSize = 14.sp,
                    color = Color(0xFF374151),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 위치 정보
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(text = "📍", fontSize = 12.sp)
                    Text(
                        text = diary.location,
                        fontSize = 10.sp,
                        color = TextGrey
                    )
                }
            }
        }
    }
}

@Composable
private fun EmotionTag(emotion: String) {
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

@Composable
private fun FloatingWriteButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val imageRes = if (isPressed) R.drawable.button_write_pressed else R.drawable.button_write

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "일기 작성",
        modifier = modifier
            .size(80.dp)
            .clickable(interactionSource = interactionSource, indication = null) {
                onClick()
            }
    )
}

