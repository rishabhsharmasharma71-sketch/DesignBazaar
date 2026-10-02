package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CatHouseColor
import com.example.ui.theme.CatPhotoColor
import com.example.ui.theme.CatTshirtColor
import com.example.ui.theme.CatVideoColor

const val ADMIN_PHONE = "9241185235"
const val PLATFORM_COMMISSION_RATE = 0.20 // 20% platform cut to Admin (9241185235)
const val SELLER_PAYOUT_RATE = 0.80       // 80% to seller

enum class DesignCategory(
    val id: String,
    val displayName: String,
    val hindiName: String,
    val badgeColor: Color,
    val defaultFormat: String,
    val iconName: String
) {
    VIDEO(
        id = "VIDEO",
        displayName = "Video",
        hindiName = "वीडियो डिजाइन",
        badgeColor = CatVideoColor,
        defaultFormat = "4K MP4 / After Effects",
        iconName = "video"
    ),
    PHOTO(
        id = "PHOTO",
        displayName = "Photo",
        hindiName = "फोटो और वॉलपेपर",
        badgeColor = CatPhotoColor,
        defaultFormat = "RAW / High-Res JPEG",
        iconName = "photo"
    ),
    HOUSE_DESIGN(
        id = "HOUSE_DESIGN",
        displayName = "House Design",
        hindiName = "घर का नक्शा / 3D डिजाइन",
        badgeColor = CatHouseColor,
        defaultFormat = "2D CAD .DWG + 3D Render",
        iconName = "house"
    ),
    TSHIRT_DESIGN(
        id = "TSHIRT_DESIGN",
        displayName = "T-Shirt Design",
        hindiName = "टी-शर्ट ग्राफिक डिजाइन",
        badgeColor = CatTshirtColor,
        defaultFormat = "Vector .AI + Print PNG (300 DPI)",
        iconName = "tshirt"
    );

    companion object {
        fun fromId(id: String): DesignCategory {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: VIDEO
        }
    }
}

data class Product(
    val id: Long = 0,
    val title: String,
    val category: DesignCategory,
    val price: Double,
    val description: String,
    val fileType: String,
    val fileSize: String,
    val sellerName: String,
    val sellerPhone: String = ADMIN_PHONE,
    val isMyListing: Boolean = false,
    val rating: Float = 4.8f,
    val reviewCount: Int = 12,
    val drawableResName: String,
    val downloadCount: Int = 120,
    val tags: List<String> = emptyList(),
    val licenseType: String = "Commercial & Personal License",
    val createdAt: Long = System.currentTimeMillis()
) {
    val platformFee: Double
        get() = price * PLATFORM_COMMISSION_RATE

    val sellerNetEarnings: Double
        get() = price * SELLER_PAYOUT_RATE
}
