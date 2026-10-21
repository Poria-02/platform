import {
  ElCheckbox, ElDescriptions, ElDescriptionsItem, ElEmpty, ElDatePicker, ElInputNumber,
  ElMessageBox, ElOption, ElPagination, ElPopover, ElSelect, ElSwitch,
  ElTable, ElTableColumn, ElTag, ElTree, ElTreeSelect
} from 'element-plus'
import 'element-plus/es/components/checkbox/style/css.mjs'
import 'element-plus/es/components/date-picker/style/css.mjs'
import 'element-plus/es/components/descriptions/style/css.mjs'
import 'element-plus/es/components/empty/style/css.mjs'
import 'element-plus/es/components/input-number/style/css.mjs'
import 'element-plus/es/components/message-box/style/css.mjs'
import 'element-plus/es/components/pagination/style/css.mjs'
import 'element-plus/es/components/popover/style/css.mjs'
import 'element-plus/es/components/select/style/css.mjs'
import 'element-plus/es/components/switch/style/css.mjs'
import 'element-plus/es/components/table/style/css.mjs'
import 'element-plus/es/components/tag/style/css.mjs'
import 'element-plus/es/components/tree/style/css.mjs'
import 'element-plus/es/components/tree-select/style/css.mjs'

// 新增业务控件时在这里补充组件及样式。
export default {
  install(app) {
    for (const component of [
      ElCheckbox, ElDescriptions, ElDescriptionsItem, ElEmpty, ElDatePicker, ElInputNumber,
      ElMessageBox, ElOption, ElPagination, ElPopover, ElSelect, ElSwitch,
      ElTable, ElTableColumn, ElTag, ElTree, ElTreeSelect
    ]) app.use(component)
  }
}
