package com.example.minddiary.ui.report

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minddiary.R
import com.example.minddiary.data.DiaryEntry
import com.example.minddiary.ui.diary.DiaryViewModel
import com.example.minddiary.ui.diary.EmotionType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// ───────────────── 데이터 클래스 ─────────────────

data class WeeklyEmotionSummary(
    val mainEmotion: String,
    val mainEmotionLabel: String,
    val mainEmotionIcon: Int,
    val mainEmotionType: EmotionType,
    val totalCount: Int,
    val averageScore: Int,
    val message: String
)

data class DailyActivity(
    val dateLabel: String,
    val count: Int
)

data class EmotionDistributionItem(
    val name: String,
    val count: Int,
    val color: Color,
    val iconRes: Int,
    val progress: Float
)

data class BadgeItem(
    val title: String,
    val description: String,
    val iconResId: Int,
    val isAchieved: Boolean
)

// ───────────────── 메인 화면 ─────────────────

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EmotionReportScreen(
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel = viewModel()
) {
    // 일기 데이터 가져오기
    val allDiaries by viewModel.allDiaries.collectAsState(initial = emptyList())
    
    // 현재 날짜 기준으로 이번 주 계산
    val today = LocalDate.now()
    val startOfWeek = today.minusDays((today.dayOfWeek.value - 1).toLong())
    val endOfWeek = startOfWeek.plusDays(6)
    
    // 이번 주 일기 필터링
    val weeklyDiaries = allDiaries.filter { diary ->
        val diaryDate = Instant.ofEpochMilli(diary.createdAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        diaryDate.isAfter(startOfWeek.minusDays(1)) && diaryDate.isBefore(endOfWeek.plusDays(1))
    }
    
    // 이번 주 가장 많이 느낀 감정 계산
    val weeklySummary = remember(weeklyDiaries) {
        if (weeklyDiaries.isEmpty()) {
            WeeklyEmotionSummary(
                mainEmotion = "기록 없음",
                mainEmotionLabel = "기록 없음",
                mainEmotionIcon = R.drawable.ic_happy,
                mainEmotionType = EmotionType.HAPPY,
                totalCount = 0,
                averageScore = 0,
                message = "이번 주 일기를 작성해보세요"
            )
        } else {
            val emotionCounts = weeklyDiaries.groupingBy { it.emotion }.eachCount()
            val mostFrequentEmotion = emotionCounts.maxByOrNull { it.value }?.key ?: "HAPPY"
            
            val emotionType = try {
                EmotionType.valueOf(mostFrequentEmotion)
            } catch (e: Exception) {
                EmotionType.HAPPY
            }
            
            val totalCount = weeklyDiaries.size
            // 긍정도 계산 (행복, 설렘은 긍정, 나머지는 부정으로 간주)
            val positiveCount = weeklyDiaries.count { 
                it.emotion == "HAPPY" || it.emotion == "EXCITEMENT" 
            }
            val averageScore = if (totalCount > 0) (positiveCount * 100 / totalCount) else 0
            
            val messages = listOf(
                "힘든 시간도 있지만, 당신은 잘 해내고 있어요",
                "오늘도 수고하셨어요",
                "작은 변화도 소중한 기록이에요",
                "당신의 감정을 기록하는 것만으로도 충분해요"
            )
            val message = messages[totalCount % messages.size]
            
            WeeklyEmotionSummary(
                mainEmotion = "${emotionType.label}해요",
                mainEmotionLabel = emotionType.label,
                mainEmotionIcon = getEmotionIcon(emotionType),
                mainEmotionType = emotionType,
                totalCount = totalCount,
                averageScore = averageScore,
                message = message
            )
        }
    }
    
    // 주간 활동 데이터 생성 (최근 5일)
    val weeklyActivities = remember(weeklyDiaries, startOfWeek) {
        val today = LocalDate.now()
        (0..4).map { dayOffset ->
            val date = today.minusDays(dayOffset.toLong())
            val dateLabel = date.format(DateTimeFormatter.ofPattern("M월 d일", Locale.getDefault()))
            val count = weeklyDiaries.count { diary ->
                val diaryDate = Instant.ofEpochMilli(diary.createdAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                diaryDate == date
            }
            DailyActivity(dateLabel, count)
        }.reversed() // 오래된 날짜부터 최신 날짜 순서로
    }
    
    // 감정 분포 계산
    val emotionDistribution = remember(weeklyDiaries) {
        val emotionCounts = weeklyDiaries.groupingBy { it.emotion }.eachCount()
        val total = weeklyDiaries.size.coerceAtLeast(1)
        
        EmotionType.entries.map { emotionType ->
            val count = emotionCounts[emotionType.name] ?: 0
            val progress = count.toFloat() / total.toFloat()
            EmotionDistributionItem(
                name = emotionType.label,
                count = count,
                color = getEmotionColor(emotionType),
                iconRes = getEmotionIcon(emotionType),
                progress = progress
            )
        }.filter { it.count > 0 }.sortedByDescending { it.count }
    }
    
    // 뱃지 달성 여부 계산
    val badges = remember(allDiaries) {
        val allDates = allDiaries.map { diary ->
            Instant.ofEpochMilli(diary.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.distinct().sorted()
        
        val hasFirstDiary = allDiaries.isNotEmpty()
        val consecutiveDays = calculateConsecutiveDays(allDates)
        val has7Days = consecutiveDays >= 7
        val has30Days = consecutiveDays >= 30
        
        listOf(
            BadgeItem("첫 일기", "첫 기록", R.drawable.days1_bage, hasFirstDiary),
            BadgeItem("7일 기록", "7일 연속", R.drawable.days7_bage, has7Days),
            BadgeItem("30일 연속", "30일 연속", R.drawable.days30_bage, has30Days)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFF6F1)) // Figma 배경색
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단 타이틀바
            TopBar()
            
            // 스크롤 가능한 콘텐츠
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 30.dp)
            ) {

            // 1. 이번 주 감정 요약
            WeeklyEmotionCard(
                summary = weeklySummary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. 주간 활동 그래프
            WeeklyActivityCard(
                activities = weeklyActivities,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. 감정 분포
            EmotionDistributionCard(
                items = emotionDistribution,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. 달성한 뱃지
            BadgeCard(
                badges = badges,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun TopBar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "감정 리포트",
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF1B2023)
            )
        }
    }
}

@Composable
private fun WeeklyEmotionCard(
    summary: WeeklyEmotionSummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 제목
            Text(
                text = "이번 주 가장 많이 느낀 감정",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4B5563)
            )
            
            // 감정 아이콘과 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 감정 아이콘 (흰색 이모티콘에 감정별 배경색) - HomeScreen 캘린더 방식과 동일
                val emotionColor = getEmotionColor(summary.mainEmotionType)
                
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(emotionColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = summary.mainEmotionIcon),
                        contentDescription = summary.mainEmotionLabel,
                        modifier = Modifier.size(38.dp),
                        tint = Color.White
                    )
                }
                
                // 감정 정보
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = summary.mainEmotion,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF3A3F49)
                    )
                    Text(
                        text = "총 ${summary.totalCount}번 기록하셨어요",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF3A3F49)
                    )
                    Text(
                        text = "평균 긍정도: ${summary.averageScore}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFF98C53)
                    )
                }
            }
            
            // 하단 메시지 영역
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0x33C7CEEA) // rgba(199, 206, 234, 0.2)
            ) {
                Text(
                    text = summary.message,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4B5563),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 0.dp)
                )
            }
        }
    }
}

