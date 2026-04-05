package com.market.android.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [GroceryItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GroceryDatabase : RoomDatabase() {

    abstract fun groceryItemDao(): GroceryItemDao

    companion object {
        fun create(context: Context): GroceryDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                GroceryDatabase::class.java,
                "market.db"
            ).build()
    }
}
