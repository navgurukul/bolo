// bolo-screens-extra.jsx — second batch of screens to round Bolo into a
// complete end-to-end product:
//   • Splash, Pair device, Consent, Mic permission, Cohort setup  (first-run)
//   • Attendance, PauseOverlay, TopicSwitch                       (session)
//   • History list, SessionDetail                                 (review)
//   • Settings, ForgetVoice, Sync                                 (care)

const { useEffect: useEffectX, useState: useStateX } = React;

// ─────────────────────────────────────────────────────────────
// SPLASH — auto-advances after a moment
// ─────────────────────────────────────────────────────────────
function SplashScreen({ t, state, set }) {
  useEffectX(() => {
    const id = setTimeout(() => set({ screen: state.paired ? 'home' : 'pair' }), 1800);
    return () => clearTimeout(id);
  }, []);
  return (
    <FullBody t={t}>
      <div style={{
        height: '100%', display: 'flex', flexDirection: 'column',
        alignItems: 'center', justifyContent: 'center', gap: 24,
        animation: 'bolo-fade-in .6s ease both',
      }}>
        <div style={{
          fontFamily: t.fontAccent || t.fontDisplay,
          fontSize: 96, lineHeight: 0.9, letterSpacing: -2,
          fontStyle: 'italic', color: t.fg,
        }}>
          Bolo<span style={{ color: t.accent, fontStyle: 'normal' }}>.</span>
        </div>
        <div style={{
          fontFamily: t.fontMono, fontSize: 11, color: t.muted,
          letterSpacing: 2, textTransform: 'uppercase',
        }}>
          speak · listen · grow
        </div>
        <div style={{ position: 'absolute', bottom: 32, left: 0, right: 0, textAlign: 'center' }}>
          <div style={{ display: 'inline-flex', gap: 4 }}>
            {[0, 1, 2].map((i) => (
              <span key={i} style={{
                width: 5, height: 5, borderRadius: '50%', background: t.accent,
                animation: `bolo-breathe 1.2s ease-in-out ${i * 0.18}s infinite`,
              }} />
            ))}
          </div>
        </div>
      </div>
    </FullBody>
  );
}

// ─────────────────────────────────────────────────────────────
// PAIR — bind this phone to a program (no per-user account)
// ─────────────────────────────────────────────────────────────
function PairScreen({ t, state, set }) {
  const [code, setCode] = useStateX(['N', 'G', 'K', '7', 'T', '4']);
  const [showing, setShowing] = useStateX(0);
  useEffectX(() => {
    // Auto-type the code for the demo.
    if (showing < 6) {
      const id = setTimeout(() => setShowing(showing + 1), 220);
      return () => clearTimeout(id);
    }
  }, [showing]);

  return (
    <ScrollBody t={t}>
      <Pad>
        <div style={{ height: 24 }} />
        <Wordmark t={t} />
        <div style={{ height: 36 }} />
        <Caption t={t}>Step 1 of 4 · Pair this phone</Caption>
        <div style={{ height: 6 }} />
        <ScreenTitle t={t}>Enter your<br/>program code.</ScreenTitle>
        <div style={{ height: 16 }} />
        <p style={{ color: t.fgSoft, fontSize: 15, lineHeight: 1.55, margin: 0, maxWidth: 320 }}>
          Your program coordinator gave you a six-character code. It binds this device to your cohort group.
        </p>

        <div style={{ height: 28 }} />

        {/* Code slots */}
        <div style={{ display: 'flex', gap: 8, justifyContent: 'center' }}>
          {code.map((ch, i) => (
            <div key={i} style={{
              width: 44, height: 56, borderRadius: t.radiusSm,
              background: i < showing ? t.surface : 'transparent',
              boxShadow: `inset 0 0 0 ${i === showing ? 2 : 1}px ${i === showing ? t.accent : t.lineStrong}`,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontFamily: t.fontMono, fontSize: 24, color: t.fg,
              fontWeight: 500,
              transition: 'all .2s ease',
            }}>
              {i < showing ? ch : ''}
            </div>
          ))}
        </div>

        <div style={{ height: 14 }} />
        <div style={{ textAlign: 'center', fontSize: 12, color: t.muted, fontFamily: t.fontMono }}>
          {showing < 6 ? 'Listening…' : '✓ Code recognised · NavGurukul, Pune'}
        </div>

        <div style={{ height: 28 }} />
        <Btn t={t} full onClick={() => set({ screen: 'consent' })}>
          Continue
        </Btn>
        <div style={{ height: 10 }} />
        <Btn t={t} full variant="quiet">
          I don't have a code yet
        </Btn>
      </Pad>
    </ScrollBody>
  );
}

