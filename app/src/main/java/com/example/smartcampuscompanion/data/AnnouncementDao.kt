package com.example.smartcampuscompanion.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY timestamp DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Insert
    suspend fun insert(announcement: Announcement)

    @Query("DELETE FROM announcements")
    suspend fun deleteAll()
}
