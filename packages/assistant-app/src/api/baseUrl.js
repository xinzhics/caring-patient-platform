const apiUrl = process.env.NODE_ENV === 'development' 
  ? 'https://dev-api.example.com/' 
  : 'https://api.example.com/'

export default apiUrl
