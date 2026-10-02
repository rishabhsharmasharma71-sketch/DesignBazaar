package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // VIDEO, PHOTO, HOUSE_DESIGN, TSHIRT_DESIGN
    val price: Double,
    val description: String,
    val fileType: String,
    val fileSize: String,
    val sellerName: String,
    val sellerPhone: String,
    val isMyListing: Boolean,
    val rating: Float,
    val reviewCount: Int,
    val drawableResName: String,
    val downloadCount: Int,
    val tags: String, // comma separated
    val licenseType: String,
    val createdAt: Long
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val productId: Long,
    val productTitle: String,
    val productCategory: String,
    val totalAmount: Double,
    val platformCommission: Double,
    val sellerPayout: Double,
    val paymentMethod: String,
    val transactionId: String,
    val purchaseTimestamp: Long,
    val downloadStatus: String,
    val invoiceUrl: String,
    val drawableResName: String,
    val fileSize: String,
    val fileType: String
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_messages")
data class SupportMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
