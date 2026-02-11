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
                Student(id = 1, name = "Alice", email = "alice@student.university.edu", yearLevel = "4th Year", departmentName = "Computer Science"),
                Student(id = 2, name = "Bob", email = "bob@student.university.edu", yearLevel = "3rd Year", departmentName = "Computer Science"),
                Student(id = 3, name = "Charlie", email = "charlie@student.university.edu", yearLevel = "2nd Year", departmentName = "Computer Science"),
                Student(id = 4, name = "David", email = "david@student.university.edu", yearLevel = "4th Year", departmentName = "Electrical Engineering"),
                Student(id = 5, name = "Eve", email = "eve@student.university.edu", yearLevel = "3rd Year", departmentName = "Electrical Engineering"),
                Student(id = 6, name = "Frank", email = "frank@student.university.edu", yearLevel = "1st Year", departmentName = "Electrical Engineering"),
                Student(id = 7, name = "Grace", email = "grace@student.university.edu", yearLevel = "2nd Year", departmentName = "Mechanical Engineering"),
                Student(id = 8, name = "Heidi", email = "heidi@student.university.edu", yearLevel = "4th Year", departmentName = "Mechanical Engineering"),
                Student(id = 9, name = "Ivan", email = "ivan@student.university.edu", yearLevel = "3rd Year", departmentName = "Mechanical Engineering"),
                Student(id = 10, name = "Judy", email = "judy@student.university.edu", yearLevel = "1st Year", departmentName = "Civil Engineering"),
                Student(id = 11, name = "Mallory", email = "mallory@student.university.edu", yearLevel = "2nd Year", departmentName = "Civil Engineering"),
                Student(id = 12, name = "Trent", email = "trent@student.university.edu", yearLevel = "4th Year", departmentName = "Civil Engineering"),
                Student(id = 13, name = "Walter", email = "walter@student.university.edu", yearLevel = "3rd Year", departmentName = "Biology"),
                Student(id = 14, name = "Peggy", email = "peggy@student.university.edu", yearLevel = "2nd Year", departmentName = "Biology"),
                Student(id = 15, name = "Victor", email = "victor@student.university.edu", yearLevel = "1st Year", departmentName = "Biology"),
                Student(id = 16, name = "Xavier", email = "xavier@student.university.edu", yearLevel = "1st Year", departmentName = "Information Technology"),
                Student(id = 17, name = "Yara", email = "yara@student.university.edu", yearLevel = "2nd Year", departmentName = "Information Technology"),
                Student(id = 18, name = "Zane", email = "zane@student.university.edu", yearLevel = "3rd Year", departmentName = "Information Technology"),
                Student(id = 19, name = "Aaron", email = "aaron@student.university.edu", yearLevel = "4th Year", departmentName = "Chemical Engineering"),
                Student(id = 20, name = "Brian", email = "brian@student.university.edu", yearLevel = "2nd Year", departmentName = "Chemical Engineering"),
                Student(id = 21, name = "Chloe", email = "chloe@student.university.edu", yearLevel = "1st Year", departmentName = "Chemical Engineering"),
                Student(id = 22, name = "Diana", email = "diana@student.university.edu", yearLevel = "3rd Year", departmentName = "Physics"),
                Student(id = 23, name = "Ethan", email = "ethan@student.university.edu", yearLevel = "4th Year", departmentName = "Physics"),
                Student(id = 24, name = "Fiona", email = "fiona@student.university.edu", yearLevel = "2nd Year", departmentName = "Physics")
            )
            departmentDao.insertStudents(students)

            val grades = listOf(
                Grade(studentId = 1, subject = "Math", grade = 95.0),
                Grade(studentId = 1, subject = "Science", grade = 90.0),
                Grade(studentId = 2, subject = "Math", grade = 85.0),
                Grade(studentId = 2, subject = "Science", grade = 80.0),
                Grade(studentId = 3, subject = "Math", grade = 75.0),
                Grade(studentId = 3, subject = "Science", grade = 70.0),
            )
            departmentDao.insertGrades(grades)
        }
    }
}
