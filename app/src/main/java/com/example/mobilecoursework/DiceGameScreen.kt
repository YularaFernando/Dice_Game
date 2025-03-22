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

    var rollCount by remember { mutableStateOf(0) } // Track the roll count (start at 0)
    var selectedDice by remember { mutableStateOf(MutableList(5) { false }) } // Track selected dice

    var isPlayerTurn by remember { mutableStateOf(true) }
    var gameEnded by remember { mutableStateOf(false) }
    var playerWon by remember { mutableStateOf(false) }

    // Keep track of scores for 1st and 2nd round
    var roundOneScore by remember { mutableStateOf(0) }
    var roundTwoScore by remember { mutableStateOf(0) }

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
            rollCount = 0  // Reset roll count after AI's turn
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
            Spacer(modifier = Modifier.height(90.dp))

            if (!gameEnded) {
                // === AI Display ===
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("AI", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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
                    Text("Round Total: $roundAiTotal", fontSize = 18.sp, color = Color.Black)
                    Text("AI Total Score: $aiTotal", fontSize = 18.sp, color = Color.Black)
                }

                Spacer(modifier = Modifier.height(50.dp))

                // === Player Display ===
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("You", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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
                    Text("Round Total: $roundPlayerTotal", fontSize = 18.sp, color = Color.Black)
                    Text("Your Total Score: $playerTotal", fontSize = 18.sp, color = Color.Black)

                    Spacer(modifier = Modifier.height(40.dp))

                    // === Roll Button ===
                    Button(
                        onClick = {
                            if (rollCount == 0) {
                                // First roll (roll all dice)
                                playerRolls = List(5) { (1..6).random() }
                            } else {
                                // Reroll unselected dice
                                playerRolls = playerRolls.mapIndexed { index, value ->
                                    if (selectedDice[index]) value else (1..6).random()
                                }
                            }
                            rollCount++  // Increment roll count after each roll
                            selectedDice = MutableList(5) { false }  // Reset selected dice after each roll

                            // If it's the 3rd roll, calculate the score
                            if (rollCount == 3) {
                                roundPlayerTotal = playerRolls.sum()
                                playerTotal += roundPlayerTotal

                                // Check for game end condition
                                if (playerTotal >= 101) {
                                    gameEnded = true
                                    playerWon = true
                                } else {
                                    isPlayerTurn = false
                                }
                            }
                        },
                        enabled = isPlayerTurn && rollCount < 3,  // Allow 3 rolls (1 initial + 2 rerolls)
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Roll Dice",
                        fontSize = 20.sp,
                            )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // === Score Button ===
                    Button(
                        onClick = {
                            // Save the current round score to the appropriate round
                            if (rollCount == 1) {
                                roundOneScore = roundPlayerTotal
                            } else if (rollCount == 2) {
                                roundTwoScore = roundPlayerTotal
                            }

                            // Stop rerolling and end the player's turn
                            rollCount = 3
                            selectedDice = MutableList(5) { false }

                            // After scoring, let the AI take its turn
                            isPlayerTurn = false
                        },
                        enabled = rollCount == 1 || rollCount == 2, // Allow score button only after 1st or 2nd roll
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Score",
                            fontSize = 18.sp,)
                    }
                }
            }

            // === Game Result ===
            if (gameEnded) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    //modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = if (playerWon) "You Win!" else "AI Wins!",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            // Restart the game
                            playerTotal = 0
                            aiTotal = 0
                            rollCount = 0
                            selectedDice = MutableList(5) { false }
                            gameEnded = false
                            isPlayerTurn = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Play Again",
                                fontSize = 20.sp,)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Back to Menu",
                            fontSize = 20.sp,)
                    }
                }
            }
        }
    }
}
