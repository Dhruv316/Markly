import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { clearSession } from '../api.js';
import Footer from './Footer.jsx';

const links = [
  ['/', 'Dashboard'],
  ['/students', 'Students'],
  ['/subjects', 'Subjects'],
  ['/attendance', 'Mark attendance'],
  ['/reports', 'Reports'],
];

export default function Layout() {
  const navigate = useNavigate();
  const logout = () => {
    clearSession();
    navigate('/login');
  };

  return (
    <div className="app">
      <header className="topbar">
        <div className="brand"><span className="logo">&#10003;</span>Markly</div>
        <nav>
          {links.map(([to, label]) => (
            <NavLink key={to} to={to} end={to === '/'} className={({ isActive }) => (isActive ? 'active' : '')}>
              {label}
            </NavLink>
          ))}
        </nav>
        <button className="btn btn-soft" onClick={logout}>Log out</button>
      </header>
      <main className="content"><Outlet /></main>
      <Footer />
    </div>
  );
}
