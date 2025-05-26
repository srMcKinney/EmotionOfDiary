package com.example.emotionaldiary.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.emotionaldiary.R
import com.example.emotionaldiary.auth.AuthManager
import com.example.emotionaldiary.models.EmotionType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DiaryScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Карточка с датой
        DateCard()

        Spacer(modifier = Modifier.height(16.dp))

        // Карточка с записью
        EntryCard()

        Spacer(modifier = Modifier.height(16.dp))

        // Карточка с трендами
        TrendsCard()
    }
}

@Composable
fun DateCard() {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale("ru"))
    val formattedDate = dateFormat.format(calendar.time)
    val dayNumber = calendar.get(Calendar.DAY_OF_MONTH).toString()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF8AB58A))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayNumber,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = formattedDate,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun EntryCard() {
    var selectedEmotion by remember { mutableStateOf(EmotionType.NEUTRAL) }
    var feelingsText by remember { mutableStateOf("") }
    var commentsText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFA6C5A6))
    ) {
        Column {
            Text(
                text = stringResource(R.string.entry_for_today),
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )

            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                // Иконка эмоции
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFFD1E7D1)),
                    contentAlignment = Alignment.Center
                ) {
                    // Здесь будет иконка эмоции
                    Text(
                        text = selectedEmotion.name,
                        color = Color(0xFF333333)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Поле для ощущений
                    Text(
                        text = stringResource(R.string.overall_feelings),
                        fontSize = 14.sp,
                        color = Color(0xFF333333),
                        modifier = Modifier
                            .background(Color(0xFFD1E7D1))
                            .padding(8.dp)
                            .fillMaxWidth()
                    )

                    TextField(
                        value = feelingsText,
                        onValueChange = { feelingsText = it },
                        placeholder = { Text(stringResource(R.string.enter_feelings)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color(0xFF333333),
                            unfocusedTextColor = Color(0xFF333333)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Поле для комментариев
                    Text(
                        text = stringResource(R.string.comments),
                        fontSize = 14.sp,
                        color = Color(0xFF333333),
                        modifier = Modifier
                            .background(Color(0xFFD1E7D1))
                            .padding(8.dp)
                            .fillMaxWidth()
                    )

                    TextField(
                        value = commentsText,
                        onValueChange = { commentsText = it },
                        placeholder = { Text(stringResource(R.string.enter_comments)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color(0xFF333333),
                            unfocusedTextColor = Color(0xFF333333)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TrendsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFA6C5A6))
    ) {
        Column {
            Text(
                text = stringResource(R.string.trends),
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "График трендов будет здесь",
                    color = Color(0xFF333333)
                )
            }
        }
    }
}