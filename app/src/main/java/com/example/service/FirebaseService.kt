package com.example.service

import android.content.Context
import android.util.Log
import com.example.model.FieldReport
import com.example.model.User
import com.example.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseService {
    private const val TAG = "FirebaseService"

    // Safe getters that catch uninitialized Firebase app in preview or unit testing
    private val auth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Auth not initialized: ${e.message}")
            null
        }

    private val firestore: FirebaseFirestore?
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore not initialized: ${e.message}")
            null
        }

    suspend fun saveUserToFirestore(user: User) {
        val db = firestore ?: return
        try {
            val userMap = hashMapOf(
                "id" to user.id,
                "name" to user.name,
                "email" to user.email,
                "role" to user.role.name,
                "department" to user.department,
                "lastLogin" to System.currentTimeMillis()
            )
            db.collection("users").document(user.id).set(userMap).await()
            Log.d(TAG, "User ${user.id} saved to Firestore successfully")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to save user to Firestore: ${e.message}")
        }
    }

    suspend fun persistFieldReportToFirestore(report: FieldReport) {
        val db = firestore ?: return
        try {
            val reportMap = hashMapOf(
                "id" to report.id,
                "type" to report.type.name,
                "locationName" to report.locationName,
                "latitude" to report.coordinates.latitude,
                "longitude" to report.coordinates.longitude,
                "description" to report.description,
                "severity" to report.severity.name,
                "officerName" to report.officerName,
                "timestamp" to report.timestamp,
                "hasPhoto" to report.hasPhoto,
                "serverSyncTime" to System.currentTimeMillis()
            )
            db.collection("field_reports").document(report.id).set(reportMap).await()
            Log.d(TAG, "Field report ${report.id} persisted to Firestore")
        } catch (e: Exception) {
            Log.w(TAG, "Firestore sync skipped: ${e.message}")
        }
    }

    fun getCurrentFirebaseUser(): User? {
        val fbUser = auth?.currentUser ?: return null
        return User(
            id = fbUser.uid,
            name = fbUser.displayName ?: "Authenticated Officer",
            email = fbUser.email ?: "officer@nerlogix.gov.in",
            role = UserRole.LOGISTICS_OPERATOR,
            department = "Regional Transit Operations"
        )
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "Sign out error: ${e.message}")
        }
    }
}
