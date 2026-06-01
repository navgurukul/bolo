// bolo-screens.jsx — every screen in the Bolo flow.
// Each screen is a pure component taking {t (theme), state, set}. State
// changes flow up through `set`. Screens are intentionally chrome-light;
// the Phone wrapper handles status bar + breathing mic dot.

const { useEffect, useRef, useState, useMemo } = React;

// ─────────────────────────────────────────────────────────────
// Shared atoms
// ─────────────────────────────────────────────────────────────

// Tap target. Variants: solid (accent fill), ghost (line), quiet (text-only).
function Btn({ t, variant = 'solid', children, onClick, full, danger, style }) {
  const base = {
    border: 'none',
    cursor: 'pointer',
    fontFamily: t.fontBody,
    fontSize: 16,
    fontWeight: 500,
    letterSpacing: -0.1,
    padding: '16px 24px',
    borderRadius: t.radiusSm,
    transition: 'transform .15s ease, opacity .15s ease, background .15s ease',
    width: full ? '100%' : 'auto',
    boxSizing: 'border-box',
  };
  const styles = {
    solid: {
      background: danger ? t.rec : t.accent,
      color: '#fff',
    },
    ghost: {
      background: 'transparent',
      color: t.fg,
      boxShadow: `inset 0 0 0 1px ${t.lineStrong}`,
    },
    quiet: {
      background: 'transparent',
      color: t.fgSoft,
      padding: '12px 8px',
    },
    pill: {
      background: t.surface,
      color: t.fg,
      boxShadow: `inset 0 0 0 1px ${t.line}`,
      borderRadius: 100,
      padding: '10px 16px',
      fontSize: 14,
      fontWeight: 500,
    },
  };
  return (
    <button onClick={onClick}
      onMouseDown={(e) => (e.currentTarget.style.transform = 'scale(0.98)')}
      onMouseUp={(e) => (e.currentTarget.style.transform = 'scale(1)')}
      onMouseLeave={(e) => (e.currentTarget.style.transform = 'scale(1)')}
      style={{ ...base, ...styles[variant], ...style }}>
      {children}
    </button>
  );
}

function Avatar({ t, name, size = 36, active }) {
  const initial = name[0];
  const hue = (name.charCodeAt(0) * 23) % 360;
  return (
    <div style={{
      width: size, height: size, borderRadius: '50%',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      fontSize: size * 0.42, fontWeight: 500, color: t.fg,
      background: t.surfaceAlt,
      boxShadow: active
        ? `inset 0 0 0 2px ${t.accent}, 0 0 0 3px ${t.accentSoft}`
        : `inset 0 0 0 1px ${t.line}`,
      fontFamily: t.fontBody,
      flexShrink: 0,
    }}>{initial}</div>
  );
}

function ScreenTitle({ t, eyebrow, children, align = 'left' }) {
  return (
    <div style={{ textAlign: align }}>
      {eyebrow && (
        <div style={{
          fontFamily: t.fontMono, fontSize: 11, letterSpacing: 1.5,
          textTransform: 'uppercase', color: t.muted, marginBottom: 12,
        }}>{eyebrow}</div>
      )}
      <h1 style={{
        margin: 0, fontFamily: t.fontDisplay,
        fontSize: t.density === 'editorial' ? 38 : 42,
        fontWeight: t.fontDisplay.includes('Geist') ? 500 : 400,
        letterSpacing: t.fontDisplay.includes('Geist') ? -1.2 : -0.5,
        lineHeight: 1.05, color: t.fg,
      }}>{children}</h1>
    </div>
  );
}

// Small horizontal rule that adapts to the variant.
function Rule({ t, dim }) {
  return <div style={{ height: 1, background: dim ? t.line : t.lineStrong, width: '100%' }} />;
}

// Format mm:ss
function fmt(s) {
  const m = Math.floor(s / 60), r = s % 60;
  return `${m}:${r.toString().padStart(2, '0')}`;
}


