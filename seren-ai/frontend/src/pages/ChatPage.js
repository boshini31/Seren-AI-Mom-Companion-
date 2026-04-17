import React, { useState, useEffect, useRef, useCallback } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { sendMessage, getHistory, clearHistory } from '../services/api';
import styles from './ChatPage.module.css';

const SESSION_KEY = 'seren_session_id';

const TypingIndicator = () => (
  <div className={styles.typingWrap}>
    <div className={styles.avatar}>S</div>
    <div className={styles.typingBubble}>
      <span className={styles.dot} />
      <span className={styles.dot} />
      <span className={styles.dot} />
    </div>
  </div>
);

const Message = ({ msg }) => {
  const isUser = msg.role === 'user';
  return (
    <div className={`${styles.msgRow} ${isUser ? styles.userRow : styles.aiRow}`}>
      {!isUser && <div className={styles.avatar}>S</div>}
      <div className={`${styles.bubble} ${isUser ? styles.userBubble : styles.aiBubble}`}>
        {msg.content}
      </div>
      {isUser && <div className={styles.avatarUser}>You</div>}
    </div>
  );
};

export default function ChatPage() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [sessionId, setSessionId] = useState('');
  const [historyLoaded, setHistoryLoaded] = useState(false);
  const bottomRef = useRef(null);
  const inputRef = useRef(null);

  // Initialize session
  useEffect(() => {
    let sid = localStorage.getItem(SESSION_KEY);
    if (!sid) {
      sid = uuidv4();
      localStorage.setItem(SESSION_KEY, sid);
    }
    setSessionId(sid);
  }, []);

  // Load history
  useEffect(() => {
    if (!sessionId) return;
    (async () => {
      try {
        const history = await getHistory(sessionId);
        if (history.length > 0) {
          const msgs = [];
          history.forEach(h => {
            msgs.push({ role: 'user', content: h.userMessage, id: `u-${h.id}` });
            msgs.push({ role: 'ai', content: h.aiReply, id: `a-${h.id}` });
          });
          setMessages(msgs);
        } else {
          setMessages([{
            role: 'ai',
            content: "Hello, dear 💙 I'm Seren, and I'm here for you. How are you feeling today? Tell me anything on your mind – I'm listening.",
            id: 'welcome'
          }]);
        }
      } catch {
        setMessages([{
          role: 'ai',
          content: "Hello, dear 💙 I'm Seren. I'm here for you. How are you feeling today?",
          id: 'welcome'
        }]);
      } finally {
        setHistoryLoaded(true);
      }
    })();
  }, [sessionId]);

  // Scroll to bottom
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, loading]);

  const handleSend = useCallback(async () => {
    const text = input.trim();
    if (!text || loading) return;

    const userMsg = { role: 'user', content: text, id: uuidv4() };
    setMessages(prev => [...prev, userMsg]);
    setInput('');
    setLoading(true);

    try {
      const data = await sendMessage(text, sessionId);
      if (data.sessionId && !localStorage.getItem(SESSION_KEY)) {
        localStorage.setItem(SESSION_KEY, data.sessionId);
        setSessionId(data.sessionId);
      }
      setMessages(prev => [...prev, { role: 'ai', content: data.reply, id: uuidv4() }]);
    } catch {
      setMessages(prev => [...prev, {
        role: 'ai',
        content: "I'm so sorry, dear – I had a little trouble connecting. Please try again in a moment. I'm here 💙",
        id: uuidv4()
      }]);
    } finally {
      setLoading(false);
      inputRef.current?.focus();
    }
  }, [input, loading, sessionId]);

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const handleClear = async () => {
    if (!window.confirm('Clear this conversation? This cannot be undone.')) return;
    try {
      await clearHistory(sessionId);
      const newSid = uuidv4();
      localStorage.setItem(SESSION_KEY, newSid);
      setSessionId(newSid);
      setMessages([{
        role: 'ai',
        content: "Fresh start, dear 💙 I'm here whenever you need me. What's on your heart?",
        id: uuidv4()
      }]);
    } catch {
      alert('Could not clear history. Please try again.');
    }
  };

  const suggestions = [
    "I'm feeling overwhelmed today",
    "I need some encouragement",
    "Can you help me calm down?",
    "I just need someone to talk to",
  ];

  return (
    <div className={styles.root}>
      {/* Ambient background blobs */}
      <div className={styles.blob1} />
      <div className={styles.blob2} />

      {/* Sidebar */}
      <aside className={styles.sidebar}>
        <div className={styles.logo}>
          <span className={styles.logoIcon}>✦</span>
          <span className={styles.logoText}>Seren</span>
        </div>
        <p className={styles.tagline}>Your caring AI companion</p>
        <div className={styles.divider} />
        <p className={styles.sideLabel}>Quick starts</p>
        <div className={styles.suggestions}>
          {suggestions.map((s, i) => (
            <button key={i} className={styles.suggBtn} onClick={() => {
              setInput(s);
              inputRef.current?.focus();
            }}>
              {s}
            </button>
          ))}
        </div>
        <div className={styles.sideFooter}>
          <button className={styles.clearBtn} onClick={handleClear}>
            ↺ New conversation
          </button>
          <p className={styles.techNote}>Java · Spring Boot · React</p>
        </div>
      </aside>

      {/* Main */}
      <main className={styles.main}>
        <header className={styles.header}>
          <div className={styles.headerLeft}>
            <div className={styles.statusDot} />
            <span className={styles.headerTitle}>Seren AI</span>
          </div>
          <span className={styles.headerSub}>Warm. Present. Always here for you.</span>
        </header>

        <div className={styles.messagesArea}>
          {!historyLoaded ? (
            <div className={styles.loadingState}>
              <div className={styles.spinner} />
              <p>Connecting with Seren…</p>
            </div>
          ) : (
            <>
              {messages.map(msg => <Message key={msg.id} msg={msg} />)}
              {loading && <TypingIndicator />}
              <div ref={bottomRef} />
            </>
          )}
        </div>

        <div className={styles.inputArea}>
          <textarea
            ref={inputRef}
            className={styles.textarea}
            value={input}
            onChange={e => setInput(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Share what's on your mind, dear…"
            rows={1}
            disabled={loading}
          />
          <button
            className={styles.sendBtn}
            onClick={handleSend}
            disabled={!input.trim() || loading}
          >
            <SendIcon />
          </button>
        </div>
        <p className={styles.hint}>Press Enter to send · Shift+Enter for new line</p>
      </main>
    </div>
  );
}

const SendIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <line x1="22" y1="2" x2="11" y2="13" />
    <polygon points="22 2 15 22 11 13 2 9 22 2" />
  </svg>
);
