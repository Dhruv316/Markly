import { useEffect, useState } from 'react';
import { api } from '../api.js';
import Alert from '../components/Alert.jsx';

export default function Dashboard() {
  const [data, setData] = useState(null);
  const [msg, setMsg] = useState(null);

  useEffect(() => {
    api.get('/dashboard').then(setData).catch((e) => setMsg({ type: 'error', text: e.message }));
  }, []);

  if (!data) return <Alert message={msg} />;

  const stats = [
    ['Students', data.students, 'mint'],
    ['Subjects', data.subjects, 'sky'],
    ['Marked today', data.markedToday, 'sun'],
    ['Average attendance', `${data.averageAttendance}%`, 'lilac'],
  ];

  return (
    <>
      <h1>Dashboard</h1>
      <p className="sub">A quick look at how attendance is going.</p>
      <div className="stats">
        {stats.map(([label, value, tone]) => (
          <div key={label} className={`stat ${tone}`}>
            <div className="stat-value">{value}</div>
            <div className="stat-label">{label}</div>
          </div>
        ))}
      </div>

      <div className="card">
        <h2>Students below 75% attendance</h2>
        {data.lowAttendance.length === 0 ? (
          <p className="empty">Everyone is on track. Nice work!</p>
        ) : (
          <table>
            <thead><tr><th>Roll no.</th><th>Name</th><th>Class</th><th>Attendance</th></tr></thead>
            <tbody>
              {data.lowAttendance.map((s) => (
                <tr key={s.studentId}>
                  <td>{s.rollNo}</td><td>{s.name}</td><td>{s.className}</td>
                  <td><span className="chip low">{s.percentage}%</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  );
}
