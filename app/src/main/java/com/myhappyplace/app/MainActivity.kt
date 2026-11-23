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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

@Composable
fun HappyPlaceApp() {
    var selected by remember { mutableStateOf(0) }
    var openedChat by remember { mutableStateOf<String?>(null) }
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Purple, secondary = Pink, background = Color(0xFFFFFBFF),
            surface = Color.White, onSurface = Ink
        )
    ) {
        if (openedChat != null) {
            ChatScreen(name = openedChat!!, onBack = { openedChat = null })
        } else {
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
                        0 -> HomeScreen(onChat = { openedChat = it })
                        1 -> MatchScreen()
                        2 -> PartyScreen()
                        3 -> ChatListScreen(onChat = { openedChat = it })
                        4 -> ProfileScreen()
                        5 -> HumanBattleScreen()
                        else -> KhmerGiftShopScreen()
                    }
                }
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
fun HomeScreen(onChat: (String) -> Unit) {
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
                    Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                        Text("Explore friends", color = Purple)
                    }
                }
            }
        }
        item { SectionTitle("People you may like", "See all") }
        items(listOf("Mina", "Dara", "Sophy", "Alex")) { name ->
            PersonRow(name, "Online · Say hello", onClick = { onChat(name) })
        }
        item { SectionTitle("Popular rooms", "View all") }
        item { RoomCard("Late Night Talk 🌙", "128 people · Friendship", Color(0xFF7555B8)) }
        item { RoomCard("Music & Chill 🎧", "64 people · Music", Color(0xFFE58AAE)) }
    }
}

@Composable
fun SectionTitle(title: String, action: String) {
    Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontWeight = FontWeight.Bold, color = Ink, fontSize = 17.sp)
        Text(action, color = Purple, fontSize = 12.sp)
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
fun RoomCard(title: String, detail: String, color: Color) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp)
        .clip(RoundedCornerShape(18.dp)).background(color).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Headphones, null, tint = Color.White, modifier = Modifier.size(32.dp))
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(detail, color = Color.White.copy(alpha = .85f), fontSize = 12.sp)
        }
        Text("JOIN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun MatchScreen() {
    Column(Modifier.fillMaxSize().background(Lavender), horizontalAlignment = Alignment.CenterHorizontally) {
        Header("Soul Match", "A new friend could be one tap away")
        Spacer(Modifier.weight(1f))
        Box(Modifier.size(190.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Pink, Purple))),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Favorite, null, tint = Color.White, modifier = Modifier.size(82.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text("Ready to meet someone?", color = Ink, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("Find a person to share a little moment with.", color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(24.dp))
        Button(onClick = {}, shape = RoundedCornerShape(24.dp), modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth()) {
            Icon(Icons.Default.AutoAwesome, null)
            Spacer(Modifier.width(8.dp))
            Text("Start matching")
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun PartyScreen() {
    Column(Modifier.fillMaxSize()) {
        Header("Voice Party 🎙️", "Join a room and make new friends")
        RoomCard("Late Night Talk 🌙", "128 people · 6 speakers", Color(0xFF7555B8))
        RoomCard("Music & Chill 🎧", "64 people · 4 speakers", Color(0xFFE58AAE))
        RoomCard("Khmer Friends 🇰🇭", "32 people · 3 speakers", Color(0xFF5C9DAD))
        Spacer(Modifier.height(20.dp))
        Button(onClick = {}, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Create a room")
        }
        Text("Voice rooms are demo screens. Live audio needs a voice service connection.",
            color = Muted, fontSize = 11.sp, modifier = Modifier.padding(20.dp))
    }
}

@Composable
fun ChatListScreen(onChat: (String) -> Unit) {
    Column {
        Header("Messages", "Your conversations")
        listOf("Mina", "Dara", "Sophy").forEach { PersonRow(it, "Tap to open conversation", onClick = { onChat(it) }) }
    }
}

@Composable
fun ChatScreen(name: String, onBack: () -> Unit) {
    var message by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().background(Color(0xFFFFFBFF))) {
        Row(Modifier.fillMaxWidth().background(Color.White).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
            Text(name, fontWeight = FontWeight.Bold, color = Ink, fontSize = 18.sp)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.MoreVert, null, tint = Muted)
        }
        Column(Modifier.weight(1f).fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.Bottom) {
            Text("Say hello to $name 👋", color = Muted, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(12.dp))
            Box(Modifier.align(Alignment.End).clip(RoundedCornerShape(18.dp)).background(Lavender).padding(12.dp)) {
                Text("Hi! Welcome to MY HAPPY PLACE 💜", color = Ink)
            }
        }
        Row(Modifier.fillMaxWidth().background(Color.White).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = message, onValueChange = { message = it }, modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") }, shape = RoundedCornerShape(24.dp), singleLine = true)
            IconButton(onClick = { message = "" }) { Icon(Icons.Default.Send, null, tint = Purple) }
        }
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


private data class KhmerGift(val name: String, val price: Int, val emoji: String, val note: String)

@Composable
fun KhmerGiftShopScreen() {
    Image(
        painter = painterResource(id = R.drawable.khmer_food_gifts),
        contentDescription = "Khmer traditional food gifts",
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .height(190.dp).clip(RoundedCornerShape(18.dp)),
        contentScale = androidx.compose.ui.layout.ContentScale.Crop
    )

    val gifts = listOf(
        KhmerGift("នំអន្សមចេក", 29, "🫔", "នំអន្សមខ្ចប់ស្លឹកចេក"),
        KhmerGift("នំអន្សមជ្រូក", 49, "🍙", "រសជាតិប្រពៃណី"),
        KhmerGift("នំគ្រក់", 19, "🥥", "នំដូងខ្មែរ"),
        KhmerGift("នំអាកោ", 25, "🍮", "នំអាកោផ្អែម"),
        KhmerGift("នំផ្លែអាយ", 15, "🍡", "បង្អែមខ្មែរ"),
        KhmerGift("នំចាក់ចាន់", 35, "🍰", "នំស្រទាប់ចម្រុះ"),
        KhmerGift("នំគម", 22, "🥮", "នំអង្ករដំណើប"),
        KhmerGift("នំល្ពៅ", 39, "🎃", "នំល្ពៅចំហុយ")
    )
    var selectedGift by remember { mutableStateOf(gifts.first()) }
    var recipient by remember { mutableStateOf("Mina") }
    var diamonds by remember { mutableIntStateOf(5000) }
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
                    diamonds -= selectedGift.price
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
fun ProfileScreen() {
    var profilePhoto by remember { mutableStateOf<android.net.Uri?>(null) }
    var coverPhoto by remember { mutableStateOf<android.net.Uri?>(null) }
    var displayName by remember { mutableStateOf("My Happy Place Member") }
    var bio by remember { mutableStateOf("Here to make happy memories ✨") }

    val pickProfile = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        profilePhoto = it
    }
    val pickCover = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        coverPhoto = it
    }

    Column(Modifier.fillMaxSize()) {
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
        Text("រូបភាពបង្ហាញក្នុង App បច្ចុប្បន្ន។ ការរក្សាទុក Online ត្រូវភ្ជាប់ Firebase Storage បន្ថែម។",
            color = Muted, fontSize = 11.sp, modifier = Modifier.padding(20.dp))
    }
}