// ─────────────────────────────────────────────────────────────
// HOME — landing surface, big "Start" + last session
// ─────────────────────────────────────────────────────────────
function HomeScreen({ t, state, set }) {
  const last = state.lastSession;
  return (
    <ScrollBody t={t}>
      <Pad>
        <Wordmark t={t} />
        <div style={{ height: 40 }} />
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ color: t.muted, fontSize: 14, fontFamily: t.fontMono, letterSpacing: 0.5 }}>
            Tuesday · 9:30 AM
          </div>
          <div style={{
            display: 'inline-flex', alignItems: 'center', gap: 6,
            padding: '4px 10px', borderRadius: 100,
            background: t.accentSoft, color: t.accentDeep,
            fontFamily: t.fontMono, fontSize: 10.5, letterSpacing: 0.6,
          }}>
            <span style={{ width: 5, height: 5, borderRadius: '50%', background: t.accent }} />
            SYNCED
          </div>
        </div>
        <div style={{ height: 8 }} />
        <ScreenTitle t={t}>Good morning,<br/>let's speak some English.</ScreenTitle>
        <div style={{ height: 28 }} />
        <Btn t={t} full onClick={() => set({ screen: 'start' })}>
          Start a session →
        </Btn>
        <div style={{ height: 6 }} />
        <div style={{ fontFamily: t.fontMono, fontSize: 11, color: t.muted, letterSpacing: 0.5, textAlign: 'center' }}>
          Pune · Batch 14 · 6 students
        </div>
        <div style={{ height: 28 }} />

        {/* Last session card */}
        <Card t={t}>
          <Caption t={t}>Last session</Caption>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: 10, marginTop: 8 }}>
            <BigPct t={t} value={last.pct} />
            <div style={{ color: t.muted, fontSize: 13 }}>English · {last.duration} min</div>
          </div>
          <div style={{ marginTop: 14, color: t.fgSoft, fontSize: 14 }}>
            {last.topic} · 6 students
          </div>
          <div style={{ height: 12 }} />
          <BarRow t={t} value={last.pct} />
        </Card>

        <div style={{ height: 28 }} />

        {/* Cohort */}
        <SectionLabel t={t}>Your cohort</SectionLabel>
        <div style={{ display: 'flex', gap: 12, marginTop: 14, flexWrap: 'wrap' }}>
          {BOLO_COHORT.map((s) => (
            <div key={s.id} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6 }}>
              <Avatar t={t} name={s.name} size={44} />
              <div style={{ fontSize: 12, color: t.fgSoft }}>{s.name}</div>
            </div>
          ))}
        </div>

        <div style={{ height: 24 }} />
        <Rule t={t} dim />
        <div style={{ height: 16 }} />
        <HomeLink t={t} onClick={() => set({ screen: 'dashboard' })} label="See your progress" />
        <Rule t={t} dim />
        <HomeLink t={t} onClick={() => set({ screen: 'history' })} label="Past sessions" hint="6 this month" />
        <Rule t={t} dim />
        <HomeLink t={t} onClick={() => set({ screen: 'enroll' })} label="Enroll a new voice" />
        <Rule t={t} dim />
        <HomeLink t={t} onClick={() => set({ screen: 'settings' })} label="Settings" />

        <div style={{ height: 24 }} />
        <PrivacyNote t={t}>
          Bolo never records or stores audio. It listens, classifies, and forgets — in real time.
        </PrivacyNote>
      </Pad>
    </ScrollBody>
  );
}


// ─────────────────────────────────────────────────────────────
// ENROLL — voice fingerprint setup
// ─────────────────────────────────────────────────────────────
function EnrollScreen({ t, state, set }) {
  const [step, setStep] = useState('intro'); // intro | recording | done
  const [hold, setHold] = useState(0);

  useEffect(() => {
    if (step !== 'recording') return;
    const id = setInterval(() => setHold((h) => {
      if (h >= 100) { clearInterval(id); setStep('done'); return 100; }
      return h + 4;
    }), 90);
    return () => clearInterval(id);
  }, [step]);

  return (
    <ScrollBody t={t}>
      <Pad>
        <TopBar t={t} onBack={() => set({ screen: 'home' })} label="Enroll a voice" />
        <div style={{ height: 32 }} />

        {step === 'intro' && (
          <>
            <ScreenTitle t={t} eyebrow="Step 1 of 1">Welcome,<br/>let's learn your voice.</ScreenTitle>
            <div style={{ height: 18 }} />
            <p style={{ color: t.fgSoft, fontSize: 16, lineHeight: 1.55, margin: 0, maxWidth: 320 }}>
              Read the sentence below out loud. About ten seconds. We turn it into a number that lives only on this phone.
            </p>
            <div style={{ height: 28 }} />
            <Card t={t} accent>
              <Caption t={t}>Read aloud</Caption>
              <div style={{
                marginTop: 10, fontFamily: t.fontAccent || t.fontDisplay,
                fontSize: 26, lineHeight: 1.25, color: t.fg,
                fontStyle: 'italic',
                letterSpacing: -0.4,
              }}>
                “The afternoon was warm and the tea had gone cold.”
              </div>
            </Card>
            <div style={{ height: 28 }} />
            <Btn t={t} full onClick={() => { setHold(0); setStep('recording'); }}>
              Hold to record
            </Btn>
            <div style={{ height: 14 }} />
            <PrivacyNote t={t}>
              Stored as an encrypted vector. Audio is never written to disk. Tap “Forget my voice” any time to wipe it.
            </PrivacyNote>
          </>
        )}

        {step === 'recording' && (
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 24, marginTop: 40 }}>
            <div style={{ position: 'relative', width: 220, height: 220 }}>
              <RingRecorder t={t} pct={hold} />
            </div>
            <div style={{ fontFamily: t.fontMono, fontSize: 12, color: t.muted, letterSpacing: 1.5, textTransform: 'uppercase' }}>
              Listening · keep reading
            </div>
            <div style={{
              fontFamily: t.fontAccent || t.fontDisplay, fontSize: 22, color: t.fgSoft, lineHeight: 1.3,
              textAlign: 'center', maxWidth: 280,
              fontStyle: 'italic',
            }}>
              “The afternoon was warm<br/>and the tea had gone cold.”
            </div>
            <Waveform t={t} active />
          </div>
        )}

        {step === 'done' && (
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 24, marginTop: 60, textAlign: 'center' }}>
            <Check t={t} />
            <ScreenTitle t={t} align="center">You're in.</ScreenTitle>
            <p style={{ color: t.fgSoft, fontSize: 15, lineHeight: 1.55, margin: 0, maxWidth: 280 }}>
              Bolo knows your voice now. Next time the phone is in the room, your time in English will count toward your trend.
            </p>
            <div style={{ height: 8 }} />
            <Btn t={t} full onClick={() => set({ screen: 'home' })}>
              Back to home
            </Btn>
          </div>
        )}
      </Pad>
    </ScrollBody>
  );
}


