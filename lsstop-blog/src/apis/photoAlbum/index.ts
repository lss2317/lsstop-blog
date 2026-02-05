import http from '@/utils/http.ts';

/** 相册 */
export interface PhotoAlbum {
  /** id */
  id: number;
  /** 相册名 */
  photoAlbumName: string;
  /** 相册描述 */
  photoAlbumDesc: string;
  /** 相册封面 */
  photoAlbumCover: string;
}

/** 相册图片 */
export interface PhotoAlbumImage {
  /** 主键id */
  id: number;
  /** 照片地址 */
  photoSrc: string;
}

/** 相册信息 */
export interface PhotoAlbumInfo {
  /** 相册名 */
  photoAlbumName: string;
  /** 相册封面 */
  photoAlbumCover: string;
}

// 获取相册列表
export function listPhotoAlbum() {
  return http.get<PhotoAlbum[]>('/photoAlbum/listPhotoAlbum');
}

// 根据相册id获取照片列表
export function listPhotoByAlbumId(albumId: number) {
  return http.get<PhotoAlbumImage[]>('/photoAlbum/listPhoto', { params: { albumId } });
}

// 根据id获取相册信息
export function getPhotoAlbumById(id: number) {
  return http.get<PhotoAlbumInfo>(`/photoAlbum/${id}`);
}
