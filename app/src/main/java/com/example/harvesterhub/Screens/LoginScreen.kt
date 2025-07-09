package com.example.harvesterhub.Screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.harvesterhub.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun LoginScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val activity = context as Activity

    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var codeSent by remember { mutableStateOf(false) }

    var timerSeconds by remember { mutableStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    val countryCodes = listOf("+91")
    var selectedCountryCode by remember { mutableStateOf("+91") }
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFDFBF7)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.farmer_icon2),
                contentDescription = "Farmer Icon",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "LOGIN",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B5D2A)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.width(100.dp)) {
                    OutlinedTextField(
                        value = selectedCountryCode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Code") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        countryCodes.forEach { code ->
                            DropdownMenuItem(
                                text = { Text(code) },
                                onClick = {
                                    selectedCountryCode = code
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.weight(1f)
                )
            }

            if (codeSent) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { otpCode = it },
                    label = { Text("Enter OTP") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isTimerRunning) {
                    Text("Resend OTP in $timerSeconds seconds", color = Color.Gray)
                } else {
                    Text(
                        text = "Didn't receive OTP? Resend",
                        color = Color.Blue,
                        modifier = Modifier.clickable {
                            val fullPhone = selectedCountryCode + phoneNumber.trim()

                            val options = PhoneAuthOptions.newBuilder(auth)
                                .setPhoneNumber(fullPhone)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(activity)
                                .setCallbacks(getCallbacks(auth, context, navController) {
                                    verificationId = it
                                    codeSent = true
                                    scope.startTimer(30) { remaining ->
                                        timerSeconds = remaining
                                        isTimerRunning = remaining > 0
                                    }
                                })
                                .build()

                            PhoneAuthProvider.verifyPhoneNumber(options)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (!codeSent) {
                        val fullPhone = selectedCountryCode + phoneNumber.trim()

                        if (!fullPhone.startsWith("+") || fullPhone.length < 10) {
                            Toast.makeText(context, "Enter valid phone number.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(fullPhone)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(activity)
                            .setCallbacks(getCallbacks(auth, context, navController) {
                                verificationId = it
                                codeSent = true
                                scope.startTimer(30) { remaining ->
                                    timerSeconds = remaining
                                    isTimerRunning = remaining > 0
                                }
                            })
                            .build()

                        PhoneAuthProvider.verifyPhoneNumber(options)

                    } else {
                        val verId = verificationId
                        if (!otpCode.isNullOrEmpty() && !verId.isNullOrEmpty()) {
                            val credential = PhoneAuthProvider.getCredential(verId, otpCode)
                            auth.signInWithCredential(credential)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val uid = auth.currentUser?.uid
                                        if (!uid.isNullOrEmpty()) {
                                            checkUserAndNavigate(uid, navController, context)
                                        }
                                    } else {
                                        Toast.makeText(context, "OTP Verification Failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF4B5D2A)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = if (!codeSent) "Send OTP" else "Verify OTP",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

fun CoroutineScope.startTimer(
    durationSeconds: Int = 30,
    onTick: (Int) -> Unit
) {
    this.launch {
        for (i in durationSeconds downTo 0) {
            onTick(i)
            delay(1000)
        }
    }
}

fun getCallbacks(
    auth: FirebaseAuth,
    context: android.content.Context,
    navController: NavController,
    onCodeSent: (String) -> Unit
): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
    return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        if (!uid.isNullOrEmpty()) {
                            checkUserAndNavigate(uid, navController, context)
                        }
                    } else {
                        Toast.makeText(context, "OTP Verification Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(context, "Verification Failed: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("Login", "Verification Failed", e)
        }

        override fun onCodeSent(verId: String, token: PhoneAuthProvider.ForceResendingToken) {
            onCodeSent(verId)
        }
    }
}

fun checkUserAndNavigate(
    uid: String,
    navController: NavController,
    context: android.content.Context
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users").document(uid).get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                navController.navigate("dashboard") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                navController.navigate("signup") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error checking user", Toast.LENGTH_SHORT).show()
        }
}
