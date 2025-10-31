<template>
  <header class="header">
    <span v-if="user && user.userName">
      Hello, {{ user.userName }}
    </span>
    <span v-else>Hello</span>
    <button @click="logout" class="logout-btn">Logout</button>
  </header>

  <div class="container">
    <h1>Job Applications</h1>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

    <form @submit.prevent="addApplication">
      <input v-model="newApp.companyName" placeholder="Company Name" required maxlength="100" />
      <input v-model="newApp.jobTitle" placeholder="Job Title" required />
      <select v-model="newApp.status" required>
        <option disabled value="">Select status</option>
        <option value="Applied">Applied</option>
        <option value="Interview">Interview</option>
        <option value="Offer">Offer</option>
        <option value="Rejected">Rejected</option>
        <option value="Withdrawn">Withdrawn</option>
      </select>
      <input v-model="newApp.dateApplied" type="date" placeholder="Date Applied" />
      <input v-model="newApp.notes" placeholder="Notes" />
      <button type="submit">Add Application</button>
    </form>

    <table v-if="applications && applications.length">
      <thead>
        <tr>
          <th>Company</th>
          <th>Job Title</th>
          <th>Status</th>
          <th>Date Applied</th>
          <th>Notes</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="app in applications" :key="app.id">
          <template v-if="editId === app.id">
            <td><input v-model="editApp.companyName" /></td>
            <td><input v-model="editApp.jobTitle" /></td>
            <td>
              <select v-model="editApp.status">
                <option value="Applied">Applied</option>
                <option value="Interview">Interview</option>
                <option value="Offer">Offer</option>
                <option value="Rejected">Rejected</option>
                <option value="Withdrawn">Withdrawn</option>
              </select>
            </td>
            <td><input v-model="editApp.dateApplied" type="date" /></td>
            <td><input v-model="editApp.notes" /></td>
            <td>
              <button @click="updateApplication(app.id)" class="success">Save</button>
              <button @click="cancelEdit" class="secondary">Cancel</button>
            </td>
          </template>

          <template v-else>
            <td>{{ app.companyName }}</td>
            <td>{{ app.jobTitle }}</td>
            <td>{{ app.status }}</td>
            <td>{{ formatDate(app.dateApplied) }}</td>
            <td>{{ app.notes }}</td>
            <td>
              <button @click="startEdit(app)">Edit</button>
              <button @click="deleteApplication(app.id)" class="danger">Delete</button>
            </td>
          </template>
        </tr>
      </tbody>
    </table>

    <p v-else class="no-data">No applications yet. Add one above.</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { apiFetch } from '@/utils/api.js'

const router = useRouter()

const user = ref({})
const applications = ref([])
const newApp = ref({
  companyName: '',
  jobTitle: '',
  status: '',
  dateApplied: '',
  notes: ''
})
const editId = ref(null)
const editApp = ref({})
const errorMessage = ref('')

function showError(msg) {
  errorMessage.value = msg
  setTimeout(() => (errorMessage.value = ''), 5000)
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const day = String(date.getDate()).padStart(2, '0')
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const year = String(date.getFullYear()).slice(-2)
  return `${day}–${month}–${year}`
}

async function loadApplications() {
  try {
    applications.value = await apiFetch('/api/applications')
  } catch (err) {
    console.error('Error loading applications:', err)
  }
}

async function loadUser() {
  try {
    user.value = await apiFetch('/api/auth/me')
  } catch (err) {
    console.error('Error fetching user info:', err)
    setTimeout(() => router.push('/login'), 1500)
  }
}

async function addApplication() {
  try {
    const app = await apiFetch('/api/applications', {
      method: 'POST',
      body: JSON.stringify(newApp.value)
    })
    applications.value.push(app)
    resetForm()
  } catch (err) {
    console.error('Error adding application:', err)
    showError(
      err.status === 400
        ? 'Invalid application data. Please check the form.'
        : 'Failed to add application.'
    )
  }
}

