type ColorScale = {
  50: string
  100: string
  200: string
  300: string
  400: string
  500: string
  600: string
  700: string
  800: string
  900: string
  950?: string
}

type JudicialColorScale = {
  50: string
  100: string
  200: string
  300: string
  400: string
  500: string
  600: string
  700: string
  800: string
  900: string
  950: string
}

export const JudicialDesignTokens = {
  colors: {
    judicial: {
      950: '#0c1929',
      900: '#132f4c',
      800: '#1e3a5f',
      700: '#2563eb',
      600: '#3b82f6',
      500: '#60a5fa',
      400: '#93c5fd',
      300: '#bfdbfe',
      200: '#dbeafe',
      100: '#eff6ff',
      50: '#f0f9ff',
    } as const,
    gold: {
      900: '#78350f',
      800: '#92400e',
      700: '#b45309',
      600: '#d97706',
      500: '#f59e0b',
      400: '#fbbf24',
      300: '#fcd34d',
      200: '#fde68a',
      100: '#fef3c7',
    } as const,
    justice: {
      900: '#064e3b',
      800: '#065f46',
      700: '#047857',
      600: '#059669',
      500: '#10b981',
      400: '#34d399',
      300: '#6ee7b7',
      200: '#a7f3d0',
      100: '#d1fae5',
    } as const,
    danger: {
      900: '#7f1d1d',
      800: '#991b1b',
      700: '#b91c1c',
      600: '#dc2626',
      500: '#ef4444',
      400: '#f87171',
      300: '#fca5a5',
      200: '#fecaca',
      100: '#fee2e2',
    } as const,
    neutral: {
      950: '#0f172a',
      900: '#111827',
      800: '#1f2937',
      700: '#374151',
      600: '#4b5563',
      500: '#6b7280',
      400: '#9ca3af',
      300: '#d1d5db',
      200: '#e5e7eb',
      100: '#f3f4f6',
      50: '#f9fafb',
    } as const,
    blockchain: {
      900: '#3b0764',
      800: '#581c87',
      700: '#6b21a8',
      600: '#7c3aed',
      500: '#8b5cf6',
      400: '#a78bfa',
      300: '#c4b5fd',
      200: '#ddd6fe',
      100: '#ede9fe',
    } as const,
  } as const,

  typography: {
    fontFamily: {
      mono: "'JetBrains Mono', 'Fira Code', 'Cascadia Code', 'Consolas', monospace",
      sans: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
      serif: "'Noto Serif SC', 'Source Han Serif SC', 'SimSun', serif",
    } as const,
    fontSize: {
      xs: '0.75rem',
      sm: '0.875rem',
      base: '1rem',
      lg: '1.125rem',
      xl: '1.25rem',
      '2xl': '1.5rem',
      '3xl': '1.875rem',
      '4xl': '2.25rem',
    } as const,
    fontWeight: {
      normal: '400',
      medium: '500',
      semibold: '600',
      bold: '700',
    } as const,
    lineHeight: {
      tight: '1.25',
      normal: '1.5',
      relaxed: '1.75',
    } as const,
  } as const,

  spacing: {
    0: '0',
    1: '0.25rem',
    2: '0.5rem',
    3: '0.75rem',
    4: '1rem',
    5: '1.25rem',
    6: '1.5rem',
    8: '2rem',
    10: '2.5rem',
    12: '3rem',
    16: '4rem',
  } as const,

  borderRadius: {
    none: '0',
    sm: '0.25rem',
    base: '0.375rem',
    md: '0.5rem',
    lg: '0.75rem',
    xl: '1rem',
    '2xl': '1.5rem',
    full: '9999px',
  } as const,

  shadows: {
    xs: '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
    sm: '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1)',
    base: '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1)',
    md: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1)',
    lg: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -4px rgba(0, 0, 0, 0.1)',
    xl: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1)',
    '2xl': '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
    inner: 'inset 0 2px 4px 0 rgba(0, 0, 0, 0.05)',
    judicial: '0 4px 14px 0 rgba(30, 58, 95, 0.15), 0 1px 3px rgba(30, 58, 95, 0.1)',
    seal: '0 0 20px 5px rgba(217, 119, 6, 0.3), 0 0 40px 10px rgba(217, 119, 6, 0.1)',
    blockchain: '0 0 15px 3px rgba(124, 58, 237, 0.2)',
    success: '0 0 15px 3px rgba(5, 150, 105, 0.2)',
  } as const,

  animations: {
    duration: {
      fast: '150ms',
      normal: '300ms',
      slow: '500ms',
      slower: '700ms',
      seal: '1200ms',
    } as const,
    easing: {
      default: 'cubic-bezier(0.4, 0, 0.2, 1)',
      easeIn: 'cubic-bezier(0.4, 0, 1, 1)',
      easeOut: 'cubic-bezier(0, 0, 0.2, 1)',
      easeInOut: 'cubic-bezier(0.4, 0, 0.2, 1)',
      bounce: 'cubic-bezier(0.68, -0.55, 0.265, 1.55)',
      sealPress: 'cubic-bezier(0.175, 0.885, 0.32, 1.275)',
    } as const,
  } as const,

  timeline: {
    nodeSize: {
      sm: '32px',
      base: '40px',
      lg: '48px',
    } as const,
    connectorWidth: '2px',
    colors: {
      completed: '#059669',
      current: '#2563eb',
      pending: '#d1d5db',
    } as const,
  } as const,

  seal: {
    size: {
      sm: '80px',
      base: '120px',
      lg: '160px',
    } as const,
    borderWidth: '3px',
    colors: {
      border: '#d97706',
      text: '#dc2626',
      background: 'rgba(255, 255, 255, 0.95)',
      shadow: 'rgba(217, 119, 6, 0.4)',
    } as const,
    fontFamily: "'STKaiti', 'KaiTi', 'SimSun', serif",
  } as const,

  audit: {
    colors: {
      create: '#2563eb',
      read: '#6b7280',
      sign: '#059669',
      download: '#8b5cf6',
      archive: '#d97706',
      reject: '#dc2626',
    } as const,
    iconBg: {
      create: 'bg-blue-100',
      read: 'bg-gray-100',
      sign: 'bg-green-100',
      download: 'bg-purple-100',
      archive: 'bg-amber-100',
      reject: 'bg-red-100',
    } as const,
  } as const,

  hashDisplay: {
    bgColor: '#0f172a',
    textColor: '#10b981',
    fontFamily: "'JetBrains Mono', monospace",
    fontSize: '0.875rem',
    padding: '0.75rem 1rem',
    borderRadius: '0.5rem',
    maxWidth: '100%',
    truncateLength: 12,
  } as const,

  cards: {
    variants: {
      default: {
        bg: 'white',
        border: 'border-gray-200',
        shadow: 'shadow-md',
        radius: 'rounded-lg',
      } as const,
      judicial: {
        bg: 'white',
        border: 'border-judicial-200',
        shadow: 'shadow-judicial',
        radius: 'rounded-lg',
        headerBg: 'bg-gradient-to-r from-judicial-800 to-judicial-700',
        headerText: 'text-white',
      } as const,
      evidence: {
        bg: 'white',
        border: 'border-gray-200',
        shadow: 'shadow-md',
        radius: 'rounded-lg',
        hoverShadow: 'shadow-lg',
      } as const,
      seal: {
        bg: 'bg-gradient-to-br from-amber-50 to-orange-50',
        border: 'border-gold-300',
        shadow: 'shadow-seal',
        radius: 'rounded-xl',
      } as const,
      audit: {
        bg: 'gray-50',
        border: 'border-gray-200',
        shadow: 'none',
        radius: 'rounded-md',
      } as const,
      blockchain: {
        bg: 'bg-gradient-to-br from-violet-950 to-slate-900',
        border: 'border-purple-500/30',
        shadow: 'shadow-blockchain',
        radius: 'rounded-xl',
        text: 'text-white',
      } as const,
    } as const,
  } as const,
} as const

