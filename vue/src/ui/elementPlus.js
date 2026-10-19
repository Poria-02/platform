import {
  ElBadge, ElButton, ElCheckbox, ElDescriptions, ElDescriptionsItem, ElDialog, ElDrawer, ElEmpty,
  ElDatePicker, ElForm, ElFormItem, ElIcon, ElInput, ElInputNumber, ElLoading,
  ElMessage, ElMessageBox, ElOption, ElPagination, ElPopover,
  ElSelect, ElSwitch, ElTable, ElTableColumn, ElTabPane, ElTabs, ElTag, ElTree, ElTreeSelect
} from 'element-plus'
import 'element-plus/es/components/button/style/css.mjs'
import 'element-plus/es/components/badge/style/css.mjs'
import 'element-plus/es/components/checkbox/style/css.mjs'
import 'element-plus/es/components/date-picker/style/css.mjs'
import 'element-plus/es/components/descriptions/style/css.mjs'
import 'element-plus/es/components/dialog/style/css.mjs'
import 'element-plus/es/components/drawer/style/css.mjs'
import 'element-plus/es/components/empty/style/css.mjs'
import 'element-plus/es/components/form/style/css.mjs'
import 'element-plus/es/components/form-item/style/css.mjs'
import 'element-plus/es/components/icon/style/css.mjs'
import 'element-plus/es/components/input/style/css.mjs'
import 'element-plus/es/components/input-number/style/css.mjs'
import 'element-plus/es/components/loading/style/css.mjs'
import 'element-plus/es/components/message/style/css.mjs'
import 'element-plus/es/components/message-box/style/css.mjs'
import 'element-plus/es/components/pagination/style/css.mjs'
import 'element-plus/es/components/popover/style/css.mjs'
import 'element-plus/es/components/select/style/css.mjs'
import 'element-plus/es/components/switch/style/css.mjs'
import 'element-plus/es/components/table/style/css.mjs'
import 'element-plus/es/components/tabs/style/css.mjs'
import 'element-plus/es/components/tag/style/css.mjs'
import 'element-plus/es/components/tree/style/css.mjs'
import 'element-plus/es/components/tree-select/style/css.mjs'

// 只注册当前页面使用的组件和服务；新增控件时在这里补充组件及样式。
export default {
  install(app) {
    for (const component of [
      ElBadge, ElButton, ElCheckbox, ElDescriptions, ElDescriptionsItem, ElDialog, ElDrawer, ElEmpty,
      ElDatePicker, ElForm, ElFormItem, ElIcon, ElInput, ElInputNumber, ElLoading,
      ElMessage, ElMessageBox, ElOption, ElPagination, ElPopover,
      ElSelect, ElSwitch, ElTable, ElTableColumn, ElTabPane, ElTabs, ElTag, ElTree, ElTreeSelect
    ]) app.use(component)
  }
}
