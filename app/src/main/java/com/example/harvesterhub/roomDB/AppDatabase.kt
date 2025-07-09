package com.example.harvesterhub.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.harvesterhub.data.models.MarketPriceEntity

@Database(entities = [MarketPriceEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun marketPriceDao(): MarketPriceDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "market_price_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
