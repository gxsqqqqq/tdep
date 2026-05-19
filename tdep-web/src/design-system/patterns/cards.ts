import { shadows } from '../tokens/shadows'
import { radius } from '../tokens/radius'
import { spacing } from '../tokens/spacing'

export const cardPatterns = {
  default: {
    padding: spacing[6],
    borderRadius: radius.lg,
    boxShadow: shadows.sm,
    backgroundColor: 'var(--color-bg-primary)',
    transition: 'box-shadow 0.3s ease, transform 0.3s ease',
    '&:hover': {
      boxShadow: shadows.md,
      transform: 'translateY(-2px)',
    },
  },

  elevated: {
    padding: spacing[6],
    borderRadius: radius.lg,
    boxShadow: shadows.md,
    backgroundColor: 'var(--color-bg-primary)',
    '&:hover': {
      boxShadow: shadows.lg,
    },
  },

  bordered: {
    padding: spacing[6],
    borderRadius: radius.lg,
    border: '1px solid var(--color-border)',
    backgroundColor: 'var(--color-bg-primary)',
  },

  flat: {
    padding: spacing[6],
    borderRadius: radius.md,
    backgroundColor: 'var(--color-bg-secondary)',
  },

  compact: {
    padding: spacing[4],
    borderRadius: radius.md,
    boxShadow: shadows.sm,
    backgroundColor: 'var(--color-bg-primary)',
  },
} as const

export type CardPattern = keyof typeof cardPatterns
