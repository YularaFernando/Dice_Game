package com.example.mobilecoursework

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    var isAiRolling by remember { mutableStateOf(false) }

    var roundPlayerTotal by remember { mutableStateOf(0) }
    var roundAiTotal by remember { mutableStateOf(0) }

    var selectedDice by remember { mutableStateOf(MutableList(5) { false }) }
    var rollCount by remember { mutableStateOf(1) }

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

    // AI roll logic
    LaunchedEffect(isAiRolling) {
        if (isAiRolling) {
            delay(800)
            aiRolls = List(5) { (1..6).random() }
            roundAiTotal = aiRolls.sum()
            aiTotal += roundAiTotal

            if (playerTotal >= 101 || aiTotal >= 101) {
                gameEnded = true
                playerWon = playerTotal >= 101 && playerTotal > aiTotal
            }

            isAiRolling = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

            if (!gameEnded) {
                // === AI Section ===
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("AI", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
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

                    Text("This Round's Total is $roundAiTotal", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Ai's Full Score is $aiTotal", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(60.dp))

                // === Player Section ===
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("You", fontSize = 30.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, color = Color.White)
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        playerImages.forEachIndexed { index, image ->
                            Box(
                                modifier = Modifier
                                    .size(69.dp)
                                    .clickable(enabled = rollCount < 3) {
                                        selectedDice[index] = !selectedDice[index]
                                    }
                            ) {
                                Image(
                                    painter = painterResource(id = image),
                                    contentDescription = "Player Dice",
                                    modifier = Modifier.matchParentSize()
                                )
                                if (selectedDice[index]) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(Color(0x88000000))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(25.dp))
                    Text("This Round's Total is $roundPlayerTotal", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Your Full Score is $playerTotal", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (rollCount == 1) {
                                // First roll: roll all
                                playerRolls = List(5) { (1..6).random() }
                            } else {
                                // Reroll selected
                                playerRolls = playerRolls.mapIndexed { i, oldValue ->
                                    if (selectedDice[i]) (1..6).random() else oldValue
                                }
                            }

                            selectedDice = MutableList(5) { false }
                            roundPlayerTotal = playerRolls.sum()
                            rollCount++

                            if (rollCount > 3) {
                                // End of player turn
                                playerTotal += roundPlayerTotal
                                if (playerTotal >= 101) {
                                    gameEnded = true
                                    playerWon = true
                                } else {
                                    isAiRolling = true
                                }
                                rollCount = 1
                            }
                        },
                        enabled = !isAiRolling && !gameEnded,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Roll", fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Reach the goal 101 first to be the winner !",
                    fontSize = 27.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            } else {
                WinOrLose(playerWon = playerWon)
            }

            Spacer(modifier = Modifier.weight(0.1f))

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
                Text("Back", fontSize = 23.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
