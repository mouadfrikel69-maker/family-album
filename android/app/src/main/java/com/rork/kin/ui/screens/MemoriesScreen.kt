package com.rork.kin.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.rork.kin.data.Photo
import com.rork.kin.ui.components.KinTagline
import com.rork.kin.ui.components.KinWordmark
import com.rork.kin.ui.components.Polaroid
import com.rork.kin.ui.state.AppViewModel
import com.rork.kin.ui.theme.BlushPink
import com.rork.kin.ui.theme.DustyRose
import com.rork.kin.ui.theme.InkBrown
import com.rork.kin.ui.theme.Mocha
import com.rork.kin.ui.theme.PolaroidWhite
import com.rork.kin.ui.theme.Terracotta
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MemoriesScreen(
    appVm: AppViewModel,
    onOpenPhoto: (String) -> Unit,
    contentPadding: PaddingValues,
) {
    val state by appVm.state.collectAsStateWithLifecycle()
    val today = LocalDate.now()
    val onThisDay = remember(state.photos) { appVm.memories(today) }
    val grouped = remember(state.photos) { appVm.groupedByMonth() }

    var visibleMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    val photosByDate = remember(state.photos) {
        state.photos.groupBy { it.takenOn }
    }
    val selectedPhotos = remember(selectedDate, state.photos) {
        selectedDate?.let { photosByDate[it].orEmpty() }.orEmpty()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 8.dp + contentPadding.calculateTopPadding(),
            bottom = 100.dp + contentPadding.calculateBottomPadding(),
            start = 0.dp, end = 0.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item { Header(today) }
        item {
            CalendarCard(
                month = visibleMonth,
                selected = selectedDate,
                today = today,
                datesWithPhotos = photosByDate.keys,
                onPrev = { visibleMonth = visibleMonth.minusMonths(1) },
                onNext = { visibleMonth = visibleMonth.plusMonths(1) },
                onSelect = { date ->
                    selectedDate = if (selectedDate == date) null else date
                },
            )
        }
        item {
            AnimatedVisibility(
                visible = selectedDate != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                SelectedDayStrip(
                    date = selectedDate,
                    photos = selectedPhotos,
                    onClick = onOpenPhoto,
                )
            }
        }
        item {
            SectionLabel("On this day")
            Spacer(Modifier.height(10.dp))
            OnThisDayCarousel(onThisDay, onOpenPhoto)
        }
        items(grouped, key = { it.first }) { (label, photos) ->
            MonthSection(label = label, photos = photos, onClick = onOpenPhoto)
        }
    }
}

@Composable
private fun Header(today: LocalDate) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            KinWordmark(showLabel = false, badgeSize = 26.dp)
            Spacer(Modifier.width(10.dp))
            Text(
                "the family album",
                style = MaterialTheme.typography.labelMedium,
                color = Mocha,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Memories",
            style = MaterialTheme.typography.displayMedium,
            color = InkBrown,
        )
        Text(
            today.format(DateTimeFormatter.ofPattern("EEEE, d MMMM")),
            style = MaterialTheme.typography.bodyLarge,
            color = Mocha,
        )
        Spacer(Modifier.height(8.dp))
        KinTagline("the moments worth coming back to")
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.headlineSmall,
        color = InkBrown,
        modifier = Modifier.padding(horizontal = 20.dp),
    )
}

