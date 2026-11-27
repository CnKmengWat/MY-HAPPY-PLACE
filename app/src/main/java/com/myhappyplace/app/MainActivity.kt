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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DarkBg = Color(0xFF16151B)
private val DarkCard = Color(0xFF222128)
private val DarkSurface2 = Color(0xFF2B2A34)
private val Ink = Color(0xFFFFFFFF)
private val Purple = Color(0xFF8B5CF6)
private val Pink = Color(0xFFFF5C8A)
private val Lavender = Color(0xFF2B2A34)
private val Muted = Color(0xFF9E9BB0)

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
    val partyMode: String = "Free Mic",
    val welcomeMessage: String = "Please click here to edit~",
    val isLocked: Boolean = false,
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

private data class OnlineStatusPerson(
    val id: String,
    val name: String,
    val isFemale: Boolean,
    val age: Int,
    val status: String,
    val isDomestic: Boolean,
    val colors: List<Color>
)

private data class MomentTopic(
    val id: String,
    val label: String,
    val emoji: String,
    val colors: List<Color>
)

private data class MomentComment(
    val id: Long,
    val author: String,
    val text: String
)

private data class MomentPost(
    val id: String,
    val author: String,
    val isFemale: Boolean,
    val age: Int,
    val timeAgo: String,
    val caption: String,
    val topicId: String,
    val hasPhotoBanner: Boolean = false,
    val imageEmoji: String? = null,
    val bannerColors: List<Color> = listOf(Color(0xFF581C87), Color(0xFFBE185D)),
    val likes: Int,
    val comments: List<MomentComment>,
    val shares: Int,
    val tab: String = "foryou"
)

private val InitialOnlineStatuses = listOf(
    OnlineStatusPerson("st-1", "Mina", true, 21, "ត្រឹមជាក្មេងកាតឿម្នាក់ហេ😜😉", true, listOf(Color(0xFF2DD4BF), Color(0xFF0EA5E9))),
    OnlineStatusPerson("st-2", "VITA", false, 21, "ចង់មានសង្សារដែរ😢🇰🇭", true, listOf(Color(0xFF3B82F6), Color(0xFF6366F1))),
    OnlineStatusPerson("st-3", "លីដា", true, 20, "like chat", true, listOf(Color(0xFF34D399), Color(0xFF10B981))),
    OnlineStatusPerson("st-4", "ah thy CG", false, 18, "💦 Acoustic night?", true, listOf(Color(0xFF60A5FA), Color(0xFF8B5CF6))),
    OnlineStatusPerson("st-5", "TONG CG", false, 18, "ចង់ស្តាប់ខ្ញុំច្រៀងអត់ឆាតមក😊💗", false, listOf(Color(0xFF64748B), Color(0xFF334155))),
    OnlineStatusPerson("st-6", "Sophy", true, 22, "Hi my name is Sophy ✨", false, listOf(Color(0xFFEC4899), Color(0xFF8B5CF6))),
    OnlineStatusPerson("st-7", "Dara", false, 24, "Hot mic tonight!", true, listOf(Color(0xFFEF4444), Color(0xFFF97316)))
)

private val MomentTopicsCatalog = listOf(
    MomentTopic("t-1", "ប្រទីបខ្មែរ", "🏮", listOf(Color(0xFF7C3AED), Color(0xFFEC4899))),
    MomentTopic("t-2", "អុំទូកឆ្នាំនេះ", "🚣", listOf(Color(0xFF0EA5E9), Color(0xFF2563EB))),
    MomentTopic("t-3", "ថ្ងៃនេះញ៉ាំអី", "🍜", listOf(Color(0xFFF59E0B), Color(0xFFEA580C))),
    MomentTopic("t-4", "ពាក្យក្នុងចិត្ត", "💌", listOf(Color(0xFFF472B6), Color(0xFFFB7185))),
    MomentTopic("t-5", "តារានិករ", "🔮", listOf(Color(0xFF6366F1), Color(0xFF4338CA)))
)

