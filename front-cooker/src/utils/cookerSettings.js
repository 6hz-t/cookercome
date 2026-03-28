const SETTINGS_KEY = 'cooker_ui_settings_v1'

export const defaultCookerSettings = {
  theme: 'light',
  fontScale: 1,
  orderReminder: true,
  systemReminder: true,
  evalReminder: true,
  preferredOrderTypes: ['家宴', '私厨'],
  autoRecommend: true
}

let stateCache = null

function normalize(settings = {}) {
  const merged = {
    ...defaultCookerSettings,
    ...settings
  }

  const fontScale = Number(merged.fontScale)
  merged.fontScale = Number.isFinite(fontScale) ? Math.min(1.2, Math.max(0.9, fontScale)) : 1

  if (!['light', 'dark'].includes(merged.theme)) {
    merged.theme = 'light'
  }

  if (!Array.isArray(merged.preferredOrderTypes)) {
    merged.preferredOrderTypes = [...defaultCookerSettings.preferredOrderTypes]
  }

  return merged
}

function readFromStorage() {
  if (typeof window === 'undefined') {
    return normalize()
  }

  try {
    const raw = localStorage.getItem(SETTINGS_KEY)
    if (!raw) {
      return normalize()
    }
    return normalize(JSON.parse(raw))
  } catch (error) {
    return normalize()
  }
}

function writeToStorage(settings) {
  if (typeof window === 'undefined') {
    return
  }
  localStorage.setItem(SETTINGS_KEY, JSON.stringify(settings))
}

export function getCookerSettings() {
  if (!stateCache) {
    stateCache = readFromStorage()
  }
  return { ...stateCache }
}

export function applyCookerSettings(settings) {
  if (typeof document === 'undefined') {
    return
  }

  const root = document.documentElement
  const body = document.body
  const theme = settings.theme || 'light'
  root.style.setProperty('--cooker-font-scale', String(settings.fontScale || 1))
  body.classList.toggle('theme-dark', theme === 'dark')
  body.classList.toggle('theme-light', theme !== 'dark')
}

export function updateCookerSettings(partial = {}) {
  const next = normalize({
    ...getCookerSettings(),
    ...partial
  })

  stateCache = next
  writeToStorage(next)
  applyCookerSettings(next)

  if (typeof window !== 'undefined') {
    window.dispatchEvent(new CustomEvent('cooker-settings-changed', { detail: next }))
  }

  return { ...next }
}

export function initCookerSettings() {
  const settings = getCookerSettings()
  applyCookerSettings(settings)
  return settings
}
