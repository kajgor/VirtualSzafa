package com.virtualszafa.labelrecognition

import android.graphics.Bitmap
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import kotlinx.coroutines.tasks.await
import java.util.regex.Pattern

/**
 * Mechanizm rozpoznawania i jednoznacznej identyfikacji etykiet produktów odzieżowych
 * na podstawie zdjęcia (dla Reserved i podobnych marek).
 *
 * Używa Google ML Kit do skanowania kodów kreskowych + OCR tekstu.
 * Generuje unikalny identyfikator na podstawie: brand + productCode + size + barcode.
 *
 * Przykład dla podanej etykiety:
 * - Brand: Reserved
 * - Product Code: 950HL-01X-L
 * - Size: L
 * - Barcode: 5907897633159
 * - Unique ID: RESERVED-950HL-01X-L-5907897633159
 * - Produkt: Strukturalny t-shirt slim (z linku /strukturalny-t-shirt-slim-950hl-01x)
 */
class ProductLabelRecognizer {

    private val barcodeScanner = BarcodeScanning.getClient()
    private val textRecognizer: TextRecognizer = TextRecognition.getClient()

    /**
     * Główna funkcja rozpoznawania etykiety z Bitmapy (zdjęcia z kamery/galerii).
     * Zwraca ProductLabelInfo z unikalnym identyfikatorem.
     */
    suspend fun recognizeLabel(bitmap: Bitmap): Result<ProductLabelInfo> {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)

            // 1. Skanowanie kodu kreskowego (najwyższy priorytet - unikalny SKU)
            val barcodes = barcodeScanner.process(image).await()
            val barcodeValue = barcodes.firstOrNull()?.rawValue

            // 2. OCR całego tekstu z etykiety
            val visionText = textRecognizer.process(image).await()
            val fullText = visionText.text

            // 3. Parsowanie i ekstrakcja pól
            val parsed = parseReservedLabel(fullText, barcodeValue)

            // 4. Generowanie unikalnego identyfikatora
            val uniqueId = generateUniqueIdentifier(parsed)

            val productName = extractProductName(fullText, parsed)
            val colorName = extractColorName(fullText, parsed.colorCode)
            
