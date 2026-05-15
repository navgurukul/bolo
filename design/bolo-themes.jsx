// bolo-themes.jsx — three calm-minimal aesthetics for Bolo.
// All low-saturation greens, all warm off-whites; the variants change type
// pairing, accent depth, density and shape language — not the soul.

const BOLO_THEMES = {
  // FINAL — Whisper palette · Geist for display/body (Linen) ·
  // Newsreader italic for the human moments (Garden) · Geist Mono for data.
  final: {
    id: 'final',
    name: 'Bolo',
    tagline: 'sage · cream · sans + italic serif accents',
    bg: '#f4f1e9',
    surface: '#fbf9f3',
    surfaceAlt: '#ece8de',
    fg: '#1f221d',
    fgSoft: '#3d423b',
    muted: '#797d72',
    line: 'rgba(31,34,29,0.10)',
    lineStrong: 'rgba(31,34,29,0.22)',
    accent: '#6d9176',
    accentDeep: '#3c5944',
    accentSoft: '#d4e1d5',
    warn: '#c89764',
    rec: '#c14f4f',
    // Geist drives numerals + titles; Newsreader italic is reserved
    // for the wordmark, the read-aloud sentence, and the reflection prompt.
    fontDisplay: "'Geist', system-ui, sans-serif",
    fontAccent:  "'Newsreader', 'Instrument Serif', serif",
    fontBody:    "'Geist', system-ui, sans-serif",
    fontMono:    "'Geist Mono', ui-monospace, monospace",
    radius: 20,
    radiusSm: 12,
    density: 'airy',
    bezel: '#1a1812',
    bezelInner: '#0e0d09',
  },

  // A · Whisper — the primary. Warm off-white, sage, soft serif numerals.
  whisper: {
    id: 'whisper',
    name: 'Whisper',
    tagline: 'sage · off-white · hairlines',
    bg: '#f4f1e9',
    surface: '#fbf9f3',
    surfaceAlt: '#ece8de',
    fg: '#1f221d',
    fgSoft: '#3d423b',
    muted: '#797d72',
    line: 'rgba(31,34,29,0.10)',
    lineStrong: 'rgba(31,34,29,0.22)',
    accent: '#6d9176',
    accentDeep: '#3c5944',
    accentSoft: '#d4e1d5',
    warn: '#c89764',
    rec: '#c14f4f',
    fontDisplay: "'Instrument Serif', 'Newsreader', serif",
    fontBody: "'Geist', system-ui, sans-serif",
    fontMono: "'Geist Mono', ui-monospace, monospace",
    radius: 20,
    radiusSm: 12,
    density: 'airy',
    // Phone bezel
    bezel: '#1a1812',
    bezelInner: '#0e0d09',
  },

  // B · Garden — deeper, more editorial. Cream + moss, Newsreader.
  garden: {
    id: 'garden',
    name: 'Garden',
    tagline: 'moss · cream · editorial',
    bg: '#e9e3d3',
    surface: '#f2ecdc',
    surfaceAlt: '#ddd5c0',
    fg: '#1b1f18',
    fgSoft: '#39403a',
    muted: '#6e6f5f',
    line: 'rgba(27,31,24,0.14)',
    lineStrong: 'rgba(27,31,24,0.32)',
    accent: '#4d6b53',
    accentDeep: '#2b4231',
    accentSoft: '#bfd0bb',
    warn: '#b07a3d',
    rec: '#a64545',
    fontDisplay: "'Newsreader', 'Instrument Serif', serif",
    fontAccent:  "'Newsreader', 'Instrument Serif', serif",
    fontBody: "'Geist', system-ui, sans-serif",
    fontMono: "'Geist Mono', ui-monospace, monospace",
    radius: 6,
    radiusSm: 4,
    density: 'editorial',
    bezel: '#23211a',
    bezelInner: '#100f0a',
  },

  // C · Linen — almost colourless. Mono numerals, lots of breath.
  linen: {
    id: 'linen',
    name: 'Linen',
    tagline: 'celadon · paper · mono data',
    bg: '#f5f4ee',
    surface: '#ffffff',
    surfaceAlt: '#eeede6',
    fg: '#0d1210',
    fgSoft: '#363a35',
    muted: '#8b8d85',
    line: 'rgba(13,18,16,0.07)',
    lineStrong: 'rgba(13,18,16,0.18)',
    accent: '#7ea98c',
    accentDeep: '#4a6d57',
    accentSoft: '#e0ebe2',
    warn: '#d09a5b',
    rec: '#cf5757',
    fontDisplay: "'Geist', system-ui, sans-serif",
    fontAccent:  "'Geist', system-ui, sans-serif",
    fontBody: "'Geist', system-ui, sans-serif",
    fontMono: "'Geist Mono', ui-monospace, monospace",
    radius: 28,
    radiusSm: 16,
    density: 'spacious',
    bezel: '#14130f',
    bezelInner: '#06060a',
  },
};

// Shared sample data — same cohort across all three variants.
const BOLO_COHORT = [
  { id: 'priya',   name: 'Priya',   pct: 84, weekly: [56, 62, 68, 64, 71, 75, 78, 84] },
  { id: 'karthik', name: 'Karthik', pct: 78, weekly: [44, 51, 55, 58, 63, 68, 72, 78] },
  { id: 'rohit',   name: 'Rohit',   pct: 71, weekly: [38, 42, 49, 52, 58, 63, 67, 71] },
  { id: 'meena',   name: 'Meena',   pct: 68, weekly: [40, 44, 46, 53, 55, 60, 64, 68] },
  { id: 'daniel',  name: 'Daniel',  pct: 64, weekly: [35, 38, 41, 46, 52, 55, 60, 64] },
  { id: 'asha',    name: 'Asha',    pct: 58, weekly: [29, 33, 36, 41, 44, 48, 53, 58] },
];

const BOLO_TOPICS = [
  'Daily life',
  'Mock interview',
  'News chat',
  'Tech',
  'Free talk',
  'Family',
];

Object.assign(window, { BOLO_THEMES, BOLO_COHORT, BOLO_TOPICS });
