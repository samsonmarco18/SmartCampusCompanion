package com.example.smartcampuscompanion.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class CampusRepository(private val departmentDao: DepartmentDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val departmentsWithStudents: Flow<List<DepartmentWithStudents>> = departmentDao.getDepartmentsWithStudents()

    suspend fun getDepartments(): List<Department> {
        return try {
            val snapshot = firestore.collection("departments")
                .get()
                .await()
            val list = snapshot.toObjects(Department::class.java)
            if (list.isNotEmpty()) {
                departmentDao.insertDepartments(list)
            }
            list
        } catch (e: Exception) {
            departmentDao.getAllDepartmentsSync()
        }
    }

    suspend fun getAllStudents(): List<Student> {
        return try {
            firestore.collection("users")
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
            // 1. Create/Verify Default Admin
            val adminEmail = "admin@smartcampus.com"
            val adminPassword = "adminpassword123"
            
            var adminUid: String? = null
            
            try {
                // Try to sign in to see if it exists
                val result = auth.signInWithEmailAndPassword(adminEmail, adminPassword).await()
                adminUid = result.user?.uid
            } catch (e: Exception) {
                // If fails, try to create
                try {
                    val result = auth.createUserWithEmailAndPassword(adminEmail, adminPassword).await()
                    adminUid = result.user?.uid
                } catch (ce: Exception) {
                    // Log error if needed
                }
            }

            // If we have a UID, ensure the Firestore document exists
            adminUid?.let { uid ->
                val userDoc = firestore.collection("users").document(uid).get().await()
                if (!userDoc.exists()) {
                    val adminUser = Student(
                        studentNumber = "ADMIN001",
                        name = "System Admin",
                        email = adminEmail,
                        role = "admin",
                        departmentName = "Administration"
                    )
                    firestore.collection("users").document(uid).set(adminUser).await()
                }
            }

            // 2. Populate Departments
            val departmentsRef = firestore.collection("departments")
            val snapshot = departmentsRef.get().await()
            
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

            if (snapshot.isEmpty) {
                val batch = firestore.batch()
                departments.forEach { dept ->
                    val docRef = departmentsRef.document(dept.name)
                    batch.set(docRef, dept)
                }
                batch.commit().await()
            }
            
            // Sync with local Room DB
            departmentDao.insertDepartments(departments)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
