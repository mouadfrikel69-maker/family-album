package com.rork.kin.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoAlbum
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rork.kin.ui.components.Avatar
import com.rork.kin.ui.components.PaperBackground
import com.rork.kin.ui.state.AppViewModel
import com.rork.kin.ui.theme.BlushPink
import com.rork.kin.ui.theme.CreamPaper
import com.rork.kin.ui.theme.InkBrown
import com.rork.kin.ui.theme.Mocha
import com.rork.kin.ui.theme.PolaroidWhite
import com.rork.kin.ui.theme.Terracotta
import com.rork.kin.ui.theme.WashiTan
import kotlinx.coroutines.launch

enum class MainTab(val label: String, val outlined: ImageVector, val filled: ImageVector) {
    Home("Home", Icons.Outlined.Home, Icons.Filled.Home),
    Albums("Albums", Icons.Outlined.PhotoAlbum, Icons.Filled.PhotoAlbum),
    Memories("Memories", Icons.Outlined.AutoAwesome, Icons.Filled.AutoAwesome),
    Profile("You", Icons.Outlined.Person, Icons.Outlined.Person),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    appVm: AppViewModel,
    onOpenPhoto: (String) -> Unit,
    onOpenAlbum: (String) -> Unit,
    onOpenMembers: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenInvite: () -> Unit,
    onSignOut: () -> Unit,
) {
    var tab by remember { mutableStateOf(MainTab.Home) }
    var uploadOpen by remember { mutableStateOf(false) }
    var chooserOpen by remember { mutableStateOf(false) }
    var newAlbumOpen by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val statusInsets = WindowInsets.statusBars.asPaddingValues()
    val navInsets = WindowInsets.navigationBars.asPaddingValues()

    val appState by appVm.state.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CreamPaper,
                modifier = Modifier.width(300.dp),
            ) {
                DrawerContent(
                    currentUserName = appState.currentUser?.name ?: "You",
                    currentUserMember = appState.currentUser,
                    familyName = appState.family?.name ?: "Your family",
                    onClose = { scope.launch { drawerState.close() } },
                    onTab = { t ->
                        tab = t
                        scope.launch { drawerState.close() }
                    },
                    onMembers = { scope.launch { drawerState.close() }; onOpenMembers() },
                    onInvite = { scope.launch { drawerState.close() }; onOpenInvite() },
                    onSettings = { scope.launch { drawerState.close() }; onOpenSettings() },
                    onSignOut = onSignOut,
                )
            }
        },
    ) {
        PaperBackground {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = if (uploadOpen) -1 else tab.ordinal,
                    transitionSpec = { fadeIn(spring()) togetherWith fadeOut(spring()) },
                    modifier = Modifier.fillMaxSize(),
                    label = "tabContent",
                ) { idx ->
                    when (idx) {
                        -1 -> UploadScreen(
                            appVm = appVm,
                            onClose = { uploadOpen = false },
                            contentPadding = PaddingValues(top = statusInsets.calculateTopPadding()),
                        )
                        0 -> HomeScreen(
                            appVm = appVm,
                            onOpenPhoto = onOpenPhoto,
                            onOpenMenu = { scope.launch { drawerState.open() } },
                            onOpenNotifications = onOpenNotifications,
                            onOpenMembers = onOpenMembers,
                            contentPadding = PaddingValues(top = statusInsets.calculateTopPadding()),
                        )
                        1 -> AlbumsScreen(
                            appVm = appVm,
                            onOpenAlbum = onOpenAlbum,
                            onNewAlbum = { newAlbumOpen = true },
                            contentPadding = PaddingValues(top = statusInsets.calculateTopPadding() + 8.dp),
                        )
                        2 -> MemoriesScreen(
                            appVm = appVm,
                            onOpenPhoto = onOpenPhoto,
                            contentPadding = PaddingValues(top = statusInsets.calculateTopPadding() + 8.dp),
                        )
                        3 -> ProfileScreen(
                            appVm = appVm,
                            onSignOut = onSignOut,
                            onOpenInvite = onOpenInvite,
                            onOpenSettings = onOpenSettings,
                            contentPadding = PaddingValues(top = statusInsets.calculateTopPadding() + 8.dp),
                        )
                    }
                }

                BottomBar(
                    selected = tab,
                    onSelect = {
                        uploadOpen = false
                        tab = it
                    },
                    onUpload = {
                        if (uploadOpen) uploadOpen = false
                        else chooserOpen = true
                    },
                    uploadOpen = uploadOpen,
                    bottomInset = navInsets.calculateBottomPadding(),
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }

    if (chooserOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { chooserOpen = false },
            sheetState = sheetState,
            containerColor = CreamPaper,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = navInsets.calculateBottomPadding()),
            ) {
                Text(
                    "What would you like to add?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = InkBrown,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Share a moment or start a new collection.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Mocha,
                )
                Spacer(Modifier.height(20.dp))
                ChooserRow(
                    icon = Icons.Filled.AddAPhoto,
                    accent = Terracotta,
                    title = "New photo",
                    subtitle = "Pick from your gallery or take one now.",
                    onClick = {
                        chooserOpen = false
                        uploadOpen = true
                    },
                )
                Spacer(Modifier.height(12.dp))
                ChooserRow(
                    icon = Icons.Filled.LibraryAdd,
                    accent = WashiTan,
                    title = "New album",
                    subtitle = "A trip, a season, a milestone — anyone can add.",
                    onClick = {
                        chooserOpen = false
                        newAlbumOpen = true
                    },
                )
                Spacer(Modifier.height(20.dp))
            }
        }
    }

    if (newAlbumOpen) {
        var title by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { newAlbumOpen = false },
            containerColor = CreamPaper,
            title = { Text("Start a new album", color = InkBrown) },
            text = {
                Column {
                    Text(
                        "Give it a name. You can add photos to it any time.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Mocha,
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Summer in Maine", color = Mocha.copy(alpha = 0.5f)) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Terracotta,
                            unfocusedBorderColor = BlushPink,
                            focusedContainerColor = PolaroidWhite,
                            unfocusedContainerColor = PolaroidWhite,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = appVm.createAlbum(title)
                        newAlbumOpen = false
                        tab = MainTab.Albums
                        onOpenAlbum(id)
                    },
                    enabled = title.trim().isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                    shape = RoundedCornerShape(12.dp),
                ) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { newAlbumOpen = false }) {
                    Text("Cancel", color = Mocha)
                }
            },
        )
    }
}

