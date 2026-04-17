package com.example.smartcampuscompanion.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.security.MessageDigest

class CampusRepository(private val departmentDao: DepartmentDao) {

    fun getDepartmentsWithStudents(): Flow<List<DepartmentWithStudents>> {
        return departmentDao.getDepartmentsWithStudents()
    }

    suspend fun getStudentByName(name: String): Student? {
        return departmentDao.getStudentByName(name)
    }

    suspend fun getStudentByStudentNumber(studentNumber: String): Student? {
        return departmentDao.getStudentByStudentNumber(studentNumber)
    }

    suspend fun addStudent(student: Student) {
        departmentDao.insertStudent(student)
    }

    suspend fun dropStudent(student: Student) {
        departmentDao.deleteStudent(student)
    }

    private fun hashPassword(password: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }

    suspend fun checkAndPopulate() {
        // This check ensures we only populate the database once.
        if (departmentDao.getDepartmentsWithStudents().first().isEmpty()) {
            val departments = listOf(
                Department("Computer Science", "cs.office@university.edu"),
                Department("Electrical Engineering", "ee.office@university.edu"),
                Department("Mechanical Engineering", "me.office@university.edu"),
                Department("Civil Engineering", "ce.office@university.edu"),
                Department("Biology", "bio.office@university.edu"),
                Department("Information Technology", "it.office@university.edu"),
                Department("Chemical Engineering", "che.office@university.edu"),
                Department("Physics", "phy.office@university.edu")
            )
            departmentDao.insertDepartments(departments)

            // Add an Admin account
            val adminPassword = hashPassword("admin123")
            val admin = Student(
                studentNumber = "ADMIN001",
                name = "Admin",
                password = adminPassword,
                email = "admin@university.edu",
                yearLevel = "N/A",
                departmentName = "Computer Science",
                role = "admin"
            )
            departmentDao.insertStudent(admin)
        }
    }
}