export type JudicialColors = typeof JudicialDesignTokens.colors
export type JudicialTypography = typeof JudicialDesignTokens.typography
export type JudicialSpacing = typeof JudicialDesignTokens.spacing
export type JudicialBorderRadius = typeof JudicialDesignTokens.borderRadius
export type JudicialShadows = typeof JudicialDesignTokens.shadows
export type JudicialAnimations = typeof JudicialDesignTokens.animations
export type JudicialTimeline = typeof JudicialDesignTokens.timeline
export type JudicialSeal = typeof JudicialDesignTokens.seal
export type JudicialAudit = typeof JudicialDesignTokens.audit
export type JudicialHashDisplay = typeof JudicialDesignTokens.hashDisplay
export type JudicialCards = typeof JudicialDesignTokens.cards

export type CardVariant = keyof JudicialCards['variants']
export type AuditActionType = keyof JudicialAudit['colors']

export const Colors = JudicialDesignTokens.colors
export const Typography = JudicialDesignTokens.typography
export const Spacing = JudicialDesignTokens.spacing
export const Shadows = JudicialDesignTokens.shadows
export const Animations = JudicialDesignTokens.animations
export const Seal = JudicialDesignTokens.seal
export const Audit = JudicialDesignTokens.audit
export const HashDisplay = JudicialDesignTokens.hashDisplay

