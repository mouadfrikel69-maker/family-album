package com.rork.kin.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rork.kin.ui.screens.AlbumDetailScreen
import com.rork.kin.ui.screens.AuthScreen
import com.rork.kin.ui.screens.CreateJoinFamilyScreen
import com.rork.kin.ui.screens.InviteScreen
import com.rork.kin.ui.screens.MainScaffold
import com.rork.kin.ui.screens.MembersScreen
import com.rork.kin.ui.screens.NotificationsScreen
import com.rork.kin.ui.screens.OnboardingScreen
import com.rork.kin.ui.screens.PhotoDetailScreen
import com.rork.kin.ui.screens.ProfileSetupScreen
import com.rork.kin.ui.screens.SettingsScreen
import com.rork.kin.ui.state.AppViewModel

private object Routes {
    const val Onboarding = "onboarding"
    const val Auth = "auth"
    const val Profile = "profile_setup"
    const val Family = "family"
    const val Main = "main"
    const val Photo = "photo/{id}"
    const val Album = "album/{id}"
    const val Members = "members"
    const val Notifications = "notifications"
    const val Settings = "settings"
    const val Invite = "invite"
    fun photo(id: String) = "photo/$id"
    fun album(id: String) = "album/$id"
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val appVm: AppViewModel = viewModel()

    NavHost(
        navController = nav,
        startDestination = Routes.Onboarding,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                tween(280),
            ) + fadeIn(tween(280))
        },
        exitTransition = { fadeOut(tween(180)) },
        popEnterTransition = { fadeIn(tween(220)) },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                tween(280),
            ) + fadeOut(tween(220))
        },
    ) {
        composable(Routes.Onboarding) {
            OnboardingScreen(onDone = {
                appVm.completeOnboarding()
                nav.navigate(Routes.Auth) {
                    popUpTo(Routes.Onboarding) { inclusive = true }
                }
            })
        }
        composable(Routes.Auth) {
            AuthScreen(onContinue = {
                appVm.signIn()
                nav.navigate(Routes.Profile) {
                    popUpTo(Routes.Auth) { inclusive = true }
                }
            })
        }
        composable(Routes.Profile) {
            ProfileSetupScreen(onContinue = { name, relationship, avatarUri ->
                appVm.setProfile(name, relationship, avatarUri)
                nav.navigate(Routes.Family) {
                    popUpTo(Routes.Profile) { inclusive = true }
                }
            })
        }
        composable(Routes.Family) {
            CreateJoinFamilyScreen(
                onCreate = { familyName ->
                    appVm.createFamily(familyName)
                    nav.navigate(Routes.Main) {
                        popUpTo(Routes.Family) { inclusive = true }
                    }
                },
                onJoin = { code ->
                    appVm.joinFamily(code)
                    nav.navigate(Routes.Main) {
                        popUpTo(Routes.Family) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.Main) {
            MainScaffold(
                appVm = appVm,
                onOpenPhoto = { id -> nav.navigate(Routes.photo(id)) },
                onOpenAlbum = { id -> nav.navigate(Routes.album(id)) },
                onOpenMembers = { nav.navigate(Routes.Members) },
                onOpenNotifications = { nav.navigate(Routes.Notifications) },
                onOpenSettings = { nav.navigate(Routes.Settings) },
                onOpenInvite = { nav.navigate(Routes.Invite) },
                onSignOut = {
                    appVm.signOut()
                    nav.navigate(Routes.Onboarding) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.Photo) { entry ->
            val id = entry.arguments?.getString("id") ?: return@composable
            PhotoDetailScreen(appVm = appVm, photoId = id, onBack = { nav.popBackStack() })
        }
        composable(Routes.Album) { entry ->
            val id = entry.arguments?.getString("id") ?: return@composable
            AlbumDetailScreen(
                appVm = appVm, albumId = id,
                onBack = { nav.popBackStack() },
                onOpenPhoto = { pid -> nav.navigate(Routes.photo(pid)) },
            )
        }
        composable(Routes.Members) {
            MembersScreen(
                appVm = appVm,
                onBack = { nav.popBackStack() },
                onInvite = { nav.navigate(Routes.Invite) },
            )
        }
        composable(Routes.Notifications) {
            NotificationsScreen(appVm = appVm, onBack = { nav.popBackStack() })
        }
        composable(Routes.Settings) {
            SettingsScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.Invite) {
            InviteScreen(appVm = appVm, onBack = { nav.popBackStack() })
        }
    }
}
