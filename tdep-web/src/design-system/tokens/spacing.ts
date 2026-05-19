export const spacing = {
  0: '0px',
  xs: '0.25rem',
  sm: '0.5rem',
  md: '1rem',
  lg: '1.5rem',
  xl: '2rem',
  '2xl': '3rem',
  '3xl': '4rem',
  1: '4px',
  2: '8px',
  3: '12px',
  4: '16px',
  5: '20px',
  6: '24px',
  8: '32px',
  10: '40px',
  12: '48px',
  16: '64px',
  20: '80px',
  24: '96px',
  32: '128px',
} as const

export type SpacingKey = keyof typeof spacing

export const layout = {
  maxWidth: '1440px',
  sidebarWidth: '256px',
  collapsedSidebarWidth: '64px',
  headerHeight: '64px',
  footerHeight: '56px',
  contentPadding: '24px',
} as const
