<template>
  <div class="register-container">
    <div class="card">
      <h1>Create Account</h1>
      <form @submit.prevent="handleRegister">
        <input v-model="userName" type="text" placeholder="Username" required />
        <input v-model="email" type="email" placeholder="Email" required />
        <input v-model="password" type="password" placeholder="Password" required />
        <button type="submit">Register</button>
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      </form>
      <p class="switch-auth">
        Already have an account?
        <router-link to="/login">Login</router-link>
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
const userName = ref('')
const errorMessage = ref('')
const router = useRouter()

const handleRegister = async () => {
  errorMessage.value = ''

  try {
    await apiFetch('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify({
        email: email.value,
        password: password.value,
        userName: userName.value
      })
    })

    router.push('/login')
  } catch (err) {
    console.error('Registration failed:', err)

    if (err.status === 409) {
      errorMessage.value = 'An account with this email already exists.'
    } else if (err.status === 400) {
      errorMessage.value = 'Please fill all fields correctly.'
    } else {
      errorMessage.value = 'Registration failed. Please try again later.'
    }
  }
}
</script>


<style scoped>
.register-container {
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
  background: #28a745;
  color: #fff;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s, transform 0.1s;
}

button:hover {
  background: #218838;
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
