import { colors } from '../tokens/colors'
import { spacing } from '../tokens/spacing'
import { shadows } from '../tokens/shadows'

export const navigationPatterns = {
  header: {
    height: '64px',
    padding: `0 ${spacing[6]`,
    backgroundColor: colors.primary[500],
    color: '#ffffff',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    zIndex: 1050,
    boxShadow: shadows.md,
  },

  sidebar: {
    width: '256px',
    backgroundColor: 'var(--color-bg-primary)',
    borderRight: '1px solid var(--color-border-light)',
    position: 'fixed',
    top: '64px',
    left: 0,
    bottom: 0,
    overflowY: 'auto',
    transition: 'width 0.3s ease',
    zIndex: 1040,

    '&.collapsed': {
      width: '64px',
    },
  },

  sidebarItem: {
    display: 'flex',
    alignItems: 'center',
    padding: `${spacing[3]} ${spacing[4]}`,
    margin: `${spacing[1]} ${spacing[3]}`,
    borderRadius: radius.md,
    color: 'var(--color-text-primary)',
    textDecoration: 'none',
    transition: 'all 0.3s ease',
    cursor: 'pointer',

    '&:hover': {
      backgroundColor: 'var(--color-bg-hover)',
      color: 'var(--color-primary)',
    },

    '&.active': {
      backgroundColor: 'var(--color-primary-light)',
      color: 'var(--color-primary-dark)',
      fontWeight: 600,
    },
  },

  breadcrumb: {
    display: 'flex',
    alignItems: 'center',
    gap: spacing[2],
    padding: `${spacing[3]} 0`,
    fontSize: '14px',
    color: 'var(--color-text-secondary)',

    a: {
      color: 'var(--color-text-secondary)',
      textDecoration: 'none',

      '&:hover': {
        color: 'var(--color-primary)',
      },
    },
  },

  tabs: {
    display: 'flex',
    borderBottom: '2px solid var(--color-border-light)',
    gap: spacing[1],

    item: {
      padding: `${spacing[3]} ${spacing[4]}`,
      color: 'var(--color-text-secondary)',
      borderBottom: '2px solid transparent',
      marginBottom: '-2px',
      cursor: 'pointer',
      transition: 'all 0.3s ease',

      '&:hover': {
        color: 'var(--color-primary)',
      },

      '&.active': {
        color: 'var(--color-primary)',
        borderBottomColor: 'var(--color-primary)',
      },
    },
  },
} as const

import { radius } from '../tokens/radius'

export type NavigationPattern = keyof typeof navigationPatterns