// ─────────────────────────────────────────────────────────────
// START — pick a topic, glance at cohort, begin
// ─────────────────────────────────────────────────────────────
function StartScreen({ t, state, set }) {
  const [topic, setTopic] = useState(state.topic || 'Daily life');

  return (
    <ScrollBody t={t}>
      <Pad>
        <TopBar t={t} onBack={() => set({ screen: 'home' })} label="New session" />
        <div style={{ height: 24 }} />
        <ScreenTitle t={t} eyebrow="What are we talking about?">Pick a topic<br/>or write your own.</ScreenTitle>
        <div style={{ height: 22 }} />

        {/* Topic pills */}
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
          {BOLO_TOPICS.map((tp) => {
            const on = tp === topic;
            return (
              <button key={tp} onClick={() => setTopic(tp)}
                style={{
                  border: 'none', cursor: 'pointer',
                  padding: '10px 16px', borderRadius: 100,
                  fontFamily: t.fontBody, fontSize: 14, fontWeight: 500,
                  background: on ? t.fg : t.surface,
                  color: on ? t.bg : t.fg,
                  boxShadow: on ? 'none' : `inset 0 0 0 1px ${t.line}`,
                  transition: 'all .15s ease',
                }}>{tp}</button>
            );
          })}
        </div>

        <div style={{ height: 18 }} />
        <input
          type="text"
          placeholder="…or type a fresh topic"
          onChange={(e) => e.target.value && setTopic(e.target.value)}
          style={{
            width: '100%', boxSizing: 'border-box',
            background: 'transparent', border: 'none',
            borderBottom: `1px solid ${t.lineStrong}`,
            padding: '10px 0', fontFamily: t.fontBody,
            color: t.fg, fontSize: 16, outline: 'none',
          }}
        />

        <div style={{ height: 28 }} />
        <SectionLabel t={t}>In the room</SectionLabel>
        <div style={{ display: 'flex', gap: -4, marginTop: 14 }}>
          {BOLO_COHORT.map((s, i) => (
            <div key={s.id} style={{ marginLeft: i === 0 ? 0 : -10 }}>
              <Avatar t={t} name={s.name} size={40} />
            </div>
          ))}
          <div style={{ marginLeft: 12, fontSize: 13, color: t.fgSoft, alignSelf: 'center' }}>
            6 students enrolled
          </div>
        </div>

        <div style={{ height: 24 }} />
        <Card t={t}>
          <Caption t={t}>Live cues</Caption>
          <ToggleRow t={t} label="Soft colour ring on the screen" sub="Green when English flows. Amber if the room drifts."
            on={state.ringOn} onChange={(v) => set({ ringOn: v })} />
          <ToggleRow t={t} label="Gentle vibration on long drift" sub="One pulse after 30s in another language."
            on={state.vibrOn} onChange={(v) => set({ vibrOn: v })} />
        </Card>

        <div style={{ height: 28 }} />
        <Btn t={t} full onClick={() => set({ screen: 'attendance', topic })}>
          Next: who's here? →
        </Btn>
        <div style={{ height: 14 }} />
        <PrivacyNote t={t}>
          A red dot stays visible whenever the mic is live. Audio never leaves the device.
        </PrivacyNote>
      </Pad>
    </ScrollBody>
  );
}