async function deleteApplication(id) {
  try {
    await apiFetch(`/api/applications/${id}`, { method: 'DELETE' })
    applications.value = applications.value.filter(a => a.id !== id)
  } catch (err) {
    console.error('Error deleting application:', err)
    showError('Failed to delete application.')
  }
}

async function logout() {
  try {
    await apiFetch('/api/auth/logout', { method: 'POST' })
    applications.value = []
    router.push('/login')
  } catch (err) {
    console.error('Error logging out:', err)
    showError('Failed to log out. Please try again.')
  }
}

function resetForm() {
  newApp.value = {
    companyName: '',
    jobTitle: '',
    status: '',
    dateApplied: '',
    notes: ''
  }
}

function startEdit(app) {
  editId.value = app.id
  editApp.value = { ...app }
}

function cancelEdit() {
  editId.value = null
  editApp.value = {}
}

async function updateApplication(id) {
  try {
    const updatedApp = await apiFetch(`/api/applications/${id}`, {
      method: 'PUT',
      body: JSON.stringify(editApp.value)
    })
    const index = applications.value.findIndex(a => a.id === id)
    if (index !== -1) applications.value.splice(index, 1, updatedApp)
    cancelEdit()
  } catch (err) {
    console.error('Error updating application:', err)
    showError('Failed to update application.')
  }
}

onMounted(() => {
  loadUser()
  loadApplications()
})
</script>

<style scoped>
.header {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: #f5f7fa;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logout-btn {
  padding: 0.5rem 1rem;
  border: none;
  background-color: #ff4d4d;
  color: white;
  border-radius: 6px;
  cursor: pointer;
}

.logout-btn:hover {
  background-color: #cc0000;
}

.user-info {
  font-weight: 500;
}

.container {
  max-width: 900px;
  margin: 2rem auto;
  padding: 1.5rem;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.error-message {
  color: #d9534f;
  background: #ffe6e6;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  font-weight: 500;
  transition: opacity 0.3s ease;
}

h1 {
  font-size: 1.8rem;
  margin-bottom: 1rem;
  text-align: center;
  color: #333;
}

form {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

form input,
form select {
  flex: 1 1 200px;
  padding: 0.6rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 0.95rem;
  transition: border-color 0.2s, box-shadow 0.2s;
}

form input:focus,
form select:focus {
  border-color: #0077ff;
  box-shadow: 0 0 0 3px rgba(0, 119, 255, 0.2);
  outline: none;
}

form button {
  padding: 0.6rem 1.2rem;
  border: none;
  border-radius: 8px;
  background-color: #0077ff;
  color: white;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background 0.2s, transform 0.1s;
}

form button:hover {
  background-color: #005fcc;
}

form button:active {
  transform: scale(0.97);
}

table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

table th,
table td {
  padding: 0.75rem 1rem;
  text-align: left;
  font-size: 0.95rem;
}

table td:last-child {
  display: flex;
  gap: 0.4rem;
  align-items: center;
}

table th {
  background: #f5f7fa;
  font-weight: 600;
  color: #555;
}

table tr:nth-child(even) {
  background-color: #fafafa;
}

table td:nth-child(3),
table th:nth-child(3) {
  min-width: 100px;
}

table tr:hover {
  background-color: #f0f8ff;
}

table input,
table select {
  width: 100%;
  padding: 0.3rem;
  font-size: inherit;
  font-family: inherit;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

button {
  padding: 0.4rem 0.8rem;
  border: none;
  border-radius: 6px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: background 0.2s, transform 0.1s;
  margin-right: 0.4rem;
}

button {
  background-color: #0077ff;
  color: white;
}

button:hover {
  background-color: #005fcc;
}

button.danger {
  background-color: #ff4d4d;
}

button.danger:hover {
  background-color: #cc0000;
}

button.success {
  background-color: #28a745;
}

button.success:hover {
  background-color: #1e7e34;
}

button:active {
  transform: scale(0.97);
}
</style>