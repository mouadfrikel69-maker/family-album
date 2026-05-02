package com.rork.kin.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rork.kin.ui.components.KinPrimaryButton
import com.rork.kin.ui.components.KinWordmark
import com.rork.kin.ui.components.PaperBackground
import com.rork.kin.ui.components.Polaroid
import com.rork.kin.ui.theme.InkBrown
import com.rork.kin.ui.theme.Mocha
import com.rork.kin.ui.theme.Terracotta
import kotlinx.coroutines.launch

private data class Slide(val title: String, val body: String, val img: String, val rot: Float)

private val slides = listOf(
    Slide(
        "Your family memories,\nin one warm place.",
        "No followers. No strangers. Just the people who already love your kids.",
        "https://images.unsplash.com/photo-1602030638412-bb8dcc0bc8b0?w=700&q=80",
        -3f,
    ),
    Slide(
        "Polaroids, not feeds.",
        "Every photo feels like a snapshot pinned to the fridge — soft, slow, and personal.",
        "https://images.unsplash.com/photo-1544126592-807ade215a0b?w=700&q=80",
        2f,
    ),
    Slide(
        "Grandma included.",
        "A tap to share. A tap to comment. Built so the whole family — yes, even Grandpa — can keep up.",
        "https://images.unsplash.com/photo-1519689680058-324335c77eba?w=700&q=80",
        -2f,
    ),
)

@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()
    val statusInsets = WindowInsets.statusBars.asPaddingValues()
    val navInsets = WindowInsets.navigationBars.asPaddingValues()

    PaperBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = statusInsets.calculateTopPadding()),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                KinWordmark(showLabel = true, badgeSize = 28.dp)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onDone) {
                    Text("Skip", color = Mocha)
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 36.dp),
                pageSpacing = 12.dp,
            ) { page ->
                val s = slides[page]
                val active by animateFloatAsState(
                    targetValue = if (page == pagerState.currentPage) 1f else 0.92f,
                    animationSpec = tween(300),
                    label = "slideScale",
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.6f)
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Polaroid(
                            imageUrl = s.img,
                            caption = listOf("April, riverside", "First day home", "Sunday lunch")[page],
                            rotation = s.rot * active,
                            showTape = true,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .rotate(0f),
                        )
                    }
                    Spacer(Modifier.height(28.dp))
                    Text(
                        text = s.title,
                        style = MaterialTheme.typography.displayMedium.copy(fontStyle = FontStyle.Normal),
                        color = InkBrown,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = s.body,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Mocha,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(slides.size) { i ->
                    val active = i == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .height(8.dp)
                            .width(if (active) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (active) Terracotta else Color(0x55C76B4A)),
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            KinPrimaryButton(
                label = if (pagerState.currentPage == slides.lastIndex) "Start your album" else "Next",
                onClick = {
                    if (pagerState.currentPage < slides.lastIndex) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onDone()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            )
            Spacer(Modifier.height(20.dp + navInsets.calculateBottomPadding()))
        }
    }

    LaunchedEffect(Unit) {} // ensure composition stable
}
