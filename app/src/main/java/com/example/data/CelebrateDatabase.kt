package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.BirthdayDao
import com.example.data.model.BirthdayContact

@Database(entities = [BirthdayContact::class], version = 1, exportSchema = false)
abstract class CelebrateDatabase : RoomDatabase() {
  abstract fun birthdayDao(): BirthdayDao

  companion object {
    @Volatile
    private var INSTANCE: CelebrateDatabase? = null

    fun getDatabase(context: Context): CelebrateDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          CelebrateDatabase::class.java,
          "celebrate_birthdays.db"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
