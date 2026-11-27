import { useEffect, useRef, useState } from 'react';
import {
  Armchair,
  ArrowLeft,
  ArrowUpDown,
  BarChart2,
  Bell,
  Calendar,
  Camera,
  Check,
  CheckCircle2,
  ChevronDown,
  ChevronRight,
  Coffee,
  Copy,
  Crown,
  Dice5,
  Disc3,
  Eye,
  EyeOff,
  Feather,
  Flag,
  Flame,
  Gamepad2,
  Gift,
  Globe,
  Headphones,
  Heart,
  Home,
  ImagePlus,
  LayoutGrid,
  ListMusic,
  Lock,
  LogOut,
  Menu,
  MessageCircle,
  Mic,
  MicOff,
  Moon,
  MoreHorizontal,
  MoreVertical,
  Music,
  Palette,
  Pause,
  Pencil,
  Phone,
  Play,
  Plus,
  Save,
  Search,
  Send,
  Settings,
  Share2,
  Shield,
  ShoppingBag,
  SkipBack,
  SkipForward,
  SlidersHorizontal,
  Smile,
  Sparkles,
  SquarePen,
  Star,
  Timer,
  Trophy,
  Unlock,
  Upload,
  User,
  UserCheck,
  UserMinus,
  UserPlus,
  Users,
  Volume2,
  VolumeX,
  Wand2,
  X,
  Zap,
} from 'lucide-react';

type TabId = 'home' | 'party' | 'feed' | 'chat' | 'me' | 'game' | 'gifts';

interface ChatMessage {
  id: number;
  text: string;
  isFromMe: boolean;
  timestamp: number;
}

interface OnlineStatusPerson {
  id: string;
  name: string;
  gender: 'male' | 'female';
  age: number;
  status: string;
  region: 'domestic' | 'worldwide';
  avatarBg: string;
}

interface MomentTopic {
  id: string;
  label: string;
  gradient: string;
  emoji: string;
}

interface MomentComment {
  id: number;
  author: string;
  text: string;
}

