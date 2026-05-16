import request from './request'

export function getUsers(params?: Record<string, unknown>) {
  return request.get('/users', { params })
}

export function getRoles() {
  return request.get('/roles')
}

export function getPermissions() {
  return request.get('/permissions')
}
