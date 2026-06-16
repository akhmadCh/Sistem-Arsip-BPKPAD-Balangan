# Project Context: Sistem Arsip BPKPAD Balangan

## 📌 Overview
The **Sistem Arsip BPKPAD Balangan** is a robust, scalable Android application designed for managing government archive documents for the BPKPAD region. It follows an **Offline-First** philosophy using Room as the local source of truth and Supabase (PostgreSQL) as the remote backend.

## 🛠 Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose (Material Design 3)
- **Architecture:** Clean Architecture (Domain, Data, Presentation) + MVVM
- **Dependency Injection:** Dagger Hilt
- **Local Database:** Room (with Paging 3 support)
- **Backend:** Supabase (Postgrest, Auth, Storage)
- **Intelligence:** ML Kit (Text Recognition) for OCR-to-Autofill
- **Image Storage:** Firebase Cloud Storage / Supabase Storage
- **Theming:** Poppins Typography, Green-centric color palette (`#2E7D32`)

## 🏗 Architecture Principles
1.  **Clean Architecture:** Strict separation between Layers.
    - `domain`: Pure Kotlin, UseCases, Repository Interfaces, Models.
    - `data`: Repository Implementations, DTOs, Room Entities, DAOs.
    - `presentation`: ViewModels (UDF), Compose Screens, Navigation.
2.  **Unidirectional Data Flow (UDF):** State flows down, events flow up.
3.  **ResultState Wrapping:** Loading, Success, and Error states handled via a generic wrapper.

## 🔄 Development Progress (June 2026 Revision)
Based on the latest stakeholder requirements, the following features and changes have been implemented or are in progress:

### 1. Navigation & Flow Improvements
- **Bypassed Add Screen:** Users now navigate directly from the Archive List (via FAB) to the `RapidInputScreen`.
- **Archive Flow:** Grouped navigation routes under an `archive_flow` to share ViewModels where necessary (e.g., `RapidInputViewModel`).

### 2. Archive List & Search
- **Mandatory Year Filter:** Users must select a Document Year before the archive list is displayed.
- **Dynamic Table Layout:** Replaced card-based lists with a table-like structure using `Modifier.weight()` for columns.
- **Paging 3 Integration:** Implemented lazy loading for the document table.
- **Retention Visualization:** Documents older than 10 years are highlighted (Red) to indicate they are due for destruction.

### 3. Data Entry & Validation
- **Document Status:** Introduced `DocumentCopyStatus` (Original vs Copy).
- **Duplicate Logic:** Allows duplicate document numbers ONLY if the status (Original/Copy) is different.
- **Auto-Retention Calculation:** The system automatically calculates document validity (Year + 10) instead of manual entry.
- **Staging Area:** Documents are kept in a local staging list (Room) for bulk verification before being pushed to the main database.

### 4. OCR & Scan Feature
- **Scan-to-Autofill:** Integrated CameraX and ML Kit to extract text from physical documents.
- **Metadata Parsing:** Extracted text is sent to an AI backend to map fields like "Nominal", "Third Party", and "Document Number".

## 🛠 Recent Fixes
- **AppNavHost.kt:** Fixed a compilation error where `ArchiveListScreen` was missing the `onNavigateToScan` lambda. This parameter is now correctly passed to allow navigation to the OCR scanner.

## 📂 Directory Structure
- `app/src/main/java/com/example/arsipbpkpad/`
    - `data/`: Local/Remote sources, DTOs, Mappers.
    - `domain/`: Business logic, UseCases, Repository interfaces.
    - `presentation/`: Compose screens, ViewModels, Navigation (`AppNavHost.kt`).
    - `di/`: Hilt Modules for Network, Database, and Repositories.
    - `ui/theme/`: Material 3 design system configuration.

## 📜 Coding Conventions
- **Naming:** `UseCase` suffix for domain logic.
- **Statelessness:** Hoist state in Compose; keep components stateless for testing and previews.
- **No Hardcoding:** All strings in `strings.xml`.
- **Audit Logs:** Handled by Supabase Database Triggers (not in Android code).
