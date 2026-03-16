/**
 * 校验邮箱格式
 * @param email 邮箱地址
 * @returns 是否为有效邮箱格式
 */
export function isValidEmail(email: string): boolean {
  const v = email.trim();
  return /^(?!.*\.\.?)(?!\.)(?!.*\.$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(v);
}

/**
 * 文件验证结果
 */
export interface FileValidationResult {
  valid: boolean;
  error?: string;
}

/**
 * 验证图片文件（类型和大小）
 * @param file 文件对象
 * @param maxSizeMB 最大文件大小（MB），默认 2MB
 * @returns 验证结果
 */
export function validateImageFile(file: File, maxSizeMB: number = 2): FileValidationResult {
  if (!file.type.startsWith('image/')) {
    return { valid: false, error: '请选择图片文件' };
  }
  if (file.size > maxSizeMB * 1024 * 1024) {
    return { valid: false, error: `图片大小不能超过 ${maxSizeMB}MB` };
  }
  return { valid: true };
}
