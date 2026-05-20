import { ref, watch, onMounted } from 'vue'

type Theme = 'light' | 'dark' | 'system'

const STORAGE_KEY = 'tdep-theme'

const theme = ref<Theme>('light')
const isDark = ref(false)

const prefersDark = window.matchMedia('(prefers-color-scheme: dark)')

function getSystemPreference(): 'light' | 'dark' {
  return prefersDark.matches ? 'dark' : 'light'
}

function applyTheme(dark: boolean) {
  isDark.value = dark

  const root = document.documentElement
  root.classList.toggle('dark', dark)
  root.setAttribute('data-theme', dark ? 'dark' : 'light')

  let metaTheme = document.querySelector('meta[name="theme-color"]')
  if (!metaTheme) {
    metaTheme = document.createElement('meta')
    metaTheme.name = 'theme-color'
    document.head.appendChild(metaTheme)
  }
  metaTheme.setAttribute('content', dark ? '#0f172a' : '#f0f9ff')

  updateScrollbarStyle(dark)
}

function updateScrollbarStyle(dark: boolean) {
  let style = document.getElementById('tdep-scrollbar-style')
  if (!style) {
    style = document.createElement('style')
    style.id = 'tdep-scrollbar-style'
    document.head.appendChild(style)
  }

  style.textContent = dark
    ? `::-webkit-scrollbar { width: 8px; height: 8px; }
       ::-webkit-scrollbar-track { background: #1e293b; }
       ::-webkit-scrollbar-thumb { background: #475569; border-radius: 4px; }
       ::-webkit-scrollbar-thumb:hover { background: #64748b; }`
    : `::-webkit-scrollbar { width: 8px; height: 8px; }
       ::-webkit-scrollbar-track { background: #f1f5f9; }
       ::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; }
       ::-webkit-scrollbar-thumb:hover { background: #94a3b8; }`
}

export function useDarkMode() {
  function init() {
    const stored = localStorage.getItem(STORAGE_KEY) as Theme | null

    if (stored && ['light', 'dark'].includes(stored)) {
      theme.value = stored
      applyTheme(stored === 'dark')
    } else if (stored === 'system' || !stored) {
      theme.value = 'system'
      applyTheme(getSystemPreference() === 'dark')
    }

    prefersDark.addEventListener('change', () => {
      if (theme.value === 'system') {
        applyTheme(getSystemPreference() === 'dark')
      }
    })
  }

  function setTheme(newTheme: Theme) {
    theme.value = newTheme
    localStorage.setItem(STORAGE_KEY, newTheme)

    if (newTheme === 'system') {
      applyTheme(getSystemPreference() === 'dark')
    } else {
      applyTheme(newTheme === 'dark')
    }
  }

  function toggle() {
    setTheme(isDark.value ? 'light' : 'dark')
  }

  onMounted(init)

  return {
    theme,
    isDark,
    setTheme,
    toggle,
    init,
  }
}
