/** 业务响应码 */
export enum ResponseCode {
  /** 成功 */
  SUCCESS = 200,
}

/** HTTP 状态码 */
export enum HttpStatus {
  /** 未授权 */
  UNAUTHORIZED = 401,
  /** 请求频繁 */
  TOO_MANY_REQUESTS = 429,
}