private val InitialMomentPosts = listOf(
    MomentPost(
        id = "m-1",
        author = "ស្រីនាង",
        isFemale = true,
        age = 26,
        timeAgo = "Active 2 hours ago",
        caption = "ល្ងាចនេះរ៉ូម៉ែនទិក ជាមួយទេសភាពមាត់ទន្លេ 🌸✨",
        topicId = "t-1",
        hasPhotoBanner = true,
        imageEmoji = "💃✨",
        bannerColors = listOf(Color(0xFF581C87), Color(0xFFBE185D), Color(0xFF1E1B4B)),
        likes = 124,
        comments = listOf(MomentComment(1L, "Mina", "So pretty tonight!")),
        shares = 8,
        tab = "foryou"
    ),
    MomentPost(
        id = "m-2",
        author = "Lii",
        isFemale = false,
        age = 18,
        timeAgo = "Active now",
        caption = "គ្មានអ្វីស្រួលជាង.... ទេ? សូមបំពេញចន្លោះ? ។🤭",
        topicId = "t-4",
        likes = 18,
        comments = listOf(MomentComment(2L, "Dara", "Sleeping early 😂")),
        shares = 3,
        tab = "foryou"
    ),
    MomentPost(
        id = "m-3",
        author = "Ah JM🥹💦",
        isFemale = true,
        age = 23,
        timeAgo = "Active 33 mins ago",
        caption = "បើប្រទេសគ្មានច្បាប់ធ្វើអីមុនគេ.........?",
        topicId = "t-5",
        likes = 6,
        comments = listOf(MomentComment(3L, "Sophy", "Sing all night in Party Chat!")),
        shares = 1,
        tab = "latest"
    ),
    MomentPost(
        id = "m-4",
        author = "Mina",
        isFemale = true,
        age = 21,
        timeAgo = "Active 12 mins ago",
        caption = "ស្តាប់ភ្លេងស្ងប់ស្ងាត់ជាមួយគ្នា ☕🎶 Join Late Night Talk!",
        topicId = "t-3",
        likes = 42,
        comments = emptyList(),
        shares = 5,
        tab = "following"
    )
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
    var selected by remember { mutableIntStateOf(0) } // 0=Home, 1=Party, 2=Moments, 3=Chat, 4=Me
    var openedChat by remember { mutableStateOf<String?>(null) }
    var activeVoiceRoom by remember { mutableStateOf<VoiceRoom?>(null) }
    var showFamilyScreen by remember { mutableStateOf(false) }
    var showFriendsScreen by remember { mutableStateOf(false) }
    var showBattleScreen by remember { mutableStateOf(false) }
    var showShopScreen by remember { mutableStateOf(false) }
    var myFamilyId by remember { mutableStateOf<String?>("angkor-lotus") }
    var checkedInToday by remember { mutableStateOf(false) }
    var diamonds by remember { mutableIntStateOf(5000) }
    var claimedFreeDiamonds by remember { mutableStateOf(false) }

    var displayName by remember { mutableStateOf("និងហើយៗចូលចិត្ត") }
    var bio by remember { mutableStateOf("@MaLen Love 💕") }
    var userStatusText by remember { mutableStateOf("") }
    var userOccupation by remember { mutableStateOf("Merchant/trader") }
    var userInterests by remember { mutableStateOf(listOf("Merchant/Trader", "Coffee")) }

    val onlineStatuses = remember { mutableStateListOf(*InitialOnlineStatuses.toTypedArray()) }
    val momentPosts = remember { mutableStateListOf(*InitialMomentPosts.toTypedArray()) }
    val likedPostIds = remember { mutableStateListOf<String>() }
    val followingAuthors = remember { mutableStateListOf("Mina") }

    var showSoulMatchDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showComposeMomentDialog by remember { mutableStateOf(false) }

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
            GiftRecord(1L, "នំអន្សមចេក", "🍙", 49, false, "Sophy", now - 3_600_000L),
            GiftRecord(2L, "នំអន្សមជ្រូក", "🫔", 29, true, "Mina", now - 7_200_000L),
            GiftRecord(3L, "នំគ្រក់", "🥥", 19, false, "Dara", now - 86_400_000L),
            GiftRecord(4L, "នំចាក់ចាន់", "🍰", 35, true, "Sophy", now - 172_800_000L)
        )
    }
    val voiceRooms = remember {
        val anchor = System.currentTimeMillis()
        mutableStateListOf(
            VoiceRoom("late-night", "សូមស្វាគមន៍មកកាន់ room NAN", "128 people · 6 speakers", RoomMood.COZY, RoomMood.COZY.cardColor, 128, listOf("Mina", "Dara", "Sokha", "Sophy", "Alex", "Nita"), owner = "Mina", backgroundId = "lounge", createdAt = anchor - 3 * 3_600_000L),
            VoiceRoom("music-chill", "🌾HBD💋កូនមាន់តូច🐥7/12/26🌾", "64 people · 4 speakers", RoomMood.CHILL, RoomMood.CHILL.cardColor, 64, listOf("Sophy", "Maly", "Dara", "Vannak"), owner = "Sophy", backgroundId = "neon", createdAt = anchor - 1 * 3_600_000L),
            VoiceRoom("khmer-friends", "កន្លែងរកស្នេហ៍ស្មោះ❤️🕊️", "32 people · 3 speakers", RoomMood.HAPPY, RoomMood.HAPPY.cardColor, 32, listOf("Sokha", "Mina", "Vannak"), owner = "Sokha", backgroundId = "sunset", createdAt = anchor - 15 * 60_000L)
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
                ChatMessage(2L, "អរគុណណាស់ លេងហ្គេមជាមួយគ្នាទេ?", false, now - 120_000L)
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
        colorScheme = darkColorScheme(
            primary = Purple,
            secondary = Pink,
            background = DarkBg,
            surface = DarkCard,
            onPrimary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White
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
        } else if (showBattleScreen) {
            Column(Modifier.fillMaxSize().background(Color(0xFF101A3B))) {
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showBattleScreen = false }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text("Back to Profile", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Box(Modifier.weight(1f)) {
                    HumanBattleScreen()
                }
            }
        } else if (showShopScreen) {
            Column(Modifier.fillMaxSize().background(Color(0xFF171421))) {
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showShopScreen = false }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text("Back", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Box(Modifier.weight(1f)) {
                    KhmerGiftShopScreen(
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
        } else {
            val currentFamily = families.firstOrNull { it.id == myFamilyId }
            Scaffold(
                containerColor = DarkBg,
                bottomBar = {
                    NavigationBar(containerColor = Color(0xFF1D1C24), tonalElevation = 0.dp) {
                        val tabs = listOf(
                            "Home" to Icons.Default.Home,
                            "Party" to Icons.Default.Mic,
                            "Moments" to Icons.Default.Public,
                            "Chat" to Icons.Default.ChatBubble,
                            "Me" to Icons.Default.Person
                        )
                        tabs.forEachIndexed { index, item ->
                            val isSelected = selected == index
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { selected = index },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    unselectedIconColor = Muted,
                                    selectedTextColor = Color.White,
                                    unselectedTextColor = Muted,
                                    indicatorColor = Purple.copy(alpha = 0.25f)
                                ),
                                icon = {
                                    Box {
                                        Icon(item.second, contentDescription = item.first)
                                        if (index == 1) {
                                            Box(
                                                Modifier
                                                    .align(Alignment.TopEnd)
                                                    .size(7.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFFF4D6D))
                                            )
                                        }
                                        if (index == 3) {
                                            Box(
                                                Modifier
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = 6.dp, y = (-4).dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFFF4D6D))
                                                    .padding(horizontal = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("2", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                },
                                label = { Text(item.first, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            ) { padding ->
                Box(Modifier.padding(padding).background(DarkBg)) {
                    when (selected) {
                        0 -> HomeScreen(
                            statuses = onlineStatuses,
                            rooms = voiceRooms,
                            friends = friends,
                            userStatusText = userStatusText,
                            onChat = { openedChat = it },
                            onTab = { selected = it },
                            onOpenSoulMatch = { showSoulMatchDialog = true },
                            onJoinVoiceGame = {
                                voiceRooms.firstOrNull()?.let { activeVoiceRoom = it }
                            },
                            onOpenFriends = { showFriendsScreen = true },
                            onAddFriend = { addFriend(it) },
                            onOpenStatusModal = { showStatusDialog = true }
                        )
                        1 -> PartyScreen(
                            rooms = voiceRooms,
                            musicCount = musicLibrary.size,
                            reportedRoomIds = reportedRoomIds,
                            onJoinRoom = { activeVoiceRoom = it },
                            onReportRoom = { reportingRoom = it },
                            onOpenAddMusic = { showAddMusicDialog = true },
                            onOpenFamily = { showFamilyScreen = true },
                            onOpenShop = { showShopScreen = true },
                            onCreateRoom = { title, mood, backgroundId, partyMode, welcomeMessage, isLocked ->
                                val created = VoiceRoom(
                                    id = "room-${System.currentTimeMillis()}",
                                    title = title,
                                    detail = "1 person · $partyMode",
                                    mood = mood,
                                    color = mood.cardColor,
                                    listeners = 1,
                                    speakers = listOf("You"),
                                    owner = "You",
                                    backgroundId = backgroundId,
                                    partyMode = partyMode,
                                    welcomeMessage = welcomeMessage.ifBlank { "Please click here to edit~" },
                                    isLocked = isLocked
                                )
                                voiceRooms.add(0, created)
                                activeVoiceRoom = created
                            }
                        )
                        2 -> MomentsFeedScreen(
                            posts = momentPosts,
                            likedPostIds = likedPostIds,
                            followingAuthors = followingAuthors,
                            onToggleLike = { postId ->
                                val idx = momentPosts.indexOfFirst { it.id == postId }
                                if (idx >= 0) {
                                    val liked = likedPostIds.contains(postId)
                                    if (liked) likedPostIds.remove(postId) else likedPostIds.add(postId)
                                    val p = momentPosts[idx]
                                    momentPosts[idx] = p.copy(likes = (p.likes + if (liked) -1 else 1).coerceAtLeast(0))
                                }
                            },
                            onAddComment = { postId, text ->
                                val clean = text.trim()
                                val idx = momentPosts.indexOfFirst { it.id == postId }
                                if (idx >= 0 && clean.isNotEmpty()) {
                                    val p = momentPosts[idx]
                                    momentPosts[idx] = p.copy(
                                        comments = p.comments + MomentComment(System.currentTimeMillis(), "You", clean)
                                    )
                                }
                            },
                            onSharePost = { postId ->
                                val idx = momentPosts.indexOfFirst { it.id == postId }
                                if (idx >= 0) {
                                    val p = momentPosts[idx]
                                    momentPosts[idx] = p.copy(shares = p.shares + 1)
                                }
                            },
                            onToggleFollow = { author ->
                                if (followingAuthors.contains(author)) followingAuthors.remove(author)
                                else followingAuthors.add(author)
                            },
                            onSayHi = { author -> openedChat = author },
                            onOpenCompose = { showComposeMomentDialog = true }
                        )
                        3 -> ChatListScreen(
                            friends = friends,
                            conversations = conversations,
                            claimedFreeDiamonds = claimedFreeDiamonds,
                            onClaimFreeDiamonds = {
                                if (!claimedFreeDiamonds) {
                                    claimedFreeDiamonds = true
                                    diamonds += 15
                                }
                            },
                            onOpenFriends = { showFriendsScreen = true },
                            onOpenParty = {
                                val first = voiceRooms.firstOrNull()
                                if (first != null) activeVoiceRoom = first else selected = 1
                            },
                            onOpenGifts = { showShopScreen = true },
                            onChat = { openedChat = it }
                        )
                        else -> ProfileScreen(
                            displayName = displayName,
                            bio = bio,
                            userStatusText = userStatusText,
                            userOccupation = userOccupation,
                            userInterests = userInterests,
                            diamonds = diamonds,
                            giftHistory = giftHistory,
                            currentFamily = currentFamily,
                            friendsCount = friends.size,
                            friendRequestCount = friendRequests.size,
                            rooms = voiceRooms,
                            posts = momentPosts,
                            onOpenFriends = { showFriendsScreen = true },
                            onOpenFamily = { showFamilyScreen = true },
                            onOpenShop = { showShopScreen = true },
                            onOpenGame = { showBattleScreen = true },
                            onOpenAddMusic = { showAddMusicDialog = true },
                            onOpenStatusModal = { showStatusDialog = true },
                            onOpenCompose = { showComposeMomentDialog = true },
                            onJoinMyRoom = {
                                val first = voiceRooms.firstOrNull()
                                if (first != null) activeVoiceRoom = first else selected = 1
                            },
                            onSaveProfile = { nextName, nextBio, nextOcc, nextInterests ->
                                displayName = nextName
                                bio = nextBio
                                userOccupation = nextOcc
                                userInterests = nextInterests
                            }
                        )
                    }
                }
            }

            if (showSoulMatchDialog) {
                var matchedPerson by remember { mutableStateOf<String?>(null) }
                AlertDialog(
                    containerColor = DarkCard,
                    onDismissRequest = { showSoulMatchDialog = false },
                    title = { Text("Soul Match ✨", fontWeight = FontWeight.ExtraBold, color = Color.White) },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(Pink, Purple))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
                            }
                            Spacer(Modifier.height(14.dp))
                            Text(
                                if (matchedPerson != null) "Matched with $matchedPerson 💜" else "Tap below to meet someone online",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    confirmButton = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { matchedPerson = peopleDirectory.random().name },
                                colors = ButtonDefaults.buttonColors(containerColor = Purple)
                            ) {
                                Text(if (matchedPerson == null) "Match Now" else "Next")
                            }
                            if (matchedPerson != null) {
                                Button(
                                    onClick = {
                                        val person = matchedPerson!!
                                        showSoulMatchDialog = false
                                        openedChat = person
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Pink)
                                ) {
                                    Text("Chat")
                                }
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSoulMatchDialog = false }) {
                            Text("Close", color = Muted)
                        }
                    }
                )
            }

            if (showStatusDialog) {
                var draft by remember { mutableStateOf(userStatusText) }
                AlertDialog(
                    containerColor = DarkCard,
                    onDismissRequest = { showStatusDialog = false },
                    title = { Text("Update Live Status", fontWeight = FontWeight.ExtraBold, color = Color.White) },
                    text = {
                        OutlinedTextField(
                            value = draft,
                            onValueChange = { draft = it },
                            placeholder = { Text("What's on your mind today?", color = Muted) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Purple,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val clean = draft.trim()
                                userStatusText = clean
                                if (clean.isNotEmpty()) {
                                    onlineStatuses.add(
                                        0,
                                        OnlineStatusPerson(
                                            id = "st-${System.currentTimeMillis()}",
                                            name = displayName,
                                            isFemale = false,
                                            age = 22,
                                            status = clean,
                                            isDomestic = true,
                                            colors = listOf(Purple, Pink)
                                        )
                                    )
                                }
                                showStatusDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Purple)
                        ) {
                            Text("Post Status")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showStatusDialog = false }) {
                            Text("Cancel", color = Muted)
                        }
                    }
                )
            }

            if (showComposeMomentDialog) {
                var caption by remember { mutableStateOf("") }
                var topicId by remember { mutableStateOf(MomentTopicsCatalog.first().id) }
                AlertDialog(
                    containerColor = DarkCard,
                    onDismissRequest = { showComposeMomentDialog = false },
                    title = { Text("Share a Moment ✨", fontWeight = FontWeight.ExtraBold, color = Color.White) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = caption,
                                onValueChange = { caption = it },
                                placeholder = { Text("Write your moment...", color = Muted) },
                                minLines = 2,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Purple,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                MomentTopicsCatalog.forEach { t ->
                                    val active = topicId == t.id
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (active) Purple else DarkSurface2)
                                            .clickable { topicId = t.id }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("${t.emoji} ${t.label}", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            enabled = caption.trim().isNotEmpty(),
                            onClick = {
                                momentPosts.add(
                                    0,
                                    MomentPost(
                                        id = "m-${System.currentTimeMillis()}",
                                        author = displayName,
                                        isFemale = false,
                                        age = 22,
                                        timeAgo = "Just now",
                                        caption = caption.trim(),
                                        topicId = topicId,
                                        likes = 1,
                                        comments = emptyList(),
                                        shares = 0,
                                        tab = "latest"
                                    )
                                )
                                showComposeMomentDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Purple)
                        ) {
                            Text("Post")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showComposeMomentDialog = false }) {
                            Text("Cancel", color = Muted)
                        }
                    }
                )
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
                                selected = 1
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
        Text(title, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
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
        containerColor = DarkCard,
        onDismissRequest = onDismiss,
        title = { Text("Add Music 🎶", fontWeight = FontWeight.Bold, color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurface2)
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
                            color = Color.White,
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
                    placeholder = { Text("Song title", color = Muted) },
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
                    value = artist,
                    onValueChange = { artist = it },
                    placeholder = { Text("Artist (optional)", color = Muted) },
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
                    RoomMood.entries.forEach { option ->
                        val active = mood == option
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (active) option.cardColor else DarkSurface2)
                                .clickable { mood = option }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(option.label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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
                    Text("Save", color = Color(0xFFA78BFA))
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
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Purple)
                ) {
                    Text("Add & Play")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Muted)
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
        containerColor = DarkCard,
        onDismissRequest = onDismiss,
        title = { Text("Report Room", fontWeight = FontWeight.Bold, color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Flagging: ${room.title}", color = Muted, fontSize = 12.sp)
                reasons.forEach { reason ->
                    val active = selectedReason == reason
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (active) Purple else DarkSurface2)
                            .clickable { selectedReason = reason }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(reason, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        if (active) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    placeholder = { Text("Additional details (optional)", color = Muted, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Purple,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = Pink)
            ) {
                Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Submit Report", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Muted)
            }
        }
    )
}

@Composable
private fun HomeScreen(
    statuses: List<OnlineStatusPerson>,
    rooms: List<VoiceRoom>,
    friends: List<String>,
    userStatusText: String,
    onChat: (String) -> Unit,
    onTab: (Int) -> Unit,
    onOpenSoulMatch: () -> Unit,
    onJoinVoiceGame: () -> Unit,
    onOpenFriends: () -> Unit,
    onAddFriend: (String) -> Unit,
    onOpenStatusModal: () -> Unit
) {
    var scopeMode by remember { mutableStateOf("Domestic") }
    var genderFilter by remember { mutableStateOf("All") } // "All", "Female", "Male"

    val filteredStatuses = remember(statuses.size, scopeMode, genderFilter) {
        statuses.filter { person ->
            val scopeMatch = if (scopeMode == "Domestic") person.isDomestic else true
            val genderMatch = when (genderFilter) {
                "Female" -> person.isFemale
                "Male" -> !person.isFemale
                else -> true
            }
            scopeMatch && genderMatch
        }
    }

    Box(Modifier.fillMaxSize().background(DarkBg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 84.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        genderFilter = when (genderFilter) {
                            "All" -> "Female"
                            "Female" -> "Male"
                            else -> "All"
                        }
                    }) {
                        Icon(Icons.Default.Tune, contentDescription = "Filter", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Text(
                        "MY HAPPY PLACE",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarkCard)
                            .clickable { onOpenFriends() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color(0xFFFF8BB8), modifier = Modifier.size(20.dp))
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Soul game card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(DarkCard)
                            .clickable { onOpenSoulMatch() }
                            .padding(vertical = 14.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFFFF6CA8), Color(0xFFD9386A)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ChatBubble, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.height(10.dp))
                        Text("Soul game", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF38BDF8)))
                            Text("23229 Online", color = Muted, fontSize = 10.sp, maxLines = 1)
                        }
                    }

                    // Voice game card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(DarkCard)
                            .clickable { onJoinVoiceGame() }
                            .padding(vertical = 14.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF34D399), Color(0xFF059669)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.height(10.dp))
                        Text("Voice game", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF38BDF8)))
                            Text("20042 Online", color = Muted, fontSize = 10.sp, maxLines = 1)
                        }
                    }

                    // Party Match card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(DarkCard)
                            .clickable { onTab(1) }
                            .padding(vertical = 14.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFFA78BFA), Color(0xFF7C3AED)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.height(10.dp))
                        Text("Party Match", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = Pink, modifier = Modifier.size(12.dp))
                            Text("${9291 + rooms.sumOf { it.listeners }} Playing", color = Muted, fontSize = 10.sp, maxLines = 1)
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(DarkCard)
                        .clickable { onOpenFriends() }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF38BDF8)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                    Column(Modifier.weight(1f).padding(start = 12.dp, end = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Worldwide Match", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Text("Meet worldwide Friends online", color = Muted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF6D28D9).copy(alpha = 0.65f))
                            .clickable {
                                scopeMode = if (scopeMode == "Domestic") "Worldwide" else "Domestic"
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$scopeMode ⇄", color = Color(0xFFE9D5FF), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Purple.copy(alpha = 0.8f))
                            .clickable {
                                genderFilter = when (genderFilter) {
                                    "All" -> "Female"
                                    "Female" -> "Male"
                                    else -> "All"
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = "Gender Filter", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            if (userStatusText.isNotBlank()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurface2)
                            .clickable { onOpenStatusModal() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AvatarBadge(name = "You", size = 40.dp, fontSize = 15.sp)
                        Column(Modifier.weight(1f)) {
                            Text("Your Status", color = Purple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(userStatusText, color = Color.White, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        Icon(Icons.Default.Edit, contentDescription = "Edit Status", tint = Muted, modifier = Modifier.size(16.dp))
                    }
                }
            }

            if (genderFilter != "All") {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Filter: $genderFilter · $scopeMode", color = Color(0xFFC4B5FD), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Reset",
                            color = Pink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { genderFilter = "All" }
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }

            items(filteredStatuses, key = { it.id }) { person ->
                val isFriend = friends.contains(person.name)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChat(person.name) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(54.dp)) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(person.colors)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                person.name.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8))
                                .border(2.dp, DarkBg, CircleShape)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 14.dp, end = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                person.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (person.isFemale) Pink else Color(0xFF0EA5E9))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "${if (person.isFemale) "♀" else "♂"} ${person.age}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            person.status,
                            color = Muted,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (!isFriend) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkSurface2)
                                .clickable { onAddFriend(person.name) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Add friend", tint = Color(0xFFC4B5FD), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Floating "+ Status" pill
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFF2E2D38))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(26.dp))
                .clickable { onOpenStatusModal() }
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Text("Status", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun SectionTitle(title: String, action: String, onActionClick: (() -> Unit)? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 17.sp)
        Text(
            action,
            color = Color(0xFFC4B5FD),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = if (onActionClick != null) Modifier.clickable { onActionClick() } else Modifier
        )
    }
}

