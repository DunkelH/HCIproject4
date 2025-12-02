package com.example.minddiary.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val emotion: String,           // 감정 (HAPPY, SAD, ANGRY, UNREST, TIRED, EXCITEMENT)
    val content: String,           // 일기 내용
    val photoUris: String,         // 사진 URI 목록 (콤마로 구분)
    val weather: String,           // 날씨 정보
    val temperature: String,       // 온도
    val location: String,          // 위치 정보
    val createdAt: Long,           // 작성 시간 (timestamp)
    val wordCount: Int             // 단어 수
)

