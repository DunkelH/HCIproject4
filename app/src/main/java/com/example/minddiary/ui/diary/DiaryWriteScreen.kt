package com.example.minddiary.ui.diary

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

// 색상 정의
private val BackgroundColor = Color(0xFFF0F2F7)
private val CardBackgroundColor = Color.White
private val PrimaryBlue = Color(0xFF537FF1)
private val LightBlue = Color(0xFFDCE8FF)
private val TextDark = Color(0xFF3A3F49)
private val TextGrey = Color(0xFF6B7280)
private val BorderColor = Color(0xFFE5E7EB)

// 감정 타입 정의
enum class EmotionType(
    val label: String,
    val defaultIcon: Int,
    val selectedIcon: Int
) {
    HAPPY("행복", R.drawable.button_happy, R.drawable.button_happy_click),
    SAD("슬픔", R.drawable.button_sad, R.drawable.button_sad_click),
    ANGRY("화남", R.drawable.button_angry, R.drawable.button_angry_click),
    UNREST("불안", R.drawable.button_unrest, R.drawable.button_unrest_click),
    TIRED("피곤", R.drawable.button_tired, R.drawable.button_tired_click),
    EXCITEMENT("설렘", R.drawable.button_excitement, R.drawable.button_excitement_click)
}

@Composable
fun DiaryWriteScreen(
    navController: NavController,
    viewModel: DiaryViewModel
) {
    // ViewModel에서 상태 가져오기
    val selectedEmotion = viewModel.selectedEmotion
    val selectedPhotos = viewModel.selectedPhotos
    val scrollState = rememberScrollState()

    // 사진 선택 런처 (최대 4장)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 4)
    ) { uris ->
        if (uris.isNotEmpty()) {
            // ViewModel에 사진 추가
            viewModel.addPhotos(uris)
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
            // 상단 헤더 (뒤로가기 + 진행바)
            TopHeader(
                onBackClick = { navController.popBackStack() },
                progress = 0.2f
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 메인 콘텐츠 카드
            ContentCard(
                selectedEmotion = selectedEmotion,
                onEmotionSelected = { emotion ->
                    // ViewModel을 통해 감정 설정 (토글 방식)
                    viewModel.setEmotion(if (selectedEmotion == emotion) null else emotion)
                },
                selectedPhotos = selectedPhotos,
                onAddPhotoClick = {
                    // 4장 미만일 때만 추가 가능
                    if (selectedPhotos.size < 4) {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                },
                onRemovePhoto = { uri ->
                    // ViewModel을 통해 사진 제거
                    viewModel.removePhoto(uri)
                }
            )

            Spacer(modifier = Modifier.height(100.dp))
        }

        // 완료 버튼
        FinishButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            onClick = {
                // 로딩 화면으로 이동
                navController.navigate("diaryLoading") {
                    popUpTo("diaryWrite") { inclusive = true }
                }
            }
        )
    }
}

@Composable
private fun TopHeader(
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

        // 프로그레스 바 (타이틀바 아래)
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
private fun ContentCard(
    selectedEmotion: EmotionType?,
    onEmotionSelected: (EmotionType) -> Unit,
    selectedPhotos: List<Uri>,
    onAddPhotoClick: () -> Unit,
    onRemovePhoto: (Uri) -> Unit
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
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 제목
            Text(
                text = "당신의 하루를 담아볼까요?",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 부제목
            Text(
                text = "사진과 함께 오늘의 이야기를 적어보세요",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = TextGrey,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 감정 선택 섹션
            EmotionSelectionSection(
                selectedEmotion = selectedEmotion,
                onEmotionSelected = onEmotionSelected
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 사진 추가 섹션
            PhotoAddSection(
                selectedPhotos = selectedPhotos,
                onAddPhotoClick = onAddPhotoClick,
                onRemovePhoto = onRemovePhoto
            )
        }
    }
}

@Composable
private fun EmotionSelectionSection(
    selectedEmotion: EmotionType?,
    onEmotionSelected: (EmotionType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // 섹션 제목
        Text(
            text = "감정 선택",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 감정 버튼 그리드 (3x2)
        val emotions = EmotionType.entries.toTypedArray()

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 첫 번째 행
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                emotions.take(3).forEach { emotion ->
                    EmotionButton(
                        emotion = emotion,
                        isSelected = selectedEmotion == emotion,
                        onClick = { onEmotionSelected(emotion) }
                    )
                }
            }

            // 두 번째 행
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                emotions.drop(3).forEach { emotion ->
                    EmotionButton(
                        emotion = emotion,
                        isSelected = selectedEmotion == emotion,
                        onClick = { onEmotionSelected(emotion) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmotionButton(
    emotion: EmotionType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val iconRes = when {
        isSelected || isPressed -> emotion.selectedIcon
        else -> emotion.defaultIcon
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = emotion.label,
            modifier = Modifier.size(80.dp)
        )
    }
}

@Composable
private fun PhotoAddSection(
    selectedPhotos: List<Uri>,
    onAddPhotoClick: () -> Unit,
    onRemovePhoto: (Uri) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // 섹션 제목
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "사진 추가",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        // 안내 문구
        Text(
            text = "최대 4장까지 선택할 수 있어요 (${selectedPhotos.size}/4)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = TextGrey
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 선택된 사진이 있으면 그리드로 표시
        if (selectedPhotos.isNotEmpty()) {
            PhotoGrid(
                photos = selectedPhotos,
                onRemovePhoto = onRemovePhoto,
                onAddPhotoClick = onAddPhotoClick
            )
        } else {
            // 사진이 없으면 추가 버튼 표시
            PhotoAddButton(onClick = onAddPhotoClick)
        }
    }
}

@Composable
private fun PhotoGrid(
    photos: List<Uri>,
    onRemovePhoto: (Uri) -> Unit,
    onAddPhotoClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 2x2 그리드로 표시
        val rows = (photos + listOf(null)).chunked(2) // null은 추가 버튼 자리

        rows.forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { uri ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    ) {
                        if (uri != null) {
                            // 선택된 사진
                            PhotoItem(
                                uri = uri,
                                onRemove = { onRemovePhoto(uri) }
                            )
                        } else if (photos.size < 4) {
                            // 추가 버튼 (4장 미만일 때만)
                            AddPhotoButton(onClick = onAddPhotoClick)
                        }
                    }
                }
                // 행에 아이템이 1개만 있으면 빈 공간 추가
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PhotoItem(
    uri: Uri,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
    ) {
        // 사진
        AsyncImage(
            model = uri,
            contentDescription = "선택된 사진",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 삭제 버튼
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "삭제",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun AddPhotoButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF5F5F5),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "+",
                    fontSize = 32.sp,
                    color = TextGrey
                )
                Text(
                    text = "추가",
                    fontSize = 12.sp,
                    color = TextGrey
                )
            }
        }
    }
}

@Composable
private fun PhotoAddButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.button_addphoto),
        contentDescription = "사진 추가",
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick)
    )
}

@Composable
private fun FinishButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Surface(
        modifier = modifier
            .width(200.dp)
            .height(44.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(9999.dp),
        color = if (isPressed) PrimaryBlue.copy(alpha = 0.8f) else PrimaryBlue,
        shadowElevation = 4.dp
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