// ─────────────────────────────────────────────────────────────
// CONSENT — the single-screen privacy promise
// ─────────────────────────────────────────────────────────────
function ConsentScreen({ t, state, set }) {
  return (
    <ScrollBody t={t}>
      <Pad>
        <div style={{ height: 24 }} />
        <Caption t={t}>Step 2 of 4 · How Bolo listens</Caption>
        <div style={{ height: 8 }} />
        <ScreenTitle t={t}>We listen.<br/>We don't remember.</ScreenTitle>
        <div style={{ height: 24 }} />

        <PromiseRow t={t} icon="ear" title="Bolo listens during a session."
          sub="It detects whether each speaker is using English or another language. That's it." />
        <PromiseRow t={t} icon="ghost" title="No audio is ever saved."
          sub="Sound is processed in memory and discarded. Nothing is written to disk." />
        <PromiseRow t={t} icon="dot" title="A red dot stays visible when the mic is on."
          sub="Always. Even if the screen dims. No surprises." />
        <PromiseRow t={t} icon="lock" title="Your voice fingerprint lives only on this phone."
          sub="Encrypted at rest. Wipe it any time with one tap." />

        <div style={{ height: 28 }} />
        <Btn t={t} full onClick={() => set({ screen: 'mic-perm' })}>
          I understand — continue
        </Btn>
        <div style={{ height: 10 }} />
        <Btn t={t} full variant="quiet">
          Read the full policy
        </Btn>
      </Pad>
    </ScrollBody>
  );
}

function PromiseRow({ t, icon, title, sub }) {
  const glyphs = {
    ear:  <path d="M7 5a5 5 0 0 1 10 0c0 3-2 4-2 7s-2 4-4 4-2-2-2-4" />,
    ghost:<><path d="M5 19V9a7 7 0 0 1 14 0v10l-2-2-2 2-2-2-2 2-2-2-2 2-2-2z" /><circle cx="9.5" cy="11" r="1" fill="currentColor" stroke="none" /><circle cx="14.5" cy="11" r="1" fill="currentColor" stroke="none" /></>,
    dot:  <><circle cx="12" cy="12" r="4" fill={t.rec} stroke="none" /><circle cx="12" cy="12" r="8" /></>,
    lock: <><rect x="5" y="11" width="14" height="9" rx="2" /><path d="M8 11V8a4 4 0 0 1 8 0v3" /></>,
  };
  return (
    <div style={{ display: 'flex', gap: 16, padding: '14px 0', borderBottom: `1px solid ${t.line}` }}>
      <div style={{
        width: 36, height: 36, borderRadius: '50%', flexShrink: 0,
        background: t.accentSoft, color: t.accentDeep,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
      }}>
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round">
          {glyphs[icon]}
        </svg>
      </div>
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 15, color: t.fg, fontWeight: 500 }}>{title}</div>
        <div style={{ fontSize: 13, color: t.muted, marginTop: 4, lineHeight: 1.5 }}>{sub}</div>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// MIC PERMISSION — Android-style sheet
