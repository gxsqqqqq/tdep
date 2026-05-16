export interface RouteMetaPermission {
  title: string
  icon?: string
  permission?: string
  permissions?: string[]
  roles?: string[]
  hidden?: boolean
  affix?: boolean
  keepAlive?: boolean
}

export interface AppRouteRecord {
  path: string
  name?: string
  component?: unknown
  redirect?: string
  meta?: RouteMetaPermission
  children?: AppRouteRecord[]
}

export interface MenuItem {
  path: string
  title: string
  icon?: string
  permission?: string
  roles?: string[]
  children?: MenuItem[]
}