@Composable
private fun ChooserRow(
    icon: ImageVector,
    accent: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(PolaroidWhite)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = accent)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = InkBrown,
            )
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Mocha)
        }
    }
}

@Composable
private fun BottomBar(
    selected: MainTab,
    onSelect: (MainTab) -> Unit,
    onUpload: () -> Unit,
    uploadOpen: Boolean,
    bottomInset: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    val fabRotation by animateDpAsState(
        targetValue = if (uploadOpen) 45.dp else 0.dp,
        label = "fabRot",
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp + bottomInset),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(28.dp), clip = false)
                .clip(RoundedCornerShape(28.dp))
                .background(PolaroidWhite)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            BarItem(MainTab.Home, selected, onSelect, modifier = Modifier.weight(1f))
            BarItem(MainTab.Albums, selected, onSelect, modifier = Modifier.weight(1f))
            // central upload spacer
            Spacer(Modifier.width(64.dp))
            BarItem(MainTab.Memories, selected, onSelect, modifier = Modifier.weight(1f))
            BarItem(MainTab.Profile, selected, onSelect, modifier = Modifier.weight(1f))
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(64.dp)
                .shadow(12.dp, CircleShape, clip = false)
                .clip(CircleShape)
                .background(Terracotta)
                .clickable(onClick = onUpload),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (uploadOpen) Icons.Filled.Close else Icons.Filled.Add,
                contentDescription = "Add memory",
                tint = Color.White,
                modifier = Modifier.size(28.dp),
            )
            // tiny dot decoration just for charm
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0x66FFFFFF)),
            )
        }
        // suppress unused warning
        @Suppress("UNUSED_EXPRESSION") fabRotation
    }
}

@Composable
private fun BarItem(
    item: MainTab,
    selected: MainTab,
    onSelect: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val active = item == selected
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onSelect(item) }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            if (active) item.filled else item.outlined,
            item.label,
            tint = if (active) Terracotta else Mocha,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) Terracotta else Mocha,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

@Composable
private fun DrawerContent(
    currentUserName: String,
    currentUserMember: com.rork.kin.data.Member?,
    familyName: String,
    onClose: () -> Unit,
    onTab: (MainTab) -> Unit,
    onMembers: () -> Unit,
    onInvite: () -> Unit,
    onSettings: () -> Unit,
    onSignOut: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamPaper)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
    ) {
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (currentUserMember != null) {
                Avatar(currentUserMember, size = 48.dp)
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(BlushPink),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(currentUserName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = InkBrown)
                Text(familyName, style = MaterialTheme.typography.bodySmall, color = Mocha)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(BlushPink)
                    .clickable(onClick = onClose)
                    .padding(8.dp),
            ) {
                Icon(Icons.Filled.Close, "Close", tint = InkBrown)
            }
        }
        Spacer(Modifier.height(10.dp))
        DrawerEntry("Home", Icons.Filled.Home, Terracotta) { onTab(MainTab.Home) }
        DrawerEntry("Albums", Icons.Filled.PhotoAlbum, WashiTan) { onTab(MainTab.Albums) }
        DrawerEntry("Memories", Icons.Filled.AutoAwesome, com.rork.kin.ui.theme.SageMist) { onTab(MainTab.Memories) }
        DrawerEntry("Family members", Icons.Filled.Group, Terracotta, onMembers)
        DrawerEntry("Favorites", Icons.Filled.StarBorder, WashiTan) { /* no-op */ }
        DrawerEntry("Trash", Icons.Filled.RestoreFromTrash, Mocha) { /* no-op */ }
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(1.dp).background(BlushPink))
        Spacer(Modifier.height(8.dp))
        DrawerEntry("Invite family", Icons.Filled.PersonAdd, Terracotta, onInvite)
        DrawerEntry("Settings", Icons.Filled.Settings, Mocha, onSettings)
        DrawerEntry("Help & support", Icons.Filled.HelpOutline, Mocha) { /* no-op */ }
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(PolaroidWhite)
                .clickable(onClick = onSignOut)
                .padding(16.dp),
        ) {
            Text("Sign out", color = Terracotta, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun DrawerEntry(
    label: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = accent)
        }
        Spacer(Modifier.width(14.dp))
        Text(label, style = MaterialTheme.typography.titleMedium, color = InkBrown)
    }
}
