# Manual Archive Input - Feature Documentation

This document describes the **Manual Input (Rapid Input)** functionality built for the BPKPAD Balangan Archive System. This feature allows users to efficiently input large volumes of physical documents into the digital archive system using a "Staging Area" workflow.

## 🚀 Workflow Overview

1.  **Home Dashboard**: A unified "Staging Status" card summarizes all active input sessions.
2.  **Staging Box List**: Manage multiple boxes currently being processed. Users can create a new box or continue an existing session.
3.  **Rapid Input Screen**: The core form where documents are added to the current box.
4.  **Bulk Upload**: Once a box is complete, the user pushes all staged documents to the database in a single action.

---

## ✨ Core Features

### 1. Unified Staging Card
Instead of multiple entries, the Home screen displays a single summary card showing:
- Total number of documents across all boxes in staging.
- Total number of unique boxes being processed.
- Quick navigation to the full staging list.

### 2. Auto-Bundle Logic (SP2D Integration)
To save time, when a user adds an **SP2D** document:
- The user can enable the **"Buatkan SPM dan SPJ (Auto-Bundle)"** checkbox.
- The system will automatically generate a corresponding **SPM** and **SPJ** record in the staging area with matching metadata.
- Users can provide a specific SPM number and SPJ description during the process.

### 3. Status Fisik & Copy Management
- **Asli (Original)**: Default state for physical documents.
- **Salinan (Copy)**: When selected, the UI dynamically reveals a "Jumlah Salinan" (Copy Count) field.
- **Enums**: All statuses are handled via type-safe `DocType` and `DocCopyType` enums.

### 4. Robust Input Validation
- **Numeric Keyboards**: Enforced `KeyboardType.Number` for:
    - Fiscal Year (Tahun Anggaran)
    - Nominal/Currency amounts (with thousands formatting).
    - Copy Count (Jumlah Salinan).
- **Required Fields**: Validation ensures document numbers and descriptions are present before allowing an item to be added to staging.

### 5. OCR Integration
- Users can jump to the **Scan Screen** directly from the input form.
- OCR results are reliably passed back and automatically populate the Document Number, Nominal, Perihal, and Type fields.

---

## 🎨 UI/UX Design

### Branding & Identity
- **Balangan Logo**: The official logo is integrated into the `TopAppBar` of every screen.
- **Branding Typography**: Header titles use `titleMedium` with `ExtraBold` weight for a professional look.

### Dynamic Theme Support
- **Dark/Light Mode**: Full support for system-wide theme changes.
- **Staging Aesthetics**:
    - Backgrounds are neutral (`surface` color).
    - Staging cards use a dynamic green shade (`secondaryContainer`) to maintain branding while ensuring readability.

---

## 🛠 Implementation Details

### Navigation
- Uses a nested `navigation` graph (`archive_flow`) to share the `RapidInputViewModel` between the List, Staging, and Form screens.
- **Result Passing**: Uses `savedStateHandle` for high-reliability data transfer between the Scan screen and the form.

### State Management
- **RapidInputViewModel**: Manages the local staging database (via Repository) and the temporary form state.
- **Paging**: The archive list uses the Paging 3 library for high-performance data loading.
