package com.example.smartcampuscompanion.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class CampusRepository(private val departmentDao: DepartmentDao) {

    private val firestore = FirebaseFirestore.getInstance()

    val departmentsWithStudents: Flow<List<DepartmentWithStudents>> = departmentDao.getDepartmentsWithStudents()

    suspend fun getDepartments(): List<Department> {
        return try {
            firestore.collection("departments")
                .get()
                .await()
                .toObjects(Department::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllStudents(): List<Student> {
        return try {
            firestore.collection("users")
                .whereEqualTo("role", "student")
                .get()
                .await()
                .toObjects(Student::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getStudentByStudentNumber(studentNumber: String): Student? {
        return try {
            val query = firestore.collection("users")
                .whereEqualTo("studentNumber", studentNumber)
                .get()
                .await()
            query.documents.firstOrNull()?.toObject(Student::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addStudent(student: Student) {
        try {
            firestore.collection("users").document(student.studentNumber).set(student).await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun checkAndPopulate() {
        try {
            val departmentsRef = firestore.collection("departments")
            val snapshot = departmentsRef.get().await()
            
            if (snapshot.isEmpty) {
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
                
                val batch = firestore.batch()
                departments.forEach { dept ->
                    val docRef = departmentsRef.document(dept.name)
                    batch.set(docRef, dept)
                }
                batch.commit().await()
                
                // Also populate Room
                departmentDao.insertDepartments(departments)
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
}
