package com.example.smartcampuscompanion.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnouncementDao {

    @Query("SELECT * FROM announcements ORDER BY timestamp DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Transaction
    @Query("""
        SELECT a.*, (r.studentNumber IS NOT NULL) as isRead 
        FROM announcements a 
        LEFT JOIN announcement_read_status r ON a.id = r.announcementId AND r.studentNumber = :studentNumber 
        WHERE a.studentNumber IS NULL OR a.studentNumber = :studentNumber 
        ORDER BY a.timestamp DESC
    """)
    fun getAnnouncementsForStudent(studentNumber: String): Flow<List<AnnouncementWithReadStatus>>

    @Query("""
        SELECT COUNT(*) FROM announcements a 
        WHERE (a.studentNumber IS NULL OR a.studentNumber = :studentNumber) 
        AND a.id NOT IN (SELECT announcementId FROM announcement_read_status WHERE studentNumber = :studentNumber)
    """)
    fun getUnreadCount(studentNumber: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(announcement: Announcement)

    @Update
    suspend fun update(announcement: Announcement)

    @Delete
    suspend fun delete(announcement: Announcement)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun markAsRead(status: AnnouncementReadStatus)

    @Query("DELETE FROM announcements")
    suspend fun deleteAll()
}

data class AnnouncementWithReadStatus(
    @Embedded val announcement: Announcement,
    val isRead: Boolean
)
