package com.example.data.model

enum class PaymentMethod(
    val title: String,
    val description: String,
    val iconName: String
) {
    UPI_GPAY("Google Pay / PhonePe / Paytm", "Fastest zero-fee instant UPI payment", "upi"),
    UPI_CUSTOM("Any UPI ID / VPA", "Pay using your @okhdfcbank, @paytm, @ybl", "qr"),
    DEBIT_CREDIT_CARD("Debit / Credit Card", "Visa, MasterCard, RuPay, Maestro", "card"),
    NET_BANKING("Net Banking", "SBI, HDFC, ICICI, Axis & 50+ Banks", "bank"),
    WALLET("DesignBazaar Wallet", "Instant checkout with available balance", "wallet")
}

data class Order(
    val id: Long = 0,
    val orderNumber: String,
    val productId: Long,
    val productTitle: String,
    val productCategory: DesignCategory,
    val totalAmount: Double,
    val platformCommission: Double, // 20%
    val sellerPayout: Double,        // 80%
    val paymentMethod: String,
    val transactionId: String,
    val purchaseTimestamp: Long = System.currentTimeMillis(),
    val downloadStatus: String = "READY",
    val invoiceUrl: String,
    val drawableResName: String,
    val fileSize: String,
    val fileType: String
)

data class CartItem(
    val id: Long = 0,
    val product: Product,
    val addedAt: Long = System.currentTimeMillis()
)

data class SupportMessage(
    val id: Long = 0,
    val sender: String, // "USER" or "AGENT"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class SellerStats(
    val totalListings: Int,
    val totalSalesUnits: Int,
    val grossSales: Double,
    val totalPlatformFees: Double, // 20%
    val netEarnings: Double,       // 80%
    val pendingWithdrawal: Double = 0.0
)