// ─────────────────────────────────────────────────────────────
// LIVE — the recording screen. Ambient ring, breathing dot, speaker.
// ─────────────────────────────────────────────────────────────
function LiveScreen({ t, state, set }) {
  const [elapsed, setElapsed] = useState(state.elapsed || 0);
  const [speakerIdx, setSpeakerIdx] = useState(2);
  const [pulse, setPulse] = useState(0.78);
  const [paused, setPaused] = useState(false);
  const [topicOpen, setTopicOpen] = useState(false);
  const [topic, setTopic] = useState(state.topic || 'Daily life');
  const drift = pulse < 0.45;

  useEffect(() => {
    if (paused) return;
    const id = setInterval(() => {
      setElapsed((e) => e + 1);
      setSpeakerIdx((i) => Math.random() < 0.16 ? Math.floor(Math.random() * 6) : i);
      setPulse((p) => {
        const target = 0.62 + Math.sin(Date.now() / 4500) * 0.22;
        return p + (target - p) * 0.25;
      });
    }, 1000);
    return () => clearInterval(id);
  }, [paused]);

  const speaker = BOLO_COHORT[speakerIdx];
  const ringColor = drift ? t.warn : t.accent;

  return (
    <FullBody t={t}>
      {/* Ambient ring — soft inner glow on the content */}
      <div style={{
        position: 'absolute', inset: 8, borderRadius: t.radius - 6,
        boxShadow: `inset 0 0 60px 6px ${ringColor}33, inset 0 0 0 1.5px ${ringColor}66`,
        animation: 'bolo-ring-pulse 3.6s ease-in-out infinite',
        pointerEvents: 'none', transition: 'box-shadow .8s ease',
      }} />

      <div style={{
        position: 'relative', zIndex: 1, padding: '24px 28px 24px',
        display: 'flex', flexDirection: 'column', height: '100%', boxSizing: 'border-box',
      }}>
        {/* Top — topic (tap to switch) + breathing dot + elapsed */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <span style={{
              width: 8, height: 8, borderRadius: '50%', background: paused ? t.muted : t.rec,
              animation: paused ? 'none' : 'bolo-breathe 1.8s ease-in-out infinite', display: 'inline-block',
            }} />
            <button onClick={() => setTopicOpen(true)} style={{
              background: 'transparent', border: 'none', cursor: 'pointer', padding: 0,
              fontFamily: t.fontMono, fontSize: 11, letterSpacing: 1.5, color: t.fgSoft, textTransform: 'uppercase',
              display: 'inline-flex', alignItems: 'center', gap: 6,
            }}>
              <span>{paused ? 'Paused' : 'Mic on'} · {topic}</span>
              <svg width="9" height="9" viewBox="0 0 9 9" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round">
                <path d="M1.5 3 L4.5 6 L7.5 3" />
              </svg>
            </button>
          </div>
          <button onClick={() => setPaused((p) => !p)} style={{
            background: 'transparent', border: 'none', cursor: 'pointer', padding: '6px 10px',
            borderRadius: 100, boxShadow: `inset 0 0 0 1px ${t.line}`,
            fontFamily: t.fontMono, fontSize: 12, color: t.fg, letterSpacing: 0.3,
            display: 'inline-flex', alignItems: 'center', gap: 6,
          }}>
            {paused ? '▶' : 'II'}
            <span style={{ fontVariantNumeric: 'tabular-nums' }}>{fmt(elapsed)}</span>
          </button>
        </div>

        {/* Big elapsed numeral */}
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', textAlign: 'center' }}>
          <div style={{
            fontFamily: t.fontDisplay,
            fontSize: 96, lineHeight: 0.95, letterSpacing: -3,
            color: t.fg, fontWeight: 400,
            fontVariantNumeric: 'tabular-nums',
          }}>{fmt(elapsed)}</div>
          <div style={{ marginTop: 8, fontSize: 14, color: t.muted, fontFamily: t.fontMono, letterSpacing: 0.5 }}>
            elapsed
          </div>

          <div style={{ height: 36 }} />

          {/* Now speaking */}
          <Caption t={t}>{drift ? 'Soft drift detected' : 'Now speaking'}</Caption>
          <div style={{ marginTop: 10, display: 'flex', alignItems: 'center', gap: 12 }}>
            <Avatar t={t} name={speaker.name} size={44} active />
            <div style={{ fontFamily: t.fontDisplay, fontSize: 22, color: t.fg, letterSpacing: -0.3 }}>
              {speaker.name}
            </div>
          </div>

          <div style={{ height: 28 }} />

          {/* Live English share meter */}
          <div style={{ width: '100%', maxWidth: 280 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 11, fontFamily: t.fontMono, color: t.muted, letterSpacing: 0.5, marginBottom: 8 }}>
              <span>ROOM IN ENGLISH</span>
              <span>{Math.round(pulse * 100)}%</span>
            </div>
            <div style={{
              height: 4, background: t.surfaceAlt, borderRadius: 4, position: 'relative', overflow: 'hidden',
            }}>
              <div style={{
                position: 'absolute', top: 0, left: 0, bottom: 0,
                width: `${pulse * 100}%`, background: ringColor,
                transition: 'width .8s ease, background .8s ease',
              }} />
            </div>
          </div>
        </div>

        {/* End button */}
        <Btn t={t} full danger onClick={() => set({ screen: 'summary', elapsed, isLive: false, topic })}>
          End session
        </Btn>
        <div style={{ height: 8 }} />
        <div style={{ textAlign: 'center', fontSize: 11, color: t.muted, fontFamily: t.fontMono, letterSpacing: 1, textTransform: 'uppercase' }}>
          No audio is being saved
        </div>
      </div>

      {paused && (
        <PauseOverlay t={t}
          onResume={() => setPaused(false)}
          onEnd={() => set({ screen: 'summary', elapsed, isLive: false, topic })}
        />
      )}
      {topicOpen && (
        <TopicSwitchSheet t={t} current={topic}
          onPick={(tp) => { setTopic(tp); setTopicOpen(false); }}
          onClose={() => setTopicOpen(false)}
        />
      )}
    </FullBody>
  );
}


// ─────────────────────────────────────────────────────────────
// SUMMARY — end-of-session
// ─────────────────────────────────────────────────────────────
function SummaryScreen({ t, state, set }) {
  // Treat per-student bars as today's session result.
  const sorted = [...BOLO_COHORT].sort((a, b) => b.pct - a.pct);
  const top = sorted[0];
  const groupPct = 71;
  const duration = Math.max(1, Math.round((state.elapsed || 1800) / 60));

  return (
    <ScrollBody t={t}>
      <Pad>
        <div style={{ height: 12 }} />
        <Caption t={t}>{state.topic} · {duration} min</Caption>
        <div style={{ height: 6 }} />
        <ScreenTitle t={t}>Nice work.</ScreenTitle>

        <div style={{ height: 28 }} />
        <div>
          <div style={{
            fontFamily: t.fontDisplay, fontSize: 120, lineHeight: 0.9,
            letterSpacing: -5, color: t.fg, fontWeight: 400,
            fontVariantNumeric: 'tabular-nums',
            display: 'flex', alignItems: 'baseline',
          }}>
            {groupPct}
            <span style={{ fontSize: 44, color: t.muted, marginLeft: 4, letterSpacing: -1 }}>%</span>
          </div>
          <div style={{ marginTop: 4, color: t.fgSoft, fontSize: 15 }}>
            of speaking time, in English
          </div>
        </div>

        <div style={{ height: 28 }} />

        {/* Top speaker — celebratory */}
        <Card t={t} accent>
          <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
            <Avatar t={t} name={top.name} size={48} active />
            <div style={{ flex: 1 }}>
              <Caption t={t}>Most English today</Caption>
              <div style={{ fontFamily: t.fontDisplay, fontSize: 22, color: t.fg, marginTop: 2, letterSpacing: -0.3 }}>
                {top.name} · {top.pct}%
              </div>
            </div>
          </div>
        </Card>

        <div style={{ height: 28 }} />
        <SectionLabel t={t}>Everyone today</SectionLabel>
        <div style={{ height: 10 }} />
        {sorted.map((s, i) => (
          <StudentBar key={s.id} t={t} student={s} rank={i + 1} />
        ))}

        <div style={{ height: 28 }} />
        <Card t={t}>
          <Caption t={t}>30-second reflection</Caption>
          <div style={{
            marginTop: 8, fontFamily: t.fontAccent || t.fontDisplay, fontSize: 22,
            color: t.fg, lineHeight: 1.35, letterSpacing: -0.3,
            fontStyle: 'italic',
          }}>
            Which moment did you switch — and what word was missing?
          </div>
        </Card>

        <div style={{ height: 20 }} />
        <SecondaryMetric t={t} label="Words in English" value="64%" />
        <SecondaryMetric t={t} label="Sustained English stretches" value="4 of 6 min+" />
        <SecondaryMetric t={t} label="Soft drifts" value="3" />

        <div style={{ height: 28 }} />
        <Btn t={t} full onClick={() => set({ screen: 'dashboard' })}>
          See your trend
        </Btn>
        <div style={{ height: 10 }} />
        <Btn t={t} full variant="quiet" onClick={() => set({ screen: 'home' })}>
          Back to home
        </Btn>
      </Pad>
    </ScrollBody>
  );
}


// ─────────────────────────────────────────────────────────────
// DASHBOARD — personal trend
// ─────────────────────────────────────────────────────────────
function DashboardScreen({ t, state, set }) {
  const [selected, setSelected] = useState('asha');
  const [filter, setFilter] = useState('All topics');
  const student = BOLO_COHORT.find((s) => s.id === selected);
  const weekly = student.weekly;
  const thisWeek = weekly[weekly.length - 1];
  const lastWeek = weekly[weekly.length - 2];
  const delta = thisWeek - lastWeek;

  return (
    <ScrollBody t={t}>
      <Pad>
        <TopBar t={t} onBack={() => set({ screen: 'home' })} label="Your progress" />
        <div style={{ height: 20 }} />

        {/* Student picker */}
        <div style={{ display: 'flex', gap: 8, overflowX: 'auto', paddingBottom: 4 }}>
          {BOLO_COHORT.map((s) => {
            const on = s.id === selected;
            return (
              <button key={s.id} onClick={() => setSelected(s.id)}
                style={{
                  border: 'none', cursor: 'pointer', flexShrink: 0,
                  padding: '8px 14px 8px 8px', borderRadius: 100,
                  display: 'flex', alignItems: 'center', gap: 8,
                  background: on ? t.fg : 'transparent',
                  color: on ? t.bg : t.fg,
                  boxShadow: on ? 'none' : `inset 0 0 0 1px ${t.line}`,
                  fontFamily: t.fontBody, fontSize: 14, fontWeight: 500,
                }}>
                <Avatar t={t} name={s.name} size={24} />
                {s.name}
              </button>
            );
          })}
        </div>

        <div style={{ height: 28 }} />
        <Caption t={t}>This week, in English</Caption>
        <div style={{
          display: 'flex', alignItems: 'baseline', gap: 14, marginTop: 6,
        }}>
          <div style={{
            fontFamily: t.fontDisplay, fontSize: 96, lineHeight: 0.9,
            letterSpacing: -3, color: t.fg, fontVariantNumeric: 'tabular-nums',
          }}>
            {thisWeek}
            <span style={{ fontSize: 38, color: t.muted, marginLeft: 2 }}>%</span>
          </div>
          <Delta t={t} value={delta} />
        </div>

        <div style={{ height: 28 }} />
        <TrendChart t={t} weekly={weekly} />

        <div style={{ height: 24 }} />
        <SectionLabel t={t}>By topic</SectionLabel>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginTop: 12 }}>
          {['All topics', 'Daily life', 'Mock interview', 'News chat'].map((f) => {
            const on = f === filter;
            return (
              <button key={f} onClick={() => setFilter(f)}
                style={{
                  border: 'none', cursor: 'pointer',
                  padding: '8px 14px', borderRadius: 100,
                  fontFamily: t.fontBody, fontSize: 13, fontWeight: 500,
                  background: on ? t.accentSoft : t.surface,
                  color: on ? t.accentDeep : t.fgSoft,
                  boxShadow: on ? 'none' : `inset 0 0 0 1px ${t.line}`,
                }}>{f}</button>
            );
          })}
        </div>

        <div style={{ height: 24 }} />
        <Card t={t}>
          <Caption t={t}>Personal best</Caption>
          <div style={{ fontFamily: t.fontDisplay, fontSize: 22, color: t.fg, marginTop: 6, letterSpacing: -0.3 }}>
            {Math.max(...weekly)}% English on a Wednesday Tech chat.
          </div>
          <div style={{ marginTop: 6, color: t.muted, fontSize: 13 }}>
            Three weeks ago.
          </div>
        </Card>

        <div style={{ height: 28 }} />
        <Rule t={t} dim />
        <button onClick={() => alert('Voice fingerprint wiped.')}
          style={{
            background: 'transparent', border: 'none', cursor: 'pointer',
            display: 'flex', justifyContent: 'space-between', alignItems: 'center',
            width: '100%', padding: '14px 0', fontFamily: t.fontBody,
            color: t.fgSoft, fontSize: 14,
          }}>
          <span>Forget my voice</span>
          <span style={{ color: t.muted }}>→</span>
        </button>
        <Rule t={t} dim />
      </Pad>
    </ScrollBody>
  );
}


