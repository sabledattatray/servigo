package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ServiceEntity
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingProcessScreen(
    serviceId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onBookingConfirm: () -> Unit
) {
    val services by viewModel.services.collectAsStateWithLifecycle()
    val service = services.find { it.id == serviceId }
    
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf(System.currentTimeMillis() + 3600000) } // +1 hr dummy
    var selectedTimeSlot by remember { mutableStateOf("ASAP") }
    
    val problemTypes = listOf("Not Working", "Making Noise", "Installation", "Other")
    var selectedProblemType by remember { mutableStateOf("") }

    if (service != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Book Service") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            bottomBar = {
                Button(
                    onClick = {
                        viewModel.bookNewService(service, selectedTime, address.ifBlank { "Home Address" }, description)
                        onBookingConfirm()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = androidx.compose.foundation.shape.CircleShape
                ) {
                    Text("Confirm Booking - ₹${service.basePrice}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${service.category} • ⭐ ${service.rating}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Select Problem Type", fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    problemTypes.forEach { type ->
                        FilterChip(
                            selected = selectedProblemType == type,
                            onClick = { selectedProblemType = type },
                            label = { Text(type) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                Text("Add Photos (Optional)", fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add Photo", tint = MaterialTheme.colorScheme.primary)
                        Text("Upload Image", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Select Time Slot", fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ASAP", "Today, 4 PM", "Tomorrow, 10 AM").forEach { slot ->
                        FilterChip(
                            selected = selectedTimeSlot == slot,
                            onClick = { selectedTimeSlot = slot },
                            label = { Text(slot) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Service Address", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    placeholder = { Text("House No, Building, Street") },
                    shape = RoundedCornerShape(24.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Issue Description (Optional)", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp).padding(top = 8.dp),
                    placeholder = { Text("E.g. AC is leaking water from the front panel") },
                    shape = RoundedCornerShape(24.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Payment Summary", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Service Base Price", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹${service.basePrice}", fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Taxes & Fee", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹49", fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", fontWeight = FontWeight.Bold)
                            Text("₹${(service.basePrice + 49)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(80.dp)) // Extra padding for bottom button
            }
        }
    }
}