@Composable
private fun CalendarCard(
    month: YearMonth,
    selected: LocalDate?,
    today: LocalDate,
    datesWithPhotos: Set<LocalDate>,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onSelect: (LocalDate) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(PolaroidWhite)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BlushPink)
                    .clickable { onPrev() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.ChevronLeft, contentDescription = "Previous month", tint = InkBrown)
            }
            Spacer(Modifier.weight(1f))
            Text(
                month.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleMedium,
                color = InkBrown,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BlushPink)
                    .clickable { onNext() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.ChevronRight, contentDescription = "Next month", tint = InkBrown)
            }
        }
        Spacer(Modifier.height(12.dp))

        // Weekday header (Mon..Sun)
        Row(modifier = Modifier.fillMaxWidth()) {
            val days = listOf(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY,
            )
            days.forEach { d ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        d.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                        style = MaterialTheme.typography.labelSmall,
                        color = Mocha,
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))

        val firstOfMonth = month.atDay(1)
        val leadingBlanks = (firstOfMonth.dayOfWeek.value + 6) % 7 // Mon=0
        val totalCells = leadingBlanks + month.lengthOfMonth()
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNum = cellIndex - leadingBlanks + 1
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f), contentAlignment = Alignment.Center) {
                        if (dayNum in 1..month.lengthOfMonth()) {
                            val date = month.atDay(dayNum)
                            val hasPhotos = datesWithPhotos.contains(date)
                            val isSelected = selected == date
                            val isToday = date == today

                            val bg = when {
                                isSelected -> Terracotta
                                isToday -> DustyRose
                                else -> Color.Transparent
                            }
                            val fg = when {
                                isSelected -> PolaroidWhite
                                else -> InkBrown
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(bg)
                                    .clickable(enabled = hasPhotos || isToday) { onSelect(date) },
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        dayNum.toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = fg,
                                        fontWeight = if (isToday || isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    )
                                    if (hasPhotos) {
                                        Spacer(Modifier.height(2.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) PolaroidWhite else Terracotta),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "Tap a marked date to see that day's photos.",
            style = MaterialTheme.typography.labelSmall,
            color = Mocha,
        )
    }
}

@Composable
private fun SelectedDayStrip(
    date: LocalDate?,
    photos: List<Photo>,
    onClick: (String) -> Unit,
) {
    if (date == null) return
    Column {
        Text(
            date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")),
            style = MaterialTheme.typography.titleMedium,
            color = InkBrown,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(10.dp))
        if (photos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(PolaroidWhite)
                    .padding(18.dp),
            ) {
                Text(
                    "No photos on this day yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Mocha,
                )
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(photos, key = { "d_${it.id}" }) { p ->
                    Polaroid(
                        imageUrl = p.url,
                        caption = null,
                        rotation = ((p.id.hashCode() % 5) - 2).toFloat(),
                        showTape = false,
                        onClick = { onClick(p.id) },
                        modifier = Modifier.width(160.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun OnThisDayCarousel(photos: List<Photo>, onClick: (String) -> Unit) {
    if (photos.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(PolaroidWhite)
                .padding(20.dp),
        ) {
            Text(
                "No memories from previous years today — yet. Come back tomorrow.",
                style = MaterialTheme.typography.bodyMedium,
                color = Mocha,
            )
        }
        return
    }
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        items(photos, key = { it.id }) { p ->
            Column(
                modifier = Modifier
                    .width(220.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(PolaroidWhite)
                    .padding(8.dp)
                    .clickable { onClick(p.id) },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.85f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BlushPink),
                ) {
                    AsyncImage(
                        model = p.url,
                        contentDescription = p.caption,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(DustyRose),
                    ) {
                        Text(
                            "${LocalDate.now().year - p.takenOn.year} years ago",
                            color = InkBrown,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    p.caption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkBrown,
                    maxLines = 2,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun MonthSection(label: String, photos: List<Photo>, onClick: (String) -> Unit) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Terracotta),
            )
            Spacer(Modifier.width(10.dp))
            Text(label, style = MaterialTheme.typography.headlineSmall, color = InkBrown)
            Spacer(Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0x33C76B4A)))
        }
        Spacer(Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(photos, key = { "m_${it.id}" }) { p ->
                Polaroid(
                    imageUrl = p.url,
                    caption = null,
                    rotation = ((p.id.hashCode() % 5) - 2).toFloat(),
                    showTape = false,
                    onClick = { onClick(p.id) },
                    modifier = Modifier.width(160.dp),
                )
            }
        }
    }
}
