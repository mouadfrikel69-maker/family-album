package com.rork.kin.data

import java.time.LocalDate
import java.time.LocalDateTime

data class Member(
    val id: String,
    val name: String,
    val relationship: String,
    val avatarColor: Long,
    val initials: String,
    val role: Role = Role.Member,
)

enum class Role { Admin, Member, Viewer }

data class Comment(
    val id: String,
    val authorId: String,
    val text: String,
    val createdAt: LocalDateTime,
)

data class Photo(
    val id: String,
    val url: String,
    val authorId: String,
    val caption: String,
    val createdAt: LocalDateTime,
    val takenOn: LocalDate,
    val location: String? = null,
    val albumIds: List<String> = emptyList(),
    val likedBy: Set<String> = emptySet(),
    val comments: List<Comment> = emptyList(),
    val taggedMemberIds: List<String> = emptyList(),
)

data class Album(
    val id: String,
    val title: String,
    val coverPhotoId: String,
    val photoIds: List<String>,
    val dateRangeLabel: String,
    val accentColor: Long,
)

data class Family(
    val id: String,
    val name: String,
    val inviteCode: String,
    val createdAt: LocalDate,
)

data class Notification(
    val id: String,
    val icon: NotifKind,
    val actorId: String,
    val text: String,
    val createdAt: LocalDateTime,
    val read: Boolean = false,
)

enum class NotifKind { NewPhoto, Comment, Like, NewMember, Memory }
