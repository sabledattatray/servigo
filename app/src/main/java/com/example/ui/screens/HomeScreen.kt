package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.model.ServiceEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToCategory: (String) -> Unit
) {
    val services by viewModel.services.collectAsStateWithLifecycle()
    
    val categories = listOf(
        CategoryItem("Appliance", Icons.Default.Build, Color(0xFF3B82F6)),
        CategoryItem("Plumbing", Icons.Default.Plumbing, Color(0xFF10B981)),
        CategoryItem("Electrical", Icons.Default.ElectricalServices, Color(0xFFF59E0B)),
        CategoryItem("Cleaning", Icons.Default.CleaningServices, Color(0xFF8B5CF6))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Current Location", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("123 Main Street, City", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Describe your problem (e.g. AC not cooling, water leakage...)") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    shape = RoundedCornerShape(24.dp)
                )
            }

            item {
                BannerOffer()
            }

            item {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.height(110.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    userScrollEnabled = false
                ) {
                    items(categories) { category ->
                        CategoryCard(category = category) {
                            onNavigateToCategory(category.name)
                        }
                    }
                }
            }

            if (services.isNotEmpty()) {
                item {
                    Text(
                        text = "Emergency Services",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B6B),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EmergencyCard(title = "Plumbing Emergency", time = "Under 30 mins", modifier = Modifier.weight(1f))
                        EmergencyCard(title = "Electrical Short", time = "Under 30 mins", modifier = Modifier.weight(1f))
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Popular Services",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(services.take(4)) { service ->
                    PopularServiceItem(service = service) {
                        onNavigateToCategory(service.category)
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Top Rated Professionals Near You",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(2) {
                    TopRatedProCard()
                }
            }
        }
    }
}

@Composable
fun BannerOffer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(com.example.ui.theme.PoliSurfaceVariant)
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text = "Up to 50% Off",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "On all home cleaning services",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CategoryCard(category: CategoryItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(category.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = category.color,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PopularServiceItem(service: ServiceEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Starts at ₹${service.basePrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Surface(
                color = Color(0xFFFFB300).copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ ${service.rating}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFF59E0B),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

data class CategoryItem(val name: String, val icon: ImageVector, val color: Color)

@Composable
fun EmergencyCard(title: String, time: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
            Text(time, style = MaterialTheme.typography.labelSmall, color = Color(0xFFD32F2F))
        }
    }
}

@Composable
fun TopRatedProCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Rajesh Sharma", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Plumbing Expert • 1.2 km away", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐ 4.9 ", style = MaterialTheme.typography.bodySmall, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                    Text("(120 jobs)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            androidx.compose.material3.Button(onClick = { /* TODO */ }, shape = CircleShape) {
                Text("Book", fontSize = 12.sp)
            }
        }
    }
}
