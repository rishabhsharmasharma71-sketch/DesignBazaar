package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.DesignBazaarDao
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.SupportMessageEntity

@Database(
    entities = [
        ProductEntity::class,
        OrderEntity::class,
        CartItemEntity::class,
        SupportMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun designBazaarDao(): DesignBazaarDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "design_bazaar_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
