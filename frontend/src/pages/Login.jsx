import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, setSession } from '../api.js';
import Alert from '../components/Alert.jsx';
import Footer from '../components/Footer.jsx';

export default function Login() {
  const [form, setForm] = useState({ username: '', password: '' });
  const [msg, setMsg] = useState(null);
  const [busy, setBusy] = useState(false);
  const navigate = useNavigate();

  const submit = async (e) => {
    e.preventDefault();
    setBusy(true);
    try {
      const res = await api.post('/auth/login', form);
      setSession(res.token, res.fullName);
      navigate('/');
    } catch (err) {
      setMsg({ type: 'error', text: err.message });
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-wrap">
        <div className="login-card">
          <div className="brand big"><span className="logo">&#10003;</span>Markly</div>
          <p className="tagline">Attendance made simple</p>
          <form onSubmit={submit}>
            <label>Username
              <input value={form.username} autoFocus onChange={(e) => setForm({ ...form, username: e.target.value })} />
            </label>
            <label>Password
              <input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
            </label>
            <Alert message={msg} />
            <button className="btn btn-primary full" disabled={busy}>{busy ? 'Logging in...' : 'Log in'}</button>
          </form>
          <p className="hint">Demo login: <strong>admin</strong> / <strong>admin123</strong></p>
        </div>
      </div>
      <Footer />
    </div>
  );
}
