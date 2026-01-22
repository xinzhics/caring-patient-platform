const apiUrl = process.env.NODE_ENV === 'development' 
  ? 'http://localhost:8760/api' 
  : 'https://api.example.com/api'

export default apiUrl
