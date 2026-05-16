import { TOKEN_KEY, USER_KEY } from './constants'
import { getStorage, removeStorage, setStorage } from './storage'

export interface CurrentUser {
  userId: number
  username: string
  realName?: string
  roles: string[]
  permissions: string[]
}

export function getAccessToken() {
  return getStorage<string>(TOKEN_KEY, '')
}

export function setAccessToken(token: string) {
  setStorage(TOKEN_KEY, token)
}

export function getCurrentUser() {
  return getStorage<CurrentUser | null>(USER_KEY, null)
}

export function setCurrentUser(user: CurrentUser | null) {
  if (user) {
    setStorage(USER_KEY, user)
  } else {
    removeStorage(USER_KEY)
  }
}

export function clearToken() {
  removeStorage(TOKEN_KEY)
  removeStorage(USER_KEY)
}