export default JudicialDesignTokens

export function toCSSCustomProperties(prefix = 'tdep'): Record<string, string> {
  const result: Record<string, string> = {}
  const { colors, typography, spacing, borderRadius, shadows, animations, timeline, seal, audit, hashDisplay } =
    JudicialDesignTokens

  const colorGroups: Record<string, Record<string, string>> = {
    ...colors,
  }

  for (const [groupName, scale] of Object.entries(colorGroups)) {
    for (const [level, value] of Object.entries(scale)) {
      result[`--${prefix}-${groupName}-${level}`] = value
    }
  }

  const fontFamilies = typography.fontFamily
  for (const [name, value] of Object.entries(fontFamilies)) {
    result[`--${prefix}-font-${name}`] = value
  }

  const fontSizes = typography.fontSize
  for (const [name, value] of Object.entries(fontSizes)) {
    result[`--${prefix}-font-size-${name}`] = value
  }

  const fontWeights = typography.fontWeight
  for (const [name, value] of Object.entries(fontWeights)) {
    result[`--${prefix}-font-weight-${name}`] = String(value)
  }

  const lineHeights = typography.lineHeight
  for (const [name, value] of Object.entries(lineHeights)) {
    result[`--${prefix}-line-height-${name}`] = String(value)
  }

  for (const [key, value] of Object.entries(spacing)) {
    result[`--${prefix}-spacing-${key}`] = value
  }

  for (const [key, value] of Object.entries(borderRadius)) {
    result[`--${prefix}-radius-${key}`] = value
  }

  for (const [key, value] of Object.entries(shadows)) {
    result[`--${prefix}-shadow-${key}`] = value
  }

  const durations = animations.duration
  for (const [key, value] of Object.entries(durations)) {
    result[`--${prefix}-duration-${key}`] = value
  }

  const easings = animations.easing
  for (const [key, value] of Object.entries(easings)) {
    result[`--${prefix}-easing-${key}`] = value
  }

  result[`--${prefix}-timeline-connector-width`] = timeline.connectorWidth
  const timelineColors = timeline.colors
  for (const [key, value] of Object.entries(timelineColors)) {
    result[`--${prefix}-timeline-color-${key}`] = value
  }
  const nodeSizes = timeline.nodeSize
  for (const [key, value] of Object.entries(nodeSizes)) {
    result[`--${prefix}-timeline-node-${key}`] = value
  }

  result[`--${prefix}-seal-border-width`] = seal.borderWidth
  result[`--${prefix}-seal-font-family`] = seal.fontFamily
  const sealSizes = seal.size
  for (const [key, value] of Object.entries(sealSizes)) {
    result[`--${prefix}-seal-size-${key}`] = value
  }
  const sealColors = seal.colors
  for (const [key, value] of Object.entries(sealColors)) {
    result[`--${prefix}-seal-color-${key}`] = value
  }

  const auditColors = audit.colors
  for (const [key, value] of Object.entries(auditColors)) {
    result[`--${prefix}-audit-color-${key}`] = value
  }

  result[`--${prefix}-hash-bg`] = hashDisplay.bgColor
  result[`--${prefix}-hash-text`] = hashDisplay.textColor
  result[`--${prefix}-hash-font`] = hashDisplay.fontFamily
  result[`--${prefix}-hash-font-size`] = hashDisplay.fontSize
  result[`--${prefix}-hash-padding`] = hashDisplay.padding
  result[`--${prefix}-hash-radius`] = hashDisplay.borderRadius
  result[`--${prefix}-hash-truncate`] = String(hashDisplay.truncateLength)

  return result
}

export function injectCSSVariables(root: HTMLElement | null = document.documentElement, prefix = 'tdep'): void {
  if (!root) return
  const vars = toCSSCustomProperties(prefix)
  for (const [prop, value] of Object.entries(vars)) {
    root.style.setProperty(prop, value)
  }
}
