package com.example.minddiary.ui.diary

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.minddiary.R
import com.example.minddiary.data.DiaryEntry as DataDiaryEntry
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DiaryScreen(
    onSettingsClick: () -> Unit,
    onDiaryClick: (Long) -> Unit = {},
    viewModel: DiaryViewModel = viewModel()
) {
    val backgroundColor = Color(0xFFFFF6F1)
    val allDiaries by viewModel.allDiaries.collectAsState(initial = emptyList())
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedEmotionFilter by remember { mutableStateOf<String?>(null) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    
    // 필터링된 일기 목록
    val filteredDiaries = remember(allDiaries, searchQuery, selectedEmotionFilter, selectedMonth) {
        allDiaries.filter { diary ->
            // 검색어 필터
            val matchesSearch = searchQuery.isBlank() || 
                diary.content.contains(searchQuery, ignoreCase = true)
            
            // 감정 필터
            val matchesEmotion = selectedEmotionFilter == null || 
                diary.emotion.equals(selectedEmotionFilter, ignoreCase = true)
            
            // 월 필터
            val diaryDate = Instant.ofEpochMilli(diary.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val matchesMonth = YearMonth.from(diaryDate) == selectedMonth
            
            matchesSearch && matchesEmotion && matchesMonth
        }
    }
    
    // 통계 계산
    val stats = remember(allDiaries) {
        val allDates = allDiaries.map { diary ->
            Instant.ofEpochMilli(diary.createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.distinct().sorted()
        
        val consecutiveDays = calculateConsecutiveDays(allDates)
        val totalDiaries = allDiaries.size
        val badges = calculateBadges(allDiaries, allDates)
        
        Stats(consecutiveDays, badges, totalDiaries)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(84.dp)) // 타이틀바 공간 (Figma: y: 84)
            }
            
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.5.dp)
                ) {
                    // 검색 및 필터 섹션
                    SearchAndFilterSection(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        selectedEmotionFilter = selectedEmotionFilter,
                        onEmotionFilterChange = { selectedEmotionFilter = it }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 통계 카드
                    StatsRow(stats = stats)
                    
                    Spacer(modifier = Modifier.height(56.dp))
                    
                    // 월별 선택
                    MonthSelector(
                        selectedMonth = selectedMonth,
                        onMonthChange = { selectedMonth = it },
                        diaryCount = filteredDiaries.size
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 일기 목록
                    filteredDiaries.forEach { diary ->
                        DiaryCard(
                            diary = diary,
                            onClick = { onDiaryClick(diary.id) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
        
        // 상단 타이틀바
        TopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            onSettingsClick = onSettingsClick
        )
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "나의 일기장",
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF1B2023)
            )
            
            IconButton(onClick = onSettingsClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings",
                    tint = Color(0xFF1B2023),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchAndFilterSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedEmotionFilter: String?,
    onEmotionFilterChange: (String?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 검색창
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "일기 검색...",
                        fontSize = 16.sp,
                        color = Color(0xFF49454F)
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search",
                        tint = Color(0xFF49454F),
                        modifier = Modifier.size(20.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0x33FFDAC1), // rgba(255, 218, 193, 0.2)
                    unfocusedContainerColor = Color(0x33FFDAC1),
                    focusedBorderColor = Color(0x80FFDAC1), // rgba(255, 218, 193, 0.5)
                    unfocusedBorderColor = Color(0x80FFDAC1),
                    focusedTextColor = Color(0xFF000000),
                    unfocusedTextColor = Color(0xFF000000),
                    cursorColor = Color(0xFF000000)
                ),
                shape = RoundedCornerShape(28.dp),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFF000000)
                )
            )
            
            // 감정 필터 칩
            val filters = listOf(
                FilterChipData("전체", null, null),
                FilterChipData("행복", "HAPPY", R.drawable.ic_happy),
                FilterChipData("슬픔", "SAD", R.drawable.ic_sad),
                FilterChipData("분노", "ANGRY", R.drawable.ic_mad),
                FilterChipData("불안", "UNREST", R.drawable.ic_anxious),
                FilterChipData("피곤", "TIRED", R.drawable.ic_tired),
                FilterChipData("설렘", "EXCITEMENT", R.drawable.ic_excited)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    EmotionFilterChip(
                        label = filter.label,
                        emotionValue = filter.emotionValue,
                        iconResId = filter.iconResId,
                        isSelected = selectedEmotionFilter == filter.emotionValue,
                        onClick = { 
                            onEmotionFilterChange(if (filter.emotionValue == null) null else filter.emotionValue)
                        }
                    )
                }
            }
        }
    }
}

