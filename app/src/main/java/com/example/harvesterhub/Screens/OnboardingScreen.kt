package com.example.harvesterhub.Screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current
    val primaryColor = Color(0xFF4B5D2A)
    val firestore = FirebaseFirestore.getInstance()

    var userType by remember { mutableStateOf<String?>(null) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val farmerQuestions = listOf(
        "Do you own land?",
        "Do you use fertilizers?",
        "Do you want to sell your crops online?"
    )
    val merchantQuestions = listOf(
        "Do you buy from farmers?",
        "Do you have cold storage?",
        "Do you want to connect with farmers directly?"
    )
    val answers = remember { mutableStateListOf<Boolean?>() }

    fun saveAnswersToFirestore() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        val phone = user?.phoneNumber

        if (uid.isNullOrBlank()) {
            Toast.makeText(context, "User UID not found", Toast.LENGTH_SHORT).show()
            return
        }

        val questions = if (userType == "farmer") farmerQuestions else merchantQuestions
        val result = questions.mapIndexed { i, q ->
            q to (answers.getOrNull(i) ?: false)
        }.toMap()

        val data = hashMapOf(
            "userType" to userType,
            "interestSurvey" to result,
            "phoneNumber" to phone
        )

        firestore.collection("users")
            .document(uid)  // ✅ Use UID as document ID
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(context, "Responses saved!", Toast.LENGTH_SHORT).show()
                navController.navigate("dashboard") {
                    popUpTo("OnboardingScreen") { inclusive = true }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
            }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFDFBF7)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (userType == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "आपण कोण आहात?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                userType = "farmer"
                                answers.clear()
                                answers.addAll(List(farmerQuestions.size) { null })
                            },
                            colors = ButtonDefaults.buttonColors(primaryColor),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("शेतकरी", color = Color.White)
                        }
                        Button(
                            onClick = {
                                userType = "merchant"
                                answers.clear()
                                answers.addAll(List(merchantQuestions.size) { null })
                            },
                            colors = ButtonDefaults.buttonColors(primaryColor),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("व्यापारी", color = Color.White)
                        }
                    }
                }
            } else {
                val questionList = if (userType == "farmer") farmerQuestions else merchantQuestions
                val currentQuestion = questionList[currentQuestionIndex]

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        currentQuestion,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Button(
                            onClick = {
                                answers[currentQuestionIndex] = true
                                if (currentQuestionIndex < questionList.lastIndex) {
                                    currentQuestionIndex++
                                } else {
                                    saveAnswersToFirestore()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(primaryColor),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("होय", color = Color.White)
                        }

                        Button(
                            onClick = {
                                answers[currentQuestionIndex] = false
                                if (currentQuestionIndex < questionList.lastIndex) {
                                    currentQuestionIndex++
                                } else {
                                    saveAnswersToFirestore()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(Color.Gray),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("नाही", color = Color.White)
                        }
                    }

                    if (currentQuestionIndex > 0) {
                        Spacer(modifier = Modifier.height(24.dp))
                        TextButton(onClick = {
                            currentQuestionIndex--
                        }) {
                            Text("पुर्वीचे प्रश्न", color = primaryColor)
                        }
                    }
                }
            }
        }
    }
}
