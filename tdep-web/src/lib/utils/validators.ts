export interface ValidationRule {
  required?: boolean
  message?: string
  min?: number
  max?: number
  pattern?: RegExp
  validator?: (value: unknown) => boolean | string
}

export interface ValidationResult {
  valid: boolean
  message?: string
}

export function validateRequired(value: unknown, fieldName: string = '此字段'): ValidationResult {
  if (value === null || value === undefined || value === '') {
    return {
      valid: false,
      message: `${fieldName}不能为空`,
    }
  }
  return { valid: true }
}

export function validateLength(
  value: string,
  min: number,
  max: number,
  fieldName: string = '此字段'
): ValidationResult {
  if (value.length < min || value.length > max) {
    return {
      valid: false,
      message: `${fieldName}长度应在${min}-${max}个字符之间`,
    }
  }
  return { valid: true }
}

export function validatePhone(value: string): ValidationResult {
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(value)) {
    return {
      valid: false,
      message: '请输入正确的手机号码',
    }
  }
  return { valid: true }
}

export function validateIdCard(value: string): ValidationResult {
  const idCardRegex = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  if (!idCardRegex.test(value)) {
    return {
      valid: false,
      message: '请输入正确的身份证号',
    }
  }
  return { valid: true }
}

export function validateEmail(value: string): ValidationResult {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(value)) {
    return {
      valid: false,
      message: '请输入正确的邮箱地址',
    }
  }
  return { valid: true }
}

export function validateField(value: unknown, rules: ValidationRule[]): ValidationResult {
  for (const rule of rules) {
    if (rule.required) {
      const result = validateRequired(value, rule.message)
      if (!result.valid) return result
    }

    if (typeof value === 'string') {
      if (rule.min !== undefined || rule.max !== undefined) {
        const result = validateLength(
          value,
          rule.min ?? 0,
          rule.max ?? Infinity,
          rule.message
        )
        if (!result.valid) return result
      }

      if (rule.pattern && !rule.pattern.test(value)) {
        return {
          valid: false,
          message: rule.message || '格式不正确',
        }
      }
    }

    if (rule.validator) {
      const result = rule.validator(value)
      if (result !== true) {
        return {
          valid: false,
          message: typeof result === 'string' ? result : rule.message || '验证失败',
        }
      }
    }
  }

  return { valid: true }
}
