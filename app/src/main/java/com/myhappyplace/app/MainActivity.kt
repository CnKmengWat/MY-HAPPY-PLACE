package com.myhappyplace.app

import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import coil.compose.AsyncImage
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Ink = Color(0xFF251B3A)
private val Purple = Color(0xFF8B5CF6)
private val Pink = Color(0xFFFF7EB6)
private val Lavender = Color(0xFFF5F0FF)
private val Muted = Color(0xFF8B8298)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HappyPlaceApp() }
    }
}

private data class ChatMessage(
    val id: Long,
    val text: String,
    val isFromMe: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

private enum class RoomMood(
    val label: String,
    val cardColor: Color,
    val gradientEndColor: Color,
    val icon: ImageVector
) {
    HAPPY("Happy", Color(0xFFE87A5D), Color(0xFFD45D79), Icons.Default.SentimentVerySatisfied),
    CHILL("Chill", Color(0xFFE58AAE), Color(0xFFB56CC4), Icons.Default.LocalCafe),
    ENERGETIC("Energetic", Color(0xFF7555B8), Color(0xFF9E5CE6), Icons.Default.Bolt),
    COZY("Cozy", Color(0xFF5C9DAD), Color(0xFF4973A6), Icons.Default.Bedtime),
    MUSIC("Music", Color(0xFF5B6EE1), Color(0xFF8B5CF6), Icons.Default.MusicNote)
}

private data class PartyBackgroundPreset(
    val id: String,
    val label: String,
    val subtitle: String,
    val colors: List<Color>,
    val drawableRes: Int? = null
)

private val PartyBackgroundCatalog = listOf(
    PartyBackgroundPreset(
        id = "lounge",
        label = "Lantern Lounge",
        subtitle = "Rooftop fairy lights & skyline",
        colors = listOf(Color(0xFF1E1538), Color(0xFF362257), Color(0xFF1A122E)),
        drawableRes = R.drawable.party_lounge_bg
    ),
    PartyBackgroundPreset(
        id = "mood",
        label = "Mood Aura",
        subtitle = "Synced to room mood glow",
        colors = listOf(Color(0xFF21163E), Color(0xFF3F2768), Color(0xFF161028))
    ),
    PartyBackgroundPreset(
        id = "neon",
        label = "Midnight Neon",
        subtitle = "Violet & magenta stage wash",
        colors = listOf(Color(0xFF120B29), Color(0xFF2F195F), Color(0xFF5E2368))
    ),
    PartyBackgroundPreset(
        id = "sunset",
        label = "Sunset Dusk",
        subtitle = "Warm rose & plum twilight",
        colors = listOf(Color(0xFF241122), Color(0xFF5A2241), Color(0xFF8C3B4A))
    ),
    PartyBackgroundPreset(
        id = "aurora",
        label = "Emerald Aurora",
        subtitle = "Calm teal & deep indigo",
        colors = listOf(Color(0xFF0B1F24), Color(0xFF17434B), Color(0xFF2A3566))
    ),
    PartyBackgroundPreset(
        id = "starlight",
        label = "Starlight Sky",
        subtitle = "Deep midnight blue & indigo",
        colors = listOf(Color(0xFF0C1329), Color(0xFF1B2852), Color(0xFF372A6E))
    )
)

private fun moodRoomGradientColors(mood: RoomMood): List<Color> = when (mood) {
    RoomMood.HAPPY -> listOf(Color(0xFF29152B), Color(0xFF6A2C46), Color(0xFF1E1430))
    RoomMood.CHILL -> listOf(Color(0xFF231331), Color(0xFF562A63), Color(0xFF19132E))
    RoomMood.ENERGETIC -> listOf(Color(0xFF181038), Color(0xFF43247D), Color(0xFF191232))
    RoomMood.COZY -> listOf(Color(0xFF0F202C), Color(0xFF23495E), Color(0xFF14172E))
    RoomMood.MUSIC -> listOf(Color(0xFF14193B), Color(0xFF2F3E86), Color(0xFF191436))
}

private enum class PartySortOrder(val label: String) {
    POPULAR("Most Popular"),
    NEWEST("Newest")
}

private data class VoiceRoom(
    val id: String,
    val title: String,
    val detail: String,
    val mood: RoomMood,
    val color: Color,
    val listeners: Int,
    val speakers: List<String>,
    val owner: String = speakers.firstOrNull() ?: "Host",
    val backgroundId: String = "mood",
    val customBackgroundUri: android.net.Uri? = null,
    val createdAt: Long = System.currentTimeMillis()
)

private data class FriendPerson(
    val name: String,
    val handle: String,
    val vibe: String,
    val isOnline: Boolean = true,
    val mutualCount: Int = 0
)

private val InitialPeopleDirectory = listOf(
    FriendPerson("Mina", "@mina.kh", "Night owl & acoustic music", true, 4),
    FriendPerson("Dara", "@dara_vibe", "Voice party host & gamer", true, 3),
    FriendPerson("Sophy", "@sophy.music", "Khmer desserts & chill beats", true, 5),
    FriendPerson("Alex", "@alex_talks", "Late-night storyteller", true, 2),
    FriendPerson("Nita", "@nita.night", "Cozy tea & coffee talks", true, 2),
    FriendPerson("Vannak", "@vannak.star", "Guitar jams & trivia", true, 3),
    FriendPerson("Maly", "@maly.lotus", "Happy Stars co-captain", false, 1),
    FriendPerson("Sokha", "@sokha.party", "Khmer Friends room host", true, 4)
)

private data class FamilyMember(
    val name: String,
    val role: String,
    val points: Int
)

private enum class FamilyLogoShape(val label: String) {
    CIRCLE("Circle"),
    BADGE("Badge"),
    SHIELD("Shield")
}

private data class FamilyClub(
    val id: String,
    val name: String,
    val motto: String,
    val emblem: String,
    val color: Color,
    val accentColor: Color = Pink,
    val logoShape: FamilyLogoShape = FamilyLogoShape.CIRCLE,
    val logoImageUri: android.net.Uri? = null,
    val level: Int,
    val points: Int,
    val members: List<FamilyMember>,
    val announcements: List<String>
)

private data class GiftRecord(
    val id: Long,
    val giftName: String,
    val emoji: String,
    val price: Int,
    val isSent: Boolean,
    val peerName: String,
    val timestamp: Long
)

private data class KhmerGift(val name: String, val price: Int, val emoji: String, val note: String)

private val KhmerGiftCatalog = listOf(
    KhmerGift("នំអន្សមចេក", 29, "🫔", "នំអន្សមខ្ចប់ស្លឹកចេក"),
    KhmerGift("នំអន្សមជ្រូក", 49, "🍙", "រសជាតិប្រពៃណី"),
    KhmerGift("នំគ្រក់", 19, "🥥", "នំដូងខ្មែរ"),
    KhmerGift("នំអាកោ", 25, "🍮", "នំអាកោផ្អែម"),
    KhmerGift("នំផ្លែអាយ", 15, "🍡", "បង្អែមខ្មែរ"),
    KhmerGift("នំចាក់ចាន់", 35, "🍰", "នំស្រទាប់ចម្រុះ"),
    KhmerGift("នំគម", 22, "🥮", "នំអង្ករដំណើប"),
    KhmerGift("នំល្ពៅ", 39, "🎃", "នំល្ពៅចំហុយ")
)

private data class PartyTrack(
    val id: String,
    val title: String,
    val artist: String,
    val vibe: String,
    val mood: RoomMood,
    val durationSeconds: Int,
    val color: Color,
    val audioUri: android.net.Uri? = null,
    val fileName: String? = null
)

private val PartyMusicCatalog = listOf(
    PartyTrack("angkor-sunset", "Angkor Acoustic Sunset", "Mina & Friends", "Chill Acoustic", RoomMood.CHILL, 185, Color(0xFFE58AAE)),
    PartyTrack("phnom-penh-lofi", "Phnom Penh Lo-Fi Rain", "DJ Dara", "Cozy Lo-Fi", RoomMood.COZY, 210, Color(0xFF5C9DAD)),
    PartyTrack("mekong-starlight", "Mekong Starlight", "Sophy", "Happy Pop", RoomMood.HAPPY, 168, Color(0xFFE87A5D)),
    PartyTrack("khmer-groove", "Khmer New Year Groove", "Vannak", "Energetic Dance", RoomMood.ENERGETIC, 196, Purple),
    PartyTrack("midnight-jazz", "Late Night Jazz Café", "Alex", "Smooth Lounge", RoomMood.MUSIC, 224, Color(0xFF5B6EE1))
)

private fun resolveAudioDisplayName(context: android.content.Context, uri: android.net.Uri): String {
    val cursor = runCatching {
        context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)
    }.getOrNull()
    cursor?.use {
        if (it.moveToFirst()) {
            val idx = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (idx >= 0) {
                val raw = it.getString(idx)
                if (!raw.isNullOrBlank()) return raw
            }
        }
    }
    return uri.lastPathSegment?.substringAfterLast('/') ?: "Uploaded Track"
}

private fun stripAudioExtension(fileName: String): String =
    fileName.substringBeforeLast('.').replace('_', ' ').replace('-', ' ').trim().ifEmpty { fileName }

private fun formatTrackTime(seconds: Int): String {
    val safe = seconds.coerceAtLeast(0)
    val mins = safe / 60
    val secs = safe % 60
    return "%d:%02d".format(mins, secs)
}

private fun formatTimestamp(timestamp: Long): String =
    SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))

