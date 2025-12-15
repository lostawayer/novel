/**
 * 表单验证规则
 */

import type { FormRule } from '@/types'

/**
 * 手机号验证
 */
export function validatePhone(rule: any, value: string, callback: Function) {
  if (!value) {
    callback()
    return
  }
  const phoneReg = /^1[3-9]\d{9}$/
  if (!phoneReg.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

/**
 * 邮箱验证
 */
export function validateEmail(rule: any, value: string, callback: Function) {
  if (!value) {
    callback()
    return
  }
  const emailReg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/
  if (!emailReg.test(value)) {
    callback(new Error('请输入正确的邮箱'))
  } else {
    callback()
  }
}

/**
 * 身份证验证
 */
export function validateIdCard(rule: any, value: string, callback: Function) {
  if (!value) {
    callback()
    return
  }
  const idCardReg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  if (!idCardReg.test(value)) {
    callback(new Error('请输入正确的身份证号'))
  } else {
    callback()
  }
}

/**
 * URL 验证
 */
export function validateUrl(rule: any, value: string, callback: Function) {
  if (!value) {
    callback()
    return
  }
  const urlReg =
    /^(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?$/
  if (!urlReg.test(value)) {
    callback(new Error('请输入正确的URL'))
  } else {
    callback()
  }
}

/**
 * 数字验证
 */
export function validateNumber(rule: any, value: string, callback: Function) {
  if (!value) {
    callback()
    return
  }
  const numberReg = /^[0-9]*$/
  if (!numberReg.test(value)) {
    callback(new Error('请输入数字'))
  } else {
    callback()
  }
}

/**
 * 金额验证（最多两位小数）
 */
export function validateMoney(rule: any, value: string, callback: Function) {
  if (!value) {
    callback()
    return
  }
  const moneyReg = /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/
  if (!moneyReg.test(value)) {
    callback(new Error('请输入正确的金额（最多两位小数）'))
  } else {
    callback()
  }
}

/**
 * 密码强度验证（至少6位）
 */
export function validatePassword(rule: any, value: string, callback: Function) {
  if (!value) {
    callback(new Error('请输入密码'))
    return
  }
  if (value.length < 6) {
    callback(new Error('密码至少6位'))
  } else {
    callback()
  }
}

/**
 * 用户名验证（字母、数字、下划线，4-16位）
 */
export function validateUsername(rule: any, value: string, callback: Function) {
  if (!value) {
    callback(new Error('请输入用户名'))
    return
  }
  const usernameReg = /^[a-zA-Z0-9_]{4,16}$/
  if (!usernameReg.test(value)) {
    callback(new Error('用户名由4-16位字母、数字、下划线组成'))
  } else {
    callback()
  }
}

/**
 * 通用验证规则对象
 */
const validate = {
  phone: validatePhone,
  email: validateEmail,
  idCard: validateIdCard,
  url: validateUrl,
  number: validateNumber,
  money: validateMoney,
  password: validatePassword,
  username: validateUsername,
}

export default validate

