import { UserRole } from '@/lib/constants/roles'

export interface User {
  id: string
  username: string
  realName: string
  email: string
  phone: string
  avatar?: string
  role: UserRole
  department?: string
  position?: string
  isActive: boolean
  lastLoginAt?: string
  createdAt: string
  updatedAt: string
}

export interface UserProfile {
  id: string
  username: string
  realName: string
  email: string
  phone: string
  avatar?: string
  idCard?: string
  gender?: 'male' | 'female'
  birthday?: string
  address?: string
}

export interface UpdateProfileRequest {
  realName?: string
  email?: string
  phone?: string
  avatar?: string
  gender?: 'male' | 'female'
  birthday?: string
  address?: string
}

export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

export interface UserListItem {
  id: string
  username: string
  realName: string
  role: UserRole
  department?: string
  isActive: boolean
  createdAt: string
}

export interface CreateUserRequest {
  username: string
  password: string
  realName: string
  email?: string
  phone?: string
  role: UserRole
  department?: string
}

export interface NotificationSettings {
  email: boolean
  sms: boolean
  push: boolean
  caseUpdate: boolean
  trialReminder: boolean
  documentReady: boolean
}
