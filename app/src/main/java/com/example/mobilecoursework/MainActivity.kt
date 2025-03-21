package com.example.mobilecoursework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilecoursework.ui.theme.MobileCourseworkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileCourseworkTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    // State to control dialog visibility and navigation
    var showDialog by remember { mutableStateOf(false) }
    var showGameScreen by remember { mutableStateOf(false) }

    // If showGameScreen is true, display the DiceGameScreen
    if (showGameScreen) {
        DiceGameUI(onBackClick = { showGameScreen = false } ,
        onGameEnd = { })
    // Pass callback to go back
    } else {
        // Main UI
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Spacer(modifier = Modifier.height(0.dp))
                // New Game Button
                Button(
                    onClick = { showGameScreen = true },  // Navigate to the DiceGameScreen
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, // transparent background
                        contentColor = Color.White          // text color
                    ),
                ) {
                    Text(
                        text = "New Game",
                        fontSize = 23.sp,
                        fontFamily = FontFamily.Serif,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(35.dp))

                // About Button
                Button(
                    onClick = { showDialog = true },  // Show the dialog
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, // transparent background
                        contentColor = Color.White          // text color
                    ),
                ) {
                    Text(
                        text = "About",
                        fontSize = 23.sp,
                        fontFamily = FontFamily.Serif,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Show the About Dialog if showDialog is true
            if (showDialog) {
                AboutDialog(onDismiss = { showDialog = false })
            }
        }
    }
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "About") },
        text = {
            Text(
                text = """
                Author: T. Yulara Fernando
                Student ID: w2084415
                  
                I confirm that I understand what plagiarism is and have read and understood the section on 
                Assessment Offences in the Essential Information for Students. The work that I have submitted 
                is entirely my own. Any work from other authors is duly referenced and acknowledged.
            """.trimIndent()
            )
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("OK")
            }
        }
    )
}

