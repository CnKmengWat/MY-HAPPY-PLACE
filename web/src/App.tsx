import { useEffect, useRef, useState } from 'react';
import {
  ArrowLeft,
  ArrowUpDown,
  Bell,
  Camera,
  Coffee,
  Disc3,
  Flag,
  Gift,
  Headphones,
  Heart,
  Home,
  ImagePlus,
  ListMusic,
  MessageCircle,
  Mic,
  MicOff,
  Moon,
  MoreHorizontal,
  MoreVertical,
  Music,
  Palette,
  Pause,
  Play,
  Plus,
  Save,
  Search,
  Send,
  SkipBack,
  SkipForward,
  Smile,
  Sparkles,
  Timer,
  Trophy,
  Upload,
  User,
  UserCheck,
  UserMinus,
  UserPlus,
  Users,
  Volume2,
  VolumeX,
  Zap,
  Check,
  X,
} from 'lucide-react';

type TabId = 'home' | 'match' | 'party' | 'chat' | 'me' | 'game' | 'gifts';

interface ChatMessage {
  id: number;
  text: string;
  isFromMe: boolean;
  timestamp: number;
}

type RoomMoodId = 'happy' | 'chill' | 'energetic' | 'cozy' | 'music';

interface RoomMood {
  id: RoomMoodId;
  label: string;
  description: string;
  color: string;
  gradientFrom: string;
  gradientTo: string;
  icon: typeof Smile;
}

type PartyBackgroundId = 'lounge' | 'mood' | 'neon' | 'sunset' | 'aurora' | 'starlight' | 'custom';

interface PartyBackgroundPreset {
  id: Exclude<PartyBackgroundId, 'custom'>;
  label: string;
  subtitle: string;
  gradient: string;
  imageSrc?: string;
}

interface VoiceRoom {
  id: string;
  title: string;
  detail: string;
  mood: RoomMoodId;
  color: string;
  listeners: number;
  speakers: string[];
  owner?: string;
  isActive?: boolean;
  backgroundId?: PartyBackgroundId;
  customBackground?: string | null;
  createdAt?: number;
}

interface FamilyMember {
  name: string;
  role: 'Leader' | 'Co-Leader' | 'Member';
  points: number;
}

type FamilyLogoShape = 'circle' | 'badge' | 'shield';

interface FamilyClub {
  id: string;
  name: string;
  motto: string;
  emblem: string;
  color: string;
  accentColor?: string;
  logoShape?: FamilyLogoShape;
  logoImage?: string | null;
  level: number;
  points: number;
  members: FamilyMember[];
  announcements: string[];
}

interface KhmerGift {
  id: string;
  name: string;
  price: number;
  emoji: string;
  note: string;
}

interface GiftRecord {
  id: number;
  giftName: string;
  emoji: string;
  price: number;
  isSent: boolean;
  peerName: string;
  timestamp: number;
}

interface BattleParticipant {
  id: string;
  name: string;
  supporters: number;
  shirtColor: string;
  hairColor: string;
  skinColor: string;
  bgStart: string;
  bgEnd: string;
}

const TABS: { id: TabId; label: string; icon: typeof Home }[] = [
  { id: 'home', label: 'Home', icon: Home },
  { id: 'match', label: 'Match', icon: Heart },
  { id: 'party', label: 'Party', icon: Headphones },
  { id: 'chat', label: 'Chat', icon: MessageCircle },
  { id: 'me', label: 'Me', icon: User },
  { id: 'game', label: 'Game', icon: Trophy },
  { id: 'gifts', label: 'Gifts', icon: Gift },
];

interface FriendPerson {
  name: string;
  handle: string;
  vibe: string;
  isOnline: boolean;
  mutualCount: number;
}

const INITIAL_PEOPLE_DIRECTORY: FriendPerson[] = [
  { name: 'Mina', handle: '@mina.kh', vibe: 'Night owl & acoustic music', isOnline: true, mutualCount: 4 },
  { name: 'Dara', handle: '@dara_vibe', vibe: 'Voice party host & gamer', isOnline: true, mutualCount: 3 },
  { name: 'Sophy', handle: '@sophy.music', vibe: 'Khmer desserts & chill beats', isOnline: true, mutualCount: 5 },
  { name: 'Alex', handle: '@alex_talks', vibe: 'Late-night storyteller', isOnline: true, mutualCount: 2 },
  { name: 'Nita', handle: '@nita.night', vibe: 'Cozy tea & coffee talks', isOnline: true, mutualCount: 2 },
  { name: 'Vannak', handle: '@vannak.star', vibe: 'Guitar jams & trivia', isOnline: true, mutualCount: 3 },
  { name: 'Maly', handle: '@maly.lotus', vibe: 'Happy Stars co-captain', isOnline: false, mutualCount: 1 },
  { name: 'Sokha', handle: '@sokha.party', vibe: 'Khmer Friends room host', isOnline: true, mutualCount: 4 },
];

const SUGGESTED_PEOPLE = ['Mina', 'Dara', 'Sophy', 'Alex'];

const ROOM_MOODS: RoomMood[] = [
  { id: 'happy', label: 'Happy', description: 'Upbeat & cheerful hangout', color: '#E87A5D', gradientFrom: '#E87A5D', gradientTo: '#D45D79', icon: Smile },
  { id: 'chill', label: 'Chill', description: 'Laid-back chats & easy vibes', color: '#E58AAE', gradientFrom: '#E58AAE', gradientTo: '#B56CC4', icon: Coffee },
  { id: 'energetic', label: 'Energetic', description: 'Lively talks & high energy', color: '#7555B8', gradientFrom: '#7555B8', gradientTo: '#9E5CE6', icon: Zap },
  { id: 'cozy', label: 'Cozy', description: 'Warm, quiet late-night talks', color: '#5C9DAD', gradientFrom: '#5C9DAD', gradientTo: '#4973A6', icon: Moon },
  { id: 'music', label: 'Music', description: 'Live tunes & jam sessions', color: '#5B6EE1', gradientFrom: '#5B6EE1', gradientTo: '#8B5CF6', icon: Music },
];

function getRoomMood(moodId: RoomMoodId): RoomMood {
  return ROOM_MOODS.find((m) => m.id === moodId) ?? ROOM_MOODS[0];
}

const PARTY_BACKGROUNDS: PartyBackgroundPreset[] = [
  {
    id: 'lounge',
    label: 'Lantern Lounge',
    subtitle: 'Rooftop fairy lights & skyline',
    gradient: 'linear-gradient(160deg, #1E1538 0%, #362257 52%, #1A122E 100%)',
    imageSrc: '/images/party_lounge_bg.jpg',
  },
  {
    id: 'mood',
    label: 'Mood Aura',
    subtitle: 'Synced to room mood glow',
    gradient: 'linear-gradient(155deg, #21163E 0%, #3F2768 55%, #161028 100%)',
  },
  {
    id: 'neon',
    label: 'Midnight Neon',
    subtitle: 'Violet & magenta stage wash',
    gradient: 'linear-gradient(155deg, #120B29 0%, #2F195F 50%, #5E2368 100%)',
  },
  {
    id: 'sunset',
    label: 'Sunset Dusk',
    subtitle: 'Warm rose & plum twilight',
    gradient: 'linear-gradient(155deg, #241122 0%, #5A2241 52%, #8C3B4A 100%)',
  },
  {
    id: 'aurora',
    label: 'Emerald Aurora',
    subtitle: 'Calm teal & deep indigo',
    gradient: 'linear-gradient(155deg, #0B1F24 0%, #17434B 52%, #2A3566 100%)',
  },
  {
    id: 'starlight',
    label: 'Starlight Sky',
    subtitle: 'Deep midnight blue & indigo',
    gradient: 'linear-gradient(155deg, #0C1329 0%, #1B2852 52%, #372A6E 100%)',
  },
];

function getMoodRoomGradient(moodId: RoomMoodId): string {
  switch (moodId) {
    case 'happy':
      return 'linear-gradient(155deg, #29152B 0%, #6A2C46 52%, #1E1430 100%)';
    case 'chill':
      return 'linear-gradient(155deg, #231331 0%, #562A63 52%, #19132E 100%)';
    case 'energetic':
      return 'linear-gradient(155deg, #181038 0%, #43247D 52%, #191232 100%)';
    case 'cozy':
      return 'linear-gradient(155deg, #0F202C 0%, #23495E 52%, #14172E 100%)';
    case 'music':
      return 'linear-gradient(155deg, #14193B 0%, #2F3E86 52%, #191436 100%)';
  }
}

const INITIAL_ROOMS_ANCHOR = Date.now();
const INITIAL_ROOMS: VoiceRoom[] = [
  { id: 'late-night', title: 'Late Night Talk 🌙', detail: '128 people · 6 speakers', mood: 'cozy', color: '#5C9DAD', listeners: 128, speakers: ['Mina', 'Dara', 'Sokha', 'Sophy', 'Alex', 'Nita'], owner: 'Mina', isActive: true, backgroundId: 'lounge', createdAt: INITIAL_ROOMS_ANCHOR - 3 * 3600_000 },
  { id: 'music-chill', title: 'Music & Chill 🎧', detail: '64 people · 4 speakers', mood: 'chill', color: '#E58AAE', listeners: 64, speakers: ['Sophy', 'Maly', 'Dara', 'Vannak'], owner: 'Sophy', isActive: true, backgroundId: 'neon', createdAt: INITIAL_ROOMS_ANCHOR - 1 * 3600_000 },
  { id: 'khmer-friends', title: 'Khmer Friends 🇰🇭', detail: '32 people · 3 speakers', mood: 'happy', color: '#E87A5D', listeners: 32, speakers: ['Sokha', 'Mina', 'Vannak'], owner: 'Sokha', isActive: true, backgroundId: 'sunset', createdAt: INITIAL_ROOMS_ANCHOR - 15 * 60_000 },
];

const INITIAL_FAMILIES: FamilyClub[] = [
  {
    id: 'angkor-lotus',
    name: 'Angkor Lotus',
    motto: 'Warm Khmer vibes, nightly music & good friends',
    emblem: '🌸',
    color: '#8B5CF6',
    level: 8,
    points: 2480,
    members: [
      { name: 'Mina', role: 'Leader', points: 860 },
      { name: 'Dara', role: 'Co-Leader', points: 620 },
      { name: 'Sophy', role: 'Member', points: 540 },
      { name: 'You', role: 'Member', points: 460 },
    ],
    announcements: [
      'Mina: Nightly Khmer acoustic room starts at 8 PM! 🎶',
      'Dara: Welcome everyone to Angkor Lotus 🌸',
    ],
  },
  {
    id: 'moonlight-crew',
    name: 'Moonlight Crew',
    motto: 'Late-night storytellers and cozy voice chats',
    emblem: '🌙',
    color: '#5C9DAD',
    level: 6,
    points: 1720,
    members: [
      { name: 'Alex', role: 'Leader', points: 710 },
      { name: 'Nita', role: 'Co-Leader', points: 530 },
      { name: 'Sokha', role: 'Member', points: 480 },
    ],
    announcements: ['Alex: Cozy late-night Q&A tonight!'],
  },
  {
    id: 'happy-stars',
    name: 'Happy Stars',
    motto: 'Daily games, Khmer snacks & cheerful energy',
    emblem: '✨',
    color: '#E87A5D',
    level: 5,
    points: 1350,
    members: [
      { name: 'Maly', role: 'Leader', points: 590 },
      { name: 'Vannak', role: 'Member', points: 410 },
      { name: 'Sophy', role: 'Member', points: 350 },
    ],
    announcements: ["Maly: Let's team up for Human Battle tonight!"],
  },
];

const BATTLE_PARTICIPANTS: BattleParticipant[] = [
  { id: 'sokha', name: 'Sokha', supporters: 13, shirtColor: '#8B5CF6', hairColor: '#1E1B2E', skinColor: '#F1C6A6', bgStart: '#3B4D8C', bgEnd: '#232F5C' },
  { id: 'dara', name: 'Dara', supporters: 63, shirtColor: '#38BDF8', hairColor: '#29223B', skinColor: '#E8B896', bgStart: '#2A4E7A', bgEnd: '#1B3052' },
  { id: 'maly', name: 'Maly', supporters: 19, shirtColor: '#FF7EB6', hairColor: '#2B1F2D', skinColor: '#F5CEB2', bgStart: '#5E3872', bgEnd: '#2F224F' },
  { id: 'vannak', name: 'Vannak', supporters: 10, shirtColor: '#F59E0B', hairColor: '#181826', skinColor: '#DFA882', bgStart: '#34497A', bgEnd: '#1E2C52' },
  { id: 'sophy', name: 'Sophy', supporters: 60, shirtColor: '#EC4899', hairColor: '#33202A', skinColor: '#F2C5A5', bgStart: '#54356E', bgEnd: '#2B1E47' },
  { id: 'alex', name: 'Alex', supporters: 7, shirtColor: '#6366F1', hairColor: '#4A3123', skinColor: '#F5D2B8', bgStart: '#2F467A', bgEnd: '#1C2A4D' },
  { id: 'nita', name: 'Nita', supporters: 4, shirtColor: '#A855F7', hairColor: '#201A2B', skinColor: '#ECC1A1', bgStart: '#493775', bgEnd: '#241F4A' },
  { id: 'mina', name: 'Mina', supporters: 3, shirtColor: '#FB7185', hairColor: '#271C24', skinColor: '#F7D5BC', bgStart: '#5C3A6B', bgEnd: '#2B2145' },
];

const KHMER_GIFTS: KhmerGift[] = [
  { id: 'ansom-chek', name: 'នំអន្សមចេក', price: 29, emoji: '🫔', note: 'នំអន្សមខ្ចប់ស្លឹកចេក' },
  { id: 'ansom-chrouk', name: 'នំអន្សមជ្រូក', price: 49, emoji: '🍙', note: 'រសជាតិប្រពៃណី' },
  { id: 'krok', name: 'នំគ្រក់', price: 19, emoji: '🥥', note: 'នំដូងខ្មែរ' },
  { id: 'akor', name: 'នំអាកោ', price: 25, emoji: '🍮', note: 'នំអាកោផ្អែម' },
  { id: 'plae-ai', name: 'នំផ្លែអាយ', price: 15, emoji: '🍡', note: 'បង្អែមខ្មែរ' },
  { id: 'chak-chan', name: 'នំចាក់ចាន់', price: 35, emoji: '🍰', note: 'នំស្រទាប់ចម្រុះ' },
  { id: 'kom', name: 'នំគម', price: 22, emoji: '🥮', note: 'នំអង្ករដំណើប' },
  { id: 'lpov', name: 'នំល្ពៅ', price: 39, emoji: '🎃', note: 'នំល្ពៅចំហុយ' },
];

interface PartyTrack {
  id: string;
  title: string;
  artist: string;
  vibe: string;
  mood: RoomMoodId;
  durationSec: number;
  bpm: number;
  color: string;
  notes: number[];
  bassFreq: number;
  addedBy?: string;
  audioSrc?: string;
  fileName?: string;
}

const PARTY_TRACKS: PartyTrack[] = [
  {
    id: 'angkor-sunset',
    title: 'Angkor Acoustic Sunset',
    artist: 'Mina & Friends',
    vibe: 'Chill Acoustic',
    mood: 'chill',
    durationSec: 185,
    bpm: 84,
    color: '#E58AAE',
    notes: [261.63, 293.66, 329.63, 392.0, 440.0, 392.0, 329.63, 293.66],
    bassFreq: 130.81,
  },
  {
    id: 'phnom-penh-lofi',
    title: 'Phnom Penh Lo-Fi Rain',
    artist: 'DJ Dara',
    vibe: 'Cozy Lo-Fi',
    mood: 'cozy',
    durationSec: 210,
    bpm: 76,
    color: '#5C9DAD',
    notes: [220.0, 261.63, 329.63, 392.0, 329.63, 293.66, 261.63, 246.94],
    bassFreq: 110.0,
  },
  {
    id: 'mekong-starlight',
    title: 'Mekong Starlight',
    artist: 'Sophy',
    vibe: 'Happy Pop',
    mood: 'happy',
    durationSec: 168,
    bpm: 108,
    color: '#E87A5D',
    notes: [293.66, 349.23, 392.0, 440.0, 523.25, 440.0, 392.0, 349.23],
    bassFreq: 146.83,
  },
  {
    id: 'khmer-groove',
    title: 'Khmer New Year Groove',
    artist: 'Vannak',
    vibe: 'Energetic Dance',
    mood: 'energetic',
    durationSec: 196,
    bpm: 122,
    color: '#8B5CF6',
    notes: [329.63, 392.0, 440.0, 493.88, 587.33, 493.88, 440.0, 392.0],
    bassFreq: 164.81,
  },
  {
    id: 'midnight-jazz',
    title: 'Late Night Jazz Café',
    artist: 'Alex',
    vibe: 'Smooth Lounge',
    mood: 'music',
    durationSec: 224,
    bpm: 92,
    color: '#5B6EE1',
    notes: [246.94, 293.66, 369.99, 440.0, 392.0, 329.63, 293.66, 246.94],
    bassFreq: 123.47,
  },
];

function getInitialTrackIndexForMood(mood: RoomMoodId): number {
  const idx = PARTY_TRACKS.findIndex((t) => t.mood === mood);
  return idx >= 0 ? idx : 0;
}

function createPartyTrack({
  title,
  artist,
  mood,
  audioSrc,
  fileName,
  durationSec = 180,
}: {
  title: string;
  artist?: string;
  mood: RoomMoodId;
  audioSrc?: string;
  fileName?: string;
  durationSec?: number;
}): PartyTrack {
  const moodMeta = getRoomMood(mood);
  const template = PARTY_TRACKS[getInitialTrackIndexForMood(mood)];
  return {
    id: `custom-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`,
    title: title.trim(),
    artist: artist?.trim() || 'Added by You',
    vibe: moodMeta.label,
    mood,
    durationSec,
    bpm: template.bpm,
    color: moodMeta.color,
    notes: template.notes,
    bassFreq: template.bassFreq,
    addedBy: 'You',
    audioSrc,
    fileName,
  };
}

function stripAudioExtension(fileName: string): string {
  return fileName.replace(/\.[^/.]+$/, '').replace(/[_-]+/g, ' ').trim();
}

function formatTrackTime(seconds: number): string {
  const safe = Math.max(0, Math.floor(seconds));
  const mins = Math.floor(safe / 60);
  const secs = safe % 60;
  return `${mins}:${secs.toString().padStart(2, '0')}`;
}

function formatTimestamp(timestamp: number): string {
  return new Date(timestamp).toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' });
}

function AvatarBadge({ name, size = 'md' }: { name: string; size?: 'xs' | 'sm' | 'md' | 'lg' }) {
  const cls =
    size === 'xs'
      ? 'w-5 h-5 text-[10px] ring-1 ring-white/60 shadow-xs'
      : size === 'sm'
        ? 'w-10 h-10 text-base'
        : size === 'lg'
          ? 'w-14 h-14 text-2xl'
          : 'w-[50px] h-[50px] text-xl';
  return (
    <div
      className={`${cls} rounded-full flex items-center justify-center text-white font-bold shrink-0`}
      style={{ background: 'linear-gradient(135deg, #FF7EB6 0%, #8B5CF6 100%)' }}
    >
      {name.slice(0, 1)}
    </div>
  );
}

