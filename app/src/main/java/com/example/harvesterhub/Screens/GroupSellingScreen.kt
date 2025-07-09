package com.example.harvesterhub.Screens

import GroupViewModel
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.harvesterhub.data.models.GroupData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSellingScreen(vm: GroupViewModel = viewModel()) {
    val context = LocalContext.current
    val groups = vm.allActiveGroups  // Use allActiveGroups here

    var showDialog by remember { mutableStateOf(false) }
    var commodity by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group Trade") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "New Group")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(groups, key = { it.id }) { group ->
                GroupCard(
                    group = group,
                    onRequestJoin = {
                        vm.requestToJoin(group.id) { success ->
                            Toast.makeText(
                                context,
                                if (success) "Requested to join" else "Request failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        }
    }

    if (showDialog) {
        CreateGroupDialog(
            commodity = commodity,
            quantity = quantity,
            location = location,
            onCommodityChange = { commodity = it },
            onQuantityChange = { if (it.all(Char::isDigit)) quantity = it },
            onLocationChange = { location = it },
            onDismiss = { showDialog = false },
            onConfirm = {
                vm.createGroup(commodity, quantity.toIntOrNull() ?: 0, location) { success ->
                    Toast.makeText(
                        context,
                        if (success) "Group created" else "Create failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                showDialog = false
                commodity = ""; quantity = ""; location = ""
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupDialog(
    commodity: String,
    quantity: String,
    location: String,
    onCommodityChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Group") },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = commodity,
                    onValueChange = onCommodityChange,
                    label = { Text("Commodity (e.g., Wheat)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = onQuantityChange,
                    label = { Text("Quantity (kg)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = onLocationChange,
                    label = { Text("Location (e.g., Pune)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun GroupCard(
    group: GroupData,
    onRequestJoin: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(group.name, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text("By ${group.leaderName}")
            Text("${group.commodity}, ${group.quantity}kg")
            Text("Location: ${group.location}")
//            Text("Members: ${group.members}")
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onRequestJoin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Request to Join")
            }
        }
    }
}