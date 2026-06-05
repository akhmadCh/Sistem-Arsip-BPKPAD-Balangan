package com.example.arsipbpkpad.utils

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Test

class DateVisualTransformationTest {

    private val transformation = DateVisualTransformation()

    @Test
    fun `test formatting with 8 digits`() {
        val input = AnnotatedString("12032024")
        val result = transformation.filter(input)
        assertEquals("12-03-2024", result.text.text)
    }

    @Test
    fun `test partial formatting`() {
        val input = AnnotatedString("12")
        val result = transformation.filter(input)
        assertEquals("12-", result.text.text)
        
        val input2 = AnnotatedString("1203")
        val result2 = transformation.filter(input2)
        assertEquals("12-03-", result2.text.text)
    }

    @Test
    fun `test offset mapping original to transformed`() {
        val input = AnnotatedString("12032024")
        val result = transformation.filter(input)
        val mapping = result.offsetMapping

        // Input: 12032024 (8 chars) -> Output: 12-03-2024 (10 chars)
        assertEquals(0, mapping.originalToTransformed(0)) // index 0 (1) -> 0
        assertEquals(1, mapping.originalToTransformed(1)) // index 1 (2) -> 1
        assertEquals(3, mapping.originalToTransformed(2)) // index 2 (0) -> 3 (after first dash)
        assertEquals(4, mapping.originalToTransformed(3)) // index 3 (3) -> 4
        assertEquals(6, mapping.originalToTransformed(4)) // index 4 (2) -> 6 (after second dash)
        assertEquals(7, mapping.originalToTransformed(5))
        assertEquals(8, mapping.originalToTransformed(6))
        assertEquals(9, mapping.originalToTransformed(7))
        assertEquals(10, mapping.originalToTransformed(8))
    }

    @Test
    fun `test offset mapping transformed to original`() {
        val input = AnnotatedString("12032024")
        val result = transformation.filter(input)
        val mapping = result.offsetMapping

        // Output: 12-03-2024 -> Input: 12032024
        assertEquals(0, mapping.transformedToOriginal(0))
        assertEquals(1, mapping.transformedToOriginal(1))
        assertEquals(2, mapping.transformedToOriginal(2)) // Dash at 2 maps to original 2
        assertEquals(2, mapping.transformedToOriginal(3)) // index 3 (0) maps to original 2
        assertEquals(3, mapping.transformedToOriginal(4))
        assertEquals(4, mapping.transformedToOriginal(5)) // Dash at 5 maps to original 4
        assertEquals(4, mapping.transformedToOriginal(6)) // index 6 (2) maps to original 4
        assertEquals(5, mapping.transformedToOriginal(7))
        assertEquals(6, mapping.transformedToOriginal(8))
        assertEquals(7, mapping.transformedToOriginal(9))
        assertEquals(8, mapping.transformedToOriginal(10))
    }
}
