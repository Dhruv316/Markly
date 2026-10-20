const TOKEN_KEY = 'markly_token';

export const getToken = () => localStorage.getItem(TOKEN_KEY);
export const setSession = (token, name) => {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem('markly_name', name || '');
};
export const clearSession = () => localStorage.clear();

async function request(path, options = {}) {
  const token = getToken();
  const res = await fetch('/api' + path, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
  });
  if (res.status === 401 && !path.startsWith('/auth')) {
    clearSession();
    window.location.href = '/login';
    throw new Error('Please log in again');
  }
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.message || 'Something went wrong. Please try again.');
  }
  return res.status === 204 ? null : res.json();
}

export const api = {
  get: (p) => request(p),
  post: (p, body) => request(p, { method: 'POST', body: JSON.stringify(body) }),
  put: (p, body) => request(p, { method: 'PUT', body: JSON.stringify(body) }),
  del: (p) => request(p, { method: 'DELETE' }),
};
