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

    var roundPlayerTotal by remember { mutableStateOf(0) }
    var roundAiTotal by remember { mutableStateOf(0) }

    var rollCount by remember { mutableStateOf(1) }
    var selectedDice by remember { mutableStateOf(MutableList(5) { false }) }

    var isPlayerTurn by remember { mutableStateOf(true) }
    var gameEnded by remember { mutableStateOf(false) }
    var playerWon by remember { mutableStateOf(false) }

    // Automatically play AI's turn when it's their turn
    LaunchedEffect(isPlayerTurn) {
        if (!isPlayerTurn && !gameEnded) {
            delay(1000)
            var aiCurrentRolls = List(5) { (1..6).random() }
            var aiCurrentTotal = aiCurrentRolls.sum()

            // Simulate 2 rerolls (randomly deciding to reroll)
            repeat(2) {
                val indicesToReroll = List(5) { it }.shuffled().take((0..5).random())
                aiCurrentRolls = aiCurrentRolls.mapIndexed { index, value ->
                    if (index in indicesToReroll) (1..6).random() else value
                }
                aiCurrentTotal = aiCurrentRolls.sum()
            }

            aiRolls = aiCurrentRolls
            roundAiTotal = aiCurrentTotal
            aiTotal += aiCurrentTotal

            if (aiTotal >= 101 || playerTotal >= 101) {
                gameEnded = true
                playerWon = playerTotal >= 101 && playerTotal > aiTotal
            }

            isPlayerTurn = true
            rollCount = 1
            selectedDice = MutableList(5) { false }
        }
    }

    val diceImages = listOf(
        R.drawable.dice_1, R.drawable.dice_2, R.drawable.dice_3,
        R.drawable.dice_4, R.drawable.dice_5, R.drawable.dice_6
    )

    fun getDiceImage(value: Int) = diceImages[value - 1]

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
            Spacer(modifier = Modifier.height(40.dp))

            if (!gameEnded) {
                // === AI Display ===
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("AI", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        aiRolls.forEach {
                            Image(
                                painter = painterResource(id = getDiceImage(it)),
                                contentDescription = "AI Dice",
                                modifier = Modifier.size(65.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Round Total: $roundAiTotal", fontSize = 18.sp, color = Color.White)
                    Text("AI Total Score: $aiTotal", fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(30.dp))

                // === Player Display ===
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("You", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        playerRolls.forEachIndexed { index, value ->
                            Box(
                                modifier = Modifier
                                    .size(65.dp)
                                    .clickable(enabled = rollCount < 3 && isPlayerTurn) {
                                        // Toggle the selection of the clicked dice
                                        selectedDice[index] = !selectedDice[index]
                                    }
                            ) {
                                // Display the dice image
                                Image(
                                    painter = painterResource(id = getDiceImage(value)),
                                    contentDescription = "Player Dice",
                                    modifier = Modifier.fillMaxSize()
                                )
                                // If the dice is selected, add a visual indicator (e.g., semi-transparent overlay)
                                if (selectedDice[index]) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(Color(0x88000000))  // Semi-transparent black overlay
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    roundPlayerTotal = playerRolls.sum()
                    Text("Round Total: $roundPlayerTotal", fontSize = 18.sp, color = Color.White)
                    Text("Your Total Score: $playerTotal", fontSize = 18.sp, color = Color.White)

                    Spacer(modifier = Modifier.height(20.dp))

                    // === Roll Button ===
                    Button(
                        onClick = {
                            if (rollCount == 1) {
                                // First full roll (initial roll)
                                playerRolls = List(5) { (1..6).random() }
                            } else {
                                // Subsequent rounds (allow re-rolling selected dice or all dice)
                                playerRolls = playerRolls.mapIndexed { i, v ->
                                    if (selectedDice[i]) (1..6).random() else v
                                }
                            }
                            rollCount++  // Increment roll count after each roll
                            selectedDice = MutableList(5) { false }  // Reset selected dice after each roll
                        },
                        enabled = isPlayerTurn && rollCount < 3,  // Allow 3 rolls: 1 initial + 2 subsequent rerolls
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
                    ) {
                        Text("Roll (${rollCount}/3)", fontSize = 18.sp, fontWeight = FontWeight.Bold)  // Display the current roll count
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // === Keep Score Button ===
                    Button(
                        onClick = {
                            playerTotal += roundPlayerTotal
                            if (playerTotal >= 101 || aiTotal >= 101) {
                                gameEnded = true
                                playerWon = playerTotal >= 101 && playerTotal > aiTotal
                            } else {
                                isPlayerTurn = false
                            }
                            rollCount = 1
                            selectedDice = MutableList(5) { false }
                        },
                        enabled = isPlayerTurn,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White)
                    ) {
                        Text("Keep Score", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
                Text("Reach 101 to Win!", fontSize = 22.sp, fontFamily = FontFamily.Serif, color = Color.White)

            } else {
                WinOrLose(playerWon = playerWon, onBackClick = onBackClick)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .padding(bottom = 16.dp)
            ) {
                Text("Back", fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White)
            }
        }
    }
}
