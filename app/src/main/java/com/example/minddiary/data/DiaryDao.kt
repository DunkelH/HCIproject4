package com.example.minddiary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    // 모든 일기 조회 (최신순)
    @Query("SELECT * FROM diary_entries ORDER BY createdAt DESC")
    fun getAllDiaries(): Flow<List<DiaryEntry>>
    
    // 특정 일기 조회
    @Query("SELECT * FROM diary_entries WHERE id = :id")
    suspend fun getDiaryById(id: Long): DiaryEntry?
    
    // 일기 저장
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiary(diary: DiaryEntry): Long
    
    // 일기 수정
    @Update
    suspend fun updateDiary(diary: DiaryEntry)
    
    // 일기 삭제
    @Delete
    suspend fun deleteDiary(diary: DiaryEntry)
    
    // 특정 날짜 범위의 일기 조회
    @Query("SELECT * FROM diary_entries WHERE createdAt BETWEEN :startTime AND :endTime ORDER BY createdAt DESC")
    fun getDiariesByDateRange(startTime: Long, endTime: Long): Flow<List<DiaryEntry>>
    
    // 특정 감정의 일기 조회
    @Query("SELECT * FROM diary_entries WHERE emotion = :emotion ORDER BY createdAt DESC")
    fun getDiariesByEmotion(emotion: String): Flow<List<DiaryEntry>>
    
    // 일기 개수 조회
    @Query("SELECT COUNT(*) FROM diary_entries")
    suspend fun getDiaryCount(): Int
}

