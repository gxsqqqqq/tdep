export function updatePageTitle(title?: string): void {
  if (title) {
    document.title = `${title} - 数字司法平台`
  } else {
    document.title = '数字司法平台'
  }
}

export default function setupTitleMiddleware() {
  if (typeof window !== 'undefined') {
    updatePageTitle()
  }
}