// ─────────────────────────────────────────────────────────────
// Internal building blocks
// ─────────────────────────────────────────────────────────────

function ScrollBody({ t, children }) {
  return (
    <div style={{ flex: 1, overflowY: 'auto', background: t.bg, color: t.fg, fontFamily: t.fontBody }}>
      {children}
    </div>
  );
}
function FullBody({ t, children }) {
  return (
    <div style={{ flex: 1, position: 'relative', overflow: 'hidden', background: t.bg, color: t.fg, fontFamily: t.fontBody }}>
      {children}
    </div>
  );
}
function Pad({ children }) {
  return <div style={{ padding: '20px 24px 32px' }}>{children}</div>;
}

function HomeLink({ t, onClick, label, hint }) {
  return (
    <button onClick={onClick}
      style={{
        background: 'transparent', border: 'none', cursor: 'pointer',
        display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 12,
        width: '100%', padding: '14px 0', fontFamily: t.fontBody, color: t.fg, fontSize: 15,
        textAlign: 'left',
      }}>
      <span>{label}</span>
      <span style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
        {hint && <span style={{ fontSize: 12, color: t.muted, fontFamily: t.fontMono }}>{hint}</span>}
        <span style={{ color: t.muted }}>→</span>
      </span>
    </button>
  );
}

