import { useEffect, useState } from 'react';
import { api } from '../api.js';
import Alert from '../components/Alert.jsx';

const today = () => new Date().toLocaleDateString('en-CA'); // YYYY-MM-DD in local time

export default function Attendance() {
  const [subjects, setSubjects] = useState([]);
  const [subjectId, setSubjectId] = useState('');
  const [date, setDate] = useState(today());
  const [rows, setRows] = useState([]);
  const [msg, setMsg] = useState(null);

  useEffect(() => {
    api.get('/subjects').then((list) => {
      setSubjects(list);
      if (list.length) setSubjectId(String(list[0].id));
    }).catch((e) => setMsg({ type: 'error', text: e.message }));
  }, []);

  useEffect(() => {
    if (!subjectId || !date) return;
    api.get(`/attendance?subjectId=${subjectId}&date=${date}`)
      .then((data) => setRows(data.map((r) => ({ ...r, saved: r.present !== null, present: r.present ?? true }))))
      .catch((e) => setMsg({ type: 'error', text: e.message }));
  }, [subjectId, date]);

  const toggle = (id) => setRows(rows.map((r) => (r.studentId === id ? { ...r, present: !r.present } : r)));
  const setAll = (value) => setRows(rows.map((r) => ({ ...r, present: value })));

  const save = async () => {
    try {
      await api.post('/attendance', {
        subjectId: Number(subjectId),
        date,
        records: rows.map((r) => ({ studentId: r.studentId, present: r.present })),
      });
      setRows(rows.map((r) => ({ ...r, saved: true })));
      setMsg({ type: 'success', text: 'Attendance saved' });
    } catch (err) {
      setMsg({ type: 'error', text: err.message });
    }
  };

  const presentCount = rows.filter((r) => r.present).length;

  return (
    <>
      <h1>Mark attendance</h1>
      <p className="sub">Everyone starts as present. Untick the students who are absent, then save.</p>
      <Alert message={msg} />

      <div className="card form-row">
        <label>Subject
          <select value={subjectId} onChange={(e) => setSubjectId(e.target.value)}>
            {subjects.map((s) => <option key={s.id} value={s.id}>{s.code} - {s.name}</option>)}
          </select>
        </label>
        <label>Date<input type="date" value={date} max={today()} onChange={(e) => setDate(e.target.value)} /></label>
      </div>

      {subjects.length === 0 ? <div className="card"><p className="empty">Add a subject first, then come back to mark attendance.</p></div> : (
        <div className="card">
          <div className="card-head">
            <h2>{presentCount} present, {rows.length - presentCount} absent</h2>
            <div className="actions">
              <button className="btn btn-mint small" onClick={() => setAll(true)}>Mark all present</button>
              <button className="btn btn-soft small" onClick={() => setAll(false)}>Mark all absent</button>
            </div>
          </div>
          {rows.length === 0 ? <p className="empty">No students yet. Add students first.</p> : (
            <table>
              <thead><tr><th>Roll no.</th><th>Name</th><th>Status</th><th>Present</th></tr></thead>
              <tbody>
                {rows.map((r) => (
                  <tr key={r.studentId}>
                    <td>{r.rollNo}</td><td>{r.name}</td>
                    <td>{r.saved ? <span className="chip code">Saved</span> : <span className="chip new">Not saved yet</span>}</td>
                    <td><input type="checkbox" className="check" checked={r.present} onChange={() => toggle(r.studentId)} /></td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
          {rows.length > 0 && <div className="save-bar"><button className="btn btn-primary" onClick={save}>Save attendance</button></div>}
        </div>
      )}
    </>
  );
}
