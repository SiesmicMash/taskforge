import { useEffect, useState} from "react"

export default function App() {
  const [tasks, setTasks] = useState([])
  const [title, setTitle] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)

  async function loadTasks() {
    try {
      const res = await fetch('/api/tasks')
      if (!res.ok) throw new Error(`Request failed (${res.status})`)
      setTasks(await res.json())
      setError(null)
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }
  useEffect(() => {
    loadTasks()
  },[])

  async function addTask(e) {
    e.preventDefault()
    const res = await fetch('api/tasks', {
      method: 'POST',
      headers: { 'Content-Type' : 'application/json' },
      body: JSON.stringify({title}),
    })

    if (res.ok) {
      setTitle('')
      loadTasks()
    } else {
      const body = await res.json()
      setError(body.fieldErrors?.title ?? body.message)
    }
  }

  async function changeStatus(task, status) {
    await fetch(`/api/tasks/${task.id}`, {
      method: 'PUT',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({
        title: task.title,
        description: task.description,
        status,
      }),
    })
    loadTasks()
  }

  async function removeTask(id) {
    await fetch(`/api/tasks/${id}`, { method: 'DELETE'})
    loadTasks()
  }

  return (    <main style={{ maxWidth: 600, margin: '2rem auto', fontFamily: 'sans-serif' }}>
        <h1>TaskForge</h1>

        <form onSubmit={addTask}>
          <input
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="New task title"
          />
          <button type="submit">Add</button>
        </form>

        {error && <p style={{ color: 'crimson' }}>{error}</p>}
        {loading && <p>Loading...</p>}

        <ul>
          {tasks.map((t) => (
              <li key={t.id}>
                {t.title}{' '}
                <select value={t.status} onChange={(e) => changeStatus(t, e.target.value)}>
                  <option value="TODO">To do</option>
                  <option value="IN_PROGRESS">In progress</option>
                  <option value="DONE">Done</option>
                </select>{' '}
                <button onClick={() => removeTask(t.id)}>Delete</button>
              </li>
          ))}
        </ul>
      </main>
  )

}