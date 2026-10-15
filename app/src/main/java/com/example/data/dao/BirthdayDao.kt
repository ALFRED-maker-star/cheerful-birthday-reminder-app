package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BirthdayContact
import kotlinx.coroutines.flow.Flow

@Dao
interface BirthdayDao {
  @Query("SELECT * FROM birthday_contacts ORDER BY name COLLATE NOCASE ASC")
  fun getAllBirthdays(): Flow<List<BirthdayContact>>

  @Query("SELECT * FROM birthday_contacts WHERE id = :id")
  suspend fun getBirthdayById(id: Long): BirthdayContact?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBirthday(contact: BirthdayContact): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(contacts: List<BirthdayContact>)

  @Update
  suspend fun updateBirthday(contact: BirthdayContact)

  @Delete
  suspend fun deleteBirthday(contact: BirthdayContact)

  @Query("DELETE FROM birthday_contacts WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("DELETE FROM birthday_contacts")
  suspend fun deleteAll()

  @Query("SELECT COUNT(*) FROM birthday_contacts")
  suspend fun getCount(): Int
}