// ─────────────────────────────────────────────────────────────
function MicPermScreen({ t, state, set }) {
  return (
    <div style={{ flex: 1, position: 'relative', background: 'rgba(20,18,12,0.45)', display: 'flex', flexDirection: 'column', justifyContent: 'flex-end' }}>
      {/* Dimmed background — show a hint of Home */}
      <div style={{
        position: 'absolute', inset: 0, background: t.bg, opacity: 0.55,
        padding: 24, fontFamily: t.fontBody,
      }}>
        <div style={{
          fontFamily: t.fontAccent || t.fontDisplay, fontSize: 30,
          fontStyle: 'italic', color: t.fg, letterSpacing: -0.6,
        }}>
          Bolo<span style={{ color: t.accent, fontStyle: 'normal' }}>.</span>
        </div>
      </div>

      <div style={{
        position: 'relative', background: t.surface, borderRadius: `${t.radius}px ${t.radius}px 0 0`,
        padding: '28px 28px 24px', animation: 'bolo-fade-in .3s ease both',
      }}>
        <div style={{
          width: 56, height: 56, borderRadius: '50%',
          background: t.accentSoft, color: t.accentDeep,
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}>
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
            <rect x="9" y="3" width="6" height="12" rx="3" />
            <path d="M5 11a7 7 0 0 0 14 0M12 18v3M8 21h8" />
          </svg>
        </div>
        <div style={{ height: 18 }} />
        <div style={{ fontFamily: t.fontDisplay, fontSize: 22, color: t.fg, letterSpacing: -0.3, fontWeight: 500 }}>
          Allow Bolo to use the microphone?
        </div>
        <div style={{ marginTop: 10, color: t.fgSoft, fontSize: 14, lineHeight: 1.5 }}>
          Bolo needs the mic to detect English vs. other languages during sessions. Audio is processed in memory and never saved.
        </div>
        <div style={{ height: 22 }} />
        <Btn t={t} full onClick={() => set({ screen: 'cohort-setup' })}>
          While using the app
        </Btn>
        <div style={{ height: 8 }} />
        <Btn t={t} full variant="ghost">Only this time</Btn>
        <div style={{ height: 8 }} />
        <Btn t={t} full variant="quiet">Don't allow</Btn>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// COHORT SETUP — name your cohort, your name as facilitator
// ─────────────────────────────────────────────────────────────
function CohortSetupScreen({ t, state, set }) {
  const [name, setName] = useStateX('Pune · Batch 14');
  const [facil, setFacil] = useStateX('Anjali');
  return (
    <ScrollBody t={t}>
      <Pad>
        <div style={{ height: 24 }} />
        <Caption t={t}>Step 4 of 4 · Name your cohort</Caption>
        <div style={{ height: 8 }} />
        <ScreenTitle t={t}>Last thing —<br/>what shall we call this group?</ScreenTitle>

        <div style={{ height: 28 }} />
        <Field t={t} label="Cohort name" value={name} onChange={setName} />
        <div style={{ height: 18 }} />
        <Field t={t} label="Your name (facilitator)" value={facil} onChange={setFacil} />

        <div style={{ height: 22 }} />
        <Card t={t}>
          <Caption t={t}>What happens next</Caption>
          <div style={{ marginTop: 8, color: t.fgSoft, fontSize: 14, lineHeight: 1.55 }}>
            On the home screen, you'll add each student and record their ten-second voice fingerprint. After that, three taps starts a session.
          </div>
        </Card>

        <div style={{ height: 28 }} />
        <Btn t={t} full onClick={() => set({ screen: 'home', paired: true })}>
          We're ready →
        </Btn>
      </Pad>
    </ScrollBody>
  );
}

function Field({ t, label, value, onChange }) {
  return (
    <label style={{ display: 'block' }}>
      <div style={{
        fontFamily: t.fontMono, fontSize: 10.5, letterSpacing: 1.4,
        color: t.muted, textTransform: 'uppercase', marginBottom: 8,
      }}>{label}</div>
      <input value={value} onChange={(e) => onChange(e.target.value)}
        style={{
          width: '100%', boxSizing: 'border-box',
          background: 'transparent', border: 'none',
          borderBottom: `1px solid ${t.lineStrong}`,
          padding: '8px 0', fontFamily: t.fontBody,
          color: t.fg, fontSize: 18, outline: 'none',
        }} />
    </label>
  );
}

// ─────────────────────────────────────────────────────────────
// ATTENDANCE — who's in the room today
// ─────────────────────────────────────────────────────────────
function AttendanceScreen({ t, state, set }) {
  const [present, setPresent] = useStateX(() => new Set(BOLO_COHORT.map((s) => s.id)));
  const toggle = (id) => setPresent((p) => {
    const next = new Set(p);
    if (next.has(id)) next.delete(id); else next.add(id);
    return next;
  });
  const count = present.size;

  return (
    <ScrollBody t={t}>
      <Pad>
        <TopBar t={t} onBack={() => set({ screen: 'start' })} label="Who's here today?" />
        <div style={{ height: 22 }} />
        <ScreenTitle t={t}>Tap each student<br/>who's in the room.</ScreenTitle>
        <div style={{ height: 8 }} />
        <p style={{ color: t.fgSoft, fontSize: 14, lineHeight: 1.55, margin: 0 }}>
          Only people you tap will be counted. The phone will still hear everyone — it just won't credit unknown speakers.
        </p>

        <div style={{ height: 22 }} />
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <Caption t={t}>Enrolled · {BOLO_COHORT.length}</Caption>
          <div style={{ fontFamily: t.fontMono, fontSize: 12, color: t.accentDeep }}>
            {count} selected
          </div>
        </div>
        <div style={{ height: 8 }} />
        {BOLO_COHORT.map((s) => {
          const on = present.has(s.id);
          return (
            <button key={s.id} onClick={() => toggle(s.id)}
              style={{
                display: 'flex', alignItems: 'center', gap: 14,
                width: '100%', padding: '12px 0', border: 'none', background: 'transparent',
                borderBottom: `1px solid ${t.line}`, cursor: 'pointer',
                textAlign: 'left',
              }}>
              <Avatar t={t} name={s.name} size={36} active={on} />
              <div style={{ flex: 1, fontSize: 15, color: on ? t.fg : t.muted }}>{s.name}</div>
              <span style={{
                width: 22, height: 22, borderRadius: '50%',
                background: on ? t.accent : 'transparent',
                boxShadow: on ? 'none' : `inset 0 0 0 1.5px ${t.lineStrong}`,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                color: '#fff',
              }}>
                {on && (
                  <svg width="11" height="11" viewBox="0 0 12 12" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M3 6.5 5 8.5 9 4" />
                  </svg>
                )}
              </span>
            </button>
          );
        })}

        <div style={{ height: 14 }} />
        <button onClick={() => set({ screen: 'add-student' })} style={{
          display: 'flex', alignItems: 'center', gap: 12,
          width: '100%', padding: '14px 0', border: 'none', background: 'transparent', cursor: 'pointer',
          color: t.accentDeep, fontFamily: t.fontBody, fontSize: 14, fontWeight: 500,
        }}>
          <span style={{
            width: 36, height: 36, borderRadius: '50%',
            background: t.accentSoft, display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: 22, color: t.accentDeep, fontWeight: 300,
          }}>+</span>
          Add a new student to the cohort
        </button>

        <div style={{ height: 24 }} />
        <Btn t={t} full onClick={() => set({ screen: 'live', elapsed: 0, isLive: true })}>
          Start with {count} {count === 1 ? 'student' : 'students'} →
        </Btn>
      </Pad>
    </ScrollBody>
  );
}

// ─────────────────────────────────────────────────────────────
// ADD STUDENT — small inline flow (name first → enroll voice)
// ─────────────────────────────────────────────────────────────
function AddStudentScreen({ t, state, set }) {
  const [name, setName] = useStateX('');
  return (
    <ScrollBody t={t}>
      <Pad>
        <TopBar t={t} onBack={() => set({ screen: 'attendance' })} label="Add a student" />
        <div style={{ height: 22 }} />
        <ScreenTitle t={t}>What's their name?</ScreenTitle>
        <div style={{ height: 22 }} />
        <Field t={t} label="First name" value={name} onChange={setName} />
        <div style={{ height: 18 }} />
        <PrivacyNote t={t}>
          Next, you'll hand them the phone for a 10-second voice reading. We'll guide them through it.
        </PrivacyNote>
        <div style={{ height: 28 }} />
        <Btn t={t} full onClick={() => set({ screen: 'enroll' })}>
          Continue to voice setup →
        </Btn>
      </Pad>
    </ScrollBody>
  );
}

// ─────────────────────────────────────────────────────────────
// HISTORY — list of past sessions
// ─────────────────────────────────────────────────────────────
const BOLO_HISTORY = [
  { id: 's1', date: 'Today · 9:30',     topic: 'Daily life',     duration: 28, pct: 71, students: 6, top: 'Priya'   },
  { id: 's2', date: 'Yesterday · 4:10', topic: 'Mock interview', duration: 42, pct: 78, students: 5, top: 'Karthik' },
  { id: 's3', date: 'Mon · 9:30',       topic: 'News chat',      duration: 35, pct: 64, students: 6, top: 'Priya'   },
  { id: 's4', date: 'Sat · 11:00',      topic: 'Free talk',      duration: 20, pct: 58, students: 4, top: 'Meena'   },
  { id: 's5', date: 'Fri · 9:30',       topic: 'Tech',           duration: 31, pct: 66, students: 6, top: 'Rohit'   },
  { id: 's6', date: 'Thu · 4:00',       topic: 'Family',         duration: 24, pct: 53, students: 5, top: 'Asha'    },
];

function HistoryScreen({ t, state, set }) {
  const median = Math.round(BOLO_HISTORY.reduce((a, s) => a + s.pct, 0) / BOLO_HISTORY.length);
  return (
    <ScrollBody t={t}>
      <Pad>
        <TopBar t={t} onBack={() => set({ screen: 'home' })} label="Session history" />
        <div style={{ height: 18 }} />
        <ScreenTitle t={t}>Last 6 sessions.</ScreenTitle>

        <div style={{ height: 22 }} />
        <Card t={t}>
          <Caption t={t}>This week vs. last</Caption>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: 14, marginTop: 6 }}>
            <div style={{
              fontFamily: t.fontDisplay, fontSize: 56, lineHeight: 0.95,
              letterSpacing: -1.8, color: t.fg, fontVariantNumeric: 'tabular-nums',
              fontWeight: 500,
            }}>
              {median}<span style={{ fontSize: 22, color: t.muted, marginLeft: 2 }}>%</span>
            </div>
            <Delta t={t} value={+7} />
          </div>
          <div style={{ color: t.muted, fontSize: 13, marginTop: 4 }}>
            median English share across the cohort
          </div>
        </Card>

        <div style={{ height: 24 }} />
        {BOLO_HISTORY.map((s) => (
          <button key={s.id} onClick={() => set({ screen: 'session-detail', detailId: s.id })}
            style={{
              display: 'flex', alignItems: 'center', gap: 14,
              width: '100%', padding: '14px 0', border: 'none', background: 'transparent',
              borderBottom: `1px solid ${t.line}`, cursor: 'pointer', textAlign: 'left',
            }}>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontFamily: t.fontMono, fontSize: 10.5, letterSpacing: 1, color: t.muted, textTransform: 'uppercase' }}>
                {s.date}
              </div>
              <div style={{ fontSize: 15, color: t.fg, marginTop: 4 }}>{s.topic}</div>
              <div style={{ fontSize: 12, color: t.muted, marginTop: 2 }}>
                {s.duration} min · {s.students} students · top: {s.top}
              </div>
            </div>
            <div style={{
              fontFamily: t.fontDisplay, fontSize: 28, color: t.fg, letterSpacing: -0.6,
              fontVariantNumeric: 'tabular-nums',
            }}>
              {s.pct}<span style={{ fontSize: 13, color: t.muted }}>%</span>
            </div>
          </button>
        ))}

        <div style={{ height: 22 }} />
        <Btn t={t} full variant="ghost">Export this month as CSV</Btn>
      </Pad>
    </ScrollBody>
  );
}

