//package com.example.emotionaldiary.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.emotionaldiary.R
//import com.example.emotionaldiary.auth.AuthManager
//import com.example.emotionaldiary.data.EmotionRepository
//import com.example.emotionaldiary.models.EmotionEntry
//import com.example.emotionaldiary.models.EmotionType
//import com.example.emotionaldiary.models.EmotionValues
//import java.text.SimpleDateFormat
//import java.util.*
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun EmotionEntryScreen(navController: NavController, dateMillis: Long) {
//    val context = LocalContext.current
//    val authManager = remember { AuthManager(context) }
//    val emotionRepository = remember { EmotionRepository(context) }
//    val scrollState = rememberScrollState()
//
//    // Преобразуем миллисекунды в дату
//    val date = remember { Date(dateMillis) }
//    val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("ru"))
//    val formattedDate = remember { dateFormat.format(date) }
//
//    // Состояние для полей ввода и слайдеров
//    var mainFeelings by remember { mutableStateOf("") }
//    var additionalComments by remember { mutableStateOf("") }
//
//    // Состояние для слайдеров эмоций
//    var generalFeeling by remember { mutableStateOf(5f) }
//    var expectation by remember { mutableStateOf(5f) }
//    var joy by remember { mutableStateOf(5f) }
//    var trust by remember { mutableStateOf(5f) }
//    var fear by remember { mutableStateOf(5f) }
//    var surprise by remember { mutableStateOf(5f) }
//    var sadness by remember { mutableStateOf(5f) }
//    var displeasure by remember { mutableStateOf(5f) }
//    var anger by remember { mutableStateOf(5f) }
//
//    // Загрузка существующей записи, если она есть
//    LaunchedEffect(key1 = date) {
//        val userId = authManager.getUserId() ?: return@LaunchedEffect
//        val entry = emotionRepository.getEntryForDate(userId, date)
//
//        if (entry != null) {
//            mainFeelings = entry.overallFeeling
//            additionalComments = entry.comments
//
//            // Загрузка значений эмоций
//            generalFeeling = entry.emotionValues.generalFeeling.toFloat()
//            expectation = entry.emotionValues.expectation.toFloat()
//            joy = entry.emotionValues.joy.toFloat()
//            trust = entry.emotionValues.trust.toFloat()
//            fear = entry.emotionValues.fear.toFloat()
//            surprise = entry.emotionValues.surprise.toFloat()
//            sadness = entry.emotionValues.sadness.toFloat()
//            displeasure = entry.emotionValues.displeasure.toFloat()
//            anger = entry.emotionValues.anger.toFloat()
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Запись эмоций") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = {
//                        // Сохранение записи
//                        val userId = authManager.getUserId()
//                        if (userId != null) {
//                            val emotionValues = EmotionValues(
//                                generalFeeling = generalFeeling.toInt(),
//                                expectation = expectation.toInt(),
//                                joy = joy.toInt(),
//                                trust = trust.toInt(),
//                                fear = fear.toInt(),
//                                surprise = surprise.toInt(),
//                                sadness = sadness.toInt(),
//                                displeasure = displeasure.toInt(),
//                                anger = anger.toInt()
//                            )
//
//                            val entry = EmotionEntry(
//                                userId = userId,
//                                date = date,
//                                emotionType = EmotionType.NEUTRAL, // Определяем по преобладающей эмоции
//                                overallFeeling = mainFeelings,
//                                comments = additionalComments,
//                                emotionValues = emotionValues
//                            )
//
//                            emotionRepository.saveEntry(entry)
//                            navController.popBackStack()
//                        }
//                    }) {
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color(0xFF8AB58A),
//                    titleContentColor = Color.White,
//                    navigationIconContentColor = Color.White,
//                    actionIconContentColor = Color.White
//                )
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .background(Color(0xFFF5F5F5))
//                .padding(16.dp)
//                .verticalScroll(scrollState)
//        ) {
//            // Дата
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(16.dp),
//                colors = CardDefaults.cardColors(containerColor = Color(0xFF8AB58A))
//            ) {
//                Text(
//                    text = formattedDate,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.White,
//                    modifier = Modifier.padding(16.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Поля для ввода текста
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(16.dp),
//                colors = CardDefaults.cardColors(containerColor = Color(0xFFA6C5A6))
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    // Основные чувства
//                    Text(
//                        text = "Основные чувства за день:",
//                        fontSize = 16.sp,
//                        color = Color.White,
//                        modifier = Modifier.padding(bottom = 8.dp)
//                    )
//
//                    TextField(
//                        value = mainFeelings,
//                        onValueChange = { mainFeelings = it },
//                        placeholder = { Text("Опишите ваши основные чувства...") },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(120.dp),
//                        colors = TextFieldDefaults.colors(
//                            focusedContainerColor = Color.White,
//                            unfocusedContainerColor = Color.White,
//                            focusedTextColor = Color(0xFF333333),
//                            unfocusedTextColor = Color(0xFF333333)
//                        )
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    // Дополнительные комментарии
//                    Text(
//                        text = "Дополнительные комментарии:",
//                        fontSize = 16.sp,
//                        color = Color.White,
//                        modifier = Modifier.padding(bottom = 8.dp)
//                    )
//
//                    TextField(
//                        value = additionalComments,
//                        onValueChange = { additionalComments = it },
//                        placeholder = { Text("Добавьте комментарии к вашим эмоциям...") },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(120.dp),
//                        colors = TextFieldDefaults.colors(
//                            focusedContainerColor = Color.White,
//                            unfocusedContainerColor = Color.White,
//                            focusedTextColor = Color(0xFF333333),
//                            unfocusedTextColor = Color(0xFF333333)
//                        )
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Слайдеры эмоций
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(16.dp),
//                colors = CardDefaults.cardColors(containerColor = Color(0xFFA6C5A6))
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    Text(
//                        text = "Эмоциональная шкала",
//                        fontSize = 16.sp,
//                        color = Color.White,
//                        modifier = Modifier.padding(bottom = 16.dp)
//                    )
//
//                    // Общее самочувствие
//                    Text(
//                        text = "Общее самочувствие",
//                        fontSize = 14.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF333333),
//                        modifier = Modifier.padding(bottom = 8.dp)
//                    )
//
//                    // Слайдер для общего самочувствия
//                    EmotionSliderWithValue(
//                        value = generalFeeling,
//                        onValueChange = { generalFeeling = it }
//                    )
//
//                    Divider(
//                        modifier = Modifier.padding(vertical = 8.dp),
//                        color = Color(0xFFD1E7D1)
//                    )
//
//                    // Эмоции первого типа
//                    Text(
//                        text = "Эмоции",
//                        fontSize = 14.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF333333),
//                        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
//                    )
//
//                    // Слайдеры для каждой эмоции
//                    EmotionSliderWithLabel(
//                        label = "Ожидание",
//                        value = expectation,
//                        onValueChange = { expectation = it }
//                    )
//
//                    EmotionSliderWithLabel(
//                        label = "Радость",
//                        value = joy,
//                        onValueChange = { joy = it }
//                    )
//
//                    EmotionSliderWithLabel(
//                        label = "Доверие",
//                        value = trust,
//                        onValueChange = { trust = it }
//                    )
//
//                    EmotionSliderWithLabel(
//                        label = "Страх",
//                        value = fear,
//                        onValueChange = { fear = it }
//                    )
//
//                    EmotionSliderWithLabel(
//                        label = "Удивление",
//                        value = surprise,
//                        onValueChange = { surprise = it }
//                    )
//
//                    EmotionSliderWithLabel(
//                        label = "Грусть",
//                        value = sadness,
//                        onValueChange = { sadness = it }
//                    )
//
//                    EmotionSliderWithLabel(
//                        label = "Неудовольствие",
//                        value = displeasure,
//                        onValueChange = { displeasure = it }
//                    )
//
//                    EmotionSliderWithLabel(
//                        label = "Гнев",
//                        value = anger,
//                        onValueChange = { anger = it }
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//        }
//    }
//}
//
//@Composable
//fun EmotionSliderWithValue(
//    value: Float,
//    onValueChange: (Float) -> Unit
//) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = "0",
//            fontSize = 12.sp,
//            color = Color(0xFF333333)
//        )
//
//        Slider(
//            value = value,
//            onValueChange = onValueChange,
//            valueRange = 0f..10f,
//            steps = 9,
//            modifier = Modifier.weight(1f),
//            colors = SliderDefaults.colors(
//                thumbColor = Color(0xFF8AB58A),
//                activeTrackColor = Color(0xFF8AB58A)
//            )
//        )
//
//        Text(
//            text = "10",
//            fontSize = 12.sp,
//            color = Color(0xFF333333)
//        )
//
//        Text(
//            text = value.toInt().toString(),
//            fontSize = 14.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color(0xFF333333),
//            modifier = Modifier.padding(start = 8.dp, end = 4.dp)
//        )
//    }
//}
//
//@Composable
//fun EmotionSliderWithLabel(
//    label: String,
//    value: Float,
//    onValueChange: (Float) -> Unit
//) {
//    Column(
//        modifier = Modifier.padding(vertical = 4.dp)
//    ) {
//        Text(
//            text = label,
//            fontSize = 14.sp,
//            color = Color(0xFF333333),
//            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
//        )
//
//        EmotionSliderWithValue(
//            value = value,
//            onValueChange = onValueChange
//        )
//
//        Divider(
//            modifier = Modifier.padding(top = 8.dp),
//            color = Color(0xFFD1E7D1)
//        )
//    }
//}