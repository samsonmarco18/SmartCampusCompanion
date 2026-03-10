package com.example.smartcampuscompanion.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE announcementId = :announcementId ORDER BY timestamp ASC")
    fun getCommentsForAnnouncement(announcementId: Int): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: Comment)

    @Update
    suspend fun update(comment: Comment)

    @Delete
    suspend fun delete(comment: Comment)

    @Query("UPDATE comments SET isReported = 1 WHERE id = :commentId")
    suspend fun reportComment(commentId: Int)
}