@Composable
fun HappyPlaceApp() {
    var selected by remember { mutableStateOf(0) }
    var openedChat by remember { mutableStateOf<String?>(null) }
    var activeVoiceRoom by remember { mutableStateOf<VoiceRoom?>(null) }
    var showFamilyScreen by remember { mutableStateOf(false) }
    var showFriendsScreen by remember { mutableStateOf(false) }
    var myFamilyId by remember { mutableStateOf<String?>("angkor-lotus") }
    var checkedInToday by remember { mutableStateOf(false) }
    var diamonds by remember { mutableIntStateOf(5000) }
    val peopleDirectory = remember { mutableStateListOf(*InitialPeopleDirectory.toTypedArray()) }
    val friends = remember { mutableStateListOf("Mina", "Dara", "Sophy") }
    val friendRequests = remember { mutableStateListOf("Nita", "Vannak") }

    val addFriend: (String) -> String? = { rawInput ->
        val cleaned = rawInput.trim().trimStart('@')
        if (cleaned.isEmpty()) {
            null
        } else {
            val existing = peopleDirectory.firstOrNull {
                it.name.equals(cleaned, ignoreCase = true) ||
                    it.handle.trimStart('@').equals(cleaned, ignoreCase = true)
            }
            val resolvedName = existing?.name ?: cleaned.replaceFirstChar { it.uppercase() }
            if (existing == null) {
                val slug = cleaned.lowercase().replace(Regex("[^a-z0-9]+"), ".").ifEmpty { "friend" }
                peopleDirectory.add(
                    0,
                    FriendPerson(
                        name = resolvedName,
                        handle = "@$slug",
                        vibe = "Added via friend search",
                        isOnline = true,
                        mutualCount = 0
                    )
                )
            }
            if (!friends.contains(resolvedName)) {
                friends.add(0, resolvedName)
            }
            friendRequests.remove(resolvedName)
            resolvedName
        }
    }
    val acceptFriendRequest: (String) -> Unit = { name ->
        friendRequests.remove(name)
        if (!friends.contains(name)) {
            friends.add(0, name)
        }
    }
    val declineFriendRequest: (String) -> Unit = { name ->
        friendRequests.remove(name)
    }
    val removeFriend: (String) -> Unit = { name ->
        friends.remove(name)
    }
    val families = remember {
        mutableStateListOf(
            FamilyClub(
                id = "angkor-lotus",
                name = "Angkor Lotus",
                motto = "Warm Khmer vibes, nightly music & good friends",
                emblem = "🌸",
                color = Purple,
                level = 8,
                points = 2480,
                members = listOf(
                    FamilyMember("Mina", "Leader", 860),
                    FamilyMember("Dara", "Co-Leader", 620),
                    FamilyMember("Sophy", "Member", 540),
                    FamilyMember("You", "Member", 460)
                ),
                announcements = listOf(
                    "Mina: Nightly Khmer acoustic room starts at 8 PM! 🎶",
                    "Dara: Welcome everyone to Angkor Lotus 🌸"
                )
            ),
            FamilyClub(
                id = "moonlight-crew",
                name = "Moonlight Crew",
                motto = "Late-night storytellers and cozy voice chats",
                emblem = "🌙",
                color = Color(0xFF5C9DAD),
                level = 6,
                points = 1720,
                members = listOf(
                    FamilyMember("Alex", "Leader", 710),
                    FamilyMember("Nita", "Co-Leader", 530),
                    FamilyMember("Sokha", "Member", 480)
                ),
                announcements = listOf(
                    "Alex: Cozy late-night Q&A tonight!"
                )
            ),
            FamilyClub(
                id = "happy-stars",
                name = "Happy Stars",
                motto = "Daily games, Khmer snacks & cheerful energy",
                emblem = "✨",
                color = Color(0xFFE87A5D),
                level = 5,
                points = 1350,
                members = listOf(
                    FamilyMember("Maly", "Leader", 590),
                    FamilyMember("Vannak", "Member", 410),
                    FamilyMember("Sophy", "Member", 350)
                ),
                announcements = listOf(
                    "Maly: Let's team up for Human Battle tonight!"
                )
            )
        )
    }
    val giftHistory = remember {
        val now = System.currentTimeMillis()
        mutableStateListOf(
            GiftRecord(1L, "នំអន្សមជ្រូក", "🍙", 49, false, "Sophy", now - 3_600_000L),
            GiftRecord(2L, "នំអន្សមចេក", "🫔", 29, true, "Mina", now - 7_200_000L),
            GiftRecord(3L, "នំគ្រក់", "🥥", 19, false, "Dara", now - 86_400_000L),
            GiftRecord(4L, "នំចាក់ចាន់", "🍰", 35, true, "Sophy", now - 172_800_000L)
        )
    }
    val voiceRooms = remember {
        val anchor = System.currentTimeMillis()
        mutableStateListOf(
            VoiceRoom("late-night", "Late Night Talk 🌙", "128 people · 6 speakers", RoomMood.COZY, RoomMood.COZY.cardColor, 128, listOf("Mina", "Dara", "Sokha", "Sophy", "Alex", "Nita"), backgroundId = "lounge", createdAt = anchor - 3 * 3_600_000L),
            VoiceRoom("music-chill", "Music & Chill 🎧", "64 people · 4 speakers", RoomMood.CHILL, RoomMood.CHILL.cardColor, 64, listOf("Sophy", "Maly", "Dara", "Vannak"), backgroundId = "neon", createdAt = anchor - 1 * 3_600_000L),
            VoiceRoom("khmer-friends", "Khmer Friends 🇰🇭", "32 people · 3 speakers", RoomMood.HAPPY, RoomMood.HAPPY.cardColor, 32, listOf("Sokha", "Mina", "Vannak"), backgroundId = "sunset", createdAt = anchor - 15 * 60_000L)
        )
    }
    val musicLibrary = remember {
        mutableStateListOf(*PartyMusicCatalog.toTypedArray())
    }
    var showAddMusicDialog by remember { mutableStateOf(false) }
    var reportingRoom by remember { mutableStateOf<VoiceRoom?>(null) }
    val reportedRoomIds = remember { mutableStateListOf<String>() }
    val conversations = remember {
        val now = System.currentTimeMillis()
        mutableStateMapOf(
            "Mina" to listOf(
                ChatMessage(1L, "Hi! Welcome to MY HAPPY PLACE 💜", false, now - 180_000L),
                ChatMessage(2L, "How is your day going?", false, now - 120_000L)
            ),
            "Dara" to listOf(
                ChatMessage(3L, "Hey! Ready for Voice Party later? 🎙️", false, now - 1_500_000L)
            ),
            "Sophy" to listOf(
                ChatMessage(4L, "Just sent you a Khmer dessert gift 🫔", false, now - 86_400_000L)
            ),
            "Alex" to listOf(
                ChatMessage(5L, "Nice to meet you here ✨", false, now - 86_400_000L)
            )
        )
    }
    val scope = rememberCoroutineScope()

    val sendMessage: (String, String) -> Unit = { friendName, rawText ->
        val trimmed = rawText.trim()
        if (trimmed.isNotEmpty()) {
            val currentList = conversations[friendName].orEmpty()
            val nextId = (currentList.maxOfOrNull { it.id } ?: 0L) + 1L
            conversations[friendName] = currentList + ChatMessage(
                id = nextId,
                text = trimmed,
                isFromMe = true,
                timestamp = System.currentTimeMillis()
            )
            scope.launch {
                delay(700)
                val updated = conversations[friendName].orEmpty()
                val replyId = (updated.maxOfOrNull { it.id } ?: 0L) + 1L
                val replies = listOf(
                    "Love that! 😊",
                    "Haha totally agree 💜",
                    "Tell me more!",
                    "Sounds awesome ✨"
                )
                conversations[friendName] = updated + ChatMessage(
                    id = replyId,
                    text = replies[(replyId % replies.size).toInt()],
                    isFromMe = false,
                    timestamp = System.currentTimeMillis()
                )
            }
        }
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Purple, secondary = Pink, background = Color(0xFFFFFBFF),
            surface = Color.White, onSurface = Ink
        )
    ) {
        if (openedChat != null) {
            val activeFriend = openedChat!!
            ChatScreen(
                name = activeFriend,
                messages = conversations[activeFriend].orEmpty(),
                onSendMessage = { text -> sendMessage(activeFriend, text) },
                onClearMessages = { conversations[activeFriend] = emptyList() },
                onBack = { openedChat = null }
            )
        } else if (activeVoiceRoom != null) {
            VoicePartyRoomScreen(
                room = activeVoiceRoom!!,
                diamonds = diamonds,
                musicLibrary = musicLibrary,
                onAddTrackToLibrary = { track -> musicLibrary.add(track) },
                onSendGift = { gift, recipient ->
                    diamonds -= gift.price
                    val nextId = (giftHistory.maxOfOrNull { it.id } ?: 0L) + 1L
                    giftHistory.add(
                        0,
                        GiftRecord(
                            id = nextId,
                            giftName = gift.name,
                            emoji = gift.emoji,
                            price = gift.price,
                            isSent = true,
                            peerName = recipient,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                },
                onUpdateRoomBackground = { roomId, bgId, customUri ->
                    val idx = voiceRooms.indexOfFirst { it.id == roomId }
                    if (idx >= 0) {
                        voiceRooms[idx] = voiceRooms[idx].copy(
                            backgroundId = bgId,
                            customBackgroundUri = customUri
                        )
                    }
                    if (activeVoiceRoom?.id == roomId) {
                        activeVoiceRoom = activeVoiceRoom?.copy(
                            backgroundId = bgId,
                            customBackgroundUri = customUri
                        )
                    }
                },
                onLeave = { activeVoiceRoom = null }
            )
        } else if (showFriendsScreen) {
            FriendsScreen(
                peopleDirectory = peopleDirectory,
                friends = friends,
                friendRequests = friendRequests,
                onBack = { showFriendsScreen = false },
                onAddFriend = addFriend,
                onAcceptRequest = acceptFriendRequest,
                onDeclineRequest = declineFriendRequest,
                onRemoveFriend = removeFriend,
                onChat = { friendName ->
                    showFriendsScreen = false
                    openedChat = friendName
                }
            )
        } else if (showFamilyScreen) {
            FamilyScreen(
                families = families,
                myFamilyId = myFamilyId,
                diamonds = diamonds,
                checkedInToday = checkedInToday,
                onBack = { showFamilyScreen = false },
                onJoinFamily = { familyId ->
                    val oldIdx = families.indexOfFirst { it.id == myFamilyId }
                    if (oldIdx >= 0 && myFamilyId != familyId) {
                        val old = families[oldIdx]
                        families[oldIdx] = old.copy(members = old.members.filterNot { it.name == "You" })
                    }
                    val targetIdx = families.indexOfFirst { it.id == familyId }
                    if (targetIdx >= 0) {
                        val target = families[targetIdx]
                        val updatedMembers = if (target.members.any { it.name == "You" }) {
                            target.members
                        } else {
                            target.members + FamilyMember("You", "Member", 100)
                        }
                        families[targetIdx] = target.copy(members = updatedMembers)
                        myFamilyId = familyId
                    }
                },
                onLeaveFamily = { familyId ->
                    val idx = families.indexOfFirst { it.id == familyId }
                    if (idx >= 0) {
                        val f = families[idx]
                        families[idx] = f.copy(members = f.members.filterNot { it.name == "You" })
                    }
                    if (myFamilyId == familyId) myFamilyId = null
                },
                onCreateFamily = { name, motto, emblem, color, accentColor, shape, logoUri ->
                    val oldIdx = families.indexOfFirst { it.id == myFamilyId }
                    if (oldIdx >= 0) {
                        val old = families[oldIdx]
                        families[oldIdx] = old.copy(members = old.members.filterNot { it.name == "You" })
                    }
                    val newFamily = FamilyClub(
                        id = "family-${System.currentTimeMillis()}",
                        name = name,
                        motto = motto.ifBlank { "Sharing happy moments together" },
                        emblem = emblem,
                        color = color,
                        accentColor = accentColor,
                        logoShape = shape,
                        logoImageUri = logoUri,
                        level = 1,
                        points = 100,
                        members = listOf(FamilyMember("You", "Leader", 100)),
                        announcements = listOf("You: Welcome to $name $emblem")
                    )
                    families.add(0, newFamily)
                    myFamilyId = newFamily.id
                },
                onUpdateFamilyLogo = { familyId, emblem, color, accentColor, shape, logoUri ->
                    val idx = families.indexOfFirst { it.id == familyId }
                    if (idx >= 0) {
                        val f = families[idx]
                        families[idx] = f.copy(
                            emblem = emblem,
                            color = color,
                            accentColor = accentColor,
                            logoShape = shape,
                            logoImageUri = logoUri
                        )
                    }
                },
                onCheckIn = { familyId ->
                    if (!checkedInToday) {
                        checkedInToday = true
                        diamonds += 25
                        val idx = families.indexOfFirst { it.id == familyId }
                        if (idx >= 0) {
                            val f = families[idx]
                            families[idx] = f.copy(points = f.points + 50)
                        }
                    }
                },
                onPostAnnouncement = { familyId, text ->
                    val idx = families.indexOfFirst { it.id == familyId }
                    if (idx >= 0 && text.isNotBlank()) {
                        val f = families[idx]
                        families[idx] = f.copy(announcements = listOf("You: ${text.trim()}") + f.announcements)
                    }
                },
                onStartFamilyParty = { family ->
                    val speakers = family.members.map { it.name }.distinct().take(6)
                    val partyRoom = VoiceRoom(
                        id = "family-room-${family.id}",
                        title = "${family.name} Hangout ${family.emblem}",
                        detail = "${family.members.size} members · ${speakers.size} speakers",
                        mood = RoomMood.HAPPY,
                        color = family.color,
                        listeners = family.members.size + 4,
                        speakers = speakers,
                        owner = family.members.firstOrNull()?.name ?: "You"
                    )
                    if (voiceRooms.none { it.id == partyRoom.id }) {
                        voiceRooms.add(0, partyRoom)
                    }
                    activeVoiceRoom = partyRoom
                },
                onChatMember = { memberName ->
                    if (memberName != "You") {
                        openedChat = memberName
                    }
                }
            )
        } else {
            val currentFamily = families.firstOrNull { it.id == myFamilyId }
            Scaffold(
                containerColor = Color(0xFFFFFBFF),
                bottomBar = {
                    NavigationBar(containerColor = Color.White) {
                        val tabs = listOf(
                            "Home" to Icons.Default.Home,
                            "Match" to Icons.Default.Favorite,
                            "Party" to Icons.Default.Headphones,
                            "Chat" to Icons.Default.ChatBubble,
                            "Me" to Icons.Default.Person,
                            "Game" to Icons.Default.EmojiEvents,
                            "Gifts" to Icons.Default.CardGiftcard
                        )
                        tabs.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = selected == index,
                                onClick = { selected = index },
                                icon = { Icon(item.second, contentDescription = item.first) },
                                label = { Text(item.first, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            ) { padding ->
                Box(Modifier.padding(padding)) {
                    when (selected) {
                        0 -> HomeScreen(
                            rooms = voiceRooms.take(2),
                            currentFamily = currentFamily,
                            musicCount = musicLibrary.size,
                            friends = friends,
                            friendRequestCount = friendRequests.size,
                            reportedRoomIds = reportedRoomIds,
                            onChat = { openedChat = it },
                            onJoinRoom = { activeVoiceRoom = it },
                            onReportRoom = { reportingRoom = it },
                            onOpenFamily = { showFamilyScreen = true },
                            onOpenFriends = { showFriendsScreen = true },
                            onOpenMatch = { selected = 1 },
                            onAddFriend = { addFriend(it) },
                            onRemoveFriend = { removeFriend(it) },
                            onOpenAddMusic = { showAddMusicDialog = true }
                        )
                        1 -> MatchScreen(
                            friends = friends,
                            onAddFriend = { addFriend(it) },
                            onChat = { openedChat = it }
                        )
                        2 -> PartyScreen(
                            rooms = voiceRooms,
                            musicCount = musicLibrary.size,
                            reportedRoomIds = reportedRoomIds,
                            onJoinRoom = { activeVoiceRoom = it },
                            onReportRoom = { reportingRoom = it },
                            onOpenAddMusic = { showAddMusicDialog = true },
                            onCreateRoom = { title, mood, backgroundId ->
                                val created = VoiceRoom(
                                    id = "room-${System.currentTimeMillis()}",
                                    title = title,
                                    detail = "1 person · 1 speaker",
                                    mood = mood,
                                    color = mood.cardColor,
                                    listeners = 1,
                                    speakers = listOf("You"),
                                    owner = "You",
                                    backgroundId = backgroundId
                                )
                                voiceRooms.add(0, created)
                                activeVoiceRoom = created
                            }
                        )
                        3 -> ChatListScreen(
                            friends = friends,
                            conversations = conversations,
                            onOpenFriends = { showFriendsScreen = true },
                            onChat = { openedChat = it }
                        )
                        4 -> ProfileScreen(
                            giftHistory = giftHistory,
                            currentFamily = currentFamily,
                            friendsCount = friends.size,
                            friendRequestCount = friendRequests.size,
                            onOpenFriends = { showFriendsScreen = true },
                            onOpenFamily = { showFamilyScreen = true }
                        )
                        5 -> HumanBattleScreen()
                        else -> KhmerGiftShopScreen(
                            diamonds = diamonds,
                            onSendGift = { gift, recipient ->
                                diamonds -= gift.price
                                val nextId = (giftHistory.maxOfOrNull { it.id } ?: 0L) + 1L
                                giftHistory.add(
                                    0,
                                    GiftRecord(
                                        id = nextId,
                                        giftName = gift.name,
                                        emoji = gift.emoji,
                                        price = gift.price,
                                        isSent = true,
                                        peerName = recipient,
                                        timestamp = System.currentTimeMillis()
                                    )
                                )
                            }
                        )
                    }
                }
            }
            if (showAddMusicDialog) {
                AddMusicDialog(
                    onDismiss = { showAddMusicDialog = false },
                    onAddTrack = { track, openParty ->
                        musicLibrary.add(track)
                        showAddMusicDialog = false
                        if (openParty) {
                            val firstRoom = voiceRooms.firstOrNull()
                            if (firstRoom != null) {
                                activeVoiceRoom = firstRoom
                            } else {
                                selected = 2
                            }
                        }
                    }
                )
            }
            if (reportingRoom != null) {
                ReportRoomDialog(
                    room = reportingRoom!!,
                    onDismiss = { reportingRoom = null },
                    onSubmit = {
                        val id = reportingRoom!!.id
                        if (!reportedRoomIds.contains(id)) {
                            reportedRoomIds.add(id)
                        }
                        reportingRoom = null
                    }
                )
            }
        }
    }
}

@Composable
fun Header(title: String, subtitle: String? = null) {
    Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(title, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
        if (subtitle != null) Text(subtitle, color = Muted, fontSize = 13.sp)
    }
}

@Composable
private fun AddMusicDialog(
    onDismiss: () -> Unit,
    onAddTrack: (PartyTrack, Boolean) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf(RoomMood.MUSIC) }
    var audioUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var fileName by remember { mutableStateOf<String?>(null) }
    val audioPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            audioUri = uri
            val resolved = resolveAudioDisplayName(context, uri)
            fileName = resolved
            if (title.isBlank()) {
                title = stripAudioExtension(resolved)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Music 🎶", fontWeight = FontWeight.Bold, color = Ink) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Lavender)
                        .clickable { audioPicker.launch("audio/*") }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Purple),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            fileName ?: "Upload audio file",
                            color = Ink,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            if (fileName != null) "Tap to change audio file" else "Optional · MP3, WAV, M4A",
                            color = Muted,
                            fontSize = 11.sp
                        )
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Song title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    placeholder = { Text("Artist (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RoomMood.entries.forEach { option ->
                        val active = mood == option
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (active) option.cardColor else Lavender)
                                .clickable { mood = option }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(option.label, color = if (active) Color.White else Ink, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TextButton(
                    enabled = title.trim().isNotEmpty(),
                    onClick = {
                        onAddTrack(
                            PartyTrack(
                                id = "custom-${System.currentTimeMillis()}",
                                title = title.trim(),
                                artist = artist.trim().ifEmpty { "Added by You" },
                                vibe = mood.label,
                                mood = mood,
                                durationSeconds = 180,
                                color = mood.cardColor,
                                audioUri = audioUri,
                                fileName = fileName
                            ),
                            false
                        )
                    }
                ) {
                    Text("Save")
                }
                Button(
                    enabled = title.trim().isNotEmpty(),
                    onClick = {
                        onAddTrack(
                            PartyTrack(
                                id = "custom-${System.currentTimeMillis()}",
                                title = title.trim(),
                                artist = artist.trim().ifEmpty { "Added by You" },
                                vibe = mood.label,
                                mood = mood,
                                durationSeconds = 180,
                                color = mood.cardColor,
                                audioUri = audioUri,
                                fileName = fileName
                            ),
                            true
                        )
                    }
                ) {
                    Text("Add & Play")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ReportRoomDialog(
    room: VoiceRoom,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    val reasons = listOf(
        "Inappropriate language",
        "Harassment or bullying",
        "Spam or scam",
        "Unsafe or explicit content",
        "Other"
    )
    var selectedReason by remember { mutableStateOf(reasons.first()) }
    var details by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report Room", fontWeight = FontWeight.Bold, color = Ink) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Flagging: ${room.title}", color = Muted, fontSize = 12.sp)
                reasons.forEach { reason ->
                    val active = selectedReason == reason
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (active) Purple else Lavender)
                            .clickable { selectedReason = reason }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(reason, color = if (active) Color.White else Ink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        if (active) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    placeholder = { Text("Additional details (optional)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5C8A))
            ) {
                Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Submit Report", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun HomeScreen(
    rooms: List<VoiceRoom>,
    currentFamily: FamilyClub?,
    musicCount: Int,
    friends: List<String>,
    friendRequestCount: Int,
    reportedRoomIds: List<String>,
    onChat: (String) -> Unit,
    onJoinRoom: (VoiceRoom) -> Unit,
    onReportRoom: (VoiceRoom) -> Unit,
    onOpenFamily: () -> Unit,
    onOpenFriends: () -> Unit,
    onOpenMatch: () -> Unit,
    onAddFriend: (String) -> Unit,
    onRemoveFriend: (String) -> Unit,
    onOpenAddMusic: () -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("MY HAPPY PLACE", color = Purple, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("Find your happy people ✨", color = Ink, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
                }
                Box(Modifier.size(44.dp).clip(CircleShape).background(Lavender), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Notifications, null, tint = Purple)
                }
            }
        }
        item {
            Box(Modifier.fillMaxWidth().padding(horizontal = 18.dp).clip(RoundedCornerShape(26.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF9C6BFA), Color(0xFFFF8BB8))))
                .padding(20.dp)) {
                Column {
                    Text("A little place for big connections 💜", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("Meet someone new today.", color = Color.White.copy(alpha = .9f))
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onOpenMatch, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                            Text("Explore friends", color = Purple, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onOpenFriends,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.22f))
                        ) {
                            Icon(Icons.Default.PersonAdd, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Add friends", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Lavender)
                    .clickable { onOpenFriends() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(Pink, Purple))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White)
                }
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text("Friends & Requests", color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    Text(
                        if (friendRequestCount > 0) {
                            "${friends.size} friends · $friendRequestCount pending requests"
                        } else {
                            "${friends.size} friends · Find people by name or handle"
                        },
                        color = Muted,
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Purple)
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text("+ Add friends", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Lavender)
                    .clickable { onOpenFamily() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FamilyLogoBadge(
                    emblem = currentFamily?.emblem ?: "🏠",
                    color = currentFamily?.color ?: Purple,
                    accentColor = currentFamily?.accentColor ?: Pink,
                    shape = currentFamily?.logoShape ?: FamilyLogoShape.CIRCLE,
                    logoImageUri = currentFamily?.logoImageUri,
                    sizeDp = 48
                )
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(
                        currentFamily?.name ?: "Join a Family",
                        color = Ink,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    Text(
                        if (currentFamily != null) {
                            "Level ${currentFamily.level} · ${currentFamily.members.size} members · ${currentFamily.points} pts"
                        } else {
                            "Find your community or start your own crew"
                        },
                        color = Muted,
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Purple)
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text("Family", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Lavender)
                    .clickable { onOpenAddMusic() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF5B6EE1), Purple))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White)
                }
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text("Party Music Library", color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    Text("$musicCount songs ready · Upload audio or add tracks", color = Muted, fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF5B6EE1))
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text("+ Add music", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
        item { SectionTitle("People you may like", "See all", onActionClick = onOpenFriends) }
        items(listOf("Mina", "Dara", "Sophy", "Alex")) { name ->
            val isFriend = friends.contains(name)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChat(name) }
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Pink, Purple))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Column(Modifier.weight(1f).padding(start = 12.dp, end = 8.dp)) {
                    Text(name, color = Ink, fontWeight = FontWeight.Bold)
                    Text(
                        if (isFriend) "Friend · Online" else "Online · Say hello",
                        color = Muted,
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isFriend) Lavender else Purple)
                        .clickable { if (isFriend) onRemoveFriend(name) else onAddFriend(name) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isFriend) "Friends" else "Add friend",
                        color = if (isFriend) Purple else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = { onChat(name) }) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Message $name", tint = Purple)
                }
            }
        }
        item { SectionTitle("Popular rooms", "View all") }
        items(rooms, key = { it.id }) { room ->
            RoomCard(
                title = room.title,
                detail = room.detail,
                owner = room.owner,
                mood = room.mood,
                color = room.color,
                isReported = reportedRoomIds.contains(room.id),
                onClick = { onJoinRoom(room) },
                onReport = { onReportRoom(room) }
            )
        }
    }
}

