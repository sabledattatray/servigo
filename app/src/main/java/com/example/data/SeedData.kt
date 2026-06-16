package com.example.data

import com.example.model.ServiceEntity
import java.util.UUID

object SeedData {
    val defaultServices = listOf(
        ServiceEntity(UUID.randomUUID().toString(), "AC Repair", "Appliance", 299),
        ServiceEntity(UUID.randomUUID().toString(), "Washing Machine Repair", "Appliance", 349),
        ServiceEntity(UUID.randomUUID().toString(), "Plumbing Works", "Plumbing", 199),
        ServiceEntity(UUID.randomUUID().toString(), "Tap Leakage Fix", "Plumbing", 99),
        ServiceEntity(UUID.randomUUID().toString(), "Electrical Wiring", "Electrical", 399),
        ServiceEntity(UUID.randomUUID().toString(), "Fan Repair", "Electrical", 149),
        ServiceEntity(UUID.randomUUID().toString(), "Full Home Cleaning", "Cleaning", 1299),
        ServiceEntity(UUID.randomUUID().toString(), "Sofa Cleaning", "Cleaning", 499)
    )
}
