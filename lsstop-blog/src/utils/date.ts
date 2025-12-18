import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'

dayjs.locale('zh-cn')

export type DateValue = dayjs.ConfigType

export const dateFormat = {
  year(value: DateValue): string {
    return dayjs(value).format('YYYY')
  },
  // date(value: DateValue): string {
  //   return dayjs(value).format('YYYY-MM-DD')
  // },
  // time(value: DateValue): string {
  //   return dayjs(value).format('HH:mm:ss')
  // }
}
