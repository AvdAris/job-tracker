<template>
  <div class="login-container">
    <div class="card">
      <h1>Login</h1>
      <form @submit.prevent="handleLogin">
        <input v-model="email" type="email" placeholder="Email" required />
        <input v-model="password" type="password" placeholder="Password" required />
        <button type="submit">Sign In</button>
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      </form>
      <p class="switch-auth">
        Don’t have an account?
        <router-link to="/register">Register</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { apiFetch } from '@/utils/api.js'

const email = ref('')
const password = ref('')
const errorMessage = ref('')
const router = useRouter()

const handleLogin = async () => {
  errorMessage.value = ''

  try {
    await apiFetch('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({
        email: email.value,
        password: password.value
      })
    })

    router.push('/applications')
  } catch (err) {
    console.error('Login failed:', err)

    if (err.status === 401) {
      errorMessage.value = 'Invalid email or password.'
    } else if (err.status === 0 || err.message.toLowerCase().includes('network')) {
      errorMessage.value = 'Network error. Please try again later.'
    } else {
      errorMessage.value = 'Login failed. Please try again.'
  }

  }
}
</script>


<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #6a11cb, #2575fc);
}

.card {
  background: #fff;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0,0,0,0.1);
  width: 100%;
  max-width: 400px;
  text-align: center;
}

h1 {
  font-size: 1.6rem;
  margin-bottom: 1.5rem;
  color: #333;
}

form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

input {
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 0.95rem;
}

input:focus {
  border-color: #2575fc;
  box-shadow: 0 0 0 3px rgba(37,117,252,0.2);
  outline: none;
}

button {
  padding: 0.75rem;
  border: none;
  border-radius: 8px;
  background: #2575fc;
  color: #fff;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s, transform 0.1s;
}

button:hover {
  background: #1d5ed8;
}

button:active {
  transform: scale(0.97);
}

.switch-auth {
  margin-top: 1rem;
  font-size: 0.9rem;
  color: #555;
}

.switch-auth a {
  color: #2575fc;
  font-weight: 600;
  text-decoration: none;
}

.switch-auth a:hover {
  text-decoration: underline;
}

.error-message {
  color: #d9534f;
  margin-top: 10px;
  font-weight: 500;
}
</style>
