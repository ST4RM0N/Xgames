package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.GardenDao
import com.example.data.entity.BotRecipeEntity
import com.example.data.entity.GardenEdgeEntity
import com.example.data.entity.GardenNodeEntity
import com.example.data.entity.RecipeStepEntity

@Database(
    entities = [
        GardenNodeEntity::class,
        GardenEdgeEntity::class,
        BotRecipeEntity::class,
        RecipeStepEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GrokpotDatabase : RoomDatabase() {
    abstract fun gardenDao(): GardenDao

    companion object {
        @Volatile
        private var INSTANCE: GrokpotDatabase? = null

        fun getDatabase(context: Context): GrokpotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GrokpotDatabase::class.java,
                    "grokpot_garden.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
