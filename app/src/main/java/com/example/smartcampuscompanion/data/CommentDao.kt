package com.example.smartcampuscompanion.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE announcementId = :announcementId ORDER BY timestamp ASC")
    fun getCommentsForAnnouncement(announcementId: Int): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE isReported = 1")
    fun getReportedComments(): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: Comment)

    @Update
    suspend fun update(comment: Comment)

    @Delete
    suspend fun delete(comment: Comment)

    @Query("UPDATE comments SET isReported = 1, reportedBy = :reportedBy, reportReason = :reason WHERE id = :commentId")
    suspend fun reportComment(commentId: Int, reportedBy: String, reason: String)

    @Query("UPDATE comments SET isReported = 0, reportedBy = NULL, reportReason = NULL WHERE id = :commentId")
    suspend fun dismissReport(commentId: Int)
}