// ─────────────────────────────────────────────────────────────
// SESSION DETAIL — one past session, deep
// ─────────────────────────────────────────────────────────────
function SessionDetailScreen({ t, state, set }) {
  const s = BOLO_HISTORY.find((x) => x.id === state.detailId) || BOLO_HISTORY[0];
  const sorted = [...BOLO_COHORT].sort((a, b) => b.pct - a.pct).slice(0, s.students);
  return (
    <ScrollBody t={t}>
      <Pad>
        <TopBar t={t} onBack={() => set({ screen: 'history' })} label={s.date} />
        <div style={{ height: 18 }} />
        <Caption t={t}>{s.topic} · {s.duration} min · {s.students} students</Caption>
        <div style={{ height: 4 }} />
        <ScreenTitle t={t}>{s.pct}% English.</ScreenTitle>

        <div style={{ height: 24 }} />
        {/* English-time timeline */}
        <Card t={t}>
          <Caption t={t}>English share over the session</Caption>
          <div style={{ height: 12 }} />
          <Timeline t={t} duration={s.duration} />
          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 8, fontFamily: t.fontMono, fontSize: 10, color: t.muted }}>
            <span>0:00</span>
            <span>{Math.floor(s.duration / 2)}:00</span>
            <span>{s.duration}:00</span>
          </div>
        </Card>

        <div style={{ height: 24 }} />
        <SectionLabel t={t}>Per student</SectionLabel>
        <div style={{ height: 8 }} />
        {sorted.map((st, i) => (
          <StudentBar key={st.id} t={t} student={st} rank={i + 1} />
        ))}

        <div style={{ height: 24 }} />
        <Card t={t} accent>
          <Caption t={t}>What the room said back</Caption>
          <div style={{
            marginTop: 8, fontFamily: t.fontAccent || t.fontDisplay,
            fontSize: 18, color: t.fg, lineHeight: 1.4,
            fontStyle: 'italic',
          }}>
            “We switched when nobody knew the word for <span style={{ color: t.accentDeep }}>‘interest rate’</span>.”
          </div>
          <div style={{ marginTop: 8, fontSize: 12, color: t.muted }}>
            From the post-session reflection
          </div>
        </Card>

        <div style={{ height: 20 }} />
        <SecondaryMetric t={t} label="Sustained English (≥ 60s)" value="6 stretches" />
        <SecondaryMetric t={t} label="Soft drift events" value="3" />
        <SecondaryMetric t={t} label="Words in English" value={`${s.pct - 3}%`} />
        <SecondaryMetric t={t} label="Synced to cloud" value="✓ 2 min ago" />
      </Pad>
    </ScrollBody>
  );
}