@Composable
fun SectionTitle(title: String, action: String, onActionClick: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontWeight = FontWeight.Bold, color = Ink, fontSize = 17.sp)
        Text(
            action,
            color = Purple,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = if (onActionClick != null) Modifier.clickable { onActionClick() } else Modifier
        )
    }
}

@Composable
fun PersonRow(name: String, detail: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 20.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(50.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Pink, Purple))),
            contentAlignment = Alignment.Center) {
            Text(name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(name, color = Ink, fontWeight = FontWeight.Bold)
            Text(detail, color = Muted, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChatBubbleOutline, null, tint = Purple)
    }
}

@Composable
private fun AvatarBadge(
    name: String,
    size: androidx.compose.ui.unit.Dp = 20.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 10.sp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(Pink, Purple)))
            .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            name.take(1).uppercase(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize
        )
    }
}

@Composable
private fun RoomCard(
    title: String,
    detail: String,
    owner: String = "Host",
    mood: RoomMood,
    color: Color,
    isReported: Boolean = false,
    onClick: () -> Unit = {},
    onReport: () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(color, mood.gradientEndColor)))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Headphones, null, tint = Color.White, modifier = Modifier.size(30.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    mood.icon,
                    contentDescription = mood.label,
                    tint = color,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
        Column(
            Modifier.weight(1f).padding(start = 12.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                "$detail · ${mood.label}",
                color = Color.White.copy(alpha = .85f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AvatarBadge(name = owner)
                Text(
                    owner,
                    color = Color.White.copy(alpha = .95f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("JOIN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isReported) Color(0xFFFF5C8A) else Color.Black.copy(alpha = 0.22f))
                    .clickable { onReport() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Flag, contentDescription = "Report Room", tint = Color.White, modifier = Modifier.size(12.dp))
                Text(
                    if (isReported) "Reported" else "Report Room",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun MatchScreen(
    friends: List<String> = emptyList(),
    onAddFriend: (String) -> Unit = {},
    onChat: (String) -> Unit = {}
) {
    var matched by remember { mutableStateOf<String?>(null) }
    val candidates = listOf("Mina", "Dara", "Sophy", "Alex", "Nita", "Vannak")
    val isFriend = matched != null && friends.contains(matched)

    Column(Modifier.fillMaxSize().background(Lavender), horizontalAlignment = Alignment.CenterHorizontally) {
        Header("Soul Match", "A new friend could be one tap away")
        Spacer(Modifier.weight(1f))
        Box(Modifier.size(190.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Pink, Purple))),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Favorite, null, tint = Color.White, modifier = Modifier.size(82.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text(
            if (matched != null) "Matched with $matched 💜" else "Ready to meet someone?",
            color = Ink,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text("Find a person to share a little moment with.", color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { matched = candidates.random() },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth()
        ) {
            Icon(Icons.Default.AutoAwesome, null)
            Spacer(Modifier.width(8.dp))
            Text("Start matching")
        }
        if (matched != null) {
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { onAddFriend(matched!!) },
                    enabled = !isFriend,
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Icon(
                        if (isFriend) Icons.Default.Check else Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(if (isFriend) "Friends" else "Add friend", color = Ink, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onChat(matched!!) },
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Chat", color = Purple, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun EmptyVoiceRoomsPlaceholder(
    moodFilter: RoomMood?,
    searchQuery: String,
    onResetAll: () -> Unit,
    onCreateRoom: () -> Unit
) {
    val accent = moodFilter?.cardColor ?: Purple
    val icon = moodFilter?.icon ?: Icons.Default.Headphones
    val trimmedQuery = searchQuery.trim()
    val description = when {
        moodFilter != null && trimmedQuery.isNotEmpty() ->
            "No ${moodFilter.label.lowercase()} rooms matched \"$trimmedQuery\" by title or host. Tap All to browse every live hangout."
        moodFilter != null ->
            "No ${moodFilter.label.lowercase()} voice rooms are live right now. Tap All to view every room or start a ${moodFilter.label.lowercase()} room."
        trimmedQuery.isNotEmpty() ->
            "No live rooms matched \"$trimmedQuery\" by title or host name. Tap All to clear filters and explore active rooms."
        else ->
            "There are no active voice rooms matching this vibe right now. Tap All to browse or start a new space."
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Purple.copy(alpha = 0.2f)),
        shadowElevation = 2.dp,
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Lavender, Color.White, Color(0xFFFFF0F6))))
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "No rooms found for this mood",
                color = Ink,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                description,
                color = Muted,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onResetAll,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple)
                ) {
                    Text("All", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                OutlinedButton(
                    onClick = onCreateRoom,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp),
                    border = BorderStroke(1.dp, Purple.copy(alpha = 0.25f))
                ) {
                    Text("Create room", color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun PartyScreen(
    rooms: List<VoiceRoom>,
    musicCount: Int,
    reportedRoomIds: List<String>,
    onJoinRoom: (VoiceRoom) -> Unit,
    onReportRoom: (VoiceRoom) -> Unit,
    onOpenAddMusic: () -> Unit,
    onCreateRoom: (String, RoomMood, String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newRoomTitle by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf(RoomMood.HAPPY) }
    var selectedBg by remember { mutableStateOf("lounge") }
    var moodFilter by remember { mutableStateOf<RoomMood?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf(PartySortOrder.POPULAR) }
    var showSortMenu by remember { mutableStateOf(false) }

    val trimmedQuery = searchQuery.trim()
    val filteredRooms = rooms
        .filter { room ->
            val matchesMood = moodFilter == null || room.mood == moodFilter
            if (!matchesMood) return@filter false
            if (trimmedQuery.isEmpty()) return@filter true
            room.title.contains(trimmedQuery, ignoreCase = true) ||
                room.owner.contains(trimmedQuery, ignoreCase = true)
        }
        .let { list ->
            when (sortOrder) {
                PartySortOrder.POPULAR -> list.sortedWith(compareByDescending<VoiceRoom> { it.listeners }.thenByDescending { it.createdAt })
                PartySortOrder.NEWEST -> list.sortedWith(compareByDescending<VoiceRoom> { it.createdAt }.thenByDescending { it.listeners })
            }
        }

    val lobbyWashColors = if (moodFilter != null) {
        listOf(
            moodFilter!!.cardColor.copy(alpha = 0.14f),
            moodFilter!!.gradientEndColor.copy(alpha = 0.06f),
            Color.Transparent
        )
    } else {
        listOf(
            Purple.copy(alpha = 0.10f),
            Pink.copy(alpha = 0.05f),
            Color.Transparent
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(lobbyWashColors))
    ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.weight(1f)) {
                    Header("Voice Party 🎙️", "Join a room · $musicCount tracks ready")
                }
                Button(
                    onClick = onOpenAddMusic,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B6EE1)),
                    shape = RoundedCornerShape(22.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Add music", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search title or host...", fontSize = 13.sp, color = Muted, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search rooms", tint = Purple)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search", tint = Muted)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple,
                        unfocusedBorderColor = Purple.copy(alpha = 0.22f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Lavender.copy(alpha = 0.7f)
                    )
                )
                Box {
                    Row(
                        modifier = Modifier
                            .defaultMinSize(minHeight = 52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Lavender.copy(alpha = 0.85f))
                            .border(1.dp, Purple.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
                            .clickable { showSortMenu = true }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.SwapVert, contentDescription = "Sort rooms", tint = Purple, modifier = Modifier.size(16.dp))
                        Text(
                            sortOrder.label,
                            color = Ink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Muted, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        PartySortOrder.entries.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        option.label,
                                        fontWeight = if (sortOrder == option) FontWeight.Bold else FontWeight.Medium,
                                        color = if (sortOrder == option) Purple else Ink
                                    )
                                },
                                leadingIcon = {
                                    if (sortOrder == option) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Purple, modifier = Modifier.size(18.dp))
                                    }
                                },
                                onClick = {
                                    sortOrder = option
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val allSelected = moodFilter == null
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (allSelected) Purple else Lavender)
                        .clickable { moodFilter = null }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "All",
                        color = if (allSelected) Color.White else Ink,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
                RoomMood.entries.forEach { mood ->
                    val selected = moodFilter == mood
                    Row(
                        modifier = Modifier
                            .defaultMinSize(minHeight = 44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (selected) mood.cardColor else Lavender)
                            .clickable { moodFilter = if (selected) null else mood }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            mood.icon,
                            contentDescription = null,
                            tint = if (selected) Color.White else mood.cardColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            mood.label,
                            color = if (selected) Color.White else Ink,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        if (filteredRooms.isEmpty()) {
            item {
                EmptyVoiceRoomsPlaceholder(
                    moodFilter = moodFilter,
                    searchQuery = searchQuery,
                    onResetAll = {
                        moodFilter = null
                        searchQuery = ""
                    },
                    onCreateRoom = {
                        if (moodFilter != null) selectedMood = moodFilter!!
                        showCreateDialog = true
                    }
                )
            }
        } else {
            items(filteredRooms, key = { it.id }) { room ->
                RoomCard(
                    title = room.title,
                    detail = room.detail,
                    owner = room.owner,
                    mood = room.mood,
                    color = room.color,
                    isReported = reportedRoomIds.contains(room.id),
                    onClick = { onJoinRoom(room) },
                    onReport = { onReportRoom(room) }
                )
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.weight(1f).defaultMinSize(minHeight = 48.dp)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Create a room")
                }
                OutlinedButton(
                    onClick = onOpenAddMusic,
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Icon(Icons.Default.MusicNote, null, tint = Color(0xFF5B6EE1))
                    Spacer(Modifier.width(6.dp))
                    Text("Add music", color = Ink)
                }
            }
            Text(
                "Voice rooms are demo screens. Live audio needs a voice service connection.",
                color = Muted,
                fontSize = 11.sp,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Voice Room", fontWeight = FontWeight.Bold, color = Ink) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newRoomTitle,
                        onValueChange = { newRoomTitle = it },
                        placeholder = { Text("Room name (e.g. Chill Vibes ☕)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Pick a mood", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Ink)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        RoomMood.entries.chunked(2).forEach { rowMoods ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowMoods.forEach { mood ->
                                    val isSelected = selectedMood == mood
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .defaultMinSize(minHeight = 44.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isSelected) mood.cardColor else Lavender)
                                            .clickable { selectedMood = mood }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            mood.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else mood.cardColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            mood.label,
                                            color = if (isSelected) Color.White else Ink,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                                if (rowMoods.size == 1) {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    Text("Room background", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Ink)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PartyBackgroundCatalog.forEach { preset ->
                            val isBgSelected = selectedBg == preset.id
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isBgSelected) Lavender else Color.White)
                                    .border(
                                        width = if (isBgSelected) 1.5.dp else 1.dp,
                                        color = if (isBgSelected) Purple else Lavender,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedBg = preset.id }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Brush.linearGradient(preset.colors))
                                ) {
                                    if (preset.drawableRes != null) {
                                        Image(
                                            painter = painterResource(id = preset.drawableRes),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                                Text(
                                    preset.label,
                                    color = Ink,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = newRoomTitle.trim()
                        if (trimmed.isNotEmpty()) {
                            onCreateRoom(trimmed, selectedMood, selectedBg)
                            newRoomTitle = ""
                            selectedMood = RoomMood.HAPPY
                            selectedBg = "lounge"
                            showCreateDialog = false
                        }
                    }
                ) {
                    Text("Join Room")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun VoicePartyRoomScreen(
    room: VoiceRoom,
    diamonds: Int,
    musicLibrary: List<PartyTrack>,
    onAddTrackToLibrary: (PartyTrack) -> Unit,
    onSendGift: (KhmerGift, String) -> Unit,
    onUpdateRoomBackground: (String, String, android.net.Uri?) -> Unit,
    onLeave: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var onStage by remember { mutableStateOf(room.speakers.contains("You")) }
    var isMuted by remember { mutableStateOf(true) }
    var roomInput by remember { mutableStateOf("") }
    var lastReaction by remember { mutableStateOf<String?>(null) }
    var showGiftSheet by remember { mutableStateOf(false) }
    var showBgSheet by remember { mutableStateOf(false) }
    var selectedGift by remember { mutableStateOf(KhmerGiftCatalog.first()) }
    val giftRecipients = remember(room.speakers) {
        room.speakers.filter { it != "You" }.ifEmpty { listOf("Mina", "Dara", "Sophy") }
    }
    var selectedRecipient by remember(giftRecipients) { mutableStateOf(giftRecipients.first()) }
    var lastGiftBanner by remember { mutableStateOf<String?>(null) }

    val roomChat = remember(room.id) {
        mutableStateListOf(
            "Mina" to "Welcome to ${room.title}! 💜",
            "Dara" to "Grab a mic seat, queue music, or customize the room background 🎙️🎨"
        )
    }

    val bgPhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            onUpdateRoomBackground(room.id, "custom", uri)
            roomChat.add("You" to "🖼️ Updated room background photo")
            showBgSheet = false
        }
    }

    val musicQueue = remember(room.id) {
        mutableStateListOf(*musicLibrary.toTypedArray())
    }
    LaunchedEffect(musicLibrary.size) {
        musicLibrary.forEach { libTrack ->
            if (musicQueue.none { it.id == libTrack.id }) {
                musicQueue.add(libTrack)
            }
        }
    }

    var currentTrackIndex by remember(room.id) {
        mutableIntStateOf(
            musicLibrary.indexOfFirst { it.mood == room.mood }.let { if (it >= 0) it else 0 }
        )
    }
    var isMusicPlaying by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var musicVolume by remember { mutableFloatStateOf(0.65f) }
    var isMusicMuted by remember { mutableStateOf(false) }
    var showMusicSheet by remember { mutableStateOf(false) }
    var customTrackTitle by remember { mutableStateOf("") }
    var customTrackArtist by remember { mutableStateOf("") }
    var customTrackMood by remember { mutableStateOf(room.mood) }
    var customAudioUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var customFileName by remember { mutableStateOf<String?>(null) }

    val audioPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            customAudioUri = uri
            val resolved = resolveAudioDisplayName(context, uri)
            customFileName = resolved
            if (customTrackTitle.isBlank()) {
                customTrackTitle = stripAudioExtension(resolved)
            }
        }
    }

    val currentTrack = musicQueue.getOrElse(currentTrackIndex) { PartyMusicCatalog.first() }
    var mediaPlayer by remember { mutableStateOf<android.media.MediaPlayer?>(null) }

    DisposableEffect(currentTrack.id) {
        mediaPlayer?.release()
        mediaPlayer = null
        val uri = currentTrack.audioUri
        if (uri != null) {
            val mp = runCatching {
                android.media.MediaPlayer().apply {
                    setDataSource(context, uri)
                    prepare()
                    val vol = if (isMusicMuted) 0f else musicVolume
                    setVolume(vol, vol)
                    setOnCompletionListener {
                        currentTrackIndex = (currentTrackIndex + 1) % musicQueue.size
                        elapsedSeconds = 0
                    }
                    if (isMusicPlaying) start()
                }
            }.getOrNull()
            mediaPlayer = mp
            if (mp != null && mp.duration > 1000) {
                val realSec = mp.duration / 1000
                val idx = musicQueue.indexOfFirst { it.id == currentTrack.id }
                if (idx >= 0) {
                    musicQueue[idx] = musicQueue[idx].copy(durationSeconds = realSec)
                }
            }
        }
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    LaunchedEffect(musicVolume, isMusicMuted, mediaPlayer) {
        val vol = if (isMusicMuted) 0f else musicVolume
        runCatching { mediaPlayer?.setVolume(vol, vol) }
    }

    LaunchedEffect(isMusicPlaying, mediaPlayer) {
        val mp = mediaPlayer ?: return@LaunchedEffect
        runCatching {
            if (isMusicPlaying && !mp.isPlaying) mp.start()
            else if (!isMusicPlaying && mp.isPlaying) mp.pause()
        }
    }

    LaunchedEffect(isMusicPlaying, currentTrack.id) {
        if (isMusicPlaying) {
            while (true) {
                delay(1000L)
                val mp = mediaPlayer
                if (mp != null) {
                    val posSec = runCatching { mp.currentPosition / 1000 }.getOrDefault(elapsedSeconds)
                    elapsedSeconds = posSec
                } else if (elapsedSeconds + 1 >= currentTrack.durationSeconds) {
                    currentTrackIndex = (currentTrackIndex + 1) % musicQueue.size
                    elapsedSeconds = 0
                } else {
                    elapsedSeconds += 1
                }
            }
        }
    }

    val displayedSpeakers = remember(room.speakers, onStage) {
        if (onStage && !room.speakers.contains("You")) {
            listOf("You") + room.speakers
        } else {
            room.speakers
        }
    }

    val activeBgPreset = PartyBackgroundCatalog.firstOrNull { it.id == room.backgroundId } ?: PartyBackgroundCatalog[1]
    val hasCustomPhoto = room.backgroundId == "custom" && room.customBackgroundUri != null
    val hasImageBackdrop = hasCustomPhoto || activeBgPreset.drawableRes != null
    val bgGradientColors = if (room.backgroundId == "mood") moodRoomGradientColors(room.mood) else activeBgPreset.colors

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(bgGradientColors))
        )
        if (hasCustomPhoto) {
            AsyncImage(
                model = room.customBackgroundUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (activeBgPreset.drawableRes != null) {
            Image(
                painter = painterResource(id = activeBgPreset.drawableRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        if (hasImageBackdrop) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xBF120D22),
                                Color(0x8C16102B),
                                Color(0xD9120D22)
                            )
                        )
                    )
            )
        }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x73140E26))
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onLeave) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(room.mood.cardColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        room.mood.icon,
                        contentDescription = room.mood.label,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column(Modifier.weight(1f).padding(start = 10.dp)) {
                    Text(room.title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${room.listeners + 1} listening · ${room.mood.label} · 💎 $diamonds", color = Color(0xFFCBBEE6), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Row(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x2EFFFFFF))
                        .clickable { showBgSheet = true }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Palette, contentDescription = "Room background", tint = Color.White, modifier = Modifier.size(15.dp))
                    Text("Bg", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
                Button(
                    onClick = onLeave,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5C8A)),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Leave", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xD91E1638))
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = roomInput,
                        onValueChange = { roomInput = it },
                        placeholder = { Text("Say something in room...", color = Color(0xFFA799C4), fontSize = 13.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilledIconButton(
                        onClick = {
                            val trimmed = roomInput.trim()
                            if (trimmed.isNotEmpty()) {
                                roomChat.add("You" to trimmed)
                                roomInput = ""
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Purple)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (!onStage) {
                                onStage = true
                                isMuted = false
                            } else {
                                isMuted = !isMuted
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (onStage && !isMuted) Purple else Color(0xFF392E5C)
                        ),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            if (onStage && !isMuted) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            if (!onStage) "Take Mic" else if (isMuted) "Unmute" else "Mic Live",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    if (onStage) {
                        OutlinedButton(
                            onClick = {
                                onStage = false
                                isMuted = true
                            },
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Text("Leave Mic", color = Color.White, fontSize = 12.sp)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF5B6EE1))
                            .clickable { showMusicSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MusicNote, contentDescription = "Party Music", tint = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD16B))
                            .clickable { showGiftSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = "Send Gift", tint = Color(0xFF24152F))
                    }
                    listOf("❤️", "👏", "🔥").forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF392E5C))
                                .clickable {
                                    lastReaction = emoji
                                    roomChat.add("You" to "Sent $emoji")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            if (lastGiftBanner != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF68468E), Color(0xFF3F2B63))))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(lastGiftBanner!!, color = Color(0xFFFFD77A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(
                        "✕",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { lastGiftBanner = null }
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xCC22193D))
                    .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(16.dp))
                    .clickable { showMusicSheet = true }
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(currentTrack.color),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            currentTrack.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "${currentTrack.artist} · ${currentTrack.vibe} · ${formatTrackTime(elapsedSeconds)} / ${formatTrackTime(currentTrack.durationSeconds)}",
                            color = Color(0xFFCBBEE6),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = { isMusicPlaying = !isMusicPlaying },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Purple)
                    ) {
                        Icon(
                            if (isMusicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isMusicPlaying) "Pause" else "Play",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = {
                            currentTrackIndex = (currentTrackIndex + 1) % musicQueue.size
                            elapsedSeconds = 0
                            isMusicPlaying = true
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF392E5C))
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next track", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (elapsedSeconds.toFloat() / currentTrack.durationSeconds.coerceAtLeast(1)).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                    color = currentTrack.color,
                    trackColor = Color.White.copy(alpha = 0.12f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Speakers on stage", color = Color(0xFFFFD77A), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                if (lastReaction != null) {
                    Text("Reaction: $lastReaction", color = Pink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth().heightIn(max = 260.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayedSpeakers) { speaker ->
                    val isMe = speaker == "You"
                    val speaking = if (isMe) onStage && !isMuted else speaker == displayedSpeakers.firstOrNull()
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xCC22193D))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                            .padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            if (speaking) listOf(Pink, Purple) else listOf(Color(0xFF56477E), Color(0xFF3C315D))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(speaker.take(1), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(if (speaking) Color(0xFF22C55E) else Color(0xFF1A142D)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (speaking) Icons.Default.Mic else Icons.Default.MicOff,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            speaker,
                            color = if (isMe) Color(0xFFFFD77A) else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            if (speaking) "Speaking" else "Listening",
                            color = Color(0xFFCBBEE6),
                            fontSize = 10.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Room chat", color = Color(0xFFCBBEE6), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(roomChat) { entry ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xCC20183A))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("${entry.first}: ", color = Pink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(entry.second, color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    if (showGiftSheet) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x990A0712))
                .clickable { showGiftSheet = false },
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                    .background(Color(0xFF211938))
                    .clickable(enabled = false) {}
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Send Party Gift 🎁", color = Color(0xFFFFD68A), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Text("💎 $diamonds", color = Color(0xFF70D9FF), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("To:", color = Color(0xFFCBBEE6), fontSize = 12.sp)
                    giftRecipients.take(4).forEach { person ->
                        val active = selectedRecipient == person
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (active) Color(0xFFFFD16B) else Color(0xFF392E5C))
                                .clickable { selectedRecipient = person }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                person,
                                color = if (active) Color(0xFF24152F) else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth().height(190.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(KhmerGiftCatalog) { gift ->
                        val active = selectedGift == gift
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (active) Color(0xFF594078) else Color(0xFF2C2347))
                                .clickable { selectedGift = gift }
                                .padding(8.dp)
                        ) {
                            Text(gift.emoji, fontSize = 26.sp)
                            Text(gift.name, color = Color.White, fontSize = 10.sp, maxLines = 1)
                            Text("💎 ${gift.price}", color = Color(0xFF71D9FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (diamonds >= selectedGift.price) {
                            onSendGift(selectedGift, selectedRecipient)
                            roomChat.add("You" to "Sent ${selectedGift.emoji} ${selectedGift.name} to $selectedRecipient!")
                            lastGiftBanner = "🎉 Sent ${selectedGift.emoji} ${selectedGift.name} to $selectedRecipient!"
                            showGiftSheet = false
                        } else {
                            lastGiftBanner = "ពេជ្រមិនគ្រប់គ្រាន់ទេ!"
                            showGiftSheet = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CardGiftcard, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Send ${selectedGift.name} (💎 ${selectedGift.price})", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showMusicSheet) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x990A0712))
                .clickable { showMusicSheet = false },
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 620.dp)
                    .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                    .background(Color(0xFF211938))
                    .clickable(enabled = false) {}
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Party Music Jukebox 🎶", color = Color(0xFFFFD68A), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Text(
                            if (isMusicPlaying) "Playing live in room" else "Paused · Tap play to start tunes",
                            color = Color(0xFFCBBEE6),
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = { showMusicSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF2C2347))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(currentTrack.color),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(currentTrack.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${currentTrack.artist} · ${currentTrack.vibe}", color = Color(0xFFCBBEE6), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    Slider(
                        value = elapsedSeconds.toFloat(),
                        onValueChange = {
                            val targetSec = it.toInt()
                            elapsedSeconds = targetSec
                            runCatching { mediaPlayer?.seekTo(targetSec * 1000) }
                        },
                        valueRange = 0f..currentTrack.durationSeconds.coerceAtLeast(1).toFloat(),
                        colors = SliderDefaults.colors(thumbColor = Purple, activeTrackColor = Purple)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatTrackTime(elapsedSeconds), color = Color(0xFFCBBEE6), fontSize = 11.sp)
                        Text(formatTrackTime(currentTrack.durationSeconds), color = Color(0xFFCBBEE6), fontSize = 11.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (elapsedSeconds > 4) {
                                        elapsedSeconds = 0
                                        runCatching { mediaPlayer?.seekTo(0) }
                                    } else {
                                        currentTrackIndex = (currentTrackIndex - 1 + musicQueue.size) % musicQueue.size
                                        elapsedSeconds = 0
                                        isMusicPlaying = true
                                    }
                                },
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF392E5C))
                            ) {
                                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White)
                            }
                            IconButton(
                                onClick = { isMusicPlaying = !isMusicPlaying },
                                modifier = Modifier.size(46.dp).clip(CircleShape).background(Purple)
                            ) {
                                Icon(
                                    if (isMusicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isMusicPlaying) "Pause" else "Play",
                                    tint = Color.White
                                )
                            }
                            IconButton(
                                onClick = {
                                    currentTrackIndex = (currentTrackIndex + 1) % musicQueue.size
                                    elapsedSeconds = 0
                                    isMusicPlaying = true
                                },
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF392E5C))
                            ) {
                                Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White)
                            }
                        }
                        Row(
                            modifier = Modifier.width(145.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            IconButton(
                                onClick = { isMusicMuted = !isMusicMuted },
                                modifier = Modifier.size(34.dp).clip(CircleShape).background(Color(0xFF392E5C))
                            ) {
                                Icon(
                                    if (isMusicMuted || musicVolume == 0f) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                    contentDescription = "Mute",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Slider(
                                value = if (isMusicMuted) 0f else musicVolume,
                                onValueChange = {
                                    musicVolume = it
                                    if (it > 0f && isMusicMuted) isMusicMuted = false
                                },
                                colors = SliderDefaults.colors(thumbColor = Pink, activeTrackColor = Pink),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF281F44))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Add Music to Party", color = Color(0xFFFFD77A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF392E5C))
                                .clickable { audioPicker.launch("audio/*") }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Text(
                                if (customFileName != null) "Change file" else "Upload audio",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (customFileName != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1A142D))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Attached: $customFileName",
                                color = Color(0xFF70D9FF),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "Remove",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 11.sp,
                                modifier = Modifier.clickable {
                                    customAudioUri = null
                                    customFileName = null
                                }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = customTrackTitle,
                        onValueChange = { customTrackTitle = it },
                        placeholder = { Text("Song title", color = Color(0xFFA799C4), fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customTrackArtist,
                        onValueChange = { customTrackArtist = it },
                        placeholder = { Text("Artist (optional)", color = Color(0xFFA799C4), fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        RoomMood.entries.forEach { moodOption ->
                            val selectedMoodOption = customTrackMood == moodOption
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (selectedMoodOption) moodOption.cardColor else Color(0xFF1A142D))
                                    .clickable { customTrackMood = moodOption }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(moodOption.label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    Button(
                        onClick = {
                            val cleanTitle = customTrackTitle.trim()
                            if (cleanTitle.isNotEmpty()) {
                                val added = PartyTrack(
                                    id = "custom-${System.currentTimeMillis()}",
                                    title = cleanTitle,
                                    artist = customTrackArtist.trim().ifEmpty { "Added by You" },
                                    vibe = customTrackMood.label,
                                    mood = customTrackMood,
                                    durationSeconds = 180,
                                    color = customTrackMood.cardColor,
                                    audioUri = customAudioUri,
                                    fileName = customFileName
                                )
                                val nextIndex = musicQueue.size
                                musicQueue.add(added)
                                onAddTrackToLibrary(added)
                                currentTrackIndex = nextIndex
                                elapsedSeconds = 0
                                isMusicPlaying = true
                                roomChat.add("You" to "🎵 Added & playing \"$cleanTitle\"")
                                customTrackTitle = ""
                                customTrackArtist = ""
                                customAudioUri = null
                                customFileName = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Purple),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add & Play", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Text("Room Queue (${musicQueue.size})", color = Color(0xFFCBBEE6), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                musicQueue.forEachIndexed { idx, track ->
                    val active = idx == currentTrackIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (active) Color(0xFF3F2E68) else Color(0xFF2C2347))
                            .clickable {
                                currentTrackIndex = idx
                                elapsedSeconds = 0
                                isMusicPlaying = true
                                roomChat.add("You" to "🎶 Now playing \"${track.title}\"")
                            }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(track.color),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(track.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${track.artist} · ${track.vibe}", color = Color(0xFFCBBEE6), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Text(
                            if (active && isMusicPlaying) "Playing" else formatTrackTime(track.durationSeconds),
                            color = Color(0xFFCBBEE6),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }

    if (showBgSheet) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x990A0712))
                .clickable { showBgSheet = false },
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 580.dp)
                    .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                    .background(Color(0xFF211938))
                    .clickable(enabled = false) {}
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Party Room Background 🎨", color = Color(0xFFFFD68A), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Text("Pick a scene, atmospheric gradient, or upload your own photo", color = Color(0xFFCBBEE6), fontSize = 11.sp)
                    }
                    IconButton(onClick = { showBgSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
                PartyBackgroundCatalog.chunked(2).forEach { rowPresets ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowPresets.forEach { preset ->
                            val selected = room.backgroundId == preset.id
                            val swatchColors = if (preset.id == "mood") moodRoomGradientColors(room.mood) else preset.colors
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF2C2347))
                                    .border(
                                        width = if (selected) 2.dp else 1.dp,
                                        color = if (selected) Color(0xFFFFD16B) else Color.White.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        onUpdateRoomBackground(room.id, preset.id, null)
                                        roomChat.add("You" to "🎨 Switched room background to ${preset.label}")
                                        showBgSheet = false
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(74.dp)
                                        .background(Brush.linearGradient(swatchColors))
                                ) {
                                    if (preset.drawableRes != null) {
                                        Image(
                                            painter = painterResource(id = preset.drawableRes),
                                            contentDescription = preset.label,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    if (selected) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFFD16B)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color(0xFF24152F), modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(preset.label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(preset.subtitle, color = Color(0xFFCBBEE6), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                        if (rowPresets.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
                Button(
                    onClick = { bgPhotoPicker.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF392E5C)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 46.dp)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFFFFD77A), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (room.backgroundId == "custom") "Change custom background photo" else "Upload custom background photo",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
    }
}

@Composable
private fun ChatListScreen(
    friends: List<String>,
    conversations: Map<String, List<ChatMessage>>,
    onOpenFriends: () -> Unit,
    onChat: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.weight(1f)) {
                Header("Messages", "Your conversations · ${friends.size} friends")
            }
            Button(
                onClick = onOpenFriends,
                shape = RoundedCornerShape(22.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Add friends", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        friends.forEach { name ->
            val preview = conversations[name]?.lastOrNull()?.text ?: "Tap to open conversation"
            PersonRow(name, preview, onClick = { onChat(name) })
        }
    }
}

@Composable
private fun ChatScreen(
    name: String,
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onClearMessages: () -> Unit,
    onBack: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    val submitCurrentMessage = {
        if (message.isNotBlank()) {
            onSendMessage(message)
            message = ""
        }
    }

    Scaffold(
        containerColor = Color(0xFFFFFBFF),
        topBar = {
            Surface(color = Color.White, shadowElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Ink)
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Pink, Purple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(name, fontWeight = FontWeight.Bold, color = Ink, fontSize = 17.sp)
                        Text("Online", color = Purple, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Muted)
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Clear messages") },
                                onClick = {
                                    onClearMessages()
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(color = Color.White, shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...", color = Muted) },
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { submitCurrentMessage() })
                    )
                    FilledIconButton(
                        onClick = submitCurrentMessage,
                        enabled = message.isNotBlank(),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Purple,
                            contentColor = Color.White,
                            disabledContainerColor = Lavender,
                            disabledContentColor = Muted
                        ),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "Say hello to $name 👋",
                    color = Muted,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            items(messages, key = { it.id }) { msg ->
                ChatBubble(msg)
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (message.isFromMe) 18.dp else 4.dp,
                        bottomEnd = if (message.isFromMe) 4.dp else 18.dp
                    )
                )
                .background(if (message.isFromMe) Purple else Lavender)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                color = if (message.isFromMe) Color.White else Ink,
                fontSize = 14.sp
            )
        }
        Text(
            text = formatTimestamp(message.timestamp),
            color = Muted,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}



@Composable
fun HumanBattleScreen() {
    val people = listOf(
        Triple("Sokha", "13", "https://i.pravatar.cc/400?img=47"),
        Triple("Dara", "63", "https://i.pravatar.cc/400?img=12"),
        Triple("Maly", "19", "https://i.pravatar.cc/400?img=44"),
        Triple("Vannak", "10", "https://i.pravatar.cc/400?img= men"),
        Triple("Sophy", "60", "https://i.pravatar.cc/400?img=32"),
        Triple("Alex", "7", "https://i.pravatar.cc/400?img=11"),
        Triple("Nita", "4", "https://i.pravatar.cc/400?img=49"),
        Triple("Mina", "3", "https://i.pravatar.cc/400?img=5")
    )
    var selectedBet by remember { mutableStateOf(100) }
    var seconds by remember { mutableStateOf(21) }
    var joined by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(Color(0xFF101A3B))) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("HUMAN BATTLE", color = Color(0xFFFFD77A), fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                Text("Real people · Live competition", color = Color.White.copy(alpha = .75f), fontSize = 11.sp)
            }
            Text("⋯", color = Color.White, fontSize = 26.sp)
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF243A70)).padding(14.dp),
            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Timer, null, tint = Color(0xFFFFD77A))
            Spacer(Modifier.width(8.dp))
            Text("ROUND STARTS IN 00:$seconds", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Text("Choose a person to support", color = Color.White, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 8.dp))
        Column(Modifier.weight(1f).padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            people.chunked(2).forEach { pair ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    pair.forEach { person ->
                        Column(Modifier.weight(1f).clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF1C2D59)).clickable { joined = true }.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Box {
                                AsyncImage(model = person.third, contentDescription = "Photo of ${person.first}",
                                    modifier = Modifier.fillMaxWidth().height(104.dp).clip(RoundedCornerShape(14.dp)))
                                Text("${person.second} supporters", color = Color.White, fontSize = 10.sp,
                                    modifier = Modifier.align(Alignment.BottomCenter).padding(4.dp)
                                        .clip(RoundedCornerShape(10.dp)).background(Color(0x990B1535)).padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                            Text(person.first, color = Color.White, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 5.dp))
                            Text("Live participant", color = Color(0xFFBFCBEB), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
        Text("Support points", color = Color.White, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 6.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            listOf(1, 10, 100, 1000, 10000).forEach { value ->
                val active = selectedBet == value
                Box(Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                    .background(if (active) Color(0xFFFFC94A) else Color(0xFF263D79))
                    .clickable { selectedBet = value }.padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center) {
                    Text(value.toString(), color = if (active) Color(0xFF33234C) else Color.White,
                        fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = { joined = false }, modifier = Modifier.weight(1f)) {
                Text("Back", color = Color.White)
            }
            Button(onClick = { joined = true }, modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC94A))) {
                Text(if (joined) "Joined ✓" else "Support", color = Color(0xFF33234C), fontWeight = FontWeight.Bold)
            }
        }
        Text("Demo screen: people, points and live results are sample data.", color = Color(0xFFBFCBEB),
            fontSize = 9.sp, modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 6.dp))
    }
}


@Composable
private fun KhmerGiftShopScreen(
    diamonds: Int,
    onSendGift: (KhmerGift, String) -> Unit
) {
    Image(
        painter = painterResource(id = R.drawable.khmer_food_gifts),
        contentDescription = "Khmer traditional food gifts",
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .height(190.dp).clip(RoundedCornerShape(18.dp)),
        contentScale = androidx.compose.ui.layout.ContentScale.Crop
    )

    val gifts = KhmerGiftCatalog
    var selectedGift by remember { mutableStateOf(gifts.first()) }
    var recipient by remember { mutableStateOf("Mina") }
    var showReceived by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("ជ្រើសរើសនំខ្មែរដើម្បីផ្ញើជាកាដូ") }

    Column(Modifier.fillMaxSize().background(Color(0xFF171421))) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("KHMER TRADITIONAL GIFTS 🇰🇭", color = Color(0xFFFFD68A),
                    fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                Text("ផ្ញើនំខ្មែរជូនមិត្តភក្តិ", color = Color.White.copy(alpha = .75f), fontSize = 12.sp)
            }
            Text("💎 $diamonds", color = Color(0xFF70D9FF), fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp).clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF302743)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("ផ្ញើជូន:", color = Color.White)
            Spacer(Modifier.width(10.dp))
            listOf("Mina", "Dara", "Sophy").forEach { name ->
                val active = recipient == name
                Text(name, color = if (active) Color(0xFF24152F) else Color.White,
                    fontSize = 12.sp, modifier = Modifier.padding(end = 6.dp).clip(RoundedCornerShape(18.dp))
                        .background(if (active) Color(0xFFFFD16B) else Color(0xFF514364))
                        .clickable { recipient = name }.padding(horizontal = 11.dp, vertical = 7.dp))
            }
        }
        Text("ជ្រើសរើសកាដូ", color = Color.White, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(gifts) { gift ->
                val active = selectedGift == gift
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                    .background(if (active) Color(0xFF594078) else Color(0xFF292337))
                    .clickable { selectedGift = gift }.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.fillMaxWidth().height(94.dp).clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF61467D), Color(0xFF302A48)))),
                        contentAlignment = Alignment.Center) {
                        Text(gift.emoji, fontSize = 53.sp)
                    }
                    Spacer(Modifier.height(7.dp))
                    Text(gift.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(gift.note, color = Color(0xFFC8BDD7), fontSize = 10.sp)
                    Text("💎 ${gift.price}", color = Color(0xFF71D9FF), fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 5.dp))
                    if (active) Text("✓ បានជ្រើសរើស", color = Color(0xFFFFD16B), fontSize = 11.sp)
                }
            }
        }
        Text(status, color = Color(0xFFFFD68A), fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 7.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f)) {
                Text("${selectedGift.name}", color = Color.White, fontWeight = FontWeight.Bold)
                Text("តម្លៃ 💎 ${selectedGift.price}", color = Color(0xFF70D9FF), fontSize = 12.sp)
            }
            Button(onClick = {
                if (diamonds >= selectedGift.price) {
                    onSendGift(selectedGift, recipient)
                    status = "អ្នកបានផ្ញើ ${selectedGift.name} ទៅ $recipient ហើយ!"
                    showReceived = true
                } else {
                    status = "ពេជ្រមិនគ្រប់គ្រាន់ទេ!"
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B70F5)),
                shape = RoundedCornerShape(24.dp)) {
                Icon(Icons.Default.Send, null)
                Spacer(Modifier.width(6.dp))
                Text("Send")
            }
        }
    }
    AnimatedVisibility(visible = showReceived, enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut()) {
        Box(Modifier.fillMaxSize().background(Color(0x990A0712)).clickable { showReceived = false },
            contentAlignment = Alignment.Center) {
            Column(Modifier.padding(28.dp).fillMaxWidth().clip(RoundedCornerShape(28.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF68468E), Color(0xFF342447))))
                .padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎉 💖 ✨", fontSize = 28.sp)
                Text("ទទួលបានកាដូ!", color = Color(0xFFFFD77A), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Text(selectedGift.emoji, fontSize = 76.sp)
                Text("${selectedGift.name}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("ពីអ្នក ទៅកាន់ $recipient", color = Color.White.copy(alpha = .85f))
                Spacer(Modifier.height(14.dp))
                Button(onClick = { showReceived = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD16B))) {
                    Text("អស្ចារ្យ!", color = Color(0xFF352344))
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(
    giftHistory: List<GiftRecord>,
    currentFamily: FamilyClub?,
    friendsCount: Int,
    friendRequestCount: Int,
    onOpenFriends: () -> Unit,
    onOpenFamily: () -> Unit
) {
    var profilePhoto by remember { mutableStateOf<android.net.Uri?>(null) }
    var coverPhoto by remember { mutableStateOf<android.net.Uri?>(null) }
    var displayName by remember { mutableStateOf("My Happy Place Member") }
    var bio by remember { mutableStateOf("Here to make happy memories ✨") }
    var historyFilter by remember { mutableStateOf("All") }

    val pickProfile = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        profilePhoto = it
    }
    val pickCover = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        coverPhoto = it
    }

    val filteredHistory = remember(giftHistory.size, historyFilter) {
        when (historyFilter) {
            "Sent" -> giftHistory.filter { it.isSent }
            "Received" -> giftHistory.filter { !it.isSent }
            else -> giftHistory
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Header("My Profile", "Your space, your identity")
        Box(Modifier.fillMaxWidth().padding(horizontal = 18.dp).height(130.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Pink, Purple)))
            .clickable { pickCover.launch("image/*") }, contentAlignment = Alignment.Center) {
            if (coverPhoto != null) {
                AsyncImage(model = coverPhoto, contentDescription = "Profile cover",
                    modifier = Modifier.fillMaxSize())
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddPhotoAlternate, null, tint = Color.White, modifier = Modifier.size(30.dp))
                    Text("Tap to add cover photo", color = Color.White, fontSize = 13.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(Modifier.size(112.dp).clip(CircleShape)
                .background(Brush.linearGradient(listOf(Pink, Purple)))
                .clickable { pickProfile.launch("image/*") }, contentAlignment = Alignment.Center) {
                if (profilePhoto != null) {
                    AsyncImage(model = profilePhoto, contentDescription = "Your profile photo",
                        modifier = Modifier.fillMaxSize())
                } else {
                    Icon(Icons.Default.AddAPhoto, null, tint = Color.White, modifier = Modifier.size(42.dp))
                }
            }
        }
        Text("Tap photo to choose from Gallery", color = Muted, fontSize = 11.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 6.dp))
        OutlinedTextField(value = displayName, onValueChange = { displayName = it },
            label = { Text("Your name") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            singleLine = true)
        OutlinedTextField(value = bio, onValueChange = { bio = it },
            label = { Text("Bio") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            minLines = 2)
        Spacer(Modifier.height(12.dp))
        Button(onClick = { }, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            Icon(Icons.Default.Save, null)
            Spacer(Modifier.width(8.dp))
            Text("Save profile")
        }

        // My Friends Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 22.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Lavender)
                .clickable { onOpenFriends() }
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Pink, Purple))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text("My Friends", color = Ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    if (friendRequestCount > 0) "$friendsCount friends · $friendRequestCount requests" else "$friendsCount friends",
                    color = Muted,
                    fontSize = 12.sp
                )
            }
            Text("Add friends", color = Purple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        // My Family Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Lavender)
                .clickable { onOpenFamily() }
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FamilyLogoBadge(
                emblem = currentFamily?.emblem ?: "🏠",
                color = currentFamily?.color ?: Purple,
                accentColor = currentFamily?.accentColor ?: Pink,
                shape = currentFamily?.logoShape ?: FamilyLogoShape.CIRCLE,
                logoImageUri = currentFamily?.logoImageUri,
                sizeDp = 46
            )
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(
                    text = currentFamily?.name ?: "No Family yet",
                    color = Ink,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = if (currentFamily != null) {
                        val role = currentFamily.members.firstOrNull { it.name == "You" }?.role ?: "Member"
                        "$role · Level ${currentFamily.level} · ${currentFamily.members.size} members"
                    } else {
                        "Tap to join or create a Family"
                    },
                    color = Muted,
                    fontSize = 12.sp
                )
            }
            Text("Open", color = Purple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        // Gift History Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Gift History", fontWeight = FontWeight.Bold, color = Ink, fontSize = 18.sp)
                Text("Khmer traditional gifts sent & received", color = Muted, fontSize = 11.sp)
            }
            Text("${filteredHistory.size} items", color = Purple, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Sent", "Received").forEach { tab ->
                val active = historyFilter == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (active) Purple else Lavender)
                        .clickable { historyFilter = tab }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = tab,
                        color = if (active) Color.White else Ink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filteredHistory.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Lavender),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item.emoji, fontSize = 24.sp)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(item.giftName, color = Ink, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = if (item.isSent) "Sent to ${item.peerName} · ${formatTimestamp(item.timestamp)}"
                            else "Received from ${item.peerName} · ${formatTimestamp(item.timestamp)}",
                            color = Muted,
                            fontSize = 11.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (item.isSent) "-💎 ${item.price}" else "+💎 ${item.price}",
                            color = if (item.isSent) Pink else Purple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (item.isSent) "Sent" else "Received",
                            color = Muted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Text("រូបភាពបង្ហាញក្នុង App បច្ចុប្បន្ន។ ការរក្សាទុក Online ត្រូវភ្ជាប់ Firebase Storage បន្ថែម។",
            color = Muted, fontSize = 11.sp, modifier = Modifier.padding(20.dp))
    }
}