export default function App() {
  const [selectedTab, setSelectedTab] = useState<TabId>('home');
  const [openedChat, setOpenedChat] = useState<string | null>(null);
  const [activeVoiceRoom, setActiveVoiceRoom] = useState<VoiceRoom | null>(null);
  const [showFamily, setShowFamily] = useState(false);
  const [showFriends, setShowFriends] = useState(false);
  const [peopleDirectory, setPeopleDirectory] = useState<FriendPerson[]>(INITIAL_PEOPLE_DIRECTORY);
  const [friends, setFriends] = useState<string[]>(['Mina', 'Dara', 'Sophy']);
  const [friendRequests, setFriendRequests] = useState<string[]>(['Nita', 'Vannak']);
  const [families, setFamilies] = useState<FamilyClub[]>(INITIAL_FAMILIES);
  const [myFamilyId, setMyFamilyId] = useState<string | null>('angkor-lotus');
  const [checkedInToday, setCheckedInToday] = useState(false);
  const [conversations, setConversations] = useState<Record<string, ChatMessage[]>>(() => {
    const now = Date.now();
    return {
      Mina: [
        { id: 1, text: 'Hi! Welcome to MY HAPPY PLACE 💜', isFromMe: false, timestamp: now - 180_000 },
        { id: 2, text: 'How is your day going?', isFromMe: false, timestamp: now - 120_000 },
      ],
      Dara: [{ id: 3, text: 'Hey! Ready for Voice Party later? 🎙️', isFromMe: false, timestamp: now - 1_500_000 }],
      Sophy: [{ id: 4, text: 'Just sent you a Khmer dessert gift 🫔', isFromMe: false, timestamp: now - 86_400_000 }],
      Alex: [{ id: 5, text: 'Nice to meet you here ✨', isFromMe: false, timestamp: now - 86_400_000 }],
    };
  });

  const [rooms, setRooms] = useState<VoiceRoom[]>(INITIAL_ROOMS);
  const [musicLibrary, setMusicLibrary] = useState<PartyTrack[]>(PARTY_TRACKS);
  const [showAddMusicModal, setShowAddMusicModal] = useState(false);
  const [reportingRoom, setReportingRoom] = useState<VoiceRoom | null>(null);
  const [reportedRoomIds, setReportedRoomIds] = useState<string[]>([]);
  const [reportToast, setReportToast] = useState<string | null>(null);
  const [diamonds, setDiamonds] = useState(5000);

  const handleAddTrackToLibrary = (track: PartyTrack) => {
    setMusicLibrary((prev) => [...prev, track]);
  };

  const handleAddFriend = (rawInput: string): string | null => {
    const cleaned = rawInput.trim().replace(/^@+/, '');
    if (!cleaned) return null;
    const lowered = cleaned.toLowerCase();
    const existing = peopleDirectory.find(
      (p) => p.name.toLowerCase() === lowered || p.handle.replace(/^@/, '').toLowerCase() === lowered,
    );
    const resolvedName =
      existing?.name ?? cleaned.charAt(0).toUpperCase() + cleaned.slice(1);
    if (!existing) {
      const slug = cleaned.toLowerCase().replace(/[^a-z0-9]+/g, '.') || 'friend';
      setPeopleDirectory((prev) => [
        {
          name: resolvedName,
          handle: `@${slug}`,
          vibe: 'Added via friend search',
          isOnline: true,
          mutualCount: 0,
        },
        ...prev,
      ]);
    }
    setFriends((prev) => (prev.includes(resolvedName) ? prev : [resolvedName, ...prev]));
    setFriendRequests((prev) => prev.filter((r) => r !== resolvedName));
    setReportToast(`Added ${resolvedName} as a friend!`);
    return resolvedName;
  };

  const handleAcceptFriendRequest = (name: string) => {
    setFriendRequests((prev) => prev.filter((r) => r !== name));
    setFriends((prev) => (prev.includes(name) ? prev : [name, ...prev]));
    setReportToast(`You and ${name} are now friends!`);
  };

  const handleDeclineFriendRequest = (name: string) => {
    setFriendRequests((prev) => prev.filter((r) => r !== name));
  };

  const handleRemoveFriend = (name: string) => {
    setFriends((prev) => prev.filter((f) => f !== name));
    setReportToast(`Removed ${name} from your friends list.`);
  };

  useEffect(() => {
    const timer = window.setInterval(() => {
      setRooms((prev) =>
        prev.map((room) => {
          const active = room.isActive ?? room.listeners > 0;
          if (!active) return room;
          const delta = Math.floor(Math.random() * 3) - 1; // -1, 0, or +1
          const nextListeners = Math.max(1, room.listeners + delta);
          if (nextListeners === room.listeners) return room;
          const peopleLabel = nextListeners === 1 ? 'person' : 'people';
          const speakerLabel = room.speakers.length === 1 ? 'speaker' : 'speakers';
          return {
            ...room,
            listeners: nextListeners,
            detail: `${nextListeners} ${peopleLabel} · ${room.speakers.length} ${speakerLabel}`,
          };
        }),
      );
    }, 3200);
    return () => window.clearInterval(timer);
  }, []);
  const [giftHistory, setGiftHistory] = useState<GiftRecord[]>(() => {
    const now = Date.now();
    return [
      { id: 1, giftName: 'នំអន្សមជ្រូក', emoji: '🍙', price: 49, isSent: false, peerName: 'Sophy', timestamp: now - 3_600_000 },
      { id: 2, giftName: 'នំអន្សមចេក', emoji: '🫔', price: 29, isSent: true, peerName: 'Mina', timestamp: now - 7_200_000 },
      { id: 3, giftName: 'នំគ្រក់', emoji: '🥥', price: 19, isSent: false, peerName: 'Dara', timestamp: now - 86_400_000 },
      { id: 4, giftName: 'នំចាក់ចាន់', emoji: '🍰', price: 35, isSent: true, peerName: 'Sophy', timestamp: now - 172_800_000 },
    ];
  });
  const [displayName, setDisplayName] = useState('My Happy Place Member');
  const [bio, setBio] = useState('Here to make happy memories ✨');
  const [profilePhoto, setProfilePhoto] = useState<string | null>(null);
  const [coverPhoto, setCoverPhoto] = useState<string | null>(null);

  const sendMessage = (friendName: string, rawText: string) => {
    const trimmed = rawText.trim();
    if (!trimmed) return;
    setConversations((prev) => {
      const current = prev[friendName] ?? [];
      const nextId = (current.reduce((max, m) => Math.max(max, m.id), 0) || 0) + 1;
      return {
        ...prev,
        [friendName]: [...current, { id: nextId, text: trimmed, isFromMe: true, timestamp: Date.now() }],
      };
    });

    window.setTimeout(() => {
      const replies = ['Love that! 😊', 'Haha totally agree 💜', 'Tell me more!', 'Sounds awesome ✨'];
      setConversations((prev) => {
        const current = prev[friendName] ?? [];
        const replyId = (current.reduce((max, m) => Math.max(max, m.id), 0) || 0) + 1;
        return {
          ...prev,
          [friendName]: [
            ...current,
            {
              id: replyId,
              text: replies[replyId % replies.length],
              isFromMe: false,
              timestamp: Date.now(),
            },
          ],
        };
      });
    }, 700);
  };

  if (openedChat) {
    return (
      <ChatScreen
        name={openedChat}
        messages={conversations[openedChat] ?? []}
        onSendMessage={(text) => sendMessage(openedChat, text)}
        onClearMessages={() => setConversations((prev) => ({ ...prev, [openedChat]: [] }))}
        onBack={() => setOpenedChat(null)}
      />
    );
  }

  if (activeVoiceRoom) {
    return (
      <VoicePartyRoomView
        room={activeVoiceRoom}
        diamonds={diamonds}
        musicLibrary={musicLibrary}
        onAddTrackToLibrary={handleAddTrackToLibrary}
        onSendGift={(gift, recipientName) => {
          setDiamonds((d) => d - gift.price);
          setGiftHistory((prev) => [
            {
              id: Date.now(),
              giftName: gift.name,
              emoji: gift.emoji,
              price: gift.price,
              isSent: true,
              peerName: recipientName,
              timestamp: Date.now(),
            },
            ...prev,
          ]);
        }}
        onUpdateRoomBackground={(roomId, backgroundId, customBackground) => {
          setRooms((prev) =>
            prev.map((r) =>
              r.id === roomId ? { ...r, backgroundId, customBackground: customBackground ?? null } : r,
            ),
          );
          setActiveVoiceRoom((prev) =>
            prev && prev.id === roomId
              ? { ...prev, backgroundId, customBackground: customBackground ?? null }
              : prev,
          );
        }}
        onLeave={() => setActiveVoiceRoom(null)}
      />
    );
  }

  const currentFamily = families.find((f) => f.id === myFamilyId) ?? null;

  if (showFriends) {
    return (
      <FriendsView
        peopleDirectory={peopleDirectory}
        friends={friends}
        friendRequests={friendRequests}
        onBack={() => setShowFriends(false)}
        onAddFriend={handleAddFriend}
        onAcceptRequest={handleAcceptFriendRequest}
        onDeclineRequest={handleDeclineFriendRequest}
        onRemoveFriend={handleRemoveFriend}
        onChat={(friendName) => {
          setShowFriends(false);
          setOpenedChat(friendName);
        }}
      />
    );
  }

  if (showFamily) {
    return (
      <FamilyView
        families={families}
        myFamilyId={myFamilyId}
        diamonds={diamonds}
        checkedInToday={checkedInToday}
        onBack={() => setShowFamily(false)}
        onJoinFamily={(familyId) => {
          setFamilies((prev) =>
            prev.map((f) => {
              if (f.id === familyId) {
                const hasMe = f.members.some((m) => m.name === 'You');
                return hasMe
                  ? f
                  : { ...f, members: [...f.members, { name: 'You', role: 'Member', points: 100 }] };
              }
              if (f.id === myFamilyId) {
                return { ...f, members: f.members.filter((m) => m.name !== 'You') };
              }
              return f;
            }),
          );
          setMyFamilyId(familyId);
        }}
        onLeaveFamily={(familyId) => {
          setFamilies((prev) =>
            prev.map((f) =>
              f.id === familyId ? { ...f, members: f.members.filter((m) => m.name !== 'You') } : f,
            ),
          );
          if (myFamilyId === familyId) setMyFamilyId(null);
        }}
        onCreateFamily={(name, motto, emblem, color, accentColor, logoShape, logoImage) => {
          const created: FamilyClub = {
            id: `family-${Date.now()}`,
            name,
            motto: motto || 'Sharing happy moments together',
            emblem,
            color,
            accentColor,
            logoShape,
            logoImage,
            level: 1,
            points: 100,
            members: [{ name: 'You', role: 'Leader', points: 100 }],
            announcements: [`You: Welcome to ${name} ${emblem}`],
          };
          setFamilies((prev) => [
            created,
            ...prev.map((f) =>
              f.id === myFamilyId ? { ...f, members: f.members.filter((m) => m.name !== 'You') } : f,
            ),
          ]);
          setMyFamilyId(created.id);
        }}
        onUpdateFamilyLogo={(familyId, emblem, color, accentColor, logoShape, logoImage) => {
          setFamilies((prev) =>
            prev.map((f) =>
              f.id === familyId ? { ...f, emblem, color, accentColor, logoShape, logoImage } : f,
            ),
          );
        }}
        onCheckIn={(familyId) => {
          if (checkedInToday) return;
          setCheckedInToday(true);
          setDiamonds((d) => d + 25);
          setFamilies((prev) =>
            prev.map((f) => (f.id === familyId ? { ...f, points: f.points + 50 } : f)),
          );
        }}
        onPostAnnouncement={(familyId, text) => {
          const trimmed = text.trim();
          if (!trimmed) return;
          setFamilies((prev) =>
            prev.map((f) =>
              f.id === familyId ? { ...f, announcements: [`You: ${trimmed}`, ...f.announcements] } : f,
            ),
          );
        }}
        onStartFamilyParty={(family) => {
          const speakers = Array.from(new Set(family.members.map((m) => m.name))).slice(0, 6);
          const partyRoom: VoiceRoom = {
            id: `family-room-${family.id}`,
            title: `${family.name} Hangout ${family.emblem}`,
            detail: `${family.members.length} members · ${speakers.length} speakers`,
            mood: 'happy',
            color: family.color,
            listeners: family.members.length + 4,
            speakers,
            owner: family.members[0]?.name ?? 'You',
            createdAt: Date.now(),
          };
          setRooms((prev) => (prev.some((r) => r.id === partyRoom.id) ? prev : [partyRoom, ...prev]));
          setActiveVoiceRoom(partyRoom);
        }}
        onChatMember={(memberName) => {
          if (memberName !== 'You') setOpenedChat(memberName);
        }}
      />
    );
  }

  const isDarkTab = selectedTab === 'game' || selectedTab === 'gifts';
  const bgClass =
    selectedTab === 'game'
      ? 'bg-[#101A3B]'
      : selectedTab === 'gifts'
        ? 'bg-[#171421]'
        : selectedTab === 'match'
          ? 'bg-[#F5F0FF]'
          : 'bg-[#FFFBFF]';

  return (
    <div className={`min-h-screen flex flex-col md:flex-row ${bgClass}`}>
      <aside
        className={`hidden md:flex md:flex-col md:w-56 lg:w-64 shrink-0 border-r p-4 sticky top-0 h-screen ${
          isDarkTab ? 'bg-[#131120] border-white/10 text-white' : 'bg-white border-[#F5F0FF] text-[#251B3A]'
        }`}
      >
        <div className="px-3 py-3 mb-4">
          <div className="text-[#8B5CF6] text-[11px] font-bold tracking-[2px]">MY HAPPY PLACE</div>
          <div className="text-xs opacity-65 mt-0.5 truncate">{displayName}</div>
        </div>
        <nav className="flex-1 space-y-1">
          {TABS.map((tab) => {
            const Icon = tab.icon;
            const active = selectedTab === tab.id;
            return (
              <button
                key={tab.id}
                type="button"
                onClick={() => setSelectedTab(tab.id)}
                className={`w-full min-h-[44px] px-3.5 py-2.5 rounded-xl flex items-center gap-3 text-sm font-semibold transition-colors cursor-pointer ${
                  active
                    ? isDarkTab
                      ? 'bg-[#8B5CF6] text-white'
                      : 'bg-[#F5F0FF] text-[#8B5CF6]'
                    : isDarkTab
                      ? 'text-white/75 hover:bg-white/10 hover:text-white'
                      : 'text-[#8B8298] hover:bg-[#F5F0FF]/50 hover:text-[#251B3A]'
                }`}
              >
                <Icon className="w-5 h-5 shrink-0" />
                <span>{tab.label}</span>
              </button>
            );
          })}
          <button
            type="button"
            onClick={() => setShowFamily(true)}
            className={`w-full min-h-[44px] px-3.5 py-2.5 rounded-xl flex items-center gap-3 text-sm font-semibold transition-colors cursor-pointer ${
              isDarkTab
                ? 'text-white/75 hover:bg-white/10 hover:text-white'
                : 'text-[#8B8298] hover:bg-[#F5F0FF]/50 hover:text-[#251B3A]'
            }`}
          >
            <Users className="w-5 h-5 shrink-0" />
            <span>Family</span>
          </button>
          <button
            type="button"
            onClick={() => setShowFriends(true)}
            className={`w-full min-h-[44px] px-3.5 py-2.5 rounded-xl flex items-center justify-between gap-3 text-sm font-semibold transition-colors cursor-pointer ${
              isDarkTab
                ? 'text-white/75 hover:bg-white/10 hover:text-white'
                : 'text-[#8B8298] hover:bg-[#F5F0FF]/50 hover:text-[#251B3A]'
            }`}
          >
            <span className="flex items-center gap-3">
              <UserPlus className="w-5 h-5 shrink-0" />
              <span>Friends</span>
            </span>
            {friendRequests.length > 0 && (
              <span className="text-xs font-bold text-[#8B5CF6] tabular-nums">{friendRequests.length}</span>
            )}
          </button>
        </nav>
        <div
          className={`mt-auto px-3 py-2.5 rounded-xl text-xs font-bold tabular-nums ${
            isDarkTab ? 'bg-white/5 text-[#70D9FF]' : 'bg-[#F5F0FF] text-[#8B5CF6]'
          }`}
        >
          💎 {diamonds} Diamonds
        </div>
      </aside>

      <main className="flex-1 min-w-0 pb-20 md:pb-0">
        <div className="max-w-5xl mx-auto min-h-full">
          {selectedTab === 'home' && (
            <HomeView
              rooms={rooms.slice(0, 2)}
              currentFamily={currentFamily}
              musicCount={musicLibrary.length}
              friends={friends}
              friendRequestCount={friendRequests.length}
              reportedRoomIds={reportedRoomIds}
              onChat={setOpenedChat}
              onTab={setSelectedTab}
              onJoinRoom={setActiveVoiceRoom}
              onReportRoom={setReportingRoom}
              onOpenFamily={() => setShowFamily(true)}
              onOpenFriends={() => setShowFriends(true)}
              onAddFriend={handleAddFriend}
              onRemoveFriend={handleRemoveFriend}
              onOpenAddMusic={() => setShowAddMusicModal(true)}
            />
          )}
          {selectedTab === 'match' && (
            <MatchView
              friends={friends}
              onAddFriend={handleAddFriend}
              onChat={setOpenedChat}
            />
          )}
          {selectedTab === 'party' && (
            <PartyView
              rooms={rooms}
              musicLibrary={musicLibrary}
              reportedRoomIds={reportedRoomIds}
              onJoinRoom={setActiveVoiceRoom}
              onReportRoom={setReportingRoom}
              onOpenAddMusic={() => setShowAddMusicModal(true)}
              onCreateRoom={(title, moodId, backgroundId) => {
                const moodMeta = getRoomMood(moodId);
                const created: VoiceRoom = {
                  id: `room-${Date.now()}`,
                  title,
                  detail: '1 person · 1 speaker',
                  mood: moodId,
                  color: moodMeta.color,
                  listeners: 1,
                  speakers: ['You'],
                  owner: 'You',
                  backgroundId,
                  createdAt: Date.now(),
                };
                setRooms((prev) => [created, ...prev]);
                setActiveVoiceRoom(created);
              }}
            />
          )}
          {selectedTab === 'chat' && (
            <div className="pb-8">
              <div className="px-5 py-4 flex items-center justify-between gap-3">
                <div>
                  <h1 className="text-[27px] font-extrabold text-[#251B3A]">Messages</h1>
                  <p className="text-[13px] text-[#8B8298]">
                    Your conversations · {friends.length} friends
                  </p>
                </div>
                <button
                  type="button"
                  onClick={() => setShowFriends(true)}
                  className="min-h-[44px] px-4 rounded-full bg-[#8B5CF6] hover:bg-[#7C4DFF] text-white font-bold text-xs flex items-center gap-1.5 cursor-pointer shrink-0 whitespace-nowrap transition-colors"
                >
                  <UserPlus className="w-4 h-4" />
                  <span>Add friends</span>
                </button>
              </div>
              <div className="max-w-2xl space-y-0.5">
                {friends.map((name) => {
                  const preview = conversations[name]?.at(-1)?.text ?? 'Tap to open conversation';
                  return (
                    <button
                      key={name}
                      type="button"
                      onClick={() => setOpenedChat(name)}
                      className="w-full flex items-center gap-3 px-5 py-2.5 text-left hover:bg-[#F5F0FF]/70 rounded-xl cursor-pointer min-h-[62px]"
                    >
                      <AvatarBadge name={name} />
                      <div className="flex-1 min-w-0">
                        <div className="text-[#251B3A] font-bold text-[15px] truncate">{name}</div>
                        <div className="text-[#8B8298] text-xs truncate">{preview}</div>
                      </div>
                      <MessageCircle className="w-5 h-5 text-[#8B5CF6] shrink-0" />
                    </button>
                  );
                })}
              </div>
            </div>
          )}
          {selectedTab === 'me' && (
            <ProfileView
              displayName={displayName}
              bio={bio}
              profilePhoto={profilePhoto}
              coverPhoto={coverPhoto}
              giftHistory={giftHistory}
              currentFamily={currentFamily}
              friendsCount={friends.length}
              friendRequestCount={friendRequests.length}
              onOpenFriends={() => setShowFriends(true)}
              onOpenFamily={() => setShowFamily(true)}
              onSave={(next) => {
                setDisplayName(next.displayName);
                setBio(next.bio);
                setProfilePhoto(next.profilePhoto);
                setCoverPhoto(next.coverPhoto);
              }}
            />
          )}
          {selectedTab === 'game' && <HumanBattleView />}
          {selectedTab === 'gifts' && (
            <KhmerGiftShopView
              diamonds={diamonds}
              onSpend={(gift, recipientName) => {
                setDiamonds((d) => d - gift.price);
                setGiftHistory((prev) => [
                  {
                    id: Date.now(),
                    giftName: gift.name,
                    emoji: gift.emoji,
                    price: gift.price,
                    isSent: true,
                    peerName: recipientName,
                    timestamp: Date.now(),
                  },
                  ...prev,
                ]);
              }}
            />
          )}
        </div>
      </main>

      <nav className="md:hidden fixed bottom-0 left-0 right-0 z-40 bg-white border-t border-[#F5F0FF] grid grid-cols-7 h-16 px-1">
        {TABS.map((tab) => {
          const Icon = tab.icon;
          const active = selectedTab === tab.id;
          return (
            <button
              key={tab.id}
              type="button"
              onClick={() => setSelectedTab(tab.id)}
              className={`flex flex-col items-center justify-center min-h-[44px] cursor-pointer ${
                active ? 'text-[#8B5CF6]' : 'text-[#8B8298]'
              }`}
            >
              <div className={`px-2.5 py-0.5 rounded-full ${active ? 'bg-[#F5F0FF]' : ''}`}>
                <Icon className="w-5 h-5" />
              </div>
              <span className="text-[10px] font-medium mt-0.5 leading-none">{tab.label}</span>
            </button>
          );
        })}
      </nav>
      {showAddMusicModal && (
        <AddMusicModal
          onClose={() => setShowAddMusicModal(false)}
          onAddTrack={(track, openParty) => {
            handleAddTrackToLibrary(track);
            setShowAddMusicModal(false);
            if (openParty) {
              const targetRoom = rooms[0];
              if (targetRoom) {
                setActiveVoiceRoom(targetRoom);
              } else {
                setSelectedTab('party');
              }
            }
          }}
        />
      )}
      {reportingRoom && (
        <ReportRoomModal
          room={reportingRoom}
          onClose={() => setReportingRoom(null)}
          onSubmit={(reason) => {
            setReportedRoomIds((prev) =>
              prev.includes(reportingRoom.id) ? prev : [...prev, reportingRoom.id],
            );
            setReportToast(`Flagged "${reportingRoom.title}" for ${reason}. Thank you for helping keep MY HAPPY PLACE safe.`);
            setReportingRoom(null);
          }}
        />
      )}
      {reportToast && (
        <div
          role="status"
          className="fixed bottom-20 md:bottom-6 right-4 left-4 md:left-auto md:max-w-md z-50 rounded-2xl bg-[#1A142D] text-white px-4 py-3 shadow-xl border border-white/15 flex items-center justify-between gap-3 text-xs"
        >
          <div className="flex items-center gap-2 min-w-0">
            <Flag className="w-4 h-4 text-[#FF7EB6] shrink-0" />
            <span className="leading-snug">{reportToast}</span>
          </div>
          <button
            type="button"
            onClick={() => setReportToast(null)}
            aria-label="Dismiss report confirmation"
            className="w-8 h-8 rounded-full hover:bg-white/10 flex items-center justify-center shrink-0 cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      )}
    </div>
  );
}

function ChatScreen({
  name,
  messages,
  onSendMessage,
  onClearMessages,
  onBack,
}: {
  name: string;
  messages: ChatMessage[];
  onSendMessage: (text: string) => void;
  onClearMessages: () => void;
  onBack: () => void;
}) {
  const [message, setMessage] = useState('');
  const [menuOpen, setMenuOpen] = useState(false);
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages.length]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!message.trim()) return;
    onSendMessage(message);
    setMessage('');
  };

  return (
    <div className="min-h-screen bg-[#FFFBFF] flex flex-col">
      <header className="bg-white border-b border-[#F5F0FF] px-3 py-2.5 flex items-center gap-3 sticky top-0 z-20">
        <button
          type="button"
          onClick={onBack}
          aria-label="Back"
          className="w-11 h-11 rounded-full flex items-center justify-center text-[#251B3A] hover:bg-[#F5F0FF] cursor-pointer"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        <AvatarBadge name={name} size="sm" />
        <div className="flex-1 min-w-0">
          <div className="font-bold text-[#251B3A] text-[17px] truncate">{name}</div>
          <div className="text-[11px] text-[#8B5CF6] font-medium">Online</div>
        </div>
        <div className="relative">
          <button
            type="button"
            onClick={() => setMenuOpen((o) => !o)}
            aria-label="Options"
            className="w-11 h-11 rounded-full flex items-center justify-center text-[#8B8298] hover:bg-[#F5F0FF] cursor-pointer"
          >
            <MoreVertical className="w-5 h-5" />
          </button>
          {menuOpen && (
            <div className="absolute right-0 top-12 w-40 rounded-xl bg-white shadow-lg border border-[#F5F0FF] py-1 z-30">
              <button
                type="button"
                onClick={() => {
                  onClearMessages();
                  setMenuOpen(false);
                }}
                className="w-full text-left px-4 py-2 text-sm text-[#251B3A] hover:bg-[#F5F0FF] cursor-pointer"
              >
                Clear messages
              </button>
            </div>
          )}
        </div>
      </header>

      <div className="flex-1 max-w-3xl w-full mx-auto px-4 py-3.5 space-y-2.5 overflow-y-auto">
        <p className="text-center text-xs text-[#8B8298] pb-1.5">Say hello to {name} 👋</p>
        {messages.map((msg) => (
          <div key={msg.id} className={`flex flex-col ${msg.isFromMe ? 'items-end' : 'items-start'}`}>
            <div
              className={`max-w-[280px] sm:max-w-md px-3.5 py-2.5 text-sm ${
                msg.isFromMe
                  ? 'bg-[#8B5CF6] text-white rounded-[18px] rounded-br-[4px]'
                  : 'bg-[#F5F0FF] text-[#251B3A] rounded-[18px] rounded-bl-[4px]'
              }`}
            >
              {msg.text}
            </div>
            <span className="text-[10px] text-[#8B8298] px-1 pt-0.5 tabular-nums">
              {formatTimestamp(msg.timestamp)}
            </span>
          </div>
        ))}
        <div ref={bottomRef} />
      </div>

      <form onSubmit={handleSubmit} className="bg-white border-t border-[#F5F0FF] px-3 py-2.5 sticky bottom-0">
        <div className="max-w-3xl mx-auto flex items-center gap-2">
          <input
            type="text"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            placeholder="Type a message..."
            className="flex-1 h-11 px-4 rounded-3xl border border-[#8B8298]/40 bg-white text-sm text-[#251B3A] placeholder:text-[#8B8298] focus:outline-none focus:border-[#8B5CF6]"
          />
          <button
            type="submit"
            disabled={!message.trim()}
            aria-label="Send"
            className={`w-12 h-12 rounded-full flex items-center justify-center transition-colors cursor-pointer ${
              message.trim() ? 'bg-[#8B5CF6] text-white' : 'bg-[#F5F0FF] text-[#8B8298]'
            }`}
          >
            <Send className="w-5 h-5" />
          </button>
        </div>
      </form>
    </div>
  );
}