@Composable
fun PersonRow(name: String, detail: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Pink, Purple))),
            contentAlignment = Alignment.Center
        ) {
            Text(name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(name, color = Color.White, fontWeight = FontWeight.Bold)
            Text(detail, color = Muted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
    listeners: Int = 5,
    mood: RoomMood,
    color: Color,
    isReported: Boolean = false,
    onClick: () -> Unit = {},
    onReport: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        DarkCard,
                        color.copy(alpha = 0.24f),
                        mood.gradientEndColor.copy(alpha = 0.18f)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(62.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(color, mood.gradientEndColor))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    owner.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color)
                    .padding(horizontal = 7.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(mood.icon, contentDescription = mood.label, tint = Color.White, modifier = Modifier.size(10.dp))
                    Text(mood.label, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AvatarBadge(name = owner, size = 18.dp, fontSize = 9.sp)
                Text(
                    owner,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("· $detail", color = Muted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.BarChart, contentDescription = "Live listeners", tint = Purple, modifier = Modifier.size(15.dp))
                Text(
                    listeners.toString(),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface2)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text("JOIN", color = Color(0xFFC4B5FD), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isReported) Pink else DarkSurface2)
                        .clickable { onReport() }
                        .padding(horizontal = 7.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(Icons.Default.Flag, contentDescription = "Report Room", tint = Color.White, modifier = Modifier.size(11.dp))
                    Text(
                        if (isReported) "Reported" else "Report Room",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 9.sp
                    )
                }
            }
        }
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
            .padding(horizontal = 16.dp, vertical = 14.dp),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        color = DarkCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "No rooms found for this mood",
                color = Color.White,
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
                    Text("All", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                OutlinedButton(
                    onClick = onCreateRoom,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp),
                    border = BorderStroke(1.dp, Purple.copy(alpha = 0.45f))
                ) {
                    Text("Create Party", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
    onOpenFamily: () -> Unit,
    onOpenShop: () -> Unit,
    onCreateRoom: (String, RoomMood, String, String, String, Boolean) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newRoomTitle by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf(RoomMood.HAPPY) }
    var selectedBg by remember { mutableStateOf("lounge") }
    var selectedPartyMode by remember { mutableStateOf("Free Mic") }
    var welcomeMessage by remember { mutableStateOf("") }
    var isLocked by remember { mutableStateOf(false) }
    var moodFilter by remember { mutableStateOf<RoomMood?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf(PartySortOrder.POPULAR) }
    var showSortMenu by remember { mutableStateOf(false) }
    var activePartyTab by remember { mutableStateOf("Hot") }
    var showSearchField by remember { mutableStateOf(true) }

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
            val effectiveSort = if (activePartyTab == "New" || activePartyTab == "Recent") PartySortOrder.NEWEST else sortOrder
            when (effectiveSort) {
                PartySortOrder.POPULAR -> list.sortedWith(compareByDescending<VoiceRoom> { it.listeners }.thenByDescending { it.createdAt })
                PartySortOrder.NEWEST -> list.sortedWith(compareByDescending<VoiceRoom> { it.createdAt }.thenByDescending { it.listeners })
            }
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 86.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Party Chat", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkCard)
                                .clickable { onOpenAddMusic() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = "Add Music ($musicCount)", tint = Color(0xFFA78BFA), modifier = Modifier.size(20.dp))
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp)
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Pink)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkCard)
                                .clickable { showSearchField = !showSearchField },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Toggle Search", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            // 2026 Annual Points Event Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF7C3AED), Color(0xFFEC4899), Color(0xFFF59E0B))
                            )
                        )
                        .clickable { onOpenShop() }
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "2026 ANNUAL POINTS",
                            color = Color(0xFFFEF08A),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Complete tasks to earn points, win a Deluxe Lucky Set & redeem rewards!",
                            color = Color.White.copy(alpha = 0.92f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 3 Quick Shortcut Icons: Ranking, Family, Shop
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { sortOrder = PartySortOrder.POPULAR }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(26.dp))
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("Ranking", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onOpenFamily() }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = Purple, modifier = Modifier.size(26.dp))
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("Family", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onOpenShop() }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Pink, modifier = Modifier.size(26.dp))
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("Shop", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Tab Strip: Following | For U | Hot | New | Recent
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Following", "For U", "Hot", "New", "Recent").forEach { tab ->
                        val active = activePartyTab == tab
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { activePartyTab = tab }
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        ) {
                            Text(
                                tab,
                                color = if (active) Color(0xFFA78BFA) else Muted,
                                fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(3.dp)
                                    .clip(CircleShape)
                                    .background(if (active) Purple else Color.Transparent)
                            )
                        }
                    }
                }
            }

            // Search Bar & Sort Dropdown
            if (showSearchField) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Search title or host...", fontSize = 13.sp, color = Muted, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search rooms", tint = Color(0xFFA78BFA))
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
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Purple,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                                focusedContainerColor = DarkCard,
                                unfocusedContainerColor = DarkCard
                            )
                        )
                        Box {
                            Row(
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 52.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DarkCard)
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                                    .clickable { showSortMenu = true }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.SwapVert, contentDescription = "Sort rooms", tint = Color(0xFFA78BFA), modifier = Modifier.size(16.dp))
                                Text(
                                    sortOrder.label,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Muted, modifier = Modifier.size(18.dp))
                            }
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false },
                                modifier = Modifier.background(DarkCard)
                            ) {
                                PartySortOrder.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                option.label,
                                                fontWeight = if (sortOrder == option) FontWeight.Bold else FontWeight.Medium,
                                                color = if (sortOrder == option) Color(0xFFA78BFA) else Color.White
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
            }

            // Mood Filter Pills
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val allSelected = moodFilter == null
                    Box(
                        modifier = Modifier
                            .defaultMinSize(minHeight = 40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (allSelected) Purple else DarkCard)
                            .clickable { moodFilter = null }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "All",
                            color = if (allSelected) Color.White else Muted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                    RoomMood.entries.forEach { mood ->
                        val selected = moodFilter == mood
                        Row(
                            modifier = Modifier
                                .defaultMinSize(minHeight = 40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) mood.cardColor else DarkCard)
                                .clickable { moodFilter = if (selected) null else mood }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                mood.icon,
                                contentDescription = null,
                                tint = if (selected) Color.White else mood.cardColor,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                mood.label,
                                color = if (selected) Color.White else Muted,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
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
                        listeners = room.listeners,
                        mood = room.mood,
                        color = room.color,
                        isReported = reportedRoomIds.contains(room.id),
                        onClick = { onJoinRoom(room) },
                        onReport = { onReportRoom(room) }
                    )
                }
            }
        }

        // Floating Create Party | Random dock pill
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF282732).copy(alpha = 0.96f))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(28.dp))
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable { showCreateDialog = true }
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Purple, Pink))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Text("Create Party", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Box(Modifier.width(1.dp).height(20.dp).background(Color.White.copy(alpha = 0.14f)))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable {
                    if (filteredRooms.isNotEmpty()) onJoinRoom(filteredRooms.random())
                    else if (rooms.isNotEmpty()) onJoinRoom(rooms.random())
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF38BDF8)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Casino, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Text("Random", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }

    if (showCreateDialog) {
        val activeBgPreset = PartyBackgroundCatalog.firstOrNull { it.id == selectedBg } ?: PartyBackgroundCatalog.first()
        val previewColors = if (selectedBg == "mood") moodRoomGradientColors(selectedMood) else activeBgPreset.colors
        val titleIdeas = listOf("Chill & Chat ☕", "Khmer Music Night 🎶", "Late Night Karaoke 🎤", "Truth or Dare 🎲")
        val modeOptions = listOf("Free Mic", "Music Lounge", "Karaoke", "Truth or Dare")

        Dialog(
            onDismissRequest = { showCreateDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .heightIn(max = 680.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1D1C24),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Brush.linearGradient(listOf(Purple, Pink))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("Create Party", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                Text("Customize your live voice room", color = Muted, fontSize = 11.sp)
                            }
                        }
                        IconButton(onClick = { showCreateDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Muted)
                        }
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Live Room Preview Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Brush.linearGradient(previewColors))
                                .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(18.dp))
                        ) {
                            if (activeBgPreset.drawableRes != null) {
                                Image(
                                    painter = painterResource(id = activeBgPreset.drawableRes),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    alpha = 0.35f,
                                    modifier = Modifier.matchParentSize()
                                )
                            }
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(selectedMood.cardColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(selectedMood.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            selectedPartyMode.uppercase(),
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color.White.copy(alpha = 0.16f))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                        if (isLocked) {
                                            Text(
                                                "LOCKED",
                                                color = Color(0xFFFDE68A),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(Color(0xFFFBBF24).copy(alpha = 0.22f))
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        newRoomTitle.trim().ifEmpty { "My Happy Party" },
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "Host: You · ${selectedMood.label} · ${activeBgPreset.label}",
                                        color = Color.White.copy(alpha = 0.78f),
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Party Name + quick ideas
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Party Name", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                            OutlinedTextField(
                                value = newRoomTitle,
                                onValueChange = { if (it.length <= 48) newRoomTitle = it },
                                placeholder = { Text("Enter a party title (e.g. Chill Vibes ☕)", color = Muted, fontSize = 13.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Purple,
                                    unfocusedBorderColor = DarkSurface2,
                                    focusedContainerColor = DarkBg,
                                    unfocusedContainerColor = DarkBg
                                )
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                titleIdeas.forEach { idea ->
                                    Text(
                                        idea,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(DarkSurface2)
                                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                                            .clickable { newRoomTitle = idea }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Party Mode
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Party Mode", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                            modeOptions.chunked(2).forEach { rowModes ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    rowModes.forEach { mode ->
                                        val selected = selectedPartyMode == mode
                                        Row(
                                            modifier = Modifier
                                                .weight(1f)
                                                .defaultMinSize(minHeight = 40.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (selected) Purple.copy(alpha = 0.22f) else DarkSurface2)
                                                .border(
                                                    1.dp,
                                                    if (selected) Purple else Color.White.copy(alpha = 0.08f),
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .clickable { selectedPartyMode = mode }
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(mode, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            if (selected) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(15.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Mood Tag
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Mood Tag", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
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
                                                .defaultMinSize(minHeight = 42.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(if (isSelected) mood.cardColor else DarkSurface2)
                                                .clickable { selectedMood = mood }
                                                .padding(horizontal = 12.dp, vertical = 9.dp),
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
                                                color = Color.White,
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

                        // Room Background
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Room Background", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
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
                                            .background(if (isBgSelected) Purple.copy(alpha = 0.25f) else DarkSurface2)
                                            .border(
                                                width = if (isBgSelected) 1.5.dp else 1.dp,
                                                color = if (isBgSelected) Purple else Color.White.copy(alpha = 0.08f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { selectedBg = preset.id }
                                            .padding(horizontal = 10.dp, vertical = 9.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clip(RoundedCornerShape(5.dp))
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
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        // Welcome Wording & Privacy Lock
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Welcome Wording (Optional)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                            OutlinedTextField(
                                value = welcomeMessage,
                                onValueChange = { if (it.length <= 80) welcomeMessage = it },
                                placeholder = { Text("Welcome everyone! Grab a mic 🎙️", color = Muted, fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Purple,
                                    unfocusedBorderColor = DarkSurface2,
                                    focusedContainerColor = DarkBg,
                                    unfocusedContainerColor = DarkBg
                                )
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkSurface2)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                                .clickable { isLocked = !isLocked }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = if (isLocked) Color(0xFFFBBF24) else Color(0xFF34D399),
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        if (isLocked) "Locked Party (Approval Needed)" else "Open Party (Anyone Can Join)",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        if (isLocked) "Listeners need your invite to take a mic" else "Guests can hop on open mic seats",
                                        color = Muted,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Switch(
                                checked = isLocked,
                                onCheckedChange = { isLocked = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Purple
                                )
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF18171F))
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showCreateDialog = false },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.defaultMinSize(minHeight = 46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurface2)
                        ) {
                            Text("Cancel", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.horizontalGradient(listOf(Purple, Pink)))
                                .clickable {
                                    val cleanTitle = newRoomTitle.trim().ifEmpty { "My Happy Party" }
                                    onCreateRoom(
                                        cleanTitle,
                                        selectedMood,
                                        selectedBg,
                                        selectedPartyMode,
                                        welcomeMessage.trim(),
                                        isLocked
                                    )
                                    newRoomTitle = ""
                                    selectedMood = RoomMood.HAPPY
                                    selectedBg = "lounge"
                                    selectedPartyMode = "Free Mic"
                                    welcomeMessage = ""
                                    isLocked = false
                                    showCreateDialog = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Create Party", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MomentsFeedScreen(
    posts: List<MomentPost>,
    likedPostIds: List<String>,
    followingAuthors: List<String>,
    onToggleLike: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onSharePost: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onSayHi: (String) -> Unit,
    onOpenCompose: () -> Unit
) {
    var activeTab by remember { mutableStateOf("For You") }
    var selectedTopicId by remember { mutableStateOf<String?>(null) }
    var openCommentPostId by remember { mutableStateOf<String?>(null) }
    var commentDraft by remember { mutableStateOf("") }

    val filteredPosts = remember(posts.size, activeTab, selectedTopicId, followingAuthors.size) {
        posts.filter { post ->
            val topicOk = selectedTopicId == null || post.topicId == selectedTopicId
            val tabOk = when (activeTab) {
                "Following" -> followingAuthors.contains(post.author) || post.author == "You" || post.tab == "following"
                "Latest" -> post.tab == "latest" || post.author == "You" || post.likes < 20
                else -> true
            }
            topicOk && tabOk
        }
    }

    Box(Modifier.fillMaxSize().background(DarkBg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 86.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        listOf("Following", "For You", "Latest").forEach { tab ->
                            val isActive = activeTab == tab
                            Row(
                                modifier = Modifier.clickable { activeTab = tab },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    tab,
                                    color = if (isActive) Color.White else Muted,
                                    fontSize = if (isActive) 19.sp else 17.sp,
                                    fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.SemiBold
                                )
                                if (tab == "Following") {
                                    Spacer(Modifier.width(4.dp))
                                    Box(Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFFF4D6D)))
                                }
                            }
                        }
                    }
                    Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }

            // Topic circles strip
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    MomentTopicsCatalog.forEach { topic ->
                        val picked = selectedTopicId == topic.id
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(68.dp)
                                .clickable {
                                    selectedTopicId = if (picked) null else topic.id
                                }
                        ) {
                            Box(modifier = Modifier.size(58.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(58.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(topic.colors))
                                        .border(if (picked) 2.5.dp else 1.dp, if (picked) Color.White else Color.White.copy(alpha = 0.25f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(topic.emoji, fontSize = 24.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Purple)
                                        .border(1.5.dp, DarkBg, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("#", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                topic.label,
                                color = if (picked) Color.White else Muted,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            items(filteredPosts, key = { it.id }) { post ->
                val isLiked = likedPostIds.contains(post.id)
                val isFollowing = followingAuthors.contains(post.author)
                val commentsOpen = openCommentPostId == post.id

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(Color(0xFF60A5FA), Purple))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(post.author.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (post.isFemale) Pink else Color(0xFF0EA5E9))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    "${if (post.isFemale) "♀" else "♂"} ${post.age}",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(post.author, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(post.timeAgo, color = Muted, fontSize = 11.sp)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isFollowing) Purple.copy(alpha = 0.25f) else DarkSurface2)
                                .clickable { onToggleFollow(post.author) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                if (isFollowing) "Following" else "Follow",
                                color = if (isFollowing) Color(0xFFC4B5FD) else Color(0xFFA78BFA),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Muted, modifier = Modifier.size(18.dp))
                    }

                    Spacer(Modifier.height(10.dp))

                    if (post.caption.isNotBlank()) {
                        Text(
                            post.caption,
                            color = Color.White.copy(alpha = 0.94f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(start = 58.dp, bottom = if (post.hasPhotoBanner) 10.dp else 6.dp)
                        )
                    }

                    if (post.hasPhotoBanner) {
                        Box(
                            modifier = Modifier
                                .padding(start = 58.dp)
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.linearGradient(post.bannerColors)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(post.imageEmoji ?: "✨", fontSize = 54.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                    }

                    Row(
                        modifier = Modifier
                            .padding(start = 58.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(16.dp))
                                .clickable { onSayHi(post.author) }
                                .padding(horizontal = 14.dp, vertical = 5.dp)
                        ) {
                            Text("Say Hi", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.clickable { onToggleLike(post.id) }
                            ) {
                                Icon(
                                    if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like",
                                    tint = if (isLiked) Pink else Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(post.likes.toString(), color = Muted, fontSize = 12.sp)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.clickable {
                                    openCommentPostId = if (commentsOpen) null else post.id
                                }
                            ) {
                                Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Comment", tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
                                Text(post.comments.size.toString(), color = Muted, fontSize = 12.sp)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.clickable { onSharePost(post.id) }
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
                                if (post.shares > 0) {
                                    Text(post.shares.toString(), color = Muted, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    if (commentsOpen) {
                        Column(
                            modifier = Modifier
                                .padding(start = 58.dp, top = 10.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkCard)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            post.comments.forEach { comment ->
                                Text(
                                    "${comment.author}: ${comment.text}",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedTextField(
                                    value = commentDraft,
                                    onValueChange = { commentDraft = it },
                                    placeholder = { Text("Add a comment...", fontSize = 12.sp, color = Muted) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = DarkBg,
                                        unfocusedContainerColor = DarkBg
                                    )
                                )
                                IconButton(onClick = {
                                    if (commentDraft.isNotBlank()) {
                                        onAddComment(post.id, commentDraft)
                                        commentDraft = ""
                                    }
                                }) {
                                    Icon(Icons.Default.Send, contentDescription = "Post comment", tint = Purple)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                }
            }
        }

        // Floating Compose Button
        FloatingActionButton(
            onClick = onOpenCompose,
            containerColor = Purple,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 18.dp)
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Compose Moment")
        }
    }
}

@Composable
private fun PartyMicSeatTile(
    seatIdx: Int,
    occupant: String?,
    hearts: Int,
    isTopRow: Boolean,
    isMuted: Boolean,
    onTapSeat: () -> Unit,
    onTapHeart: () -> Unit
) {
    val isMe = occupant == "You"
    val isHost = seatIdx == 0
    val isSpeaking = occupant != null && (if (isMe) !isMuted else isHost)
    val seatSize = if (isTopRow) 62.dp else 54.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.width(68.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.clickable { onTapSeat() }
        ) {
            if (occupant != null) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(seatSize)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    if (isSpeaking) listOf(Color(0xFF38BDF8), Purple, Pink)
                                    else listOf(Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.15f))
                                )
                            )
                            .padding(2.5.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF6D28D9), Color(0xFFBE185D)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            occupant.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = if (isTopRow) 20.sp else 17.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(19.dp)
                            .clip(CircleShape)
                            .background(if (isSpeaking) Color(0xFF38BDF8) else Color(0xFF1E1930))
                            .border(1.dp, Color(0xFF140E26), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isSpeaking) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = null,
                            tint = if (isSpeaking) Color(0xFF0F172A) else Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(seatSize)
                        .clip(CircleShape)
                        .background(Color(0xA6241E3E))
                        .border(1.dp, Color.White.copy(alpha = 0.16f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Chair,
                        contentDescription = "Join seat",
                        tint = Color.White.copy(alpha = 0.78f),
                        modifier = Modifier.size(if (isTopRow) 24.dp else 20.dp)
                    )
                }
            }
        }

        if (occupant != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                if (isHost) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Purple),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Flag, contentDescription = null, tint = Color.White, modifier = Modifier.size(9.dp))
                    }
                }
                Text(
                    occupant,
                    color = if (isMe) Color(0xFFFFD77A) else Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xD93A2848))
                    .clickable { onTapHeart() }
                    .padding(horizontal = 7.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Heart", tint = Color(0xFFEC4899), modifier = Modifier.size(10.dp))
                Text("$hearts", color = Color(0xFFF9A8D4), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .clickable { onTapSeat() }
                    .padding(horizontal = 12.dp, vertical = 2.dp)
            ) {
                Text("Join", color = Color.White.copy(alpha = 0.92f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
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
    val seats = remember(room.id) {
        mutableStateListOf<String?>().apply {
            val hostName = room.owner.ifBlank { room.speakers.firstOrNull() ?: "Mina" }
            add(hostName)
            repeat(9) { add(null) }
        }
    }
    val seatHearts = remember(room.id) {
        mutableStateListOf<Int>().apply { repeat(10) { add(0) } }
    }
    var isMuted by remember { mutableStateOf(false) }
    var roomInput by remember { mutableStateOf("") }
    var lastReaction by remember { mutableStateOf<String?>(null) }
    var showReactionTray by remember { mutableStateOf(false) }
    var showGiftSheet by remember { mutableStateOf(false) }
    var showPartyChallengeDialog by remember { mutableStateOf(false) }
    var challengeLevel by remember { mutableIntStateOf(1) }
    var showBgSheet by remember { mutableStateOf(false) }
    var showOffMicCard by remember { mutableStateOf(true) }
    var showRoomMenu by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }
    var showPartyModeDialog by remember { mutableStateOf(false) }
    var partyMode by remember(room.id) { mutableStateOf(room.partyMode) }
    var isPartyLocked by remember(room.id) { mutableStateOf(room.isLocked) }
    var blockAnimations by remember(room.id) { mutableStateOf(false) }
    val invitedFriends = remember(room.id) { mutableStateListOf<String>() }
    var roomTitleText by remember(room.id) { mutableStateOf(room.title) }
    var editingRoomTitle by remember { mutableStateOf(false) }
    var welcomeWording by remember(room.id) { mutableStateOf(room.welcomeMessage) }
    var editingWelcome by remember { mutableStateOf(false) }
    var copiedRoomId by remember { mutableStateOf(false) }
    var selectedGift by remember { mutableStateOf(KhmerGiftCatalog.first()) }
    val giftRecipients = remember(room.speakers) {
        room.speakers.filter { it != "You" }.ifEmpty { listOf("Mina", "Dara", "Sophy") }
    }
    var selectedRecipient by remember(giftRecipients) { mutableStateOf(giftRecipients.first()) }
    var lastGiftBanner by remember { mutableStateOf<String?>(null) }

    val roomChat = remember(room.id) {
        val hostName = room.owner.ifBlank { room.speakers.firstOrNull() ?: "Mina" }
        mutableStateListOf(
            "SYS:$hostName" to "is on the mic."
        )
    }

    val numericRoomId = remember(room.id) {
        var hash = 71465514L
        for (ch in room.id) {
            hash = (hash * 31L + ch.code) % 900000000L
        }
        (kotlin.math.abs(hash) + 100000000L).toString().take(9)
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

    val mySeatIndex = seats.indexOf("You")
    val onStage = mySeatIndex >= 0
    val offMicUsers = room.speakers.filter { it != "You" && !seats.contains(it) }
    val totalHearts = seatHearts.sum()
    val heatDisplay = String.format(Locale.US, "%.2fK", 19.95 + totalHearts * 0.01 + if (onStage) 0.05 else 0.0)
    val chestProgress = ((18 + totalHearts * 8 + roomChat.size * 4).coerceAtMost(100)) / 100f

    val handleJoinSeat: (Int) -> Unit = { idx ->
        val occupant = seats[idx]
        if (occupant == "You") {
            isMuted = !isMuted
        } else if (occupant != null) {
            seatHearts[idx] = seatHearts[idx] + 1
            roomChat.add("You" to "Sent ❤️ to $occupant")
        } else if (isPartyLocked) {
            roomChat.add("SYS:System" to "🔒 Party is currently locked by host.")
        } else {
            val prevIdx = seats.indexOf("You")
            if (prevIdx >= 0) seats[prevIdx] = null
            seats[idx] = "You"
            isMuted = false
            roomChat.add("SYS:You" to "joined mic seat #${idx + 1}.")
        }
    }

    val handleToggleQuickSeat: () -> Unit = {
        if (onStage) {
            if (mySeatIndex >= 0) seats[mySeatIndex] = null
            isMuted = true
            roomChat.add("SYS:You" to "stepped down from the mic.")
        } else {
            val openIdx = seats.indexOfFirst { it == null }
            if (openIdx >= 0) handleJoinSeat(openIdx)
        }
    }

    val activeBgPreset = PartyBackgroundCatalog.firstOrNull { it.id == room.backgroundId } ?: PartyBackgroundCatalog[1]
    val hasCustomPhoto = room.backgroundId == "custom" && room.customBackgroundUri != null
    val hasImageBackdrop = hasCustomPhoto || activeBgPreset.drawableRes != null
    val bgGradientColors = if (room.backgroundId == "mood") moodRoomGradientColors(room.mood) else activeBgPreset.colors

    LaunchedEffect(copiedRoomId) {
        if (copiedRoomId) {
            delay(1500)
            copiedRoomId = false
        }
    }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        if (hasImageBackdrop) {
                            listOf(Color(0xCC110B22), Color(0xA6150E2A), Color(0xEB0D0919))
                        } else {
                            listOf(Color(0x4021163E), Color(0x88150E2A), Color(0xE60B0815))
                        }
                    )
                )
        )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onLeave) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.clickable { editingRoomTitle = true }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit title", tint = Color.White.copy(alpha = 0.78f), modifier = Modifier.size(14.dp))
                            Text(
                                roomTitleText.ifBlank { room.title },
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text("ID: $numericRoomId", color = Color.White.copy(alpha = 0.65f), fontSize = 11.sp)
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy ID",
                                tint = Color.White.copy(alpha = 0.65f),
                                modifier = Modifier
                                    .size(13.dp)
                                    .clickable {
                                        val cm = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        cm.setPrimaryClip(android.content.ClipData.newPlainText("Room ID", numericRoomId))
                                        copiedRoomId = true
                                    }
                            )
                            if (copiedRoomId) {
                                Text("Copied", color = Color(0xFF70D9FF), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                            if (isPartyLocked) {
                                Text("🔒 Locked", color = Color(0xFFFFD77A), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                            if (partyMode != "Free Mic") {
                                Text("· $partyMode", color = Color(0xFFC4B5FD), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.14f))
                            .clickable { showOffMicCard = !showOffMicCard },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${offMicUsers.size}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Box {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.14f))
                                .clickable { showRoomMenu = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MoreHoriz, contentDescription = "Room options", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(
                            expanded = showRoomMenu,
                            onDismissRequest = { showRoomMenu = false },
                            modifier = Modifier
                                .width(220.dp)
                                .background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Invite Friend", color = Color(0xFF1E1C28), fontSize = 15.sp, fontWeight = FontWeight.Medium) },
                                leadingIcon = { Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFF2D2A3A), modifier = Modifier.size(20.dp)) },
                                onClick = {
                                    showRoomMenu = false
                                    showInviteDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Party Mode", color = Color(0xFF1E1C28), fontSize = 15.sp, fontWeight = FontWeight.Medium) },
                                leadingIcon = { Icon(Icons.Default.Widgets, contentDescription = null, tint = Color(0xFF2D2A3A), modifier = Modifier.size(20.dp)) },
                                onClick = {
                                    showRoomMenu = false
                                    showPartyModeDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Decoration", color = Color(0xFF1E1C28), fontSize = 15.sp, fontWeight = FontWeight.Medium) },
                                leadingIcon = { Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Color(0xFF2D2A3A), modifier = Modifier.size(20.dp)) },
                                onClick = {
                                    showRoomMenu = false
                                    showBgSheet = true
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (isPartyLocked) "Unlock Party" else "Lock Party",
                                        color = Color(0xFF1E1C28),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        if (isPartyLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (isPartyLocked) Purple else Color(0xFF2D2A3A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                onClick = {
                                    val nextLocked = !isPartyLocked
                                    isPartyLocked = nextLocked
                                    showRoomMenu = false
                                    roomChat.add(
                                        "SYS:System" to if (nextLocked) "🔒 Party locked by host." else "🔓 Party unlocked."
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (blockAnimations) "Allow Animation" else "Block Animation",
                                        color = Color(0xFF1E1C28),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        if (blockAnimations) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = if (blockAnimations) Purple else Color(0xFF2D2A3A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                onClick = {
                                    val nextBlocked = !blockAnimations
                                    blockAnimations = nextBlocked
                                    if (nextBlocked) lastGiftBanner = null
                                    showRoomMenu = false
                                    roomChat.add(
                                        "SYS:System" to if (nextBlocked) "🚫 Gift & entry animations blocked." else "✨ Animations enabled."
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (showOffMicCard) "Close Off-mic" else "Open Off-mic",
                                        color = Color(0xFF1E1C28),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        if (showOffMicCard) Icons.Default.VoiceOverOff else Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = if (showOffMicCard) Color(0xFF2D2A3A) else Purple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                onClick = {
                                    showOffMicCard = !showOffMicCard
                                    showRoomMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Exit", color = Color(0xFF1E1C28), fontSize = 15.sp, fontWeight = FontWeight.Medium) },
                                leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFF2D2A3A), modifier = Modifier.size(20.dp)) },
                                onClick = {
                                    showRoomMenu = false
                                    onLeave()
                                }
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x55D97706))
                                .border(1.dp, Color(0x55F59E0B), RoundedCornerShape(16.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(14.dp))
                            Text(heatDisplay, color = Color(0xFFFDE68A), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x55000000))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                .clickable { showPartyChallengeDialog = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🧰", fontSize = 13.sp)
                            LinearProgressIndicator(
                                progress = { chestProgress },
                                modifier = Modifier.width(42.dp).height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFFF59E0B),
                                trackColor = Color.White.copy(alpha = 0.18f)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x55000000))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                                .clickable { showMusicSheet = true }
                                .padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(Icons.Default.Album, contentDescription = null, tint = Color(0xFF70D9FF), modifier = Modifier.size(13.dp))
                            Text(
                                if (isMusicPlaying) currentTrack.title else "Music",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 96.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0x55000000))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                                .clickable { showBgSheet = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = "Background", tint = Color(0xFFFBBF24), modifier = Modifier.size(17.dp))
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(5.dp)
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xD90D0919))
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                if (showReactionTray) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xF01E1638))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("❤️", "👏", "🔥", "😂", "🎉").forEach { emoji ->
                                Text(
                                    emoji,
                                    fontSize = 20.sp,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable {
                                            lastReaction = emoji
                                            roomChat.add("You" to "Sent $emoji")
                                            showReactionTray = false
                                        }
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .clickable {
                                if (!isMusicPlaying) {
                                    isMusicPlaying = true
                                } else {
                                    isMusicMuted = !isMusicMuted
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isMusicMuted || !isMusicPlaying) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Room audio",
                            tint = if (isMusicMuted || !isMusicPlaying) Color.White else Color(0xFF70D9FF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (onStage && !isMuted) Purple else Color.White.copy(alpha = 0.12f))
                            .clickable {
                                if (!onStage) handleToggleQuickSeat() else isMuted = !isMuted
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (onStage && !isMuted) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = "Microphone",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.text.BasicTextField(
                            value = roomInput,
                            onValueChange = { roomInput = it },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(Color.White),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                val trimmed = roomInput.trim()
                                if (trimmed.isNotEmpty()) {
                                    roomChat.add("You" to trimmed)
                                    roomInput = ""
                                }
                            }),
                            decorationBox = { innerTextField ->
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                                    if (roomInput.isEmpty()) {
                                        Text("Enter", color = Color.White.copy(alpha = 0.55f), fontSize = 13.sp)
                                    }
                                    innerTextField()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        if (roomInput.isNotBlank()) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color(0xFF70D9FF),
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        val trimmed = roomInput.trim()
                                        if (trimmed.isNotEmpty()) {
                                            roomChat.add("You" to trimmed)
                                            roomInput = ""
                                        }
                                    }
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .clickable { showReactionTray = !showReactionTray },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.EmojiEmotions, contentDescription = "Reactions", tint = Color.White, modifier = Modifier.size(19.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3D2520))
                            .border(1.dp, Color(0x66F59E0B), CircleShape)
                            .clickable { showMusicSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MusicNote, contentDescription = "Party Music", tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFFF472B6), Purple)))
                            .clickable { showGiftSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = "Send Gift", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .clickable { showBgSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Background & Menu", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (!blockAnimations && lastGiftBanner != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF68468E), Color(0xFF3F2B63))))
                        .padding(horizontal = 12.dp, vertical = 7.dp),
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

            // 10-Seat Interactive Stage (2 + 4 + 4 layout)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(28.dp, Alignment.CenterHorizontally)
                ) {
                    listOf(0, 1).forEach { seatIdx ->
                        PartyMicSeatTile(
                            seatIdx = seatIdx,
                            occupant = seats[seatIdx],
                            hearts = seatHearts[seatIdx],
                            isTopRow = true,
                            isMuted = isMuted,
                            onTapSeat = { handleJoinSeat(seatIdx) },
                            onTapHeart = { seatHearts[seatIdx] = seatHearts[seatIdx] + 1 }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(2, 3, 4, 5).forEach { seatIdx ->
                        PartyMicSeatTile(
                            seatIdx = seatIdx,
                            occupant = seats[seatIdx],
                            hearts = seatHearts[seatIdx],
                            isTopRow = false,
                            isMuted = isMuted,
                            onTapSeat = { handleJoinSeat(seatIdx) },
                            onTapHeart = { seatHearts[seatIdx] = seatHearts[seatIdx] + 1 }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(6, 7, 8, 9).forEach { seatIdx ->
                        PartyMicSeatTile(
                            seatIdx = seatIdx,
                            occupant = seats[seatIdx],
                            hearts = seatHearts[seatIdx],
                            isTopRow = false,
                            isMuted = isMuted,
                            onTapSeat = { handleJoinSeat(seatIdx) },
                            onTapHeart = { seatHearts[seatIdx] = seatHearts[seatIdx] + 1 }
                        )
                    }
                }
            }

            if (showOffMicCard) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xEB090711))
                        .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Off-mic Users(${offMicUsers.size})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(18.dp)
                                .clickable { showOffMicCard = false }
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    if (offMicUsers.isEmpty()) {
                        Text(
                            "No off-mic users yet, gather some friends here~",
                            color = Color.White.copy(alpha = 0.78f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp)
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            offMicUsers.forEach { person ->
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Color.White.copy(alpha = 0.12f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    AvatarBadge(name = person, size = 20.dp, fontSize = 10.sp)
                                    Text(person, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            // Chat Feed, Welcome Wording, Party Tips & Floating Right Buttons
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 56.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 8.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x73000000))
                                .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                                .clickable { editingWelcome = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Automatic Welcome Wording", color = Color(0xFFFDE68A), fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                Icon(Icons.Default.Edit, contentDescription = "Edit welcome", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(13.dp))
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(welcomeWording, color = Color.White.copy(alpha = 0.88f), fontSize = 12.sp)
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x73000000))
                                .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("Party Tips:", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text(
                                "Tell you a secret about using room heating - after heating, the more people chatting on the mic in your room and the more attention you receive, the room will have the opportunity to get more traffic in the future:)",
                                color = Color(0xFF38BDF8).copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                    items(roomChat) { entry ->
                        val isSys = entry.first.startsWith("SYS:")
                        val senderName = if (isSys) entry.first.removePrefix("SYS:") else entry.first
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x80000000))
                                .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            if (isSys) {
                                Text("$senderName ", color = Color(0xFF93C5FD), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(entry.second, color = Color.White.copy(alpha = 0.92f), fontSize = 12.sp)
                            } else {
                                Text("$senderName: ", color = Pink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(entry.second, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x66000000))
                                .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text("Party Tips:", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("Please wear headphones to avoid echoes.", color = Color(0xFF38BDF8).copy(alpha = 0.9f), fontSize = 11.sp)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (lastReaction != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x99000000))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(lastReaction!!, fontSize = 14.sp)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (onStage) Color(0xFF38BDF8) else Color(0xE616192C))
                            .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
                            .clickable { handleToggleQuickSeat() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Chair,
                            contentDescription = "Quick mic seat",
                            tint = if (onStage) Color(0xFF0F172A) else Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFFF59E0B), Pink, Purple)))
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1C1433))
                            .clickable { showMusicSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Album, contentDescription = "Party Music", tint = Color(0xFFFFD77A), modifier = Modifier.size(16.dp))
                            Text("MUSIC", color = Color.White, fontSize = 6.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }

    if (editingRoomTitle) {
        AlertDialog(
            onDismissRequest = { editingRoomTitle = false },
            title = { Text("Edit Room Name", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = roomTitleText,
                    onValueChange = { roomTitleText = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = { editingRoomTitle = false }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { editingRoomTitle = false }) { Text("Cancel") }
            }
        )
    }

    if (editingWelcome) {
        AlertDialog(
            onDismissRequest = { editingWelcome = false },
            title = { Text("Automatic Welcome Wording", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = welcomeWording,
                    onValueChange = { welcomeWording = it },
                    placeholder = { Text("Enter welcome message...") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = { editingWelcome = false }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { editingWelcome = false }) { Text("Cancel") }
            }
        )
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

    if (showPartyChallengeDialog) {
        Dialog(
            onDismissRequest = { showPartyChallengeDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .heightIn(max = 680.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1B172B),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("👑", fontSize = 18.sp)
                            Text("Party Challenge", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                        }
                        IconButton(onClick = { showPartyChallengeDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Muted)
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    // Level selector 1 to 10
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (lvl in 1..10) {
                            val active = challengeLevel == lvl
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (active) Purple else DarkSurface2)
                                    .clickable { challengeLevel = lvl }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Level $lvl", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))

                    // Chest graphic & progress
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF2D1E4E), Color(0xFF151128))))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(18.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Level $challengeLevel of 10", color = Color(0xFFFDE68A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text("🧰", fontSize = 42.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Chest Unboxing Rewards", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            Text("${challengeLevel * 1250} / ${challengeLevel * 4000} Room Points", color = Muted, fontSize = 11.sp)
                            Spacer(Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = ((challengeLevel * 1250f) / (challengeLevel * 4000f)).coerceIn(0f, 1f),
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFFFBBF24),
                                trackColor = Color.White.copy(alpha = 0.15f)
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))

                    Text("Unboxing rewards (Level $challengeLevel)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Spacer(Modifier.height(8.dp))

                    val rewardItems = listOf(
                        Triple("Jellyfish", "🪼", 59 + challengeLevel * 10),
                        Triple("Magnifying Glass", "🔍", 9 + challengeLevel * 5),
                        Triple("Gold Ring", "💍", 199 + challengeLevel * 25),
                        Triple("Pinwheel", "🎡", 129 + challengeLevel * 15),
                        Triple("Fireworks", "🎆", 1592 + challengeLevel * 100),
                        Triple("Bugatti Supercar", "🏎️", 15992 + challengeLevel * 1000)
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rewardItems.chunked(3).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { (name, emoji, price) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(DarkSurface2)
                                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                                            .padding(10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(emoji, fontSize = 24.sp)
                                            Spacer(Modifier.height(4.dp))
                                            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text("💎 $price", color = Color(0xFF70D9FF), fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
                                        }
                                    }
                                }
                                if (rowItems.size < 3) {
                                    Spacer(Modifier.weight(1.0f * (3 - rowItems.size)))
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick = {
                            lastGiftBanner = "✨ Unboxed Level $challengeLevel Party Chest rewards!"
                            showPartyChallengeDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24))
                    ) {
                        Text("Claim Level $challengeLevel Reward 🎁", color = Color(0xFF1A122E), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    }
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

    if (showInviteDialog) {
        AlertDialog(
            containerColor = DarkCard,
            onDismissRequest = { showInviteDialog = false },
            title = { Text("Invite Friend to Party", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Mina", "Dara", "Sophy", "Alex", "Nita").forEach { friendName ->
                        val invited = invitedFriends.contains(friendName)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkSurface2)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AvatarBadge(name = friendName, size = 28.dp, fontSize = 12.sp)
                                Text(friendName, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                            Button(
                                onClick = {
                                    invitedFriends.add(friendName)
                                    roomChat.add("You" to "📨 Invited $friendName to join the room!")
                                },
                                enabled = !invited,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Purple,
                                    disabledContainerColor = Color.White.copy(alpha = 0.12f)
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(if (invited) "Invited" else "Invite", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showInviteDialog = false }) {
                    Text("Done", color = Color(0xFFC4B5FD), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showPartyModeDialog) {
        AlertDialog(
            containerColor = DarkCard,
            onDismissRequest = { showPartyModeDialog = false },
            title = { Text("Choose Party Mode", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Free Mic", "Music Lounge", "Karaoke", "Truth or Dare").forEach { modeOption ->
                        val active = partyMode == modeOption
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (active) Purple.copy(alpha = 0.25f) else DarkSurface2)
                                .border(1.dp, if (active) Purple else Color.Transparent, RoundedCornerShape(14.dp))
                                .clickable {
                                    partyMode = modeOption
                                    roomChat.add("SYS:System" to "🎛️ Party Mode set to $modeOption")
                                    showPartyModeDialog = false
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(modeOption, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (active) {
                                Icon(Icons.Default.Check, contentDescription = "Active", tint = Color(0xFFC4B5FD), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPartyModeDialog = false }) {
                    Text("Close", color = Muted)
                }
            }
        )
    }
    }
}

@Composable
private fun ChatListScreen(
    friends: List<String>,
    conversations: Map<String, List<ChatMessage>>,
    claimedFreeDiamonds: Boolean,
    onClaimFreeDiamonds: () -> Unit,
    onOpenFriends: () -> Unit,
    onOpenParty: () -> Unit,
    onOpenGifts: () -> Unit,
    onChat: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onOpenFriends) {
                    Icon(Icons.Default.GridView, contentDescription = "Friends Directory", tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Messages", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text("Swipe to meet more friends ▾", color = Muted, fontSize = 11.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(DarkSurface2)
                        .clickable { onOpenFriends() }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add friends", tint = Color(0xFFFF8BB8), modifier = Modifier.size(15.dp))
                        Text("Add friends", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3 Highlight Story Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Friends Online
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkCard)
                        .clickable { onOpenFriends() }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(54.dp), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFF38BDF8), Purple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Pink)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text("♀ 33", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("Nita", color = Color(0xFFFDE68A), fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text("Friends Online", color = Muted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = Purple, modifier = Modifier.size(12.dp))
                        Text("7", color = Muted, fontSize = 11.sp)
                    }
                }

                // Card 2: You Sent Gift
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkCard)
                        .clickable { onOpenGifts() }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(54.dp), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Pink, Color(0xFF7C3AED)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Pink)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text("♀ 23", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("រាស្រ្តសាមញ្ញ", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("You Sent Gift", color = Muted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = Purple, modifier = Modifier.size(12.dp))
                        Text("5", color = Muted, fontSize = 11.sp)
                    }
                }

                // Card 3: Party Room Followed
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkCard)
                        .clickable { onOpenParty() }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(54.dp), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFFF59E0B), Pink))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Pink)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text("♀ 18", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("Ker Y-Not", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("Party Followed", color = Muted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = Purple, modifier = Modifier.size(12.dp))
                        Text("1", color = Muted, fontSize = 11.sp)
                    }
                }
            }
        }

        // Free Diamonds Promo Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClaimFreeDiamonds() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFFE0E7FF), Color(0xFFC4B5FD)))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Diamond, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(24.dp))
                }
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text("Free Diamonds 💎", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        if (claimedFreeDiamonds) "Claimed +15 diamonds today!" else "Win random diamonds by watching videos",
                        color = Muted,
                        fontSize = 12.sp
                    )
                }
                if (!claimedFreeDiamonds) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF4D6D)))
                } else {
                    Text("Claimed", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // MY HAPPY PLACE Official Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenGifts() }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF34D399), Purple))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Column(Modifier.weight(1f).padding(start = 12.dp, end = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("MY HAPPY PLACE Official", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                    }
                    Text(
                        "Congratulations on completing your daily pass task...",
                        color = Muted,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text("09:58", color = Muted, fontSize = 11.sp)
            }
        }

        // Friends Conversations
        items(friends.size) { index ->
            val name = friends[index]
            val msgs = conversations[name].orEmpty()
            val preview = msgs.lastOrNull()?.text ?: if (index == 0) "[Effect: Balloons]" else "Tap to open conversation"
            val isSoulmate = index == 0 || index == 3
            val isPartyActive = index % 2 == 1
            val age = 21 + (index * 3) % 15

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChat(name) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(52.dp)) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Pink, Purple)))
                            .border(2.dp, if (isSoulmate) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.25f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    }
                    if (isPartyActive) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF7C3AED))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text("• PARTY", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp, end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (index % 2 == 0) Pink else Color(0xFF0EA5E9))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text("${if (index % 2 == 0) "♀" else "♂"} $age", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        if (isSoulmate) {
                            Text(
                                "SOULMATE",
                                color = Color(0xFFE879F9),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                    Text(preview, color = Muted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        if (msgs.isNotEmpty()) formatTimestamp(msgs.last().timestamp) else "14:26",
                        color = Muted,
                        fontSize = 11.sp
                    )
                    if (index == 0 || index == 2) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF4D6D)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("1", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Muted.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                    }
                }
            }
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
        containerColor = DarkBg,
        topBar = {
            Surface(color = DarkCard, shadowElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Pink, Purple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.take(1).uppercase(),
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
                        Text(name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 17.sp)
                        Text("Online", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Muted)
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(DarkCard)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Clear messages", color = Color.White) },
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
            Surface(color = DarkCard, shadowElevation = 4.dp) {
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
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = DarkSurface2,
                            focusedContainerColor = DarkBg,
                            unfocusedContainerColor = DarkBg
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { submitCurrentMessage() })
                    )
                    FilledIconButton(
                        onClick = submitCurrentMessage,
                        enabled = message.isNotBlank(),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Purple,
                            contentColor = Color.White,
                            disabledContainerColor = DarkSurface2,
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
                    textAlign = TextAlign.Center
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
                .background(if (message.isFromMe) Purple else DarkSurface2)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
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
    displayName: String,
    bio: String,
    userStatusText: String,
    userOccupation: String,
    userInterests: List<String>,
    diamonds: Int,
    giftHistory: List<GiftRecord>,
    currentFamily: FamilyClub?,
    friendsCount: Int,
    friendRequestCount: Int,
    rooms: List<VoiceRoom>,
    posts: List<MomentPost>,
    onOpenFriends: () -> Unit,
    onOpenFamily: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenGame: () -> Unit,
    onOpenAddMusic: () -> Unit,
    onOpenStatusModal: () -> Unit,
    onOpenCompose: () -> Unit,
    onJoinMyRoom: () -> Unit,
    onSaveProfile: (String, String, String, List<String>) -> Unit
) {
    var profilePhoto by remember { mutableStateOf<android.net.Uri?>(null) }
    var coverPhoto by remember { mutableStateOf<android.net.Uri?>(null) }
    var historyFilter by remember { mutableStateOf("All") }
    var copiedId by remember { mutableStateOf(false) }
    var showEditModal by remember { mutableStateOf(false) }
    var editName by remember(displayName) { mutableStateOf(displayName) }
    var editBio by remember(bio) { mutableStateOf(bio) }
    var editOccupation by remember(userOccupation) { mutableStateOf(userOccupation) }
    var editInterestInput by remember { mutableStateOf("") }
    var editInterests by remember(userInterests) { mutableStateOf(userInterests) }

    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val pickProfile = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) profilePhoto = it
    }
    val pickCover = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) coverPhoto = it
    }

    val filteredHistory = remember(giftHistory.size, historyFilter) {
        when (historyFilter) {
            "Sent" -> giftHistory.filter { it.isSent }
            "Received" -> giftHistory.filter { !it.isSent }
            else -> giftHistory
        }
    }

    Box(Modifier.fillMaxSize().background(DarkBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 86.dp)
        ) {
            // Cover banner + top action icons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(Color(0xFFEA580C), Color(0xFF7C3AED), Color(0xFF1E1B4B))))
                        .clickable { pickCover.launch("image/*") }
                ) {
                    if (coverPhoto != null) {
                        AsyncImage(
                            model = coverPhoto,
                            contentDescription = "Profile cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.25f), DarkBg.copy(alpha = 0.7f))))
                    )
                }

                // Top translucent toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.45f))
                            .clickable { onOpenShop() }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(Icons.Default.Diamond, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                        Text("Recharge", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            Icons.Default.SportsEsports to onOpenGame,
                            Icons.Default.EmojiEvents to onOpenFamily,
                            Icons.Default.Star to onOpenFriends,
                            Icons.Default.Headphones to onOpenAddMusic,
                            Icons.Default.Settings to { showEditModal = true }
                        ).forEach { (icon, action) ->
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                                    .clickable { action() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            // Avatar & Soulmate CP Widget Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-36).dp)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Pink, Purple)))
                        .border(3.dp, DarkBg, CircleShape)
                        .clickable { pickProfile.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (profilePhoto != null) {
                        AsyncImage(
                            model = profilePhoto,
                            contentDescription = "Your profile photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            displayName.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Soulmate CP Capsule
                Row(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF2D2542))
                        .border(1.dp, Purple.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                        .clickable { onOpenFriends() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("💍", fontSize = 22.sp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AvatarBadge(name = "MaLen", size = 28.dp, fontSize = 11.sp)
                        Text("30.4K", color = Color(0xFFC4B5FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .offset(y = (-24).dp)
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Name + gender/age badge
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(displayName, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0EA5E9))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text("♂ 22", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Copyable ID
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.clickable {
                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString("7146551484"))
                        copiedId = true
                    }
                ) {
                    Text("ID: 7146551484", color = Muted, fontSize = 12.sp)
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy ID", tint = Muted, modifier = Modifier.size(14.dp))
                    if (copiedId) {
                        Text("Copied", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // + Status pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2E2746))
                        .clickable { onOpenStatusModal() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        if (userStatusText.isNotBlank()) "✨ $userStatusText" else "+ Status",
                        color = Color(0xFFC4B5FD),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Bio & Occupation
                Text(bio, color = Color(0xFFC4B5FD), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.clickable { showEditModal = true }
                ) {
                    Text(userOccupation, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Icon(Icons.Default.Edit, contentDescription = "Edit profile", tint = Muted, modifier = Modifier.size(15.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Muted, modifier = Modifier.size(15.dp))
                    Text("Joined March 2026 · 💎 $diamonds", color = Muted, fontSize = 12.sp)
                }

                // Interest chips
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    userInterests.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkCard)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(tag, color = Muted, fontSize = 12.sp)
                        }
                    }
                    IconButton(onClick = { showEditModal = true }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit interests", tint = Muted, modifier = Modifier.size(15.dp))
                    }
                }

                // Stats row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("15 Following", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("203 Follower", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("5.82K Visited", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("${rooms.size} Rooms", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                // My Voice Room Bar
                val myRoomTitle = rooms.firstOrNull()?.title ?: "សូមស្វាគមន៍មកកាន់ room NAN"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkCard)
                        .clickable { onJoinMyRoom() }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(DarkSurface2),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(myRoomTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = Muted, modifier = Modifier.size(13.dp))
                            Text("${rooms.firstOrNull()?.listeners ?: 0}", color = Muted, fontSize = 11.sp)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Muted)
                }

                // Inspiration Card
                Spacer(Modifier.height(6.dp))
                Text("Inspiration", color = Muted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF28223C))
                        .border(1.dp, Purple.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(Purple, Pink))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏮", fontSize = 24.sp)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("🎉 Happy Water Festival Vibes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Share your moment with MY HAPPY PLACE", color = Color(0xFFC4B5FD), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Purple, RoundedCornerShape(16.dp))
                            .clickable { onOpenCompose() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Post now ❤️", color = Color(0xFFC4B5FD), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Pinned Moment Card
                val pinnedPost = posts.firstOrNull()
                if (pinnedPost != null) {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.PushPin, contentDescription = null, tint = Muted, modifier = Modifier.size(15.dp))
                        Text("Pinned · 11-23 22:49", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkCard)
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🥰😘 ${pinnedPost.caption}", color = Color.White, fontSize = 13.sp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(pinnedPost.bannerColors)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(pinnedPost.imageEmoji ?: "💜", fontSize = 36.sp)
                        }
                    }
                }

                // My Friends Card
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DarkCard)
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
                        Text("My Friends", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            if (friendRequestCount > 0) "$friendsCount friends · $friendRequestCount requests" else "$friendsCount friends",
                            color = Muted,
                            fontSize = 12.sp
                        )
                    }
                    Text("Add friends", color = Color(0xFFC4B5FD), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                // My Family Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DarkCard)
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
                            color = Color.White,
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
                    Text("Open", color = Color(0xFFC4B5FD), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                // Gift History Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Gift History", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 17.sp)
                        Text("Khmer traditional gifts sent & received", color = Muted, fontSize = 11.sp)
                    }
                    Text("${filteredHistory.size} items", color = Color(0xFFC4B5FD), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Sent", "Received").forEach { tab ->
                        val active = historyFilter == tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (active) Purple else DarkCard)
                                .clickable { historyFilter = tab }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = tab,
                                color = if (active) Color.White else Muted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))
                filteredHistory.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkCard)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurface2),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(item.emoji, fontSize = 24.sp)
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        ) {
                            Text(item.giftName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                color = if (item.isSent) Pink else Color(0xFFC4B5FD),
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
        }

        // Floating Compose Button
        FloatingActionButton(
            onClick = onOpenCompose,
            containerColor = Purple,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 18.dp)
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Compose Moment")
        }
    }

    if (showEditModal) {
        AlertDialog(
            containerColor = DarkCard,
            onDismissRequest = { showEditModal = false },
            title = { Text("Edit Profile", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display name", color = Muted) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = DarkBg,
                            unfocusedContainerColor = DarkBg
                        )
                    )
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio / Love tag", color = Muted) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = DarkBg,
                            unfocusedContainerColor = DarkBg
                        )
                    )
                    OutlinedTextField(
                        value = editOccupation,
                        onValueChange = { editOccupation = it },
                        label = { Text("Occupation", color = Muted) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = DarkBg,
                            unfocusedContainerColor = DarkBg
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = editInterestInput,
                            onValueChange = { editInterestInput = it },
                            placeholder = { Text("Add interest...", color = Muted, fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = DarkBg,
                                unfocusedContainerColor = DarkBg
                            )
                        )
                        Button(
                            onClick = {
                                val trimmed = editInterestInput.trim()
                                if (trimmed.isNotEmpty() && !editInterests.contains(trimmed)) {
                                    editInterests = editInterests + trimmed
                                    editInterestInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Purple)
                        ) {
                            Text("Add", color = Color.White)
                        }
                    }
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        editInterests.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkSurface2)
                                    .clickable { editInterests = editInterests.filterNot { it == tag } }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text("$tag ✕", color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveProfile(
                            editName.trim().ifEmpty { displayName },
                            editBio.trim(),
                            editOccupation.trim(),
                            editInterests
                        )
                        showEditModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Purple)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditModal = false }) {
                    Text("Cancel", color = Muted)
                }
            }
        )
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
        containerColor = DarkBg,
        topBar = {
            Surface(color = DarkCard, shadowElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Column(Modifier.weight(1f).padding(start = 6.dp)) {
                        Text("Family", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 20.sp)
                        Text("Connect with your community", color = Muted, fontSize = 11.sp)
                    }
                    Text(
                        "💎 $diamonds",
                        color = Color(0xFF38BDF8),
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
                                placeholder = { Text("Share an update with family...", fontSize = 13.sp, color = Muted) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = DarkCard,
                                    unfocusedContainerColor = DarkCard,
                                    focusedBorderColor = Purple,
                                    unfocusedBorderColor = DarkSurface2
                                )
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
                                .background(DarkCard)
                                .padding(12.dp)
                        ) {
                            Text(note, color = Color.White, fontSize = 13.sp)
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
                                .background(DarkCard)
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
                                Text(member.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${member.role} · ${member.points} pts", color = Muted, fontSize = 11.sp)
                            }
                            if (member.name != "You") {
                                TextButton(onClick = { onChatMember(member.name) }) {
                                    Text("Chat", color = Color(0xFFC4B5FD), fontWeight = FontWeight.Bold)
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
                            Text("Leave Family", color = Color(0xFFFF5C8A))
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
                            .background(DarkCard)
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
                                Text(family.name, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
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
                                    containerColor = if (isMine) DarkSurface2 else Purple
                                )
                            ) {
                                Text(
                                    if (isMine) "Joined" else "Join",
                                    color = if (isMine) Color(0xFFC4B5FD) else Color.White,
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
            containerColor = DarkCard,
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
        containerColor = DarkBg,
        topBar = {
            Surface(color = DarkCard, shadowElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Column(Modifier.weight(1f).padding(start = 6.dp)) {
                        Text("Add Friends", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 18.sp)
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
                        .background(DarkCard)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Add by name or @username", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
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
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = DarkBg,
                                unfocusedContainerColor = DarkBg,
                                focusedBorderColor = Purple,
                                unfocusedBorderColor = DarkSurface2
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
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                            Text(feedback!!, color = Color(0xFF34D399), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFC4B5FD)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Muted)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard,
                        focusedBorderColor = Purple,
                        unfocusedBorderColor = DarkSurface2
                    )
                )
            }

            if (requestPeople.isNotEmpty()) {
                item {
                    Text("Friend Requests (${requestPeople.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                items(requestPeople, key = { "req-${it.name}" }) { person ->
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, DarkSurface2),
                        color = DarkCard
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AvatarBadge(name = person.name, size = 40.dp, fontSize = 16.sp)
                            Column(Modifier.weight(1f)) {
                                Text(person.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                Text("People You May Know", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                        border = BorderStroke(1.dp, DarkSurface2),
                        color = DarkCard
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AvatarBadge(name = person.name, size = 40.dp, fontSize = 16.sp)
                            Column(Modifier.weight(1f)) {
                                Text(person.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Chat", tint = Color(0xFFC4B5FD))
                            }
                        }
                    }
                }
            }

            item {
                Text("My Friends (${friendPeople.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            items(friendPeople, key = { "friend-${it.name}" }) { person ->
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, DarkSurface2),
                    color = DarkCard
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AvatarBadge(name = person.name, size = 40.dp, fontSize = 16.sp)
                        Column(Modifier.weight(1f)) {
                            Text(person.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                            Text("Chat", color = Color(0xFFC4B5FD), fontSize = 12.sp, fontWeight = FontWeight.Bold)
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


