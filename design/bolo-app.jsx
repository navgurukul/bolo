// bolo-app.jsx — final canvas: interactive flow + complete storyboard.

function BoloApp() {
  return (
    <DesignCanvas
      title="Bolo · English Conversation Coach"
      subtitle="End-to-end · Whisper palette · Geist + Newsreader italic accents"
    >
      <DCSection
        id="flow"
        title="Interactive flow"
        subtitle="Tap through · everything is live · Home is the entry point"
      >
        <DCArtboard id="prototype-home" label="Start from Home · full flow" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="home" /></div>
        </DCArtboard>
        <DCArtboard id="prototype-firstrun" label="Start from Splash · first-run" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="splash" /></div>
        </DCArtboard>
      </DCSection>

      <DCSection
        id="firstrun"
        title="First-run · pair this phone"
        subtitle="Splash · Pair · Consent · Mic permission · Cohort setup"
      >
        <DCArtboard id="scr-splash"  label="01 · Splash" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="splash" /></div>
        </DCArtboard>
        <DCArtboard id="scr-pair"    label="02 · Pair device" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="pair" /></div>
        </DCArtboard>
        <DCArtboard id="scr-consent" label="03 · Consent" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="consent" /></div>
        </DCArtboard>
        <DCArtboard id="scr-mic"     label="04 · Mic permission" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="mic-perm" /></div>
        </DCArtboard>
        <DCArtboard id="scr-cohort"  label="05 · Name your cohort" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="cohort-setup" /></div>
        </DCArtboard>
      </DCSection>

      <DCSection
        id="core"
        title="Every-day flow"
        subtitle="Home · Start · Attendance · Live · Summary"
      >
        <DCArtboard id="scr-home"        label="Home" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="home" /></div>
        </DCArtboard>
        <DCArtboard id="scr-start"       label="Pick a topic" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="start" /></div>
        </DCArtboard>
        <DCArtboard id="scr-attendance"  label="Who's here?" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="attendance" /></div>
        </DCArtboard>
        <DCArtboard id="scr-live"        label="Live recording" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="live" /></div>
        </DCArtboard>
        <DCArtboard id="scr-summary"     label="Session summary" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="summary" /></div>
        </DCArtboard>
      </DCSection>

      <DCSection
        id="enroll"
        title="Onboarding a student"
        subtitle="Name then voice fingerprint · 10 seconds total"
      >
        <DCArtboard id="scr-addstudent" label="Add a student" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="add-student" /></div>
        </DCArtboard>
        <DCArtboard id="scr-enroll"     label="Voice enrollment" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="enroll" /></div>
        </DCArtboard>
      </DCSection>

      <DCSection
        id="review"
        title="Review · history & trend"
        subtitle="Past sessions · drill-down · personal progress"
      >
        <DCArtboard id="scr-history"   label="Session history" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="history" /></div>
        </DCArtboard>
        <DCArtboard id="scr-detail"    label="Session detail" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="session-detail" /></div>
        </DCArtboard>
        <DCArtboard id="scr-dashboard" label="Personal trend" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="dashboard" /></div>
        </DCArtboard>
      </DCSection>

      <DCSection
        id="care"
        title="Settings & care"
        subtitle="Where the privacy promise is operational"
      >
        <DCArtboard id="scr-settings" label="Settings" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="settings" /></div>
        </DCArtboard>
        <DCArtboard id="scr-forget"   label="Forget my voice" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="forget-voice" /></div>
        </DCArtboard>
        <DCArtboard id="scr-sync"     label="Sync" width={PHONE_W + 24} height={PHONE_H + 24}>
          <div style={{ padding: 12 }}><BoloFlow themeId="final" initialScreen="sync" /></div>
        </DCArtboard>
      </DCSection>

      <DCPostIt top={140} left={40} rotate={-3} width={220}>
        First-row phones are fully interactive. Open Home and tap "Start a session" to walk the full loop including mid-session pause + topic switch.
      </DCPostIt>
      <DCPostIt top={140} right={40} rotate={2} width={230}>
        No traditional auth — the device pairs once to a program code. Inside the cohort, "who you are" is your voice.
      </DCPostIt>
    </DesignCanvas>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<BoloApp />);
