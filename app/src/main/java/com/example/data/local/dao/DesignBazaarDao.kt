package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.SupportMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DesignBazaarDao {
    // Products
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE isMyListing = 1 ORDER BY id DESC")
    fun getMyListings(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY id DESC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Long)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    // Cart
    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(cartItem: CartItemEntity): Long

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun removeFromCart(productId: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // Orders / Purchases
    @Query("SELECT * FROM orders ORDER BY purchaseTimestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Long): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("UPDATE orders SET downloadStatus = 'DOWNLOADED' WHERE id = :id")
    suspend fun markOrderDownloaded(id: Long)

    // Support Messages
    @Query("SELECT * FROM support_messages ORDER BY timestamp ASC")
    fun getAllSupportMessages(): Flow<List<SupportMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportMessage(message: SupportMessageEntity): Long
}
