package com.example.minddiary.ui.diary

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.minddiary.data.DiaryDatabase
import com.example.minddiary.data.DiaryEntry
import com.example.minddiary.data.DiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: DiaryRepository
    val allDiaries: Flow<List<DiaryEntry>>
    
    init {
        val diaryDao = DiaryDatabase.getDatabase(application).diaryDao()
        repository = DiaryRepository(diaryDao)
        allDiaries = repository.allDiaries
    }
    
    // 선택된 감정
    var selectedEmotion by mutableStateOf<EmotionType?>(null)
        private set

    // 선택된 사진들
    var selectedPhotos by mutableStateOf<List<Uri>>(emptyList())
        private set

    // AI가 생성한 일기 내용
    var diaryContent by mutableStateOf("")
        private set
    
    // 저장 성공 여부
    var saveSuccess by mutableStateOf(false)
        private set

    // 감정 설정
    fun setEmotion(emotion: EmotionType?) {
        selectedEmotion = emotion
    }

    // 사진 추가
    fun addPhotos(uris: List<Uri>) {
        val newPhotos = (selectedPhotos + uris).take(4)
        selectedPhotos = newPhotos
    }

    // 사진 제거
    fun removePhoto(uri: Uri) {
        selectedPhotos = selectedPhotos.filter { it != uri }
    }

    // 일기 내용 설정
    fun updateDiaryContent(content: String) {
        diaryContent = content
    }
    
    // 일기 저장
    fun saveDiary(
        weather: String = "흐림",
        temperature: String = "16℃",
        location: String = "서울 광진구",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val emotion = selectedEmotion?.name ?: "UNKNOWN"
            val photoUrisString = selectedPhotos.joinToString(",") { it.toString() }
            val wordCount = diaryContent.split(" ", "\n").filter { it.isNotBlank() }.size
            
            val diaryEntry = DiaryEntry(
                emotion = emotion,
                content = diaryContent,
                photoUris = photoUrisString,
                weather = weather,
                temperature = temperature,
                location = location,
                createdAt = System.currentTimeMillis(),
                wordCount = wordCount
            )
            
            repository.insertDiary(diaryEntry)
            saveSuccess = true
            onSuccess()
            
            // 저장 후 상태 초기화
            reset()
        }
    }
    
    // 저장 성공 상태 초기화
    fun resetSaveSuccess() {
        saveSuccess = false
    }

    // 초기화
    fun reset() {
        selectedEmotion = null
        selectedPhotos = emptyList()
        diaryContent = ""
    }
    
    // 일기 삭제
    fun deleteDiary(diary: DiaryEntry) {
        viewModelScope.launch {
            repository.deleteDiary(diary)
        }
    }
    
    // 일기 개수 조회
    suspend fun getDiaryCount(): Int {
        return repository.getDiaryCount()
    }
    
    // 특정 일기 조회
    suspend fun getDiaryById(id: Long): DiaryEntry? {
        return repository.getDiaryById(id)
    }
    
    // 일기 업데이트
    fun updateDiary(
        diary: DiaryEntry,
        emotion: EmotionType?,
        content: String,
        photos: List<Uri>,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val emotionString = emotion?.name ?: diary.emotion
            val photoUrisString = photos.joinToString(",") { it.toString() }
            val wordCount = content.split(" ", "\n").filter { it.isNotBlank() }.size
            
            val updatedDiary = diary.copy(
                emotion = emotionString,
                content = content,
                photoUris = photoUrisString,
                wordCount = wordCount
            )
            
            repository.updateDiary(updatedDiary)
            onSuccess()
        }
    }
}
