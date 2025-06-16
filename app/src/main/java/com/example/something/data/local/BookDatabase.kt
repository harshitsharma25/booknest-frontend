package com.example.something.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.something.data.local.dao.BookDao
import com.example.something.data.local.entities.CartItem
import com.example.something.utils.Converters


@Database(entities = [CartItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BookDatabase : RoomDatabase(){
    abstract fun BookDao() : BookDao

    companion object {
        @Volatile private var Instance : BookDatabase? = null

        fun getDatabase(context: Context) : BookDatabase {
            return Instance ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    "cart_database"
                ).fallbackToDestructiveMigration()
                    .build()
                Instance = instance
                instance
            }
        }
    }
}