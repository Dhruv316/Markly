import { useEffect, useState } from 'react';
import { api } from '../api.js';
import Alert from '../components/Alert.jsx';

const empty = { rollNo: '', name: '', email: '', className: '' };

export default function Students() {
  const [list, setList] = useState([]);
  const [form, setForm] = useState(empty);
  const [editId, setEditId] = useState(null);
  const [query, setQuery] = useState('');
  const [msg, setMsg] = useState(null);

  const load = () => api.get('/students').then(setList).catch((e) => setMsg({ type: 'error', text: e.message }));
  useEffect(() => { load(); }, []);

  const submit = async (e) => {
    e.preventDefault();
    try {
      if (editId) await api.put(`/students/${editId}`, form);
      else await api.post('/students', form);
      setMsg({ type: 'success', text: editId ? 'Student updated' : 'Student added' });
      setForm(empty);
      setEditId(null);
      load();
    } catch (err) {
      setMsg({ type: 'error', text: err.message });
    }
  };

  const edit = (s) => {
    setEditId(s.id);
    setForm({ rollNo: s.rollNo, name: s.name, email: s.email || '', className: s.className });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const remove = async (s) => {
    if (!window.confirm(`Delete ${s.name}? Their attendance records will be deleted too.`)) return;
    try {
      await api.del(`/students/${s.id}`);
      setMsg({ type: 'success', text: 'Student deleted' });
      load();
    } catch (err) {
      setMsg({ type: 'error', text: err.message });
    }
  };

  const shown = list.filter((s) => `${s.rollNo} ${s.name} ${s.className}`.toLowerCase().includes(query.toLowerCase()));
  const field = (key) => ({ value: form[key], onChange: (e) => setForm({ ...form, [key]: e.target.value }) });

  return (
    <>
      <h1>Students</h1>
      <p className="sub">Add, edit and remove the students you take attendance for.</p>
      <Alert message={msg} />

      <form className="card form-row" onSubmit={submit}>
        <label>Roll no.<input {...field('rollNo')} placeholder="TE01" /></label>
        <label>Full name<input {...field('name')} placeholder="Aarav Sharma" /></label>
        <label>Email<input {...field('email')} placeholder="aarav@example.com" /></label>
        <label>Class<input {...field('className')} placeholder="TE-IT" /></label>
        <div className="form-actions">
          <button className="btn btn-primary">{editId ? 'Save changes' : 'Add student'}</button>
          {editId && <button type="button" className="btn btn-soft" onClick={() => { setEditId(null); setForm(empty); }}>Cancel</button>}
        </div>
      </form>

      <div className="card">
        <div className="card-head">
          <h2>All students ({shown.length})</h2>
          <input className="search" placeholder="Search students" value={query} onChange={(e) => setQuery(e.target.value)} />
        </div>
        {shown.length === 0 ? <p className="empty">No students found. Add your first student above.</p> : (
          <div className="table-wrap">
            <table>
              <thead><tr><th>Roll no.</th><th>Name</th><th>Email</th><th>Class</th><th></th></tr></thead>
              <tbody>
                {shown.map((s) => (
                  <tr key={s.id}>
                    <td>{s.rollNo}</td><td>{s.name}</td><td>{s.email}</td><td>{s.className}</td>
                    <td className="actions">
                      <button className="btn btn-sky small" onClick={() => edit(s)}>Edit</button>
                      <button className="btn btn-danger small" onClick={() => remove(s)}>Delete</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </>
  );
}
