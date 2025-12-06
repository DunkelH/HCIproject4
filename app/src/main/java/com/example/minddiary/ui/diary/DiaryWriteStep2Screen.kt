package com.example.minddiary.ui.diary

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
private val TextDark = Color(0xFF3A3F49)
private val TextGrey = Color(0xFF6B7280)
private val TextLightGrey = Color(0xFF9CA3AF)
private val BorderColor = Color(0xFFE5E7EB)
private val InputBorderColor = Color(0xFFD1D5DB)
private val EmotionTagColor = Color(0xFFD2C4E8)  // 불안 감정 색상
private val ButtonTextGrey = Color(0xFF4B5563)

@Composable
fun DiaryWriteStep2Screen(
    navController: NavController,
    viewModel: DiaryViewModel
) {
    // 일기 개수에 따른 기본 내용 정의
    val defaultContents = remember {
        listOf(
            """오늘은 남양주시 조안면 일대를 천천히 둘러보며 여유로운 하루를 보냈다.

오후 1시쯤, 조안면 송촌리 467 '물의정원에 도착했을 때는 흐린 하늘 아래 강과 초록 들판이 한눈에 펼쳐져 있었다. 잔잔한 물결과 멀리 보이는 산들, 그리고 초록빛 텐트가 어우러져 정말 평온한 풍경이었다. 잠시 산책하면서 강가를 바라보니 마음이 차분해지고 머리가 맑아지는 기분이었다.

그 이후, 조안면 북한강로 근처의 찻집에 들렀다. 따뜻한 차와 함께 호두, 양갱 같은 작은 디저트가 나와서 천천히 맛보며 쉬어갔다. 내부 분위기는 조용하고 아늑해서, 물의정원에서 느꼈던 차분함이 이어지는 느낌이었다. 생각보다 오래 머무르면서 편안하게 시간을 보냈다.

저녁이 되어, 조안면 삼봉리 200-12에 있는 레스토랑 앞에 도착했다. 외관 전체를 덮은 초록 식물들과 따뜻한 조명이 어둠 속에서 더 돋보였다. 입구 주변의 가로등도 은은하게 빛나서 분위기가 정말 좋았다. 하루의 끝을 조용하고 감성적인 공간에서 마무리하니 마음이 편안하게 정리되는 느낌이었다.

자연과 좋은 공간 속에서 보낸 하루라 그런지, 오늘은 유난히 마음이 안정되고 쉬어가는 기분이었다.""",
            "오늘은 친구들과 함께 시간을 보내서 정말 즐거웠다. 오랜만에 만나서 이야기 나누니 마음이 편안해졌다. 이런 소중한 순간들이 인생을 풍요롭게 만드는 것 같다.",
            "새로운 프로젝트를 시작하게 되었다. 처음엔 막막했지만 차근차근 준비해보니 할 수 있을 것 같다. 도전하는 과정에서 배우는 것들이 많을 것 같아 기대된다."
        )
    }
    
    var diaryContent by remember {
        mutableStateOf("")
    }
    var diaryCount by remember {
        mutableStateOf(0)
    }
    val scrollState = rememberScrollState()
    
    // 일기 개수 가져오기
    LaunchedEffect(Unit) {
        diaryCount = viewModel.getDiaryCount()
        // 일기 개수에 따라 기본 내용 설정 (0, 1, 2번째 일기만 하드코딩)
        diaryContent = if (diaryCount < defaultContents.size) {
            defaultContents[diaryCount]
        } else {
            defaultContents[0] // 3개 이상일 경우 첫 번째 내용 사용
        }
    }
    
    // ViewModel에서 선택된 감정과 사진 가져오기
    val selectedEmotion = viewModel.selectedEmotion
    val selectedPhotos = viewModel.selectedPhotos
    
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
            // 상단 헤더
            TopHeaderStep2(
                onBackClick = {
                    // DiaryWriteScreen으로 돌아가기
                    navController.navigate("diaryWrite") {
                        popUpTo("diaryWriteStep2") { inclusive = true }
                    }
                },
                progress = 0.8f
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 메인 콘텐츠 카드
            AIReviewCard(
                diaryContent = diaryContent,
                onContentChange = { diaryContent = it },
                wordCount = wordCount,
                dateTime = currentDateTime,
                selectedEmotion = selectedEmotion,
                selectedPhotos = selectedPhotos
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 하단 버튼들
            BottomButtons(
                onRegenerateClick = {
                    // DiaryWriteScreen으로 돌아가기
                    navController.navigate("diaryWrite") {
                        popUpTo("diaryWriteStep2") { inclusive = true }
                    }
                },
                onCompleteClick = {
                    // 일기 내용을 ViewModel에 저장
                    viewModel.updateDiaryContent(diaryContent)
                    // 일기 작성 완료 화면으로 이동
                    navController.navigate("diaryComplete") {
                        popUpTo("diaryWriteStep2") { inclusive = true }
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TopHeaderStep2(
    onBackClick: () -> Unit,
    progress: Float
) {
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
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 뒤로가기 버튼
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
            }
        }

        // 프로그레스 바 (80%)
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
private fun AIReviewCard(
    diaryContent: String,
    onContentChange: (String) -> Unit,
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 제목
            Text(
                text = "AI 초안 검토",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 부제목
            Text(
                text = "필요하면 수정하세요",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = TextDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

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
            EmotionTag(selectedEmotion = selectedEmotion)

            Spacer(modifier = Modifier.height(16.dp))

            // 날씨/위치 정보
            WeatherLocationInfo()

            Spacer(modifier = Modifier.height(16.dp))

            // 오늘의 이야기 섹션
            DiaryContentSection(
                diaryContent = diaryContent,
                onContentChange = onContentChange,
                wordCount = wordCount,
                dateTime = dateTime
            )
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
private fun EmotionTag(selectedEmotion: EmotionType?) {
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
private fun WeatherLocationInfo() {
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
                // 날씨 아이콘
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
                // 위치 아이콘
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
private fun DiaryContentSection(
    diaryContent: String,
    onContentChange: (String) -> Unit,
    wordCount: Int,
    dateTime: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 섹션 제목
        Text(
            text = "오늘의 이야기",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        Spacer(modifier = Modifier.height(15.dp))

        // 텍스트 입력 필드
        OutlinedTextField(
            value = diaryContent,
            onValueChange = onContentChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(127.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = InputBorderColor,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                color = Color(0xFF374151)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                color = Color(0xFF757575)
            )

            // 단어 수
            Text(
                text = "${wordCount}개 단어",
                fontSize = 14.sp,
                color = TextGrey,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun BottomButtons(
    onRegenerateClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 다시 생성 버튼
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            shape = RoundedCornerShape(9999.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor),
            shadowElevation = 4.dp,
            onClick = onRegenerateClick
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "다시 생성",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = ButtonTextGrey,
                    textAlign = TextAlign.Center
                )
            }
        }

        // 완료 버튼
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            shape = RoundedCornerShape(9999.dp),
            color = PrimaryBlue,
            shadowElevation = 4.dp,
            onClick = onCompleteClick
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "완료",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
