package com.example.minddiary.data

import kotlinx.coroutines.flow.Flow

class DiaryRepository(private val diaryDao: DiaryDao) {
    
    // 모든 일기 조회
    val allDiaries: Flow<List<DiaryEntry>> = diaryDao.getAllDiaries()
    
    // 일기 저장
    suspend fun insertDiary(diary: DiaryEntry): Long {
        return diaryDao.insertDiary(diary)
    }
    
    // 일기 수정
    suspend fun updateDiary(diary: DiaryEntry) {
        diaryDao.updateDiary(diary)
    }
    
    // 일기 삭제
    suspend fun deleteDiary(diary: DiaryEntry) {
        diaryDao.deleteDiary(diary)
    }
    
    // 특정 일기 조회
    suspend fun getDiaryById(id: Long): DiaryEntry? {
        return diaryDao.getDiaryById(id)
    }
    
    // 특정 날짜 범위의 일기 조회
    fun getDiariesByDateRange(startTime: Long, endTime: Long): Flow<List<DiaryEntry>> {
        return diaryDao.getDiariesByDateRange(startTime, endTime)
    }
    
    // 특정 감정의 일기 조회
    fun getDiariesByEmotion(emotion: String): Flow<List<DiaryEntry>> {
        return diaryDao.getDiariesByEmotion(emotion)
    }
    
    // 일기 개수 조회
    suspend fun getDiaryCount(): Int {
        return diaryDao.getDiaryCount()
    }
}

