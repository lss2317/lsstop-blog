import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'

dayjs.locale('zh-cn')

export type DateValue = dayjs.ConfigType

export const dateFormat = {
  year(value: DateValue): string {
    return dayjs(value).format('YYYY')
  },
  datetime(value: DateValue): string {
    return dayjs(value).format('YYYY-MM-DD HH:mm:ss')
  }
}

// 格式化时间显示（用于评论等场景）
export const formatTime = (time: DateValue): string => {
  if (!time) return ''
  const date = dayjs(time)
  const now = dayjs()
  const diffHours = now.diff(date, 'hour')

  if (diffHours < 1) return '刚刚'
  if (diffHours < 24) return `${diffHours} 小时前`

  return date.format('YYYY.MM.DD')
}
