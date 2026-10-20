import { useEffect, useState } from 'react';
import { api } from '../api.js';
import Alert from '../components/Alert.jsx';

export default function Subjects() {
  const [list, setList] = useState([]);
  const [form, setForm] = useState({ code: '', name: '' });
  const [msg, setMsg] = useState(null);

  const load = () => api.get('/subjects').then(setList).catch((e) => setMsg({ type: 'error', text: e.message }));
  useEffect(() => { load(); }, []);

  const submit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/subjects', form);
      setMsg({ type: 'success', text: 'Subject added' });
      setForm({ code: '', name: '' });
      load();
    } catch (err) {
      setMsg({ type: 'error', text: err.message });
    }
  };

  const remove = async (s) => {
    if (!window.confirm(`Delete ${s.name}? Its attendance records will be deleted too.`)) return;
    try {
      await api.del(`/subjects/${s.id}`);
      setMsg({ type: 'success', text: 'Subject deleted' });
      load();
    } catch (err) {
      setMsg({ type: 'error', text: err.message });
    }
  };

  return (
    <>
      <h1>Subjects</h1>
      <p className="sub">Attendance is marked per subject, so add every subject you teach.</p>
      <Alert message={msg} />

      <form className="card form-row" onSubmit={submit}>
        <label>Code<input value={form.code} placeholder="FSJ" onChange={(e) => setForm({ ...form, code: e.target.value })} /></label>
        <label>Subject name<input value={form.name} placeholder="Full Stack Java Programming" onChange={(e) => setForm({ ...form, name: e.target.value })} /></label>
        <div className="form-actions"><button className="btn btn-primary">Add subject</button></div>
      </form>

      <div className="card">
        <h2>All subjects ({list.length})</h2>
        {list.length === 0 ? <p className="empty">No subjects yet. Add one above.</p> : (
          <table>
            <thead><tr><th>Code</th><th>Name</th><th></th></tr></thead>
            <tbody>
              {list.map((s) => (
                <tr key={s.id}>
                  <td><span className="chip code">{s.code}</span></td><td>{s.name}</td>
                  <td className="actions"><button className="btn btn-danger small" onClick={() => remove(s)}>Delete</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  );
}