function Wordmark({ t }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <span style={{
        fontFamily: t.fontAccent || t.fontDisplay,
        fontSize: 30, color: t.fg, letterSpacing: -0.6,
        fontStyle: 'italic', fontWeight: 400, lineHeight: 1,
      }}>
        Bolo
        <span style={{ color: t.accent, marginLeft: 1, fontStyle: 'normal' }}>.</span>
      </span>
      <button style={{
        background: 'transparent', border: 'none', cursor: 'pointer',
        width: 36, height: 36, borderRadius: '50%', display: 'flex',
        alignItems: 'center', justifyContent: 'center',
        boxShadow: `inset 0 0 0 1px ${t.line}`,
      }}>
        <svg width="14" height="14" viewBox="0 0 16 16" fill="none" stroke={t.fg} strokeWidth="1.6" strokeLinecap="round">
          <circle cx="8" cy="4" r="1.2" />
          <circle cx="8" cy="8" r="1.2" />
          <circle cx="8" cy="12" r="1.2" />
        </svg>
      </button>
    </div>
  );
}

function TopBar({ t, onBack, label }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 12, paddingTop: 8 }}>
      <button onClick={onBack} style={{
        background: 'transparent', border: 'none', cursor: 'pointer',
        padding: 0, color: t.fg, display: 'flex', alignItems: 'center', justifyContent: 'center',
        width: 36, height: 36, borderRadius: '50%',
        boxShadow: `inset 0 0 0 1px ${t.line}`,
      }}>
        <svg width="14" height="14" viewBox="0 0 16 16" fill="none" stroke={t.fg} strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round">
          <path d="M10 3 L5 8 L10 13" />
        </svg>
      </button>
      <div style={{ fontFamily: t.fontMono, fontSize: 11, letterSpacing: 1.5, color: t.muted, textTransform: 'uppercase' }}>
        {label}
      </div>
    </div>
  );
}

function Card({ t, accent, children }) {
  return (
    <div style={{
      background: accent ? t.accentSoft : t.surface,
      borderRadius: t.radius,
      padding: 20,
      boxShadow: accent ? 'none' : `inset 0 0 0 1px ${t.line}`,
    }}>
      {children}
    </div>
  );
}

function Caption({ t, children }) {
  return (
    <div style={{
      fontFamily: t.fontMono, fontSize: 10.5, letterSpacing: 1.4,
      color: t.muted, textTransform: 'uppercase',
    }}>{children}</div>
  );
}

function SectionLabel({ t, children }) {
  return (
    <div style={{
      fontFamily: t.fontDisplay, fontSize: 14, color: t.fgSoft, letterSpacing: -0.2,
      textTransform: t.density === 'editorial' ? 'uppercase' : 'none',
      letterSpacing: t.density === 'editorial' ? 2 : -0.2,
    }}>{children}</div>
  );
}

