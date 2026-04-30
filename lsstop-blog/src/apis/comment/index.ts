import http from '@/utils/http';
import type {
  CommentPageVO,
  CommentQueryParams,
  AddCommentParams,
  AddCommentResult,
  ReplyQueryParams,
  Reply,
} from './types';

export * from './types';

/**
 * 获取评论列表
 */
export function getComments(params: CommentQueryParams, showProgress = true) {
  return http.get<CommentPageVO>('/comment/listComment', { params, showProgress });
}

/**
 * 添加评论或回复评论
 * 当parentId为null或undefined时，添加主评论
 * 当parentId存在时，回复对应评论
 */
export function addComment(data: AddCommentParams) {
  return http.post<AddCommentResult>('/comment/addComment', data, { showProgress: false });
}

/**
 * 获取子评论列表
 */
export function getReplyList(params: ReplyQueryParams) {
  return http.get<Reply[]>('/comment/listReply', { params, showProgress: false });
}

/**
 * 删除评论
 * @param commentId 评论ID
 */
export function deleteComment(commentId: number) {
  return http.post('/comment/deleteComment', { commentId }, { showProgress: false });
}
