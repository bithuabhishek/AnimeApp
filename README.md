
# AnimeApp – Offline‑First Anime Browser (MVVM + Flow + Room)

## Overview
AnimeApp is a production‑grade Android application that displays a paginated list of top anime with full offline support.  
It follows **clean MVVM architecture** and demonstrates modern Android development practices used in real‑world apps.

Key highlights:
- Offline‑first design
- Pagination with caching
- Search without breaking pagination
- Anime detail caching
- Trailer playback via WebView
- Centralized logging & error handling
- Lifecycle‑safe UI events

---

## High‑Level Architecture (HLD)

```
Activity / UI
      ↓
   ViewModel
      ↓
  Repository
   ↓        ↓
Remote API   Local DB (Room)
```

### Core Principles
- UI never talks directly to API or DB
- ViewModels contain no Android context
- Repository decides data source
- Single source of truth
- Clear separation of concerns

---

## Application Class

### `MyApplication`
The `Application` class is the **global initializer** for the app.

Responsibilities:
- Create Room database instance
- Initialize in‑memory caches
- Initialize centralized logger
- Provide shared dependencies across app

Why this is important:
- Avoids multiple DB instances
- Avoids repeated cache creation
- Keeps ViewModels clean and testable

```kotlin
class MyApplication : Application() {
    val database: AnimeDatabase
    val animeDetailsCache: AnimeDetailsCache
    val logger: AppLogger
}
```

---

## Caching Strategy

### 1. List Screen Cache (Room DB)
- API results are always saved into Room
- When offline, paginated data is read from DB
- Same pagination logic works for both API & DB

### 2. Detail Screen Cache
- Separate `anime_details` table
- In‑memory cache holds anime IDs already fetched
- Prevents duplicate API calls for details

Flow:
1. Check DB
2. If present → return
3. If absent → fetch from API
4. Save to DB
5. Update cache

---

## Repository Layer

### AnimeRepository (List)
Handles:
- Paginated API calls
- DB pagination queries
- Online / offline switching

Returns data wrapped in:
```kotlin
Result<
  Pair<List<AnimeEntity>, Boolean> // API
  OR
  List<AnimeEntity>                // DB
>
```

### AnimeDetailsRepository
Handles:
- Offline‑first detail fetching
- Cache lookup
- Internet availability checks
- YouTube trailer ID extraction

---

## Internet Check Logic

Internet availability is checked:
- Before API calls
- Inside repository (not UI)
- For trailer playback

Benefits:
- Prevents crashes
- Allows graceful fallback
- Keeps UI logic simple

```kotlin
NetworkUtils.isInternetAvailable(context)
```

---

## Pagination Logic

Pagination is handled manually (without Paging 3) to demonstrate understanding.

Rules:
- Prevent multiple simultaneous loads
- Stop when no more pages
- Same logic for API and DB
- Pagination disabled during search

Variables:
```kotlin
currentPage
hasNextPage
isLoading
pageBuffer
```

---

## Search in RecyclerView

Search is implemented using `Filterable` in adapter.

Behavior:
- Filters only loaded items
- Pagination pauses while searching
- Clears search restores full list

Why this approach:
- Simple
- Efficient
- No unnecessary API calls

---

## ViewModel Layer

### AnimeViewModel
Manages:
- Pagination state
- List UI state
- UI events (loading, toast)

Uses:
- `StateFlow<List<AnimeEntity>>` for UI state
- `Channel<UiEvent>` for one‑time events

### AnimeDetailViewModel
Manages:
- Anime detail state
- Loading indicator
- Error events

Uses:
- `StateFlow<AnimeDetailsEntity?>`
- `Channel<UiEvent>`

---

## UI Events System

### UiEvent
```kotlin
sealed class UiEvent {
    data class Loading(val isLoading: Boolean)
    data class ShowToast(val message: String)
}
```

### Why Channel?
- No duplicate toasts
- No replay on rotation
- Lifecycle‑safe
- Exactly‑once delivery

---

## Trailer Playback (WebView)

Trailer handling:
- Extract YouTube video ID from embed URL
- Use `youtube‑nocookie` embed
- Disable WebView scrolling
- Hide trailer when offline

Why WebView:
- Lightweight
- No YouTube SDK dependency
- Works with embed URLs

---

## Error Handling Strategy

- All repository calls return `Result`
- No crashes exposed to UI
- User‑friendly messages via Toast
- Technical details logged only

---

## Logging

### AppLogger
- Initialized in Application class
- Central logging point
- Logs errors from ViewModels & repositories
- Easy to extend (Crashlytics, Sentry)

---

## Edge Cases Covered

- No internet
- Empty API response
- Duplicate pagination calls
- Configuration changes
- App restart with cached data
- Trailer unavailable
- Search + pagination conflict

---

## Tech Stack

- Kotlin
- Coroutines
- Flow (StateFlow + Channel)
- Retrofit
- Room
- Material 3
- Glide

---

## Why This Project Is Interview‑Ready

- Real production patterns
- Correct async handling
- Offline‑first architecture
- Clean separation of concerns
- Lifecycle‑safe UI events
- Scalable design

---

## Future Improvements

- Paging 3
- Hilt / Dependency Injection
- Jetpack Compose UI
- Unit & UI tests

---

## Author
**Abhishek Bithu**  
Android Developer
