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
  },
  monthDay(value: DateValue): string {
    return dayjs(value).format('MM-DD')
  },
}

/** 获取年份数字 */
export const getYear = (value: DateValue): number => {
  return dayjs(value).year()
}

/** 获取月份数字 (1-12) */
export const getMonth = (value: DateValue): number => {
  return dayjs(value).month() + 1
}

export const formatTime = (time: DateValue): string => {
  if (!time) return ''
  const date = dayjs(time)
  const now = dayjs()
  const diffHours = now.diff(date, 'hour')

  if (diffHours < 1) return '刚刚'
  if (diffHours < 24) return `${diffHours} 小时前`

  return date.format('YYYY.MM.DD')
}
