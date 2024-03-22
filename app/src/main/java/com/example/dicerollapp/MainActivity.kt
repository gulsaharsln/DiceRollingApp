package com.example.dicerollapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.dicerollapp.ui.theme.DiceRollAppTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceRollerApp()
        }
    }
}

@Composable
fun DiceRollerApp() {
    DiceRollAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DiceRollerScreen()
        }
    }
}

@Composable
fun MyText(text: String, fontSize: TextUnit, fontWeight: FontWeight, color: Color) {
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color
    )
}

@Composable
fun DiceRollerScreen() {
    var selectedDice by remember { mutableStateOf(setOf<Int>()) }
    var rollResults by remember { mutableStateOf(mapOf<Int, List<Int>>()) }
    val diceTypes = listOf(4, 6, 8, 10, 12, 20)
    var diceQuantities by remember { mutableStateOf(mutableStateMapOf<Int, Int>().apply { diceTypes.forEach { this[it] = 1 } }) }
    val onDiceSelected: (Int) -> Unit = { sides ->
        selectedDice = if (selectedDice.contains(sides)) {
            selectedDice - sides
        } else {
            selectedDice + sides
        }
    }


    val onRollResultsChanged: (Map<Int, List<Int>>) -> Unit = { newResults ->
        val newRollResults = mutableMapOf<Int, List<Int>>()
        for (dice in selectedDice) {
            newRollResults[dice] = List(diceQuantities[dice] ?: 1) {
                Random.nextInt(1, dice + 1)
            }
        }
        rollResults = newResults
    }
    Surface(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyText(
                text = "Select Dice to Roll",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(32.dp))

            DiceSelectionGrid(
                diceTypes = diceTypes,
                selectedDice = selectedDice,
                diceQuantities = diceQuantities,
                onDiceSelected = onDiceSelected
            )

            Spacer(modifier = Modifier.height(32.dp))

            RollButton(diceQuantities, selectedDice, onRollResultsChanged)
            ResultsDisplay(rollResults)
        }
    }
}

@Composable
fun RollButton(
    diceQuantities: Map<Int, Int>,
    selectedDice: Set<Int>,
    onRollResultsChanged: (Map<Int, List<Int>>) -> Unit
) {
    Button(
        onClick = {
            val newResults = mutableMapOf<Int, MutableList<Int>>()

            selectedDice.forEach { diceType ->
                val quantity = diceQuantities[diceType] ?: 1
                newResults[diceType] = MutableList(quantity) {
                    Random.nextInt(1, diceType + 1)
                }
            }

            onRollResultsChanged(newResults)
        },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = Color.Black
        )
    ) {
        Icon(Icons.Filled.Casino, contentDescription = "Roll", tint = Color.DarkGray)
        Spacer(modifier = Modifier.width(8.dp))
        MyText("Roll", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    }
}

@Composable
fun ResultsDisplay(rollResults: Map<Int, List<Int>>) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (rollResults.isNotEmpty()) {
                MyText(
                    text = "Results:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                val sortedResults = rollResults.toSortedMap().mapValues { entry ->
                    entry.value.sorted()
                }

                sortedResults.forEach { (diceType, resultsList) ->
                    MyText(
                        text = "D$diceType:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    resultsList.forEach { result ->
                        MyText(
                            text = "Rolled a $result",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                MyText(
                    text = "No rolls yet",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}



@Composable
fun DiceQuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier.padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { if (quantity > 0) onDecrease() },
            modifier = Modifier.size(60.dp)
        ) {
            Text("-", fontSize = 18.sp)
        }
        Text(
            text = quantity.toString(),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .width(IntrinsicSize.Min)
        )
        Button(
            onClick = onIncrease,
            modifier = Modifier.size(60.dp)
        ) {
            Text("+", fontSize = 18.sp)
        }
    }
}

@Composable
fun DiceSelectionGrid(
    diceTypes: List<Int>,
    selectedDice: Set<Int>,
    diceQuantities: MutableMap<Int, Int>,
    onDiceSelected: (Int) -> Unit
) {
    Column {
        diceTypes.forEach { diceType ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DiceButton(
                    sides = diceType,
                    isSelected = selectedDice.contains(diceType),
                    onSelectionChanged = { onDiceSelected(diceType) }
                )
                DiceQuantitySelector(
                    quantity = diceQuantities[diceType] ?: 1,
                    onIncrease = {
                        diceQuantities[diceType] = (diceQuantities[diceType] ?: 1) + 1
                    },
                    onDecrease = {
                        diceQuantities[diceType] = (diceQuantities[diceType]?.takeIf { it > 1 } ?: 1) - 1
                    }
                )
            }
        }
    }
}





@Composable
fun DiceButton(sides: Int, isSelected: Boolean, onSelectionChanged: () -> Unit) {

    val buttonSize = 100.dp
    val buttonShape = RoundedCornerShape(10)
    ButtonDefaults.buttonColors(
        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    )


    val buttonElevation = ButtonDefaults.elevatedButtonElevation(
        defaultElevation = 8.dp,
        pressedElevation = 12.dp,
        disabledElevation = 0.dp
    )

    Button(
        onClick = { onSelectionChanged() },
        modifier = Modifier
            .size(buttonSize)
            .padding(4.dp),
        shape = buttonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
            contentColor = Color.Black
        ),
        elevation = buttonElevation
    ) {
        Text("D$sides")
    }
}

@Preview(showBackground = true)
@Composable
fun DiceRollerAppPreview() {
    DiceRollerApp()
}
