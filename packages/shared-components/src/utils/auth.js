/**
 * 认证相关工具函数
 */

const TOKEN_KEY = 'caring-token'
const USER_KEY = 'caring-user'
const TENANT_KEY = 'caring-tenant'

/**
 * 获取token
 * @returns {string} token
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * 设置token
 * @param {string} token token值
 */
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

/**
 * 移除token
 */
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

/**
 * 获取用户信息
 * @returns {object} 用户信息
 */
export function getUser() {
  const userStr = localStorage.getItem(USER_KEY)
  return userStr ? JSON.parse(userStr) : null
}

/**
 * 设置用户信息
 * @param {object} user 用户信息
 */
export function setUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

/**
 * 移除用户信息
 */
export function removeUser() {
  localStorage.removeItem(USER_KEY)
}

/**
 * 获取租户信息
 * @returns {string} 租户代码
 */
export function getTenant() {
  return localStorage.getItem(TENANT_KEY)
}

/**
 * 设置租户信息
 * @param {string} tenant 租户代码
 */
export function setTenant(tenant) {
  localStorage.setItem(TENANT_KEY, tenant)
}

/**
 * 移除租户信息
 */
export function removeTenant() {
  localStorage.removeItem(TENANT_KEY)
}

/**
 * 检查是否已登录
 * @returns {boolean} 是否已登录
 */
export function isLoggedIn() {
  return !!getToken()
}

/**
 * 检查是否有权限
 * @param {string|Array} permissions 权限代码
 * @returns {boolean} 是否有权限
 */
export function hasPermission(permissions) {
  const user = getUser()
  if (!user || !user.permissions) return false
  
  const userPermissions = user.permissions
  if (Array.isArray(permissions)) {
    return permissions.some(permission => userPermissions.includes(permission))
  }
  
  return userPermissions.includes(permissions)
}

/**
 * 检查是否有角色
 * @param {string|Array} roles 角色代码
 * @returns {boolean} 是否有角色
 */
export function hasRole(roles) {
  const user = getUser()
  if (!user || !user.roles) return false
  
  const userRoles = user.roles
  if (Array.isArray(roles)) {
    return roles.some(role => userRoles.includes(role))
  }
  
  return userRoles.includes(roles)
}

/**
 * 登出
 */
export function logout() {
  removeToken()
  removeUser()
  removeTenant()
  
  // 跳转到登录页
  window.location.href = '/login'
}

/**
 * 清除所有认证信息
 */
export function clearAuth() {
  removeToken()
  removeUser()
  removeTenant()
}