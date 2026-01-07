import http from '@/utils/http.ts'

/**
 * 相册VO
 */
export interface PhotoAlbumVO {
  /** id */
  id: number
  /** 相册名 */
  photoAlbumName: string
  /** 相册描述 */
  photoAlbumDesc: string
  /** 相册封面 */
  photoAlbumCover: string
}

// 获取相册列表
export function listPhotoAlbum() {
  return http.get<PhotoAlbumVO[]>('/photoAlbum/listPhotoAlbum')
}
