package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.SeedData
import com.example.data.repository.ServiGoRepository
import com.example.model.BookingEntity
import com.example.model.ServiceEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ServiGoRepository) : ViewModel() {

    val services: StateFlow<List<ServiceEntity>> = repository.allServices
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeBookings: StateFlow<List<BookingEntity>> = repository.activeBookings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    val allBookings: StateFlow<List<BookingEntity>> = repository.allBookings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Seed database if empty
        viewModelScope.launch {
            repository.allServices.collect { currentServices ->
                if (currentServices.isEmpty()) {
                    repository.insertServices(SeedData.defaultServices)
                }
            }
        }
    }

    fun getServicesByCategory(category: String): StateFlow<List<ServiceEntity>> {
        return repository.getServicesByCategory(category)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun bookNewService(service: ServiceEntity, scheduleTime: Long, address: String, description: String) {
        viewModelScope.launch {
            val booking = BookingEntity(
                serviceId = service.id,
                serviceName = service.name,
                price = service.basePrice,
                scheduleTime = scheduleTime,
                address = address,
                description = description
            )
            repository.insertBooking(booking)
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            repository.deleteBookingById(bookingId)
        }
    }
}

class MainViewModelFactory(private val repository: ServiGoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
