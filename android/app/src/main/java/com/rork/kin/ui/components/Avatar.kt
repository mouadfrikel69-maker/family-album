package com.rork.kin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.kin.data.Member
import com.rork.kin.ui.theme.WarmWhite

@Composable
fun Avatar(
    member: Member,
    size: Dp = 36.dp,
    modifier: Modifier = Modifier,
    bordered: Boolean = true,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(member.avatarColor))
            .then(if (bordered) Modifier.border(2.dp, WarmWhite, CircleShape) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = member.initials,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = (size.value * 0.38f).sp,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