@Composable
private fun WeeklyActivityCard(
    activities: List<DailyActivity>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "주간 활동",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4B5563)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 차트 영역
            val maxCount = (activities.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)
            val chartHeight = 174.dp
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Y축 레이블
                Column(
                    modifier = Modifier
                        .height(chartHeight)
                        .padding(end = 4.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    (maxCount downTo 0).forEach { value ->
                        Text(
                            text = "$value",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xB2000000) // rgba(0, 0, 0, 0.7)
                        )
                    }
                }
                
                // 바 차트 영역 (점선 포함)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(chartHeight)
                        .padding(start = 1.dp, end = 1.dp)
                ) {
                    // 점선 그리드 (수평선 + 수직선)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // 수평 점선
                        val lineCount = maxCount + 1
                        val lineSpacing = size.height / (lineCount - 1)
                        
                        for (i in 0 until lineCount) {
                            val y = i * lineSpacing
                            drawLine(
                                color = Color(0x2600001A), // rgba(0, 0, 26, 0.15)
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 2f), 0f)
                            )
                        }
                        
                        // 수직 점선 (막대 사이)
                        val barCount = activities.size
                        if (barCount > 1) {
                            val barSpacing = size.width / barCount
                            for (i in 1 until barCount) {
                                val x = i * barSpacing
                                drawLine(
                                    color = Color(0x2600001A), // rgba(0, 0, 26, 0.15)
                                    start = Offset(x, 0f),
                                    end = Offset(x, size.height),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 2f), 0f)
                                )
                            }
                        }
                    }
                    
                    // 바 차트
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        activities.forEach { activity ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            // 바
                            val barHeight = if (maxCount > 0) {
                                (chartHeight * activity.count / maxCount.coerceAtLeast(1))
                            } else {
                                0.dp
                            }
                            
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(barHeight)
                                    .background(
                                        Color(0xFFD6DBED).copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(0.dp)
                                    )
                            ) {
                                if (activity.count > 0) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(barHeight)
                                            .background(
                                                Color(0xFFF98C53),
                                                shape = RoundedCornerShape(topStart = 9.dp, topEnd = 9.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
                                            )
                                            .align(Alignment.BottomCenter)
                                    )
                                }
                            }
                        }
                    }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // X축 레이블
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                activities.forEach { activity ->
                    Text(
                        text = activity.dateLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xB2000000),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmotionDistributionCard(
    items: List<EmotionDistributionItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "감정 분포",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4B5563)
            )
            
            // 상단 전체 분포 바
            val total = items.sumOf { it.count }.coerceAtLeast(1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp)
                    .clip(RoundedCornerShape(5.dp)),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items.forEachIndexed { index, item ->
                    val ratio = item.count.toFloat() / total.toFloat()
                    Box(
                        modifier = Modifier
                            .weight(ratio)
                            .fillMaxHeight()
                            .background(
                                item.color,
                                shape = when {
                                    index == 0 -> RoundedCornerShape(topStart = 5.dp, bottomStart = 5.dp)
                                    index == items.size - 1 -> RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp)
                                    else -> RoundedCornerShape(0.dp)
                                }
                            )
                    )
                }
            }
            
            // 개별 감정 리스트
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 감정 아이콘
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.name,
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                        
                        // 감정 이름과 횟수
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF4B5563)
                            )
                            Text(
                                text = "${item.count}회",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                        
                        // 프로그레스 바
                        LinearProgressIndicator(
                            progress = { item.progress },
                            modifier = Modifier
                                .width(60.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = item.color,
                            trackColor = Color(0xFFE5E7EB)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeCard(
    badges: List<BadgeItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "달성한 뱃지",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4B5563)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                badges.forEach { badge ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .width(80.dp)
                                .padding(vertical = 15.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = if (badge.isAchieved) Color(0xFFF98C53) else Color.White,
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 뱃지 아이콘 (SVG 아이콘 사용)
                                val iconRes = when (badge.title) {
                                    "첫 일기" -> R.drawable.ic_badge // 별 아이콘
                                    "7일 기록" -> R.drawable.ic_fire // 불 아이콘
                                    "30일 연속" -> R.drawable.ic_badge // 다이아몬드 아이콘
                                    else -> R.drawable.ic_badge
                                }
                                
                                Icon(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = badge.title,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (badge.isAchieved) Color.White else Color(0xFF9CA3AF)
                                )
                                
                                Text(
                                    text = badge.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = if (badge.isAchieved) Color.White else Color(0xFF9CA3AF),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ───────────────── 헬퍼 함수 ─────────────────

private fun getEmotionColor(emotion: EmotionType): Color {
    return when (emotion) {
        EmotionType.HAPPY -> Color(0xFFF9E28C)
        EmotionType.SAD -> Color(0xFF6B9BD2)
        EmotionType.ANGRY -> Color(0xFFFF9381)
        EmotionType.UNREST -> Color(0xFFD2C4E8)
        EmotionType.TIRED -> Color(0xFFB2B2B2)
        EmotionType.EXCITEMENT -> Color(0xFFFFC7C3)
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

private fun calculateConsecutiveDays(dates: List<LocalDate>): Int {
    if (dates.isEmpty()) return 0
    
    var maxConsecutive = 1
    var currentConsecutive = 1
    
    for (i in 1 until dates.size) {
        val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(dates[i - 1], dates[i])
        if (daysBetween == 1L) {
            currentConsecutive++
            maxConsecutive = maxOf(maxConsecutive, currentConsecutive)
        } else {
            currentConsecutive = 1
        }
    }
    
    return maxConsecutive
}
