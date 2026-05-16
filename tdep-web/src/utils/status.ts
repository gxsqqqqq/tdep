export function statusType(status?: string): 'success' | 'warning' | 'info' | 'primary' | 'danger' {
  if (!status) return 'info'
  if (['SUCCESS', 'SIGNED', 'EFFECTIVE', 'ARCHIVED', 'ACCEPTED', 'PAID', 'PASSED'].includes(status)) return 'success'
  if (['PENDING', 'GENERATED', 'UNDER_REVIEW', 'SUBMITTED', 'FILING_REVIEW'].includes(status)) return 'warning'
  if (['FAILED', 'VOIDED', 'REJECTED', 'FILING_REJECTED'].includes(status)) return 'danger'
  if (['DRAFT'].includes(status)) return 'info'
  return 'primary'
}

export function formatFileSize(size?: number) {
  if (!size) return '0 B'
  if (size < 1024) return `${size} B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`
  return `${(size / 1024 / 1024).toFixed(1)} MB`
}