function AddMusicModal({
  onClose,
  onAddTrack,
}: {
  onClose: () => void;
  onAddTrack: (track: PartyTrack, openParty: boolean) => void;
}) {
  const [title, setTitle] = useState('');
  const [artist, setArtist] = useState('');
  const [mood, setMood] = useState<RoomMoodId>('music');
  const [audioSrc, setAudioSrc] = useState<string | undefined>(undefined);
  const [fileName, setFileName] = useState<string | undefined>(undefined);
  const [durationSec, setDurationSec] = useState<number>(180);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const objectUrl = URL.createObjectURL(file);
    setAudioSrc(objectUrl);
    setFileName(file.name);
    if (!title.trim()) {
      setTitle(stripAudioExtension(file.name));
    }
    const probe = new Audio(objectUrl);
    probe.addEventListener('loadedmetadata', () => {
      if (Number.isFinite(probe.duration) && probe.duration > 1) {
        setDurationSec(Math.round(probe.duration));
      }
    });
  };

  const submitTrack = (openParty: boolean) => {
    const cleanTitle = title.trim();
    if (!cleanTitle) return;
    onAddTrack(
      createPartyTrack({
        title: cleanTitle,
        artist,
        mood,
        audioSrc,
        fileName,
        durationSec,
      }),
      openParty,
    );
  };

  return (
    <div
      onClick={onClose}
      className="fixed inset-0 z-50 bg-[#0A0712]/65 flex items-center justify-center p-4"
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="w-full max-w-md rounded-3xl bg-white p-5 space-y-4 shadow-xl"
      >
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-lg font-extrabold text-[#251B3A]">Add Music 🎶</h2>
            <p className="text-xs text-[#8B8298]">Upload an audio file or add a song to play in Party</p>
          </div>
          <button
            type="button"
            onClick={onClose}
            aria-label="Close Add Music"
            className="w-10 h-10 rounded-full bg-[#F5F0FF] text-[#251B3A] flex items-center justify-center cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <label className="w-full min-h-[52px] rounded-2xl border border-dashed border-[#8B5CF6]/45 bg-[#F5F0FF]/70 hover:bg-[#F5F0FF] px-4 py-3 flex items-center justify-between gap-3 cursor-pointer transition-colors">
          <div className="flex items-center gap-2.5 min-w-0">
            <div className="w-9 h-9 rounded-xl bg-[#8B5CF6] text-white flex items-center justify-center shrink-0">
              <Upload className="w-4 h-4" />
            </div>
            <div className="min-w-0">
              <div className="text-xs font-bold text-[#251B3A] truncate">
                {fileName ? fileName : 'Upload audio file (MP3, WAV, M4A)'}
              </div>
              <div className="text-[11px] text-[#8B8298]">
                {fileName ? `${formatTrackTime(durationSec)} ready to play` : 'Optional · Plays your real audio file'}
              </div>
            </div>
          </div>
          <span className="px-3 py-1.5 rounded-xl bg-white text-[#8B5CF6] font-bold text-xs shrink-0">
            {fileName ? 'Change' : 'Choose'}
          </span>
          <input type="file" accept="audio/*" onChange={handleFileChange} className="hidden" />
        </label>

        <div className="space-y-2.5">
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Song title (e.g. Angkor Night Breeze)"
            className="w-full min-h-[44px] px-3.5 rounded-xl border border-[#8B5CF6]/30 bg-white text-sm text-[#251B3A] focus:outline-none focus:ring-2 focus:ring-[#8B5CF6]"
          />
          <input
            type="text"
            value={artist}
            onChange={(e) => setArtist(e.target.value)}
            placeholder="Artist (optional)"
            className="w-full min-h-[44px] px-3.5 rounded-xl border border-[#8B5CF6]/30 bg-white text-sm text-[#251B3A] focus:outline-none focus:ring-2 focus:ring-[#8B5CF6]"
          />
        </div>

        <div>
          <div className="text-xs font-bold text-[#251B3A] mb-2">Music vibe</div>
          <div className="flex items-center gap-1.5 flex-wrap">
            {ROOM_MOODS.map((m) => {
              const MoodIcon = m.icon;
              const active = mood === m.id;
              return (
                <button
                  key={m.id}
                  type="button"
                  onClick={() => setMood(m.id)}
                  className={`min-h-[38px] px-3 rounded-xl flex items-center gap-1.5 text-xs font-bold cursor-pointer transition-colors ${
                    active ? 'text-white' : 'bg-[#F5F0FF] text-[#251B3A]'
                  }`}
                  style={active ? { backgroundColor: m.color } : undefined}
                >
                  <MoodIcon className="w-3.5 h-3.5" style={active ? undefined : { color: m.color }} />
                  <span>{m.label}</span>
                </button>
              );
            })}
          </div>
        </div>

        <div className="flex items-center gap-2 pt-1">
          <button
            type="button"
            disabled={!title.trim()}
            onClick={() => submitTrack(false)}
            className={`flex-1 min-h-[44px] rounded-xl font-bold text-xs cursor-pointer ${
              title.trim() ? 'bg-[#F5F0FF] text-[#8B5CF6] hover:bg-[#EAE0FF]' : 'bg-[#F5F0FF] text-[#8B8298] opacity-60'
            }`}
          >
            Add to Library
          </button>
          <button
            type="button"
            disabled={!title.trim()}
            onClick={() => submitTrack(true)}
            className={`flex-1 min-h-[44px] rounded-xl font-bold text-xs cursor-pointer ${
              title.trim() ? 'bg-[#8B5CF6] text-white' : 'bg-[#8B5CF6]/40 text-white'
            }`}
          >
            Add & Play in Party
          </button>
        </div>
      </div>
    </div>
  );
}

const REPORT_ROOM_REASONS = [
  'Inappropriate language',
  'Harassment or bullying',
  'Spam or scam',
  'Unsafe or explicit content',
  'Other',
];

function ReportRoomModal({
  room,
  onClose,
  onSubmit,
}: {
  room: VoiceRoom;
  onClose: () => void;
  onSubmit: (reason: string, details: string) => void;
}) {
  const [reason, setReason] = useState(REPORT_ROOM_REASONS[0]);
  const [details, setDetails] = useState('');

  return (
    <div
      onClick={onClose}
      className="fixed inset-0 z-50 bg-[#0A0712]/65 flex items-center justify-center p-4"
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="w-full max-w-md rounded-3xl bg-white p-5 space-y-4 shadow-xl"
      >
        <div className="flex items-center justify-between gap-3">
          <div className="min-w-0">
            <h2 className="text-lg font-extrabold text-[#251B3A]">Report Room</h2>
            <p className="text-xs text-[#8B8298] truncate">Flagging: {room.title}</p>
          </div>
          <button
            type="button"
            onClick={onClose}
            aria-label="Close Report Room"
            className="w-10 h-10 rounded-full bg-[#F5F0FF] text-[#251B3A] flex items-center justify-center cursor-pointer shrink-0"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="space-y-2">
          <div className="text-xs font-bold text-[#251B3A]">Select a reason</div>
          <div className="space-y-1.5">
            {REPORT_ROOM_REASONS.map((option) => {
              const active = reason === option;
              return (
                <button
                  key={option}
                  type="button"
                  onClick={() => setReason(option)}
                  className={`w-full min-h-[42px] px-3.5 rounded-xl text-left text-xs font-semibold flex items-center justify-between cursor-pointer transition-colors ${
                    active
                      ? 'bg-[#8B5CF6] text-white'
                      : 'bg-[#F5F0FF] text-[#251B3A] hover:bg-[#EAE0FF]'
                  }`}
                >
                  <span>{option}</span>
                  {active && <Check className="w-4 h-4 shrink-0" />}
                </button>
              );
            })}
          </div>
        </div>

        <div>
          <label htmlFor="report-room-details" className="block text-xs font-bold text-[#251B3A] mb-1.5">
            Additional details (optional)
          </label>
          <textarea
            id="report-room-details"
            rows={3}
            value={details}
            onChange={(e) => setDetails(e.target.value)}
            placeholder="Share any context that helps our moderation team..."
            className="w-full p-3 rounded-xl border border-[#8B5CF6]/30 bg-white text-xs text-[#251B3A] focus:outline-none focus:ring-2 focus:ring-[#8B5CF6]"
          />
        </div>

        <div className="flex items-center gap-2 pt-1">
          <button
            type="button"
            onClick={onClose}
            className="flex-1 min-h-[44px] rounded-xl bg-[#F5F0FF] text-[#251B3A] font-bold text-xs cursor-pointer"
          >
            Cancel
          </button>
          <button
            type="button"
            onClick={() => onSubmit(reason, details.trim())}
            className="flex-1 min-h-[44px] rounded-xl bg-[#FF5C8A] hover:bg-[#F04476] text-white font-bold text-xs flex items-center justify-center gap-1.5 cursor-pointer"
          >
            <Flag className="w-3.5 h-3.5" />
            <span>Submit Report</span>
          </button>
        </div>
      </div>
    </div>
  );
}

function HomeView({
  rooms,
  currentFamily,
  musicCount,
  friends,
  friendRequestCount,
  reportedRoomIds,
  onChat,
  onTab,
  onJoinRoom,
  onReportRoom,
  onOpenFamily,
  onOpenFriends,
  onAddFriend,
  onRemoveFriend,
  onOpenAddMusic,
}: {
  rooms: VoiceRoom[];
  currentFamily: FamilyClub | null;
  musicCount: number;
  friends: string[];
  friendRequestCount: number;
  reportedRoomIds: string[];
  onChat: (name: string) => void;
  onTab: (tab: TabId) => void;
  onJoinRoom: (room: VoiceRoom) => void;
  onReportRoom: (room: VoiceRoom) => void;
  onOpenFamily: () => void;
  onOpenFriends: () => void;
  onAddFriend: (name: string) => void;
  onRemoveFriend: (name: string) => void;
  onOpenAddMusic: () => void;
}) {
  const [notifOpen, setNotifOpen] = useState(false);

  return (
    <div className="pb-8">
      <div className="flex items-center justify-between px-5 py-[18px] relative">
        <div>
          <p className="text-[#8B5CF6] text-xs font-bold tracking-[2px]">MY HAPPY PLACE</p>
          <h1 className="text-[#251B3A] text-[23px] font-extrabold">Find your happy people ✨</h1>
        </div>
        <button
          type="button"
          onClick={() => setNotifOpen((p) => !p)}
          aria-label="Notifications"
          className="w-11 h-11 rounded-full bg-[#F5F0FF] flex items-center justify-center text-[#8B5CF6] cursor-pointer"
        >
          <Bell className="w-5 h-5" />
        </button>
        {notifOpen && (
          <div className="absolute right-5 top-16 z-30 w-64 rounded-2xl bg-white shadow-lg border border-[#F5F0FF] p-3.5 text-xs">
            <div className="flex items-center justify-between mb-2">
              <span className="font-bold text-[#251B3A]">Notifications</span>
              <button type="button" onClick={() => setNotifOpen(false)} className="text-[#8B8298] cursor-pointer">
                <X className="w-4 h-4" />
              </button>
            </div>
            <p className="text-[#8B8298]">Welcome to MY HAPPY PLACE 💜</p>
          </div>
        )}
      </div>

      <div className="px-[18px] space-y-3">
        <div
          className="rounded-[26px] p-5 md:p-7 text-white"
          style={{ background: 'linear-gradient(135deg, #9C6BFA 0%, #FF8BB8 100%)' }}
        >
          <h2 className="text-xl font-bold">A little place for big connections 💜</h2>
          <p className="text-white/90 text-sm mt-1.5">Meet someone new today.</p>
          <div className="flex flex-wrap items-center gap-2.5 mt-3.5">
            <button
              type="button"
              onClick={() => onTab('match')}
              className="px-5 py-2.5 min-h-[44px] rounded-full bg-white text-[#8B5CF6] font-bold text-sm cursor-pointer"
            >
              Explore friends
            </button>
            <button
              type="button"
              onClick={onOpenFriends}
              className="px-5 py-2.5 min-h-[44px] rounded-full bg-white/20 hover:bg-white/30 text-white font-bold text-sm flex items-center gap-1.5 cursor-pointer transition-colors"
            >
              <UserPlus className="w-4 h-4" />
              <span>Add friends</span>
            </button>
          </div>
        </div>

        <button
          type="button"
          onClick={onOpenFriends}
          className="w-full rounded-[20px] bg-[#F5F0FF] hover:bg-[#EAE0FF] transition-colors p-4 flex items-center gap-3 text-left cursor-pointer"
        >
          <div
            className="w-12 h-12 rounded-2xl flex items-center justify-center text-white shrink-0"
            style={{ background: 'linear-gradient(135deg, #FF7EB6 0%, #8B5CF6 100%)' }}
          >
            <UserPlus className="w-6 h-6" />
          </div>
          <div className="flex-1 min-w-0">
            <div className="font-extrabold text-[#251B3A] text-[15px] truncate">Friends &amp; Requests</div>
            <div className="text-xs text-[#8B8298] truncate tabular-nums">
              {friends.length} friends
              {friendRequestCount > 0 ? ` · ${friendRequestCount} pending requests` : ' · Find people by name or handle'}
            </div>
          </div>
          <span className="px-3.5 py-2 rounded-full bg-[#8B5CF6] text-white font-bold text-xs whitespace-nowrap shrink-0 flex items-center gap-1">
            <UserPlus className="w-3.5 h-3.5" />
            <span>Add friends</span>
          </span>
        </button>

        <button
          type="button"
          onClick={onOpenFamily}
          className="w-full rounded-[20px] bg-[#F5F0FF] hover:bg-[#EAE0FF] transition-colors p-4 flex items-center gap-3 text-left cursor-pointer"
        >
          <FamilyLogoBadge
            emblem={currentFamily?.emblem ?? '🏠'}
            color={currentFamily?.color ?? '#8B5CF6'}
            accentColor={currentFamily?.accentColor ?? '#FF7EB6'}
            shape={currentFamily?.logoShape ?? 'circle'}
            logoImage={currentFamily?.logoImage}
            size="md"
          />
          <div className="flex-1 min-w-0">
            <div className="font-extrabold text-[#251B3A] text-[15px] truncate">
              {currentFamily ? currentFamily.name : 'Join a Family'}
            </div>
            <div className="text-xs text-[#8B8298] truncate tabular-nums">
              {currentFamily
                ? `Level ${currentFamily.level} · ${currentFamily.members.length} members · ${currentFamily.points} pts`
                : 'Find your community or create your own crew'}
            </div>
          </div>
          <span className="px-3.5 py-2 rounded-full bg-[#8B5CF6] text-white font-bold text-xs whitespace-nowrap shrink-0">
            Family
          </span>
        </button>

        <button
          type="button"
          onClick={onOpenAddMusic}
          className="w-full rounded-[20px] bg-[#F5F0FF] hover:bg-[#EAE0FF] transition-colors p-4 flex items-center gap-3 text-left cursor-pointer"
        >
          <div
            className="w-12 h-12 rounded-2xl flex items-center justify-center text-white shrink-0"
            style={{ background: 'linear-gradient(135deg, #5B6EE1 0%, #8B5CF6 100%)' }}
          >
            <Music className="w-6 h-6" />
          </div>
          <div className="flex-1 min-w-0">
            <div className="font-extrabold text-[#251B3A] text-[15px] truncate">Party Music Library</div>
            <div className="text-xs text-[#8B8298] truncate tabular-nums">
              {musicCount} songs ready · Upload audio or add tracks
            </div>
          </div>
          <span className="px-3.5 py-2 rounded-full bg-[#5B6EE1] text-white font-bold text-xs whitespace-nowrap shrink-0 flex items-center gap-1">
            <Plus className="w-3.5 h-3.5" />
            <span>Add music</span>
          </span>
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-4 mt-2">
        <section className="lg:col-span-7">
          <div className="flex items-center justify-between px-5 pt-6 pb-2">
            <h2 className="font-bold text-[#251B3A] text-[17px]">People you may like</h2>
            <button type="button" onClick={onOpenFriends} className="text-[#8B5CF6] font-semibold text-xs cursor-pointer">
              See all
            </button>
          </div>
          {SUGGESTED_PEOPLE.map((name) => {
            const isFriend = friends.includes(name);
            return (
              <div
                key={name}
                className="w-full flex items-center gap-3 px-5 py-2 hover:bg-[#F5F0FF]/70 rounded-xl"
              >
                <button
                  type="button"
                  onClick={() => onChat(name)}
                  className="flex-1 min-w-0 flex items-center gap-3 text-left cursor-pointer py-0.5"
                >
                  <AvatarBadge name={name} />
                  <div className="flex-1 min-w-0">
                    <div className="text-[#251B3A] font-bold text-[15px] truncate">{name}</div>
                    <div className="text-[#8B8298] text-xs truncate">
                      {isFriend ? 'Friend · Online' : 'Online · Say hello'}
                    </div>
                  </div>
                </button>
                <button
                  type="button"
                  onClick={() => (isFriend ? onRemoveFriend(name) : onAddFriend(name))}
                  className={`min-h-[38px] px-3 rounded-full text-xs font-bold inline-flex items-center gap-1.5 whitespace-nowrap cursor-pointer transition-colors shrink-0 ${
                    isFriend
                      ? 'bg-[#F5F0FF] text-[#8B5CF6] hover:bg-[#EAE0FF]'
                      : 'bg-[#8B5CF6] text-white hover:bg-[#7C4DFF]'
                  }`}
                >
                  {isFriend ? <UserCheck className="w-3.5 h-3.5" /> : <UserPlus className="w-3.5 h-3.5" />}
                  <span>{isFriend ? 'Friends' : 'Add friend'}</span>
                </button>
                <button
                  type="button"
                  onClick={() => onChat(name)}
                  aria-label={`Message ${name}`}
                  className="min-h-[38px] min-w-[38px] rounded-full flex items-center justify-center text-[#8B5CF6] hover:bg-[#F5F0FF] cursor-pointer shrink-0"
                >
                  <MessageCircle className="w-5 h-5" />
                </button>
              </div>
            );
          })}
        </section>

        <section className="lg:col-span-5">
          <div className="flex items-center justify-between px-5 pt-6 pb-2">
            <h2 className="font-bold text-[#251B3A] text-[17px]">Popular rooms</h2>
            <button type="button" onClick={() => onTab('party')} className="text-[#8B5CF6] text-xs cursor-pointer">
              View all
            </button>
          </div>
          <div className="px-5 space-y-3">
            {rooms.map((room) => (
              <VoiceRoomCard
                key={room.id}
                room={room}
                isReported={reportedRoomIds.includes(room.id)}
                onClick={() => onJoinRoom(room)}
                onReport={() => onReportRoom(room)}
              />
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}

function MatchView({
  friends,
  onAddFriend,
  onChat,
}: {
  friends: string[];
  onAddFriend: (name: string) => void;
  onChat: (name: string) => void;
}) {
  const [matched, setMatched] = useState<string | null>(null);
  const isFriend = matched ? friends.includes(matched) : false;
  return (
    <div className="min-h-full bg-[#F5F0FF] flex flex-col">
      <div className="px-5 py-4">
        <h1 className="text-[27px] font-extrabold text-[#251B3A]">Soul Match</h1>
        <p className="text-[13px] text-[#8B8298]">A new friend could be one tap away</p>
      </div>
      <div className="flex-1 flex flex-col items-center justify-center px-6 py-10 text-center">
        <div
          className="w-[190px] h-[190px] rounded-full flex items-center justify-center"
          style={{ background: 'linear-gradient(135deg, #FF7EB6 0%, #8B5CF6 100%)' }}
        >
          <Heart className="w-[82px] h-[82px] text-white fill-white" />
        </div>
        <h2 className="text-[#251B3A] font-bold text-xl mt-6">
          {matched ? `Matched with ${matched} 💜` : 'Ready to meet someone?'}
        </h2>
        <p className="text-[#8B8298] text-[13px] mt-1">
          Find a person to share a little moment with.
        </p>
        <div className="flex flex-wrap items-center justify-center gap-3 mt-6">
          <button
            type="button"
            onClick={() => setMatched(SUGGESTED_PEOPLE[Math.floor(Math.random() * SUGGESTED_PEOPLE.length)])}
            className="min-h-[48px] px-7 rounded-3xl bg-[#8B5CF6] text-white font-bold text-sm flex items-center gap-2 cursor-pointer"
          >
            <Sparkles className="w-5 h-5" />
            <span>Start matching</span>
          </button>
          {matched && (
            <>
              <button
                type="button"
                onClick={() => onAddFriend(matched)}
                disabled={isFriend}
                className={`min-h-[48px] px-5 rounded-3xl font-bold text-sm flex items-center gap-1.5 cursor-pointer ${
                  isFriend ? 'bg-white/70 text-[#8B5CF6]' : 'bg-white text-[#251B3A] hover:bg-[#EAE0FF]'
                }`}
              >
                {isFriend ? <UserCheck className="w-4 h-4" /> : <UserPlus className="w-4 h-4" />}
                <span>{isFriend ? 'Friends' : 'Add friend'}</span>
              </button>
              <button
                type="button"
                onClick={() => onChat(matched)}
                className="min-h-[48px] px-5 rounded-3xl bg-white text-[#8B5CF6] font-bold text-sm cursor-pointer"
              >
                Chat
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

function VoiceRoomCard({
  room,
  isReported = false,
  onClick,
  onReport,
}: {
  room: VoiceRoom;
  isReported?: boolean;
  onClick: () => void;
  onReport: () => void;
}) {
  const mood = getRoomMood(room.mood);
  const MoodIcon = mood.icon;
  const isActive = room.isActive ?? room.listeners > 0;
  const gradientStart = room.color || mood.gradientFrom;
  const owner = room.owner ?? room.speakers[0] ?? 'Host';
  return (
    <div
      className="VoiceRoomCard group/card w-full rounded-[18px] p-4 flex items-center gap-3 transition-[transform,box-shadow] duration-200 ease-[cubic-bezier(0.16,1,0.3,1)] hover:scale-[1.015] hover:shadow-md active:scale-[0.985] motion-reduce:transform-none motion-reduce:transition-none"
      style={{
        backgroundColor: gradientStart,
        backgroundImage: `linear-gradient(135deg, ${gradientStart} 0%, ${mood.gradientTo} 100%)`,
      }}
    >
      <button
        type="button"
        onClick={onClick}
        className="flex-1 min-w-0 flex items-center gap-3 text-left cursor-pointer focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-white rounded-xl"
      >
        <div className="relative w-11 h-11 flex items-center justify-center shrink-0">
          <Headphones className="w-8 h-8 text-white" />
          <span
            role="img"
            aria-label={`${mood.label} mood: ${mood.description}`}
            className="group/mood absolute -bottom-0.5 -right-0.5 w-5 h-5 rounded-full bg-white flex items-center justify-center shadow-sm"
          >
            <MoodIcon className="w-3 h-3" style={{ color: gradientStart }} />
            <span
              role="tooltip"
              className="pointer-events-none absolute left-0 bottom-full mb-2 z-30 w-max max-w-[210px] rounded-lg bg-[#1A142D] px-2.5 py-1.5 text-left text-[11px] leading-snug text-white shadow-lg ring-1 ring-white/15 opacity-0 translate-y-1 transition-[opacity,transform] duration-150 ease-out group-hover/mood:opacity-100 group-hover/mood:translate-y-0 group-focus-visible/card:opacity-100 group-focus-visible/card:translate-y-0 motion-reduce:transition-none"
            >
              <span className="font-bold">{mood.label}</span>
              <span className="text-white/80"> — {mood.description}</span>
            </span>
          </span>
        </div>
        <div className="flex-1 min-w-0 space-y-1">
          <div className="text-white font-bold text-[15px] truncate">{room.title}</div>
          <div className="text-white/85 text-[11px] truncate tabular-nums">
            {room.detail} · {mood.label}
          </div>
          <div className="flex items-center gap-1.5 min-w-0">
            <AvatarBadge name={owner} size="xs" />
            <span className="text-white/95 text-xs font-semibold truncate">{owner}</span>
          </div>
        </div>
      </button>
      <div className="flex flex-col items-end gap-1.5 shrink-0">
        <div className="flex items-center gap-1.5">
          <span
            aria-label={`${room.listeners} live users`}
            className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-black/20 text-white text-xs font-bold tabular-nums whitespace-nowrap ${
              isActive ? 'live-user-badge--active' : 'opacity-75'
            }`}
          >
            <Users className="w-3.5 h-3.5 shrink-0" />
            <span>{room.listeners}</span>
          </span>
          <button
            type="button"
            onClick={onClick}
            className="text-white font-bold text-xs px-3 py-1 rounded-full bg-white/20 hover:bg-white/30 whitespace-nowrap cursor-pointer transition-colors"
          >
            JOIN
          </button>
        </div>
        <button
          type="button"
          onClick={onReport}
          aria-label={`Report Room: ${room.title}`}
          className={`min-h-[28px] px-2.5 py-0.5 rounded-full text-[11px] font-semibold inline-flex items-center gap-1 whitespace-nowrap cursor-pointer transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-white ${
            isReported
              ? 'bg-[#FF5C8A] text-white'
              : 'bg-black/20 hover:bg-[#FF5C8A] text-white/95 hover:text-white'
          }`}
        >
          <Flag className="w-3 h-3 shrink-0" />
          <span>{isReported ? 'Reported' : 'Report Room'}</span>
        </button>
      </div>
    </div>
  );
}

function EmptyVoiceRoomPlaceholder({
  moodFilter,
  searchQuery,
  onResetAll,
  onCreateRoom,
}: {
  moodFilter: RoomMoodId | 'all';
  searchQuery: string;
  onResetAll: () => void;
  onCreateRoom: () => void;
}) {
  const activeMood = moodFilter === 'all' ? null : getRoomMood(moodFilter);
  const MoodIcon = activeMood ? activeMood.icon : Headphones;
  const accentColor = activeMood ? activeMood.color : '#8B5CF6';
  const trimmedQuery = searchQuery.trim();

  let description =
    'There are no active voice rooms matching this vibe right now. Switch back to All rooms or open a fresh space.';
  if (activeMood && trimmedQuery) {
    description = `No ${activeMood.label.toLowerCase()} rooms matched "${trimmedQuery}" by title or host. Reset to All to browse every live hangout.`;
  } else if (activeMood) {
    description = `No ${activeMood.label.toLowerCase()} voice rooms are live right now. Tap All to view every room or start a ${activeMood.label.toLowerCase()} room.`;
  } else if (trimmedQuery) {
    description = `No live rooms matched "${trimmedQuery}" by title or host name. Tap All to clear filters and explore active rooms.`;
  }

  return (
    <div
      className="mx-5 rounded-[22px] border border-[#8B5CF6]/20 bg-gradient-to-br from-[#F5F0FF] via-white to-[#FFF0F6] p-6 sm:p-7 text-center max-w-lg shadow-sm"
      role="status"
      aria-live="polite"
    >
      <div
        className="mx-auto w-12 h-12 rounded-2xl flex items-center justify-center shadow-sm mb-3"
        style={{ backgroundColor: `${accentColor}1A`, color: accentColor }}
      >
        <MoodIcon className="w-6 h-6" />
      </div>
      <h2 className="text-[#251B3A] font-extrabold text-base">No rooms found for this mood</h2>
      <p className="text-xs text-[#6E657D] mt-1.5 leading-relaxed max-w-sm mx-auto">{description}</p>
      <div className="flex flex-wrap items-center justify-center gap-2.5 mt-4">
        <button
          type="button"
          onClick={onResetAll}
          className="min-h-[44px] px-6 rounded-full bg-[#8B5CF6] hover:bg-[#7C4DFF] text-white font-bold text-xs cursor-pointer transition-colors whitespace-nowrap shadow-sm"
        >
          All
        </button>
        <button
          type="button"
          onClick={onCreateRoom}
          className="min-h-[44px] px-5 rounded-full bg-white hover:bg-[#F5F0FF] text-[#251B3A] border border-[#8B5CF6]/25 font-bold text-xs cursor-pointer transition-colors whitespace-nowrap"
        >
          Create room
        </button>
      </div>
    </div>
  );
}

function PartyView({
  rooms,
  musicLibrary,
  reportedRoomIds,
  onJoinRoom,
  onReportRoom,
  onOpenAddMusic,
  onCreateRoom,
}: {
  rooms: VoiceRoom[];
  musicLibrary: PartyTrack[];
  reportedRoomIds: string[];
  onJoinRoom: (room: VoiceRoom) => void;
  onReportRoom: (room: VoiceRoom) => void;
  onOpenAddMusic: () => void;
  onCreateRoom: (title: string, mood: RoomMoodId, backgroundId: PartyBackgroundId) => void;
}) {
  const [title, setTitle] = useState('');
  const [selectedMood, setSelectedMood] = useState<RoomMoodId>('happy');
  const [selectedBg, setSelectedBg] = useState<PartyBackgroundId>('lounge');
  const [moodFilter, setMoodFilter] = useState<RoomMoodId | 'all'>('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [sortBy, setSortBy] = useState<'popular' | 'newest'>('popular');
  const [creating, setCreating] = useState(false);

  const normalizedQuery = searchQuery.trim().toLowerCase();
  const visibleRooms = rooms
    .filter((r) => {
      const matchesMood = moodFilter === 'all' || r.mood === moodFilter;
      if (!matchesMood) return false;
      if (!normalizedQuery) return true;
      const ownerName = (r.owner ?? r.speakers[0] ?? '').toLowerCase();
      return r.title.toLowerCase().includes(normalizedQuery) || ownerName.includes(normalizedQuery);
    })
    .slice()
    .sort((a, b) => {
      if (sortBy === 'newest') {
        return (b.createdAt ?? 0) - (a.createdAt ?? 0) || b.listeners - a.listeners;
      }
      return b.listeners - a.listeners || (b.createdAt ?? 0) - (a.createdAt ?? 0);
    });

  const activeMoodTheme = moodFilter === 'all' ? null : getRoomMood(moodFilter);
  const lobbyBackdrop = activeMoodTheme
    ? `linear-gradient(180deg, ${activeMoodTheme.gradientFrom}18 0%, ${activeMoodTheme.gradientTo}0D 38%, rgba(255,251,255,0) 78%)`
    : 'linear-gradient(180deg, rgba(139,92,246,0.10) 0%, rgba(255,126,182,0.06) 36%, rgba(255,251,255,0) 75%)';

  return (
    <div className="pb-8 min-h-full transition-[background] duration-300" style={{ backgroundImage: lobbyBackdrop }}>
      <div className="px-5 pt-4 pb-3 flex items-center justify-between gap-3">
        <div>
          <h1 className="text-[27px] font-extrabold text-[#251B3A]">Voice Party 🎙️</h1>
          <p className="text-[13px] text-[#8B8298]">
            Join a room and make new friends · {musicLibrary.length} party tracks ready
          </p>
        </div>
        <button
          type="button"
          onClick={onOpenAddMusic}
          className="min-h-[44px] px-4 rounded-full bg-[#5B6EE1] text-white font-bold text-xs flex items-center gap-1.5 cursor-pointer shrink-0 whitespace-nowrap"
        >
          <Music className="w-4 h-4" />
          <span>Add music</span>
        </button>
      </div>

      <div className="px-5 pb-3 flex flex-wrap items-center gap-2.5 max-w-2xl">
        <div className="relative flex items-center flex-1 min-w-[210px]">
          <label htmlFor="party-room-search" className="sr-only">
            Search rooms by title or owner name
          </label>
          <Search className="w-4 h-4 text-[#8B5CF6] absolute left-3.5 pointer-events-none" />
          <input
            id="party-room-search"
            type="search"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search rooms by title or host name..."
            className="w-full min-h-[44px] pl-10 pr-11 rounded-2xl bg-[#F5F0FF]/80 border border-[#8B5CF6]/20 text-sm text-[#251B3A] placeholder:text-[#8B8298] focus:bg-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#8B5CF6] transition-colors"
          />
          {searchQuery.length > 0 && (
            <button
              type="button"
              onClick={() => setSearchQuery('')}
              aria-label="Clear room search"
              className="absolute right-1.5 min-h-[36px] min-w-[36px] rounded-full flex items-center justify-center text-[#8B8298] hover:text-[#251B3A] hover:bg-[#8B5CF6]/10 cursor-pointer transition-colors"
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>
        <div className="relative flex items-center shrink-0">
          <label htmlFor="party-room-sort" className="sr-only">
            Sort voice rooms
          </label>
          <ArrowUpDown className="w-3.5 h-3.5 text-[#8B5CF6] absolute left-3 pointer-events-none" />
          <select
            id="party-room-sort"
            aria-label="Sort voice rooms"
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value as 'popular' | 'newest')}
            className="min-h-[44px] pl-8 pr-4 rounded-2xl bg-[#F5F0FF]/80 border border-[#8B5CF6]/20 text-xs font-bold text-[#251B3A] focus:bg-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#8B5CF6] cursor-pointer transition-colors"
          >
            <option value="popular">Most Popular</option>
            <option value="newest">Newest</option>
          </select>
        </div>
      </div>

      <div className="px-5 pb-4 flex items-center gap-2 overflow-x-auto">
        <button
          type="button"
          onClick={() => setMoodFilter('all')}
          className={`min-h-[44px] px-4 rounded-full text-xs font-bold whitespace-nowrap shrink-0 cursor-pointer transition-colors ${
            moodFilter === 'all'
              ? 'bg-[#8B5CF6] text-white'
              : 'bg-[#F5F0FF] text-[#251B3A] hover:bg-[#EAE0FF]'
          }`}
        >
          All
        </button>
        {ROOM_MOODS.map((mood) => {
          const MoodIcon = mood.icon;
          const active = moodFilter === mood.id;
          return (
            <button
              key={mood.id}
              type="button"
              onClick={() => setMoodFilter(active ? 'all' : mood.id)}
              className={`min-h-[44px] px-4 rounded-full text-xs font-bold flex items-center gap-1.5 whitespace-nowrap shrink-0 cursor-pointer transition-colors ${
                active ? 'text-white' : 'bg-[#F5F0FF] text-[#251B3A] hover:bg-[#EAE0FF]'
              }`}
              style={active ? { backgroundColor: mood.color } : undefined}
            >
              <MoodIcon className="w-4 h-4 shrink-0" style={active ? undefined : { color: mood.color }} />
              <span>{mood.label}</span>
            </button>
          );
        })}
      </div>

      {visibleRooms.length === 0 ? (
        <EmptyVoiceRoomPlaceholder
          moodFilter={moodFilter}
          searchQuery={searchQuery}
          onResetAll={() => {
            setMoodFilter('all');
            setSearchQuery('');
          }}
          onCreateRoom={() => {
            if (moodFilter !== 'all') setSelectedMood(moodFilter);
            setCreating(true);
          }}
        />
      ) : (
        <div className="px-5 grid grid-cols-1 md:grid-cols-2 gap-3">
          {visibleRooms.map((room) => (
            <VoiceRoomCard
              key={room.id}
              room={room}
              isReported={reportedRoomIds.includes(room.id)}
              onClick={() => onJoinRoom(room)}
              onReport={() => onReportRoom(room)}
            />
          ))}
        </div>
      )}

      <div className="px-5 mt-5 max-w-lg">
        {creating ? (
          <form
            onSubmit={(e) => {
              e.preventDefault();
              if (!title.trim()) return;
              onCreateRoom(title.trim(), selectedMood, selectedBg);
              setTitle('');
              setSelectedMood('happy');
              setSelectedBg('lounge');
              setCreating(false);
            }}
            className="rounded-2xl bg-white border border-[#8B5CF6]/20 p-4 space-y-3 shadow-sm"
          >
            <div className="flex items-center justify-between">
              <span className="text-sm font-bold text-[#251B3A]">Create Voice Room</span>
              <button
                type="button"
                onClick={() => setCreating(false)}
                className="min-h-[44px] min-w-[44px] -mr-2 flex items-center justify-center text-[#8B8298] hover:text-[#251B3A] cursor-pointer"
                aria-label="Close form"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="Room name (e.g. Chill Vibes ☕)"
              className="w-full min-h-[44px] px-3.5 rounded-xl border border-[#8B5CF6]/30 bg-white text-sm text-[#251B3A] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#8B5CF6]"
            />
            <div>
              <div className="text-xs font-bold text-[#251B3A] mb-2">Pick a mood tag</div>
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
                {ROOM_MOODS.map((mood) => {
                  const MoodIcon = mood.icon;
                  const active = selectedMood === mood.id;
                  return (
                    <button
                      key={mood.id}
                      type="button"
                      onClick={() => setSelectedMood(mood.id)}
                      className={`min-h-[44px] px-3 py-2 rounded-xl flex items-center gap-2 text-xs font-bold whitespace-nowrap cursor-pointer transition-colors ${
                        active
                          ? 'text-white shadow-sm'
                          : 'bg-[#F5F0FF] text-[#251B3A] hover:bg-[#EAE0FF]'
                      }`}
                      style={active ? { backgroundColor: mood.color } : undefined}
                    >
                      <MoodIcon className="w-4 h-4 shrink-0" style={active ? undefined : { color: mood.color }} />
                      <span>{mood.label}</span>
                    </button>
                  );
                })}
              </div>
            </div>
            <div>
              <div className="text-xs font-bold text-[#251B3A] mb-2">Room background</div>
              <div className="flex items-center gap-2 overflow-x-auto pb-1">
                {PARTY_BACKGROUNDS.map((bg) => {
                  const active = selectedBg === bg.id;
                  return (
                    <button
                      key={bg.id}
                      type="button"
                      onClick={() => setSelectedBg(bg.id)}
                      className={`min-h-[40px] px-3 py-1.5 rounded-xl flex items-center gap-2 text-xs font-semibold whitespace-nowrap shrink-0 cursor-pointer border transition-colors ${
                        active
                          ? 'border-[#8B5CF6] bg-[#F5F0FF] text-[#251B3A]'
                          : 'border-[#F5F0FF] bg-white text-[#6E657D] hover:bg-[#F5F0FF]/50'
                      }`}
                    >
                      <span
                        className="w-4 h-4 rounded-md shrink-0 bg-cover bg-center border border-black/10"
                        style={
                          bg.imageSrc
                            ? { backgroundImage: `url(${bg.imageSrc})` }
                            : { backgroundImage: bg.gradient }
                        }
                      />
                      <span>{bg.label}</span>
                    </button>
                  );
                })}
              </div>
            </div>
            <div className="flex items-center gap-2 pt-1">
              <button
                type="button"
                onClick={() => setCreating(false)}
                className="min-h-[44px] px-4 rounded-xl bg-[#F5F0FF] text-[#251B3A] font-semibold text-sm cursor-pointer"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="flex-1 min-h-[44px] px-4 rounded-xl bg-[#8B5CF6] text-white font-bold text-sm cursor-pointer"
              >
                Join Room
              </button>
            </div>
          </form>
        ) : (
          <button
            type="button"
            onClick={() => setCreating(true)}
            className="w-full min-h-[48px] rounded-full bg-[#8B5CF6] text-white font-bold text-sm flex items-center justify-center gap-2 cursor-pointer"
          >
            <Plus className="w-5 h-5" />
            <span>Create a room</span>
          </button>
        )}
      </div>
      <p className="text-[#8B8298] text-[11px] px-5 pt-5">
        Voice rooms are demo screens. Live audio needs a voice service connection.
      </p>
    </div>
  );
}

function VoicePartyRoomView({
  room,
  diamonds,
  musicLibrary,
  onAddTrackToLibrary,
  onSendGift,
  onUpdateRoomBackground,
  onLeave,
}: {
  room: VoiceRoom;
  diamonds: number;
  musicLibrary: PartyTrack[];
  onAddTrackToLibrary: (track: PartyTrack) => void;
  onSendGift: (gift: KhmerGift, recipientName: string) => void;
  onUpdateRoomBackground: (roomId: string, backgroundId: PartyBackgroundId, customBackground?: string | null) => void;
  onLeave: () => void;
}) {
  const [onStage, setOnStage] = useState(room.speakers.includes('You'));
  const [isMuted, setIsMuted] = useState(true);
  const [roomInput, setRoomInput] = useState('');
  const [lastReaction, setLastReaction] = useState<string | null>(null);
  const [showGiftDrawer, setShowGiftDrawer] = useState(false);
  const [showBgDrawer, setShowBgDrawer] = useState(false);
  const [selectedGift, setSelectedGift] = useState<KhmerGift>(KHMER_GIFTS[0]);
  const giftRecipients = room.speakers.filter((s) => s !== 'You');
  const recipientOptions = giftRecipients.length > 0 ? giftRecipients : ['Mina', 'Dara', 'Sophy'];
  const [selectedRecipient, setSelectedRecipient] = useState(recipientOptions[0]);
  const [giftBanner, setGiftBanner] = useState<string | null>(null);
  const [roomChat, setRoomChat] = useState<{ sender: string; text: string }[]>([
    { sender: 'Mina', text: `Welcome to ${room.title}! 💜` },
    { sender: 'Dara', text: 'Grab a mic seat, queue music, or customize the room background 🎙️🎨' },
  ]);

  const activeBgId: PartyBackgroundId = room.backgroundId ?? 'mood';
  const activeBgPreset = PARTY_BACKGROUNDS.find((b) => b.id === activeBgId) ?? PARTY_BACKGROUNDS[1];
  const bgImageUrl =
    activeBgId === 'custom' && room.customBackground
      ? room.customBackground
      : activeBgPreset.imageSrc;
  const bgGradient =
    activeBgId === 'mood' ? getMoodRoomGradient(room.mood) : activeBgPreset.gradient;

  const handleUploadRoomBg = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      if (typeof reader.result === 'string') {
        onUpdateRoomBackground(room.id, 'custom', reader.result);
        setRoomChat((prev) => [...prev, { sender: 'You', text: '🖼️ Updated room background photo' }]);
        setShowBgDrawer(false);
      }
    };
    reader.readAsDataURL(file);
  };

  const [musicQueue, setMusicQueue] = useState<PartyTrack[]>(musicLibrary);
  const [currentTrackIndex, setCurrentTrackIndex] = useState(() => {
    const idx = musicLibrary.findIndex((t) => t.mood === room.mood);
    return idx >= 0 ? idx : 0;
  });
  const [isMusicPlaying, setIsMusicPlaying] = useState(false);
  const [elapsedSec, setElapsedSec] = useState(0);
  const [musicVolume, setMusicVolume] = useState(0.65);
  const [isMusicMuted, setIsMusicMuted] = useState(false);
  const [showMusicDrawer, setShowMusicDrawer] = useState(false);
  const [customTrackTitle, setCustomTrackTitle] = useState('');
  const [customTrackArtist, setCustomTrackArtist] = useState('');
  const [customTrackMood, setCustomTrackMood] = useState<RoomMoodId>(room.mood);
  const [customAudioSrc, setCustomAudioSrc] = useState<string | undefined>(undefined);
  const [customFileName, setCustomFileName] = useState<string | undefined>(undefined);
  const [customDurationSec, setCustomDurationSec] = useState<number>(180);

  const audioCtxRef = useRef<AudioContext | null>(null);
  const masterGainRef = useRef<GainNode | null>(null);
  const stepIndexRef = useRef(0);
  const audioElemRef = useRef<HTMLAudioElement | null>(null);

  useEffect(() => {
    setMusicQueue(musicLibrary);
  }, [musicLibrary]);

  const currentTrack = musicQueue[currentTrackIndex] ?? musicQueue[0];

  const ensureAudioContext = () => {
    const AudioCtx = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
    if (!AudioCtx) return null;
    if (!audioCtxRef.current) {
      const ctx = new AudioCtx();
      const gain = ctx.createGain();
      gain.gain.value = isMusicMuted ? 0 : musicVolume * 0.22;
      gain.connect(ctx.destination);
      audioCtxRef.current = ctx;
      masterGainRef.current = gain;
    }
    if (audioCtxRef.current.state === 'suspended') {
      void audioCtxRef.current.resume();
    }
    return audioCtxRef.current;
  };

  useEffect(() => {
    if (masterGainRef.current && audioCtxRef.current) {
      const target = isMusicMuted ? 0 : musicVolume * 0.22;
      masterGainRef.current.gain.setTargetAtTime(target, audioCtxRef.current.currentTime, 0.04);
    }
    if (audioElemRef.current) {
      audioElemRef.current.volume = isMusicMuted ? 0 : musicVolume;
      audioElemRef.current.muted = isMusicMuted;
    }
  }, [musicVolume, isMusicMuted]);

  useEffect(() => {
    const audioEl = audioElemRef.current;
    if (!audioEl) return;
    if (currentTrack.audioSrc) {
      if (audioEl.src !== currentTrack.audioSrc) {
        audioEl.src = currentTrack.audioSrc;
      }
      if (isMusicPlaying) {
        void audioEl.play().catch(() => {});
      } else {
        audioEl.pause();
      }
    } else {
      audioEl.pause();
      audioEl.removeAttribute('src');
    }
  }, [isMusicPlaying, currentTrack.id, currentTrack.audioSrc]);

  useEffect(() => {
    if (!isMusicPlaying || currentTrack.audioSrc) return;
    const ctx = ensureAudioContext();
    const stepMs = Math.max(220, Math.round((60 / currentTrack.bpm) * 500));

    const playStep = () => {
      if (!ctx || !masterGainRef.current) return;
      const now = ctx.currentTime;
      const idx = stepIndexRef.current % currentTrack.notes.length;
      const freq = currentTrack.notes[idx];

      const osc = ctx.createOscillator();
      const noteGain = ctx.createGain();
      osc.type = currentTrack.mood === 'energetic' ? 'triangle' : 'sine';
      osc.frequency.setValueAtTime(freq, now);
      noteGain.gain.setValueAtTime(0.001, now);
      noteGain.gain.exponentialRampToValueAtTime(0.35, now + 0.03);
      noteGain.gain.exponentialRampToValueAtTime(0.001, now + stepMs / 1000 * 0.9);
      osc.connect(noteGain);
      noteGain.connect(masterGainRef.current);
      osc.start(now);
      osc.stop(now + stepMs / 1000);

      if (idx % 2 === 0) {
        const bassOsc = ctx.createOscillator();
        const bassGain = ctx.createGain();
        bassOsc.type = 'sine';
        bassOsc.frequency.setValueAtTime(currentTrack.bassFreq, now);
        bassGain.gain.setValueAtTime(0.001, now);
        bassGain.gain.exponentialRampToValueAtTime(0.28, now + 0.04);
        bassGain.gain.exponentialRampToValueAtTime(0.001, now + (stepMs / 1000) * 1.6);
        bassOsc.connect(bassGain);
        bassGain.connect(masterGainRef.current);
        bassOsc.start(now);
        bassOsc.stop(now + (stepMs / 1000) * 1.7);
      }

      stepIndexRef.current += 1;
    };

    playStep();
    const synthTimer = window.setInterval(playStep, stepMs);
    return () => window.clearInterval(synthTimer);
  }, [isMusicPlaying, currentTrack.id, currentTrack.bpm, currentTrack.audioSrc]);

  useEffect(() => {
    if (!isMusicPlaying || currentTrack.audioSrc) return;
    const tick = window.setInterval(() => {
      setElapsedSec((prev) => {
        if (prev + 1 >= currentTrack.durationSec) {
          setCurrentTrackIndex((idx) => (idx + 1) % musicQueue.length);
          stepIndexRef.current = 0;
          return 0;
        }
        return prev + 1;
      });
    }, 1000);
    return () => window.clearInterval(tick);
  }, [isMusicPlaying, currentTrack.durationSec, currentTrack.audioSrc, musicQueue.length]);

  useEffect(() => {
    return () => {
      if (audioElemRef.current) {
        audioElemRef.current.pause();
      }
      if (audioCtxRef.current) {
        void audioCtxRef.current.close();
        audioCtxRef.current = null;
      }
    };
  }, []);

  const toggleMusicPlayback = () => {
    if (!isMusicPlaying && !currentTrack.audioSrc) {
      ensureAudioContext();
    }
    setIsMusicPlaying((p) => !p);
  };

  const selectTrack = (index: number, announce = true) => {
    const next = musicQueue[index];
    if (!next) return;
    if (!next.audioSrc) {
      ensureAudioContext();
    }
    setCurrentTrackIndex(index);
    setElapsedSec(0);
    stepIndexRef.current = 0;
    setIsMusicPlaying(true);
    if (announce) {
      setRoomChat((prev) => [...prev, { sender: 'You', text: `🎶 Now playing "${next.title}"` }]);
    }
  };

  const handleNextTrack = () => {
    const nextIdx = (currentTrackIndex + 1) % musicQueue.length;
    selectTrack(nextIdx, false);
  };

  const handlePrevTrack = () => {
    if (elapsedSec > 4) {
      setElapsedSec(0);
      if (audioElemRef.current && currentTrack.audioSrc) {
        audioElemRef.current.currentTime = 0;
      }
      return;
    }
    const prevIdx = (currentTrackIndex - 1 + musicQueue.length) % musicQueue.length;
    selectTrack(prevIdx, false);
  };

  const handleUploadTrackFile = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const objectUrl = URL.createObjectURL(file);
    setCustomAudioSrc(objectUrl);
    setCustomFileName(file.name);
    if (!customTrackTitle.trim()) {
      setCustomTrackTitle(stripAudioExtension(file.name));
    }
    const probe = new Audio(objectUrl);
    probe.addEventListener('loadedmetadata', () => {
      if (Number.isFinite(probe.duration) && probe.duration > 1) {
        setCustomDurationSec(Math.round(probe.duration));
      }
    });
  };

  const speakers = onStage && !room.speakers.includes('You') ? ['You', ...room.speakers] : room.speakers;
  const progressPct = Math.min(100, Math.round((elapsedSec / Math.max(1, currentTrack.durationSec)) * 100));

  return (
    <div
      className="min-h-screen text-white flex flex-col relative overflow-hidden"
      style={{ backgroundColor: '#1A142D', backgroundImage: bgGradient }}
    >
      {bgImageUrl && (
        <div
          aria-hidden="true"
          className="pointer-events-none absolute inset-0 bg-cover bg-center"
          style={{ backgroundImage: `url(${bgImageUrl})` }}
        />
      )}
      <div
        aria-hidden="true"
        className={`pointer-events-none absolute inset-0 ${
          bgImageUrl
            ? 'bg-gradient-to-b from-[#120D22]/75 via-[#16102B]/55 to-[#120D22]/85'
            : 'bg-radial from-white/5 via-transparent to-black/25'
        }`}
      />
      <audio
        ref={audioElemRef}
        onLoadedMetadata={(e) => {
          const dur = e.currentTarget.duration;
          if (Number.isFinite(dur) && dur > 1) {
            const rounded = Math.round(dur);
            setMusicQueue((prev) =>
              prev.map((t) => (t.id === currentTrack.id ? { ...t, durationSec: rounded } : t)),
            );
          }
        }}
        onTimeUpdate={(e) => {
          if (currentTrack.audioSrc) {
            setElapsedSec(Math.floor(e.currentTarget.currentTime));
          }
        }}
        onEnded={() => {
          setCurrentTrackIndex((idx) => (idx + 1) % musicQueue.length);
          setElapsedSec(0);
        }}
      />
      <header className="relative z-10 px-4 py-3.5 flex items-center gap-2.5 border-b border-white/10 bg-[#140E26]/45 backdrop-blur-md">
        <button
          type="button"
          onClick={onLeave}
          aria-label="Back"
          className="w-11 h-11 rounded-full flex items-center justify-center hover:bg-white/10 cursor-pointer shrink-0"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        {(() => {
          const mood = getRoomMood(room.mood);
          const MoodIcon = mood.icon;
          return (
            <>
              <div
                className="w-9 h-9 rounded-full flex items-center justify-center shrink-0"
                style={{ backgroundColor: mood.color }}
                title={mood.label}
              >
                <MoodIcon className="w-4 h-4 text-white" />
              </div>
              <div className="flex-1 min-w-0">
                <h1 className="font-extrabold text-[17px] truncate">{room.title}</h1>
                <p className="text-[#CBBEE6] text-[11px] tabular-nums truncate">
                  {room.listeners + 1} listening · {mood.label} · 💎 {diamonds}
                </p>
              </div>
            </>
          );
        })()}
        <button
          type="button"
          onClick={() => setShowBgDrawer(true)}
          aria-label="Change party background"
          className="min-h-[38px] px-3 rounded-full bg-white/15 hover:bg-white/25 text-white font-semibold text-xs inline-flex items-center gap-1.5 cursor-pointer shrink-0 transition-colors"
        >
          <Palette className="w-3.5 h-3.5" />
          <span>Background</span>
        </button>
        <button
          type="button"
          onClick={onLeave}
          className="px-4 py-2 rounded-full bg-[#FF5C8A] text-white font-bold text-xs cursor-pointer shrink-0"
        >
          Leave
        </button>
      </header>

      <div className="relative z-10 flex-1 max-w-3xl w-full mx-auto px-4 py-4 flex flex-col gap-4">
        {giftBanner && (
          <div className="rounded-xl bg-gradient-to-r from-[#68468E]/90 to-[#3F2B63]/90 backdrop-blur-md px-3.5 py-2 flex items-center justify-between text-xs font-bold text-[#FFD77A]">
            <span>{giftBanner}</span>
            <button type="button" onClick={() => setGiftBanner(null)} className="text-white/70 cursor-pointer">
              <X className="w-4 h-4" />
            </button>
          </div>
        )}

        <div className="rounded-2xl bg-[#22193D]/80 backdrop-blur-md border border-white/15 p-3 space-y-2.5">
          <div className="flex items-center gap-3">
            <button
              type="button"
              onClick={() => setShowMusicDrawer(true)}
              aria-label="Open Party Music jukebox"
              className="w-11 h-11 rounded-full flex items-center justify-center shrink-0 cursor-pointer"
              style={{ backgroundColor: currentTrack.color }}
            >
              <Disc3 className={`w-6 h-6 text-white ${isMusicPlaying ? 'party-disc--playing' : ''}`} />
            </button>
            <button
              type="button"
              onClick={() => setShowMusicDrawer(true)}
              className="flex-1 min-w-0 text-left cursor-pointer"
            >
              <div className="flex items-center gap-2">
                <span className="font-bold text-[13px] text-white truncate">{currentTrack.title}</span>
                {isMusicPlaying && (
                  <span className="inline-flex items-end gap-0.5 h-3 shrink-0" aria-hidden="true">
                    <span className="w-0.5 h-3 rounded-full bg-[#FF7EB6] party-eq-bar" />
                    <span className="w-0.5 h-2 rounded-full bg-[#FFD77A] party-eq-bar" style={{ animationDelay: '0.2s' }} />
                    <span className="w-0.5 h-3 rounded-full bg-[#70D9FF] party-eq-bar" style={{ animationDelay: '0.4s' }} />
                  </span>
                )}
              </div>
              <div className="text-[11px] text-[#CBBEE6] truncate tabular-nums">
                {currentTrack.artist} · {currentTrack.vibe} · {formatTrackTime(elapsedSec)} / {formatTrackTime(currentTrack.durationSec)}
              </div>
            </button>
            <div className="flex items-center gap-1.5 shrink-0">
              <button
                type="button"
                onClick={toggleMusicPlayback}
                aria-label={isMusicPlaying ? 'Pause party music' : 'Play party music'}
                className="w-11 h-11 rounded-full bg-[#8B5CF6] hover:bg-[#7C3AED] text-white flex items-center justify-center cursor-pointer transition-colors"
              >
                {isMusicPlaying ? <Pause className="w-4 h-4" /> : <Play className="w-4 h-4 ml-0.5" />}
              </button>
              <button
                type="button"
                onClick={handleNextTrack}
                aria-label="Next track"
                className="w-10 h-10 rounded-full bg-[#392E5C] hover:bg-[#493B74] text-white flex items-center justify-center cursor-pointer transition-colors"
              >
                <SkipForward className="w-4 h-4" />
              </button>
              <button
                type="button"
                onClick={() => setShowMusicDrawer(true)}
                aria-label="Music queue"
                className="w-10 h-10 rounded-full bg-[#392E5C] hover:bg-[#493B74] text-white flex items-center justify-center cursor-pointer transition-colors"
              >
                <ListMusic className="w-4 h-4" />
              </button>
            </div>
          </div>
          <div className="w-full h-1.5 rounded-full bg-white/10 overflow-hidden">
            <div
              className="h-full rounded-full transition-[width] duration-300"
              style={{ width: `${progressPct}%`, backgroundColor: currentTrack.color }}
            />
          </div>
        </div>

        <div className="flex items-center justify-between">
          <span className="text-[#FFD77A] font-bold text-xs">Speakers on stage</span>
          {lastReaction && <span className="text-[#FF7EB6] font-bold text-xs">Reaction: {lastReaction}</span>}
        </div>
        <div className="grid grid-cols-3 sm:grid-cols-4 gap-2.5">
          {speakers.map((speaker, idx) => {
            const isMe = speaker === 'You';
            const speaking = isMe ? onStage && !isMuted : idx === 0;
            return (
              <div key={speaker} className="rounded-2xl bg-[#22193D]/78 backdrop-blur-md border border-white/10 py-3 px-2 flex flex-col items-center">
                <div className="relative">
                  <div
                    className="w-14 h-14 rounded-full flex items-center justify-center text-xl font-extrabold"
                    style={{
                      background: speaking
                        ? 'linear-gradient(135deg, #FF7EB6, #8B5CF6)'
                        : 'linear-gradient(135deg, #56477E, #3C315D)',
                    }}
                  >
                    {speaker.slice(0, 1)}
                  </div>
                  <div
                    className={`absolute -bottom-0.5 -right-0.5 w-5 h-5 rounded-full flex items-center justify-center ${
                      speaking ? 'bg-emerald-500' : 'bg-[#1A142D]'
                    }`}
                  >
                    {speaking ? <Mic className="w-3 h-3" /> : <MicOff className="w-3 h-3" />}
                  </div>
                </div>
                <span className={`font-bold text-xs mt-2 ${isMe ? 'text-[#FFD77A]' : 'text-white'}`}>
                  {speaker}
                </span>
                <span className="text-[10px] text-[#CBBEE6]">{speaking ? 'Speaking' : 'Listening'}</span>
              </div>
            );
          })}
        </div>

        <div className="flex-1 flex flex-col gap-2">
          <span className="text-[#CBBEE6] font-bold text-xs">Room chat</span>
          <div className="space-y-1.5 max-h-56 overflow-y-auto">
            {roomChat.map((entry, idx) => (
              <div key={idx} className="rounded-xl bg-[#20183A]/80 backdrop-blur-sm border border-white/10 px-3 py-2 text-xs">
                <span className="text-[#FF7EB6] font-bold">{entry.sender}: </span>
                <span>{entry.text}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="relative z-10 bg-[#1B1432]/85 backdrop-blur-md border-t border-white/10 px-4 py-3 space-y-2.5 sticky bottom-0">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            if (!roomInput.trim()) return;
            setRoomChat((prev) => [...prev, { sender: 'You', text: roomInput.trim() }]);
            setRoomInput('');
          }}
          className="max-w-3xl mx-auto flex items-center gap-2"
        >
          <input
            type="text"
            value={roomInput}
            onChange={(e) => setRoomInput(e.target.value)}
            placeholder="Say something in room..."
            className="flex-1 h-10 px-4 rounded-3xl bg-[#1A142D] border border-white/20 text-xs text-white placeholder:text-[#A799C4] focus:outline-none focus:border-[#8B5CF6]"
          />
          <button
            type="submit"
            aria-label="Send"
            className="w-10 h-10 rounded-full bg-[#8B5CF6] text-white flex items-center justify-center cursor-pointer"
          >
            <Send className="w-4 h-4" />
          </button>
        </form>
        <div className="max-w-3xl mx-auto flex items-center gap-2">
          <button
            type="button"
            onClick={() => {
              if (!onStage) {
                setOnStage(true);
                setIsMuted(false);
              } else {
                setIsMuted((m) => !m);
              }
            }}
            className={`flex-1 min-h-[42px] rounded-full font-bold text-xs flex items-center justify-center gap-1.5 cursor-pointer ${
              onStage && !isMuted ? 'bg-[#8B5CF6] text-white' : 'bg-[#392E5C] text-white'
            }`}
          >
            {onStage && !isMuted ? <Mic className="w-4 h-4" /> : <MicOff className="w-4 h-4" />}
            <span>{!onStage ? 'Take Mic' : isMuted ? 'Unmute' : 'Mic Live'}</span>
          </button>
          {onStage && (
            <button
              type="button"
              onClick={() => {
                setOnStage(false);
                setIsMuted(true);
              }}
              className="px-4 min-h-[42px] rounded-full border border-white/30 text-xs font-semibold cursor-pointer"
            >
              Leave Mic
            </button>
          )}
          <button
            type="button"
            onClick={() => setShowMusicDrawer(true)}
            aria-label="Open Party Music"
            className={`w-10 h-10 rounded-full flex items-center justify-center cursor-pointer shrink-0 ${
              isMusicPlaying ? 'bg-[#5B6EE1] text-white ring-2 ring-white/30' : 'bg-[#5B6EE1] text-white'
            }`}
          >
            <Music className="w-4 h-4" />
          </button>
          <button
            type="button"
            onClick={() => setShowGiftDrawer(true)}
            aria-label="Send Party Gift"
            className="w-10 h-10 rounded-full bg-[#FFD16B] text-[#24152F] flex items-center justify-center cursor-pointer shrink-0"
          >
            <Gift className="w-4 h-4" />
          </button>
          {['❤️', '👏', '🔥'].map((emoji) => (
            <button
              key={emoji}
              type="button"
              onClick={() => {
                setLastReaction(emoji);
                setRoomChat((prev) => [...prev, { sender: 'You', text: `Sent ${emoji}` }]);
              }}
              className="w-10 h-10 rounded-full bg-[#392E5C] flex items-center justify-center text-base cursor-pointer shrink-0"
            >
              {emoji}
            </button>
          ))}
        </div>
      </div>

      {showMusicDrawer && (
        <div
          onClick={() => setShowMusicDrawer(false)}
          className="fixed inset-0 z-50 bg-[#0A0712]/70 flex items-end justify-center"
        >
          <div
            onClick={(e) => e.stopPropagation()}
            className="w-full max-w-2xl max-h-[85vh] overflow-y-auto rounded-t-[26px] bg-[#211938] p-4 space-y-4"
          >
            <div className="flex items-center justify-between">
              <div>
                <h2 className="text-[#FFD68A] font-extrabold text-base">Party Music Jukebox 🎶</h2>
                <p className="text-[11px] text-[#CBBEE6]">
                  {isMusicPlaying ? 'Playing live in room' : 'Paused · Tap play to start room tunes'}
                </p>
              </div>
              <button
                type="button"
                onClick={() => setShowMusicDrawer(false)}
                aria-label="Close Party Music"
                className="w-10 h-10 rounded-full bg-[#2C2347] text-white flex items-center justify-center cursor-pointer"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="rounded-2xl bg-[#2C2347] p-3.5 space-y-3">
              <div className="flex items-center gap-3">
                <div
                  className="w-12 h-12 rounded-full flex items-center justify-center shrink-0"
                  style={{ backgroundColor: currentTrack.color }}
                >
                  <Disc3 className={`w-6 h-6 text-white ${isMusicPlaying ? 'party-disc--playing' : ''}`} />
                </div>
                <div className="flex-1 min-w-0">
                  <div className="font-bold text-sm text-white truncate">{currentTrack.title}</div>
                  <div className="text-xs text-[#CBBEE6] truncate">
                    {currentTrack.artist} · {currentTrack.vibe}
                  </div>
                </div>
              </div>

              <div className="space-y-1">
                <input
                  type="range"
                  min={0}
                  max={currentTrack.durationSec}
                  value={elapsedSec}
                  onChange={(e) => {
                    const nextSec = Number(e.target.value);
                    setElapsedSec(nextSec);
                    if (audioElemRef.current && currentTrack.audioSrc) {
                      audioElemRef.current.currentTime = nextSec;
                    }
                  }}
                  aria-label="Seek track position"
                  className="w-full accent-[#8B5CF6] cursor-pointer"
                />
                <div className="flex justify-between text-[11px] text-[#CBBEE6] tabular-nums">
                  <span>{formatTrackTime(elapsedSec)}</span>
                  <span>{formatTrackTime(currentTrack.durationSec)}</span>
                </div>
              </div>

              <div className="flex items-center justify-between gap-3">
                <div className="flex items-center gap-2">
                  <button
                    type="button"
                    onClick={handlePrevTrack}
                    aria-label="Previous track"
                    className="w-11 h-11 rounded-full bg-[#392E5C] text-white flex items-center justify-center cursor-pointer"
                  >
                    <SkipBack className="w-4 h-4" />
                  </button>
                  <button
                    type="button"
                    onClick={toggleMusicPlayback}
                    aria-label={isMusicPlaying ? 'Pause' : 'Play'}
                    className="w-12 h-12 rounded-full bg-[#8B5CF6] text-white flex items-center justify-center cursor-pointer"
                  >
                    {isMusicPlaying ? <Pause className="w-5 h-5" /> : <Play className="w-5 h-5 ml-0.5" />}
                  </button>
                  <button
                    type="button"
                    onClick={handleNextTrack}
                    aria-label="Next track"
                    className="w-11 h-11 rounded-full bg-[#392E5C] text-white flex items-center justify-center cursor-pointer"
                  >
                    <SkipForward className="w-4 h-4" />
                  </button>
                </div>

                <div className="flex items-center gap-2 flex-1 max-w-[180px]">
                  <button
                    type="button"
                    onClick={() => setIsMusicMuted((m) => !m)}
                    aria-label={isMusicMuted ? 'Unmute music' : 'Mute music'}
                    className="w-9 h-9 rounded-full bg-[#392E5C] text-white flex items-center justify-center cursor-pointer shrink-0"
                  >
                    {isMusicMuted || musicVolume === 0 ? <VolumeX className="w-4 h-4" /> : <Volume2 className="w-4 h-4" />}
                  </button>
                  <input
                    type="range"
                    min={0}
                    max={100}
                    value={isMusicMuted ? 0 : Math.round(musicVolume * 100)}
                    onChange={(e) => {
                      const val = Number(e.target.value) / 100;
                      setMusicVolume(val);
                      if (val > 0 && isMusicMuted) setIsMusicMuted(false);
                    }}
                    aria-label="Music volume"
                    className="w-full accent-[#FF7EB6] cursor-pointer"
                  />
                </div>
              </div>
            </div>

            <form
              onSubmit={(e) => {
                e.preventDefault();
                const cleanTitle = customTrackTitle.trim();
                if (!cleanTitle) return;
                const newTrack = createPartyTrack({
                  title: cleanTitle,
                  artist: customTrackArtist,
                  mood: customTrackMood,
                  audioSrc: customAudioSrc,
                  fileName: customFileName,
                  durationSec: customDurationSec,
                });
                const nextIndex = musicQueue.length;
                setMusicQueue((prev) => [...prev, newTrack]);
                onAddTrackToLibrary(newTrack);
                setCurrentTrackIndex(nextIndex);
                setElapsedSec(0);
                stepIndexRef.current = 0;
                setIsMusicPlaying(true);
                setRoomChat((prev) => [...prev, { sender: 'You', text: `🎵 Added & playing "${cleanTitle}"` }]);
                setCustomTrackTitle('');
                setCustomTrackArtist('');
                setCustomAudioSrc(undefined);
                setCustomFileName(undefined);
                setCustomDurationSec(180);
              }}
              className="rounded-2xl bg-[#281F44] p-3 space-y-2.5"
            >
              <div className="flex items-center justify-between gap-2">
                <span className="text-xs font-bold text-[#FFD77A]">Add Music to Party</span>
                <label className="min-h-[34px] px-3 rounded-xl bg-[#392E5C] hover:bg-[#493B74] text-white text-[11px] font-bold flex items-center gap-1.5 cursor-pointer shrink-0">
                  <Upload className="w-3.5 h-3.5" />
                  <span>{customFileName ? 'Change file' : 'Upload audio'}</span>
                  <input type="file" accept="audio/*" onChange={handleUploadTrackFile} className="hidden" />
                </label>
              </div>
              {customFileName && (
                <div className="flex items-center justify-between rounded-xl bg-[#1A142D] px-3 py-1.5 text-[11px] text-[#70D9FF]">
                  <span className="truncate">Attached: {customFileName}</span>
                  <button
                    type="button"
                    onClick={() => {
                      setCustomAudioSrc(undefined);
                      setCustomFileName(undefined);
                      setCustomDurationSec(180);
                    }}
                    className="text-white/70 hover:text-white ml-2 cursor-pointer"
                  >
                    Remove
                  </button>
                </div>
              )}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                <input
                  type="text"
                  value={customTrackTitle}
                  onChange={(e) => setCustomTrackTitle(e.target.value)}
                  placeholder="Song title (e.g. Phnom Penh Sunset)"
                  className="h-10 px-3 rounded-xl bg-[#1A142D] border border-white/15 text-xs text-white placeholder:text-[#A799C4] focus:outline-none focus:border-[#8B5CF6]"
                />
                <input
                  type="text"
                  value={customTrackArtist}
                  onChange={(e) => setCustomTrackArtist(e.target.value)}
                  placeholder="Artist (optional)"
                  className="h-10 px-3 rounded-xl bg-[#1A142D] border border-white/15 text-xs text-white placeholder:text-[#A799C4] focus:outline-none focus:border-[#8B5CF6]"
                />
              </div>
              <div className="flex items-center justify-between gap-2 flex-wrap">
                <div className="flex items-center gap-1.5 flex-wrap">
                  {ROOM_MOODS.map((m) => (
                    <button
                      key={m.id}
                      type="button"
                      onClick={() => setCustomTrackMood(m.id)}
                      className={`px-2.5 py-1 rounded-full text-[11px] font-semibold cursor-pointer ${
                        customTrackMood === m.id ? 'text-white' : 'bg-[#1A142D] text-[#CBBEE6]'
                      }`}
                      style={customTrackMood === m.id ? { backgroundColor: m.color } : undefined}
                    >
                      {m.label}
                    </button>
                  ))}
                </div>
                <button
                  type="submit"
                  className="min-h-[40px] px-4 rounded-xl bg-[#8B5CF6] text-white font-bold text-xs flex items-center gap-1.5 cursor-pointer shrink-0"
                >
                  <Plus className="w-3.5 h-3.5" />
                  <span>Add & Play</span>
                </button>
              </div>
            </form>

            <div className="space-y-1.5">
              <div className="text-xs font-bold text-[#CBBEE6]">Room Queue ({musicQueue.length})</div>
              {musicQueue.map((track, idx) => {
                const active = idx === currentTrackIndex;
                return (
                  <button
                    key={track.id}
                    type="button"
                    onClick={() => selectTrack(idx)}
                    className={`w-full rounded-xl p-2.5 flex items-center gap-3 text-left cursor-pointer transition-colors ${
                      active ? 'bg-[#3F2E68] ring-1 ring-[#8B5CF6]' : 'bg-[#2C2347] hover:bg-[#342A54]'
                    }`}
                  >
                    <div
                      className="w-9 h-9 rounded-lg flex items-center justify-center shrink-0"
                      style={{ backgroundColor: track.color }}
                    >
                      <Music className="w-4 h-4 text-white" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="text-xs font-bold text-white truncate">{track.title}</div>
                      <div className="text-[11px] text-[#CBBEE6] truncate">
                        {track.artist} · {track.vibe}
                      </div>
                    </div>
                    <span className="text-[11px] text-[#CBBEE6] tabular-nums shrink-0">
                      {active && isMusicPlaying ? 'Playing' : formatTrackTime(track.durationSec)}
                    </span>
                  </button>
                );
              })}
            </div>
          </div>
        </div>
      )}

      {showGiftDrawer && (
        <div
          onClick={() => setShowGiftDrawer(false)}
          className="fixed inset-0 z-50 bg-[#0A0712]/70 flex items-end justify-center"
        >
          <div
            onClick={(e) => e.stopPropagation()}
            className="w-full max-w-2xl rounded-t-[26px] bg-[#211938] p-4 space-y-3"
          >
            <div className="flex items-center justify-between">
              <span className="text-[#FFD68A] font-extrabold text-base">Send Party Gift 🎁</span>
              <span className="text-[#70D9FF] font-bold text-sm tabular-nums">💎 {diamonds}</span>
            </div>
            <div className="flex items-center gap-2 flex-wrap">
              <span className="text-xs text-[#CBBEE6]">To:</span>
              {recipientOptions.slice(0, 5).map((person) => (
                <button
                  key={person}
                  type="button"
                  onClick={() => setSelectedRecipient(person)}
                  className={`px-3 py-1 rounded-full text-xs font-semibold cursor-pointer ${
                    selectedRecipient === person ? 'bg-[#FFD16B] text-[#24152F]' : 'bg-[#392E5C] text-white'
                  }`}
                >
                  {person}
                </button>
              ))}
            </div>
            <div className="grid grid-cols-4 gap-2">
              {KHMER_GIFTS.map((gift) => {
                const active = selectedGift.id === gift.id;
                return (
                  <button
                    key={gift.id}
                    type="button"
                    onClick={() => setSelectedGift(gift)}
                    className={`rounded-2xl p-2 flex flex-col items-center cursor-pointer ${
                      active ? 'bg-[#594078] ring-1 ring-[#FFD16B]' : 'bg-[#2C2347]'
                    }`}
                  >
                    <span className="text-2xl">{gift.emoji}</span>
                    <span className="text-[11px] font-semibold truncate w-full text-center mt-1">{gift.name}</span>
                    <span className="text-[10px] text-[#71D9FF] font-bold tabular-nums">💎 {gift.price}</span>
                  </button>
                );
              })}
            </div>
            <button
              type="button"
              onClick={() => {
                if (diamonds >= selectedGift.price) {
                  onSendGift(selectedGift, selectedRecipient);
                  setRoomChat((prev) => [
                    ...prev,
                    { sender: 'You', text: `Sent ${selectedGift.emoji} ${selectedGift.name} to ${selectedRecipient}!` },
                  ]);
                  setGiftBanner(`🎉 Sent ${selectedGift.emoji} ${selectedGift.name} to ${selectedRecipient}!`);
                  setShowGiftDrawer(false);
                } else {
                  setGiftBanner('ពេជ្រមិនគ្រប់គ្រាន់ទេ!');
                  setShowGiftDrawer(false);
                }
              }}
              className="w-full min-h-[44px] rounded-full bg-[#8B5CF6] text-white font-bold text-sm flex items-center justify-center gap-2 cursor-pointer"
            >
              <Gift className="w-4 h-4" />
              <span>Send {selectedGift.name} (💎 {selectedGift.price})</span>
            </button>
          </div>
        </div>
      )}

      {showBgDrawer && (
        <div
          onClick={() => setShowBgDrawer(false)}
          className="fixed inset-0 z-50 bg-[#0A0712]/70 flex items-end justify-center"
        >
          <div
            onClick={(e) => e.stopPropagation()}
            className="w-full max-w-2xl rounded-t-[26px] bg-[#211938] p-4 space-y-4"
          >
            <div className="flex items-center justify-between">
              <div>
                <h2 className="text-[#FFD68A] font-extrabold text-base">Party Room Background 🎨</h2>
                <p className="text-[11px] text-[#CBBEE6]">
                  Choose a scene, atmospheric gradient, or upload your own photo
                </p>
              </div>
              <button
                type="button"
                onClick={() => setShowBgDrawer(false)}
                aria-label="Close Background Picker"
                className="w-10 h-10 rounded-full bg-[#2C2347] text-white flex items-center justify-center cursor-pointer"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-3 gap-2.5">
              {PARTY_BACKGROUNDS.map((bg) => {
                const selected = activeBgId === bg.id;
                const previewBg = bg.id === 'mood' ? getMoodRoomGradient(room.mood) : bg.gradient;
                return (
                  <button
                    key={bg.id}
                    type="button"
                    onClick={() => {
                      onUpdateRoomBackground(room.id, bg.id, null);
                      setRoomChat((prev) => [
                        ...prev,
                        { sender: 'You', text: `🎨 Switched room background to ${bg.label}` },
                      ]);
                      setShowBgDrawer(false);
                    }}
                    className={`rounded-2xl overflow-hidden text-left border transition-all cursor-pointer ${
                      selected
                        ? 'border-[#FFD16B] ring-2 ring-[#FFD16B]/50'
                        : 'border-white/10 hover:border-white/30'
                    }`}
                  >
                    <div
                      className="h-20 w-full bg-cover bg-center relative"
                      style={
                        bg.imageSrc
                          ? { backgroundImage: `url(${bg.imageSrc})` }
                          : { backgroundImage: previewBg }
                      }
                    >
                      {selected && (
                        <span className="absolute top-2 right-2 w-6 h-6 rounded-full bg-[#FFD16B] text-[#24152F] flex items-center justify-center shadow">
                          <Check className="w-3.5 h-3.5" />
                        </span>
                      )}
                    </div>
                    <div className="p-2.5 bg-[#2C2347]">
                      <div className="font-bold text-xs text-white truncate">{bg.label}</div>
                      <div className="text-[10px] text-[#CBBEE6] truncate">{bg.subtitle}</div>
                    </div>
                  </button>
                );
              })}
            </div>

            <label className="w-full min-h-[46px] rounded-2xl bg-[#392E5C] hover:bg-[#493B74] border border-white/15 text-white font-bold text-xs flex items-center justify-center gap-2 cursor-pointer transition-colors">
              <ImagePlus className="w-4 h-4 text-[#FFD77A]" />
              <span>{activeBgId === 'custom' ? 'Change custom background photo' : 'Upload custom background photo'}</span>
              <input type="file" accept="image/*" onChange={handleUploadRoomBg} className="hidden" />
            </label>
          </div>
        </div>
      )}
    </div>
  );
}

function ProfileView({
  displayName,
  bio,
  profilePhoto,
  coverPhoto,
  giftHistory,
  currentFamily,
  friendsCount,
  friendRequestCount,
  onOpenFriends,
  onOpenFamily,
  onSave,
}: {
  displayName: string;
  bio: string;
  profilePhoto: string | null;
  coverPhoto: string | null;
  giftHistory: GiftRecord[];
  currentFamily: FamilyClub | null;
  friendsCount: number;
  friendRequestCount: number;
  onOpenFriends: () => void;
  onOpenFamily: () => void;
  onSave: (next: { displayName: string; bio: string; profilePhoto: string | null; coverPhoto: string | null }) => void;
}) {
  const [name, setName] = useState(displayName);
  const [about, setAbout] = useState(bio);
  const [avatar, setAvatar] = useState(profilePhoto);
  const [cover, setCover] = useState(coverPhoto);
  const [saved, setSaved] = useState(false);
  const [historyFilter, setHistoryFilter] = useState<'all' | 'sent' | 'received'>('all');

  const pickFile = (e: React.ChangeEvent<HTMLInputElement>, setter: (v: string) => void) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => typeof reader.result === 'string' && setter(reader.result);
    reader.readAsDataURL(file);
  };

  const filteredHistory = giftHistory.filter((item) => {
    if (historyFilter === 'sent') return item.isSent;
    if (historyFilter === 'received') return !item.isSent;
    return true;
  });

  const myRole = currentFamily?.members.find((m) => m.name === 'You')?.role ?? 'Member';

  return (
    <div className="pb-10 max-w-2xl">
      <div className="px-5 py-4">
        <h1 className="text-[27px] font-extrabold text-[#251B3A]">My Profile</h1>
        <p className="text-[13px] text-[#8B8298]">Your space, your identity</p>
      </div>
      <div className="px-[18px]">
        <label
          className="w-full h-[140px] rounded-[20px] overflow-hidden flex items-center justify-center cursor-pointer"
          style={{ background: 'linear-gradient(135deg, #FF7EB6 0%, #8B5CF6 100%)' }}
        >
          <input type="file" accept="image/*" className="hidden" onChange={(e) => pickFile(e, setCover)} />
          {cover ? (
            <img src={cover} alt="Cover" className="w-full h-full object-cover" />
          ) : (
            <div className="flex flex-col items-center text-white">
              <ImagePlus className="w-7 h-7 mb-1" />
              <span className="text-[13px]">Tap to add cover photo</span>
            </div>
          )}
        </label>
      </div>
      <div className="flex flex-col items-center mt-3">
        <label
          className="w-[112px] h-[112px] rounded-full overflow-hidden flex items-center justify-center cursor-pointer"
          style={{ background: 'linear-gradient(135deg, #FF7EB6 0%, #8B5CF6 100%)' }}
        >
          <input type="file" accept="image/*" className="hidden" onChange={(e) => pickFile(e, setAvatar)} />
          {avatar ? (
            <img src={avatar} alt="Profile" className="w-full h-full object-cover" />
          ) : (
            <Camera className="w-10 h-10 text-white" />
          )}
        </label>
        <p className="text-[#8B8298] text-[11px] mt-1.5">Tap photo to choose from Gallery</p>
      </div>
      <div className="px-5 mt-4 space-y-3">
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Your name"
          className="w-full px-3.5 py-2.5 rounded-xl border border-[#8B8298]/40 bg-white text-sm"
        />
        <textarea
          rows={2}
          value={about}
          onChange={(e) => setAbout(e.target.value)}
          placeholder="Bio"
          className="w-full px-3.5 py-2.5 rounded-xl border border-[#8B8298]/40 bg-white text-sm resize-none"
        />
        <button
          type="button"
          onClick={() => {
            onSave({ displayName: name, bio: about, profilePhoto: avatar, coverPhoto: cover });
            setSaved(true);
            window.setTimeout(() => setSaved(false), 1800);
          }}
          className="w-full min-h-[48px] rounded-full bg-[#8B5CF6] text-white font-bold text-sm flex items-center justify-center gap-2 cursor-pointer"
        >
          {saved ? <Check className="w-5 h-5" /> : <Save className="w-5 h-5" />}
          <span>{saved ? 'Saved!' : 'Save profile'}</span>
        </button>
      </div>

      {/* My Friends & Family Sections */}
      <section className="px-5 mt-6 space-y-2.5">
        <button
          type="button"
          onClick={onOpenFriends}
          className="w-full rounded-2xl bg-[#F5F0FF] hover:bg-[#EAE0FF] transition-colors p-4 flex items-center gap-3 text-left cursor-pointer"
        >
          <div
            className="w-10 h-10 rounded-full flex items-center justify-center text-white shrink-0"
            style={{ background: 'linear-gradient(135deg, #FF7EB6 0%, #8B5CF6 100%)' }}
          >
            <UserPlus className="w-5 h-5" />
          </div>
          <div className="flex-1 min-w-0">
            <div className="font-bold text-sm text-[#251B3A] truncate">My Friends</div>
            <div className="text-xs text-[#8B8298] truncate tabular-nums">
              {friendsCount} friends{friendRequestCount > 0 ? ` · ${friendRequestCount} requests` : ''}
            </div>
          </div>
          <span className="text-xs font-bold text-[#8B5CF6]">Add friends</span>
        </button>

        <button
          type="button"
          onClick={onOpenFamily}
          className="w-full rounded-2xl bg-[#F5F0FF] hover:bg-[#EAE0FF] transition-colors p-4 flex items-center gap-3 text-left cursor-pointer"
        >
          <FamilyLogoBadge
            emblem={currentFamily?.emblem ?? '🏠'}
            color={currentFamily?.color ?? '#8B5CF6'}
            accentColor={currentFamily?.accentColor ?? '#FF7EB6'}
            shape={currentFamily?.logoShape ?? 'circle'}
            logoImage={currentFamily?.logoImage}
            size="sm"
          />
          <div className="flex-1 min-w-0">
            <div className="font-bold text-sm text-[#251B3A] truncate">
              {currentFamily?.name ?? 'No Family yet'}
            </div>
            <div className="text-xs text-[#8B8298] truncate tabular-nums">
              {currentFamily
                ? `${myRole} · Level ${currentFamily.level} · ${currentFamily.members.length} members`
                : 'Tap to join or create a Family'}
            </div>
          </div>
          <span className="text-xs font-bold text-[#8B5CF6]">Open</span>
        </button>
      </section>

      {/* Gift History Section */}
      <section className="px-5 mt-7">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-lg font-bold text-[#251B3A]">Gift History</h2>
            <p className="text-[11px] text-[#8B8298]">Khmer traditional gifts sent &amp; received</p>
          </div>
          <span className="text-xs font-semibold text-[#8B5CF6] tabular-nums">
            {filteredHistory.length} items
          </span>
        </div>

        <div className="flex items-center gap-2 mt-3">
          {(['all', 'sent', 'received'] as const).map((filterKey) => {
            const active = historyFilter === filterKey;
            const label = filterKey === 'all' ? 'All' : filterKey === 'sent' ? 'Sent' : 'Received';
            return (
              <button
                key={filterKey}
                type="button"
                onClick={() => setHistoryFilter(filterKey)}
                className={`px-3.5 py-1.5 rounded-full text-xs font-semibold transition-colors cursor-pointer ${
                  active ? 'bg-[#8B5CF6] text-white' : 'bg-[#F5F0FF] text-[#251B3A] hover:bg-[#EAE0FF]'
                }`}
              >
                {label}
              </button>
            );
          })}
        </div>

        <div className="mt-3 space-y-2">
          {filteredHistory.map((item) => (
            <div
              key={item.id}
              className="rounded-2xl bg-white border border-[#F5F0FF] p-3 flex items-center gap-3"
            >
              <div className="w-11 h-11 rounded-xl bg-[#F5F0FF] flex items-center justify-center text-2xl shrink-0">
                {item.emoji}
              </div>
              <div className="flex-1 min-w-0">
                <div className="font-bold text-sm text-[#251B3A] truncate">{item.giftName}</div>
                <div className="text-[11px] text-[#8B8298] truncate">
                  {item.isSent ? `Sent to ${item.peerName}` : `Received from ${item.peerName}`} ·{' '}
                  {formatTimestamp(item.timestamp)}
                </div>
              </div>
              <div className="text-right shrink-0">
                <div
                  className={`text-xs font-bold tabular-nums ${
                    item.isSent ? 'text-[#FF7EB6]' : 'text-[#8B5CF6]'
                  }`}
                >
                  {item.isSent ? `-💎 ${item.price}` : `+💎 ${item.price}`}
                </div>
                <div className="text-[10px] text-[#8B8298]">{item.isSent ? 'Sent' : 'Received'}</div>
              </div>
            </div>
          ))}
        </div>
      </section>

      <p className="text-[#8B8298] text-[11px] px-5 pt-5">
        រូបភាពបង្ហាញក្នុង App បច្ចុប្បន្ន។ ការរក្សាទុក Online ត្រូវភ្ជាប់ Firebase Storage បន្ថែម។
      </p>
    </div>
  );
}

const FAMILY_LOGO_PALETTES: { primary: string; accent: string }[] = [
  { primary: '#8B5CF6', accent: '#FF7EB6' },
  { primary: '#E87A5D', accent: '#FFB86C' },
  { primary: '#5C9DAD', accent: '#38BDF8' },
  { primary: '#E58AAE', accent: '#7555B8' },
];

function deriveMonogram(name: string): string {
  const words = name.trim().split(/\s+/).filter(Boolean);
  if (words.length >= 2) return `${words[0][0]}${words[1][0]}`.toUpperCase();
  if (words.length === 1) return words[0].slice(0, 2).toUpperCase();
  return 'HP';
}

function FamilyLogoBadge({
  emblem,
  color,
  accentColor = '#FF7EB6',
  shape = 'circle',
  logoImage,
  size = 'md',
}: {
  emblem: string;
  color: string;
  accentColor?: string;
  shape?: FamilyLogoShape;
  logoImage?: string | null;
  size?: 'sm' | 'md' | 'lg' | 'xl';
}) {
  const sizeCls =
    size === 'sm'
      ? 'w-11 h-11 text-xl'
      : size === 'lg'
        ? 'w-14 h-14 text-3xl'
        : size === 'xl'
          ? 'w-16 h-16 text-3xl'
          : 'w-12 h-12 text-2xl';
  const radiusCls =
    shape === 'circle'
      ? 'rounded-full'
      : shape === 'badge'
        ? 'rounded-2xl'
        : 'rounded-t-xl rounded-b-3xl';
  const isMonogram = emblem.length <= 2 && /^[a-z0-9]+$/i.test(emblem);
  const textCls = isMonogram ? (size === 'xl' ? 'text-xl font-extrabold' : 'text-sm font-extrabold') : '';

  return (
    <div
      className={`${sizeCls} ${radiusCls} overflow-hidden flex items-center justify-center text-white shrink-0 ring-2 ring-white/30 shadow-sm`}
      style={{ background: `linear-gradient(135deg, ${color} 0%, ${accentColor} 100%)` }}
    >
      {logoImage ? (
        <img src={logoImage} alt="Family logo" className="w-full h-full object-cover" />
      ) : (
        <span className={textCls}>{emblem}</span>
      )}
    </div>
  );
}

function FamilyView({
  families,
  myFamilyId,
  diamonds,
  checkedInToday,
  onBack,
  onJoinFamily,
  onLeaveFamily,
  onCreateFamily,
  onUpdateFamilyLogo,
  onCheckIn,
  onPostAnnouncement,
  onStartFamilyParty,
  onChatMember,
}: {
  families: FamilyClub[];
  myFamilyId: string | null;
  diamonds: number;
  checkedInToday: boolean;
  onBack: () => void;
  onJoinFamily: (familyId: string) => void;
  onLeaveFamily: (familyId: string) => void;
  onCreateFamily: (
    name: string,
    motto: string,
    emblem: string,
    color: string,
    accentColor: string,
    logoShape: FamilyLogoShape,
    logoImage: string | null,
  ) => void;
  onUpdateFamilyLogo: (
    familyId: string,
    emblem: string,
    color: string,
    accentColor: string,
    logoShape: FamilyLogoShape,
    logoImage: string | null,
  ) => void;
  onCheckIn: (familyId: string) => void;
  onPostAnnouncement: (familyId: string, text: string) => void;
  onStartFamilyParty: (family: FamilyClub) => void;
  onChatMember: (memberName: string) => void;
}) {
  const [activeTab, setActiveTab] = useState<'mine' | 'explore'>(myFamilyId ? 'mine' : 'explore');
  const [wallInput, setWallInput] = useState('');
  const [showLogoBuilder, setShowLogoBuilder] = useState(false);
  const [editingFamilyId, setEditingFamilyId] = useState<string | null>(null);
  const [newName, setNewName] = useState('');
  const [newMotto, setNewMotto] = useState('');
  const [newEmblem, setNewEmblem] = useState('🌸');
  const [useMonogram, setUseMonogram] = useState(false);
  const [paletteIdx, setPaletteIdx] = useState(0);
  const [logoShape, setLogoShape] = useState<FamilyLogoShape>('circle');
  const [logoImage, setLogoImage] = useState<string | null>(null);

  const myFamily = families.find((f) => f.id === myFamilyId) ?? null;
  const activePalette = FAMILY_LOGO_PALETTES[paletteIdx] ?? FAMILY_LOGO_PALETTES[0];
  const resolvedEmblem = useMonogram ? deriveMonogram(newName || myFamily?.name || 'HP') : newEmblem;

  const pickLogoFile = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      if (typeof reader.result === 'string') setLogoImage(reader.result);
    };
    reader.readAsDataURL(file);
  };

  const openEditLogo = (family: FamilyClub) => {
    setEditingFamilyId(family.id);
    setNewName(family.name);
    const isMono = family.emblem.length <= 2 && /^[a-z0-9]+$/i.test(family.emblem);
    setUseMonogram(isMono);
    if (!isMono) setNewEmblem(family.emblem);
    const idx = FAMILY_LOGO_PALETTES.findIndex((p) => p.primary === family.color);
    setPaletteIdx(idx >= 0 ? idx : 0);
    setLogoShape(family.logoShape ?? 'circle');
    setLogoImage(family.logoImage ?? null);
    setShowLogoBuilder(true);
  };

  const renderLogoBuilder = () => {
    const isEditing = editingFamilyId !== null;
    return (
      <form
        onSubmit={(e) => {
          e.preventDefault();
          if (isEditing && editingFamilyId) {
            onUpdateFamilyLogo(
              editingFamilyId,
              resolvedEmblem,
              activePalette.primary,
              activePalette.accent,
              logoShape,
              logoImage,
            );
            setEditingFamilyId(null);
            setShowLogoBuilder(false);
          } else {
            if (!newName.trim()) return;
            onCreateFamily(
              newName.trim(),
              newMotto.trim(),
              resolvedEmblem,
              activePalette.primary,
              activePalette.accent,
              logoShape,
              logoImage,
            );
            setNewName('');
            setNewMotto('');
            setLogoImage(null);
            setUseMonogram(false);
            setShowLogoBuilder(false);
            setActiveTab('mine');
          }
        }}
        className="rounded-2xl bg-white border border-[#8B5CF6]/20 p-4 space-y-3.5 shadow-sm"
      >
        <div className="flex items-center justify-between">
          <span className="font-bold text-sm text-[#251B3A]">
            {isEditing ? 'Customize Family Logo' : 'Create Family & Logo'}
          </span>
          <button
            type="button"
            onClick={() => {
              setShowLogoBuilder(false);
              setEditingFamilyId(null);
            }}
            className="min-h-[44px] min-w-[44px] -mr-2 flex items-center justify-center text-[#8B8298] cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="rounded-2xl bg-[#F5F0FF] p-3.5 flex items-center gap-3.5">
          <FamilyLogoBadge
            emblem={resolvedEmblem}
            color={activePalette.primary}
            accentColor={activePalette.accent}
            shape={logoShape}
            logoImage={logoImage}
            size="xl"
          />
          <div className="flex-1 min-w-0">
            <div className="text-xs font-bold text-[#251B3A]">Live Logo Preview</div>
            <div className="flex flex-wrap items-center gap-2 mt-1.5">
              <label className="min-h-[36px] px-3 py-1.5 rounded-xl bg-white text-[#8B5CF6] text-xs font-bold inline-flex items-center gap-1.5 cursor-pointer">
                <ImagePlus className="w-3.5 h-3.5" />
                <span>Upload photo</span>
                <input type="file" accept="image/*" className="hidden" onChange={pickLogoFile} />
              </label>
              {logoImage && (
                <button
                  type="button"
                  onClick={() => setLogoImage(null)}
                  className="min-h-[36px] px-3 py-1.5 rounded-xl bg-white text-[#D9386A] text-xs font-bold cursor-pointer"
                >
                  Use crest
                </button>
              )}
            </div>
          </div>
        </div>

        {!isEditing && (
          <>
            <input
              type="text"
              value={newName}
              onChange={(e) => setNewName(e.target.value)}
              placeholder="Family name (e.g. Lotus Vibes)"
              className="w-full min-h-[44px] px-3.5 rounded-xl border border-[#8B5CF6]/30 text-sm"
            />
            <input
              type="text"
              value={newMotto}
              onChange={(e) => setNewMotto(e.target.value)}
              placeholder="Family motto"
              className="w-full min-h-[44px] px-3.5 rounded-xl border border-[#8B5CF6]/30 text-sm"
            />
          </>
        )}

        <div>
          <div className="text-xs font-bold text-[#251B3A] mb-1.5">Logo shape</div>
          <div className="grid grid-cols-3 gap-2">
            {(
              [
                { id: 'circle', label: 'Circle' },
                { id: 'badge', label: 'Badge' },
                { id: 'shield', label: 'Shield' },
              ] as const
            ).map((s) => (
              <button
                key={s.id}
                type="button"
                onClick={() => setLogoShape(s.id)}
                className={`min-h-[44px] rounded-xl text-xs font-bold cursor-pointer transition-colors ${
                  logoShape === s.id ? 'bg-[#8B5CF6] text-white' : 'bg-[#F5F0FF] text-[#251B3A]'
                }`}
              >
                {s.label}
              </button>
            ))}
          </div>
        </div>

        <div>
          <div className="text-xs font-bold text-[#251B3A] mb-1.5">Crest symbol or initials</div>
          <div className="flex flex-wrap gap-2">
            {['🌸', '👑', '🐉', '✨', '🔥', '💎', '🪷'].map((emblem) => {
              const active = !useMonogram && newEmblem === emblem;
              return (
                <button
                  key={emblem}
                  type="button"
                  onClick={() => {
                    setUseMonogram(false);
                    setNewEmblem(emblem);
                    setLogoImage(null);
                  }}
                  className={`w-11 h-11 rounded-full text-xl flex items-center justify-center cursor-pointer ${
                    active ? 'bg-[#8B5CF6]' : 'bg-[#F5F0FF]'
                  }`}
                >
                  {emblem}
                </button>
              );
            })}
            <button
              type="button"
              onClick={() => {
                setUseMonogram(true);
                setLogoImage(null);
              }}
              className={`min-h-[44px] px-3.5 rounded-full text-xs font-extrabold cursor-pointer ${
                useMonogram ? 'bg-[#8B5CF6] text-white' : 'bg-[#F5F0FF] text-[#251B3A]'
              }`}
            >
              ABC Initials
            </button>
          </div>
        </div>

        <div>
          <div className="text-xs font-bold text-[#251B3A] mb-1.5">Logo gradient</div>
          <div className="flex gap-2.5">
            {FAMILY_LOGO_PALETTES.map((palette, idx) => (
              <button
                key={palette.primary}
                type="button"
                onClick={() => setPaletteIdx(idx)}
                className="w-11 h-11 rounded-full flex items-center justify-center text-white cursor-pointer"
                style={{ background: `linear-gradient(135deg, ${palette.primary}, ${palette.accent})` }}
              >
                {paletteIdx === idx && <Check className="w-4 h-4" />}
              </button>
            ))}
          </div>
        </div>

        <div className="flex gap-2 pt-1">
          <button
            type="button"
            onClick={() => {
              setShowLogoBuilder(false);
              setEditingFamilyId(null);
            }}
            className="min-h-[44px] px-4 rounded-xl bg-[#F5F0FF] text-[#251B3A] font-semibold text-sm cursor-pointer"
          >
            Cancel
          </button>
          <button
            type="submit"
            className="flex-1 min-h-[44px] rounded-xl bg-[#8B5CF6] text-white font-bold text-sm cursor-pointer"
          >
            {isEditing ? 'Save Logo' : 'Create Family'}
          </button>
        </div>
      </form>
    );
  };

  return (
    <div className="min-h-screen bg-[#FFFBFF] flex flex-col">
      <header className="bg-white border-b border-[#F5F0FF] px-3 py-2.5 flex items-center gap-3 sticky top-0 z-20">
        <button
          type="button"
          onClick={onBack}
          aria-label="Back"
          className="w-11 h-11 rounded-full flex items-center justify-center text-[#251B3A] hover:bg-[#F5F0FF] cursor-pointer"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        <div className="flex-1 min-w-0">
          <h1 className="text-[#251B3A] font-extrabold text-[19px]">Family</h1>
          <p className="text-[11px] text-[#8B8298]">Connect with your community</p>
        </div>
        <span className="text-xs font-bold text-[#8B5CF6] px-3 py-1.5 rounded-full bg-[#F5F0FF] tabular-nums">
          💎 {diamonds}
        </span>
      </header>

      <div className="max-w-3xl w-full mx-auto px-5 py-5 space-y-5">
        <div className="grid grid-cols-2 gap-2 p-1 bg-[#F5F0FF] rounded-2xl">
          <button
            type="button"
            onClick={() => setActiveTab('mine')}
            className={`min-h-[44px] rounded-xl text-sm font-bold cursor-pointer transition-colors ${
              activeTab === 'mine' ? 'bg-[#8B5CF6] text-white' : 'text-[#251B3A]'
            }`}
          >
            My Family
          </button>
          <button
            type="button"
            onClick={() => setActiveTab('explore')}
            className={`min-h-[44px] rounded-xl text-sm font-bold cursor-pointer transition-colors ${
              activeTab === 'explore' ? 'bg-[#8B5CF6] text-white' : 'text-[#251B3A]'
            }`}
          >
            Explore Families
          </button>
        </div>

        {activeTab === 'mine' ? (
          myFamily ? (
            <div className="space-y-5">
              <div className="rounded-[24px] p-5 text-white" style={{ backgroundColor: myFamily.color }}>
                <div className="flex items-center gap-3.5">
                  <FamilyLogoBadge
                    emblem={myFamily.emblem}
                    color={myFamily.color}
                    accentColor={myFamily.accentColor}
                    shape={myFamily.logoShape}
                    logoImage={myFamily.logoImage}
                    size="lg"
                  />
                  <div className="flex-1 min-w-0">
                    <h2 className="text-xl font-extrabold truncate">{myFamily.name}</h2>
                    <p className="text-xs text-white/90 tabular-nums">
                      Level {myFamily.level} · {myFamily.points} pts · {myFamily.members.length} members
                    </p>
                  </div>
                  <button
                    type="button"
                    onClick={() => openEditLogo(myFamily)}
                    className="min-h-[36px] px-3 py-1.5 rounded-full bg-white/20 hover:bg-white/30 text-white font-bold text-xs whitespace-nowrap cursor-pointer"
                  >
                    Edit logo
                  </button>
                </div>
                <p className="text-sm text-white/90 mt-3">{myFamily.motto}</p>
                <div className="grid grid-cols-2 gap-2.5 mt-4">
                  <button
                    type="button"
                    disabled={checkedInToday}
                    onClick={() => onCheckIn(myFamily.id)}
                    className={`min-h-[44px] rounded-full font-bold text-xs cursor-pointer transition-opacity ${
                      checkedInToday ? 'bg-white/30 text-white cursor-default' : 'bg-white text-[#251B3A]'
                    }`}
                  >
                    {checkedInToday ? 'Checked in ✓' : 'Check in (+25 💎)'}
                  </button>
                  <button
                    type="button"
                    onClick={() => onStartFamilyParty(myFamily)}
                    className="min-h-[44px] rounded-full bg-[#1A142D] text-white font-bold text-xs flex items-center justify-center gap-1.5 cursor-pointer"
                  >
                    <Headphones className="w-4 h-4" />
                    <span>Family Party</span>
                  </button>
                </div>
              </div>

              {showLogoBuilder && editingFamilyId === myFamily.id && renderLogoBuilder()}

              <section className="space-y-2.5">
                <div className="flex items-center justify-between">
                  <h3 className="font-bold text-[#251B3A] text-base">Family Wall</h3>
                  <span className="text-xs text-[#8B5CF6] font-semibold tabular-nums">
                    {myFamily.announcements.length} posts
                  </span>
                </div>
                <form
                  onSubmit={(e) => {
                    e.preventDefault();
                    if (!wallInput.trim()) return;
                    onPostAnnouncement(myFamily.id, wallInput);
                    setWallInput('');
                  }}
                  className="flex gap-2"
                >
                  <input
                    type="text"
                    value={wallInput}
                    onChange={(e) => setWallInput(e.target.value)}
                    placeholder="Share an update with family..."
                    className="flex-1 min-h-[44px] px-3.5 rounded-xl border border-[#8B5CF6]/30 bg-white text-sm"
                  />
                  <button
                    type="submit"
                    aria-label="Post announcement"
                    className="min-h-[44px] min-w-[44px] px-4 rounded-xl bg-[#8B5CF6] text-white flex items-center justify-center cursor-pointer"
                  >
                    <Send className="w-4 h-4" />
                  </button>
                </form>
                <div className="space-y-2">
                  {myFamily.announcements.map((note, idx) => (
                    <div key={idx} className="rounded-xl bg-[#F5F0FF] px-3.5 py-2.5 text-sm text-[#251B3A]">
                      {note}
                    </div>
                  ))}
                </div>
              </section>

              <section className="space-y-2.5">
                <div className="flex items-center justify-between">
                  <h3 className="font-bold text-[#251B3A] text-base">Members</h3>
                  <span className="text-xs text-[#8B8298] tabular-nums">{myFamily.members.length} people</span>
                </div>
                <div className="space-y-2">
                  {myFamily.members.map((member) => (
                    <div
                      key={member.name}
                      className="rounded-2xl bg-white border border-[#F5F0FF] p-3 flex items-center gap-3"
                    >
                      <AvatarBadge name={member.name} size="sm" />
                      <div className="flex-1 min-w-0">
                        <div className="font-bold text-sm text-[#251B3A] truncate">{member.name}</div>
                        <div className="text-xs text-[#8B8298] tabular-nums">
                          {member.role} · {member.points} pts
                        </div>
                      </div>
                      {member.name !== 'You' && (
                        <button
                          type="button"
                          onClick={() => onChatMember(member.name)}
                          className="min-h-[44px] px-3.5 rounded-xl text-xs font-bold text-[#8B5CF6] hover:bg-[#F5F0FF] cursor-pointer"
                        >
                          Chat
                        </button>
                      )}
                    </div>
                  ))}
                </div>
              </section>

              <button
                type="button"
                onClick={() => {
                  onLeaveFamily(myFamily.id);
                  setActiveTab('explore');
                }}
                className="w-full min-h-[44px] rounded-full border border-[#D9386A]/40 text-[#D9386A] font-bold text-sm cursor-pointer"
              >
                Leave Family
              </button>
            </div>
          ) : (
            <div className="rounded-2xl bg-[#F5F0FF] p-6 text-center space-y-3">
              <div className="text-4xl">🏠</div>
              <div className="font-bold text-[#251B3A]">You haven't joined a Family yet</div>
              <p className="text-xs text-[#8B8298]">Join an existing community or create your own crew.</p>
              <div className="flex items-center justify-center gap-2 pt-1">
                <button
                  type="button"
                  onClick={() => setActiveTab('explore')}
                  className="min-h-[44px] px-4 rounded-xl bg-white text-[#8B5CF6] font-bold text-xs cursor-pointer"
                >
                  Explore
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setEditingFamilyId(null);
                    setActiveTab('explore');
                    setShowLogoBuilder(true);
                  }}
                  className="min-h-[44px] px-4 rounded-xl bg-[#8B5CF6] text-white font-bold text-xs cursor-pointer"
                >
                  Create Family
                </button>
              </div>
            </div>
          )
        ) : (
          <div className="space-y-4">
            {showLogoBuilder && editingFamilyId === null ? (
              renderLogoBuilder()
            ) : (
              <button
                type="button"
                onClick={() => {
                  setEditingFamilyId(null);
                  setShowLogoBuilder(true);
                }}
                className="w-full min-h-[48px] rounded-full bg-[#8B5CF6] text-white font-bold text-sm flex items-center justify-center gap-2 cursor-pointer"
              >
                <Plus className="w-5 h-5" />
                <span>Create a Family</span>
              </button>
            )}

            <div className="space-y-3">
              {families.map((family) => {
                const isMine = family.id === myFamilyId;
                return (
                  <div key={family.id} className="rounded-2xl bg-white border border-[#F5F0FF] p-4 space-y-2">
                    <div className="flex items-center gap-3">
                      <FamilyLogoBadge
                        emblem={family.emblem}
                        color={family.color}
                        accentColor={family.accentColor}
                        shape={family.logoShape}
                        logoImage={family.logoImage}
                        size="md"
                      />
                      <div className="flex-1 min-w-0">
                        <div className="font-extrabold text-[#251B3A] text-base truncate">{family.name}</div>
                        <div className="text-xs text-[#8B8298] tabular-nums">
                          Level {family.level} · {family.members.length} members · {family.points} pts
                        </div>
                      </div>
                      <button
                        type="button"
                        onClick={() => {
                          onJoinFamily(family.id);
                          setActiveTab('mine');
                        }}
                        className={`min-h-[44px] px-4 rounded-full font-bold text-xs cursor-pointer ${
                          isMine ? 'bg-[#F5F0FF] text-[#8B5CF6]' : 'bg-[#8B5CF6] text-white'
                        }`}
                      >
                        {isMine ? 'Joined' : 'Join'}
                      </button>
                    </div>
                    <p className="text-xs text-[#8B8298]">{family.motto}</p>
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

function HumanBattleView() {
  const [selectedBet, setSelectedBet] = useState(100);
  const [seconds, setSeconds] = useState(21);
  const [joined, setJoined] = useState(false);

  useEffect(() => {
    const id = window.setInterval(() => setSeconds((s) => (s <= 1 ? 21 : s - 1)), 1000);
    return () => window.clearInterval(id);
  }, []);

  return (
    <div className="min-h-full bg-[#101A3B] text-white pb-6">
      <div className="flex items-center justify-between px-4 py-3">
        <div>
          <h1 className="text-[#FFD77A] font-extrabold text-[19px]">HUMAN BATTLE</h1>
          <p className="text-white/75 text-[11px]">Real people · Live competition</p>
        </div>
        <MoreHorizontal className="w-6 h-6 text-white" />
      </div>
      <div className="px-4">
        <div className="rounded-2xl bg-[#243A70] py-3.5 flex items-center justify-center gap-2">
          <Timer className="w-5 h-5 text-[#FFD77A]" />
          <span className="font-bold text-sm tabular-nums">ROUND STARTS IN 00:{String(seconds).padStart(2, '0')}</span>
        </div>
      </div>
      <h2 className="font-bold text-sm px-4 pt-3.5 pb-2">Choose a person to support</h2>
      <div className="px-3 grid grid-cols-2 md:grid-cols-4 gap-2.5">
        {BATTLE_PARTICIPANTS.map((p) => (
          <button
            key={p.id}
            type="button"
            onClick={() => setJoined(true)}
            className="rounded-[18px] bg-[#1C2D59] p-2 flex flex-col items-center cursor-pointer"
          >
            <div
              className="w-full h-[104px] rounded-[14px] relative flex items-end justify-center overflow-hidden"
              style={{ background: `linear-gradient(160deg, ${p.bgStart}, ${p.bgEnd})` }}
            >
              <svg viewBox="0 0 120 110" className="w-24 h-24">
                <path d="M24 110 C24 84 40 76 60 76 C80 76 96 84 96 110 Z" fill={p.shirtColor} />
                <circle cx="60" cy="42" r="24" fill={p.hairColor} />
                <circle cx="60" cy="47" r="20" fill={p.skinColor} />
              </svg>
              <span className="absolute bottom-1 rounded-[10px] bg-[#0B1535]/70 px-2 py-0.5 text-[10px]">
                {p.supporters} supporters
              </span>
            </div>
            <span className="font-bold text-sm mt-1.5">{p.name}</span>
            <span className="text-[10px] text-[#BFCBEB]">Live participant</span>
          </button>
        ))}
      </div>
      <h2 className="font-bold text-sm px-4 pt-4 pb-2">Support points</h2>
      <div className="px-2.5 grid grid-cols-5 gap-1.5">
        {[1, 10, 100, 1000, 10000].map((v) => (
          <button
            key={v}
            type="button"
            onClick={() => setSelectedBet(v)}
            className={`min-h-[44px] rounded-xl font-bold text-xs cursor-pointer ${
              selectedBet === v ? 'bg-[#FFC94A] text-[#33234C]' : 'bg-[#263D79] text-white'
            }`}
          >
            {v}
          </button>
        ))}
      </div>
      <div className="px-3 pt-3 grid grid-cols-2 gap-2.5 max-w-xl">
        <button
          type="button"
          onClick={() => setJoined(false)}
          className="min-h-[46px] rounded-full border border-white/40 text-white font-semibold text-sm cursor-pointer"
        >
          Back
        </button>
        <button
          type="button"
          onClick={() => setJoined(true)}
          className="min-h-[46px] rounded-full bg-[#FFC94A] text-[#33234C] font-bold text-sm cursor-pointer"
        >
          {joined ? 'Joined ✓' : 'Support'}
        </button>
      </div>
      <p className="text-[#BFCBEB] text-[10px] text-center px-4 pt-3">
        Demo screen: people, points and live results are sample data.
      </p>
    </div>
  );
}

function KhmerGiftShopView({
  diamonds,
  onSpend,
}: {
  diamonds: number;
  onSpend: (gift: KhmerGift, recipientName: string) => void;
}) {
  const [selected, setSelected] = useState<KhmerGift>(KHMER_GIFTS[0]);
  const [recipient, setRecipient] = useState('Mina');
  const [showModal, setShowModal] = useState(false);
  const [status, setStatus] = useState('ជ្រើសរើសនំខ្មែរដើម្បីផ្ញើជាកាដូ');

  return (
    <div className="min-h-full bg-[#171421] text-white pb-6 relative">
      <div className="flex items-center justify-between px-[18px] py-3.5">
        <div>
          <h1 className="text-[#FFD68A] font-extrabold text-[17px]">KHMER TRADITIONAL GIFTS 🇰🇭</h1>
          <p className="text-white/75 text-xs">ផ្ញើនំខ្មែរជូនមិត្តភក្តិ</p>
        </div>
        <span className="text-[#70D9FF] font-bold tabular-nums">💎 {diamonds}</span>
      </div>
      <div className="px-4 mb-3">
        <img
          src="/images/khmer_food_gifts.png"
          alt="Khmer traditional food gifts"
          className="w-full h-[180px] rounded-[18px] object-cover"
        />
      </div>
      <div className="px-3.5">
        <div className="rounded-2xl bg-[#302743] p-3 flex items-center gap-2 flex-wrap">
          <span className="text-sm">ផ្ញើជូន:</span>
          {['Mina', 'Dara', 'Sophy'].map((name) => (
            <button
              key={name}
              type="button"
              onClick={() => setRecipient(name)}
              className={`px-3 py-1.5 rounded-[18px] text-xs font-semibold cursor-pointer ${
                recipient === name ? 'bg-[#FFD16B] text-[#24152F]' : 'bg-[#514364] text-white'
              }`}
            >
              {name}
            </button>
          ))}
        </div>
      </div>
      <h2 className="font-bold text-sm px-4 pt-4 pb-2">ជ្រើសរើសកាដូ</h2>
      <div className="px-3 grid grid-cols-2 md:grid-cols-4 gap-2.5">
        {KHMER_GIFTS.map((g) => {
          const active = selected.id === g.id;
          return (
            <button
              key={g.id}
              type="button"
              onClick={() => setSelected(g)}
              className={`rounded-[18px] p-3 flex flex-col items-center cursor-pointer ${
                active ? 'bg-[#594078]' : 'bg-[#292337]'
              }`}
            >
              <div
                className="w-full h-[94px] rounded-[14px] flex items-center justify-center text-[50px]"
                style={{ background: 'linear-gradient(135deg, #61467D 0%, #302A48 100%)' }}
              >
                {g.emoji}
              </div>
              <span className="font-bold text-sm mt-2">{g.name}</span>
              <span className="text-[#C8BDD7] text-[10px]">{g.note}</span>
              <span className="text-[#71D9FF] font-bold text-sm mt-1">💎 {g.price}</span>
              {active && <span className="text-[#FFD16B] text-[11px]">✓ បានជ្រើសរើស</span>}
            </button>
          );
        })}
      </div>
      <p className="text-[#FFD68A] text-xs text-center py-2">{status}</p>
      <div className="px-3.5 flex items-center justify-between">
        <div>
          <div className="font-bold text-sm">{selected.name}</div>
          <div className="text-[#70D9FF] text-xs">តម្លៃ 💎 {selected.price}</div>
        </div>
        <button
          type="button"
          onClick={() => {
            if (diamonds >= selected.price) {
              onSpend(selected, recipient);
              setStatus(`អ្នកបានផ្ញើ ${selected.name} ទៅ ${recipient} ហើយ!`);
              setShowModal(true);
            } else {
              setStatus('ពេជ្រមិនគ្រប់គ្រាន់ទេ!');
            }
          }}
          className="min-h-[44px] px-5 rounded-3xl bg-[#9B70F5] text-white font-bold text-sm flex items-center gap-1.5 cursor-pointer"
        >
          <Send className="w-4 h-4" />
          <span>Send</span>
        </button>
      </div>
      {showModal && (
        <div
          onClick={() => setShowModal(false)}
          className="fixed inset-0 z-50 bg-[#0A0712]/70 flex items-center justify-center p-6"
        >
          <div
            onClick={(e) => e.stopPropagation()}
            className="w-full max-w-sm rounded-[28px] p-6 flex flex-col items-center text-center"
            style={{ background: 'linear-gradient(135deg, #68468E 0%, #342447 100%)' }}
          >
            <div className="text-[28px]">🎉 💖 ✨</div>
            <h3 className="text-[#FFD77A] text-[22px] font-extrabold">ទទួលបានកាដូ!</h3>
            <div className="text-[76px] leading-none my-2">{selected.emoji}</div>
            <div className="text-xl font-bold">{selected.name}</div>
            <p className="text-white/85 text-sm">ពីអ្នក ទៅកាន់ {recipient}</p>
            <button
              type="button"
              onClick={() => setShowModal(false)}
              className="mt-4 min-h-[44px] px-6 rounded-full bg-[#FFD16B] text-[#352344] font-bold text-sm cursor-pointer"
            >
              អស្ចារ្យ!
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

function FriendsView({
  peopleDirectory,
  friends,
  friendRequests,
  onBack,
  onAddFriend,
  onAcceptRequest,
  onDeclineRequest,
  onRemoveFriend,
  onChat,
}: {
  peopleDirectory: FriendPerson[];
  friends: string[];
  friendRequests: string[];
  onBack: () => void;
  onAddFriend: (rawInput: string) => string | null;
  onAcceptRequest: (name: string) => void;
  onDeclineRequest: (name: string) => void;
  onRemoveFriend: (name: string) => void;
  onChat: (name: string) => void;
}) {
  const [searchQuery, setSearchQuery] = useState('');
  const [customFriendInput, setCustomFriendInput] = useState('');
  const [feedback, setFeedback] = useState<string | null>(null);

  const normalizedQuery = searchQuery.trim().toLowerCase();
  const findPerson = (name: string): FriendPerson =>
    peopleDirectory.find((p) => p.name.toLowerCase() === name.toLowerCase()) ?? {
      name,
      handle: `@${name.toLowerCase().replace(/[^a-z0-9]+/g, '.')}`,
      vibe: 'MY HAPPY PLACE member',
      isOnline: true,
      mutualCount: 0,
    };

  const matchesSearch = (person: FriendPerson) => {
    if (!normalizedQuery) return true;
    return (
      person.name.toLowerCase().includes(normalizedQuery) ||
      person.handle.toLowerCase().includes(normalizedQuery) ||
      person.vibe.toLowerCase().includes(normalizedQuery)
    );
  };

  const requestPeople = friendRequests.map(findPerson).filter(matchesSearch);
  const friendPeople = friends.map(findPerson).filter(matchesSearch);
  const discoverPeople = peopleDirectory.filter(
    (p) => !friends.includes(p.name) && !friendRequests.includes(p.name) && matchesSearch(p),
  );

  const handleQuickAddSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const added = onAddFriend(customFriendInput);
    if (!added) return;
    setCustomFriendInput('');
    setFeedback(`Added ${added} to your friends list!`);
  };

  return (
    <div className="min-h-screen bg-[#FFFBFF] pb-12">
      <div className="sticky top-0 z-20 bg-white/95 backdrop-blur border-b border-[#F5F0FF] px-4 py-3 flex items-center gap-3">
        <button
          type="button"
          onClick={onBack}
          aria-label="Back"
          className="min-h-[44px] min-w-[44px] rounded-full flex items-center justify-center text-[#251B3A] hover:bg-[#F5F0FF] cursor-pointer"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        <div className="flex-1 min-w-0">
          <h1 className="text-lg font-extrabold text-[#251B3A]">Add Friends</h1>
          <p className="text-xs text-[#8B8298] tabular-nums">
            {friends.length} friends · {friendRequests.length} requests
          </p>
        </div>
      </div>

      <div className="max-w-3xl mx-auto px-5 pt-5 space-y-6">
        <form
          onSubmit={handleQuickAddSubmit}
          className="rounded-[22px] bg-[#F5F0FF] p-4 space-y-3"
        >
          <div>
            <h2 className="font-extrabold text-[#251B3A] text-sm">Add by name or @username</h2>
            <p className="text-xs text-[#8B8298] mt-0.5">
              Connect directly with friends on MY HAPPY PLACE
            </p>
          </div>
          <div className="flex flex-col sm:flex-row gap-2">
            <input
              type="text"
              value={customFriendInput}
              onChange={(e) => setCustomFriendInput(e.target.value)}
              placeholder="Enter friend's name or @handle (e.g. Koko)"
              className="flex-1 min-h-[44px] px-3.5 rounded-xl border border-[#8B5CF6]/25 bg-white text-sm text-[#251B3A] placeholder:text-[#8B8298] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#8B5CF6]"
            />
            <button
              type="submit"
              className="min-h-[44px] px-5 rounded-xl bg-[#8B5CF6] hover:bg-[#7C4DFF] text-white font-bold text-xs inline-flex items-center justify-center gap-1.5 cursor-pointer whitespace-nowrap transition-colors"
            >
              <UserPlus className="w-4 h-4" />
              <span>Add friend</span>
            </button>
          </div>
          {feedback && (
            <div role="status" className="text-xs font-semibold text-[#5B6EE1] flex items-center gap-1.5">
              <Check className="w-4 h-4 shrink-0" />
              <span>{feedback}</span>
            </div>
          )}
        </form>

        <div className="relative flex items-center">
          <Search className="w-4 h-4 text-[#8B5CF6] absolute left-3.5 pointer-events-none" />
          <input
            type="search"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Filter people by name, @handle, or vibe..."
            className="w-full min-h-[44px] pl-10 pr-10 rounded-2xl bg-[#F5F0FF]/70 border border-[#8B5CF6]/20 text-sm text-[#251B3A] placeholder:text-[#8B8298] focus:bg-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#8B5CF6]"
          />
          {searchQuery.length > 0 && (
            <button
              type="button"
              onClick={() => setSearchQuery('')}
              aria-label="Clear filter"
              className="absolute right-1.5 min-h-[36px] min-w-[36px] rounded-full flex items-center justify-center text-[#8B8298] hover:text-[#251B3A] cursor-pointer"
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>

        {requestPeople.length > 0 && (
          <section className="space-y-2.5">
            <div className="flex items-center justify-between">
              <h2 className="font-bold text-[#251B3A] text-base">Friend Requests</h2>
              <span className="text-xs font-semibold text-[#8B5CF6] tabular-nums">
                {requestPeople.length} pending
              </span>
            </div>
            <div className="space-y-2">
              {requestPeople.map((person) => (
                <div
                  key={person.name}
                  className="rounded-2xl bg-white border border-[#F5F0FF] p-3.5 flex flex-wrap items-center gap-3 shadow-xs"
                >
                  <AvatarBadge name={person.name} size="sm" />
                  <div className="flex-1 min-w-[140px]">
                    <div className="font-bold text-sm text-[#251B3A] truncate">{person.name}</div>
                    <div className="text-xs text-[#8B8298] truncate">
                      {person.handle} · {person.mutualCount} mutual friends
                    </div>
                  </div>
                  <div className="flex items-center gap-2 shrink-0">
                    <button
                      type="button"
                      onClick={() => {
                        onAcceptRequest(person.name);
                        setFeedback(`Accepted ${person.name}'s friend request!`);
                      }}
                      className="min-h-[40px] px-4 rounded-full bg-[#8B5CF6] hover:bg-[#7C4DFF] text-white font-bold text-xs inline-flex items-center gap-1 cursor-pointer transition-colors"
                    >
                      <Check className="w-3.5 h-3.5" />
                      <span>Accept</span>
                    </button>
                    <button
                      type="button"
                      onClick={() => onDeclineRequest(person.name)}
                      className="min-h-[40px] px-3.5 rounded-full bg-[#F5F0FF] hover:bg-[#EAE0FF] text-[#251B3A] font-semibold text-xs cursor-pointer transition-colors"
                    >
                      Decline
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </section>
        )}

        <section className="space-y-2.5">
          <div className="flex items-center justify-between">
            <h2 className="font-bold text-[#251B3A] text-base">People You May Know</h2>
            <span className="text-xs text-[#8B8298] tabular-nums">{discoverPeople.length} suggested</span>
          </div>
          {discoverPeople.length === 0 ? (
            <div className="rounded-2xl bg-[#F5F0FF]/60 p-4 text-xs text-[#8B8298]">
              {normalizedQuery
                ? `No suggested people matched "${searchQuery.trim()}". Try adding them directly above.`
                : 'You have added all suggested people! Add anyone else by name or @handle above.'}
            </div>
          ) : (
            <div className="space-y-2">
              {discoverPeople.map((person) => (
                <div
                  key={person.name}
                  className="rounded-2xl bg-white border border-[#F5F0FF] p-3.5 flex flex-wrap items-center gap-3 shadow-xs"
                >
                  <AvatarBadge name={person.name} size="sm" />
                  <div className="flex-1 min-w-[140px]">
                    <div className="font-bold text-sm text-[#251B3A] truncate">{person.name}</div>
                    <div className="text-xs text-[#8B8298] truncate">
                      {person.handle} · {person.vibe}
                    </div>
                  </div>
                  <div className="flex items-center gap-2 shrink-0">
                    <button
                      type="button"
                      onClick={() => {
                        onAddFriend(person.name);
                        setFeedback(`Added ${person.name} to your friends!`);
                      }}
                      className="min-h-[40px] px-4 rounded-full bg-[#8B5CF6] hover:bg-[#7C4DFF] text-white font-bold text-xs inline-flex items-center gap-1.5 cursor-pointer transition-colors"
                    >
                      <UserPlus className="w-3.5 h-3.5" />
                      <span>Add friend</span>
                    </button>
                    <button
                      type="button"
                      onClick={() => onChat(person.name)}
                      aria-label={`Chat with ${person.name}`}
                      className="min-h-[40px] min-w-[40px] rounded-full bg-[#F5F0FF] text-[#8B5CF6] flex items-center justify-center cursor-pointer"
                    >
                      <MessageCircle className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>

        <section className="space-y-2.5">
          <div className="flex items-center justify-between">
            <h2 className="font-bold text-[#251B3A] text-base">My Friends</h2>
            <span className="text-xs font-semibold text-[#8B5CF6] tabular-nums">{friendPeople.length}</span>
          </div>
          {friendPeople.length === 0 ? (
            <div className="rounded-2xl bg-[#F5F0FF]/60 p-4 text-xs text-[#8B8298]">
              No friends match this filter yet.
            </div>
          ) : (
            <div className="space-y-2">
              {friendPeople.map((person) => (
                <div
                  key={person.name}
                  className="rounded-2xl bg-white border border-[#F5F0FF] p-3.5 flex flex-wrap items-center gap-3 shadow-xs"
                >
                  <AvatarBadge name={person.name} size="sm" />
                  <div className="flex-1 min-w-[140px]">
                    <div className="font-bold text-sm text-[#251B3A] truncate">{person.name}</div>
                    <div className="text-xs text-[#8B8298] truncate">
                      {person.handle} · {person.isOnline ? 'Online' : 'Offline'} · {person.vibe}
                    </div>
                  </div>
                  <div className="flex items-center gap-2 shrink-0">
                    <button
                      type="button"
                      onClick={() => onChat(person.name)}
                      className="min-h-[40px] px-4 rounded-full bg-[#F5F0FF] hover:bg-[#EAE0FF] text-[#8B5CF6] font-bold text-xs inline-flex items-center gap-1.5 cursor-pointer transition-colors"
                    >
                      <MessageCircle className="w-3.5 h-3.5" />
                      <span>Chat</span>
                    </button>
                    <button
                      type="button"
                      onClick={() => {
                        onRemoveFriend(person.name);
                        setFeedback(`Removed ${person.name} from friends.`);
                      }}
                      aria-label={`Remove ${person.name}`}
                      className="min-h-[40px] min-w-[40px] rounded-full bg-[#F5F0FF]/70 hover:bg-[#FFE8EF] text-[#8B8298] hover:text-[#D93B6B] flex items-center justify-center cursor-pointer transition-colors"
                    >
                      <UserMinus className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>
      </div>
    </div>
  );
}