private val FamilyLogoPalettes = listOf(
    Color(0xFF8B5CF6) to Color(0xFFFF7EB6),
    Color(0xFFE87A5D) to Color(0xFFFFB86C),
    Color(0xFF5C9DAD) to Color(0xFF38BDF8),
    Color(0xFFE58AAE) to Color(0xFF7555B8)
)

@Composable
private fun FamilyLogoBadge(
    emblem: String,
    color: Color,
    accentColor: Color,
    shape: FamilyLogoShape,
    logoImageUri: android.net.Uri?,
    sizeDp: Int = 48
) {
    val clipShape = when (shape) {
        FamilyLogoShape.CIRCLE -> CircleShape
        FamilyLogoShape.BADGE -> RoundedCornerShape((sizeDp * 0.28f).dp)
        FamilyLogoShape.SHIELD -> RoundedCornerShape(
            topStart = (sizeDp * 0.22f).dp,
            topEnd = (sizeDp * 0.22f).dp,
            bottomStart = (sizeDp * 0.5f).dp,
            bottomEnd = (sizeDp * 0.5f).dp
        )
    }
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(clipShape)
            .background(Brush.linearGradient(listOf(color, accentColor))),
        contentAlignment = Alignment.Center
    ) {
        if (logoImageUri != null) {
            AsyncImage(
                model = logoImageUri,
                contentDescription = "Family logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        } else {
            val isTextMonogram = emblem.length <= 2 && emblem.all { it.isLetterOrDigit() }
            Text(
                text = emblem,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = if (isTextMonogram) (sizeDp * 0.36f).sp else (sizeDp * 0.48f).sp
            )
        }
    }
}

