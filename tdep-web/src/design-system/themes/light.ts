import { colors } from '../tokens/colors'

export const lightTheme = {
  name: 'light' as const,
  colors: {
    ...colors,
    background: {
      ...colors.background,
      primary: '#ffffff',
      secondary: '#f5f5f5',
      tertiary: '#fafafa',
    },
    text: {
      ...colors.text,
      primary: '#1a1a1a',
      secondary: '#666666',
    },
  },
  components: {
    card: {
      backgroundColor: '#ffffff',
      borderColor: '#e8e8e8',
      shadow: '0 1px 2px rgba(0, 0, 0, 0.03)',
      hoverShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
    },
    modal: {
      maskBackgroundColor: 'rgba(0, 0, 0, 0.45)',
      backgroundColor: '#ffffff',
    },
    input: {
      backgroundColor: '#ffffff',
      borderColor: '#d9d9d9',
      borderColorHover: '#40a9ff',
      borderColorFocus: '#1890ff',
    },
    button: {
      primaryBackground: '#1890ff',
      primaryColor: '#ffffff',
      primaryHoverBackground: '#40a9ff',
      defaultBackground: '#ffffff',
      defaultBorderColor: '#d9d9d9',
      defaultColor: 'rgba(0, 0, 0, 0.85)',
      dangerBackground: '#ff4d4f',
      dangerColor: '#ffffff',
      ghostBackground: 'transparent',
      ghostColor: '#1890ff',
      textBackground: 'transparent',
      textColor: '#1890ff',
    },
    timeline: {
      lineColor: '#e8e8e8',
      lineActiveColor: '#1890ff',
      nodeCompletedBg: '#1890ff',
      nodeActiveBorder: '#1890ff',
      nodePendingBorder: '#d9d9d9',
    },
    badge: {
      dotSize: '6px',
    },
  },
}

export type LightTheme = typeof lightTheme
