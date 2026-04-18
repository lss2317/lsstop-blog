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
