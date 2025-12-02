package com.example.minddiary.ui.diary

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
private val InputBorderColor = Color(0xFFD1D5DB)

// 감정별 색상
private val EmotionHappy = Color(0xFFF9E28C)
private val EmotionSad = Color(0xFF6B9BD2)
private val EmotionAngry = Color(0xFFFFC7C3)
private val EmotionUnrest = Color(0xFFD2C4E8)
private val EmotionTired = Color(0xFFB2B2B2)
private val EmotionExcitement = Color(0xFFFFC0CB)

@Composable
fun DiaryEditScreen(
    navController: NavController,
    diaryId: Long,
    viewModel: DiaryViewModel
) {
    val context = LocalContext.current
    var diary by remember { mutableStateOf<DiaryEntry?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    // 편집 가능한 상태
    var editedContent by remember { mutableStateOf("") }
    var editedPhotos by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var editedEmotion by remember { mutableStateOf<EmotionType?>(null) }
    var showEmotionDialog by remember { mutableStateOf(false) }
    
    // diaryId로 일기 조회
    LaunchedEffect(diaryId) {
        val loadedDiary = viewModel.getDiaryById(diaryId)
        if (loadedDiary != null) {
            diary = loadedDiary
            editedContent = loadedDiary.content
            editedPhotos = loadedDiary.photoUris.split(",")
                .filter { it.isNotBlank() }
                .mapNotNull { uriString ->
                    try {
                        Uri.parse(uriString)
                    } catch (e: Exception) {
                        null
                    }
                }
            editedEmotion = try {
                EmotionType.valueOf(loadedDiary.emotion)
            } catch (e: Exception) {
                null
            }
        }
        isLoading = false
        
        // 일기를 찾을 수 없으면 뒤로가기
        if (loadedDiary == null) {
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
    
    // 단어 수 계산
    val wordCount = editedContent.split(" ", "\n").filter { it.isNotBlank() }.size
    
    // 사진 선택 런처
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 4)
    ) { uris ->
        if (uris.isNotEmpty()) {
            editedPhotos = uris.take(4)
        }
    }
    
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
                onBackClick = { navController.popBackStack() },
                onSaveClick = {
                    // 일기 업데이트
                    viewModel.updateDiary(
                        diary = currentDiary,
                        emotion = editedEmotion,
                        content = editedContent,
                        photos = editedPhotos,
                        onSuccess = {
                            Toast.makeText(context, "일기가 수정되었습니다", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 일기 편집 카드
            DiaryEditCard(
                diary = currentDiary,
                editedContent = editedContent,
                onContentChange = { editedContent = it },
                editedPhotos = editedPhotos,
                onPhotoAddClick = {
                    if (editedPhotos.size < 4) {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                },
                onPhotoRemove = { uri ->
                    editedPhotos = editedPhotos.filter { it != uri }
                },
                editedEmotion = editedEmotion,
                onEmotionClick = { showEmotionDialog = true },
                onEmotionChange = { editedEmotion = it },
                wordCount = wordCount
            )
            
            // 감정 선택 다이얼로그
            if (showEmotionDialog) {
                EmotionSelectionDialog(
                    currentEmotion = editedEmotion,
                    onEmotionSelected = { emotion ->
                        editedEmotion = emotion
                        showEmotionDialog = false
                    },
                    onDismiss = { showEmotionDialog = false }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TopBar(
    date: String,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
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
            
            // 저장 버튼 (체크 아이콘 대신 텍스트 사용)
            Text(
                text = "저장",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark,
                modifier = Modifier
                    .clickable(onClick = onSaveClick)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun DiaryEditCard(
    diary: DiaryEntry,
    editedContent: String,
    onContentChange: (String) -> Unit,
    editedPhotos: List<Uri>,
    onPhotoAddClick: () -> Unit,
    onPhotoRemove: (Uri) -> Unit,
    editedEmotion: EmotionType?,
    onEmotionClick: () -> Unit,
    onEmotionChange: (EmotionType?) -> Unit,
    wordCount: Int
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
            // 사진 섹션
            PhotoSection(
                photos = editedPhotos,
                onAddPhotoClick = onPhotoAddClick,
                onRemovePhoto = onPhotoRemove
            )
            
            // 감정 태그
            EmotionTagSection(
                selectedEmotion = editedEmotion,
                onEmotionClick = onEmotionClick
            )
            
            // 일기 내용
            ContentSection(
                content = editedContent,
                onContentChange = onContentChange
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
                        text = "${wordCount}개 단어",
                        fontSize = 14.sp,
                        color = TextGrey,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            
            // 날씨/위치 정보
            WeatherLocationInfo(
                weather = diary.weather,
                temperature = diary.temperature,
                location = diary.location
            )
        }
    }
}

@Composable
private fun PhotoSection(
    photos: List<Uri>,
    onAddPhotoClick: () -> Unit,
    onRemovePhoto: (Uri) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 사진 업로드 버튼 또는 기존 사진
        if (photos.isEmpty()) {
            // 사진 업로드 버튼
            Surface(
                modifier = Modifier
                    .size(81.dp)
                    .clickable(onClick = onAddPhotoClick),
                shape = RoundedCornerShape(8.dp),
                color = BackgroundColor,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.upload_photo),
                        contentDescription = "사진 업로드",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "사진 업로드",
                        fontSize = 12.sp,
                        color = Color(0xFF4B5563),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // 기존 사진 표시
            photos.forEach { uri ->
                Box(
                    modifier = Modifier.size(81.dp)
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "일기 사진",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    // 삭제 버튼
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .clickable { onRemovePhoto(uri) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "×",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
            // 추가 버튼 (4장 미만일 때)
            if (photos.size < 4) {
                Surface(
                    modifier = Modifier
                        .size(81.dp)
                        .clickable(onClick = onAddPhotoClick),
                    shape = RoundedCornerShape(8.dp),
                    color = BackgroundColor,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.upload_photo),
                            contentDescription = "사진 업로드",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "사진 업로드",
                            fontSize = 12.sp,
                            color = Color(0xFF4B5563),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmotionTagSection(
    selectedEmotion: EmotionType?,
    onEmotionClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        selectedEmotion?.let { emotion ->
            val emotionColor = getEmotionColor(emotion)
            val emotionIcon = getEmotionIcon(emotion)
            
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = emotionColor,
                modifier = Modifier.clickable { onEmotionClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = emotionIcon),
                        contentDescription = emotion.label,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                    Text(
                        text = emotion.label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmotionSelectionDialog(
    currentEmotion: EmotionType?,
    onEmotionSelected: (EmotionType?) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = CardBackgroundColor
    ) {
        val dialogScrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(dialogScrollState)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 제목 섹션
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "오늘 당신의 마음은 어떤가요?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4B5563),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "가장 가까운 감정을 선택해주세요",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4B5563),
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 감정 버튼들 (2열 그리드)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val emotions = EmotionType.entries.toList()
                // 2개씩 묶어서 행으로 표시
                emotions.chunked(2).forEach { rowEmotions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowEmotions.forEach { emotion ->
                            EmotionBottomSheetButton(
                                emotion = emotion,
                                isSelected = currentEmotion == emotion,
                                onClick = { onEmotionSelected(emotion) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // 홀수 개일 경우 빈 공간 추가
                        if (rowEmotions.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 닫기 버튼
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clickable(onClick = onDismiss),
                shape = RoundedCornerShape(9999.dp),
                color = Color(0xFF537FF1),
                border = BorderStroke(1.dp, BorderColor),
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "닫기",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun EmotionBottomSheetButton(
    emotion: EmotionType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageRes = if (isSelected) emotion.selectedIcon else emotion.defaultIcon
    
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = emotion.label,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        contentScale = ContentScale.FillWidth
    )
}

@Composable
private fun ContentSection(
    content: String,
    onContentChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(127.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BorderColor,
                unfocusedBorderColor = BorderColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                color = Color(0xFF374151)
            )
        )
    }
}

@Composable
private fun WeatherLocationInfo(
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

private fun getEmotionColor(emotion: EmotionType): Color {
    return when (emotion) {
        EmotionType.HAPPY -> EmotionHappy
        EmotionType.SAD -> EmotionSad
        EmotionType.ANGRY -> EmotionAngry
        EmotionType.UNREST -> EmotionUnrest
        EmotionType.TIRED -> EmotionTired
        EmotionType.EXCITEMENT -> EmotionExcitement
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