function BigPct({ t, value }) {
  return (
    <div style={{
      fontFamily: t.fontDisplay, fontSize: 46, lineHeight: 0.95, letterSpacing: -1.5,
      color: t.fg, fontVariantNumeric: 'tabular-nums',
    }}>
      {value}<span style={{ fontSize: 22, color: t.muted, marginLeft: 1 }}>%</span>
    </div>
  );
}

function BarRow({ t, value }) {
  return (
    <div style={{ height: 4, background: t.surfaceAlt, borderRadius: 4, overflow: 'hidden' }}>
      <div style={{ width: `${value}%`, height: '100%', background: t.accent }} />
    </div>
  );
}

function StudentBar({ t, student, rank }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', gap: 14, padding: '10px 0',
      borderBottom: `1px solid ${t.line}`,
    }}>
      <span style={{ fontFamily: t.fontMono, fontSize: 11, color: t.muted, width: 18 }}>
        {String(rank).padStart(2, '0')}
      </span>
      <Avatar t={t} name={student.name} size={28} />
      <div style={{ flex: 1, fontSize: 14, color: t.fg }}>{student.name}</div>
      <div style={{ width: 110 }}>
        <BarRow t={t} value={student.pct} />
      </div>
      <div style={{ fontFamily: t.fontMono, fontSize: 13, color: t.fg, width: 36, textAlign: 'right', fontVariantNumeric: 'tabular-nums' }}>
        {student.pct}%
      </div>
    </div>
  );
}

function SecondaryMetric({ t, label, value }) {
  return (
    <div style={{
      display: 'flex', justifyContent: 'space-between', alignItems: 'baseline',
      padding: '12px 0', borderBottom: `1px solid ${t.line}`,
    }}>
      <span style={{ color: t.fgSoft, fontSize: 14 }}>{label}</span>
      <span style={{ fontFamily: t.fontMono, color: t.fg, fontSize: 14 }}>{value}</span>
    </div>
  );
}

function ToggleRow({ t, label, sub, on, onChange }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', gap: 14,
      padding: '14px 0', borderBottom: `1px solid ${t.line}`,
    }}>
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 14, color: t.fg }}>{label}</div>
        <div style={{ fontSize: 12, color: t.muted, marginTop: 2, lineHeight: 1.4 }}>{sub}</div>
      </div>
      <button onClick={() => onChange(!on)} style={{
        width: 38, height: 22, borderRadius: 11, padding: 0, border: 'none', cursor: 'pointer',
        background: on ? t.accent : t.surfaceAlt,
        position: 'relative', transition: 'background .15s',
      }}>
        <span style={{
          position: 'absolute', top: 2, left: on ? 18 : 2,
          width: 18, height: 18, borderRadius: '50%',
          background: '#fff', transition: 'left .18s ease',
          boxShadow: '0 1px 3px rgba(0,0,0,0.18)',
        }} />
      </button>
    </div>
  );
}

function PrivacyNote({ t, children }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'flex-start', gap: 10,
      color: t.muted, fontSize: 12, lineHeight: 1.5,
    }}>
      <svg width="14" height="14" viewBox="0 0 16 16" fill="none" stroke={t.muted} strokeWidth="1.4" style={{ flexShrink: 0, marginTop: 2 }}>
        <path d="M8 1.5 L13 4 L13 8.5 C13 11.5 10.7 13.7 8 14.5 C5.3 13.7 3 11.5 3 8.5 L3 4 Z" />
      </svg>
      <span>{children}</span>
    </div>
  );
}

function Delta({ t, value }) {
  const pos = value >= 0;
  return (
    <div style={{
      display: 'inline-flex', alignItems: 'center', gap: 4,
      padding: '4px 10px', borderRadius: 100,
      background: pos ? t.accentSoft : t.surfaceAlt,
      color: pos ? t.accentDeep : t.muted,
      fontSize: 12, fontFamily: t.fontMono, fontWeight: 500,
    }}>
      <span>{pos ? '↑' : '↓'}</span>
      <span>{Math.abs(value)} pts</span>
    </div>
  );
}

function Check({ t }) {
  return (
    <div style={{
      width: 64, height: 64, borderRadius: '50%',
      background: t.accentSoft,
      display: 'flex', alignItems: 'center', justifyContent: 'center',
    }}>
      <svg width="28" height="28" viewBox="0 0 28 28" fill="none" stroke={t.accentDeep} strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M6 14 L12 20 L22 8" />
      </svg>
    </div>
  );
}

function RingRecorder({ t, pct }) {
  const C = 2 * Math.PI * 96;
  const off = C - (pct / 100) * C;
  return (
    <svg viewBox="0 0 220 220" width="220" height="220">
      {/* Outer breathing halo */}
      <circle cx="110" cy="110" r="100" fill="none" stroke={t.accentSoft} strokeWidth="2"
        style={{ animation: 'bolo-ring-pulse 2.6s ease-in-out infinite', transformOrigin: 'center' }} />
      {/* Track */}
      <circle cx="110" cy="110" r="96" fill="none" stroke={t.line} strokeWidth="3" />
      {/* Progress */}
      <circle cx="110" cy="110" r="96" fill="none" stroke={t.accent} strokeWidth="3"
        strokeDasharray={C} strokeDashoffset={off} strokeLinecap="round"
        transform="rotate(-90 110 110)" style={{ transition: 'stroke-dashoffset .15s linear' }} />
      {/* Inner dot */}
      <circle cx="110" cy="110" r="14" fill={t.rec}
        style={{ animation: 'bolo-breathe 1.6s ease-in-out infinite', transformOrigin: 'center' }} />
    </svg>
  );
}

