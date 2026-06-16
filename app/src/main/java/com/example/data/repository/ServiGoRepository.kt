package com.example.data.repository

import com.example.data.local.ServiGoDao
import com.example.model.BookingEntity
import com.example.model.ServiceEntity
import kotlinx.coroutines.flow.Flow

class ServiGoRepository(private val dao: ServiGoDao) {
    val allServices: Flow<List<ServiceEntity>> = dao.getAllServices()
    val allBookings: Flow<List<BookingEntity>> = dao.getAllBookings()
    val activeBookings: Flow<List<BookingEntity>> = dao.getActiveBookings()

    fun getServicesByCategory(category: String): Flow<List<ServiceEntity>> = dao.getServicesByCategory(category)
    suspend fun getServiceById(id: String): ServiceEntity? = dao.getServiceById(id)

    suspend fun insertServices(services: List<ServiceEntity>) = dao.insertServices(services)

    suspend fun insertBooking(booking: BookingEntity) = dao.insertBooking(booking)
    suspend fun updateBooking(booking: BookingEntity) = dao.updateBooking(booking)
    suspend fun deleteBookingById(id: String) = dao.deleteBookingById(id)
}
