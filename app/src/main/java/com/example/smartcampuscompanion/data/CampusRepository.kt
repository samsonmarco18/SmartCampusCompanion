package com.example.smartcampuscompanion.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CampusRepository(private val departmentDao: DepartmentDao) {

    fun getDepartmentsWithStudents(): Flow<List<DepartmentWithStudents>> {
        return departmentDao.getDepartmentsWithStudents()
    }

    suspend fun addStudent(student: Student) {
        departmentDao.insertStudent(student)
    }

    suspend fun dropStudent(student: Student) {
        departmentDao.deleteStudent(student)
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

            val students = listOf(
                Student(name = "Alice", email = "alice@student.university.edu", yearLevel = "4th Year", departmentName = "Computer Science"),
                Student(name = "Bob", email = "bob@student.university.edu", yearLevel = "3rd Year", departmentName = "Computer Science"),
                Student(name = "Charlie", email = "charlie@student.university.edu", yearLevel = "2nd Year", departmentName = "Computer Science"),
                Student(name = "David", email = "david@student.university.edu", yearLevel = "4th Year", departmentName = "Electrical Engineering"),
                Student(name = "Eve", email = "eve@student.university.edu", yearLevel = "3rd Year", departmentName = "Electrical Engineering"),
                Student(name = "Frank", email = "frank@student.university.edu", yearLevel = "1st Year", departmentName = "Electrical Engineering"),
                Student(name = "Grace", email = "grace@student.university.edu", yearLevel = "2nd Year", departmentName = "Mechanical Engineering"),
                Student(name = "Heidi", email = "heidi@student.university.edu", yearLevel = "4th Year", departmentName = "Mechanical Engineering"),
                Student(name = "Ivan", email = "ivan@student.university.edu", yearLevel = "3rd Year", departmentName = "Mechanical Engineering"),
                Student(name = "Judy", email = "judy@student.university.edu", yearLevel = "1st Year", departmentName = "Civil Engineering"),
                Student(name = "Mallory", email = "mallory@student.university.edu", yearLevel = "2nd Year", departmentName = "Civil Engineering"),
                Student(name = "Trent", email = "trent@student.university.edu", yearLevel = "4th Year", departmentName = "Civil Engineering"),
                Student(name = "Walter", email = "walter@student.university.edu", yearLevel = "3rd Year", departmentName = "Biology"),
                Student(name = "Peggy", email = "peggy@student.university.edu", yearLevel = "2nd Year", departmentName = "Biology"),
                Student(name = "Victor", email = "victor@student.university.edu", yearLevel = "1st Year", departmentName = "Biology"),
                Student(name = "Xavier", email = "xavier@student.university.edu", yearLevel = "1st Year", departmentName = "Information Technology"),
                Student(name = "Yara", email = "yara@student.university.edu", yearLevel = "2nd Year", departmentName = "Information Technology"),
                Student(name = "Zane", email = "zane@student.university.edu", yearLevel = "3rd Year", departmentName = "Information Technology"),
                Student(name = "Aaron", email = "aaron@student.university.edu", yearLevel = "4th Year", departmentName = "Chemical Engineering"),
                Student(name = "Brian", email = "brian@student.university.edu", yearLevel = "2nd Year", departmentName = "Chemical Engineering"),
                Student(name = "Chloe", email = "chloe@student.university.edu", yearLevel = "1st Year", departmentName = "Chemical Engineering"),
                Student(name = "Diana", email = "diana@student.university.edu", yearLevel = "3rd Year", departmentName = "Physics"),
                Student(name = "Ethan", email = "ethan@student.university.edu", yearLevel = "4th Year", departmentName = "Physics"),
                Student(name = "Fiona", email = "fiona@student.university.edu", yearLevel = "2nd Year", departmentName = "Physics")
            )
            departmentDao.insertStudents(students)
        }
    }
}
