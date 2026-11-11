import { useEffect, useState } from 'react'

const COLUMNS = [
  { status: 'TODO', label: 'To do' },
  { status: 'IN_PROGRESS', label: 'In progress' },
  { status: 'DONE', label: 'Done' },
]

async function errorMessage(res) {
  try {
    const body = await res.json()
    const fieldErrors = body.fieldErrors && Object.values(body.fieldErrors)
    return fieldErrors?.length ? fieldErrors.join(', ') : body.message ?? `Request failed (${res.status})`
  } catch {
    return `Request failed (${res.status})`
  }
}

export default function App() {
  const [tasks, setTasks] = useState([])
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)

  async function request(url, options) {
    try {
      const res = await fetch(url, options)
      if (!res.ok) {
        setError(await errorMessage(res))
        return null
      }
      setError(null)
      return res
    } catch {
      setError('Could not reach the server')
      return null
    }
  }

  async function loadTasks() {
    const res = await request('/api/tasks')
    if (res) setTasks(await res.json())
    setLoading(false)
  }

  useEffect(() => {
    fetch('/api/tasks')
      .then((res) => (res.ok ? res.json() : Promise.reject(new Error(`Request failed (${res.status})`))))
      .then(setTasks)
      .catch((e) => setError(e instanceof TypeError ? 'Could not reach the server' : e.message))
      .finally(() => setLoading(false))
  }, [])

  async function addTask(e) {
    e.preventDefault()
    const res = await request('/api/tasks', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, description: description || null }),
    })
    if (res) {
      setTitle('')
      setDescription('')
      loadTasks()
    }
  }

  async function changeStatus(task, status) {
    const res = await request(`/api/tasks/${task.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title: task.title, description: task.description, status }),
    })
    if (res) loadTasks()
  }

  async function removeTask(id) {
    const res = await request(`/api/tasks/${id}`, { method: 'DELETE' })
    if (res) loadTasks()
  }

  return (
    <div className="app">
      <header className="topbar">
        <h1>TaskForge</h1>
        <span className="count">{tasks.length} {tasks.length === 1 ? 'task' : 'tasks'}</span>
      </header>

      <form className="new-task" onSubmit={addTask}>
        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="What needs doing?"
          maxLength={100}
          required
        />
        <input
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Description (optional)"
          maxLength={500}
        />
        <button type="submit">Add task</button>
      </form>

      {error && (
        <div className="error" role="alert">
          {error}
          <button className="link" onClick={() => setError(null)}>Dismiss</button>
        </div>
      )}

      {loading ? (
        <p className="muted">Loading…</p>
      ) : (
        <div className="board">
          {COLUMNS.map((col) => {
            const colTasks = tasks.filter((t) => t.status === col.status)
            return (
              <section key={col.status} className={`column column-${col.status.toLowerCase()}`}>
                <h2>
                  {col.label} <span className="pill">{colTasks.length}</span>
                </h2>
                {colTasks.length === 0 && <p className="muted empty">No tasks</p>}
                {colTasks.map((t) => (
                  <article key={t.id} className="card">
                    <div className="card-title">{t.title}</div>
                    {t.description && <p className="card-desc">{t.description}</p>}
                    <div className="card-footer">
                      <span className="task-id">TF-{t.id}</span>
                      <select
                        value={t.status}
                        onChange={(e) => changeStatus(t, e.target.value)}
                        aria-label={`Status of ${t.title}`}
                      >
                        {COLUMNS.map((c) => (
                          <option key={c.status} value={c.status}>{c.label}</option>
                        ))}
                      </select>
                      <button className="delete" onClick={() => removeTask(t.id)} aria-label={`Delete ${t.title}`}>
                        ✕
                      </button>
                    </div>
                  </article>
                ))}
              </section>
            )
          })}
        </div>
      )}
    </div>
  )
}
