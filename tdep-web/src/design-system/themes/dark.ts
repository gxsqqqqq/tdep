import { colors } from '../tokens/colors'

export const darkTheme = {
  name: 'dark' as const,
  colors: {
    ...colors,
    background: {
      ...colors.background,
      primary: '#141414',
      secondary: '#1f1f1f',
      tertiary: '#262626',
    },
    text: {
      ...colors.text,
      primary: '#ffffffd9',
      secondary: '#ffffffa6',
      disabled: '#ffffff73',
    },
    border: {
      default: '#434343',
      light: '#303030',
      dark: '#434343',
    },
  },
  components: {
    card: {
      backgroundColor: '#1f1f1f',
      borderColor: '#434343',
      shadow: '0 1px 2px rgba(0, 0, 0, 0.15)',
      hoverShadow: '0 4px 12px rgba(0, 0, 0, 0.3)',
    },
    modal: {
      maskBackgroundColor: 'rgba(0, 0, 0, 0.65)',
      backgroundColor: '#1f1f1f',
    },
    input: {
      backgroundColor: '#262626',
      borderColor: '#434343',
      borderColorHover: '#59b8ff',
      borderColorFocus: '#1890ff',
    },
    button: {
      primaryBackground: '#177ddc',
      primaryColor: '#ffffff',
      primaryHoverBackground: '#3c9ae8',
      defaultBackground: 'transparent',
      defaultBorderColor: '#434343',
      defaultColor: '#ffffffa6',
      dangerBackground: '#a61d24',
      dangerColor: '#ffffff',
      ghostBackground: 'transparent',
      ghostColor: '#177ddc',
      textBackground: 'transparent',
      textColor: '#177ddc',
    },
    timeline: {
      lineColor: '#434343',
      lineActiveColor: '#177ddc',
      nodeCompletedBg: '#177ddc',
      nodeActiveBorder: '#177ddc',
      nodePendingBorder: '#434343',
    },
    badge: {
      dotSize: '6px',
    },
  },
}

export type DarkTheme = typeof darkTheme