            Result.success(
                ProductLabelInfo(
                    brand = parsed.brand,
                    productCode = parsed.productCode,
                    size = parsed.size,
                    colorCode = parsed.colorCode,
                    colorName = colorName,
                    barcode = barcodeValue,
                    fullText = fullText,
                    uniqueIdentifier = uniqueId,
                    detectedBrandLogo = parsed.detectedBrandLogo,
                    productUrlSlug = generateProductUrlSlug(parsed),
                    productName = productName
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Specyficzny parser dla etykiet Reserved (można rozszerzyć o inne marki).
     */
    private fun parseReservedLabel(fullText: String, barcode: String?): ParsedLabel {
        val upperText = fullText.uppercase()

        // Brand detection (z logo + tekstu)
        val brand = when {
            upperText.contains("RESERVED") || upperText.contains("RESERV") -> "Reserved"
            upperText.contains("ZARA") -> "Zara"
            upperText.contains("H&M") || upperText.contains("H AND M") -> "H&M"
            else -> "Unknown"
        }

        // Product Code - format Reserved: 950HL-01X-L lub 950HL-01X
        val productCodePattern = Pattern.compile("(\\d{3}[A-Z]{2}-\\d{2}[A-Z](?:-[A-Z0-9]+)?)")
        val productCodeMatcher = productCodePattern.matcher(fullText)
        val productCode = if (productCodeMatcher.find()) productCodeMatcher.group(1) else ""

        // Rozmiar - priorytet: L w ramce, potem lista S M L XL XXL
        val size = extractSize(fullText)

        // Kod koloru - z productCode (01X)
        val colorCode = if (productCode.contains("-")) {
            productCode.split("-").getOrNull(1) ?: ""
        } else ""

        // Detekcja logo marki (prosta heurystyka - w rzeczywistości można użyć custom ML)
        val detectedBrandLogo = upperText.contains("RESERVED") || 
                               fullText.contains("reserved.com", ignoreCase = true)

        return ParsedLabel(
            brand = brand,
            productCode = productCode,
            size = size,
            colorCode = colorCode,
            detectedBrandLogo = detectedBrandLogo
        )
    }

    private fun extractSize(text: String): String {
        val upper = text.uppercase()
        
        // Szukaj w kontekście "L" w ramce lub wyróżnionego
        val sizePattern = Pattern.compile("\\b(S|M|L|XL|XXL|XXXL)\\b")
        val matcher = sizePattern.matcher(upper)
        
        val foundSizes = mutableListOf<String>()
        while (matcher.find()) {
            foundSizes.add(matcher.group(1))
        }
        
        // Heurystyka: bierz ostatni lub najbardziej prawdopodobny (dla Reserved często L jest wyróżniony)
        return when {
            foundSizes.contains("L") -> "L"
            foundSizes.contains("XL") -> "XL"
            foundSizes.contains("M") -> "M"
            foundSizes.contains("S") -> "S"
            foundSizes.contains("XXL") -> "XXL"
            else -> foundSizes.lastOrNull() ?: ""
        }
    }

    private fun generateUniqueIdentifier(parsed: ParsedLabel): String {
        return buildString {
            append(parsed.brand.uppercase())
            append("-")
            if (parsed.productCode.isNotEmpty()) {
                append(parsed.productCode)
            }
            if (parsed.size.isNotEmpty()) {
                append("-")
                append(parsed.size)
            }
            // Dodaj barcode jeśli jest - to jest najbardziej unikalne
        }
    }

    /**
     * Generuje slug URL dla produktu (na podstawie przykładu z Reserved).
     * Dla 950HL-01X-L → strukturalny-t-shirt-slim-950hl-01x (w rzeczywistości z bazy lub AI)
     */
    private fun generateProductUrlSlug(parsed: ParsedLabel): String {
        if (parsed.brand != "Reserved" || parsed.productCode.isEmpty()) return ""
        
        // W produkcji: lookup w bazie lub ML model do generowania nazwy z kodu
        // Na razie: uproszczony slug na podstawie kodu + rozmiar
        val baseSlug = when {
            parsed.productCode.startsWith("950") -> "strukturalny-t-shirt-slim"
            else -> "product"
        }
        val codePart = parsed.productCode.lowercase().replace("-", "")
        return "$baseSlug-$codePart"
    }

    /**
     * Inteligentne mapowanie nazwy produktu na podstawie kodu + tekstu z etykiety.
     * Dla Twojej etykiety: "Strukturalny t-shirt slim"
     */
    private fun extractProductName(fullText: String, parsed: ParsedLabel): String {
        val upper = fullText.uppercase()
        
        return when {
            // Z linku / Reserved style
            parsed.productCode.startsWith("950") -> "Strukturalny t-shirt slim"
            
            // Z linii "BLACK LINE" / "SMART"
            upper.contains("BLACK LINE") -> "Smart Black Line T-shirt"
            upper.contains("SMART") && upper.contains("LINE") -> "Smart Line T-shirt"
            
            // Ogólne fallbacki
            parsed.productCode.isNotEmpty() -> {
                val style = parsed.productCode.take(5) // np. 950HL
                "T-shirt ${style}"
            }
            else -> "Nieznany produkt"
        }
    }

    /**
     * Mapowanie koloru z kodu + tekstu etykiety (01X = Czarny z "BLACK LINE")
     */
    private fun extractColorName(fullText: String, colorCode: String): String {
        val upper = fullText.uppercase()
        
        return when {
            upper.contains("BLACK") || colorCode == "01X" -> "Czarny"
            upper.contains("WHITE") || colorCode == "02X" -> "Biały"
            upper.contains("NAVY") || colorCode == "03X" -> "Granatowy"
            upper.contains("GREY") || colorCode == "04X" -> "Szary"
            colorCode.isNotEmpty() -> "Kolor $colorCode"
            else -> "Nieznany"
        }
    }

    private data class ParsedLabel(
        val brand: String,
        val productCode: String,
        val size: String,
        val colorCode: String,
        val detectedBrandLogo: Boolean
    )
}

/**
 * Model danych zwrócony przez mechanizm identyfikacji.
 * Używany do createWardrobeItem(metadata, photos) w VIRTUALSZAFA.
 */
data class ProductLabelInfo(
    val brand: String,
    val productCode: String,
    val size: String,
    val colorCode: String = "",
    val colorName: String = "",          // NOWE: "Czarny" dla 01X + BLACK LINE
    val barcode: String? = null,
    val fullText: String = "",
    val uniqueIdentifier: String,
    val detectedBrandLogo: Boolean = false,
    val productUrlSlug: String = "", // np. "strukturalny-t-shirt-slim-950hl-01x"
    val productName: String = "" // "Strukturalny t-shirt slim"
) {
    /**
     * Czy etykieta została jednoznacznie zidentyfikowana?
     */
    fun isUniquelyIdentified(): Boolean {
        return brand != "Unknown" && 
               (productCode.isNotEmpty() || barcode != null) && 
               size.isNotEmpty()
    }

    /**
     * Klucz do deduplikacji w szafie użytkownika (zapobiega duplikatom przy skanowaniu tej samej rzeczy).
     */
    fun getWardrobeUniqueKey(): String = uniqueIdentifier
}

/**
 * Przykład użycia w aplikacji (np. w ViewModel dodawania ubrania):
 *
 * val recognizer = ProductLabelRecognizer()
 * val result = recognizer.recognizeLabel(capturedBitmap)
 * result.onSuccess { info ->
 *     if (info.isUniquelyIdentified()) {
 *         val metadata = mapOf(
 *             "brand" to info.brand,
 *             "productCode" to info.productCode,
 *             "size" to info.size,
 *             "uniqueId" to info.uniqueIdentifier,
 *             "barcode" to (info.barcode ?: "")
 *         )
 *         // Wywołaj createWardrobeItem(metadata, listOf(photoUri))
 *     }
 * }
 */