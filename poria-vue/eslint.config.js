import js from '@eslint/js'
import pluginVue from 'eslint-plugin-vue'
import globals from 'globals'
import * as pinia from 'pinia'
import * as vue from 'vue'
import * as vueRouter from 'vue-router'

const autoImportedGlobals = Object.fromEntries([
  ...Object.keys(vue),
  ...Object.keys(vueRouter),
  ...Object.keys(pinia),
  'useDict',
  'selectDictLabel'
].map(name => [name, 'readonly']))

export default [
  {
    ignores: ['node_modules/**', 'dist/**', 'public/**']
  },
  js.configs.recommended,
  ...pluginVue.configs['flat/recommended'],
  {
    files: ['**/*.{js,vue}'],
    languageOptions: {
      globals: {
        ...globals.browser,
        ...globals.node,
        ...autoImportedGlobals
      }
    },
    rules: {
      'vue/multi-word-component-names': 'off',
      'no-unused-vars': ['warn', { argsIgnorePattern: '^_', caughtErrorsIgnorePattern: '^_' }],
      'vue/no-unused-vars': 'warn',
      'vue/attributes-order': 'off',
      'vue/html-self-closing': 'off',
      'vue/max-attributes-per-line': 'off',
      'vue/multiline-html-element-content-newline': 'off',
      'vue/singleline-html-element-content-newline': 'off'
    }
  }
]
