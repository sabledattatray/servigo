package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.model.BookingEntity
import com.example.model.ServiceEntity

@Database(entities = [ServiceEntity::class, BookingEntity::class], version = 1, exportSchema = false)
abstract class ServiGoDatabase : RoomDatabase() {
    abstract fun serviGoDao(): ServiGoDao
}
