package com.example.mobilecoursework

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WinOrLose(playerWon: Boolean, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (playerWon) {
                Text(
                    text = "You win",
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Green,
                    fontFamily = FontFamily.Serif
                )
            } else {
                Text(
                    text = "You Lost",
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    fontFamily = FontFamily.Serif
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // Push the button to the bottom

            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth(0.4f)
            ) {
                Text("Back", fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White)
            }
        }
    }
}
