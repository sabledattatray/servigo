package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val basePrice: Int,
    val imageUrl: String = "",
    val active: Boolean = true,
    val rating: Double = 4.5
)