function Timeline({ t, duration }) {
  // Generate a deterministic pattern of english (light) and other (dark) bars.
  const segments = 40;
  const bars = Array.from({ length: segments }).map((_, i) => {
    const noise = Math.sin(i * 1.3) * 0.5 + Math.cos(i * 0.7) * 0.5;
    return noise > -0.1; // English if above threshold
  });
  return (
    <div style={{ display: 'flex', gap: 2, height: 60, alignItems: 'flex-end' }}>
      {bars.map((isEn, i) => (
        <div key={i} style={{
          flex: 1,
          height: isEn ? `${50 + Math.abs(Math.sin(i * 0.9)) * 30}%` : `${20 + Math.abs(Math.cos(i)) * 25}%`,
          background: isEn ? t.accent : t.surfaceAlt,
          borderRadius: 1.5,
        }} />
      ))}
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// PAUSE OVERLAY — modal on Live
// ─────────────────────────────────────────────────────────────
function PauseOverlay({ t, onResume, onEnd }) {
  return (
    <div style={{
      position: 'absolute', inset: 0, zIndex: 5,
      background: 'rgba(20,18,12,0.55)',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      animation: 'bolo-fade-in .2s ease both',
    }}>
      <div style={{
        background: t.surface, borderRadius: t.radius,
        padding: 28, width: 280, textAlign: 'center',
        boxShadow: '0 22px 60px rgba(0,0,0,0.3)',
      }}>
        <div style={{
          fontFamily: t.fontAccent || t.fontDisplay,
          fontSize: 26, fontStyle: 'italic', color: t.fg, letterSpacing: -0.4,
        }}>
          Paused.
        </div>
        <div style={{ marginTop: 8, fontSize: 13, color: t.fgSoft, lineHeight: 1.5 }}>
          The mic is muted. Nothing is being counted right now.
        </div>
        <div style={{ height: 22 }} />
        <Btn t={t} full onClick={onResume}>Resume listening</Btn>
        <div style={{ height: 8 }} />
        <Btn t={t} full variant="quiet" onClick={onEnd}>End the session</Btn>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// SETTINGS — main settings page
// ─────────────────────────────────────────────────────────────
function SettingsScreen({ t, state, set }) {
  return (
    <ScrollBody t={t}>
      <Pad>
        <TopBar t={t} onBack={() => set({ screen: 'home' })} label="Settings" />
        <div style={{ height: 22 }} />
        <ScreenTitle t={t}>Settings.</ScreenTitle>
        <div style={{ height: 24 }} />

        <SectionLabel t={t}>Cohort</SectionLabel>
        <SettingRow t={t} label="Cohort name" value="Pune · Batch 14" />
        <SettingRow t={t} label="Program code" value="NGK-7T4P" mono />
        <SettingRow t={t} label="Facilitator" value="Anjali" />
        <SettingRow t={t} label="Manage students" value="6 enrolled" chevron onClick={() => set({ screen: 'attendance' })} />

        <div style={{ height: 24 }} />
        <SectionLabel t={t}>During a session</SectionLabel>
        <ToggleRow t={t} label="Show soft colour ring" sub="Green when English flows. Amber on drift."
          on={state.ringOn} onChange={(v) => set({ ringOn: v })} />
        <ToggleRow t={t} label="Vibrate on long drift" sub="One pulse after 30s in another language."
          on={state.vibrOn} onChange={(v) => set({ vibrOn: v })} />
        <ToggleRow t={t} label="Show topic switch reminder" sub="Suggest switching topic after 20 min."
          on={true} onChange={() => {}} />

        <div style={{ height: 24 }} />
        <SectionLabel t={t}>Sync</SectionLabel>
        <SettingRow t={t} label="Last sync" value="2 minutes ago" chevron onClick={() => set({ screen: 'sync' })} />
        <SettingRow t={t} label="Only sync on WiFi" value="On" />

        <div style={{ height: 24 }} />
        <SectionLabel t={t}>Privacy</SectionLabel>
        <SettingRow t={t} label="Forget my voice" value="" chevron danger onClick={() => set({ screen: 'forget-voice' })} />
        <SettingRow t={t} label="Export my data" value="" chevron />
        <SettingRow t={t} label="Privacy policy" value="" chevron />

        <div style={{ height: 24 }} />
        <SectionLabel t={t}>About</SectionLabel>
        <SettingRow t={t} label="Version" value="0.1.4 · build 207" mono />
        <SettingRow t={t} label="Acknowledgements" value="" chevron />

        <div style={{ height: 28 }} />
        <div style={{ textAlign: 'center', fontSize: 12, color: t.muted, fontFamily: t.fontMono, letterSpacing: 0.5 }}>
          Made with care for shared classrooms.
        </div>
      </Pad>
    </ScrollBody>
  );
}

function SettingRow({ t, label, value, mono, chevron, danger, onClick }) {
  return (
    <button onClick={onClick} disabled={!onClick}
      style={{
        display: 'flex', alignItems: 'center', gap: 14,
        width: '100%', padding: '14px 0', border: 'none', background: 'transparent',
        borderBottom: `1px solid ${t.line}`,
        cursor: onClick ? 'pointer' : 'default', textAlign: 'left',
      }}>
      <div style={{ flex: 1, fontSize: 14, color: danger ? t.rec : t.fg }}>{label}</div>
      <div style={{
        fontSize: 13, color: t.muted,
        fontFamily: mono ? t.fontMono : t.fontBody,
      }}>{value}</div>
      {chevron && <span style={{ color: t.muted, fontSize: 14 }}>→</span>}
    </button>
  );
}

// ─────────────────────────────────────────────────────────────
// FORGET VOICE — destructive hold-to-confirm
// ─────────────────────────────────────────────────────────────
function ForgetVoiceScreen({ t, state, set }) {
  const [holding, setHolding] = useStateX(false);
  const [pct, setPct] = useStateX(0);
  const [done, setDone] = useStateX(false);

  useEffectX(() => {
    if (!holding || done) return;
    const id = setInterval(() => {
      setPct((p) => {
        if (p >= 100) { setDone(true); clearInterval(id); return 100; }
        return p + 4;
      });
    }, 60);
    return () => clearInterval(id);
  }, [holding, done]);

  useEffectX(() => {
    if (!holding && !done) setPct(0);
  }, [holding]);

  return (
    <ScrollBody t={t}>
      <Pad>
        <TopBar t={t} onBack={() => set({ screen: 'settings' })} label="Forget my voice" />
        <div style={{ height: 22 }} />

        {!done ? (
          <>
            <ScreenTitle t={t}>This is permanent.</ScreenTitle>
            <div style={{ height: 16 }} />
            <p style={{ color: t.fgSoft, fontSize: 15, lineHeight: 1.6, margin: 0 }}>
              We will erase your voice fingerprint from this phone. Past session summaries stay (they're already anonymous), but new sessions won't be able to recognise you until you enroll again.
            </p>

            <div style={{ height: 32 }} />
            <Card t={t}>
              <Caption t={t}>To confirm</Caption>
              <div style={{ marginTop: 8, fontSize: 15, color: t.fg, lineHeight: 1.5 }}>
                Hold the button below for two seconds.
              </div>
            </Card>

            <div style={{ height: 28 }} />
            <button
              onMouseDown={() => setHolding(true)}
              onMouseUp={() => setHolding(false)}
              onMouseLeave={() => setHolding(false)}
              onTouchStart={() => setHolding(true)}
              onTouchEnd={() => setHolding(false)}
              style={{
                position: 'relative', width: '100%', padding: '18px 20px',
                border: 'none', cursor: 'pointer', overflow: 'hidden',
                borderRadius: t.radiusSm, color: '#fff',
                background: t.rec, fontFamily: t.fontBody, fontSize: 16, fontWeight: 500,
              }}>
              <span style={{
                position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.25)',
                width: `${pct}%`, transition: holding ? 'width .06s linear' : 'width .25s ease-out',
              }} />
              <span style={{ position: 'relative' }}>
                {holding ? 'Keep holding…' : 'Hold to forget'}
              </span>
            </button>

            <div style={{ height: 14 }} />
            <Btn t={t} full variant="quiet" onClick={() => set({ screen: 'settings' })}>
              Never mind
            </Btn>
          </>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 24, marginTop: 60, textAlign: 'center' }}>
            <div style={{
              width: 64, height: 64, borderRadius: '50%',
              background: t.surfaceAlt, display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}>
              <svg width="28" height="28" viewBox="0 0 28 28" fill="none" stroke={t.muted} strokeWidth="2" strokeLinecap="round">
                <path d="M6 6 L22 22 M22 6 L6 22" />
              </svg>
            </div>
            <ScreenTitle t={t} align="center">Forgotten.</ScreenTitle>
            <p style={{ color: t.fgSoft, fontSize: 14, lineHeight: 1.5, margin: 0, maxWidth: 260 }}>
              Your voice fingerprint is gone. You can enroll again any time.
            </p>
            <Btn t={t} full onClick={() => set({ screen: 'home' })}>Back to home</Btn>
          </div>
        )}
      </Pad>
    </ScrollBody>
  );
}

// ─────────────────────────────────────────────────────────────
// SYNC — show what's queued and last sync
// ─────────────────────────────────────────────────────────────
function SyncScreen({ t, state, set }) {
  return (
    <ScrollBody t={t}>
      <Pad>
        <TopBar t={t} onBack={() => set({ screen: 'settings' })} label="Sync" />
        <div style={{ height: 22 }} />
        <ScreenTitle t={t}>All caught up.</ScreenTitle>
        <div style={{ height: 14 }} />
        <p style={{ color: t.fgSoft, fontSize: 14, lineHeight: 1.55, margin: 0 }}>
          Bolo only syncs session summaries — JSON files about two kilobytes each. Audio is never uploaded. Voice fingerprints never leave the phone.
        </p>

        <div style={{ height: 28 }} />
        <Card t={t}>
          <Caption t={t}>Last sync</Caption>
          <div style={{ fontFamily: t.fontDisplay, fontSize: 28, color: t.fg, marginTop: 6, letterSpacing: -0.5, fontWeight: 500 }}>
            2 minutes ago
          </div>
          <div style={{ marginTop: 10, color: t.muted, fontSize: 13 }}>
            12 sessions uploaded · 24.3 KB total
          </div>
        </Card>

        <div style={{ height: 22 }} />
        <SectionLabel t={t}>Queue</SectionLabel>
        <div style={{ height: 14 }} />
        <SyncQueueRow t={t} status="ok" label="Today · 9:30 · Daily life" detail="2.1 KB · synced" />
        <SyncQueueRow t={t} status="ok" label="Yesterday · 4:10 · Mock interview" detail="2.3 KB · synced" />
        <SyncQueueRow t={t} status="ok" label="Mon · 9:30 · News chat" detail="2.0 KB · synced" />

        <div style={{ height: 28 }} />
        <Btn t={t} full variant="ghost">Sync now</Btn>
        <div style={{ height: 10 }} />
        <PrivacyNote t={t}>
          Sync runs in the background on WiFi only. You can keep using Bolo offline.
        </PrivacyNote>
      </Pad>
    </ScrollBody>
  );
}

function SyncQueueRow({ t, status, label, detail }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', gap: 14, padding: '12px 0',
      borderBottom: `1px solid ${t.line}`,
    }}>
      <span style={{
        width: 8, height: 8, borderRadius: '50%',
        background: status === 'ok' ? t.accent : t.warn,
      }} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 14, color: t.fg }}>{label}</div>
        <div style={{ fontSize: 12, color: t.muted, marginTop: 2, fontFamily: t.fontMono }}>{detail}</div>
      </div>
    </div>
  );
}

Object.assign(window, {
  SplashScreen, PairScreen, ConsentScreen, MicPermScreen, CohortSetupScreen,
  AttendanceScreen, AddStudentScreen, HistoryScreen, SessionDetailScreen,
  PauseOverlay, SettingsScreen, ForgetVoiceScreen, SyncScreen,
});
