package com.example.data.repository

import com.example.data.local.dao.DesignBazaarDao
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.SupportMessageEntity
import com.example.data.model.ADMIN_PHONE
import com.example.data.model.CartItem
import com.example.data.model.DesignCategory
import com.example.data.model.Order
import com.example.data.model.PLATFORM_COMMISSION_RATE
import com.example.data.model.Product
import com.example.data.model.SELLER_PAYOUT_RATE
import com.example.data.model.SellerStats
import com.example.data.model.SupportMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class DesignBazaarRepository(private val dao: DesignBazaarDao) {

    val allProducts: Flow<List<Product>> = dao.getAllProducts().map { entities ->
        entities.map { it.toDomain() }
    }

    val myListings: Flow<List<Product>> = dao.getMyListings().map { entities ->
        entities.map { it.toDomain() }
    }

    val cartItems: Flow<List<CartItem>> = combine(dao.getAllCartItems(), allProducts) { cartEntities, products ->
        val productMap = products.associateBy { it.id }
        cartEntities.mapNotNull { cartEntity ->
            productMap[cartEntity.productId]?.let { prod ->
                CartItem(
                    id = cartEntity.id,
                    product = prod,
                    addedAt = cartEntity.addedAt
                )
            }
        }
    }

    val allOrders: Flow<List<Order>> = dao.getAllOrders().map { entities ->
        entities.map { it.toDomain() }
    }

    val supportMessages: Flow<List<SupportMessage>> = dao.getAllSupportMessages().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getProductById(id: Long): Product? {
        return dao.getProductById(id)?.toDomain()
    }

    suspend fun addToCart(productId: Long) {
        dao.addToCart(CartItemEntity(productId = productId))
    }

    suspend fun removeFromCart(productId: Long) {
        dao.removeFromCart(productId)
    }

    suspend fun clearCart() {
        dao.clearCart()
    }

    suspend fun createProductListing(
        title: String,
        category: DesignCategory,
        price: Double,
        description: String,
        fileType: String,
        fileSize: String,
        drawableResName: String,
        tags: List<String>,
        sellerName: String = "You (Verified Creator)",
        sellerPhone: String = ADMIN_PHONE
    ): Long {
        val entity = ProductEntity(
            title = title,
            category = category.id,
            price = price,
            description = description,
            fileType = fileType,
            fileSize = fileSize,
            sellerName = sellerName,
            sellerPhone = sellerPhone,
            isMyListing = true,
            rating = 5.0f,
            reviewCount = 1,
            drawableResName = drawableResName,
            downloadCount = 0,
            tags = tags.joinToString(","),
            licenseType = "Commercial & Merchandising Rights Included",
            createdAt = System.currentTimeMillis()
        )
        return dao.insertProduct(entity)
    }

    suspend fun deleteListing(productId: Long) {
        dao.deleteProductById(productId)
    }

    suspend fun processCheckout(
        products: List<Product>,
        paymentMethodName: String
    ): List<Order> {
        val createdOrders = mutableListOf<Order>()
        val dateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        
        products.forEach { prod ->
            val randomSuffix = (1000..9999).random()
            val orderNumber = "DB-$dateStr-$randomSuffix"
            val txnId = "TXN" + System.currentTimeMillis() + (100..999).random()
            val platformFee = prod.price * PLATFORM_COMMISSION_RATE // 20%
            val sellerPayout = prod.price * SELLER_PAYOUT_RATE       // 80%

            val orderEntity = OrderEntity(
                orderNumber = orderNumber,
                productId = prod.id,
                productTitle = prod.title,
                productCategory = prod.category.id,
                totalAmount = prod.price,
                platformCommission = platformFee,
                sellerPayout = sellerPayout,
                paymentMethod = paymentMethodName,
                transactionId = txnId,
                purchaseTimestamp = System.currentTimeMillis(),
                downloadStatus = "READY",
                invoiceUrl = "INV-$orderNumber.pdf",
                drawableResName = prod.drawableResName,
                fileSize = prod.fileSize,
                fileType = prod.fileType
            )
            val newId = dao.insertOrder(orderEntity)
            createdOrders.add(orderEntity.copy(id = newId).toDomain())
        }

        // Clear cart for purchased products
        products.forEach { dao.removeFromCart(it.id) }

        return createdOrders
    }

    suspend fun markOrderDownloaded(orderId: Long) {
        dao.markOrderDownloaded(orderId)
    }

    suspend fun sendUserSupportMessage(userQuery: String) {
        val userEntity = SupportMessageEntity(
            sender = "USER",
            message = userQuery,
            timestamp = System.currentTimeMillis()
        )
        dao.insertSupportMessage(userEntity)

        // Generate context-aware response
        val botResponse = generateSupportAnswer(userQuery)
        val agentEntity = SupportMessageEntity(
            sender = "AGENT",
            message = botResponse,
            timestamp = System.currentTimeMillis() + 800
        )
        dao.insertSupportMessage(agentEntity)
    }

    private fun generateSupportAnswer(query: String): String {
        val q = query.lowercase(Locale.getDefault())
        return when {
            q.contains("commission") || q.contains("20%") || q.contains("earning") || q.contains("payout") || q.contains("percent") -> {
                "Platform Commission Rule: For every sale on DesignBazaar, 20% commission goes to platform maintenance & escrow security (Admin Phone: $ADMIN_PHONE). 80% of the sale price is credited directly to your Seller Wallet, withdrawable anytime to UPI/Bank account!"
            }
            q.contains("download") || q.contains("file") || q.contains("access") -> {
                "Downloads are instant! As soon as your online payment is verified, visit the 'My Purchases' tab to download source files (.MP4, .RAW, .DWG, .AI vectors) along with your GST/commercial invoice."
            }
            q.contains("sell") || q.contains("upload") || q.contains("list") -> {
                "Selling is simple & open to everyone! Tap the 'Sell (+)' tab, choose your design category (Video, Photo, House Design, or T-Shirt Design), set your price in ₹, add description & format specs, and publish immediately."
            }
            q.contains("payment") || q.contains("upi") || q.contains("gpay") || q.contains("refund") -> {
                "We support instant 100% secure payments via UPI (GPay, PhonePe, Paytm, BHIM), Cards, and Net Banking. All transactions are covered by DesignBazaar Buyer Protection."
            }
            q.contains("call") || q.contains("number") || q.contains("contact") || q.contains("phone") || q.contains("admin") -> {
                "You can directly reach the DesignBazaar Admin at +91 $ADMIN_PHONE via Phone Call or WhatsApp for priority seller onboarding and VIP support!"
            }
            else -> {
                "Namaste! Thank you for reaching DesignBazaar Customer Service. Our executive and Admin ($ADMIN_PHONE) are actively monitoring. Feel free to ask about buying, selling, payments, 20% platform commission, or file downloads."
            }
        }
    }

    suspend fun getSellerStats(): SellerStats {
        val userListings = dao.getMyListings().firstOrNull() ?: emptyList()
        val allOrdersList = dao.getAllOrders().firstOrNull() ?: emptyList()
        
        // Calculate sales for user listings or simulate verified initial activity
        val userProductIds = userListings.map { it.id }.toSet()
        val userSoldOrders = allOrdersList.filter { userProductIds.contains(it.productId) }
        
        val totalListings = userListings.size
        val totalSoldUnits = if (userSoldOrders.isNotEmpty()) userSoldOrders.size else if (totalListings > 0) 3 else 0
        val grossSales = if (userSoldOrders.isNotEmpty()) {
            userSoldOrders.sumOf { it.totalAmount }
        } else if (totalListings > 0) {
            userListings.sumOf { it.price } * 1.5
        } else {
            0.0
        }
        val platformFees = grossSales * PLATFORM_COMMISSION_RATE
        val netEarnings = grossSales * SELLER_PAYOUT_RATE

        return SellerStats(
            totalListings = totalListings,
            totalSalesUnits = totalSoldUnits,
            grossSales = grossSales,
            totalPlatformFees = platformFees,
            netEarnings = netEarnings,
            pendingWithdrawal = 0.0
        )
    }

    suspend fun initializeSeedDataIfNeeded() {
        val count = dao.getProductCount()
        if (count == 0) {
            val seedProducts = listOf(
                // Video Category
                ProductEntity(
                    title = "Cinematic 4K Cyberpunk Neon Title & Reels Video Pack",
                    category = DesignCategory.VIDEO.id,
                    price = 799.0,
                    description = "Ultra HD 4K 60FPS motion graphics template with customizable 3D neon titles, audio reactive particles, and 15 modern transition overlays. Ready for Premiere Pro & After Effects.",
                    fileType = "4K 60FPS MP4 + .AEP (After Effects Project)",
                    fileSize = "1.4 GB",
                    sellerName = "MotionCraft FX",
                    sellerPhone = ADMIN_PHONE,
                    isMyListing = false,
                    rating = 4.9f,
                    reviewCount = 38,
                    drawableResName = "preview_video",
                    downloadCount = 245,
                    tags = "Video,Reels,Motion Graphics,4K,Template",
                    licenseType = "Full Commercial License",
                    createdAt = System.currentTimeMillis() - 10000000
                ),
                ProductEntity(
                    title = "Modern YouTube Tech & Gaming Intro Video Template",
                    category = DesignCategory.VIDEO.id,
                    price = 499.0,
                    description = "High energy dynamic gaming & tech review video opener with sound effects, customizable 3D text layers, and logo reveal animation. Includes clean render exports.",
                    fileType = "1080p / 4K MP4 + DaVinci Resolve & FCPX",
                    fileSize = "620 MB",
                    sellerName = "PixelForge Studios",
                    sellerPhone = ADMIN_PHONE,
                    isMyListing = false,
                    rating = 4.7f,
                    reviewCount = 21,
                    drawableResName = "preview_video",
                    downloadCount = 180,
                    tags = "Intro,YouTube,Gaming,Video,Logo Reveal",
                    licenseType = "Commercial & YouTube Monetization Allowed",
                    createdAt = System.currentTimeMillis() - 8000000
                ),

                // House Design Category
                ProductEntity(
                    title = "Modern Luxury 3-Floor Duplex Villa 3D Elevation & CAD Floor Plan",
                    category = DesignCategory.HOUSE_DESIGN.id,
                    price = 2499.0,
                    description = "Complete architectural package for 30x60 ft plot: 2D floor plans with structural columns, plumbing & electrical layouts, plus photorealistic 3D front elevation exterior & interior 4K renders.",
                    fileType = "AutoCAD .DWG + Revit 3D + 4K Renders",
                    fileSize = "480 MB",
                    sellerName = "Ar. Rahul Sharma Architecture",
                    sellerPhone = ADMIN_PHONE,
                    isMyListing = false,
                    rating = 5.0f,
                    reviewCount = 54,
                    drawableResName = "preview_house",
                    downloadCount = 310,
                    tags = "House Design,Architectural,3D Elevation,CAD,Floor Plan",
                    licenseType = "Unlimited Construction Rights",
                    createdAt = System.currentTimeMillis() - 15000000
                ),
                ProductEntity(
                    title = "25x45 Ft 4BHK Compact Modern Home Blueprints & Interior 3D",
                    category = DesignCategory.HOUSE_DESIGN.id,
                    price = 1499.0,
                    description = "Smart Indian architectural home plan featuring open modular kitchen, rooftop gazebo, car parking, Vastu compliant layouts, and detailed dimension blueprints.",
                    fileType = ".DWG CAD + PDF Print Ready + 3ds Max",
                    fileSize = "320 MB",
                    sellerName = "DesignVastu Consultants",
                    sellerPhone = ADMIN_PHONE,
                    isMyListing = false,
                    rating = 4.8f,
                    reviewCount = 29,
                    drawableResName = "preview_house",
                    downloadCount = 195,
                    tags = "House Design,Vastu,BHK,Floor Plan,3D",
                    licenseType = "Architectural Building License",
                    createdAt = System.currentTimeMillis() - 12000000
                ),

                // T-Shirt Design Category
                ProductEntity(
                    title = "Urban Streetwear Cyberpunk Samurai Graphic T-Shirt Vector Art",
                    category = DesignCategory.TSHIRT_DESIGN.id,
                    price = 399.0,
                    description = "Premium high-res Japanese typography and neo-tokyo samurai streetwear illustration. Scalable vector artwork optimized for DTF, Screen Print, and Sublimation merch.",
                    fileType = "Adobe Illustrator .AI + EPS + 300 DPI Transparent PNG",
                    fileSize = "95 MB",
                    sellerName = "TokyoDrip Apparel",
                    sellerPhone = ADMIN_PHONE,
                    isMyListing = false,
                    rating = 4.9f,
                    reviewCount = 67,
                    drawableResName = "preview_tshirt",
                    downloadCount = 520,
                    tags = "T-Shirt Design,Streetwear,Vector,Merchandise,Apparel",
                    licenseType = "Unlimited Merchandising & POD Allowed",
                    createdAt = System.currentTimeMillis() - 9000000
                ),
                ProductEntity(
                    title = "Vintage Indian Typography & Motorcycle Club Graphic Tee Mockup",
                    category = DesignCategory.TSHIRT_DESIGN.id,
                    price = 299.0,
                    description = "Distressed retro vintage emblem design with custom lettering, piston skull illustration, and dark grunge overlays. Includes editable text layers and realistic fabric mockup.",
                    fileType = "Photoshop .PSD Mockup + Vector SVG + PNG",
                    fileSize = "110 MB",
                    sellerName = "DesiCraft Apparel",
                    sellerPhone = ADMIN_PHONE,
                    isMyListing = false,
                    rating = 4.7f,
                    reviewCount = 33,
                    drawableResName = "preview_tshirt",
                    downloadCount = 340,
                    tags = "T-Shirt Design,Vintage,Desi,Typography,Apparel",
                    licenseType = "Commercial Print License",
                    createdAt = System.currentTimeMillis() - 7000000
                ),

                // Photo Category
                ProductEntity(
                    title = "Himalayan Sunrise Lake Reflection 8K Ultra-HD Landscape Photo",
                    category = DesignCategory.PHOTO.id,
                    price = 349.0,
                    description = "Stunning commercial landscape photography captured at Pangong Lake during golden hour. Ultra high dynamic range with vivid pink-gold clouds and razor-sharp crystal waters.",
                    fileType = "Sony Alpha RAW (.ARW) + 50MP JPEG (300 DPI)",
                    fileSize = "82 MB",
                    sellerName = "ShutterPeak Photography",
                    sellerPhone = ADMIN_PHONE,
                    isMyListing = false,
                    rating = 4.8f,
                    reviewCount = 44,
                    drawableResName = "preview_photo",
                    downloadCount = 280,
                    tags = "Photo,Landscape,Himalayas,Wallpaper,8K",
                    licenseType = "Editorial & Commercial Advertising License",
                    createdAt = System.currentTimeMillis() - 6000000
                ),
                ProductEntity(
                    title = "Monsoon Heritage Fort & Mist Aesthetic Wallpaper Photo Pack",
                    category = DesignCategory.PHOTO.id,
                    price = 249.0,
                    description = "Atmospheric fine-art photography collection featuring ancient Rajasthani forts shrouded in monsoon fog, cinematic moody lighting, and color grading presets.",
                    fileType = "10x Ultra HD RAW + Lightroom .XMP Presets",
                    fileSize = "165 MB",
                    sellerName = "Vantage Heritage",
                    sellerPhone = ADMIN_PHONE,
                    isMyListing = false,
                    rating = 4.6f,
                    reviewCount = 18,
                    drawableResName = "preview_photo",
                    downloadCount = 140,
                    tags = "Photo,Monsoon,Heritage,Lightroom Presets,Wallpaper",
                    licenseType = "Commercial & Web License",
                    createdAt = System.currentTimeMillis() - 5000000
                )
            )
            dao.insertProducts(seedProducts)

            // Seed initial welcome customer support message
            dao.insertSupportMessage(
                SupportMessageEntity(
                    sender = "AGENT",
                    message = "Welcome to DesignBazaar! 🎨✨ I am your dedicated customer support assistant. You can buy or sell digital designs (Video, Photo, House Design, T-Shirt Design) with instant online payment. For any direct query, our owner/admin is available at +91 $ADMIN_PHONE.",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // Converters
    private fun ProductEntity.toDomain() = Product(
        id = id,
        title = title,
        category = DesignCategory.fromId(category),
        price = price,
        description = description,
        fileType = fileType,
        fileSize = fileSize,
        sellerName = sellerName,
        sellerPhone = sellerPhone,
        isMyListing = isMyListing,
        rating = rating,
        reviewCount = reviewCount,
        drawableResName = drawableResName,
        downloadCount = downloadCount,
        tags = if (tags.isBlank()) emptyList() else tags.split(","),
        licenseType = licenseType,
        createdAt = createdAt
    )

    private fun OrderEntity.toDomain() = Order(
        id = id,
        orderNumber = orderNumber,
        productId = productId,
        productTitle = productTitle,
        productCategory = DesignCategory.fromId(productCategory),
        totalAmount = totalAmount,
        platformCommission = platformCommission,
        sellerPayout = sellerPayout,
        paymentMethod = paymentMethod,
        transactionId = transactionId,
        purchaseTimestamp = purchaseTimestamp,
        downloadStatus = downloadStatus,
        invoiceUrl = invoiceUrl,
        drawableResName = drawableResName,
        fileSize = fileSize,
        fileType = fileType
    )

    private fun SupportMessageEntity.toDomain() = SupportMessage(
        id = id,
        sender = sender,
        message = message,
        timestamp = timestamp
    )
}
