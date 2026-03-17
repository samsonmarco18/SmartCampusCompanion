package com.example.smartcampuscompanion.data

import kotlinx.coroutines.flow.Flow

class AnnouncementRepository(private val announcementDao: AnnouncementDao, private val commentDao: CommentDao) {

    fun getAllAnnouncements(): Flow<List<Announcement>> = announcementDao.getAllAnnouncements()

    fun getAnnouncementsForStudent(studentNumber: String): Flow<List<Announcement>> = 
        announcementDao.getAnnouncementsForStudent(studentNumber)

    fun getUnreadCount(studentNumber: String): Flow<Int> = announcementDao.getUnreadCount(studentNumber)

    suspend fun insertAnnouncement(announcement: Announcement) = announcementDao.insert(announcement)

    suspend fun updateAnnouncement(announcement: Announcement) = announcementDao.update(announcement)

    suspend fun deleteAnnouncement(announcement: Announcement) = announcementDao.delete(announcement)

    suspend fun markAsRead(id: Int) = announcementDao.markAsRead(id)

    // Comment related methods
    fun getCommentsForAnnouncement(announcementId: Int): Flow<List<Comment>> = 
        commentDao.getCommentsForAnnouncement(announcementId)

    suspend fun insertComment(comment: Comment) = commentDao.insert(comment)

    suspend fun updateComment(comment: Comment) = commentDao.update(comment)

    suspend fun deleteComment(comment: Comment) = commentDao.delete(comment)

    suspend fun reportComment(commentId: Int) = commentDao.reportComment(commentId)
}