data class FilterChipData(
    val label: String,
    val emotionValue: String?,
    val iconResId: Int?
)

@Composable
private fun EmotionFilterChip(
    label: String,
    emotionValue: String?,
    iconResId: Int?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) Color(0xFFF98C53) else Color.White
    val textColor = if (isSelected) Color.White else Color(0xFF9CA3AF)
    val borderColor = if (isSelected) Color.Transparent else Color(0xFFE5E7EB)
    
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .clickable { onClick() }
            .background(bgColor, shape = RoundedCornerShape(100.dp))
            .then(
                if (!isSelected) {
                    Modifier.border(1.dp, borderColor, shape = RoundedCornerShape(100.dp))
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconResId != null) {
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = label,
                    modifier = Modifier.size(20.dp),
                    tint = textColor
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

data class Stats(
    val consecutiveDays: Int,
    val badges: Int,
    val totalDiaries: Int
)

@Composable
private fun StatsRow(stats: Stats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 연속 기록 (그라데이션 배경)
        StatCard(
            iconRes = R.drawable.ic_fire,
            title = "연속 기록",
            value = "${stats.consecutiveDays}",
            isGradient = true
        )
        
        // 획득 뱃지
        StatCard(
            iconRes = R.drawable.ic_badge,
            title = "획득 뱃지",
            value = "${stats.badges}",
            isGradient = false
        )
        
        // 총 기록
        StatCard(
            iconRes = R.drawable.ic_totalrecord,
            title = "총 기록",
            value = "${stats.totalDiaries}",
            isGradient = false
        )
    }
}

@Composable
private fun StatCard(
    iconRes: Int,
    title: String,
    value: String,
    isGradient: Boolean = false
) {
    Card(
        modifier = Modifier
            .width(95.dp)
            .height(62.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isGradient) Color.Transparent else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isGradient) {
                        Modifier.background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xCCF98C53), // rgba(249, 140, 83, 0.8)
                                    Color(0xFFF98C53)
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, top = 9.dp, bottom = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // 아이콘
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = if (isGradient) Color.White else Color(0xFF1B2023)
                )
                
                Spacer(modifier = Modifier.width(7.dp))
                
                // 텍스트 (제목 + 숫자)
                Column(
                    modifier = Modifier.wrapContentWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = if (isGradient) Color.White else Color(0xFF9CA3AF)
                    )
                    Text(
                        text = value,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isGradient) Color.White else Color(0xFF000000)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthSelector(
    selectedMonth: YearMonth,
    onMonthChange: (YearMonth) -> Unit,
    diaryCount: Int
) {
    var showMonthDialog by remember { mutableStateOf(false) }
    var selectedYear by remember { mutableStateOf(selectedMonth.year) }
    var selectedMonthValue by remember { mutableStateOf(selectedMonth.monthValue) }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_cal),
            contentDescription = "Calendar",
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF1B2023)
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${selectedMonth.year}년",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E1E1E)
            )
            
            // 월 선택 버튼
            Row(
                modifier = Modifier.clickable { showMonthDialog = true },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${selectedMonth.monthValue}월",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E1E1E)
                )
                Image(
                    painter = painterResource(id = R.drawable.button_month),
                    contentDescription = "Select month",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Text(
            text = "($diaryCount)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF757575)
        )
    }
    
    // 월 선택 다이얼로그
    if (showMonthDialog) {
        MonthSelectionDialog(
            currentYear = selectedMonth.year,
            currentMonth = selectedMonth.monthValue,
            onDismiss = { showMonthDialog = false },
            onConfirm = { year, month ->
                selectedYear = year
                selectedMonthValue = month
                onMonthChange(YearMonth.of(year, month))
                showMonthDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthSelectionDialog(
    currentYear: Int,
    currentMonth: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var selectedYear by remember { mutableStateOf(currentYear) }
    var selectedMonth by remember { mutableStateOf(currentMonth) }
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    // 연도 리스트 생성 (현재 연도 기준 과거 10년까지)
    val currentYearValue = YearMonth.now().year
    val years = (currentYearValue - 10..currentYearValue).toList()
    val selectedYearIndex = years.indexOf(selectedYear).coerceAtLeast(0)
    
    // 월 리스트 생성
    val months = (1..12).toList()
    val selectedMonthIndex = months.indexOf(selectedMonth).coerceAtLeast(0)
    
    val yearListState = rememberLazyListState(initialFirstVisibleItemIndex = selectedYearIndex)
    val monthListState = rememberLazyListState(initialFirstVisibleItemIndex = selectedMonthIndex)
    
    // 선택된 연도/월로 스크롤
    LaunchedEffect(selectedYearIndex) {
        if (selectedYearIndex >= 0) {
            yearListState.animateScrollToItem(selectedYearIndex)
        }
    }
    
    LaunchedEffect(selectedMonthIndex) {
        if (selectedMonthIndex >= 0) {
            monthListState.animateScrollToItem(selectedMonthIndex)
        }
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 제목과 닫기 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "날짜 선택",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1B2023)
                )
                Text(
                    text = "✕",
                    fontSize = 24.sp,
                    color = Color(0xFF1B2023),
                    modifier = Modifier.clickable { onDismiss() }
                )
            }
            
            // 연도/월 선택 영역
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // 연도 선택 (좌측)
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    state = yearListState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(years.size) { index ->
                        val year = years[index]
                        val isSelected = year == selectedYear
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(
                                    if (isSelected) Color(0xFFF98C53).copy(alpha = 0.1f) else Color.Transparent
                                )
                                .clickable { selectedYear = year },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${year}년",
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                color = if (isSelected) Color(0xFFF98C53) else Color(0xFF1B2023)
                            )
                        }
                    }
                }
                
                // 구분선
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(Color(0xFFE5E7EB))
                )
                
                // 월 선택 (우측)
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    state = monthListState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(months.size) { index ->
                        val month = months[index]
                        val isSelected = month == selectedMonth
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(
                                    if (isSelected) Color(0xFFF98C53).copy(alpha = 0.1f) else Color.Transparent
                                )
                                .clickable { selectedMonth = month },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${month}월",
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                color = if (isSelected) Color(0xFFF98C53) else Color(0xFF1B2023)
                            )
                        }
                    }
                }
            }
            
            // 확인 버튼
            Button(
                onClick = { onConfirm(selectedYear, selectedMonth) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF98C53)
                )
            ) {
                Text(
                    text = "확인",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun DiaryCard(
    diary: DataDiaryEntry,
    onClick: () -> Unit
) {
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    val diaryDate = Instant.ofEpochMilli(diary.createdAt)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val dateString = diaryDate.format(dateFormat)
    
    val emotionLabel = getEmotionLabel(diary.emotion)
    val emotionColor = getEmotionColor(diary.emotion)
    val emotionIcon = getEmotionIcon(diary.emotion)
    
    val photoUris = diary.photoUris.split(",").filter { it.isNotBlank() }
    val firstPhotoUri = photoUris.firstOrNull()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 이미지
            if (firstPhotoUri != null) {
                AsyncImage(
                    model = firstPhotoUri,
                    contentDescription = "Diary image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 24.dp,
                                topEnd = 24.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        ),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Color(0xFFF0F0F0))
                )
            }
            
            // 내용
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 감정 칩
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = emotionColor
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = emotionIcon),
                                    contentDescription = emotionLabel,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.White
                                )
                                Text(
                                    text = emotionLabel,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                        
                        // 날짜
                        Text(
                            text = dateString,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF6B7280)
                        )
                    }
                    
                    // 수정 아이콘
                    IconButton(onClick = { /* TODO: 수정 기능 */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Edit",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // 일기 내용
                Text(
                    text = diary.content,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF374151),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun getEmotionLabel(emotion: String): String {
    return when (emotion.uppercase()) {
        "HAPPY" -> "행복"
        "SAD" -> "슬픔"
        "ANGRY" -> "분노"
        "UNREST" -> "불안"
        "TIRED" -> "피곤"
        "EXCITEMENT" -> "설렘"
        else -> "행복"
    }
}

private fun getEmotionColor(emotion: String): Color {
    return when (emotion.uppercase()) {
        "HAPPY" -> Color(0xFFF9E28C)
        "SAD" -> Color(0xFF6B9BD2)
        "ANGRY" -> Color(0xFFFF9381)
        "UNREST" -> Color(0xFFD2C4E8)
        "TIRED" -> Color(0xFFB2B2B2)
        "EXCITEMENT" -> Color(0xFFFFC7C3)
        else -> Color(0xFFF9E28C)
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

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
private fun calculateBadges(diaries: List<DataDiaryEntry>, dates: List<LocalDate>): Int {
    var badgeCount = 0
    
    // 첫 일기
    if (diaries.isNotEmpty()) badgeCount++
    
    // 연속 기록
    val consecutiveDays = calculateConsecutiveDays(dates)
    if (consecutiveDays >= 7) badgeCount++
    if (consecutiveDays >= 30) badgeCount++
    
    return badgeCount
}
