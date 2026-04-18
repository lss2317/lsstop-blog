import http from '@/utils/http.ts';
import type { PhotoAlbum, PhotoAlbumImage, PhotoAlbumInfo } from './types';

export * from './types';

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
