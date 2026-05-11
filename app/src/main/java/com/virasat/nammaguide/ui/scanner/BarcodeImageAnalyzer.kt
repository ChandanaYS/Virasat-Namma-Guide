package com.virasat.nammaguide.ui.scanner

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE
import com.google.mlkit.vision.common.InputImage
import com.virasat.nammaguide.data.HeritageQrParser

class BarcodeImageAnalyzer(
    private val onQrScanned: (rawValue: String, placeId: String?) -> Unit
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(FORMAT_QR_CODE)
            .build()
    )
    private var hasScanned = false

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null || hasScanned) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val qrBarcode = barcodes
                    .asSequence()
                    .filter { it.format == Barcode.FORMAT_QR_CODE }
                    .firstOrNull { !it.rawValue.isNullOrBlank() }

                val rawValue = qrBarcode?.rawValue
                if (rawValue != null && !hasScanned) {
                    val placeId = HeritageQrParser.parsePlaceId(rawValue)
                    hasScanned = true
                    onQrScanned(rawValue, placeId)
                }
            }
            .addOnFailureListener {
                // Keep scanning; transient frame failures are normal with live camera input.
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun reset() {
        hasScanned = false
    }

    fun close() {
        scanner.close()
    }
}
