const apiUrl = process.env.NODE_ENV === 'development' 
  ? 'http://localhost:8760' 
  : 'https://api.example.com'

export default apiUrl
  