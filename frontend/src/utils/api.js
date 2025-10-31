export async function apiFetch(url, options = {}) {
  const finalOptions = {
    credentials: 'include',
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  }

  try {
    const res = await fetch(url, finalOptions)
    const currentPath = window.location.pathname

    if ((res.status === 401 || res.status === 403) && currentPath !== '/login') {
      console.warn(`Unauthorized (${res.status}) – redirecting to /login`)
      window.location.href = '/login'
      return
    }

    if (!res.ok) {
      let message = `Request failed with status ${res.status}`
      let errData = null

      try {
        errData = await res.json()
        message = errData.error || errData.message || message
      } catch {
        // ignore non-JSON errors
      }

      const error = new Error(message)
      error.status = res.status
      error.data = errData
      throw error
    }

    const text = await res.text()
    return text ? JSON.parse(text) : null

  } catch (err) {
    console.error('API fetch error:', err)
    throw err
  }
}
