const apiUrl = process.env.NODE_ENV === 'development' 
  ? 'https://dev-api.example.com/api' 
  : 'https://api.example.com/api'

export default apiUrl
