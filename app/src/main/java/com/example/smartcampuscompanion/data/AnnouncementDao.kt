package com.example.smartcampuscompanion.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnouncementDao {

    @Query("SELECT * FROM announcements ORDER BY timestamp DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Transaction
    @Query("SELECT * FROM announcements WHERE studentNumber IS NULL OR studentNumber = :studentNumber ORDER BY timestamp DESC")
    fun getAnnouncementsForStudent(studentNumber: String): Flow<List<Announcement>>

    @Query("SELECT COUNT(*) FROM announcements WHERE isRead = 0 AND (studentNumber IS NULL OR studentNumber = :studentNumber)")
    fun getUnreadCount(studentNumber: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(announcement: Announcement)

    @Update
    suspend fun update(announcement: Announcement)

    @Delete
    suspend fun delete(announcement: Announcement)

    @Query("UPDATE announcements SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)

    @Query("DELETE FROM announcements")
    suspend fun deleteAll()
}