private fun deriveMonogram(name: String): String {
    val words = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        words.size >= 2 -> "${words[0].first().uppercaseChar()}${words[1].first().uppercaseChar()}"
        words.size == 1 -> words[0].take(2).uppercase()
        else -> "HP"
    }
}

@Composable
private fun FamilyScreen(
    families: List<FamilyClub>,
    myFamilyId: String?,
    diamonds: Int,
    checkedInToday: Boolean,
    onBack: () -> Unit,
    onJoinFamily: (String) -> Unit,
    onLeaveFamily: (String) -> Unit,
    onCreateFamily: (String, String, String, Color, Color, FamilyLogoShape, android.net.Uri?) -> Unit,
    onUpdateFamilyLogo: (String, String, Color, Color, FamilyLogoShape, android.net.Uri?) -> Unit,
    onCheckIn: (String) -> Unit,
    onPostAnnouncement: (String, String) -> Unit,
    onStartFamilyParty: (FamilyClub) -> Unit,
    onChatMember: (String) -> Unit
) {
    var activeTab by remember { mutableStateOf(if (myFamilyId != null) 0 else 1) }
    var wallInput by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingLogoFamilyId by remember { mutableStateOf<String?>(null) }
    var newName by remember { mutableStateOf("") }
    var newMotto by remember { mutableStateOf("") }
    var newEmblem by remember { mutableStateOf("🌸") }
    var useMonogram by remember { mutableStateOf(false) }
    var newPaletteIdx by remember { mutableIntStateOf(0) }
    var newShape by remember { mutableStateOf(FamilyLogoShape.CIRCLE) }
    var newLogoUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val pickFamilyLogo = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) newLogoUri = it
    }

    val myFamily = families.firstOrNull { it.id == myFamilyId }

    Scaffold(
        containerColor = Color(0xFFFFFBFF),
        topBar = {
            Surface(color = Color.White, shadowElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Ink)
                    }
                    Column(Modifier.weight(1f).padding(start = 6.dp)) {
                        Text("Family", fontWeight = FontWeight.ExtraBold, color = Ink, fontSize = 20.sp)
                        Text("Connect with your community", color = Muted, fontSize = 11.sp)
                    }
                    Text(
                        "💎 $diamonds",
                        color = Purple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("My Family", "Explore").forEachIndexed { idx, label ->
                        val selected = activeTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 44.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(if (selected) Purple else Lavender)
                                .clickable { activeTab = idx }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                color = if (selected) Color.White else Ink,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            if (activeTab == 0) {
                if (myFamily == null) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(Lavender)
                                .padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🏠", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("You haven't joined a Family yet", color = Ink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Join an existing community or start your own!", color = Muted, fontSize = 12.sp)
                            Spacer(Modifier.height(14.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedButton(onClick = { activeTab = 1 }) {
                                    Text("Explore")
                                }
                                Button(onClick = { showCreateDialog = true }) {
                                    Text("Create Family")
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(myFamily.color)
                                .padding(20.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FamilyLogoBadge(
                                    emblem = myFamily.emblem,
                                    color = myFamily.color,
                                    accentColor = myFamily.accentColor,
                                    shape = myFamily.logoShape,
                                    logoImageUri = myFamily.logoImageUri,
                                    sizeDp = 58
                                )
                                Column(Modifier.weight(1f).padding(start = 14.dp)) {
                                    Text(myFamily.name, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                                    Text(
                                        "Level ${myFamily.level} · ${myFamily.points} pts · ${myFamily.members.size} members",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 12.sp
                                    )
                                }
                                TextButton(
                                    onClick = {
                                        editingLogoFamilyId = myFamily.id
                                        newName = myFamily.name
                                        val isMono = myFamily.emblem.length <= 2 && myFamily.emblem.all { it.isLetterOrDigit() }
                                        useMonogram = isMono
                                        if (!isMono) newEmblem = myFamily.emblem
                                        newShape = myFamily.logoShape
                                        newLogoUri = myFamily.logoImageUri
                                        newPaletteIdx = FamilyLogoPalettes.indexOfFirst { it.first == myFamily.color }.coerceAtLeast(0)
                                        showCreateDialog = true
                                    }
                                ) {
                                    Text("Edit logo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(myFamily.motto, color = Color.White.copy(alpha = 0.92f), fontSize = 13.sp)
                            Spacer(Modifier.height(14.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { onCheckIn(myFamily.id) },
                                    enabled = !checkedInToday,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = myFamily.color,
                                        disabledContainerColor = Color.White.copy(alpha = 0.35f),
                                        disabledContentColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        if (checkedInToday) "Checked in ✓" else "Check in (+25 💎)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Button(
                                    onClick = { onStartFamilyParty(myFamily) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A142D)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Headphones, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Family Party", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    item {
                        SectionTitle("Family Wall", "${myFamily.announcements.size} posts")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = wallInput,
                                onValueChange = { wallInput = it },
                                placeholder = { Text("Share an update with family...", fontSize = 13.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            FilledIconButton(
                                onClick = {
                                    if (wallInput.isNotBlank()) {
                                        onPostAnnouncement(myFamily.id, wallInput)
                                        wallInput = ""
                                    }
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Purple)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Post", tint = Color.White)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }

                    items(myFamily.announcements) { note ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Lavender)
                                .padding(12.dp)
                        ) {
                            Text(note, color = Ink, fontSize = 13.sp)
                        }
                    }

                    item {
                        SectionTitle("Members", "${myFamily.members.size} people")
                    }

                    items(myFamily.members) { member ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 5.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(Pink, Purple))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(member.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            }
                            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(member.name, color = Ink, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${member.role} · ${member.points} pts", color = Muted, fontSize = 11.sp)
                            }
                            if (member.name != "You") {
                                TextButton(onClick = { onChatMember(member.name) }) {
                                    Text("Chat", color = Purple, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        Spacer(Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = {
                                onLeaveFamily(myFamily.id)
                                activeTab = 1
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Text("Leave Family", color = Color(0xFFD9386A))
                        }
                    }
                }
            } else {
                item {
                    Button(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .defaultMinSize(minHeight = 48.dp)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Create a Family")
                    }
                    Spacer(Modifier.height(8.dp))
                }
                items(families, key = { it.id }) { family ->
                    val isMine = family.id == myFamilyId
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FamilyLogoBadge(
                                emblem = family.emblem,
                                color = family.color,
                                accentColor = family.accentColor,
                                shape = family.logoShape,
                                logoImageUri = family.logoImageUri,
                                sizeDp = 48
                            )
                            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(family.name, color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                Text(
                                    "Level ${family.level} · ${family.members.size} members · ${family.points} pts",
                                    color = Muted,
                                    fontSize = 11.sp
                                )
                            }
                            Button(
                                onClick = {
                                    onJoinFamily(family.id)
                                    activeTab = 0
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isMine) Lavender else Purple
                                )
                            ) {
                                Text(
                                    if (isMine) "Joined" else "Join",
                                    color = if (isMine) Purple else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(family.motto, color = Muted, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        val activePalette = FamilyLogoPalettes[newPaletteIdx]
        val resolvedEmblem = if (useMonogram) deriveMonogram(newName) else newEmblem
        val isEditingLogo = editingLogoFamilyId != null

        AlertDialog(
            onDismissRequest = {
                showCreateDialog = false
                editingLogoFamilyId = null
            },
            title = {
                Text(
                    if (isEditingLogo) "Customize Family Logo" else "Create Family & Logo",
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Lavender)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FamilyLogoBadge(
                            emblem = resolvedEmblem,
                            color = activePalette.first,
                            accentColor = activePalette.second,
                            shape = newShape,
                            logoImageUri = newLogoUri,
                            sizeDp = 64
                        )
                        Column(Modifier.weight(1f)) {
                            Text("Logo Preview", fontWeight = FontWeight.Bold, color = Ink, fontSize = 13.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                TextButton(
                                    onClick = { pickFamilyLogo.launch("image/*") },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Upload photo", fontSize = 12.sp, color = Purple, fontWeight = FontWeight.Bold)
                                }
                                if (newLogoUri != null) {
                                    TextButton(
                                        onClick = { newLogoUri = null },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Use crest", fontSize = 12.sp, color = Pink, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    if (!isEditingLogo) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            placeholder = { Text("Family name (e.g. Lotus Vibes)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newMotto,
                            onValueChange = { newMotto = it },
                            placeholder = { Text("Family motto") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Text("Logo shape", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Ink)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FamilyLogoShape.entries.forEach { shape ->
                            val picked = newShape == shape
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (picked) Purple else Lavender)
                                    .clickable { newShape = shape },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    shape.label,
                                    color = if (picked) Color.White else Ink,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Text("Crest symbol or initials", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Ink)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("🌸", "👑", "🐉", "✨", "🔥", "💎", "🪷").forEach { emblem ->
                            val picked = !useMonogram && newEmblem == emblem
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (picked) Purple else Lavender)
                                    .clickable {
                                        useMonogram = false
                                        newEmblem = emblem
                                        newLogoUri = null
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emblem, fontSize = 20.sp)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .height(44.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(if (useMonogram) Purple else Lavender)
                                .clickable {
                                    useMonogram = true
                                    newLogoUri = null
                                }
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "ABC",
                                color = if (useMonogram) Color.White else Ink,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Text("Logo gradient", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Ink)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FamilyLogoPalettes.forEachIndexed { idx, palette ->
                            val picked = newPaletteIdx == idx
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(palette.first, palette.second)))
                                    .clickable { newPaletteIdx = idx },
                                contentAlignment = Alignment.Center
                            ) {
                                if (picked) {
                                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isEditingLogo) {
                            onUpdateFamilyLogo(
                                editingLogoFamilyId!!,
                                resolvedEmblem,
                                activePalette.first,
                                activePalette.second,
                                newShape,
                                newLogoUri
                            )
                            editingLogoFamilyId = null
                            showCreateDialog = false
                        } else {
                            val trimmed = newName.trim()
                            if (trimmed.isNotEmpty()) {
                                onCreateFamily(
                                    trimmed,
                                    newMotto.trim(),
                                    resolvedEmblem,
                                    activePalette.first,
                                    activePalette.second,
                                    newShape,
                                    newLogoUri
                                )
                                newName = ""
                                newMotto = ""
                                newLogoUri = null
                                useMonogram = false
                                showCreateDialog = false
                                activeTab = 0
                            }
                        }
                    }
                ) {
                    Text(if (isEditingLogo) "Save Logo" else "Create")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateDialog = false
                        editingLogoFamilyId = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun FriendsScreen(
    peopleDirectory: List<FriendPerson>,
    friends: List<String>,
    friendRequests: List<String>,
    onBack: () -> Unit,
    onAddFriend: (String) -> String?,
    onAcceptRequest: (String) -> Unit,
    onDeclineRequest: (String) -> Unit,
    onRemoveFriend: (String) -> Unit,
    onChat: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var customInput by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }

    val query = searchQuery.trim()
    val resolvePerson: (String) -> FriendPerson = { name ->
        peopleDirectory.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: FriendPerson(
            name = name,
            handle = "@${name.lowercase().replace(Regex("[^a-z0-9]+"), ".")}",
            vibe = "MY HAPPY PLACE member",
            isOnline = true,
            mutualCount = 0
        )
    }
    val matches: (FriendPerson) -> Boolean = { person ->
        query.isEmpty() ||
            person.name.contains(query, ignoreCase = true) ||
            person.handle.contains(query, ignoreCase = true) ||
            person.vibe.contains(query, ignoreCase = true)
    }

    val requestPeople = friendRequests.map(resolvePerson).filter(matches)
    val friendPeople = friends.map(resolvePerson).filter(matches)
    val discoverPeople = peopleDirectory.filter {
        !friends.contains(it.name) && !friendRequests.contains(it.name) && matches(it)
    }

    Scaffold(
        containerColor = Color(0xFFFFFBFF),
        topBar = {
            Surface(color = Color.White, shadowElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Ink)
                    }
                    Column(Modifier.weight(1f).padding(start = 6.dp)) {
                        Text("Add Friends", fontWeight = FontWeight.ExtraBold, color = Ink, fontSize = 18.sp)
                        Text(
                            "${friends.size} friends · ${friendRequests.size} requests",
                            color = Muted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Lavender)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Add by name or @username", color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customInput,
                            onValueChange = { customInput = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Name or @handle", fontSize = 13.sp, color = Muted) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                        Button(
                            onClick = {
                                val added = onAddFriend(customInput)
                                if (added != null) {
                                    customInput = ""
                                    feedback = "Added $added to your friends list!"
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.defaultMinSize(minHeight = 52.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Add", fontWeight = FontWeight.Bold)
                        }
                    }
                    if (feedback != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF5B6EE1), modifier = Modifier.size(16.dp))
                            Text(feedback!!, color = Color(0xFF5B6EE1), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Filter people by name, @handle, or vibe...", fontSize = 13.sp, color = Muted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Purple) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Muted)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
            }

            if (requestPeople.isNotEmpty()) {
                item {
                    Text("Friend Requests (${requestPeople.size})", color = Ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                items(requestPeople, key = { "req-${it.name}" }) { person ->
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Lavender),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AvatarBadge(name = person.name, size = 40.dp, fontSize = 16.sp)
                            Column(Modifier.weight(1f)) {
                                Text(person.name, color = Ink, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    "${person.handle} · ${person.mutualCount} mutual friends",
                                    color = Muted,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Button(
                                onClick = {
                                    onAcceptRequest(person.name)
                                    feedback = "Accepted ${person.name}'s friend request!"
                                },
                                shape = RoundedCornerShape(18.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Accept", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            TextButton(onClick = { onDeclineRequest(person.name) }) {
                                Text("Decline", color = Muted, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                Text("People You May Know", color = Ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            if (discoverPeople.isEmpty()) {
                item {
                    Text(
                        if (query.isNotEmpty()) "No suggested people matched \"$query\"." else "All suggested people have been added!",
                        color = Muted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else {
                items(discoverPeople, key = { "disc-${it.name}" }) { person ->
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Lavender),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AvatarBadge(name = person.name, size = 40.dp, fontSize = 16.sp)
                            Column(Modifier.weight(1f)) {
                                Text(person.name, color = Ink, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    "${person.handle} · ${person.vibe}",
                                    color = Muted,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Button(
                                onClick = {
                                    onAddFriend(person.name)
                                    feedback = "Added ${person.name} to your friends!"
                                },
                                shape = RoundedCornerShape(18.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Add friend", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { onChat(person.name) }) {
                                Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Chat", tint = Purple)
                            }
                        }
                    }
                }
            }

            item {
                Text("My Friends (${friendPeople.size})", color = Ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            items(friendPeople, key = { "friend-${it.name}" }) { person ->
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Lavender),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AvatarBadge(name = person.name, size = 40.dp, fontSize = 16.sp)
                        Column(Modifier.weight(1f)) {
                            Text(person.name, color = Ink, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                "${person.handle} · ${if (person.isOnline) "Online" else "Offline"} · ${person.vibe}",
                                color = Muted,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        OutlinedButton(
                            onClick = { onChat(person.name) },
                            shape = RoundedCornerShape(18.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Chat", color = Purple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = {
                            onRemoveFriend(person.name)
                            feedback = "Removed ${person.name} from friends."
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove friend", tint = Muted)
                        }
                    }
                }
            }
        }
    }
}


