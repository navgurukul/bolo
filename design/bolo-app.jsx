// bolo-app.jsx — finalized presentation: one interactive flow,
// plus a storyboard of every screen at rest.

function BoloApp() {
  return (
    <DesignCanvas
      title="Bolo · English Conversation Coach"
      subtitle="Final · Whisper palette · Geist + Newsreader italic accents"
    >
      <DCSection
        id="flow"
        title="Interactive flow"
        subtitle="Tap through · Home → Start → Live → End → Dashboard"
      >
        <DCArtboard id="final-flow" label="Tap-through prototype" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}>
            <BoloFlow themeId="final" initialScreen="home" />
          </div>
        </DCArtboard>
      </DCSection>

      <DCSection
        id="storyboard"
        title="Every screen, at rest"
        subtitle="Home · Enroll · Start · Live · Summary · Dashboard"
      >
        <DCArtboard id="screen-home" label="01 · Home" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}>
            <BoloFlow themeId="final" initialScreen="home" />
          </div>
        </DCArtboard>
        <DCArtboard id="screen-enroll" label="02 · Enroll a voice" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}>
            <BoloFlow themeId="final" initialScreen="enroll" />
          </div>
        </DCArtboard>
        <DCArtboard id="screen-start" label="03 · Start a session" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}>
            <BoloFlow themeId="final" initialScreen="start" />
          </div>
        </DCArtboard>
        <DCArtboard id="screen-live" label="04 · Live recording" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}>
            <BoloFlow themeId="final" initialScreen="live" />
          </div>
        </DCArtboard>
        <DCArtboard id="screen-summary" label="05 · Session summary" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}>
            <BoloFlow themeId="final" initialScreen="summary" />
          </div>
        </DCArtboard>
        <DCArtboard id="screen-dashboard" label="06 · Personal trend" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}>
            <BoloFlow themeId="final" initialScreen="dashboard" />
          </div>
        </DCArtboard>
      </DCSection>

      <DCPostIt top={140} left={40} rotate={-3} width={210}>
        Tap “Start a session” on the first phone to walk the whole flow live.
      </DCPostIt>
      <DCPostIt top={140} right={40} rotate={2} width={220}>
        Type · Geist for numerals & UI · Newsreader italic for the human moments (wordmark, read-aloud, reflection) · Geist Mono for data labels.
      </DCPostIt>
    </DesignCanvas>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<BoloApp />);