function Waveform({ t, active }) {
  const bars = Array.from({ length: 24 });
  return (
    <div style={{ display: 'flex', gap: 4, alignItems: 'center', height: 36 }}>
      {bars.map((_, i) => (
        <span key={i} style={{
          width: 3, height: 28, borderRadius: 2, background: t.accent, opacity: 0.85,
          transformOrigin: 'center',
          animation: active ? `bolo-wave 1.${(i % 6) + 2}s ease-in-out ${i * 0.04}s infinite` : 'none',
        }} />
      ))}
    </div>
  );
}

function TrendChart({ t, weekly }) {
  const W = 320, H = 130, P = 10;
  const max = 100, min = 0;
  const xs = weekly.map((_, i) => P + (i * (W - 2 * P)) / (weekly.length - 1));
  const ys = weekly.map((v) => H - P - ((v - min) / (max - min)) * (H - 2 * P));
  const d = xs.map((x, i) => `${i === 0 ? 'M' : 'L'} ${x} ${ys[i]}`).join(' ');
  const dArea = `${d} L ${xs[xs.length - 1]} ${H - P} L ${xs[0]} ${H - P} Z`;
  const labels = ['8w', '7w', '6w', '5w', '4w', '3w', '2w', 'now'];
  return (
    <div style={{ background: t.surface, borderRadius: t.radius, padding: 18, boxShadow: `inset 0 0 0 1px ${t.line}` }}>
      <svg viewBox={`0 0 ${W} ${H}`} width="100%" preserveAspectRatio="none" style={{ display: 'block' }}>
        {/* gridlines */}
        {[25, 50, 75].map((g) => {
          const y = H - P - (g / 100) * (H - 2 * P);
          return <line key={g} x1={P} x2={W - P} y1={y} y2={y} stroke={t.line} strokeDasharray="2 3" />;
        })}
        <path d={dArea} fill={t.accentSoft} opacity="0.5" />
        <path d={d} fill="none" stroke={t.accent} strokeWidth="2" strokeLinejoin="round" strokeLinecap="round" />
        {xs.map((x, i) => (
          <circle key={i} cx={x} cy={ys[i]} r={i === xs.length - 1 ? 5 : 3}
            fill={i === xs.length - 1 ? t.accent : t.surface}
            stroke={t.accent} strokeWidth="1.6" />
        ))}
      </svg>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 10, fontFamily: t.fontMono, fontSize: 10, color: t.muted, letterSpacing: 0.5 }}>
        {labels.map((l) => <span key={l}>{l}</span>)}
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Topic switch sheet — bottom drawer on Live
// ─────────────────────────────────────────────────────────────
function TopicSwitchSheet({ t, current, onPick, onClose }) {
  return (
    <div onClick={onClose} style={{
      position: 'absolute', inset: 0, zIndex: 5,
      background: 'rgba(20,18,12,0.45)',
      display: 'flex', alignItems: 'flex-end',
      animation: 'bolo-fade-in .2s ease both',
    }}>
      <div onClick={(e) => e.stopPropagation()} style={{
        background: t.surface, width: '100%',
        borderRadius: `${t.radius}px ${t.radius}px 0 0`,
        padding: '20px 24px 24px',
      }}>
        <div style={{ width: 40, height: 4, background: t.lineStrong, borderRadius: 2, margin: '0 auto 18px' }} />
        <Caption t={t}>Switch topic mid-session</Caption>
        <div style={{ height: 4 }} />
        <div style={{
          fontFamily: t.fontAccent || t.fontDisplay,
          fontSize: 22, color: t.fg, fontStyle: 'italic', letterSpacing: -0.3,
        }}>What are we talking about now?</div>
        <div style={{ height: 16 }} />
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
          {BOLO_TOPICS.map((tp) => {
            const on = tp === current;
            return (
              <button key={tp} onClick={() => onPick(tp)}
                style={{
                  border: 'none', cursor: 'pointer',
                  padding: '10px 16px', borderRadius: 100,
                  fontFamily: t.fontBody, fontSize: 14, fontWeight: 500,
                  background: on ? t.fg : 'transparent',
                  color: on ? t.bg : t.fg,
                  boxShadow: on ? 'none' : `inset 0 0 0 1px ${t.line}`,
                }}>{tp}</button>
            );
          })}
        </div>
        <div style={{ height: 12 }} />
        <div style={{ fontSize: 12, color: t.muted, lineHeight: 1.4 }}>
          The session continues — we'll just tag what's said from here on with the new topic.
        </div>
      </div>
    </div>
  );
}

Object.assign(window, {
  HomeScreen, EnrollScreen, StartScreen, LiveScreen, SummaryScreen, DashboardScreen,
  Btn, Avatar, Card, Wordmark, TopBar, ScreenTitle, Pad, ScrollBody, FullBody,
  TopicSwitchSheet,
  // Sub-atoms reused by extras
  SectionLabel, Caption, BarRow, StudentBar, SecondaryMetric, ToggleRow, PrivacyNote,
  Delta, Field: null,
});
