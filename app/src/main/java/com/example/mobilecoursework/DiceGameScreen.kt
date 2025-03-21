package com.example.mobilecoursework

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay

@Composable
fun DiceGameUI(onBackClick: () -> Unit, onGameEnd: (Boolean) -> Unit) {
    var playerRolls by remember { mutableStateOf(List(5) { (1..6).random() }) }
    var aiRolls by remember { mutableStateOf(List(5) { (1..6).random() }) }
    var playerTotal by remember { mutableStateOf(0) }
    var aiTotal by remember { mutableStateOf(0) }
    var gameEnded by remember { mutableStateOf(false) }
    var playerWon by remember { mutableStateOf(false) }
    var isAiRolling by remember { mutableStateOf(false) } // Track AI rolling state
    var roundPlayerTotal by remember { mutableStateOf(0) } // Track player's total for each round
    var roundAiTotal by remember { mutableStateOf(0) } // Track AI's total for each round

    val playerImages = playerRolls.map {
        when (it) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
    }

    val aiImages = aiRolls.map {
        when (it) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
    }

    // Logic for AI roll delay
    // AI Roll Logic
    LaunchedEffect(isAiRolling) {
        if (isAiRolling) {
            delay(800)  // Delay before AI rolls (0.8 seconds)
            aiRolls = List(5) { (1..6).random() }
            roundAiTotal = aiRolls.sum()
            aiTotal += roundAiTotal

            // Check if either the player or AI has reached or exceeded 101 points
            if (playerTotal >= 101 || aiTotal >= 101) {
                gameEnded = true
                // Determine the winner based on who has the higher score
                playerWon = playerTotal >= 101 && playerTotal > aiTotal
            }

            // Reset AI rolling state after AI completes its turn
            isAiRolling = false
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.wallpaper),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // Only display game UI (dice and scores) if game has not ended
            if (!gameEnded) {
                // === AI SIDE ===
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AI",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        aiImages.forEach { image ->
                            Image(
                                painter = painterResource(id = image),
                                contentDescription = "AI Dice",
                                modifier = Modifier.size(69.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "This Round's Total is $roundAiTotal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Ai's Full Score is $aiTotal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))

                // === PLAYER SIDE ===
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "You",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        playerImages.forEach { image ->
                            Image(
                                painter = painterResource(id = image),
                                contentDescription = "Player Dice",
                                modifier = Modifier.size(69.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(25.dp))

                    Text(
                        text = "This Round's Total is $roundPlayerTotal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Your Full Score is $playerTotal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Roll Button
                    Button(
                        onClick = {
                            playerRolls = List(5) { (1..6).random() }
                            roundPlayerTotal = playerRolls.sum()

                            playerTotal += roundPlayerTotal

                            if (playerTotal >= 101) {
                                gameEnded = true
                                playerWon = true
                            } else {
                                // After player rolls, trigger AI roll after a delay
                                isAiRolling = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Roll",
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Reach the goal 101 first to be the winner !",
                    fontSize = 27.sp,
                    //fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )

            } else {
                // Display results (win/loss/draw)
                WinOrLose(playerWon = playerWon)
            }

            Spacer(modifier = Modifier.weight(0.1f))
            // Back Button
            Button(
                onClick = { onBackClick() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Back",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
