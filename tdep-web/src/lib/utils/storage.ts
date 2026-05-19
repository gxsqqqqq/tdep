const TOKEN_KEY = 'tdep_token'
const USER_KEY = 'tdep_user'
const THEME_KEY = 'tdep_theme'
const LOCALE_KEY = 'tdep_locale'

export const storage = {
  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY)
  },

  setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token)
  },

  removeToken(): void {
    localStorage.removeItem(TOKEN_KEY)
  },

  getUser<T>(): T | null {
    const userStr = localStorage.getItem(USER_KEY)
    if (!userStr) return null
    try {
      return JSON.parse(userStr) as T
    } catch {
      return null
    }
  },

  setUser<T>(user: T): void {
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  },

  removeUser(): void {
    localStorage.removeItem(USER_KEY)
  },

  clearAuth(): void {
    this.removeToken()
    this.removeUser()
  },

  getTheme(): string | null {
    return localStorage.getItem(THEME_KEY)
  },

  setTheme(theme: string): void {
    localStorage.setItem(THEME_KEY, theme)
  },

  getLocale(): string | null {
    return localStorage.getItem(LOCALE_KEY)
  },

  setLocale(locale: string): void {
    localStorage.setItem(LOCALE_KEY, locale)
  },
}
