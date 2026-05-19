import { spacing } from '../tokens/spacing'
import { radius } from '../tokens/radius'

export const formPatterns = {
  input: {
    height: '40px',
    padding: `0 ${spacing[4]}`,
    borderRadius: radius.md,
    fontSize: '14px',
    lineHeight: '1.5',
    border: '1px solid var(--color-border)',
    backgroundColor: 'var(--color-bg-primary)',
    transition: 'border-color 0.3s ease, box-shadow 0.3s ease',
    '&:focus': {
      outline: 'none',
      borderColor: 'var(--color-primary)',
      boxShadow: '0 0 0 2px rgba(24, 144, 255, 0.1)',
    },
    '&:hover:not(:focus)': {
      borderColor: 'var(--color-primary-light)',
    },
    '&::placeholder': {
      color: 'var(--color-text-placeholder)',
    },
  },

  textarea: {
    minHeight: '100px',
    padding: `${spacing[3]} ${spacing[4]}`,
    borderRadius: radius.md,
    fontSize: '14px',
    lineHeight: '1.5',
    border: '1px solid var(--color-border)',
    backgroundColor: 'var(--color-bg-primary)',
    resize: 'vertical',
    fontFamily: 'inherit',
  },

  select: {
    height: '40px',
    padding: `0 ${spacing[8]} 0 ${spacing[4]}`,
    borderRadius: radius.md,
    fontSize: '14px',
    border: '1px solid var(--color-border)',
    backgroundColor: 'var(--color-bg-primary)',
    appearance: 'none',
    backgroundImage: `url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e")`,
    backgroundPosition: `right ${spacing[3]} center`,
    backgroundRepeat: 'no-repeat',
    backgroundSize: '16px 16px',
  },

  label: {
    display: 'block',
    marginBottom: spacing[2],
    fontSize: '14px',
    fontWeight: 500,
    color: 'var(--color-text-primary)',
  },

  error: {
    marginTop: spacing[1],
    fontSize: '12px',
    color: 'var(--color-error)',
  },

  helpText: {
    marginTop: spacing[1],
    fontSize: '12px',
    color: 'var(--color-text-secondary)',
  },
} as const

export type FormPattern = keyof typeof formPatterns
