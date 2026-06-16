package com.example.arsipbpkpad.presentation.scan

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arsipbpkpad.core.common.ResultState
import com.example.arsipbpkpad.domain.usecase.ExtractTextWithMlKitUseCase
import com.example.arsipbpkpad.domain.usecase.ParseMetadataWithAiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val extractTextUseCase: ExtractTextWithMlKitUseCase,
    private val parseMetadataUseCase: ParseMetadataWithAiUseCase
) : ViewModel() {

    init {
        Log.e("ScanVM", "ScanViewModel initialized")
    }

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun onImageCaptured(uri: Uri) {
        Log.e("ScanVM", "PROCESS: onImageCaptured triggered for $uri")
        _uiState.update { it.copy(isLoading = true, capturedImageUri = uri.toString(), errorMessage = null) }
        
        viewModelScope.launch {
            // Step 1: Extract Text
            Log.e("ScanVM", "PROCESS: Starting OCR extraction...")
            val ocrResult = extractTextUseCase(uri)
            Log.e("ScanVM", "PROCESS: OCR result: $ocrResult")
            
            if (ocrResult is ResultState.Success) {
                // Step 2: Parse Metadata
                Log.e("ScanVM", "PROCESS: Starting AI parsing. Raw text length: ${ocrResult.data.length}")
                val aiResult = parseMetadataUseCase(ocrResult.data)
                Log.e("ScanVM", "PROCESS: AI parsing finished. Result type: ${aiResult::class.simpleName}")
                
                if (aiResult is ResultState.Success) {
                    Log.e("ScanVM", "PROCESS: AI success. Data: ${aiResult.data}")
                    _uiState.update { it.copy(
                        isLoading = false,
                        isSuccess = true,
                        parsedData = aiResult.data
                    ) }
                } else if (aiResult is ResultState.Error) {
                    Log.e("ScanVM", "PROCESS: AI Error: ${aiResult.message}")
                    _uiState.update { it.copy(isLoading = false, errorMessage = aiResult.message) }
                }
            } else if (ocrResult is ResultState.Error) {
                Log.e("ScanVM", "PROCESS: OCR Error: ${ocrResult.message}")
                _uiState.update { it.copy(isLoading = false, errorMessage = ocrResult.message) }
            }
        }
    }

    fun resetState() {
        _uiState.update { ScanUiState() }
    }
}
