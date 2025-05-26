//package com.example.emotionaldiary.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.KeyboardArrowLeft
//import androidx.compose.material.icons.filled.KeyboardArrowRight
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.emotionaldiary.R
//import com.example.emotionaldiary.auth.AuthManager
//import com.example.emotionaldiary.data.EmotionRepository
//import com.example.emotionaldiary.models.EmotionEntry
//import com.example.emotionaldiary.models.EmotionValues
//import java.text.SimpleDateFormat
//import java.util.*
//
//@Composable
//fun CalendarScreen(navController: NavController) {
//    val scrollState = rememberScrollState()
//    val context = LocalContext.current
//    val authManager = remember { AuthManager(context) }
//    val emotionRepository = remember { EmotionRepository(context) }
//
//    // Получаем ID пользователя
//    val userId = authManager.getUserId()
//
//    // Состояние для выбранного месяца и года
//    val calendar = Calendar.getInstance()
//    var currentMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
//    var currentYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
//
//    // Загружаем записи для текущего месяца
//    val entries = remember(userId, currentMonth, currentYear) {
//        mutableStateOf<List<EmotionEntry>>(emptyList())
//    }
//
//    LaunchedEffect(userId, currentMonth, currentYear) {
//        if (userId != null) {
//            val startCalendar = Calendar.getInstance()
//            startCalendar.set(currentYear, currentMonth, 1, 0, 0, 0)
//            startCalendar.set(Calendar.MILLISECOND, 0)
//
//            val endCalendar = Calendar.getInstance()
//            endCalendar.set(currentYear, currentMonth, 1, 0, 0, 0)
//            endCalendar.set(Calendar.MILLISECOND, 0)
//            endCalendar.add(Calendar.MONTH, 1)
//
//            entries.value = emotionRepository.getEntriesBetweenDates(userId, startCalendar.time, endCalendar.time)
//        }
//    }
//
////    // Вычисляем средние значения эмоций для статистики
////    val emotionStats = remember(entries.value) {
////        if (entries.value.isEmpty()) {
////            EmotionStats()
////        } else {
////            val generalFeelingSum = entries.value.sumOf { it.emotionValues.generalFeeling }
////            val expectationSum = entries.value.sumOf { it.emotionValues.expectation }
////            val joySum = entries.value.sumOf { it.emotionValues.joy }
////            val trustSum = entries.value.sumOf { it.emotionValues.trust }
////            val fearSum = entries.value.sumOf { it.emotionValues.fear }
////            val surpriseSum = entries.value.sumOf { it.emotionValues.surprise }
////            val sadnessSum = entries.value.sumOf { it.emotionValues.sadness }
////            val displeasureSum = entries.value.sumOf { it.emotionValues.displeasure }
////            val angerSum = entries.value.sumOf { it.emotionValues.anger }
////
////            val count = entries.value.size.toFloat()
////
////            EmotionStats(
////                generalFeeling = generalFeelingSum / count,
////                expectation = expectationSum / count,
////                joy = joySum / count,
////                trust = trustSum / count,
////                fear = fearSum / count,
////                surprise = surpriseSum / count,
////                sadness = sadnessSum / count,
////                displeasure = displeasureSum / count,
////                anger = angerSum / count
////            )
////        }
////    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF5F5F5))
//            .padding(16.dp)
//            .verticalScroll(scrollState)
//    ) {
//        // Заголовок с датой - переименовано для устранения конфликта
//        CalendarDateCard()
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Календарь
//        CalendarCard(
//            currentMonth = currentMonth,
//            currentYear = currentYear,
//            entries = entries.value,
//            onMonthChanged = { newMonth, newYear ->
//                currentMonth = newMonth
//                currentYear = newYear
//            },
//            onDateSelected = { date ->
//                // Переход на экран записи с выбранной датой
//                navController.navigate("emotion_entry/${date.time}")
//            }
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Список записей
//        EntriesCard(
//            entries = entries.value,
//            onEntryClick = { entry ->
//                // Переход на экран редактирования записи
//                navController.navigate("emotion_entry/${entry.date.time}")
//            }
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Кнопка добавления записи
//        Button(
//            onClick = {
//                // Переход на экран создания записи с текущей датой
//                navController.navigate("emotion_entry/${System.currentTimeMillis()}")
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            shape = RoundedCornerShape(16.dp),
//            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8AB58A))
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Center,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp)
//            ) {
//                Text(
//                    text = stringResource(R.string.add_entry),
//                    fontSize = 16.sp,
//                    color = Color.White
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = null,
//                    tint = Color.White
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Статистика эмоций
//        EmotionStatsCard(stats = emotionStats)
//
//        Spacer(modifier = Modifier.height(24.dp))
//    }
//}
//
//@Composable
//fun CalendarDateCard() {
//    val calendar = Calendar.getInstance()
//    val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale("ru"))
//    val formattedDate = dateFormat.format(calendar.time)
//    val dayNumber = calendar.get(Calendar.DAY_OF_MONTH).toString()
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFF8AB58A))
//    ) {
//        Row(
//            modifier = Modifier.padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(48.dp)
//                    .background(Color.White, RoundedCornerShape(24.dp)),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = dayNumber,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFF333333)
//                )
//            }
//
//            Spacer(modifier = Modifier.width(16.dp))
//
//            Text(
//                text = formattedDate,
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.White,
//                modifier = Modifier.weight(1f)
//            )
//        }
//    }
//}
//
//@Composable
//fun CalendarCard(
//    currentMonth: Int,
//    currentYear: Int,
//    entries: List<EmotionEntry>,
//    onMonthChanged: (Int, Int) -> Unit,
//    onDateSelected: (Date) -> Unit
//) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFFA6C5A6))
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Text(
//                text = stringResource(R.string.calendar),
//                fontSize = 16.sp,
//                color = Color.White,
//                modifier = Modifier.padding(bottom = 16.dp)
//            )
//
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(8.dp),
//                colors = CardDefaults.cardColors(containerColor = Color.White)
//            ) {
//                Column(
//                    modifier = Modifier.padding(8.dp)
//                ) {
//                    // Заголовок месяца с навигацией
//                    MonthHeader(
//                        currentMonth = currentMonth,
//                        currentYear = currentYear,
//                        onPreviousMonth = {
//                            var newMonth = currentMonth - 1
//                            var newYear = currentYear
//                            if (newMonth < 0) {
//                                newMonth = 11
//                                newYear--
//                            }
//                            onMonthChanged(newMonth, newYear)
//                        },
//                        onNextMonth = {
//                            var newMonth = currentMonth + 1
//                            var newYear = currentYear
//                            if (newMonth > 11) {
//                                newMonth = 0
//                                newYear++
//                            }
//                            onMonthChanged(newMonth, newYear)
//                        }
//                    )
//
//                    // Дни недели
//                    WeekdaysHeader()
//
//                    // Сетка календаря
//                    CalendarGrid(
//                        currentMonth = currentMonth,
//                        currentYear = currentYear,
//                        entries = entries,
//                        onDateSelected = onDateSelected
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun MonthHeader(
//    currentMonth: Int,
//    currentYear: Int,
//    onPreviousMonth: () -> Unit,
//    onNextMonth: () -> Unit
//) {
//    val calendar = Calendar.getInstance()
//    calendar.set(Calendar.DAY_OF_MONTH, 1)
//    calendar.set(Calendar.MONTH, currentMonth)
//    calendar.set(Calendar.YEAR, currentYear)
//
//    val dateFormat = SimpleDateFormat("LLLL yyyy", Locale("ru"))
//    val monthYearText = dateFormat.format(calendar.time)
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = monthYearText.replaceFirstChar { it.uppercase() },
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color(0xFF333333),
//            modifier = Modifier.weight(1f)
//        )
//
//        IconButton(onClick = onPreviousMonth) {
//            Icon(
//                imageVector = Icons.Default.KeyboardArrowLeft,
//                contentDescription = stringResource(R.string.previous_month),
//                tint = Color(0xFF8AB58A)
//            )
//        }
//
//        IconButton(onClick = onNextMonth) {
//            Icon(
//                imageVector = Icons.Default.KeyboardArrowRight,
//                contentDescription = stringResource(R.string.next_month),
//                tint = Color(0xFF8AB58A)
//            )
//        }
//    }
//}
//
//@Composable
//fun WeekdaysHeader() {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//    ) {
//        val daysOfWeek = listOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС")
//        daysOfWeek.forEach { day ->
//            Text(
//                text = day,
//                fontSize = 12.sp,
//                color = Color(0xFF757575),
//                textAlign = TextAlign.Center,
//                modifier = Modifier.weight(1f)
//            )
//        }
//    }
//}
//
//@Composable
//fun CalendarGrid(
//    currentMonth: Int,
//    currentYear: Int,
//    entries: List<EmotionEntry>,
//    onDateSelected: (Date) -> Unit
//) {
//    val days = getDaysForCalendarGrid(currentYear, currentMonth)
//
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(7),
//        modifier = Modifier.height(240.dp),
//        contentPadding = PaddingValues(4.dp)
//    ) {
//        items(days) { day ->
//            val hasEntry = entries.any { entry ->
//                val entryCalendar = Calendar.getInstance()
//                entryCalendar.time = entry.date
//                entryCalendar.get(Calendar.DAY_OF_MONTH) == day.day &&
//                        entryCalendar.get(Calendar.MONTH) == day.month &&
//                        entryCalendar.get(Calendar.YEAR) == day.year
//            }
//
//            CalendarDay(
//                day = day,
//                isCurrentMonth = day.month == currentMonth,
//                isToday = isToday(day),
//                hasEntry = hasEntry,
//                onClick = {
//                    onDateSelected(day.date)
//                }
//            )
//        }
//    }
//}
//
//@Composable
//fun CalendarDay(
//    day: CalendarDay,
//    isCurrentMonth: Boolean,
//    isToday: Boolean,
//    hasEntry: Boolean,
//    onClick: () -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .padding(4.dp)
//            .size(32.dp)
//            .clip(CircleShape)
//            .background(
//                when {
//                    isToday -> Color(0xFF8AB58A)
//                    hasEntry -> Color(0xFFD1E7D1)
//                    else -> Color.Transparent
//                }
//            )
//            .border(
//                width = if (isToday || hasEntry) 0.dp else 1.dp,
//                color = if (isCurrentMonth) Color(0xFFCCCCCC) else Color.Transparent,
//                shape = CircleShape
//            )
//            .clickable(onClick = onClick),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = day.day.toString(),
//            fontSize = 14.sp,
//            color = when {
//                isToday -> Color.White
//                hasEntry -> Color(0xFF333333)
//                isCurrentMonth -> Color(0xFF333333)
//                else -> Color(0xFFCCCCCC)
//            }
//        )
//    }
//}
//
//@Composable
//fun EntriesCard(
//    entries: List<EmotionEntry>,
//    onEntryClick: (EmotionEntry) -> Unit
//) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFFA6C5A6))
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Text(
//                text = stringResource(R.string.your_entries),
//                fontSize = 16.sp,
//                color = Color.White,
//                modifier = Modifier.padding(bottom = 16.dp)
//            )
//
//            if (entries.isEmpty()) {
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(8.dp),
//                    colors = CardDefaults.cardColors(containerColor = Color.White)
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(100.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "Нет записей за этот месяц",
//                            fontSize = 14.sp,
//                            color = Color(0xFF757575)
//                        )
//                    }
//                }
//            } else {
//                // Сортируем записи по дате (от новых к старым)
//                val sortedEntries = entries.sortedByDescending { it.date }
//
//                sortedEntries.forEach { entry ->
//                    EntryItem(entry = entry, onClick = { onEntryClick(entry) })
//                    Spacer(modifier = Modifier.height(8.dp))
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun EntryItem(
//    entry: EmotionEntry,
//    onClick: () -> Unit
//) {
//    val dateFormat = SimpleDateFormat("d MMMM", Locale("ru"))
//    val formattedDate = dateFormat.format(entry.date)
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick),
//        shape = RoundedCornerShape(8.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        Row(
//            modifier = Modifier.padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Индикатор эмоции
//            Box(
//                modifier = Modifier
//                    .size(40.dp)
//                    .background(getEmotionColor(entry.emotionValues), CircleShape),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = formattedDate,
//                    fontSize = 12.sp,
//                    color = Color.White,
//                    textAlign = TextAlign.Center
//                )
//            }
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = entry.overallFeeling.takeIf { it.isNotEmpty() } ?: "Без описания",
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFF333333),
//                    maxLines = 1
//                )
//
//                if (entry.comments.isNotEmpty()) {
//                    Text(
//                        text = entry.comments,
//                        fontSize = 12.sp,
//                        color = Color(0xFF757575),
//                        maxLines = 1
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun EmotionStatsCard(stats: EmotionStats) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFFA6C5A6))
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Text(
//                text = "Статистика эмоций",
//                fontSize = 16.sp,
//                color = Color.White,
//                modifier = Modifier.padding(bottom = 16.dp)
//            )
//
//            // Общее самочувствие
//            Text(
//                text = "Общее самочувствие",
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color(0xFF333333),
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//
//            EmotionSlider(
//                value = stats.generalFeeling,
//                label = "Общее самочувствие"
//            )
//
//            Divider(
//                modifier = Modifier.padding(vertical = 8.dp),
//                color = Color(0xFFD1E7D1)
//            )
//
//            // Эмоции первого типа
//            Text(
//                text = "Эмоции",
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color(0xFF333333),
//                modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
//            )
//
//            EmotionSlider(value = stats.expectation, label = "Ожидание")
//            EmotionSlider(value = stats.joy, label = "Радость")
//            EmotionSlider(value = stats.trust, label = "Доверие")
//            EmotionSlider(value = stats.fear, label = "Страх")
//            EmotionSlider(value = stats.surprise, label = "Удивление")
//            EmotionSlider(value = stats.sadness, label = "Грусть")
//            EmotionSlider(value = stats.displeasure, label = "Неудовольствие")
//            EmotionSlider(value = stats.anger, label = "Гнев")
//        }
//    }
//}
//
//@Composable
//fun EmotionSlider(
//    value: Float,
//    label: String
//) {
//    Column(
//        modifier = Modifier.padding(vertical = 4.dp)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = label,
//                fontSize = 14.sp,
//                color = Color(0xFF333333)
//            )
//
//            Text(
//                text = String.format("%.1f", value),
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color(0xFF333333)
//            )
//        }
//
//        LinearProgressIndicator(
//            progress = value / 10f,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(8.dp)
//                .clip(RoundedCornerShape(4.dp)),
//            color = Color(0xFF8AB58A),
//            trackColor = Color(0xFFE0E0E0)
//        )
//    }
//}
//
//data class CalendarDay(
//    val day: Int,
//    val month: Int,
//    val year: Int,
//    val date: Date
//)
//
//data class EmotionStats(
//    val generalFeeling: Float = 0f,
//    val expectation: Float = 0f,
//    val joy: Float = 0f,
//    val trust: Float = 0f,
//    val fear: Float = 0f,
//    val surprise: Float = 0f,
//    val sadness: Float = 0f,
//    val displeasure: Float = 0f,
//    val anger: Float = 0f
//)
//
//fun getDaysForCalendarGrid(year: Int, month: Int): List<CalendarDay> {
//    val calendar = Calendar.getInstance()
//    calendar.set(year, month, 1)
//
//    // Получаем день недели первого дня месяца (0 - воскресенье, 1 - понедельник, и т.д.)
//    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
//
//    // Корректируем для начала недели с понедельника
//    val offset = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2
//
//    // Перемещаемся к первому дню в сетке (может быть из предыдущего месяца)
//    calendar.add(Calendar.DAY_OF_MONTH, -offset)
//
//    val days = mutableListOf<CalendarDay>()
//
//    // Добавляем 42 дня (6 недель) в сетку
//    for (i in 0 until 42) {
//        days.add(
//            CalendarDay(
//                day = calendar.get(Calendar.DAY_OF_MONTH),
//                month = calendar.get(Calendar.MONTH),
//                year = calendar.get(Calendar.YEAR),
//                date = calendar.time
//            )
//        )
//        calendar.add(Calendar.DAY_OF_MONTH, 1)
//    }
//
//    return days
//}
//
//fun isToday(day: CalendarDay): Boolean {
//    val today = Calendar.getInstance()
//    return day.day == today.get(Calendar.DAY_OF_MONTH) &&
//            day.month == today.get(Calendar.MONTH) &&
//            day.year == today.get(Calendar.YEAR)
//}
//
//fun getEmotionColor(emotionValues: EmotionValues): Color {
//    // Определяем преобладающую эмоцию
//    val emotions = listOf(
//        emotionValues.joy to Color(0xFF4CAF50),        // Зеленый для радости
//        emotionValues.trust to Color(0xFF2196F3),      // Синий для доверия
//        emotionValues.fear to Color(0xFF9C27B0),       // Фиолетовый для страха
//        emotionValues.surprise to Color(0xFFFFEB3B),   // Желтый для удивления
//        emotionValues.sadness to Color(0xFF607D8B),    // Серо-синий для грусти
//        emotionValues.displeasure to Color(0xFFFF9800), // Оранжевый для неудовольствия
//        emotionValues.anger to Color(0xFFF44336),      // Красный для гнева
//        emotionValues.expectation to Color(0xFF00BCD4) // Голубой для ожидания
//    )
//
//    val maxEmotion = emotions.maxByOrNull { it.first }
//
//    return maxEmotion?.second ?: Color(0xFF8AB58A) // Зеленый по умолчанию
//}
//
//// Метод для получения записей между датами (должен быть реализован в EmotionRepository)
//fun EmotionRepository.getEntriesBetweenDates(userId: String, startDate: Date, endDate: Date): List<EmotionEntry> {
//    return getAllEntriesForUser(userId).filter {
//        it.date in startDate..endDate
//    }
//}