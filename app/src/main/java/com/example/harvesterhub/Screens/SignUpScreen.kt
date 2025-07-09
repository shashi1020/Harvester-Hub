package com.example.harvesterhub.Screens

// Imports
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.harvesterhub.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var villageName by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("farmer") }
    val userTypes = listOf("farmer", "merchant")
    var expandedID by remember { mutableStateOf(false) }
    var expandedUser by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val phoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber ?: ""


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.merchant_icon),
                    contentDescription = "Merchant Icon",
                    modifier = Modifier.size(60.dp)
                )
            }

            Text(
                "REGISTER",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B5D2A)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {},
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expandedUser,
                onExpandedChange = { expandedUser = !expandedUser }
            ) {
                OutlinedTextField(
                    value = userType,
                    onValueChange = {},
                    label = { Text("User Type") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUser) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedUser,
                    onDismissRequest = { expandedUser = false }
                ) {
                    userTypes.forEach { type ->
                        DropdownMenuItem(text = { Text(type) }, onClick = {
                            userType = type
                            expandedUser = false
                        })
                    }
                }
            }

            OutlinedTextField(
                value = state,
                onValueChange = { state = it },
                label = { Text("State") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("District") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = villageName,
                onValueChange = { villageName = it },
                label = { Text("Village Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (fullName.isBlank() || phoneNumber.isBlank()) {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid == null) {
                        Toast.makeText(context, "Please verify OTP before submitting", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val userData = mapOf(
                        "fullName" to fullName,
                        "phoneNumber" to phoneNumber,
                        "state" to state,
                        "district" to district,
                        "villageName" to villageName,
                        "userType" to userType
                    )

                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").document(uid).set(userData)
                        .addOnSuccessListener {
                            Log.d("SignUpScreen", "User data saved successfully for uid: $uid")
                            Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate("OnboardingScreen")
                            {
                                popUpTo("SignUpScreen") {
                                    inclusive = true
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("SignUpScreen", "Failed to save user data", e)
                            Toast.makeText(context, "Failed to save data", Toast.LENGTH_SHORT).show()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF556B2F))
            ) {
                Text("Register", color = Color.White)
            }
        }
    }
}