interface MomentPost {
  id: string;
  author: string;
  gender: 'male' | 'female';
  age: number;
  timeAgo: string;
  caption: string;
  topicId?: string;
  imageBg?: string;
  imageEmoji?: string;
  likes: number;
  comments: MomentComment[];
  shares: number;
  tab: 'following' | 'foryou' | 'latest';
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
  partyMode?: 'Free Mic' | 'Music Lounge' | 'Karaoke' | 'Truth or Dare';
  welcomeMessage?: string;
  isLocked?: boolean;
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

const MOBILE_TABS: { id: TabId; label: string; icon: typeof Home }[] = [
  { id: 'home', label: 'Home', icon: Home },
  { id: 'party', label: 'Party', icon: Mic },
  { id: 'feed', label: 'Moments', icon: Globe },
  { id: 'chat', label: 'Chat', icon: MessageCircle },
  { id: 'me', label: 'Me', icon: User },
];

const SIDEBAR_TABS: { id: TabId; label: string; icon: typeof Home }[] = [
  ...MOBILE_TABS,
  { id: 'game', label: 'Battle', icon: Trophy },
  { id: 'gifts', label: 'Shop', icon: ShoppingBag },
];

const INITIAL_ONLINE_STATUSES: OnlineStatusPerson[] = [
  { id: 'st-1', name: 'Mina', gender: 'female', age: 21, status: 'ត្រឹមជាក្មេងកាតឿម្នាក់ហេ😜😉', region: 'domestic', avatarBg: 'linear-gradient(135deg, #2DD4BF, #0EA5E9)' },
  { id: 'st-2', name: 'VITA', gender: 'male', age: 21, status: 'ចង់មានសង្សារដែរ😢🇰🇭', region: 'domestic', avatarBg: 'linear-gradient(135deg, #3B82F6, #6366F1)' },
  { id: 'st-3', name: 'លីដា', gender: 'female', age: 20, status: 'like chat', region: 'domestic', avatarBg: 'linear-gradient(135deg, #34D399, #10B981)' },
  { id: 'st-4', name: 'ah thy CG', gender: 'male', age: 18, status: '💦 Acoustic night?', region: 'domestic', avatarBg: 'linear-gradient(135deg, #60A5FA, #8B5CF6)' },
  { id: 'st-5', name: 'TONG CG', gender: 'male', age: 18, status: 'ចង់ស្តាប់ខ្ញុំច្រៀងអត់ឆាតមក😊💗', region: 'worldwide', avatarBg: 'linear-gradient(135deg, #64748B, #334155)' },
  { id: 'st-6', name: 'Sophy', gender: 'female', age: 22, status: 'Hi my name is Sophy ✨', region: 'worldwide', avatarBg: 'linear-gradient(135deg, #EC4899, #8B5CF6)' },
  { id: 'st-7', name: 'Dara', gender: 'male', age: 24, status: 'Hot mic tonight!', region: 'domestic', avatarBg: 'linear-gradient(135deg, #EF4444, #F97316)' },
];

const MOMENT_TOPICS: MomentTopic[] = [
  { id: 't-1', label: 'ប្រទីបខ្មែរបែបទំនើប', gradient: 'linear-gradient(135deg, #7C3AED, #EC4899)', emoji: '🏮' },
  { id: 't-2', label: 'អុំទូកឆ្នាំនេះ', gradient: 'linear-gradient(135deg, #0EA5E9, #2563EB)', emoji: '🚣' },
  { id: 't-3', label: 'ថ្ងៃនេះញ៉ាំអី', gradient: 'linear-gradient(135deg, #F59E0B, #EA580C)', emoji: '🍜' },
  { id: 't-4', label: 'ពាក្យក្នុងចិត្ត', gradient: 'linear-gradient(135deg, #F472B6, #FB7185)', emoji: '💌' },
  { id: 't-5', label: 'សាកទាយតារានិករ', gradient: 'linear-gradient(135deg, #6366F1, #4338CA)', emoji: '🔮' },
];

const INITIAL_MOMENT_POSTS: MomentPost[] = [
  {
    id: 'm-1',
    author: 'ស្រីនាង',
    gender: 'female',
    age: 26,
    timeAgo: 'Active 2 hours ago',
    caption: 'ល្ងាចនេះរ៉ូម៉ែនទិក ជាមួយទេសភាពមាត់ទន្លេ 🌸✨',
    topicId: 't-1',
    imageBg: 'linear-gradient(135deg, #581C87 0%, #BE185D 55%, #1E1B4B 100%)',
    imageEmoji: '💃✨',
    likes: 124,
    comments: [{ id: 1, author: 'Mina', text: 'So pretty tonight!' }],
    shares: 8,
    tab: 'foryou',
  },
  {
    id: 'm-2',
    author: 'Lii',
    gender: 'male',
    age: 18,
    timeAgo: 'Active now',
    caption: 'គ្មានអ្វីស្រួលជាង.... ទេ? សូមបំពេញចន្លោះ? ។🤭',
    topicId: 't-4',
    likes: 18,
    comments: [{ id: 2, author: 'Dara', text: 'Sleeping early 😂' }],
    shares: 3,
    tab: 'foryou',
  },
  {
    id: 'm-3',
    author: 'Ah JM🥹💦',
    gender: 'female',
    age: 23,
    timeAgo: 'Active 33 mins ago',
    caption: 'បើប្រទេសគ្មានច្បាប់ធ្វើអីមុនគេ.........?',
    topicId: 't-5',
    likes: 6,
    comments: [{ id: 3, author: 'Sophy', text: 'Sing all night in Party Chat!' }],
    shares: 1,
    tab: 'latest',
  },
  {
    id: 'm-4',
    author: 'Mina',
    gender: 'female',
    age: 21,
    timeAgo: 'Active 12 mins ago',
    caption: 'កាហ្វេមួយកែវ និងបទចម្រៀងស្ងប់ស្ងាត់ ☕🎶',
    topicId: 't-3',
    imageBg: 'linear-gradient(135deg, #1E293B 0%, #4C1D95 100%)',
    imageEmoji: '☕🎶',
    likes: 42,
    comments: [],
    shares: 5,
    tab: 'following',
  },
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
  const [displayName, setDisplayName] = useState('និងហើយៗចូលចិត្ត');
  const [bio, setBio] = useState('Here to make happy memories ✨');
  const [userStatusText, setUserStatusText] = useState('');
  const [userOccupation, setUserOccupation] = useState('Merchant/trader');
  const [userInterests, setUserInterests] = useState<string[]>(['Merchant/Trader', 'Coffee']);
  const [profilePhoto, setProfilePhoto] = useState<string | null>(null);
  const [coverPhoto, setCoverPhoto] = useState<string | null>(null);
  const [onlineStatuses, setOnlineStatuses] = useState<OnlineStatusPerson[]>(INITIAL_ONLINE_STATUSES);
  const [momentPosts, setMomentPosts] = useState<MomentPost[]>(INITIAL_MOMENT_POSTS);
  const [likedPostIds, setLikedPostIds] = useState<string[]>([]);
  const [followingAuthors, setFollowingAuthors] = useState<string[]>(['Mina']);
  const [claimedFreeDiamonds, setClaimedFreeDiamonds] = useState(false);
  const [showSoulMatchModal, setShowSoulMatchModal] = useState(false);
  const [showStatusModal, setShowStatusModal] = useState(false);
  const [showComposeMomentModal, setShowComposeMomentModal] = useState(false);
  const [statusDraft, setStatusDraft] = useState('');
  const [momentDraft, setMomentDraft] = useState('');
  const [momentTopicDraft, setMomentTopicDraft] = useState<string>(MOMENT_TOPICS[0].id);
  const [soulMatchedPerson, setSoulMatchedPerson] = useState<string | null>(null);

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

  const bgClass =
    selectedTab === 'game'
      ? 'bg-[#101A3B]'
      : selectedTab === 'gifts'
        ? 'bg-[#171421]'
        : 'bg-[#16151B]';

  const unreadChatCount = 2;

  return (
    <div className={`min-h-screen flex flex-col md:flex-row text-white ${bgClass}`}>
      <aside className="hidden md:flex md:flex-col md:w-56 lg:w-64 shrink-0 border-r border-white/10 bg-[#121117] p-4 sticky top-0 h-screen text-white">
        <div className="px-3 py-3 mb-4">
          <div className="text-[#A78BFA] text-[11px] font-bold tracking-[2px]">MY HAPPY PLACE</div>
          <div className="text-xs text-white/65 mt-0.5 truncate">{displayName}</div>
        </div>
        <nav className="flex-1 space-y-1">
          {SIDEBAR_TABS.map((tab) => {
            const Icon = tab.icon;
            const active = selectedTab === tab.id;
            return (
              <button
                key={tab.id}
                type="button"
                onClick={() => setSelectedTab(tab.id)}
                className={`w-full min-h-[44px] px-3.5 py-2.5 rounded-xl flex items-center justify-between gap-3 text-sm font-semibold transition-colors cursor-pointer ${
                  active
                    ? 'bg-[#8B5CF6] text-white'
                    : 'text-white/70 hover:bg-white/10 hover:text-white'
                }`}
              >
                <span className="flex items-center gap-3">
                  <Icon className="w-5 h-5 shrink-0" />
                  <span>{tab.label}</span>
                </span>
                {tab.id === 'chat' && unreadChatCount > 0 && (
                  <span className="px-1.5 py-0.5 rounded-full bg-[#FF4D6D] text-white text-[10px] font-bold tabular-nums">
                    {unreadChatCount}
                  </span>
                )}
              </button>
            );
          })}
          <button
            type="button"
            onClick={() => setShowFamily(true)}
            className="w-full min-h-[44px] px-3.5 py-2.5 rounded-xl flex items-center gap-3 text-sm font-semibold text-white/70 hover:bg-white/10 hover:text-white transition-colors cursor-pointer"
          >
            <Users className="w-5 h-5 shrink-0" />
            <span>Family</span>
          </button>
          <button
            type="button"
            onClick={() => setShowFriends(true)}
            className="w-full min-h-[44px] px-3.5 py-2.5 rounded-xl flex items-center justify-between gap-3 text-sm font-semibold text-white/70 hover:bg-white/10 hover:text-white transition-colors cursor-pointer"
          >
            <span className="flex items-center gap-3">
              <UserPlus className="w-5 h-5 shrink-0" />
              <span>Friends</span>
            </span>
            {friendRequests.length > 0 && (
              <span className="text-xs font-bold text-[#A78BFA] tabular-nums">{friendRequests.length}</span>
            )}
          </button>
        </nav>
        <div className="mt-auto px-3 py-2.5 rounded-xl bg-white/5 text-[#70D9FF] text-xs font-bold tabular-nums">
          💎 {diamonds} Diamonds
        </div>
      </aside>

      <main className="flex-1 min-w-0 pb-20 md:pb-0">
        <div className="max-w-5xl mx-auto min-h-full">
          {selectedTab === 'home' && (
            <HomeView
              statuses={onlineStatuses}
              rooms={rooms}
              friends={friends}
              userStatusText={userStatusText}
              onChat={setOpenedChat}
              onTab={setSelectedTab}
              onOpenSoulMatch={() => setShowSoulMatchModal(true)}
              onJoinVoiceGame={() => {
                const target = rooms[0];
                if (target) setActiveVoiceRoom(target);
              }}
              onOpenFriends={() => setShowFriends(true)}
              onAddFriend={handleAddFriend}
              onOpenStatusModal={() => {
                setStatusDraft(userStatusText);
                setShowStatusModal(true);
              }}
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
              onOpenFamily={() => setShowFamily(true)}
              onOpenShop={() => setSelectedTab('gifts')}
              onCreateRoom={({ title, mood, backgroundId, partyMode, welcomeMessage, isLocked }) => {
                const moodMeta = getRoomMood(mood);
                const created: VoiceRoom = {
                  id: `room-${Date.now()}`,
                  title,
                  detail: `1 person · ${partyMode}`,
                  mood,
                  color: moodMeta.color,
                  listeners: 1,
                  speakers: ['You'],
                  owner: 'You',
                  backgroundId,
                  partyMode,
                  welcomeMessage,
                  isLocked,
                  createdAt: Date.now(),
                };
                setRooms((prev) => [created, ...prev]);
                setActiveVoiceRoom(created);
              }}
            />
          )}
          {selectedTab === 'feed' && (
            <MomentsFeedView
              posts={momentPosts}
              likedPostIds={likedPostIds}
              followingAuthors={followingAuthors}
              onToggleLike={(postId) => {
                const isLiked = likedPostIds.includes(postId);
                setLikedPostIds((prev) => (isLiked ? prev.filter((id) => id !== postId) : [...prev, postId]));
                setMomentPosts((prev) =>
                  prev.map((p) => (p.id === postId ? { ...p, likes: Math.max(0, p.likes + (isLiked ? -1 : 1)) } : p)),
                );
              }}
              onAddComment={(postId, text) => {
                const clean = text.trim();
                if (!clean) return;
                setMomentPosts((prev) =>
                  prev.map((p) =>
                    p.id === postId
                      ? { ...p, comments: [...p.comments, { id: Date.now(), author: 'You', text: clean }] }
                      : p,
                  ),
                );
              }}
              onSharePost={(postId) => {
                setMomentPosts((prev) => prev.map((p) => (p.id === postId ? { ...p, shares: p.shares + 1 } : p)));
                setReportToast('Shared post with your friends!');
              }}
              onToggleFollow={(author) => {
                setFollowingAuthors((prev) =>
                  prev.includes(author) ? prev.filter((a) => a !== author) : [...prev, author],
                );
              }}
              onSayHi={(author) => setOpenedChat(author)}
              onOpenCompose={() => setShowComposeMomentModal(true)}
            />
          )}
          {selectedTab === 'chat' && (
            <ChatTabView
              friends={friends}
              conversations={conversations}
              claimedFreeDiamonds={claimedFreeDiamonds}
              onClaimFreeDiamonds={() => {
                if (claimedFreeDiamonds) {
                  setReportToast('Daily Free Diamonds already claimed! Visit Shop anytime 💎');
                  return;
                }
                setClaimedFreeDiamonds(true);
                setDiamonds((d) => d + 15);
                setReportToast('You won +15 Free Diamonds 💎!');
              }}
              onOpenFriends={() => setShowFriends(true)}
              onOpenParty={() => {
                if (rooms[0]) setActiveVoiceRoom(rooms[0]);
                else setSelectedTab('party');
              }}
              onOpenGifts={() => setSelectedTab('gifts')}
              onChat={setOpenedChat}
            />
          )}
          {selectedTab === 'me' && (
            <ProfileView
              displayName={displayName}
              bio={bio}
              userStatusText={userStatusText}
              userOccupation={userOccupation}
              userInterests={userInterests}
              profilePhoto={profilePhoto}
              coverPhoto={coverPhoto}
              diamonds={diamonds}
              giftHistory={giftHistory}
              currentFamily={currentFamily}
              friendsCount={friends.length}
              friendRequestCount={friendRequests.length}
              rooms={rooms}
              posts={momentPosts}
              onOpenFriends={() => setShowFriends(true)}
              onOpenFamily={() => setShowFamily(true)}
              onOpenShop={() => setSelectedTab('gifts')}
              onOpenGame={() => setSelectedTab('game')}
              onOpenAddMusic={() => setShowAddMusicModal(true)}
              onOpenStatusModal={() => {
                setStatusDraft(userStatusText);
                setShowStatusModal(true);
              }}
              onOpenCompose={() => setShowComposeMomentModal(true)}
              onJoinMyRoom={() => {
                if (rooms[0]) setActiveVoiceRoom(rooms[0]);
                else setSelectedTab('party');
              }}
              onSave={(next) => {
                setDisplayName(next.displayName);
                setBio(next.bio);
                setUserOccupation(next.occupation);
                setUserInterests(next.interests);
                setProfilePhoto(next.profilePhoto);
                setCoverPhoto(next.coverPhoto);
              }}
            />
          )}
          {selectedTab === 'game' && (
            <div>
              <div className="px-4 pt-3 md:hidden">
                <button
                  type="button"
                  onClick={() => setSelectedTab('me')}
                  className="inline-flex items-center gap-1.5 text-xs font-bold text-white/80 hover:text-white cursor-pointer"
                >
                  <ArrowLeft className="w-4 h-4" />
                  <span>Back to Profile</span>
                </button>
              </div>
              <HumanBattleView />
            </div>
          )}
          {selectedTab === 'gifts' && (
            <div>
              <div className="px-4 pt-3 md:hidden">
                <button
                  type="button"
                  onClick={() => setSelectedTab('party')}
                  className="inline-flex items-center gap-1.5 text-xs font-bold text-white/80 hover:text-white cursor-pointer"
                >
                  <ArrowLeft className="w-4 h-4" />
                  <span>Back to Party</span>
                </button>
              </div>
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
            </div>
          )}
        </div>
      </main>

      <nav className="md:hidden fixed bottom-0 left-0 right-0 z-40 bg-[#1A1921]/98 backdrop-blur-md border-t border-white/10 grid grid-cols-5 h-16 px-2">
        {MOBILE_TABS.map((tab) => {
          const Icon = tab.icon;
          const active = selectedTab === tab.id;
          return (
            <button
              key={tab.id}
              type="button"
              onClick={() => setSelectedTab(tab.id)}
              aria-label={tab.label}
              className={`relative flex flex-col items-center justify-center min-h-[44px] cursor-pointer transition-colors ${
                active ? 'text-white' : 'text-[#8B8797]'
              }`}
            >
              <div className="relative flex items-center justify-center">
                {tab.id === 'me' ? (
                  <div
                    className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold border-2 ${
                      active ? 'border-[#8B5CF6]' : 'border-white/25'
                    }`}
                    style={{ background: 'linear-gradient(135deg, #DC2626, #9333EA)' }}
                  >
                    {displayName.slice(0, 1)}
                  </div>
                ) : (
                  <Icon className={`w-6 h-6 ${active ? 'text-white' : 'text-[#8B8797]'}`} />
                )}
                {tab.id === 'party' && (
                  <span className="absolute -top-0.5 -right-1 w-2 h-2 rounded-full bg-[#FF4D6D]" />
                )}
                {tab.id === 'chat' && unreadChatCount > 0 && (
                  <span className="absolute -top-1.5 -right-2.5 min-w-[17px] h-[17px] px-1 rounded-full bg-[#FF4D6D] text-white text-[10px] font-bold flex items-center justify-center tabular-nums">
                    {unreadChatCount}
                  </span>
                )}
              </div>
              <span className="text-[10px] font-medium mt-0.5 leading-none">{tab.label}</span>
            </button>
          );
        })}
      </nav>

      {showSoulMatchModal && (
        <div
          onClick={() => setShowSoulMatchModal(false)}
          className="fixed inset-0 z-50 bg-[#0A0712]/75 backdrop-blur-sm flex items-center justify-center p-4"
        >
          <div
            onClick={(e) => e.stopPropagation()}
            className="w-full max-w-sm rounded-3xl bg-[#222128] border border-white/10 p-6 text-center space-y-4 shadow-2xl"
          >
            <div className="flex items-center justify-between">
              <span className="text-sm font-extrabold text-white">Soul Game Match ✨</span>
              <button
                type="button"
                onClick={() => setShowSoulMatchModal(false)}
                aria-label="Close Soul Game"
                className="w-8 h-8 rounded-full bg-[#2B2A34] text-white/80 flex items-center justify-center cursor-pointer"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
            <div
              className="mx-auto w-28 h-28 rounded-full flex items-center justify-center shadow-lg"
              style={{ background: 'linear-gradient(135deg, #FF5C8A 0%, #8B5CF6 100%)' }}
            >
              <Heart className="w-12 h-12 text-white fill-white" />
            </div>
            <div>
              <h3 className="text-lg font-extrabold text-white">
                {soulMatchedPerson ? `Matched with ${soulMatchedPerson} 💜` : 'Ready for a 3-min soul chat?'}
              </h3>
              <p className="text-xs text-[#9C97A8] mt-1">
                23,229 friends online right now waiting to vibe.
              </p>
            </div>
            <div className="flex flex-col gap-2">
              <button
                type="button"
                onClick={() =>
                  setSoulMatchedPerson(SUGGESTED_PEOPLE[Math.floor(Math.random() * SUGGESTED_PEOPLE.length)])
                }
                className="w-full min-h-[44px] rounded-full bg-gradient-to-r from-[#FF5C8A] to-[#8B5CF6] text-white font-bold text-sm flex items-center justify-center gap-2 cursor-pointer"
              >
                <Sparkles className="w-4 h-4" />
                <span>{soulMatchedPerson ? 'Find Another Match' : 'Start Soul Match'}</span>
              </button>
              {soulMatchedPerson && (
                <div className="flex gap-2">
                  <button
                    type="button"
                    onClick={() => handleAddFriend(soulMatchedPerson)}
                    className="flex-1 min-h-[42px] rounded-full bg-[#2B2A34] text-white text-xs font-bold cursor-pointer"
                  >
                    {friends.includes(soulMatchedPerson) ? 'Friends ✓' : '+ Add Friend'}
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      setShowSoulMatchModal(false);
                      setOpenedChat(soulMatchedPerson);
                    }}
                    className="flex-1 min-h-[42px] rounded-full bg-[#8B5CF6] text-white text-xs font-bold cursor-pointer"
                  >
                    Say Hi
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {showStatusModal && (
        <div
          onClick={() => setShowStatusModal(false)}
          className="fixed inset-0 z-50 bg-[#0A0712]/75 flex items-center justify-center p-4"
        >
          <form
            onClick={(e) => e.stopPropagation()}
            onSubmit={(e) => {
              e.preventDefault();
              const clean = statusDraft.trim();
              setUserStatusText(clean);
              if (clean) {
                setOnlineStatuses((prev) => [
                  {
                    id: `st-${Date.now()}`,
                    name: displayName,
                    gender: 'male',
                    age: 22,
                    status: clean,
                    region: 'domestic',
                    avatarBg: 'linear-gradient(135deg, #DC2626, #8B5CF6)',
                  },
                  ...prev.filter((p) => p.name !== displayName),
                ]);
                setReportToast('Status updated on Home feed!');
              }
              setShowStatusModal(false);
            }}
            className="w-full max-w-sm rounded-3xl bg-[#222128] border border-white/10 p-5 space-y-3.5 shadow-2xl"
          >
            <div className="flex items-center justify-between">
              <h2 className="text-base font-extrabold text-white">Update Live Status ✨</h2>
              <button
                type="button"
                onClick={() => setShowStatusModal(false)}
                className="w-8 h-8 rounded-full bg-[#2B2A34] text-white/80 flex items-center justify-center cursor-pointer"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
            <input
              type="text"
              value={statusDraft}
              onChange={(e) => setStatusDraft(e.target.value)}
              placeholder="What's on your mind right now?"
              className="w-full min-h-[44px] px-3.5 rounded-xl bg-[#16151B] border border-white/10 text-sm text-white placeholder:text-[#7E798C] focus:outline-none focus:border-[#8B5CF6]"
            />
            <div className="flex flex-wrap gap-1.5">
              {['Listening to music 🎧', 'Looking for friends ✨', 'Coffee time ☕', 'Join my Party! 🎙️'].map((preset) => (
                <button
                  key={preset}
                  type="button"
                  onClick={() => setStatusDraft(preset)}
                  className="px-2.5 py-1 rounded-full bg-[#2B2A34] hover:bg-[#3A3846] text-[11px] text-white/85 cursor-pointer"
                >
                  {preset}
                </button>
              ))}
            </div>
            <button
              type="submit"
              className="w-full min-h-[44px] rounded-full bg-[#8B5CF6] text-white font-bold text-sm cursor-pointer"
            >
              Post Status
            </button>
          </form>
        </div>
      )}

      {showComposeMomentModal && (
        <div
          onClick={() => setShowComposeMomentModal(false)}
          className="fixed inset-0 z-50 bg-[#0A0712]/75 flex items-center justify-center p-4"
        >
          <form
            onClick={(e) => e.stopPropagation()}
            onSubmit={(e) => {
              e.preventDefault();
              const clean = momentDraft.trim();
              if (!clean) return;
              const topic = MOMENT_TOPICS.find((t) => t.id === momentTopicDraft);
              const created: MomentPost = {
                id: `m-${Date.now()}`,
                author: displayName,
                gender: 'male',
                age: 22,
                timeAgo: 'Active now',
                caption: clean,
                topicId: momentTopicDraft,
                imageBg: topic?.gradient,
                imageEmoji: topic?.emoji,
                likes: 1,
                comments: [],
                shares: 0,
                tab: 'latest',
              };
              setMomentPosts((prev) => [created, ...prev]);
              setMomentDraft('');
              setShowComposeMomentModal(false);
              setSelectedTab('feed');
              setReportToast('Published your moment!');
            }}
            className="w-full max-w-md rounded-3xl bg-[#222128] border border-white/10 p-5 space-y-3.5 shadow-2xl"
          >
            <div className="flex items-center justify-between">
              <h2 className="text-base font-extrabold text-white">Post a Moment 🪶</h2>
              <button
                type="button"
                onClick={() => setShowComposeMomentModal(false)}
                className="w-8 h-8 rounded-full bg-[#2B2A34] text-white/80 flex items-center justify-center cursor-pointer"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
            <textarea
              rows={3}
              value={momentDraft}
              onChange={(e) => setMomentDraft(e.target.value)}
              placeholder="Share a story, mood, or festival wish..."
              className="w-full p-3.5 rounded-xl bg-[#16151B] border border-white/10 text-sm text-white placeholder:text-[#7E798C] focus:outline-none focus:border-[#8B5CF6]"
            />
            <div>
              <div className="text-xs font-bold text-white/75 mb-1.5">Tag a #Topic</div>
              <div className="flex flex-wrap gap-1.5">
                {MOMENT_TOPICS.map((t) => (
                  <button
                    key={t.id}
                    type="button"
                    onClick={() => setMomentTopicDraft(t.id)}
                    className={`px-2.5 py-1 rounded-full text-[11px] font-semibold cursor-pointer ${
                      momentTopicDraft === t.id ? 'bg-[#8B5CF6] text-white' : 'bg-[#2B2A34] text-white/80'
                    }`}
                  >
                    #{t.label}
                  </button>
                ))}
              </div>
            </div>
            <button
              type="submit"
              disabled={!momentDraft.trim()}
              className="w-full min-h-[44px] rounded-full bg-[#8B5CF6] disabled:opacity-50 text-white font-bold text-sm cursor-pointer"
            >
              Publish Moment
            </button>
          </form>
        </div>
      )}

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
          className="fixed bottom-20 md:bottom-6 right-4 left-4 md:left-auto md:max-w-md z-50 rounded-2xl bg-[#222128] text-white px-4 py-3 shadow-xl border border-white/15 flex items-center justify-between gap-3 text-xs"
        >
          <div className="flex items-center gap-2 min-w-0">
            <Sparkles className="w-4 h-4 text-[#FF7EB6] shrink-0" />
            <span className="leading-snug">{reportToast}</span>
          </div>
          <button
            type="button"
            onClick={() => setReportToast(null)}
            aria-label="Dismiss notification"
            className="w-8 h-8 rounded-full hover:bg-white/10 flex items-center justify-center shrink-0 cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      )}
    </div>
  );
}

function ChatTabView({
  friends,
  conversations,
  claimedFreeDiamonds,
  onClaimFreeDiamonds,
  onOpenFriends,
  onOpenParty,
  onOpenGifts,
  onChat,
}: {
  friends: string[];
  conversations: Record<string, ChatMessage[]>;
  claimedFreeDiamonds: boolean;
  onClaimFreeDiamonds: () => void;
  onOpenFriends: () => void;
  onOpenParty: () => void;
  onOpenGifts: () => void;
  onChat: (name: string) => void;
}) {
  const richMeta: Record<
    string,
    { gender: 'male' | 'female'; age: number; badge?: 'SOULMATE' | 'OFFICIAL'; isParty?: boolean; unread?: number; timeLabel?: string }
  > = {
    Mina: { gender: 'female', age: 25, badge: 'SOULMATE', unread: 1, timeLabel: '14:26' },
    Dara: { gender: 'male', age: 23, timeLabel: 'Sunday' },
    Sophy: { gender: 'female', age: 24, timeLabel: '12:18' },
    Alex: { gender: 'male', age: 27, badge: 'SOULMATE', timeLabel: 'Yesterday' },
    Nita: { gender: 'female', age: 23, isParty: true, unread: 1, timeLabel: 'Wednesday' },
    Sokha: { gender: 'male', age: 32, isParty: true, timeLabel: 'Monday' },
  };

  return (
    <div className="pb-10">
      <header className="px-4 pt-4 pb-3 flex items-center justify-between gap-3">
        <button
          type="button"
          onClick={onOpenFriends}
          aria-label="Friends directory"
          className="w-10 h-10 rounded-full hover:bg-white/10 flex items-center justify-center text-white/90 cursor-pointer"
        >
          <LayoutGrid className="w-5 h-5" />
        </button>
        <div className="text-center min-w-0">
          <h1 className="text-[19px] font-extrabold text-white leading-tight">Chat</h1>
          <button
            type="button"
            onClick={onOpenFriends}
            className="text-[11px] text-[#9C97A8] inline-flex items-center gap-0.5 cursor-pointer hover:text-white"
          >
            <span>Swipe to meet more friends!</span>
            <ChevronDown className="w-3 h-3" />
          </button>
        </div>
        <button
          type="button"
          onClick={onOpenFriends}
          aria-label="Add friends"
          className="w-10 h-10 rounded-full hover:bg-white/10 flex items-center justify-center text-[#FF7EB6] cursor-pointer"
        >
          <Bell className="w-5 h-5" />
        </button>
      </header>

      <div className="px-4 grid grid-cols-3 gap-2.5">
        <button
          type="button"
          onClick={onOpenFriends}
          className="rounded-2xl bg-[#222128] border border-white/5 p-3 flex flex-col items-center text-center cursor-pointer hover:bg-[#2A2833] transition-colors"
        >
          <div className="relative w-12 h-12 rounded-full bg-gradient-to-br from-[#38BDF8] to-[#0EA5E9] flex items-center justify-center text-white font-extrabold text-lg ring-2 ring-[#38BDF8]/40">
            N
            <span className="absolute -bottom-1 px-1.5 py-0.2 rounded-full bg-[#FF4D8D] text-[9px] font-bold">
              ♀ 23
            </span>
          </div>
          <div className="text-[11px] font-bold text-[#FDE68A] mt-2 truncate w-full">🌾 Nita 🌾</div>
          <div className="text-[10px] text-white/80">Friends Online</div>
          <div className="inline-flex items-center gap-1 text-[10px] text-[#A78BFA] font-bold mt-1.5">
            <BarChart2 className="w-3 h-3" />
            <span>7</span>
          </div>
        </button>

        <button
          type="button"
          onClick={onOpenGifts}
          className="rounded-2xl bg-[#222128] border border-white/5 p-3 flex flex-col items-center text-center cursor-pointer hover:bg-[#2A2833] transition-colors"
        >
          <div className="relative w-12 h-12 rounded-full bg-gradient-to-br from-[#F472B6] to-[#8B5CF6] flex items-center justify-center text-white font-extrabold text-lg ring-2 ring-[#EC4899]/40">
            S
            <span className="absolute -bottom-1 px-1.5 py-0.2 rounded-full bg-[#FF4D8D] text-[9px] font-bold">
              ♀ 23
            </span>
          </div>
          <div className="text-[11px] font-bold text-white/90 mt-2 truncate w-full">Sophy</div>
          <div className="text-[10px] text-white/80">You Sent Gift</div>
          <div className="inline-flex items-center gap-1 text-[10px] text-[#A78BFA] font-bold mt-1.5">
            <BarChart2 className="w-3 h-3" />
            <span>5</span>
          </div>
        </button>

        <button
          type="button"
          onClick={onOpenParty}
          className="rounded-2xl bg-[#222128] border border-white/5 p-3 flex flex-col items-center text-center cursor-pointer hover:bg-[#2A2833] transition-colors"
        >
          <div className="relative w-12 h-12 rounded-full bg-gradient-to-br from-[#F59E0B] to-[#B45309] flex items-center justify-center text-white font-extrabold text-lg ring-2 ring-[#F59E0B]/60">
            K
            <span className="absolute -bottom-1 px-1.5 py-0.2 rounded-full bg-[#FF4D8D] text-[9px] font-bold">
              ♀ 18
            </span>
          </div>
          <div className="text-[11px] font-bold text-white/90 mt-2 truncate w-full">Ker Y-Not</div>
          <div className="text-[10px] text-white/80">Party Followed</div>
          <div className="inline-flex items-center gap-1 text-[10px] text-[#A78BFA] font-bold mt-1.5">
            <BarChart2 className="w-3 h-3" />
            <span>1</span>
          </div>
        </button>
      </div>

      <div className="px-4 mt-3">
        <button
          type="button"
          onClick={onClaimFreeDiamonds}
          className="w-full rounded-2xl bg-[#222128] hover:bg-[#292832] border border-white/5 p-3 flex items-center gap-3 text-left cursor-pointer transition-colors"
        >
          <div className="w-11 h-11 rounded-2xl bg-gradient-to-br from-[#A855F7] to-[#6366F1] flex items-center justify-center text-xl shrink-0">
            💎
          </div>
          <div className="flex-1 min-w-0">
            <div className="text-sm font-bold text-white flex items-center gap-1.5">
              <span>Free Diamonds</span>
              <span>💎</span>
            </div>
            <div className="text-xs text-[#9C97A8] truncate">
              {claimedFreeDiamonds ? 'Claimed +15 diamonds today!' : 'Win random diamonds by watching clips'}
            </div>
          </div>
          {!claimedFreeDiamonds && <span className="w-2.5 h-2.5 rounded-full bg-[#FF4D6D] shrink-0" />}
        </button>
      </div>

      <div className="mt-2 divide-y divide-white/5 max-w-2xl">
        <button
          type="button"
          onClick={onOpenGifts}
          className="w-full px-4 py-3 flex items-center gap-3 text-left hover:bg-[#222128]/70 cursor-pointer"
        >
          <div className="w-12 h-12 rounded-full bg-gradient-to-br from-[#38BDF8] to-[#8B5CF6] flex items-center justify-center text-white text-xl shrink-0">
            💜
          </div>
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-1.5">
              <span className="text-sm font-bold text-white truncate">Happy Place official</span>
              <CheckCircle2 className="w-4 h-4 text-[#FBBF24] fill-[#FBBF24]/20 shrink-0" />
            </div>
            <div className="text-xs text-[#9C97A8] truncate">
              Congratulations on completing the daily party task!
            </div>
          </div>
          <div className="text-[11px] text-[#7E798C] shrink-0">09:58</div>
        </button>

        {friends.map((name) => {
          const preview = conversations[name]?.at(-1)?.text ?? '[Effect: Greetings]';
          const meta = richMeta[name] ?? { gender: 'female', age: 21, timeLabel: 'Online' };
          return (
            <button
              key={name}
              type="button"
              onClick={() => onChat(name)}
              className="w-full px-4 py-3 flex items-center gap-3 text-left hover:bg-[#222128]/70 cursor-pointer transition-colors"
            >
              <div className="relative shrink-0">
                <AvatarBadge name={name} />
                {meta.isParty && (
                  <span className="absolute -top-1 left-0 right-0 text-center text-[8px] font-extrabold px-1 rounded-full bg-[#7C3AED] text-white">
                    • PARTY
                  </span>
                )}
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-1.5 flex-wrap">
                  <span className="text-sm font-bold text-white truncate">{name}</span>
                  <span
                    className={`px-1.5 py-0.5 rounded-full text-[9px] font-bold text-white ${
                      meta.gender === 'male' ? 'bg-[#0EA5E9]' : 'bg-[#FF4D8D]'
                    }`}
                  >
                    {meta.gender === 'male' ? '♂' : '♀'} {meta.age}
                  </span>
                  {meta.badge === 'SOULMATE' && (
                    <span className="text-[10px] font-extrabold italic tracking-wide bg-gradient-to-r from-[#FF7EB6] to-[#A78BFA] bg-clip-text text-transparent">
                      SOULMATE
                    </span>
                  )}
                </div>
                <div className="text-xs text-[#9C97A8] truncate mt-0.5">{preview}</div>
              </div>
              <div className="flex flex-col items-end gap-1 shrink-0">
                <span className="text-[11px] text-[#7E798C] tabular-nums">{meta.timeLabel ?? 'Now'}</span>
                {meta.unread ? (
                  <span className="w-5 h-5 rounded-full bg-[#FF4D6D] text-white text-[10px] font-bold flex items-center justify-center tabular-nums">
                    {meta.unread}
                  </span>
                ) : (
                  <Star className="w-3.5 h-3.5 text-[#7E798C]" />
                )}
              </div>
            </button>
          );
        })}
      </div>
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
    <div className="min-h-screen bg-[#16151B] text-white flex flex-col">
      <header className="bg-[#1C1B22] border-b border-white/10 px-3 py-2.5 flex items-center gap-3 sticky top-0 z-20">
        <button
          type="button"
          onClick={onBack}
          aria-label="Back"
          className="w-11 h-11 rounded-full flex items-center justify-center text-white hover:bg-white/10 cursor-pointer"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        <AvatarBadge name={name} size="sm" />
        <div className="flex-1 min-w-0">
          <div className="font-bold text-white text-[17px] truncate">{name}</div>
          <div className="text-[11px] text-[#38BDF8] font-medium">Online</div>
        </div>
        <div className="relative">
          <button
            type="button"
            onClick={() => setMenuOpen((o) => !o)}
            aria-label="Options"
            className="w-11 h-11 rounded-full flex items-center justify-center text-[#9C97A8] hover:bg-white/10 cursor-pointer"
          >
            <MoreVertical className="w-5 h-5" />
          </button>
          {menuOpen && (
            <div className="absolute right-0 top-12 w-40 rounded-xl bg-[#222128] shadow-xl border border-white/10 py-1 z-30">
              <button
                type="button"
                onClick={() => {
                  onClearMessages();
                  setMenuOpen(false);
                }}
                className="w-full text-left px-4 py-2 text-sm text-white hover:bg-white/10 cursor-pointer"
              >
                Clear messages
              </button>
            </div>
          )}
        </div>
      </header>

      <div className="flex-1 max-w-3xl w-full mx-auto px-4 py-3.5 space-y-2.5 overflow-y-auto">
        <p className="text-center text-xs text-[#9C97A8] pb-1.5">Say hello to {name} 👋</p>
        {messages.map((msg) => (
          <div key={msg.id} className={`flex flex-col ${msg.isFromMe ? 'items-end' : 'items-start'}`}>
            <div
              className={`max-w-[280px] sm:max-w-md px-3.5 py-2.5 text-sm ${
                msg.isFromMe
                  ? 'bg-[#8B5CF6] text-white rounded-[18px] rounded-br-[4px]'
                  : 'bg-[#222128] text-white border border-white/10 rounded-[18px] rounded-bl-[4px]'
              }`}
            >
              {msg.text}
            </div>
            <span className="text-[10px] text-[#7E798C] px-1 pt-0.5 tabular-nums">
              {formatTimestamp(msg.timestamp)}
            </span>
          </div>
        ))}
        <div ref={bottomRef} />
      </div>

      <form onSubmit={handleSubmit} className="bg-[#1C1B22] border-t border-white/10 px-3 py-2.5 sticky bottom-0">
        <div className="max-w-3xl mx-auto flex items-center gap-2">
          <input
            type="text"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            placeholder="Type a message..."
            className="flex-1 h-11 px-4 rounded-3xl border border-white/15 bg-[#16151B] text-sm text-white placeholder:text-[#7E798C] focus:outline-none focus:border-[#8B5CF6]"
          />
          <button
            type="submit"
            disabled={!message.trim()}
            aria-label="Send"
            className={`w-12 h-12 rounded-full flex items-center justify-center transition-colors cursor-pointer ${
              message.trim() ? 'bg-[#8B5CF6] text-white' : 'bg-[#2B2A34] text-[#7E798C]'
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
      className="fixed inset-0 z-50 bg-[#0A0712]/75 flex items-center justify-center p-4"
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="w-full max-w-md rounded-3xl bg-[#222128] border border-white/10 p-5 space-y-4 shadow-xl text-white"
      >
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-lg font-extrabold text-white">Add Music 🎶</h2>
            <p className="text-xs text-[#9C97A8]">Upload an audio file or add a song to play in Party</p>
          </div>
          <button
            type="button"
            onClick={onClose}
            aria-label="Close Add Music"
            className="w-10 h-10 rounded-full bg-[#2B2A34] text-white flex items-center justify-center cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <label className="w-full min-h-[52px] rounded-2xl border border-dashed border-[#8B5CF6]/55 bg-[#16151B] hover:bg-[#1E1C26] px-4 py-3 flex items-center justify-between gap-3 cursor-pointer transition-colors">
          <div className="flex items-center gap-2.5 min-w-0">
            <div className="w-9 h-9 rounded-xl bg-[#8B5CF6] text-white flex items-center justify-center shrink-0">
              <Upload className="w-4 h-4" />
            </div>
            <div className="min-w-0">
              <div className="text-xs font-bold text-white truncate">
                {fileName ? fileName : 'Upload audio file (MP3, WAV, M4A)'}
              </div>
              <div className="text-[11px] text-[#9C97A8]">
                {fileName ? `${formatTrackTime(durationSec)} ready to play` : 'Optional · Plays your real audio file'}
              </div>
            </div>
          </div>
          <span className="px-3 py-1.5 rounded-xl bg-[#2B2A34] text-[#A78BFA] font-bold text-xs shrink-0">
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
            className="w-full min-h-[44px] px-3.5 rounded-xl border border-white/15 bg-[#16151B] text-sm text-white placeholder:text-[#7E798C] focus:outline-none focus:ring-2 focus:ring-[#8B5CF6]"
          />
          <input
            type="text"
            value={artist}
            onChange={(e) => setArtist(e.target.value)}
            placeholder="Artist (optional)"
            className="w-full min-h-[44px] px-3.5 rounded-xl border border-white/15 bg-[#16151B] text-sm text-white placeholder:text-[#7E798C] focus:outline-none focus:ring-2 focus:ring-[#8B5CF6]"
          />
        </div>

        <div>
          <div className="text-xs font-bold text-white/85 mb-2">Music vibe</div>
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
                    active ? 'text-white' : 'bg-[#2B2A34] text-white/85'
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
              title.trim() ? 'bg-[#2B2A34] text-[#A78BFA] hover:bg-[#353340]' : 'bg-[#2B2A34] text-[#7E798C] opacity-60'
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
      className="fixed inset-0 z-50 bg-[#0A0712]/75 flex items-center justify-center p-4"
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="w-full max-w-md rounded-3xl bg-[#222128] border border-white/10 p-5 space-y-4 shadow-xl text-white"
      >
        <div className="flex items-center justify-between gap-3">
          <div className="min-w-0">
            <h2 className="text-lg font-extrabold text-white">Report Room</h2>
            <p className="text-xs text-[#9C97A8] truncate">Flagging: {room.title}</p>
          </div>
          <button
            type="button"
            onClick={onClose}
            aria-label="Close Report Room"
            className="w-10 h-10 rounded-full bg-[#2B2A34] text-white flex items-center justify-center cursor-pointer shrink-0"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="space-y-2">
          <div className="text-xs font-bold text-white/85">Select a reason</div>
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
                      : 'bg-[#2B2A34] text-white/85 hover:bg-[#353340]'
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
          <label htmlFor="report-room-details" className="block text-xs font-bold text-white/85 mb-1.5">
            Additional details (optional)
          </label>
          <textarea
            id="report-room-details"
            rows={3}
            value={details}
            onChange={(e) => setDetails(e.target.value)}
            placeholder="Share any context that helps our moderation team..."
            className="w-full p-3 rounded-xl border border-white/15 bg-[#16151B] text-xs text-white placeholder:text-[#7E798C] focus:outline-none focus:ring-2 focus:ring-[#8B5CF6]"
          />
        </div>

        <div className="flex items-center gap-2 pt-1">
          <button
            type="button"
            onClick={onClose}
            className="flex-1 min-h-[44px] rounded-xl bg-[#2B2A34] text-white font-bold text-xs cursor-pointer"
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
  statuses,
  rooms,
  friends,
  userStatusText,
  onChat,
  onTab,
  onOpenSoulMatch,
  onJoinVoiceGame,
  onOpenFriends,
  onAddFriend,
  onOpenStatusModal,
}: {
  statuses: OnlineStatusPerson[];
  rooms: VoiceRoom[];
  friends: string[];
  userStatusText: string;
  onChat: (name: string) => void;
  onTab: (tab: TabId) => void;
  onOpenSoulMatch: () => void;
  onJoinVoiceGame: () => void;
  onOpenFriends: () => void;
  onAddFriend: (name: string) => void;
  onOpenStatusModal: () => void;
}) {
  const [regionFilter, setRegionFilter] = useState<'domestic' | 'worldwide'>('domestic');
  const [genderFilter, setGenderFilter] = useState<'all' | 'female' | 'male'>('all');
  const [notifOpen, setNotifOpen] = useState(false);

  const visibleStatuses = statuses.filter((person) => {
    const matchesRegion = regionFilter === 'worldwide' ? true : person.region === 'domestic';
    const matchesGender = genderFilter === 'all' || person.gender === genderFilter;
    return matchesRegion && matchesGender;
  });

  const cycleGenderFilter = () => {
    setGenderFilter((prev) => (prev === 'all' ? 'female' : prev === 'female' ? 'male' : 'all'));
  };

  return (
    <div className="pb-24 min-h-full bg-[#16151B] text-white relative">
      <div className="flex items-center justify-between px-4 py-3.5 relative">
        <button
          type="button"
          onClick={onOpenFriends}
          aria-label="Friends & Filters"
          className="w-10 h-10 rounded-full bg-[#222128] hover:bg-[#2B2A34] flex items-center justify-center text-white/85 cursor-pointer transition-colors"
        >
          <SlidersHorizontal className="w-5 h-5" />
        </button>
        <div className="text-center">
          <h1 className="text-white text-[18px] font-extrabold tracking-wide">MY HAPPY PLACE</h1>
        </div>
        <button
          type="button"
          onClick={() => setNotifOpen((p) => !p)}
          aria-label="Notifications"
          className="relative w-10 h-10 rounded-full bg-[#222128] hover:bg-[#2B2A34] flex items-center justify-center text-[#FF7EB6] cursor-pointer transition-colors"
        >
          <Bell className="w-5 h-5" />
          <span className="absolute top-2 right-2 w-2 h-2 rounded-full bg-[#FF4D6D]" />
        </button>
        {notifOpen && (
          <div className="absolute right-4 top-16 z-30 w-64 rounded-2xl bg-[#222128] shadow-xl border border-white/10 p-3.5 text-xs">
            <div className="flex items-center justify-between mb-2">
              <span className="font-bold text-white">Notifications</span>
              <button type="button" onClick={() => setNotifOpen(false)} className="text-white/50 hover:text-white cursor-pointer">
                <X className="w-4 h-4" />
              </button>
            </div>
            <p className="text-white/75">Welcome to MY HAPPY PLACE 💜 {rooms.length} live rooms active now.</p>
          </div>
        )}
      </div>

      <div className="px-4 space-y-3">
        <div className="grid grid-cols-3 gap-2.5">
          <button
            type="button"
            onClick={onOpenSoulMatch}
            className="rounded-2xl bg-[#222128] hover:bg-[#2B2A34] border border-white/5 p-3.5 flex flex-col items-center text-center cursor-pointer transition-colors"
          >
            <div className="w-12 h-12 rounded-2xl bg-[#FF5C8A]/15 flex items-center justify-center text-[#FF5C8A] mb-2">
              <MessageCircle className="w-6 h-6 fill-[#FF5C8A]/30" />
            </div>
            <div className="text-sm font-bold text-white">Soul game</div>
            <div className="mt-1 flex items-center gap-1 text-[11px] text-white/60 tabular-nums">
              <span className="w-1.5 h-1.5 rounded-full bg-[#38BDF8]" />
              <span>23229 Online</span>
            </div>
          </button>

          <button
            type="button"
            onClick={onJoinVoiceGame}
            className="rounded-2xl bg-[#222128] hover:bg-[#2B2A34] border border-white/5 p-3.5 flex flex-col items-center text-center cursor-pointer transition-colors"
          >
            <div className="w-12 h-12 rounded-2xl bg-[#10B981]/15 flex items-center justify-center text-[#34D399] mb-2">
              <Phone className="w-6 h-6" />
            </div>
            <div className="text-sm font-bold text-white">Voice game</div>
            <div className="mt-1 flex items-center gap-1 text-[11px] text-white/60 tabular-nums">
              <span className="w-1.5 h-1.5 rounded-full bg-[#38BDF8]" />
              <span>20042 Online</span>
            </div>
          </button>

          <button
            type="button"
            onClick={() => onTab('party')}
            className="rounded-2xl bg-[#222128] hover:bg-[#2B2A34] border border-white/5 p-3.5 flex flex-col items-center text-center cursor-pointer transition-colors"
          >
            <div className="w-12 h-12 rounded-2xl bg-[#8B5CF6]/20 flex items-center justify-center text-[#A78BFA] mb-2">
              <Mic className="w-6 h-6" />
            </div>
            <div className="text-sm font-bold text-white">Party Match</div>
            <div className="mt-1 flex items-center gap-1 text-[11px] text-white/60 tabular-nums">
              <BarChart2 className="w-3 h-3 text-[#FF5C8A]" />
              <span>9291 Playing</span>
            </div>
          </button>
        </div>

        <div className="rounded-2xl bg-[#222128] border border-white/5 px-3.5 py-3 flex items-center gap-3">
          <div
            className="w-12 h-12 rounded-full shrink-0 flex items-center justify-center shadow-inner"
            style={{ background: 'radial-gradient(circle at 30% 30%, #A78BFA 0%, #6D28D9 65%, #1E1B4B 100%)' }}
          >
            <Globe className="w-6 h-6 text-white" />
          </div>
          <button
            type="button"
            onClick={() => setRegionFilter((r) => (r === 'domestic' ? 'worldwide' : 'domestic'))}
            className="flex-1 min-w-0 text-left cursor-pointer"
          >
            <div className="flex items-center gap-1 text-sm font-extrabold text-white">
              <span>Worldwide Match</span>
              <ChevronRight className="w-4 h-4 text-white/50" />
            </div>
            <div className="text-[11px] text-white/55 truncate">Meet worldwide Friends online</div>
          </button>
          <button
            type="button"
            onClick={() => setRegionFilter((r) => (r === 'domestic' ? 'worldwide' : 'domestic'))}
            className="px-3 py-1.5 rounded-full bg-[#3A2D54] hover:bg-[#4B396E] text-[#E9D5FF] text-xs font-bold whitespace-nowrap cursor-pointer transition-colors"
          >
            {regionFilter === 'domestic' ? 'Domestic ⇄' : 'Worldwide ⇄'}
          </button>
          <button
            type="button"
            onClick={cycleGenderFilter}
            aria-label="Filter by gender"
            className="px-2.5 py-1.5 rounded-full bg-[#2B2A34] hover:bg-[#363442] text-[#A78BFA] text-xs font-bold whitespace-nowrap cursor-pointer transition-colors"
          >
            {genderFilter === 'all' ? 'All' : genderFilter === 'female' ? '♀' : '♂'}
          </button>
        </div>

        {userStatusText && (
          <div className="rounded-2xl bg-[#222128] border border-[#8B5CF6]/25 px-3.5 py-2.5 flex items-center justify-between gap-2">
            <div className="min-w-0">
              <div className="text-[10px] font-bold uppercase tracking-wider text-[#A78BFA]">Your live status</div>
              <div className="text-xs text-white/90 truncate">{userStatusText}</div>
            </div>
            <button
              type="button"
              onClick={onOpenStatusModal}
              className="text-xs font-bold text-[#A78BFA] hover:text-white px-2.5 py-1 rounded-full bg-[#2B2A34] cursor-pointer shrink-0"
            >
              Edit
            </button>
          </div>
        )}

        <div className="space-y-1 pt-1">
          {visibleStatuses.map((person) => {
            const isFriend = friends.includes(person.name);
            return (
              <div
                key={person.id}
                className="flex items-center gap-3 px-2 py-2.5 rounded-2xl hover:bg-[#222128]/90 transition-colors"
              >
                <button
                  type="button"
                  onClick={() => onChat(person.name)}
                  className="flex-1 min-w-0 flex items-center gap-3 text-left cursor-pointer"
                >
                  <div className="relative shrink-0">
                    <AvatarBadge name={person.name} size="md" />
                    <span className="absolute bottom-0 right-0 w-3 h-3 rounded-full bg-[#38BDF8] ring-2 ring-[#16151B]" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-1.5">
                      <span className="text-sm font-bold text-white truncate">{person.name}</span>
                      <span
                        className={`px-1.5 py-0.5 rounded-full text-[10px] font-bold ${
                          person.gender === 'female'
                            ? 'bg-[#FF5C8A]/20 text-[#FF85A6]'
                            : 'bg-[#38BDF8]/20 text-[#60A5FA]'
                        }`}
                      >
                        {person.gender === 'female' ? '♀' : '♂'} {person.age}
                      </span>
                    </div>
                    <div className="text-xs text-white/60 truncate mt-0.5">{person.status}</div>
                  </div>
                </button>
                <button
                  type="button"
                  onClick={() => (isFriend ? onChat(person.name) : onAddFriend(person.name))}
                  className={`min-h-[36px] px-3 rounded-full text-xs font-bold inline-flex items-center gap-1 whitespace-nowrap cursor-pointer shrink-0 transition-colors ${
                    isFriend
                      ? 'bg-[#2B2A34] text-white/80 hover:bg-[#383645]'
                      : 'bg-[#8B5CF6]/20 hover:bg-[#8B5CF6] text-[#C4B5FD] hover:text-white'
                  }`}
                >
                  {isFriend ? <UserCheck className="w-3.5 h-3.5" /> : <UserPlus className="w-3.5 h-3.5" />}
                  <span>{isFriend ? 'Chat' : 'Add'}</span>
                </button>
              </div>
            );
          })}
        </div>
      </div>

      <button
        type="button"
        onClick={onOpenStatusModal}
        className="fixed bottom-24 right-5 md:bottom-8 md:right-8 z-30 min-h-[44px] px-4 rounded-full bg-[#2B2A34] hover:bg-[#3A3846] border border-white/10 text-white font-bold text-xs flex items-center gap-1.5 shadow-lg cursor-pointer transition-colors"
      >
        <Plus className="w-4 h-4" />
        <span>Status</span>
      </button>
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
      className="VoiceRoomCard group/card w-full rounded-[18px] p-3.5 flex items-center gap-3 border border-white/10 transition-[transform,box-shadow] duration-200 ease-[cubic-bezier(0.16,1,0.3,1)] hover:scale-[1.01] hover:border-white/20 active:scale-[0.99] motion-reduce:transform-none motion-reduce:transition-none"
      style={{
        backgroundColor: '#222128',
        backgroundImage: `linear-gradient(135deg, ${gradientStart}30 0%, #222128 70%)`,
      }}
    >
      <button
        type="button"
        onClick={onClick}
        className="flex-1 min-w-0 flex items-center gap-3 text-left cursor-pointer focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#8B5CF6] rounded-xl"
      >
        <div className="relative shrink-0">
          <AvatarBadge name={owner} size="lg" />
          <span
            role="img"
            aria-label={`${mood.label} mood: ${mood.description}`}
            className="group/mood absolute -bottom-1 -right-1 w-6 h-6 rounded-full bg-[#16151B] border border-white/15 flex items-center justify-center shadow-sm"
          >
            <MoodIcon className="w-3.5 h-3.5" style={{ color: gradientStart }} />
            <span
              role="tooltip"
              className="pointer-events-none absolute left-0 bottom-full mb-2 z-30 w-max max-w-[210px] rounded-lg bg-[#16151B] px-2.5 py-1.5 text-left text-[11px] leading-snug text-white shadow-lg ring-1 ring-white/15 opacity-0 translate-y-1 transition-[opacity,transform] duration-150 ease-out group-hover/mood:opacity-100 group-hover/mood:translate-y-0 motion-reduce:transition-none"
            >
              <span className="font-bold">{mood.label}</span>
              <span className="text-white/80"> — {mood.description}</span>
            </span>
          </span>
        </div>

        <div className="flex-1 min-w-0 space-y-1">
          <div className="text-white font-bold text-[15px] truncate">{room.title}</div>
          <div className="flex items-center gap-1.5 min-w-0">
            <AvatarBadge name={owner} size="xs" />
            <span className="text-white/80 text-xs font-semibold truncate">{owner}</span>
            <span className="text-white/40 text-[11px]">·</span>
            <span className="text-white/60 text-[11px] truncate">{mood.label}</span>
          </div>
          <div className="text-white/50 text-[11px] truncate tabular-nums">{room.detail}</div>
        </div>
      </button>

      <div className="flex flex-col items-end gap-1.5 shrink-0">
        <div className="flex items-center gap-1.5">
          <span
            aria-label={`${room.listeners} live users`}
            className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-black/30 text-[#C4B5FD] text-xs font-bold tabular-nums whitespace-nowrap ${
              isActive ? 'live-user-badge--active' : 'opacity-70'
            }`}
          >
            <BarChart2 className="w-3.5 h-3.5 text-[#8B5CF6] shrink-0" />
            <span>{room.listeners}</span>
          </span>
          <button
            type="button"
            onClick={onClick}
            className="text-white font-bold text-xs px-3 py-1 rounded-full bg-[#8B5CF6] hover:bg-[#7C3AED] whitespace-nowrap cursor-pointer transition-colors"
          >
            JOIN
          </button>
        </div>
        <button
          type="button"
          onClick={onReport}
          aria-label={`Report Room: ${room.title}`}
          className={`min-h-[28px] px-2.5 py-0.5 rounded-full text-[11px] font-semibold inline-flex items-center gap-1 whitespace-nowrap cursor-pointer transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#FF5C8A] ${
            isReported
              ? 'bg-[#FF5C8A] text-white'
              : 'bg-white/10 hover:bg-[#FF5C8A] text-white/75 hover:text-white'
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
      className="mx-4 rounded-[22px] border border-white/10 bg-[#222128] p-6 sm:p-7 text-center max-w-lg shadow-sm"
      role="status"
      aria-live="polite"
    >
      <div
        className="mx-auto w-12 h-12 rounded-2xl flex items-center justify-center shadow-sm mb-3"
        style={{ backgroundColor: `${accentColor}26`, color: accentColor }}
      >
        <MoodIcon className="w-6 h-6" />
      </div>
      <h2 className="text-white font-extrabold text-base">No rooms found for this mood</h2>
      <p className="text-xs text-white/60 mt-1.5 leading-relaxed max-w-sm mx-auto">{description}</p>
      <div className="flex flex-wrap items-center justify-center gap-2.5 mt-4">
        <button
          type="button"
          onClick={onResetAll}
          className="min-h-[44px] px-6 rounded-full bg-[#8B5CF6] hover:bg-[#7C3AED] text-white font-bold text-xs cursor-pointer transition-colors whitespace-nowrap"
        >
          All
        </button>
        <button
          type="button"
          onClick={onCreateRoom}
          className="min-h-[44px] px-5 rounded-full bg-[#2B2A34] hover:bg-[#363442] text-white border border-white/10 font-bold text-xs cursor-pointer transition-colors whitespace-nowrap"
        >
          Create Party
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
  onOpenFamily,
  onOpenShop,
  onCreateRoom,
}: {
  rooms: VoiceRoom[];
  musicLibrary: PartyTrack[];
  reportedRoomIds: string[];
  onJoinRoom: (room: VoiceRoom) => void;
  onReportRoom: (room: VoiceRoom) => void;
  onOpenAddMusic: () => void;
  onOpenFamily: () => void;
  onOpenShop: () => void;
  onCreateRoom: (payload: {
    title: string;
    mood: RoomMoodId;
    backgroundId: PartyBackgroundId;
    partyMode: 'Free Mic' | 'Music Lounge' | 'Karaoke' | 'Truth or Dare';
    welcomeMessage: string;
    isLocked: boolean;
  }) => void;
}) {
  const [title, setTitle] = useState('');
  const [selectedMood, setSelectedMood] = useState<RoomMoodId>('happy');
  const [selectedBg, setSelectedBg] = useState<PartyBackgroundId>('lounge');
  const [selectedPartyMode, setSelectedPartyMode] = useState<'Free Mic' | 'Music Lounge' | 'Karaoke' | 'Truth or Dare'>('Free Mic');
  const [welcomeMessage, setWelcomeMessage] = useState('');
  const [isLocked, setIsLocked] = useState(false);
  const [moodFilter, setMoodFilter] = useState<RoomMoodId | 'all'>('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [sortBy, setSortBy] = useState<'popular' | 'newest'>('popular');
  const [activeSubTab, setActiveSubTab] = useState<'following' | 'foru' | 'hot' | 'new' | 'recent'>('recent');
  const [creating, setCreating] = useState(false);
  const searchInputRef = useRef<HTMLInputElement>(null);

  const activeCreateMood = getRoomMood(selectedMood);
  const ActiveCreateMoodIcon = activeCreateMood.icon;
  const activeCreateBg = PARTY_BACKGROUNDS.find((b) => b.id === selectedBg) ?? PARTY_BACKGROUNDS[0];
  const createTitleSuggestions = ['Chill & Chat ☕', 'Khmer Music Night 🎶', 'Late Night Karaoke 🎤', 'Truth or Dare 🎲'];
  const createPartyModes: ('Free Mic' | 'Music Lounge' | 'Karaoke' | 'Truth or Dare')[] = [
    'Free Mic',
    'Music Lounge',
    'Karaoke',
    'Truth or Dare',
  ];

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

  const handleJoinRandom = () => {
    const pool = visibleRooms.length > 0 ? visibleRooms : rooms;
    if (pool.length === 0) return;
    const picked = pool[Math.floor(Math.random() * pool.length)];
    onJoinRoom(picked);
  };

  return (
    <div className="pb-28 min-h-full bg-[#16151B] text-white relative">
      <div className="px-4 pt-4 pb-3 flex items-center justify-between gap-3">
        <div>
          <h1 className="text-[22px] font-extrabold text-white">Party Chat</h1>
          <p className="text-[11px] text-white/55 tabular-nums">{musicLibrary.length} party tracks ready</p>
        </div>
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={onOpenAddMusic}
            aria-label="Open music library"
            className="relative w-10 h-10 rounded-xl bg-[#222128] hover:bg-[#2B2A34] flex items-center justify-center text-[#A78BFA] cursor-pointer transition-colors"
          >
            <Music className="w-5 h-5" />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-[#FF4D6D]" />
          </button>
          <button
            type="button"
            onClick={() => searchInputRef.current?.focus()}
            aria-label="Focus room search"
            className="w-10 h-10 rounded-xl bg-[#222128] hover:bg-[#2B2A34] flex items-center justify-center text-white/85 cursor-pointer transition-colors"
          >
            <Search className="w-5 h-5" />
          </button>
        </div>
      </div>

      {/* 2026 Annual Points Banner */}
      <div className="px-4 mb-3">
        <div
          className="rounded-2xl p-4 relative overflow-hidden border border-[#FBBF24]/25"
          style={{ background: 'linear-gradient(135deg, #4C1D95 0%, #831843 55%, #B45309 100%)' }}
        >
          <div className="flex items-center justify-between gap-3">
            <div className="min-w-0">
              <div className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full bg-black/30 text-[#FDE68A] text-[10px] font-bold mb-1">
                <Trophy className="w-3 h-3" />
                <span>2026 Annual Points</span>
              </div>
              <div className="text-sm font-extrabold text-white">Complete tasks &amp; earn Deluxe Lucky rewards</div>
              <p className="text-[11px] text-white/80 mt-0.5">Collect room heat points with friends in MY HAPPY PLACE</p>
            </div>
            <button
              type="button"
              onClick={handleJoinRandom}
              className="px-3.5 py-2 rounded-full bg-[#FBBF24] text-[#251B3A] font-extrabold text-xs whitespace-nowrap cursor-pointer shrink-0"
            >
              Join Now
            </button>
          </div>
        </div>
      </div>

      {/* Ranking / Family / Shop Shortcuts */}
      <div className="px-4 mb-4 grid grid-cols-3 gap-2.5">
        <button
          type="button"
          onClick={() => {
            setSortBy('popular');
            setActiveSubTab('hot');
          }}
          className="rounded-2xl bg-[#222128] hover:bg-[#2B2A34] py-2.5 px-2 flex flex-col items-center gap-1 cursor-pointer transition-colors"
        >
          <Trophy className="w-5 h-5 text-[#FBBF24]" />
          <span className="text-xs font-semibold text-white/85">Ranking</span>
        </button>
        <button
          type="button"
          onClick={onOpenFamily}
          className="rounded-2xl bg-[#222128] hover:bg-[#2B2A34] py-2.5 px-2 flex flex-col items-center gap-1 cursor-pointer transition-colors"
        >
          <Shield className="w-5 h-5 text-[#8B5CF6]" />
          <span className="text-xs font-semibold text-white/85">Family</span>
        </button>
        <button
          type="button"
          onClick={onOpenShop}
          className="rounded-2xl bg-[#222128] hover:bg-[#2B2A34] py-2.5 px-2 flex flex-col items-center gap-1 cursor-pointer transition-colors"
        >
          <ShoppingBag className="w-5 h-5 text-[#FF5C8A]" />
          <span className="text-xs font-semibold text-white/85">Shop</span>
        </button>
      </div>

      {/* Category Sub-tabs */}
      <div className="px-4 pb-3 flex items-center gap-5 overflow-x-auto border-b border-white/5 mb-3">
        {(
          [
            { id: 'following', label: 'Following' },
            { id: 'foru', label: 'For U' },
            { id: 'hot', label: 'Hot' },
            { id: 'new', label: 'New' },
            { id: 'recent', label: 'Recent' },
          ] as const
        ).map((sub) => {
          const active = activeSubTab === sub.id;
          return (
            <button
              key={sub.id}
              type="button"
              onClick={() => {
                setActiveSubTab(sub.id);
                if (sub.id === 'new') setSortBy('newest');
                else if (sub.id === 'hot') setSortBy('popular');
              }}
              className={`pb-2 text-sm font-bold whitespace-nowrap relative cursor-pointer transition-colors ${
                active ? 'text-white' : 'text-white/45 hover:text-white/80'
              }`}
            >
              {sub.label}
              {active && <span className="absolute left-1/2 -translate-x-1/2 bottom-0 w-5 h-0.5 rounded-full bg-[#8B5CF6]" />}
            </button>
          );
        })}
      </div>

      {/* Search & Sort Controls */}
      <div className="px-4 pb-3 flex flex-wrap items-center gap-2.5 max-w-2xl">
        <div className="relative flex items-center flex-1 min-w-[200px]">
          <label htmlFor="party-room-search" className="sr-only">
            Search rooms by title or owner name
          </label>
          <Search className="w-4 h-4 text-[#A78BFA] absolute left-3.5 pointer-events-none" />
          <input
            ref={searchInputRef}
            id="party-room-search"
            type="search"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search rooms by title or host name..."
            className="w-full min-h-[42px] pl-10 pr-10 rounded-2xl bg-[#222128] border border-white/10 text-sm text-white placeholder:text-white/40 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#8B5CF6] transition-colors"
          />
          {searchQuery.length > 0 && (
            <button
              type="button"
              onClick={() => setSearchQuery('')}
              aria-label="Clear room search"
              className="absolute right-1.5 min-h-[34px] min-w-[34px] rounded-full flex items-center justify-center text-white/50 hover:text-white cursor-pointer"
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>
        <div className="relative flex items-center shrink-0">
          <label htmlFor="party-room-sort" className="sr-only">
            Sort voice rooms
          </label>
          <ArrowUpDown className="w-3.5 h-3.5 text-[#A78BFA] absolute left-3 pointer-events-none" />
          <select
            id="party-room-sort"
            aria-label="Sort voice rooms"
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value as 'popular' | 'newest')}
            className="min-h-[42px] pl-8 pr-4 rounded-2xl bg-[#222128] border border-white/10 text-xs font-bold text-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#8B5CF6] cursor-pointer"
          >
            <option value="popular">Most Popular</option>
            <option value="newest">Newest</option>
          </select>
        </div>
      </div>

      {/* Mood Filter Pills */}
      <div className="px-4 pb-4 flex items-center gap-2 overflow-x-auto">
        <button
          type="button"
          onClick={() => setMoodFilter('all')}
          className={`min-h-[38px] px-4 rounded-full text-xs font-bold whitespace-nowrap shrink-0 cursor-pointer transition-colors ${
            moodFilter === 'all' ? 'bg-[#8B5CF6] text-white' : 'bg-[#222128] text-white/75 hover:bg-[#2B2A34]'
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
              className={`min-h-[38px] px-3.5 rounded-full text-xs font-bold flex items-center gap-1.5 whitespace-nowrap shrink-0 cursor-pointer transition-colors ${
                active ? 'text-white' : 'bg-[#222128] text-white/75 hover:bg-[#2B2A34]'
              }`}
              style={active ? { backgroundColor: mood.color } : undefined}
            >
              <MoodIcon className="w-3.5 h-3.5 shrink-0" style={active ? undefined : { color: mood.color }} />
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
        <div className="px-4 grid grid-cols-1 md:grid-cols-2 gap-3">
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

      {creating && (
        <div
          className="fixed inset-0 z-50 bg-black/75 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4"
          role="dialog"
          aria-modal="true"
          aria-labelledby="create-party-title"
        >
          <form
            onSubmit={(e) => {
              e.preventDefault();
              const cleanTitle = title.trim() || 'My Happy Party';
              onCreateRoom({
                title: cleanTitle,
                mood: selectedMood,
                backgroundId: selectedBg,
                partyMode: selectedPartyMode,
                welcomeMessage: welcomeMessage.trim(),
                isLocked,
              });
              setTitle('');
              setSelectedMood('happy');
              setSelectedBg('lounge');
              setSelectedPartyMode('Free Mic');
              setWelcomeMessage('');
              setIsLocked(false);
              setCreating(false);
            }}
            className="w-full max-w-lg max-h-[90vh] flex flex-col rounded-t-3xl sm:rounded-3xl bg-[#1D1C24] border border-white/10 shadow-2xl overflow-hidden"
          >
            {/* Modal Header */}
            <div className="px-5 py-4 border-b border-white/10 flex items-center justify-between shrink-0">
              <div className="flex items-center gap-2.5">
                <div className="w-8 h-8 rounded-xl bg-gradient-to-br from-[#8B5CF6] to-[#FF5C8A] flex items-center justify-center text-white">
                  <Sparkles className="w-4 h-4" />
                </div>
                <div>
                  <h2 id="create-party-title" className="text-base font-extrabold text-white leading-tight">
                    Create Party
                  </h2>
                  <p className="text-[11px] text-white/55">Customize your live voice room</p>
                </div>
              </div>
              <button
                type="button"
                onClick={() => setCreating(false)}
                className="min-h-[38px] min-w-[38px] rounded-full bg-white/5 hover:bg-white/10 flex items-center justify-center text-white/70 hover:text-white cursor-pointer transition-colors"
                aria-label="Close Create Party modal"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            {/* Scrollable Body */}
            <div className="p-5 space-y-4 overflow-y-auto">
              {/* Live Room Preview Card */}
              <div
                className="relative rounded-2xl p-4 overflow-hidden border border-white/15"
                style={
                  activeCreateBg.imageSrc
                    ? {
                        backgroundImage: `linear-gradient(135deg, rgba(16,14,26,0.72), rgba(22,19,38,0.88)), url(${activeCreateBg.imageSrc})`,
                        backgroundSize: 'cover',
                        backgroundPosition: 'center',
                      }
                    : {
                        backgroundImage:
                          selectedBg === 'mood' ? getMoodRoomGradient(selectedMood) : activeCreateBg.gradient,
                      }
                }
              >
                <div className="flex items-start justify-between gap-3">
                  <div className="flex items-center gap-3 min-w-0">
                    <div
                      className="w-11 h-11 rounded-2xl flex items-center justify-center shrink-0 text-white shadow-md"
                      style={{ backgroundColor: activeCreateMood.color }}
                    >
                      <ActiveCreateMoodIcon className="w-5 h-5" />
                    </div>
                    <div className="min-w-0">
                      <div className="flex items-center gap-1.5">
                        <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-full bg-white/15 text-white">
                          {selectedPartyMode}
                        </span>
                        {isLocked && (
                          <span className="inline-flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-full bg-[#FBBF24]/20 text-[#FDE68A]">
                            <Lock className="w-2.5 h-2.5" /> Locked
                          </span>
                        )}
                      </div>
                      <div className="text-sm font-extrabold text-white truncate mt-1">
                        {title.trim() || 'My Happy Party'}
                      </div>
                      <div className="text-[11px] text-white/75 truncate">
                        Host: You · {activeCreateMood.label} · {activeCreateBg.label}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              {/* Party Name + Quick Ideas */}
              <div>
                <label htmlFor="create-party-name" className="block text-xs font-bold text-white/85 mb-1.5">
                  Party Name
                </label>
                <input
                  id="create-party-name"
                  type="text"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  maxLength={48}
                  placeholder="Enter a party title (e.g. Chill Vibes ☕)"
                  className="w-full min-h-[44px] px-3.5 rounded-xl border border-white/15 bg-[#16151B] text-sm text-white placeholder:text-white/40 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#8B5CF6]"
                />
                <div className="flex items-center gap-1.5 overflow-x-auto pt-2 pb-0.5">
                  {createTitleSuggestions.map((idea) => (
                    <button
                      key={idea}
                      type="button"
                      onClick={() => setTitle(idea)}
                      className="px-2.5 py-1 rounded-full bg-[#2B2A34] hover:bg-[#363442] border border-white/10 text-[11px] font-medium text-white/80 whitespace-nowrap shrink-0 cursor-pointer transition-colors"
                    >
                      {idea}
                    </button>
                  ))}
                </div>
              </div>

              {/* Party Mode */}
              <div>
                <div className="text-xs font-bold text-white/85 mb-2">Party Mode</div>
                <div className="grid grid-cols-2 gap-2">
                  {createPartyModes.map((mode) => {
                    const active = selectedPartyMode === mode;
                    return (
                      <button
                        key={mode}
                        type="button"
                        onClick={() => setSelectedPartyMode(mode)}
                        className={`min-h-[40px] px-3 py-2 rounded-xl flex items-center justify-between text-xs font-bold cursor-pointer border transition-colors ${
                          active
                            ? 'border-[#8B5CF6] bg-[#8B5CF6]/20 text-white'
                            : 'border-white/10 bg-[#2B2A34] text-white/75 hover:bg-[#363442]'
                        }`}
                      >
                        <span>{mode}</span>
                        {active && <Check className="w-3.5 h-3.5 text-[#A78BFA] shrink-0" />}
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Mood Tag */}
              <div>
                <div className="text-xs font-bold text-white/85 mb-2">Mood Tag</div>
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
                  {ROOM_MOODS.map((mood) => {
                    const MoodIcon = mood.icon;
                    const active = selectedMood === mood.id;
                    return (
                      <button
                        key={mood.id}
                        type="button"
                        onClick={() => setSelectedMood(mood.id)}
                        className={`min-h-[40px] px-3 py-2 rounded-xl flex items-center gap-2 text-xs font-bold whitespace-nowrap cursor-pointer transition-colors ${
                          active ? 'text-white shadow-sm' : 'bg-[#2B2A34] text-white/75 hover:bg-[#363442]'
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

              {/* Room Background */}
              <div>
                <div className="text-xs font-bold text-white/85 mb-2">Room Background</div>
                <div className="flex items-center gap-2 overflow-x-auto pb-1">
                  {PARTY_BACKGROUNDS.map((bg) => {
                    const active = selectedBg === bg.id;
                    return (
                      <button
                        key={bg.id}
                        type="button"
                        onClick={() => setSelectedBg(bg.id)}
                        className={`min-h-[46px] px-3 py-1.5 rounded-xl flex items-center gap-2 text-xs font-semibold whitespace-nowrap shrink-0 cursor-pointer border transition-colors ${
                          active
                            ? 'border-[#8B5CF6] bg-[#8B5CF6]/20 text-white'
                            : 'border-white/10 bg-[#2B2A34] text-white/70 hover:text-white'
                        }`}
                      >
                        <span
                          className="w-5 h-5 rounded-md shrink-0 bg-cover bg-center border border-white/20"
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

              {/* Welcome Greeting & Privacy Lock */}
              <div className="space-y-3">
                <div>
                  <label htmlFor="create-party-welcome" className="block text-xs font-bold text-white/85 mb-1.5">
                    Welcome Wording (Optional)
                  </label>
                  <input
                    id="create-party-welcome"
                    type="text"
                    value={welcomeMessage}
                    onChange={(e) => setWelcomeMessage(e.target.value)}
                    maxLength={80}
                    placeholder="Welcome everyone! Grab a mic 🎙️"
                    className="w-full min-h-[42px] px-3.5 rounded-xl border border-white/15 bg-[#16151B] text-xs text-white placeholder:text-white/40 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#8B5CF6]"
                  />
                </div>
                <button
                  type="button"
                  onClick={() => setIsLocked((prev) => !prev)}
                  className="w-full rounded-xl bg-[#2B2A34] hover:bg-[#363442] border border-white/10 px-3.5 py-2.5 flex items-center justify-between gap-3 cursor-pointer transition-colors"
                >
                  <div className="flex items-center gap-2.5 text-left">
                    {isLocked ? (
                      <Lock className="w-4 h-4 text-[#FBBF24] shrink-0" />
                    ) : (
                      <Unlock className="w-4 h-4 text-[#34D399] shrink-0" />
                    )}
                    <div>
                      <div className="text-xs font-bold text-white">
                        {isLocked ? 'Locked Party (Approval Needed)' : 'Open Party (Anyone Can Join)'}
                      </div>
                      <div className="text-[11px] text-white/55">
                        {isLocked ? 'Listeners need your invite to take a mic' : 'Guests can hop on open mic seats'}
                      </div>
                    </div>
                  </div>
                  <span
                    className={`w-10 h-6 rounded-full p-0.5 transition-colors flex items-center shrink-0 ${
                      isLocked ? 'bg-[#8B5CF6] justify-end' : 'bg-white/20 justify-start'
                    }`}
                  >
                    <span className="w-5 h-5 rounded-full bg-white shadow" />
                  </span>
                </button>
              </div>
            </div>

            {/* Footer CTA */}
            <div className="px-5 py-4 border-t border-white/10 bg-[#18171F] flex items-center gap-2.5 shrink-0">
              <button
                type="button"
                onClick={() => setCreating(false)}
                className="min-h-[44px] px-5 rounded-xl bg-[#2B2A34] hover:bg-[#363442] text-white font-semibold text-sm cursor-pointer transition-colors"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="flex-1 min-h-[44px] px-5 rounded-xl bg-gradient-to-r from-[#8B5CF6] to-[#FF5C8A] text-white font-extrabold text-sm cursor-pointer shadow-lg hover:opacity-95 transition-opacity"
              >
                Create Party
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Floating Create Party | Random Dock */}
      <div className="fixed bottom-20 left-1/2 -translate-x-1/2 md:bottom-6 z-30 flex items-center rounded-full bg-[#2B2A34]/95 backdrop-blur-md border border-white/10 px-2 py-1.5 shadow-xl">
        <button
          type="button"
          onClick={() => setCreating(true)}
          className="px-4 py-1.5 rounded-full hover:bg-white/10 text-white text-xs font-bold flex items-center gap-1.5 cursor-pointer transition-colors"
        >
          <span className="w-6 h-6 rounded-full bg-gradient-to-br from-[#8B5CF6] to-[#FF5C8A] text-white flex items-center justify-center">
            <Plus className="w-3.5 h-3.5" />
          </span>
          <span>Create Party</span>
        </button>
        <span className="w-px h-5 bg-white/15 mx-1" />
        <button
          type="button"
          onClick={handleJoinRandom}
          className="px-4 py-1.5 rounded-full hover:bg-white/10 text-white text-xs font-bold flex items-center gap-1.5 cursor-pointer transition-colors"
        >
          <span className="w-6 h-6 rounded-full bg-[#38BDF8]/20 text-[#38BDF8] flex items-center justify-center">
            <Dice5 className="w-3.5 h-3.5" />
          </span>
          <span>Random</span>
        </button>
      </div>
    </div>
  );
}

function MomentsFeedView({
  posts,
  likedPostIds,
  followingAuthors,
  onToggleLike,
  onAddComment,
  onSharePost,
  onToggleFollow,
  onSayHi,
  onOpenCompose,
}: {
  posts: MomentPost[];
  likedPostIds: string[];
  followingAuthors: string[];
  onToggleLike: (postId: string) => void;
  onAddComment: (postId: string, text: string) => void;
  onSharePost: (postId: string) => void;
  onToggleFollow: (author: string) => void;
  onSayHi: (author: string) => void;
  onOpenCompose: () => void;
}) {
  const [feedTab, setFeedTab] = useState<'following' | 'foryou' | 'latest'>('foryou');
  const [selectedTopicId, setSelectedTopicId] = useState<string | null>(null);
  const [openCommentsFor, setOpenCommentsFor] = useState<string | null>(null);
  const [commentDrafts, setCommentDrafts] = useState<Record<string, string>>({});

  const visiblePosts = posts.filter((post) => {
    if (selectedTopicId && post.topicId !== selectedTopicId) return false;
    if (feedTab === 'following') return followingAuthors.includes(post.author);
    if (feedTab === 'latest') return post.tab === 'latest' || post.timeAgo.includes('Just now') || post.timeAgo.includes('mins');
    return true;
  });

  return (
    <div className="pb-24 min-h-full bg-[#16151B] text-white relative">
      {/* Top Bar */}
      <div className="px-4 pt-4 pb-3 flex items-center justify-between">
        <div className="flex items-center gap-5">
          {(
            [
              { id: 'following', label: 'Following', dot: true },
              { id: 'foryou', label: 'For You', dot: false },
              { id: 'latest', label: 'Latest', dot: false },
            ] as const
          ).map((t) => {
            const active = feedTab === t.id;
            return (
              <button
                key={t.id}
                type="button"
                onClick={() => setFeedTab(t.id)}
                className={`relative text-base font-extrabold cursor-pointer transition-colors ${
                  active ? 'text-white' : 'text-white/45 hover:text-white/80'
                }`}
              >
                {t.label}
                {t.dot && <span className="inline-block w-1.5 h-1.5 rounded-full bg-[#FF4D6D] ml-1 align-top" />}
              </button>
            );
          })}
        </div>
        <button
          type="button"
          onClick={onOpenCompose}
          aria-label="Post new moment"
          className="w-10 h-10 rounded-full bg-[#222128] hover:bg-[#2B2A34] flex items-center justify-center text-white/80 cursor-pointer"
        >
          <Bell className="w-5 h-5" />
        </button>
      </div>

      {/* Story / Topic Circles */}
      <div className="px-4 pb-4 flex items-start gap-3 overflow-x-auto">
        {MOMENT_TOPICS.map((topic) => {
          const active = selectedTopicId === topic.id;
          return (
            <button
              key={topic.id}
              type="button"
              onClick={() => setSelectedTopicId((prev) => (prev === topic.id ? null : topic.id))}
              className="flex flex-col items-center gap-1.5 w-[72px] shrink-0 cursor-pointer"
            >
              <div
                className={`relative w-14 h-14 rounded-full p-0.5 ${active ? 'ring-2 ring-[#8B5CF6]' : ''}`}
                style={{ background: topic.gradient }}
              >
                <div className="w-full h-full rounded-full bg-[#16151B]/30 flex items-center justify-center text-2xl">
                  {topic.emoji}
                </div>
                <span className="absolute -bottom-0.5 -right-0.5 w-5 h-5 rounded-full bg-[#8B5CF6] text-white text-[10px] font-extrabold flex items-center justify-center ring-2 ring-[#16151B]">
                  #
                </span>
              </div>
              <span className="text-[11px] text-white/80 text-center line-clamp-2 leading-tight">{topic.label}</span>
            </button>
          );
        })}
      </div>

      {/* Feed Cards */}
      <div className="px-4 space-y-4">
        {visiblePosts.length === 0 ? (
          <div className="rounded-2xl bg-[#222128] border border-white/10 p-6 text-center space-y-2">
            <p className="text-sm font-bold text-white">No moments here yet</p>
            <p className="text-xs text-white/55">Follow creators in For You or tap All topics to explore.</p>
            <button
              type="button"
              onClick={() => {
                setFeedTab('foryou');
                setSelectedTopicId(null);
              }}
              className="mt-2 px-4 py-2 rounded-full bg-[#8B5CF6] text-white text-xs font-bold cursor-pointer"
            >
              Show For You
            </button>
          </div>
        ) : (
          visiblePosts.map((post) => {
            const isLiked = likedPostIds.includes(post.id);
            const isFollowing = followingAuthors.includes(post.author);
            const showComments = openCommentsFor === post.id;
            const draft = commentDrafts[post.id] ?? '';
            return (
              <article key={post.id} className="rounded-2xl bg-[#222128] border border-white/5 p-4 space-y-3">
                <div className="flex items-center gap-3">
                  <AvatarBadge name={post.author} size="md" />
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-1.5">
                      <span className="font-bold text-sm text-white truncate">{post.author}</span>
                      <span
                        className={`px-1.5 py-0.5 rounded-full text-[10px] font-bold ${
                          post.gender === 'female'
                            ? 'bg-[#FF5C8A]/20 text-[#FF85A6]'
                            : 'bg-[#38BDF8]/20 text-[#60A5FA]'
                        }`}
                      >
                        {post.gender === 'female' ? '♀' : '♂'} {post.age}
                      </span>
                    </div>
                    <div className="text-[11px] text-white/45">{post.timeAgo}</div>
                  </div>
                  <button
                    type="button"
                    onClick={() => onToggleFollow(post.author)}
                    className={`px-3.5 py-1.5 rounded-full text-xs font-bold cursor-pointer transition-colors ${
                      isFollowing
                        ? 'bg-[#2B2A34] text-white/75'
                        : 'bg-[#8B5CF6]/20 hover:bg-[#8B5CF6] text-[#C4B5FD] hover:text-white'
                    }`}
                  >
                    {isFollowing ? 'Following' : 'Follow'}
                  </button>
                </div>

                <p className="text-sm text-white/90 leading-relaxed whitespace-pre-line">{post.caption}</p>

                {post.imageBg && (
                  <div
                    className="w-full h-56 rounded-2xl flex items-center justify-center text-5xl border border-white/10 overflow-hidden"
                    style={{ background: post.imageBg }}
                  >
                    <span>{post.imageEmoji ?? '✨'}</span>
                  </div>
                )}

                <div className="flex items-center justify-between pt-1">
                  <button
                    type="button"
                    onClick={() => onSayHi(post.author)}
                    className="px-3.5 py-1.5 rounded-full border border-white/25 hover:border-white/50 text-white text-xs font-bold italic cursor-pointer transition-colors"
                  >
                    Say Hi
                  </button>
                  <div className="flex items-center gap-4 text-xs text-white/70">
                    <button
                      type="button"
                      onClick={() => onToggleLike(post.id)}
                      className={`inline-flex items-center gap-1 cursor-pointer transition-colors ${
                        isLiked ? 'text-[#FF5C8A] font-bold' : 'hover:text-white'
                      }`}
                    >
                      <Heart className={`w-4 h-4 ${isLiked ? 'fill-[#FF5C8A]' : ''}`} />
                      <span className="tabular-nums">{post.likes}</span>
                    </button>
                    <button
                      type="button"
                      onClick={() => setOpenCommentsFor((prev) => (prev === post.id ? null : post.id))}
                      className="inline-flex items-center gap-1 hover:text-white cursor-pointer"
                    >
                      <MessageCircle className="w-4 h-4" />
                      <span className="tabular-nums">{post.comments.length}</span>
                    </button>
                    <button
                      type="button"
                      onClick={() => onSharePost(post.id)}
                      className="inline-flex items-center gap-1 hover:text-white cursor-pointer"
                    >
                      <Share2 className="w-4 h-4" />
                      <span className="tabular-nums">{post.shares}</span>
                    </button>
                  </div>
                </div>

                {showComments && (
                  <div className="pt-2 border-t border-white/10 space-y-2">
                    {post.comments.map((c) => (
                      <div key={c.id} className="text-xs text-white/80 bg-[#16151B] rounded-xl px-3 py-2">
                        <span className="font-bold text-[#A78BFA] mr-1.5">{c.author}:</span>
                        <span>{c.text}</span>
                      </div>
                    ))}
                    <form
                      onSubmit={(e) => {
                        e.preventDefault();
                        if (!draft.trim()) return;
                        onAddComment(post.id, draft);
                        setCommentDrafts((prev) => ({ ...prev, [post.id]: '' }));
                      }}
                      className="flex gap-2"
                    >
                      <input
                        type="text"
                        value={draft}
                        onChange={(e) => setCommentDrafts((prev) => ({ ...prev, [post.id]: e.target.value }))}
                        placeholder="Write a comment..."
                        className="flex-1 min-h-[38px] px-3 rounded-xl bg-[#16151B] border border-white/10 text-xs text-white placeholder:text-white/40"
                      />
                      <button
                        type="submit"
                        className="px-3.5 rounded-xl bg-[#8B5CF6] text-white text-xs font-bold cursor-pointer"
                      >
                        Send
                      </button>
                    </form>
                  </div>
                )}
              </article>
            );
          })
        )}
      </div>

      {/* Floating Compose Button */}
      <button
        type="button"
        onClick={onOpenCompose}
        aria-label="Create Moment"
        className="fixed bottom-24 right-5 md:bottom-8 md:right-8 z-30 w-13 h-13 rounded-full text-white flex items-center justify-center shadow-xl cursor-pointer transition-transform hover:scale-105"
        style={{ background: 'linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%)' }}
      >
        <Feather className="w-5 h-5" />
      </button>
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
  const [seats, setSeats] = useState<(string | null)[]>(() => {
    const initial: (string | null)[] = Array(10).fill(null);
    const hostName = room.owner ?? room.speakers[0] ?? 'Mina';
    initial[0] = hostName;
    return initial;
  });
  const [seatHearts, setSeatHearts] = useState<number[]>(() => Array(10).fill(0));
  const [isMuted, setIsMuted] = useState(false);
  const [roomInput, setRoomInput] = useState('');
  const [lastReaction, setLastReaction] = useState<string | null>(null);
  const [showReactionTray, setShowReactionTray] = useState(false);
  const [showGiftDrawer, setShowGiftDrawer] = useState(false);
  const [showPartyChallenge, setShowPartyChallenge] = useState(false);
  const [challengeLevel, setChallengeLevel] = useState<number>(1);
  const [showBgDrawer, setShowBgDrawer] = useState(false);
  const [showOffMicModal, setShowOffMicModal] = useState(true);
  const [showRoomMenu, setShowRoomMenu] = useState(false);
  const [showInviteModal, setShowInviteModal] = useState(false);
  const [showPartyModeModal, setShowPartyModeModal] = useState(false);
  const [partyMode, setPartyMode] = useState<'Free Mic' | 'Music Lounge' | 'Karaoke' | 'Truth or Dare'>(
    room.partyMode ?? 'Free Mic',
  );
  const [isPartyLocked, setIsPartyLocked] = useState(Boolean(room.isLocked));
  const [blockAnimations, setBlockAnimations] = useState(false);
  const [invitedFriends, setInvitedFriends] = useState<string[]>([]);
  const [roomTitleText, setRoomTitleText] = useState(room.title);
  const [editingRoomTitle, setEditingRoomTitle] = useState(false);
  const [welcomeWording, setWelcomeWording] = useState(room.welcomeMessage?.trim() || 'Please click here to edit~');
  const [editingWelcome, setEditingWelcome] = useState(false);
  const [copiedRoomId, setCopiedRoomId] = useState(false);
  const [selectedGift, setSelectedGift] = useState<KhmerGift>(KHMER_GIFTS[0]);
  const giftRecipients = room.speakers.filter((s) => s !== 'You');
  const recipientOptions = giftRecipients.length > 0 ? giftRecipients : ['Mina', 'Dara', 'Sophy'];
  const [selectedRecipient, setSelectedRecipient] = useState(recipientOptions[0]);
  const [giftBanner, setGiftBanner] = useState<string | null>(null);
  const [roomChat, setRoomChat] = useState<{ sender: string; text: string; isSystem?: boolean }[]>([
    { sender: room.owner ?? room.speakers[0] ?? 'Mina', text: 'is on the mic.', isSystem: true },
  ]);

  const numericRoomId = (() => {
    let hash = 71465514;
    for (let i = 0; i < room.id.length; i += 1) {
      hash = (hash * 31 + room.id.charCodeAt(i)) % 900000000;
    }
    return String(Math.abs(hash) + 100000000).slice(0, 9);
  })();

  const mySeatIndex = seats.findIndex((s) => s === 'You');
  const onStage = mySeatIndex >= 0;
  const offMicUsers = room.speakers.filter((s) => s !== 'You' && !seats.includes(s));
  const totalHearts = seatHearts.reduce((acc, v) => acc + v, 0);
  const heatDisplay = ((19.95 + totalHearts * 0.01 + (onStage ? 0.05 : 0))).toFixed(2) + 'K';
  const chestPct = Math.min(100, 18 + totalHearts * 8 + roomChat.length * 4);

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

  const handleJoinSeat = (index: number) => {
    const occupant = seats[index];
    if (occupant === 'You') {
      setIsMuted((m) => !m);
      return;
    }
    if (occupant !== null) {
      setSeatHearts((prev) => prev.map((h, i) => (i === index ? h + 1 : h)));
      setRoomChat((prev) => [...prev, { sender: 'You', text: `Sent ❤️ to ${occupant}` }]);
      return;
    }
    if (isPartyLocked) {
      setRoomChat((prev) => [...prev, { sender: 'System', text: '🔒 Party is currently locked by host.', isSystem: true }]);
      return;
    }
    setSeats((prev) =>
      prev.map((s, i) => {
        if (i === index) return 'You';
        return s === 'You' ? null : s;
      }),
    );
    setIsMuted(false);
    setRoomChat((prev) => [...prev, { sender: 'You', text: `joined mic seat #${index + 1}.`, isSystem: true }]);
  };

  const handleToggleQuickMicSeat = () => {
    if (onStage) {
      setSeats((prev) => prev.map((s) => (s === 'You' ? null : s)));
      setIsMuted(true);
      setRoomChat((prev) => [...prev, { sender: 'You', text: 'stepped down from the mic.', isSystem: true }]);
    } else {
      const openIdx = seats.findIndex((s) => s === null);
      if (openIdx >= 0) {
        handleJoinSeat(openIdx);
      }
    }
  };

  const renderSeatTile = (seatIdx: number, isTopRow: boolean) => {
    const occupant = seats[seatIdx];
    const isMe = occupant === 'You';
    const isHost = seatIdx === 0;
    const isSpeaking = occupant !== null && (isMe ? !isMuted : isHost);
    const ringSize = isTopRow ? 'w-[62px] h-[62px]' : 'w-[54px] h-[54px]';

    return (
      <div key={seatIdx} className="flex flex-col items-center gap-1 min-w-[66px]">
        <button
          type="button"
          onClick={() => handleJoinSeat(seatIdx)}
          className="relative group cursor-pointer focus:outline-none"
          aria-label={occupant ? `${occupant} mic seat` : `Join seat ${seatIdx + 1}`}
        >
          {occupant ? (
            <>
              <div
                className={`${ringSize} rounded-full p-[2.5px] transition-transform group-hover:scale-105 ${
                  isSpeaking
                    ? 'bg-gradient-to-tr from-[#38BDF8] via-[#A855F7] to-[#EC4899] shadow-[0_0_14px_rgba(168,85,247,0.45)]'
                    : 'bg-white/25'
                }`}
              >
                <div className="w-full h-full rounded-full bg-gradient-to-br from-[#6D28D9] to-[#BE185D] flex items-center justify-center text-white font-extrabold text-lg">
                  {occupant.slice(0, 1).toUpperCase()}
                </div>
              </div>
              <span
                className={`absolute -bottom-0.5 right-0 w-5 h-5 rounded-full flex items-center justify-center border border-[#140E26] ${
                  isSpeaking ? 'bg-[#38BDF8] text-[#0F172A]' : 'bg-[#1E1930] text-white/80'
                }`}
              >
                {isSpeaking ? <Mic className="w-2.5 h-2.5" /> : <MicOff className="w-2.5 h-2.5" />}
              </span>
            </>
          ) : (
            <div
              className={`${ringSize} rounded-full bg-[#241E3E]/65 hover:bg-[#312954]/80 border border-white/15 flex items-center justify-center text-white/75 transition-colors`}
            >
              <Armchair className={isTopRow ? 'w-6 h-6' : 'w-5 h-5'} />
            </div>
          )}
        </button>

        {occupant ? (
          <div className="flex flex-col items-center gap-0.5 max-w-[78px]">
            <div className="flex items-center gap-1 text-[11px] font-semibold text-white truncate max-w-full">
              {isHost && (
                <span className="inline-flex items-center justify-center w-3.5 h-3.5 rounded-sm bg-[#8B5CF6] text-[9px] shrink-0">
                  <Flag className="w-2.5 h-2.5 text-white" />
                </span>
              )}
              <span className={`truncate ${isMe ? 'text-[#FFD77A]' : 'text-white'}`}>{occupant}</span>
            </div>
            <button
              type="button"
              onClick={() => setSeatHearts((prev) => prev.map((h, i) => (i === seatIdx ? h + 1 : h)))}
              className="px-2 py-0.5 rounded-full bg-[#3A2848]/85 hover:bg-[#4F3562] text-[10px] font-bold text-[#F9A8D4] flex items-center gap-1 cursor-pointer tabular-nums"
            >
              <Heart className="w-2.5 h-2.5 fill-[#EC4899] text-[#EC4899]" />
              <span>{seatHearts[seatIdx]}</span>
            </button>
          </div>
        ) : (
          <button
            type="button"
            onClick={() => handleJoinSeat(seatIdx)}
            className="px-3.5 py-0.5 rounded-full bg-white/15 hover:bg-white/25 text-white/90 text-[11px] font-semibold cursor-pointer transition-colors"
          >
            Join
          </button>
        )}
      </div>
    );
  };

  return (
    <div
      className="min-h-screen text-white flex flex-col relative overflow-hidden"
      style={{ backgroundColor: '#120C24', backgroundImage: bgGradient }}
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
            ? 'bg-gradient-to-b from-[#110B22]/80 via-[#150E2A]/65 to-[#0D0919]/92'
            : 'bg-radial from-[#4C1D95]/20 via-[#150E2A]/60 to-[#0B0815]/90'
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

      {/* Top Room Header */}
      <header className="relative z-20 px-3 pt-3 pb-2 flex items-center gap-2">
        <button
          type="button"
          onClick={onLeave}
          aria-label="Back"
          className="w-10 h-10 rounded-full flex items-center justify-center hover:bg-white/10 cursor-pointer shrink-0"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>

        <div className="flex-1 min-w-0">
          {editingRoomTitle ? (
            <form
              onSubmit={(e) => {
                e.preventDefault();
                setEditingRoomTitle(false);
              }}
              className="flex items-center gap-1.5"
            >
              <input
                type="text"
                value={roomTitleText}
                onChange={(e) => setRoomTitleText(e.target.value)}
                onBlur={() => setEditingRoomTitle(false)}
                autoFocus
                className="flex-1 min-w-0 bg-white/15 border border-white/25 rounded-lg px-2 py-0.5 text-sm font-bold text-white focus:outline-none"
              />
            </form>
          ) : (
            <button
              type="button"
              onClick={() => setEditingRoomTitle(true)}
              className="flex items-center gap-1.5 text-left max-w-full cursor-pointer group"
            >
              <Pencil className="w-3.5 h-3.5 text-white/75 shrink-0 group-hover:text-white" />
              <h1 className="font-bold text-[15px] text-white truncate">{roomTitleText || room.title}</h1>
            </button>
          )}
          <div className="flex items-center gap-1.5 mt-0.5 flex-wrap">
            <span className="text-[11px] text-white/65 tabular-nums">ID: {numericRoomId}</span>
            <button
              type="button"
              onClick={() => {
                void navigator.clipboard?.writeText(numericRoomId);
                setCopiedRoomId(true);
                window.setTimeout(() => setCopiedRoomId(false), 1500);
              }}
              aria-label="Copy room ID"
              className="text-white/60 hover:text-white cursor-pointer inline-flex items-center gap-1"
            >
              <Copy className="w-3 h-3" />
              {copiedRoomId && <span className="text-[10px] text-[#70D9FF]">Copied</span>}
            </button>
            {isPartyLocked && (
              <span className="inline-flex items-center gap-1 px-1.5 py-0.5 rounded bg-white/15 text-[10px] font-semibold text-[#FFD77A]">
                <Lock className="w-2.5 h-2.5" /> Locked
              </span>
            )}
            {partyMode !== 'Free Mic' && (
              <span className="px-1.5 py-0.5 rounded bg-[#8B5CF6]/40 text-[10px] font-semibold text-white">
                {partyMode}
              </span>
            )}
          </div>
        </div>

        <button
          type="button"
          onClick={() => setShowOffMicModal((v) => !v)}
          aria-label="Off-mic users"
          className="w-9 h-9 rounded-full bg-white/12 hover:bg-white/20 text-white text-xs font-bold flex items-center justify-center cursor-pointer tabular-nums shrink-0"
        >
          {offMicUsers.length}
        </button>

        <div className="relative shrink-0">
          <button
            type="button"
            onClick={() => setShowRoomMenu((v) => !v)}
            aria-label="Room options"
            className="w-9 h-9 rounded-full bg-white/12 hover:bg-white/20 text-white flex items-center justify-center cursor-pointer"
          >
            <MoreHorizontal className="w-4 h-4" />
          </button>
          {showRoomMenu && (
            <>
              <div
                onClick={() => setShowRoomMenu(false)}
                className="fixed inset-0 z-30"
              />
              <div className="absolute right-0 mt-2 w-56 rounded-2xl bg-white text-[#1E1C28] shadow-2xl py-2 z-40 text-[15px] font-medium">
                <button
                  type="button"
                  onClick={() => {
                    setShowRoomMenu(false);
                    setShowInviteModal(true);
                  }}
                  className="w-full px-4 py-3 flex items-center gap-3.5 hover:bg-[#F4F3F8] text-left cursor-pointer transition-colors"
                >
                  <UserPlus className="w-5 h-5 text-[#2D2A3A] shrink-0" strokeWidth={1.8} />
                  <span>Invite Friend</span>
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowRoomMenu(false);
                    setShowPartyModeModal(true);
                  }}
                  className="w-full px-4 py-3 flex items-center gap-3.5 hover:bg-[#F4F3F8] text-left cursor-pointer transition-colors"
                >
                  <LayoutGrid className="w-5 h-5 text-[#2D2A3A] shrink-0" strokeWidth={1.8} />
                  <span>Party Mode</span>
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowRoomMenu(false);
                    setShowBgDrawer(true);
                  }}
                  className="w-full px-4 py-3 flex items-center gap-3.5 hover:bg-[#F4F3F8] text-left cursor-pointer transition-colors"
                >
                  <Wand2 className="w-5 h-5 text-[#2D2A3A] shrink-0" strokeWidth={1.8} />
                  <span>Decoration</span>
                </button>
                <button
                  type="button"
                  onClick={() => {
                    const nextLocked = !isPartyLocked;
                    setIsPartyLocked(nextLocked);
                    setShowRoomMenu(false);
                    setRoomChat((prev) => [
                      ...prev,
                      {
                        sender: 'System',
                        text: nextLocked ? '🔒 Party locked by host.' : '🔓 Party unlocked.',
                        isSystem: true,
                      },
                    ]);
                  }}
                  className="w-full px-4 py-3 flex items-center gap-3.5 hover:bg-[#F4F3F8] text-left cursor-pointer transition-colors"
                >
                  {isPartyLocked ? (
                    <Unlock className="w-5 h-5 text-[#8B5CF6] shrink-0" strokeWidth={1.8} />
                  ) : (
                    <Lock className="w-5 h-5 text-[#2D2A3A] shrink-0" strokeWidth={1.8} />
                  )}
                  <span>{isPartyLocked ? 'Unlock Party' : 'Lock Party'}</span>
                </button>
                <button
                  type="button"
                  onClick={() => {
                    const nextBlocked = !blockAnimations;
                    setBlockAnimations(nextBlocked);
                    setShowRoomMenu(false);
                    if (nextBlocked) setGiftBanner(null);
                    setRoomChat((prev) => [
                      ...prev,
                      {
                        sender: 'System',
                        text: nextBlocked ? '🚫 Gift & entry animations blocked.' : '✨ Animations enabled.',
                        isSystem: true,
                      },
                    ]);
                  }}
                  className="w-full px-4 py-3 flex items-center gap-3.5 hover:bg-[#F4F3F8] text-left cursor-pointer transition-colors"
                >
                  {blockAnimations ? (
                    <Eye className="w-5 h-5 text-[#8B5CF6] shrink-0" strokeWidth={1.8} />
                  ) : (
                    <EyeOff className="w-5 h-5 text-[#2D2A3A] shrink-0" strokeWidth={1.8} />
                  )}
                  <span>{blockAnimations ? 'Allow Animation' : 'Block Animation'}</span>
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowOffMicModal((v) => !v);
                    setShowRoomMenu(false);
                  }}
                  className="w-full px-4 py-3 flex items-center gap-3.5 hover:bg-[#F4F3F8] text-left cursor-pointer transition-colors"
                >
                  {showOffMicModal ? (
                    <MicOff className="w-5 h-5 text-[#2D2A3A] shrink-0" strokeWidth={1.8} />
                  ) : (
                    <Mic className="w-5 h-5 text-[#8B5CF6] shrink-0" strokeWidth={1.8} />
                  )}
                  <span>{showOffMicModal ? 'Close Off-mic' : 'Open Off-mic'}</span>
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowRoomMenu(false);
                    onLeave();
                  }}
                  className="w-full px-4 py-3 flex items-center gap-3.5 hover:bg-[#FFF1F5] text-[#1E1C28] text-left cursor-pointer transition-colors"
                >
                  <LogOut className="w-5 h-5 text-[#2D2A3A] shrink-0" strokeWidth={1.8} />
                  <span>Exit</span>
                </button>
              </div>
            </>
          )}
        </div>
      </header>

      {/* Heat, Chest & Quick Music Ribbon */}
      <div className="relative z-10 px-3 pb-2 flex items-center justify-between gap-2">
        <div className="flex items-center gap-2 min-w-0">
          <div className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-gradient-to-r from-[#D97706]/35 to-[#92400E]/25 border border-[#F59E0B]/35 text-[11px] font-bold text-[#FDE68A] tabular-nums shrink-0">
            <Crown className="w-3.5 h-3.5 text-[#FBBF24] fill-[#FBBF24]" />
            <span>{heatDisplay}</span>
          </div>
          <button
            type="button"
            onClick={() => setShowPartyChallenge(true)}
            className="inline-flex items-center gap-2 px-2.5 py-1 rounded-full bg-black/35 border border-white/10 cursor-pointer shrink-0"
            title="Party Challenge - Level 1 to 10"
          >
            <span className="text-sm leading-none">🧰</span>
            <span className="w-12 h-2 rounded-full bg-white/15 overflow-hidden">
              <span
                className="block h-full rounded-full bg-gradient-to-r from-[#F59E0B] to-[#FBBF24]"
                style={{ width: `${chestPct}%` }}
              />
            </span>
          </button>
        </div>

        <div className="flex items-center gap-1.5 shrink-0">
          <button
            type="button"
            onClick={() => setShowMusicDrawer(true)}
            className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-black/35 hover:bg-black/50 border border-white/12 text-[11px] font-semibold text-white/90 cursor-pointer max-w-[140px]"
          >
            <Disc3 className={`w-3.5 h-3.5 text-[#70D9FF] shrink-0 ${isMusicPlaying ? 'party-disc--playing' : ''}`} />
            <span className="truncate">{isMusicPlaying ? currentTrack.title : 'Music'}</span>
          </button>
          <button
            type="button"
            onClick={() => setShowBgDrawer(true)}
            aria-label="Room heat and atmosphere"
            className="relative w-8 h-8 rounded-full bg-black/35 hover:bg-black/50 border border-white/10 flex items-center justify-center cursor-pointer"
          >
            <Flame className="w-4 h-4 text-[#FBBF24] fill-[#F59E0B]" />
            <span className="absolute top-1 right-1 w-2 h-2 rounded-full bg-[#EF4444]" />
          </button>
        </div>
      </div>

      {/* Main Stage + Chat Area */}
      <div className="relative z-10 flex-1 max-w-xl w-full mx-auto px-3 pb-2 flex flex-col justify-between gap-3">
        {!blockAnimations && giftBanner && (
          <div className="rounded-xl bg-gradient-to-r from-[#68468E]/90 to-[#3F2B63]/90 backdrop-blur-md px-3.5 py-2 flex items-center justify-between text-xs font-bold text-[#FFD77A]">
            <span>{giftBanner}</span>
            <button type="button" onClick={() => setGiftBanner(null)} className="text-white/70 cursor-pointer">
              <X className="w-4 h-4" />
            </button>
          </div>
        )}

        {/* 10-Seat Interactive Stage (2 + 4 + 4) */}
        <div className="pt-1 pb-2 space-y-4">
          <div className="flex items-start justify-center gap-8">
            {[0, 1].map((idx) => renderSeatTile(idx, true))}
          </div>
          <div className="grid grid-cols-4 gap-2 justify-items-center">
            {[2, 3, 4, 5].map((idx) => renderSeatTile(idx, false))}
          </div>
          <div className="grid grid-cols-4 gap-2 justify-items-center">
            {[6, 7, 8, 9].map((idx) => renderSeatTile(idx, false))}
          </div>
        </div>

        {/* Floating Off-Mic Users Overlay */}
        {showOffMicModal && (
          <div className="rounded-2xl bg-[#090711]/92 backdrop-blur-lg border border-white/12 p-4 shadow-2xl">
            <div className="relative flex items-center justify-center mb-3">
              <h2 className="text-sm font-bold text-white">Off-mic Users({offMicUsers.length})</h2>
              <button
                type="button"
                onClick={() => setShowOffMicModal(false)}
                aria-label="Close off-mic users"
                className="absolute right-0 w-7 h-7 rounded-full flex items-center justify-center text-white/75 hover:text-white cursor-pointer"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
            {offMicUsers.length === 0 ? (
              <p className="text-center text-xs text-white/75 py-5">
                No off-mic users yet, gather some friends here~
              </p>
            ) : (
              <div className="flex flex-wrap gap-2 justify-center py-2">
                {offMicUsers.map((person) => (
                  <div
                    key={person}
                    className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-white/10 text-xs font-semibold text-white"
                  >
                    <AvatarBadge name={person} size="xs" />
                    <span>{person}</span>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Chat Stream, Welcome Wording & Party Tips + Right Floating Action Buttons */}
        <div className="relative flex-1 flex flex-col justify-end min-h-[180px]">
          <div className="pr-14 space-y-2 max-h-60 overflow-y-auto">
            {/* Automatic Welcome Wording */}
            <div className="rounded-xl bg-black/45 border border-white/10 px-3 py-2">
              <div className="flex items-center justify-between gap-2">
                <span className="text-xs font-semibold text-[#FDE68A]">Automatic Welcome Wording</span>
                <button
                  type="button"
                  onClick={() => setEditingWelcome((v) => !v)}
                  aria-label="Edit welcome wording"
                  className="text-white/70 hover:text-white cursor-pointer"
                >
                  <SquarePen className="w-3.5 h-3.5" />
                </button>
              </div>
              {editingWelcome ? (
                <form
                  onSubmit={(e) => {
                    e.preventDefault();
                    setEditingWelcome(false);
                  }}
                  className="mt-1.5 flex items-center gap-1.5"
                >
                  <input
                    type="text"
                    value={welcomeWording}
                    onChange={(e) => setWelcomeWording(e.target.value)}
                    placeholder="Enter welcome message..."
                    autoFocus
                    className="flex-1 bg-white/10 border border-white/20 rounded-lg px-2.5 py-1 text-xs text-white focus:outline-none"
                  />
                  <button
                    type="submit"
                    className="px-2.5 py-1 rounded-lg bg-[#8B5CF6] text-white text-[11px] font-bold cursor-pointer"
                  >
                    Save
                  </button>
                </form>
              ) : (
                <button
                  type="button"
                  onClick={() => setEditingWelcome(true)}
                  className="mt-0.5 text-left text-xs text-white/85 hover:text-white cursor-pointer block w-full"
                >
                  {welcomeWording}
                </button>
              )}
            </div>

            {/* Party Tips Card */}
            <div className="rounded-xl bg-black/45 border border-white/10 px-3 py-2 text-xs leading-relaxed">
              <div className="font-bold text-[#38BDF8]">Party Tips:</div>
              <p className="text-[#38BDF8]/90">
                Tell you a secret about using room heating - after heating, the more people chatting on the mic in your room and the more attention you receive, the room will have the opportunity to get more traffic in the future:)
              </p>
            </div>

            {/* Room Messages */}
            {roomChat.map((entry, idx) =>
              entry.isSystem ? (
                <div
                  key={idx}
                  className="inline-flex items-center gap-1.5 rounded-xl bg-black/50 border border-white/10 px-3 py-1.5 text-xs"
                >
                  <span className="text-[#93C5FD] font-bold">{entry.sender}</span>
                  <span className="text-white/90">{entry.text}</span>
                </div>
              ) : (
                <div
                  key={idx}
                  className="rounded-xl bg-black/45 border border-white/10 px-3 py-1.5 text-xs"
                >
                  <span className="text-[#FF7EB6] font-bold">{entry.sender}: </span>
                  <span className="text-white/95">{entry.text}</span>
                </div>
              ),
            )}

            <div className="rounded-xl bg-black/40 border border-white/10 px-3 py-1.5 text-xs">
              <div className="font-bold text-[#38BDF8]">Party Tips:</div>
              <div className="text-[#38BDF8]/90">Please wear headphones to avoid echoes.</div>
            </div>
          </div>

          {/* Floating Right Action Pills */}
          <div className="absolute right-0 bottom-1 flex flex-col items-center gap-2.5">
            {lastReaction && (
              <div className="px-2.5 py-1 rounded-full bg-black/60 border border-white/15 text-xs font-bold">
                {lastReaction}
              </div>
            )}
            <button
              type="button"
              onClick={handleToggleQuickMicSeat}
              aria-label={onStage ? 'Leave mic seat' : 'Join open mic seat'}
              className={`w-11 h-11 rounded-full flex items-center justify-center border shadow-lg cursor-pointer transition-colors ${
                onStage
                  ? 'bg-[#38BDF8] text-[#0F172A] border-white/40'
                  : 'bg-[#16192C]/90 text-[#38BDF8] border-white/20 hover:bg-[#1E2540]'
              }`}
            >
              <Armchair className="w-5 h-5" />
            </button>
            <button
              type="button"
              onClick={() => setShowMusicDrawer(true)}
              aria-label="Open Party Music Jukebox"
              className="w-11 h-11 rounded-full bg-gradient-to-br from-[#F59E0B] via-[#EC4899] to-[#8B5CF6] p-[2px] shadow-lg cursor-pointer"
            >
              <div className="w-full h-full rounded-full bg-[#1C1433] flex flex-col items-center justify-center">
                <Disc3 className={`w-4 h-4 text-[#FFD77A] ${isMusicPlaying ? 'party-disc--playing' : ''}`} />
                <span className="text-[7px] font-extrabold text-white leading-none mt-0.5">MUSIC</span>
              </div>
            </button>
          </div>
        </div>
      </div>

      {/* Quick Emoji Reaction Strip */}
      {showReactionTray && (
        <div className="relative z-20 px-4 pb-2 flex justify-center">
          <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-[#1E1638]/95 border border-white/15 shadow-xl">
            {['❤️', '👏', '🔥', '😂', '🎉'].map((emoji) => (
              <button
                key={emoji}
                type="button"
                onClick={() => {
                  setLastReaction(emoji);
                  setRoomChat((prev) => [...prev, { sender: 'You', text: `Sent ${emoji}` }]);
                  setShowReactionTray(false);
                }}
                className="w-9 h-9 rounded-full hover:bg-white/15 flex items-center justify-center text-lg cursor-pointer"
              >
                {emoji}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Sleek Bottom Action Bar matching screenshot */}
      <div className="relative z-20 bg-[#0D0919]/85 backdrop-blur-md border-t border-white/10 px-3 py-2.5 sticky bottom-0">
        <div className="max-w-xl mx-auto flex items-center gap-2">
          <button
            type="button"
            onClick={() => {
              if (!isMusicPlaying) {
                toggleMusicPlayback();
              } else {
                setIsMusicMuted((m) => !m);
              }
            }}
            aria-label={isMusicMuted || !isMusicPlaying ? 'Turn room audio on' : 'Mute room audio'}
            className="w-10 h-10 rounded-full bg-white/12 hover:bg-white/20 text-white flex items-center justify-center cursor-pointer shrink-0"
          >
            {isMusicMuted || !isMusicPlaying ? <VolumeX className="w-4 h-4" /> : <Volume2 className="w-4 h-4 text-[#70D9FF]" />}
          </button>

          <button
            type="button"
            onClick={() => {
              if (!onStage) {
                handleToggleQuickMicSeat();
              } else {
                setIsMuted((m) => !m);
              }
            }}
            aria-label={onStage && !isMuted ? 'Mute microphone' : 'Unmute microphone'}
            className={`w-10 h-10 rounded-full flex items-center justify-center cursor-pointer shrink-0 ${
              onStage && !isMuted ? 'bg-[#8B5CF6] text-white' : 'bg-white/12 hover:bg-white/20 text-white'
            }`}
          >
            {onStage && !isMuted ? <Mic className="w-4 h-4" /> : <MicOff className="w-4 h-4" />}
          </button>

          <form
            onSubmit={(e) => {
              e.preventDefault();
              if (!roomInput.trim()) return;
              setRoomChat((prev) => [...prev, { sender: 'You', text: roomInput.trim() }]);
              setRoomInput('');
            }}
            className="flex-1 min-w-0 flex items-center bg-white/12 border border-white/15 rounded-full h-10 px-3.5"
          >
            <input
              type="text"
              value={roomInput}
              onChange={(e) => setRoomInput(e.target.value)}
              placeholder="Enter"
              className="flex-1 min-w-0 bg-transparent text-xs text-white placeholder:text-white/55 focus:outline-none"
            />
            {roomInput.trim().length > 0 && (
              <button
                type="submit"
                aria-label="Send message"
                className="text-[#70D9FF] hover:text-white ml-1.5 cursor-pointer"
              >
                <Send className="w-3.5 h-3.5" />
              </button>
            )}
          </form>

          <button
            type="button"
            onClick={() => setShowReactionTray((v) => !v)}
            aria-label="Reactions"
            className="w-10 h-10 rounded-full bg-white/12 hover:bg-white/20 text-white flex items-center justify-center cursor-pointer shrink-0"
          >
            <Smile className="w-5 h-5" />
          </button>

          <button
            type="button"
            onClick={() => setShowMusicDrawer(true)}
            aria-label="Open Party Music"
            className="w-10 h-10 rounded-full bg-[#3D2520] border border-[#F59E0B]/40 text-[#FBBF24] flex items-center justify-center cursor-pointer shrink-0"
          >
            <Music className="w-4 h-4" />
          </button>

          <button
            type="button"
            onClick={() => setShowGiftDrawer(true)}
            aria-label="Send Party Gift"
            className="w-10 h-10 rounded-full bg-gradient-to-br from-[#F472B6] to-[#8B5CF6] text-white flex items-center justify-center cursor-pointer shrink-0 shadow"
          >
            <Gift className="w-4 h-4" />
          </button>

          <button
            type="button"
            onClick={() => setShowBgDrawer(true)}
            aria-label="Room Background & Settings"
            className="w-10 h-10 rounded-full bg-white/12 hover:bg-white/20 text-white flex items-center justify-center cursor-pointer shrink-0"
          >
            <Menu className="w-4 h-4" />
          </button>
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

      {showInviteModal && (
        <div
          onClick={() => setShowInviteModal(false)}
          className="fixed inset-0 z-50 bg-[#0A0712]/70 flex items-center justify-center p-4"
        >
          <div
            onClick={(e) => e.stopPropagation()}
            className="w-full max-w-sm rounded-3xl bg-[#222128] border border-white/10 p-5 space-y-3.5 shadow-2xl"
          >
            <div className="flex items-center justify-between">
              <h2 className="text-base font-extrabold text-white">Invite Friend to Party</h2>
              <button
                type="button"
                onClick={() => setShowInviteModal(false)}
                className="w-8 h-8 rounded-full bg-white/10 text-white flex items-center justify-center cursor-pointer"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
            <div className="space-y-2">
              {['Mina', 'Dara', 'Sophy', 'Alex', 'Nita'].map((friendName) => {
                const invited = invitedFriends.includes(friendName);
                return (
                  <div
                    key={friendName}
                    className="flex items-center justify-between rounded-2xl bg-[#2B2A34] px-3.5 py-2.5"
                  >
                    <div className="flex items-center gap-2.5">
                      <AvatarBadge name={friendName} size="sm" />
                      <span className="text-sm font-semibold text-white">{friendName}</span>
                    </div>
                    <button
                      type="button"
                      disabled={invited}
                      onClick={() => {
                        setInvitedFriends((prev) => [...prev, friendName]);
                        setRoomChat((prev) => [
                          ...prev,
                          { sender: 'You', text: `📨 Invited ${friendName} to join the room!` },
                        ]);
                      }}
                      className={`px-3 py-1.5 rounded-full text-xs font-bold cursor-pointer ${
                        invited ? 'bg-white/10 text-[#9E9BB0]' : 'bg-[#8B5CF6] text-white'
                      }`}
                    >
                      {invited ? 'Invited' : 'Invite'}
                    </button>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      )}

      {showPartyModeModal && (
        <div
          onClick={() => setShowPartyModeModal(false)}
          className="fixed inset-0 z-50 bg-[#0A0712]/70 flex items-center justify-center p-4"
        >
          <div
            onClick={(e) => e.stopPropagation()}
            className="w-full max-w-sm rounded-3xl bg-[#222128] border border-white/10 p-5 space-y-3.5 shadow-2xl"
          >
            <div className="flex items-center justify-between">
              <h2 className="text-base font-extrabold text-white">Choose Party Mode</h2>
              <button
                type="button"
                onClick={() => setShowPartyModeModal(false)}
                className="w-8 h-8 rounded-full bg-white/10 text-white flex items-center justify-center cursor-pointer"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
            <div className="grid grid-cols-2 gap-2.5">
              {(['Free Mic', 'Music Lounge', 'Karaoke', 'Truth or Dare'] as const).map((modeOption) => {
                const active = partyMode === modeOption;
                return (
                  <button
                    key={modeOption}
                    type="button"
                    onClick={() => {
                      setPartyMode(modeOption);
                      setRoomChat((prev) => [
                        ...prev,
                        { sender: 'System', text: `🎛️ Party Mode set to ${modeOption}`, isSystem: true },
                      ]);
                      setShowPartyModeModal(false);
                    }}
                    className={`rounded-2xl p-3 text-left border cursor-pointer transition-colors ${
                      active
                        ? 'bg-[#8B5CF6]/25 border-[#8B5CF6] text-white'
                        : 'bg-[#2B2A34] border-white/5 text-[#CBBEE6] hover:text-white'
                    }`}
                  >
                    <div className="text-sm font-bold">{modeOption}</div>
                    <div className="text-[11px] text-[#9E9BB0] mt-0.5">
                      {active ? 'Active mode' : 'Tap to switch'}
                    </div>
                  </button>
                );
              })}
            </div>
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
  userStatusText,
  userOccupation,
  userInterests,
  profilePhoto,
  coverPhoto,
  diamonds,
  giftHistory,
  currentFamily,
  friendsCount,
  friendRequestCount,
  rooms,
  posts,
  onOpenFriends,
  onOpenFamily,
  onOpenShop,
  onOpenGame,
  onOpenAddMusic,
  onOpenStatusModal,
  onOpenCompose,
  onJoinMyRoom,
  onSave,
}: {
  displayName: string;
  bio: string;
  userStatusText: string;
  userOccupation: string;
  userInterests: string[];
  profilePhoto: string | null;
  coverPhoto: string | null;
  diamonds: number;
  giftHistory: GiftRecord[];
  currentFamily: FamilyClub | null;
  friendsCount: number;
  friendRequestCount: number;
  rooms: VoiceRoom[];
  posts: MomentPost[];
  onOpenFriends: () => void;
  onOpenFamily: () => void;
  onOpenShop: () => void;
  onOpenGame: () => void;
  onOpenAddMusic: () => void;
  onOpenStatusModal: () => void;
  onOpenCompose: () => void;
  onJoinMyRoom: () => void;
  onSave: (next: {
    displayName: string;
    bio: string;
    occupation: string;
    interests: string[];
    profilePhoto: string | null;
    coverPhoto: string | null;
  }) => void;
}) {
  const [name, setName] = useState(displayName);
  const [about, setAbout] = useState(bio);
  const [occupation, setOccupation] = useState(userOccupation);
  const [interestsInput, setInterestsInput] = useState(userInterests.join(', '));
  const [avatar, setAvatar] = useState(profilePhoto);
  const [cover, setCover] = useState(coverPhoto);
  const [showEditModal, setShowEditModal] = useState(false);
  const [savedToast, setSavedToast] = useState(false);
  const [copiedId, setCopiedId] = useState(false);
  const [showInspiration, setShowInspiration] = useState(true);
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
  const myRoom = rooms[0];
  const pinnedPost = posts[0];

  const handleCopyId = () => {
    navigator.clipboard?.writeText('7146551484').catch(() => {});
    setCopiedId(true);
    window.setTimeout(() => setCopiedId(false), 1800);
  };

  return (
    <div className="pb-28 min-h-full bg-[#16151B] text-white relative">
      {/* Cover Header & Top Action Bar */}
      <div className="relative h-44 sm:h-52 w-full overflow-hidden">
        {cover ? (
          <img src={cover} alt="Cover" className="w-full h-full object-cover" />
        ) : (
          <div
            className="w-full h-full"
            style={{ background: 'linear-gradient(135deg, #D97706 0%, #7C3AED 55%, #1E1B4B 100%)' }}
          />
        )}
        <div className="absolute inset-0 bg-gradient-to-b from-black/35 via-transparent to-[#16151B]" />

        <div className="absolute top-3 left-3 right-3 flex items-center justify-between gap-2">
          <button
            type="button"
            onClick={onOpenShop}
            className="min-h-[34px] px-3 rounded-full bg-black/45 backdrop-blur-md border border-white/15 flex items-center gap-1.5 text-xs font-bold text-white cursor-pointer hover:bg-black/65 transition-colors"
          >
            <span className="text-[#38BDF8]">💎</span>
            <span className="tabular-nums">Recharge ({diamonds})</span>
            <span className="w-4 h-4 rounded-full bg-[#38BDF8] text-[#16151B] flex items-center justify-center text-[11px] font-extrabold">
              +
            </span>
          </button>

          <div className="flex items-center gap-1.5">
            <button
              type="button"
              onClick={onOpenGame}
              aria-label="Human Battle Game"
              className="w-9 h-9 rounded-full bg-black/45 backdrop-blur-md border border-white/10 flex items-center justify-center text-white cursor-pointer hover:bg-black/65"
            >
              <Gamepad2 className="w-4 h-4" />
            </button>
            <button
              type="button"
              onClick={onOpenFamily}
              aria-label="Family Club"
              className="w-9 h-9 rounded-full bg-black/45 backdrop-blur-md border border-white/10 flex items-center justify-center text-white cursor-pointer hover:bg-black/65"
            >
              <Crown className="w-4 h-4" />
            </button>
            <button
              type="button"
              onClick={onOpenFriends}
              aria-label="Friends"
              className="relative w-9 h-9 rounded-full bg-black/45 backdrop-blur-md border border-white/10 flex items-center justify-center text-white cursor-pointer hover:bg-black/65"
            >
              <Star className="w-4 h-4" />
              {friendRequestCount > 0 && (
                <span className="absolute -top-0.5 -right-0.5 w-2.5 h-2.5 rounded-full bg-[#FF4D6D] ring-2 ring-[#16151B]" />
              )}
            </button>
            <button
              type="button"
              onClick={onOpenAddMusic}
              aria-label="Music Library"
              className="w-9 h-9 rounded-full bg-black/45 backdrop-blur-md border border-white/10 flex items-center justify-center text-white cursor-pointer hover:bg-black/65"
            >
              <Headphones className="w-4 h-4" />
            </button>
            <button
              type="button"
              onClick={() => setShowEditModal(true)}
              aria-label="Edit Profile"
              className="w-9 h-9 rounded-full bg-black/45 backdrop-blur-md border border-white/10 flex items-center justify-center text-white cursor-pointer hover:bg-black/65"
            >
              <Settings className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      {/* Overlapping Avatar + Soulmate CP Pill */}
      <div className="px-4 -mt-10 relative z-10 flex items-end justify-between gap-3">
        <div className="flex items-end gap-3">
          <label className="relative w-20 h-20 rounded-full overflow-hidden ring-4 ring-[#16151B] cursor-pointer shrink-0 bg-[#DC2626] flex items-center justify-center">
            <input
              type="file"
              accept="image/*"
              className="hidden"
              onChange={(e) =>
                pickFile(e, (nextImg) => {
                  setAvatar(nextImg);
                  onSave({
                    displayName: name,
                    bio: about,
                    occupation,
                    interests: userInterests,
                    profilePhoto: nextImg,
                    coverPhoto: cover,
                  });
                })
              }
            />
            {avatar ? (
              <img src={avatar} alt={displayName} className="w-full h-full object-cover" />
            ) : (
              <Camera className="w-8 h-8 text-white/90" />
            )}
          </label>

          {/* Soulmate CP Widget */}
          <button
            type="button"
            onClick={onOpenFriends}
            className="mb-1 px-3 py-1.5 rounded-full bg-[#2B2540] border border-[#8B5CF6]/30 flex items-center gap-2 cursor-pointer hover:bg-[#352E4E] transition-colors"
          >
            <span className="text-base">💍</span>
            <AvatarBadge name="Mina" size="xs" />
            <span className="text-xs font-extrabold text-[#C4B5FD] tabular-nums">30.4K</span>
          </button>
        </div>

        <button
          type="button"
          onClick={() => setShowEditModal(true)}
          className="mb-1 px-3.5 py-1.5 rounded-full bg-[#2B2A34] hover:bg-[#383645] text-white text-xs font-bold flex items-center gap-1.5 cursor-pointer"
        >
          <Pencil className="w-3.5 h-3.5" />
          <span>Edit</span>
        </button>
      </div>

      {/* User Identity */}
      <div className="px-4 mt-3 space-y-2">
        <div className="flex items-center gap-2 flex-wrap">
          <h1 className="text-xl font-extrabold text-white">{displayName}</h1>
          <span className="px-2 py-0.5 rounded-full bg-[#38BDF8]/20 text-[#60A5FA] text-[11px] font-bold">
            ♂ 22
          </span>
          {savedToast && <span className="text-xs font-bold text-[#34D399]">Saved ✓</span>}
        </div>

        <div className="flex items-center gap-2 text-xs text-white/55">
          <span className="tabular-nums">ID: 7146551484</span>
          <button
            type="button"
            onClick={handleCopyId}
            aria-label="Copy ID"
            className="text-white/60 hover:text-white inline-flex items-center gap-1 cursor-pointer"
          >
            {copiedId ? <Check className="w-3.5 h-3.5 text-[#34D399]" /> : <Copy className="w-3.5 h-3.5" />}
            {copiedId && <span className="text-[11px] text-[#34D399]">Copied</span>}
          </button>
        </div>

        <div className="flex items-center gap-2 flex-wrap">
          <button
            type="button"
            onClick={onOpenStatusModal}
            className="px-3 py-1 rounded-full bg-[#2B2540] hover:bg-[#3A3156] text-[#C4B5FD] text-xs font-bold inline-flex items-center gap-1 cursor-pointer"
          >
            <Plus className="w-3.5 h-3.5" />
            <span>{userStatusText ? userStatusText : 'Status'}</span>
          </button>
        </div>

        <p className="text-sm font-semibold text-[#C4B5FD] pt-0.5">{bio}</p>

        <div className="flex items-center gap-2 text-xs text-white/80">
          <span>{userOccupation}</span>
          <button
            type="button"
            onClick={() => setShowEditModal(true)}
            aria-label="Edit occupation"
            className="text-white/50 hover:text-white cursor-pointer"
          >
            <Pencil className="w-3.5 h-3.5" />
          </button>
        </div>

        <div className="flex items-center gap-1.5 text-xs text-white/50">
          <Calendar className="w-3.5 h-3.5" />
          <span>Joined March 2026</span>
        </div>

        <div className="flex items-center gap-2 flex-wrap pt-0.5">
          {userInterests.map((tag) => (
            <span
              key={tag}
              className="px-3 py-1 rounded-full bg-[#222128] border border-white/10 text-xs text-white/80"
            >
              {tag}
            </span>
          ))}
          <button
            type="button"
            onClick={() => setShowEditModal(true)}
            aria-label="Edit interests"
            className="w-7 h-7 rounded-full bg-[#222128] flex items-center justify-center text-white/55 hover:text-white cursor-pointer"
          >
            <Pencil className="w-3.5 h-3.5" />
          </button>
        </div>

        {/* Stats Row */}
        <div className="flex items-center gap-4 pt-2 text-xs text-white/60 flex-wrap tabular-nums">
          <button type="button" onClick={onOpenFriends} className="hover:text-white cursor-pointer">
            <strong className="text-white font-extrabold">{friendsCount + 10}</strong> Following
          </button>
          <span>
            <strong className="text-white font-extrabold">203</strong> Follower
          </span>
          <span>
            <strong className="text-white font-extrabold">5.82K</strong> Visited
          </span>
          <span>
            <strong className="text-white font-extrabold">{rooms.length}</strong> Rooms
          </span>
        </div>

        {/* My Voice Room Card */}
        {myRoom && (
          <button
            type="button"
            onClick={onJoinMyRoom}
            className="w-full mt-2 rounded-2xl bg-[#222128] hover:bg-[#2B2A34] border border-white/10 p-3.5 flex items-center gap-3 text-left cursor-pointer transition-colors"
          >
            <div className="w-11 h-11 rounded-full bg-[#2B2A34] flex items-center justify-center text-white/85 shrink-0">
              <Mic className="w-5 h-5" />
            </div>
            <div className="flex-1 min-w-0">
              <div className="text-sm font-bold text-white truncate">{myRoom.title}</div>
              <div className="text-[11px] text-white/55 flex items-center gap-1 tabular-nums mt-0.5">
                <BarChart2 className="w-3 h-3 text-[#8B5CF6]" />
                <span>{myRoom.listeners}</span>
              </div>
            </div>
            <ChevronRight className="w-4 h-4 text-white/45 shrink-0" />
          </button>
        )}

        {/* Inspiration Card */}
        {showInspiration && (
          <div className="pt-2 space-y-2">
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold text-white/60">Inspiration</span>
              <button
                type="button"
                onClick={() => setShowInspiration(false)}
                aria-label="Dismiss inspiration"
                className="text-white/45 hover:text-white cursor-pointer"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
            <div className="rounded-2xl bg-[#2B2540] border border-[#8B5CF6]/25 p-3.5 flex items-center gap-3">
              <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-[#7C3AED] to-[#EC4899] flex items-center justify-center text-2xl shrink-0">
                🏮
              </div>
              <div className="flex-1 min-w-0">
                <div className="text-sm font-bold text-white truncate">🎉 រីករាយពិធីបុណ្យអុំទូក!</div>
                <div className="text-[11px] text-white/70 truncate">
                  💜 ចែករំលែកពេលវេលាដ៏ស្រស់ស្អាតជាមួយ MY HAPPY PLACE
                </div>
              </div>
              <button
                type="button"
                onClick={onOpenCompose}
                className="px-3.5 py-2 rounded-full border border-[#8B5CF6] bg-[#16151B]/50 hover:bg-[#8B5CF6] text-white text-xs font-bold whitespace-nowrap cursor-pointer transition-colors shrink-0"
              >
                Post now ❤️
              </button>
            </div>
          </div>
        )}

        {/* Pinned Post Preview */}
        {pinnedPost && (
          <div className="pt-2 space-y-2">
            <div className="flex items-center justify-between text-xs text-white/60">
              <span>📌 Pinned · {pinnedPost.timeAgo}</span>
            </div>
            <div className="rounded-2xl bg-[#222128] border border-white/10 p-3.5 space-y-2">
              <p className="text-sm text-white/90">{pinnedPost.caption}</p>
              {pinnedPost.imageBg && (
                <div
                  className="w-full h-36 rounded-xl flex items-center justify-center text-4xl"
                  style={{ background: pinnedPost.imageBg }}
                >
                  {pinnedPost.imageEmoji ?? '✨'}
                </div>
              )}
            </div>
          </div>
        )}
      </div>

      {/* Friends & Family Cards */}
      <section className="px-4 mt-5 space-y-2.5">
        <button
          type="button"
          onClick={onOpenFriends}
          className="w-full rounded-2xl bg-[#222128] hover:bg-[#2B2A34] border border-white/5 transition-colors p-4 flex items-center gap-3 text-left cursor-pointer"
        >
          <div
            className="w-10 h-10 rounded-full flex items-center justify-center text-white shrink-0"
            style={{ background: 'linear-gradient(135deg, #FF7EB6 0%, #8B5CF6 100%)' }}
          >
            <UserPlus className="w-5 h-5" />
          </div>
          <div className="flex-1 min-w-0">
            <div className="font-bold text-sm text-white truncate">My Friends</div>
            <div className="text-xs text-white/55 truncate tabular-nums">
              {friendsCount} friends{friendRequestCount > 0 ? ` · ${friendRequestCount} requests` : ''}
            </div>
          </div>
          <span className="text-xs font-bold text-[#A78BFA]">Add friends</span>
        </button>

        <button
          type="button"
          onClick={onOpenFamily}
          className="w-full rounded-2xl bg-[#222128] hover:bg-[#2B2A34] border border-white/5 transition-colors p-4 flex items-center gap-3 text-left cursor-pointer"
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
            <div className="font-bold text-sm text-white truncate">
              {currentFamily?.name ?? 'No Family yet'}
            </div>
            <div className="text-xs text-white/55 truncate tabular-nums">
              {currentFamily
                ? `${myRole} · Level ${currentFamily.level} · ${currentFamily.members.length} members`
                : 'Tap to join or create a Family'}
            </div>
          </div>
          <span className="text-xs font-bold text-[#A78BFA]">Open</span>
        </button>
      </section>

      {/* Gift History Section */}
      <section className="px-4 mt-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-base font-bold text-white">Gift History</h2>
            <p className="text-[11px] text-white/50">Khmer traditional gifts sent &amp; received</p>
          </div>
          <span className="text-xs font-semibold text-[#A78BFA] tabular-nums">{filteredHistory.length} items</span>
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
                  active ? 'bg-[#8B5CF6] text-white' : 'bg-[#222128] text-white/75 hover:bg-[#2B2A34]'
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
              className="rounded-2xl bg-[#222128] border border-white/5 p-3 flex items-center gap-3"
            >
              <div className="w-11 h-11 rounded-xl bg-[#2B2A34] flex items-center justify-center text-2xl shrink-0">
                {item.emoji}
              </div>
              <div className="flex-1 min-w-0">
                <div className="font-bold text-sm text-white truncate">{item.giftName}</div>
                <div className="text-[11px] text-white/50 truncate">
                  {item.isSent ? `Sent to ${item.peerName}` : `Received from ${item.peerName}`} ·{' '}
                  {formatTimestamp(item.timestamp)}
                </div>
              </div>
              <div className="text-right shrink-0">
                <div
                  className={`text-xs font-bold tabular-nums ${
                    item.isSent ? 'text-[#FF7EB6]' : 'text-[#A78BFA]'
                  }`}
                >
                  {item.isSent ? `-💎 ${item.price}` : `+💎 ${item.price}`}
                </div>
                <div className="text-[10px] text-white/45">{item.isSent ? 'Sent' : 'Received'}</div>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Edit Profile Modal */}
      {showEditModal && (
        <div
          onClick={() => setShowEditModal(false)}
          className="fixed inset-0 z-50 bg-black/75 flex items-center justify-center p-4"
        >
          <form
            onClick={(e) => e.stopPropagation()}
            onSubmit={(e) => {
              e.preventDefault();
              const parsedInterests = interestsInput
                .split(',')
                .map((s) => s.trim())
                .filter(Boolean);
              onSave({
                displayName: name.trim() || displayName,
                bio: about,
                occupation: occupation.trim() || userOccupation,
                interests: parsedInterests.length > 0 ? parsedInterests : userInterests,
                profilePhoto: avatar,
                coverPhoto: cover,
              });
              setShowEditModal(false);
              setSavedToast(true);
              window.setTimeout(() => setSavedToast(false), 1800);
            }}
            className="w-full max-w-md rounded-3xl bg-[#222128] border border-white/10 p-5 space-y-3 text-white shadow-2xl"
          >
            <div className="flex items-center justify-between">
              <h3 className="text-base font-extrabold">Edit Profile</h3>
              <button type="button" onClick={() => setShowEditModal(false)} className="text-white/55 hover:text-white cursor-pointer">
                <X className="w-5 h-5" />
              </button>
            </div>
            <label className="block rounded-2xl bg-[#16151B] border border-white/10 p-3 text-xs text-white/75 cursor-pointer">
              <span className="font-bold text-[#A78BFA]">Tap to change Cover Photo</span>
              <input type="file" accept="image/*" className="hidden" onChange={(e) => pickFile(e, setCover)} />
            </label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Display name"
              className="w-full min-h-[42px] px-3.5 rounded-xl bg-[#16151B] border border-white/10 text-sm text-white"
            />
            <input
              type="text"
              value={about}
              onChange={(e) => setAbout(e.target.value)}
              placeholder="Bio / handle"
              className="w-full min-h-[42px] px-3.5 rounded-xl bg-[#16151B] border border-white/10 text-sm text-white"
            />
            <input
              type="text"
              value={occupation}
              onChange={(e) => setOccupation(e.target.value)}
              placeholder="Occupation"
              className="w-full min-h-[42px] px-3.5 rounded-xl bg-[#16151B] border border-white/10 text-sm text-white"
            />
            <input
              type="text"
              value={interestsInput}
              onChange={(e) => setInterestsInput(e.target.value)}
              placeholder="Interests (comma separated)"
              className="w-full min-h-[42px] px-3.5 rounded-xl bg-[#16151B] border border-white/10 text-sm text-white"
            />
            <div className="flex gap-2 pt-1">
              <button
                type="button"
                onClick={() => setShowEditModal(false)}
                className="flex-1 min-h-[42px] rounded-full bg-[#2B2A34] text-white font-bold text-xs cursor-pointer"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="flex-1 min-h-[42px] rounded-full bg-[#8B5CF6] text-white font-bold text-xs cursor-pointer"
              >
                Save Changes
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Floating Compose Moment FAB */}
      <button
        type="button"
        onClick={onOpenCompose}
        aria-label="Create Moment"
        className="fixed bottom-24 right-5 md:bottom-8 md:right-8 z-30 w-13 h-13 rounded-full text-white flex items-center justify-center shadow-xl cursor-pointer transition-transform hover:scale-105"
        style={{ background: 'linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%)' }}
      >
        <Feather className="w-5 h-5" />
      </button>
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
        className="rounded-2xl bg-[#222128] border border-white/10 p-4 space-y-3.5 shadow-lg text-white"
      >
        <div className="flex items-center justify-between">
          <span className="font-bold text-sm text-white">
            {isEditing ? 'Customize Family Logo' : 'Create Family & Logo'}
          </span>
          <button
            type="button"
            onClick={() => {
              setShowLogoBuilder(false);
              setEditingFamilyId(null);
            }}
            className="min-h-[44px] min-w-[44px] -mr-2 flex items-center justify-center text-white/55 hover:text-white cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="rounded-2xl bg-[#16151B] border border-white/10 p-3.5 flex items-center gap-3.5">
          <FamilyLogoBadge
            emblem={resolvedEmblem}
            color={activePalette.primary}
            accentColor={activePalette.accent}
            shape={logoShape}
            logoImage={logoImage}
            size="xl"
          />
          <div className="flex-1 min-w-0">
            <div className="text-xs font-bold text-white">Live Logo Preview</div>
            <div className="flex flex-wrap items-center gap-2 mt-1.5">
              <label className="min-h-[36px] px-3 py-1.5 rounded-xl bg-[#2B2A34] text-[#A78BFA] text-xs font-bold inline-flex items-center gap-1.5 cursor-pointer">
                <ImagePlus className="w-3.5 h-3.5" />
                <span>Upload photo</span>
                <input type="file" accept="image/*" className="hidden" onChange={pickLogoFile} />
              </label>
              {logoImage && (
                <button
                  type="button"
                  onClick={() => setLogoImage(null)}
                  className="min-h-[36px] px-3 py-1.5 rounded-xl bg-[#2B2A34] text-[#FF5C8A] text-xs font-bold cursor-pointer"
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
              className="w-full min-h-[44px] px-3.5 rounded-xl border border-white/15 bg-[#16151B] text-white placeholder:text-white/40 text-sm"
            />
            <input
              type="text"
              value={newMotto}
              onChange={(e) => setNewMotto(e.target.value)}
              placeholder="Family motto"
              className="w-full min-h-[44px] px-3.5 rounded-xl border border-white/15 bg-[#16151B] text-white placeholder:text-white/40 text-sm"
            />
          </>
        )}

        <div>
          <div className="text-xs font-bold text-white/80 mb-1.5">Logo shape</div>
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
                  logoShape === s.id ? 'bg-[#8B5CF6] text-white' : 'bg-[#2B2A34] text-white/75'
                }`}
              >
                {s.label}
              </button>
            ))}
          </div>
        </div>

        <div>
          <div className="text-xs font-bold text-white/80 mb-1.5">Crest symbol or initials</div>
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
                    active ? 'bg-[#8B5CF6]' : 'bg-[#2B2A34]'
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
                useMonogram ? 'bg-[#8B5CF6] text-white' : 'bg-[#2B2A34] text-white/80'
              }`}
            >
              ABC Initials
            </button>
          </div>
        </div>

        <div>
          <div className="text-xs font-bold text-white/80 mb-1.5">Logo gradient</div>
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
            className="min-h-[44px] px-4 rounded-xl bg-[#2B2A34] text-white font-semibold text-sm cursor-pointer"
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
    <div className="min-h-screen bg-[#16151B] text-white flex flex-col">
      <header className="bg-[#16151B]/95 backdrop-blur border-b border-white/10 px-3 py-2.5 flex items-center gap-3 sticky top-0 z-20">
        <button
          type="button"
          onClick={onBack}
          aria-label="Back"
          className="w-11 h-11 rounded-full flex items-center justify-center text-white hover:bg-[#222128] cursor-pointer"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        <div className="flex-1 min-w-0">
          <h1 className="text-white font-extrabold text-[19px]">Family</h1>
          <p className="text-[11px] text-white/55">Connect with your community</p>
        </div>
        <span className="text-xs font-bold text-[#70D9FF] px-3 py-1.5 rounded-full bg-[#222128] tabular-nums">
          💎 {diamonds}
        </span>
      </header>

      <div className="max-w-3xl w-full mx-auto px-5 py-5 space-y-5">
        <div className="grid grid-cols-2 gap-2 p-1 bg-[#222128] rounded-2xl border border-white/5">
          <button
            type="button"
            onClick={() => setActiveTab('mine')}
            className={`min-h-[44px] rounded-xl text-sm font-bold cursor-pointer transition-colors ${
              activeTab === 'mine' ? 'bg-[#8B5CF6] text-white' : 'text-white/70'
            }`}
          >
            My Family
          </button>
          <button
            type="button"
            onClick={() => setActiveTab('explore')}
            className={`min-h-[44px] rounded-xl text-sm font-bold cursor-pointer transition-colors ${
              activeTab === 'explore' ? 'bg-[#8B5CF6] text-white' : 'text-white/70'
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
                  <h3 className="font-bold text-white text-base">Family Wall</h3>
                  <span className="text-xs text-[#A78BFA] font-semibold tabular-nums">
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
                    className="flex-1 min-h-[44px] px-3.5 rounded-xl border border-white/15 bg-[#222128] text-white placeholder:text-white/40 text-sm"
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
                    <div key={idx} className="rounded-xl bg-[#222128] border border-white/5 px-3.5 py-2.5 text-sm text-white/90">
                      {note}
                    </div>
                  ))}
                </div>
              </section>

              <section className="space-y-2.5">
                <div className="flex items-center justify-between">
                  <h3 className="font-bold text-white text-base">Members</h3>
                  <span className="text-xs text-white/55 tabular-nums">{myFamily.members.length} people</span>
                </div>
                <div className="space-y-2">
                  {myFamily.members.map((member) => (
                    <div
                      key={member.name}
                      className="rounded-2xl bg-[#222128] border border-white/5 p-3 flex items-center gap-3"
                    >
                      <AvatarBadge name={member.name} size="sm" />
                      <div className="flex-1 min-w-0">
                        <div className="font-bold text-sm text-white truncate">{member.name}</div>
                        <div className="text-xs text-white/55 tabular-nums">
                          {member.role} · {member.points} pts
                        </div>
                      </div>
                      {member.name !== 'You' && (
                        <button
                          type="button"
                          onClick={() => onChatMember(member.name)}
                          className="min-h-[40px] px-3.5 rounded-xl text-xs font-bold text-[#A78BFA] bg-[#2B2A34] hover:bg-[#383645] cursor-pointer"
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
                className="w-full min-h-[44px] rounded-full border border-[#FF5C8A]/40 text-[#FF5C8A] font-bold text-sm cursor-pointer"
              >
                Leave Family
              </button>
            </div>
          ) : (
            <div className="rounded-2xl bg-[#222128] border border-white/5 p-6 text-center space-y-3">
              <div className="text-4xl">🏠</div>
              <div className="font-bold text-white">You haven't joined a Family yet</div>
              <p className="text-xs text-white/55">Join an existing community or create your own crew.</p>
              <div className="flex items-center justify-center gap-2 pt-1">
                <button
                  type="button"
                  onClick={() => setActiveTab('explore')}
                  className="min-h-[44px] px-4 rounded-xl bg-[#2B2A34] text-[#A78BFA] font-bold text-xs cursor-pointer"
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
                  <div key={family.id} className="rounded-2xl bg-[#222128] border border-white/5 p-4 space-y-2">
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
                        <div className="font-extrabold text-white text-base truncate">{family.name}</div>
                        <div className="text-xs text-white/55 tabular-nums">
                          Level {family.level} · {family.members.length} members · {family.points} pts
                        </div>
                      </div>
                      <button
                        type="button"
                        onClick={() => {
                          onJoinFamily(family.id);
                          setActiveTab('mine');
                        }}
                        className={`min-h-[40px] px-4 rounded-full font-bold text-xs cursor-pointer ${
                          isMine ? 'bg-[#2B2A34] text-[#A78BFA]' : 'bg-[#8B5CF6] text-white'
                        }`}
                      >
                        {isMine ? 'Joined' : 'Join'}
                      </button>
                    </div>
                    <p className="text-xs text-white/60">{family.motto}</p>
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
    <div className="min-h-screen bg-[#16151B] text-white pb-12">
      <div className="sticky top-0 z-20 bg-[#16151B]/95 backdrop-blur border-b border-white/10 px-4 py-3 flex items-center gap-3">
        <button
          type="button"
          onClick={onBack}
          aria-label="Back"
          className="min-h-[44px] min-w-[44px] rounded-full flex items-center justify-center text-white hover:bg-[#222128] cursor-pointer"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        <div className="flex-1 min-w-0">
          <h1 className="text-lg font-extrabold text-white">Add Friends</h1>
          <p className="text-xs text-white/55 tabular-nums">
            {friends.length} friends · {friendRequests.length} requests
          </p>
        </div>
      </div>

      <div className="max-w-3xl mx-auto px-5 pt-5 space-y-6">
        <form
          onSubmit={handleQuickAddSubmit}
          className="rounded-[22px] bg-[#222128] border border-white/10 p-4 space-y-3"
        >
          <div>
            <h2 className="font-extrabold text-white text-sm">Add by name or @username</h2>
            <p className="text-xs text-white/55 mt-0.5">
              Connect directly with friends on MY HAPPY PLACE
            </p>
          </div>
          <div className="flex flex-col sm:flex-row gap-2">
            <input
              type="text"
              value={customFriendInput}
              onChange={(e) => setCustomFriendInput(e.target.value)}
              placeholder="Enter friend's name or @handle (e.g. Koko)"
              className="flex-1 min-h-[44px] px-3.5 rounded-xl border border-white/15 bg-[#16151B] text-sm text-white placeholder:text-white/40 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#8B5CF6]"
            />
            <button
              type="submit"
              className="min-h-[44px] px-5 rounded-xl bg-[#8B5CF6] hover:bg-[#7C3AED] text-white font-bold text-xs inline-flex items-center justify-center gap-1.5 cursor-pointer whitespace-nowrap transition-colors"
            >
              <UserPlus className="w-4 h-4" />
              <span>Add friend</span>
            </button>
          </div>
          {feedback && (
            <div role="status" className="text-xs font-semibold text-[#A78BFA] flex items-center gap-1.5">
              <Check className="w-4 h-4 shrink-0" />
              <span>{feedback}</span>
            </div>
          )}
        </form>

        <div className="relative flex items-center">
          <Search className="w-4 h-4 text-[#A78BFA] absolute left-3.5 pointer-events-none" />
          <input
            type="search"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Filter people by name, @handle, or vibe..."
            className="w-full min-h-[44px] pl-10 pr-10 rounded-2xl bg-[#222128] border border-white/10 text-sm text-white placeholder:text-white/40 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#8B5CF6]"
          />
          {searchQuery.length > 0 && (
            <button
              type="button"
              onClick={() => setSearchQuery('')}
              aria-label="Clear filter"
              className="absolute right-1.5 min-h-[36px] min-w-[36px] rounded-full flex items-center justify-center text-white/55 hover:text-white cursor-pointer"
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>

        {requestPeople.length > 0 && (
          <section className="space-y-2.5">
            <div className="flex items-center justify-between">
              <h2 className="font-bold text-white text-base">Friend Requests</h2>
              <span className="text-xs font-semibold text-[#A78BFA] tabular-nums">
                {requestPeople.length} pending
              </span>
            </div>
            <div className="space-y-2">
              {requestPeople.map((person) => (
                <div
                  key={person.name}
                  className="rounded-2xl bg-[#222128] border border-white/5 p-3.5 flex flex-wrap items-center gap-3"
                >
                  <AvatarBadge name={person.name} size="sm" />
                  <div className="flex-1 min-w-[140px]">
                    <div className="font-bold text-sm text-white truncate">{person.name}</div>
                    <div className="text-xs text-white/55 truncate">
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
                      className="min-h-[40px] px-4 rounded-full bg-[#8B5CF6] hover:bg-[#7C3AED] text-white font-bold text-xs inline-flex items-center gap-1 cursor-pointer transition-colors"
                    >
                      <Check className="w-3.5 h-3.5" />
                      <span>Accept</span>
                    </button>
                    <button
                      type="button"
                      onClick={() => onDeclineRequest(person.name)}
                      className="min-h-[40px] px-3.5 rounded-full bg-[#2B2A34] hover:bg-[#383645] text-white/80 font-semibold text-xs cursor-pointer transition-colors"
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
            <h2 className="font-bold text-white text-base">People You May Know</h2>
            <span className="text-xs text-white/55 tabular-nums">{discoverPeople.length} suggested</span>
          </div>
          {discoverPeople.length === 0 ? (
            <div className="rounded-2xl bg-[#222128] p-4 text-xs text-white/55">
              {normalizedQuery
                ? `No suggested people matched "${searchQuery.trim()}". Try adding them directly above.`
                : 'You have added all suggested people! Add anyone else by name or @handle above.'}
            </div>
          ) : (
            <div className="space-y-2">
              {discoverPeople.map((person) => (
                <div
                  key={person.name}
                  className="rounded-2xl bg-[#222128] border border-white/5 p-3.5 flex flex-wrap items-center gap-3"
                >
                  <AvatarBadge name={person.name} size="sm" />
                  <div className="flex-1 min-w-[140px]">
                    <div className="font-bold text-sm text-white truncate">{person.name}</div>
                    <div className="text-xs text-white/55 truncate">
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
                      className="min-h-[40px] px-4 rounded-full bg-[#8B5CF6] hover:bg-[#7C3AED] text-white font-bold text-xs inline-flex items-center gap-1.5 cursor-pointer transition-colors"
                    >
                      <UserPlus className="w-3.5 h-3.5" />
                      <span>Add friend</span>
                    </button>
                    <button
                      type="button"
                      onClick={() => onChat(person.name)}
                      aria-label={`Chat with ${person.name}`}
                      className="min-h-[40px] min-w-[40px] rounded-full bg-[#2B2A34] text-[#A78BFA] flex items-center justify-center cursor-pointer"
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
            <h2 className="font-bold text-white text-base">My Friends</h2>
            <span className="text-xs font-semibold text-[#A78BFA] tabular-nums">{friendPeople.length}</span>
          </div>
          {friendPeople.length === 0 ? (
            <div className="rounded-2xl bg-[#222128] p-4 text-xs text-white/55">
              No friends match this filter yet.
            </div>
          ) : (
            <div className="space-y-2">
              {friendPeople.map((person) => (
                <div
                  key={person.name}
                  className="rounded-2xl bg-[#222128] border border-white/5 p-3.5 flex flex-wrap items-center gap-3"
                >
                  <AvatarBadge name={person.name} size="sm" />
                  <div className="flex-1 min-w-[140px]">
                    <div className="font-bold text-sm text-white truncate">{person.name}</div>
                    <div className="text-xs text-white/55 truncate">
                      {person.handle} · {person.isOnline ? 'Online' : 'Offline'} · {person.vibe}
                    </div>
                  </div>
                  <div className="flex items-center gap-2 shrink-0">
                    <button
                      type="button"
                      onClick={() => onChat(person.name)}
                      className="min-h-[40px] px-4 rounded-full bg-[#2B2A34] hover:bg-[#383645] text-[#A78BFA] font-bold text-xs inline-flex items-center gap-1.5 cursor-pointer transition-colors"
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
                      className="min-h-[40px] min-w-[40px] rounded-full bg-[#2B2A34] hover:bg-[#FF5C8A]/20 text-white/60 hover:text-[#FF5C8A] flex items-center justify-center cursor-pointer transition-colors"
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
