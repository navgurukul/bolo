// bolo-print.jsx — print-friendly layout. One phone per page, with title.

const PRINT_SCREENS = [
  { key: 'splash',         label: '01 · Splash',                section: 'First-run · pair this phone' },
  { key: 'pair',           label: '02 · Pair device',           section: 'First-run · pair this phone' },
  { key: 'consent',        label: '03 · Consent',               section: 'First-run · pair this phone' },
  { key: 'mic-perm',       label: '04 · Mic permission',        section: 'First-run · pair this phone' },
  { key: 'cohort-setup',   label: '05 · Name your cohort',      section: 'First-run · pair this phone' },

  { key: 'home',           label: '06 · Home',                  section: 'Every-day flow' },
  { key: 'start',          label: '07 · Pick a topic',          section: 'Every-day flow' },
  { key: 'attendance',     label: "08 · Who's here?",           section: 'Every-day flow' },
  { key: 'live',           label: '09 · Live recording',        section: 'Every-day flow' },
  { key: 'summary',        label: '10 · Session summary',       section: 'Every-day flow' },

  { key: 'add-student',    label: '11 · Add a student',         section: 'Onboarding a student' },
  { key: 'enroll',         label: '12 · Voice enrollment',      section: 'Onboarding a student' },

  { key: 'history',        label: '13 · Session history',       section: 'Review · history & trend' },
  { key: 'session-detail', label: '14 · Session detail',        section: 'Review · history & trend' },
  { key: 'dashboard',      label: '15 · Personal trend',        section: 'Review · history & trend' },

  { key: 'settings',       label: '16 · Settings',              section: 'Settings & care' },
  { key: 'forget-voice',   label: '17 · Forget my voice',       section: 'Settings & care' },
  { key: 'sync',           label: '18 · Sync',                  section: 'Settings & care' },
];

function PrintApp() {
  const t = BOLO_THEMES.final;
  return (
    <div className="pages">
      {/* Cover */}
      <section className="page cover">
        <div className="cover-inner">
          <div className="cover-eyebrow">English Conversation Coach</div>
          <div className="cover-wordmark" style={{ fontFamily: t.fontAccent }}>
            Bolo<span className="dot">.</span>
          </div>
          <div className="cover-tagline" style={{ fontFamily: t.fontAccent }}>
            Speak. Listen. Grow.
          </div>
          <div className="cover-meta">
            <div><span className="lbl">Working draft</span><span>v0.1</span></div>
            <div><span className="lbl">Type</span><span>Geist · Newsreader italic · Geist Mono</span></div>
            <div><span className="lbl">Palette</span><span>Whisper · sage on cream</span></div>
            <div><span className="lbl">Screens</span><span>{PRINT_SCREENS.length}</span></div>
          </div>
        </div>
      </section>

      {PRINT_SCREENS.map((s) => (
        <section key={s.key} className="page screen-page">
          <header className="page-head">
            <div className="page-section">{s.section}</div>
            <div className="page-title">{s.label}</div>
          </header>
          <div className="phone-wrap">
            <BoloFlow themeId="final" initialScreen={s.key} />
          </div>
          <footer className="page-foot">
            <span>Bolo · English Conversation Coach</span>
            <span>{s.label}</span>
          </footer>
        </section>
      ))}
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<PrintApp />);

// Auto-print: wait for fonts + a tick after mount.
(async () => {
  try {
    if (document.fonts && document.fonts.ready) await document.fonts.ready;
  } catch (e) {}
  setTimeout(() => { window.print(); }, 600);
})();
