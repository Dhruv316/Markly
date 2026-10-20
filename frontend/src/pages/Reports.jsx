import { useEffect, useState } from 'react';
import { api } from '../api.js';
import Alert from '../components/Alert.jsx';

export default function Reports() {
  const [subjects, setSubjects] = useState([]);
  const [subjectId, setSubjectId] = useState('');
  const [rows, setRows] = useState([]);
  const [msg, setMsg] = useState(null);

  useEffect(() => {
    api.get('/subjects').then(setSubjects).catch((e) => setMsg({ type: 'error', text: e.message }));
  }, []);

  useEffect(() => {
    api.get(`/reports/summary${subjectId ? `?subjectId=${subjectId}` : ''}`)
      .then(setRows).catch((e) => setMsg({ type: 'error', text: e.message }));
  }, [subjectId]);

  const exportCsv = () => {
    const head = 'Roll No,Name,Class,Lectures,Present,Percentage';
    const lines = rows.map((r) => [r.rollNo, `"${r.name}"`, r.className, r.total, r.present, r.percentage].join(','));
    const blob = new Blob([[head, ...lines].join('\n')], { type: 'text/csv' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'markly-attendance-report.csv';
    link.click();
  };

  return (
    <>
      <h1>Reports</h1>
      <p className="sub">Students need at least 75% attendance. Those below it are flagged.</p>
      <Alert message={msg} />

      <div className="card form-row">
        <label>Subject
          <select value={subjectId} onChange={(e) => setSubjectId(e.target.value)}>
            <option value="">All subjects</option>
            {subjects.map((s) => <option key={s.id} value={s.id}>{s.code} - {s.name}</option>)}
          </select>
        </label>
        <div className="form-actions"><button className="btn btn-sky" onClick={exportCsv}>Download CSV</button></div>
      </div>

      <div className="card">
        {rows.length === 0 ? <p className="empty">No students yet.</p> : (
          <div className="table-wrap">
            <table>
              <thead><tr><th>Roll no.</th><th>Name</th><th>Lectures</th><th>Present</th><th>Attendance</th><th>Status</th></tr></thead>
              <tbody>
                {rows.map((r) => (
                  <tr key={r.studentId}>
                    <td>{r.rollNo}</td><td>{r.name}</td><td>{r.total}</td><td>{r.present}</td>
                    <td>
                      <div className="bar"><div className={`fill ${r.percentage < 75 ? 'low' : 'good'}`} style={{ width: `${r.percentage}%` }} /></div>
                      <span className="pct">{r.percentage}%</span>
                    </td>
                    <td>
                      {r.total === 0 ? <span className="chip new">No data</span>
                        : r.percentage < 75 ? <span className="chip low">Low</span>
                        : <span className="chip good">Good</span>}
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
