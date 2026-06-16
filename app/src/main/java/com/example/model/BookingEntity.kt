package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val serviceId: String,
    val serviceName: String,
    val status: String = "pending", // pending, assigned, in_progress, completed, cancelled
    val technicianName: String = "Assigning...",
    val price: Int,
    val scheduleTime: Long,
    val address: String = "Home",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
