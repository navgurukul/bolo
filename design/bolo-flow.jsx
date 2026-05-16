// bolo-flow.jsx — the Phone shell + screen router for one BoloFlow instance.
// Each BoloFlow holds its own state (which screen, current topic, etc) and
// renders into a soft phone bezel. Three of these sit on the design canvas,
// each given a different theme.

const PHONE_W = 380;
const PHONE_H = 800;

function PhoneShell({ t, children, dark = false }) {
  return (
    <div style={{
      width: PHONE_W, height: PHONE_H,
      background: t.bezel,
      borderRadius: 44,
      padding: 8,
      boxSizing: 'border-box',
      boxShadow: `0 22px 60px rgba(0,0,0,0.32), 0 2px 6px rgba(0,0,0,0.18), inset 0 0 0 1px ${t.bezelInner}`,
      position: 'relative',
    }}>
      <div style={{
        width: '100%', height: '100%',
        borderRadius: 36,
        overflow: 'hidden',
        background: t.bg,
        position: 'relative',
        display: 'flex', flexDirection: 'column',
      }}>
        <PhoneStatusBar t={t} />
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minHeight: 0 }}>
          {children}
        </div>
        <PhoneNavGesture t={t} />
      </div>
      {/* Camera punch-hole */}
      <div style={{
        position: 'absolute', top: 22, left: '50%', transform: 'translateX(-50%)',
        width: 14, height: 14, borderRadius: '50%', background: '#000',
        boxShadow: 'inset 0 0 0 1.5px #2a2825',
      }} />
    </div>
  );
}

function PhoneStatusBar({ t }) {
  return (
    <div style={{
      height: 44, paddingTop: 14,
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      padding: '14px 26px 0',
      fontFamily: t.fontMono, fontSize: 12, color: t.fg, letterSpacing: 0.3,
    }}>
      <span style={{ fontVariantNumeric: 'tabular-nums' }}>9:30</span>
      <div style={{ display: 'flex', gap: 5, alignItems: 'center' }}>
        {/* sig bars */}
        <svg width="14" height="10" viewBox="0 0 14 10">
          <rect x="0" y="6" width="2" height="4" fill={t.fg} />
          <rect x="3" y="4" width="2" height="6" fill={t.fg} />
          <rect x="6" y="2" width="2" height="8" fill={t.fg} />
          <rect x="9" y="0" width="2" height="10" fill={t.fg} />
        </svg>
        {/* wifi */}
        <svg width="14" height="10" viewBox="0 0 16 12" fill="none" stroke={t.fg} strokeWidth="1.6" strokeLinecap="round">
          <path d="M1.5 4.5 a 9 9 0 0 1 13 0" />
          <path d="M4 7 a 5.5 5.5 0 0 1 8 0" />
          <circle cx="8" cy="10" r="0.8" fill={t.fg} stroke="none" />
        </svg>
        {/* battery */}
        <svg width="22" height="10" viewBox="0 0 24 10" fill="none">
          <rect x="0.5" y="0.5" width="20" height="9" rx="2" stroke={t.fg} strokeOpacity="0.7" />
          <rect x="22" y="3" width="1.5" height="4" rx="0.5" fill={t.fg} fillOpacity="0.7" />
          <rect x="2" y="2" width="14" height="6" rx="1" fill={t.fg} />
        </svg>
      </div>
    </div>
  );
}

function PhoneNavGesture({ t }) {
  return (
    <div style={{
      height: 18, display: 'flex', alignItems: 'center', justifyContent: 'center',
      flexShrink: 0,
    }}>
      <div style={{
        width: 120, height: 4, borderRadius: 2,
        background: t.fgSoft, opacity: 0.4,
      }} />
    </div>
  );
}


// ─────────────────────────────────────────────────────────────
// BoloFlow — the routed prototype for one variant.
// ─────────────────────────────────────────────────────────────
function BoloFlow({ themeId, initialScreen = 'home' }) {
  const t = BOLO_THEMES[themeId];
  const [state, setState] = React.useState({
    screen: initialScreen,
    topic: 'Daily life',
    elapsed: 0,
    isLive: false,
    ringOn: true,
    vibrOn: false,
    lastSession: { pct: 64, duration: 32, topic: 'News chat' },
  });

  const set = (patch) => setState((s) => ({ ...s, ...patch }));

  const screen = state.screen;

  return (
    <PhoneShell t={t}>
      {screen === 'splash'         && <SplashScreen        t={t} state={state} set={set} />}
      {screen === 'pair'           && <PairScreen          t={t} state={state} set={set} />}
      {screen === 'consent'        && <ConsentScreen       t={t} state={state} set={set} />}
      {screen === 'mic-perm'       && <MicPermScreen       t={t} state={state} set={set} />}
      {screen === 'cohort-setup'   && <CohortSetupScreen   t={t} state={state} set={set} />}
      {screen === 'home'           && <HomeScreen          t={t} state={state} set={set} />}
      {screen === 'enroll'         && <EnrollScreen        t={t} state={state} set={set} />}
      {screen === 'start'          && <StartScreen         t={t} state={state} set={set} />}
      {screen === 'attendance'     && <AttendanceScreen    t={t} state={state} set={set} />}
      {screen === 'add-student'    && <AddStudentScreen    t={t} state={state} set={set} />}
      {screen === 'live'           && <LiveScreen          t={t} state={state} set={set} />}
      {screen === 'summary'        && <SummaryScreen       t={t} state={state} set={set} />}
      {screen === 'dashboard'      && <DashboardScreen     t={t} state={state} set={set} />}
      {screen === 'history'        && <HistoryScreen       t={t} state={state} set={set} />}
      {screen === 'session-detail' && <SessionDetailScreen t={t} state={state} set={set} />}
      {screen === 'settings'       && <SettingsScreen      t={t} state={state} set={set} />}
      {screen === 'forget-voice'   && <ForgetVoiceScreen   t={t} state={state} set={set} />}
      {screen === 'sync'           && <SyncScreen          t={t} state={state} set={set} />}
    </PhoneShell>
  );
}

Object.assign(window, { BoloFlow, PhoneShell, PHONE_W, PHONE_H });
