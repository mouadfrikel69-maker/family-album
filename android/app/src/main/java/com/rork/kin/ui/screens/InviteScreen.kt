package com.rork.kin.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rork.kin.ui.state.AppViewModel
import com.rork.kin.ui.components.KinPrimaryButton
import com.rork.kin.ui.components.KinScreenHeader
import com.rork.kin.ui.components.QrCode
import com.rork.kin.ui.theme.BlushPink
import com.rork.kin.ui.theme.CreamPaper
import com.rork.kin.ui.theme.InkBrown
import com.rork.kin.ui.theme.Mocha
import com.rork.kin.ui.theme.PolaroidWhite
import com.rork.kin.ui.theme.Terracotta
import com.rork.kin.ui.theme.WashiTan

@Composable
fun InviteScreen(appVm: AppViewModel, onBack: () -> Unit) {
    val statusInsets = WindowInsets.statusBars.asPaddingValues()
    val navInsets = WindowInsets.navigationBars.asPaddingValues()
    val ctx = LocalContext.current

    val state by appVm.state.collectAsStateWithLifecycle()
    val familyName = state.family?.name ?: "our family"
    val code = state.family?.inviteCode?.takeIf { it.isNotBlank() } ?: "—"
    val link = "https://kin.family/join/$code"
    val message = "Join $familyName on Kin — our private little family album.\n\n" +
        "Tap to join: $link\nOr enter code: $code"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamPaper)
            .padding(top = statusInsets.calculateTopPadding())
            .verticalScroll(rememberScrollState()),
    ) {
        KinScreenHeader(
            title = "Bring everyone in.",
            subtitle = "Share this with your family. Anyone with the code or link joins $familyName.",
            tagline = "a quiet circle, just for you",
            onBack = onBack,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
        )

        Spacer(Modifier.height(20.dp))

        // QR card
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PolaroidWhite)
                .border(1.dp, BlushPink, RoundedCornerShape(20.dp))
                .padding(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CreamPaper)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    QrCode(
                        content = link,
                        modifier = Modifier.fillMaxSize(),
                        foreground = InkBrown,
                    )
                }
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BlushPink.copy(alpha = 0.35f))
                        .clickable { copy(ctx, code, "Code copied") }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    Text(
                        code,
                        style = MaterialTheme.typography.displaySmall.copy(letterSpacing = 4.sp),
                        color = Terracotta,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Tap the code to copy",
                    style = MaterialTheme.typography.labelMedium,
                    color = Mocha,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Link row
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PolaroidWhite)
                .border(1.dp, WashiTan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .clickable { copy(ctx, link, "Link copied") }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                link,
                style = MaterialTheme.typography.bodyMedium,
                color = InkBrown,
                modifier = Modifier.weight(1f),
            )
            Icon(Icons.Filled.ContentCopy, "Copy link", tint = Mocha)
        }

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ShareTile(
                Icons.Filled.Share,
                "Share link",
                Modifier.weight(1f),
            ) { shareLink(ctx, message) }
            ShareTile(
                Icons.Filled.Mail,
                "Email invite",
                Modifier.weight(1f),
            ) { emailInvite(ctx, familyName, message) }
            ShareTile(
                Icons.Filled.ContentCopy,
                "Copy code",
                Modifier.weight(1f),
            ) { copy(ctx, code, "Code copied") }
        }

        Spacer(Modifier.height(28.dp))
        KinPrimaryButton(
            label = "Done",
            onClick = onBack,
            modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
        )
        Spacer(Modifier.height(20.dp + navInsets.calculateBottomPadding()))
    }
}

@Composable
private fun ShareTile(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(PolaroidWhite)
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(50))
                .background(WashiTan.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = WashiTan)
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = InkBrown)
    }
}

private fun copy(ctx: Context, text: String, toast: String) {
    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("Kin invite", text))
    Toast.makeText(ctx, toast, Toast.LENGTH_SHORT).show()
}

private fun shareLink(ctx: Context, message: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Join our family on Kin")
        putExtra(Intent.EXTRA_TEXT, message)
    }
    ctx.startActivity(Intent.createChooser(intent, "Share invite"))
}

private fun emailInvite(
    ctx: Context,
    familyName: String,
    message: String,
) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_SUBJECT, "Join $familyName on Kin")
        putExtra(Intent.EXTRA_TEXT, message)
    }
    val resolved = intent.resolveActivity(ctx.packageManager)
    if (resolved != null) {
        ctx.startActivity(intent)
    } else {
        // Fallback to general share
        shareLink(ctx, message)
    }
}
