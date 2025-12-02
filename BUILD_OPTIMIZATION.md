# 빌드 속도 개선 가이드

## 현재 적용된 최적화
1. ✅ 병렬 빌드 활성화
2. ✅ 빌드 캐시 활성화
3. ✅ 메모리 4GB로 증가
4. ✅ G1 가비지 컬렉터 사용
5. ✅ Room 증분 컴파일 활성화

## 빌드가 여전히 느린 경우

### 1. Gradle 캐시 정리 후 재빌드
```powershell
.\gradlew.bat clean
.\gradlew.bat --stop
.\gradlew.bat installDebug
```

### 2. Android Studio 캐시 정리
- File → Invalidate Caches / Restart
- "Invalidate and Restart" 선택

### 3. Room 컴파일러 비활성화 (임시 테스트)
`app/build.gradle.kts`에서 다음 줄을 주석 처리:
```kotlin
// ksp("androidx.room:room-compiler:2.6.1")
```

### 4. 증분 빌드 확인
```powershell
.\gradlew.bat assembleDebug --rerun-tasks
```

### 5. 빌드 프로파일 확인
```powershell
.\gradlew.bat installDebug --profile
```
결과는 `build/reports/profile/` 폴더에서 확인

## 예상 빌드 시간
- 첫 빌드: 5-10분 (의존성 다운로드 포함)
- 증분 빌드: 30초-2분
- Clean 빌드: 3-5분

## 문제가 계속되면
1. Android Studio → Help → Show Log in Explorer
2. `idea.log` 파일 확인
3. 빌드 로그에서 멈춘 단계 확인

