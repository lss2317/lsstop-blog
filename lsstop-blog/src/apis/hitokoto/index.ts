/** 一言响应 */
export interface HitokotoResponse {
  /** 一言内容 */
  hitokoto: string;
}

/** 获取一言 */
export async function getHitokoto(): Promise<string> {
  const res = await fetch('https://v1.hitokoto.cn?c=i');
  const data: HitokotoResponse = await res.json();
  return data.hitokoto;
}
