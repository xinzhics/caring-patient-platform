module.exports = {
  root: true,
  env: {
    node: true,
    browser: true,
    es6: true
  },
  extends: [
    'plugin:vue/essential',
    'eslint:recommended',
    // '@typescript-eslint/recommended',
    // 'plugin:vue/vue3-recommended',
    'prettier'
  ],
  parser: 'vue-eslint-parser',
  parserOptions: {
    parser: '@typescript-eslint/parser',
    ecmaVersion: 2018,  // Updated to support object spread syntax
    sourceType: 'module',
    allowImportExportEverywhere: true,
    codeFrame: false
  },
  plugins: ['@typescript-eslint', 'vue'],
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'vue/multi-word-component-names': 'off',
    '@typescript-eslint/no-unused-vars': ['warn', { argsIgnorePattern: '^_' }],
    '@typescript-eslint/explicit-function-return-type': 'off',
    '@typescript-eslint/explicit-module-boundary-types': 'off',
    '@typescript-eslint/no-explicit-any': 'warn',
    // Allow object spread syntax
    'prefer-spread': 'off',
    'no-useless-escape': 'off'
  },
  // Only apply Vue plugin rules to .vue files
  overrides: [
    {
      files: ['*.vue'],
      parser: 'vue-eslint-parser',
      parserOptions: {
        parser: '@typescript-eslint/parser',
        ecmaVersion: 2018,  // Updated to support object spread syntax
        sourceType: 'module',
        extraFileExtensions: ['.vue']
      }
    },
    {
      // For JS files, use standard JS parser instead of vue parser
      files: ['*.js'],
      parser: '@typescript-eslint/parser',
      parserOptions: {
        ecmaVersion: 2018,  // Updated to support object spread syntax
        sourceType: 'module'
      },
      rules: {
        // Disable Vue-specific rules for JS files
        'vue/no-duplicate-attributes': 'off',
        'vue/no-template-key': 'off',
        'vue/no-textarea-mustache': 'off',
        'vue/no-unused-vars': 'off',
        'vue/require-component-is': 'off',
        'vue/require-v-for-key': 'off',
        'vue/valid-v-bind': 'off',
        'vue/valid-v-cloak': 'off',
        'vue/valid-v-else-if': 'off',
        'vue/valid-v-else': 'off',
        'vue/valid-v-for': 'off',
        'vue/valid-v-html': 'off',
        'vue/valid-v-if': 'off',
        'vue/valid-v-model': 'off',
        'vue/valid-v-on': 'off',
        'vue/valid-v-once': 'off',
        'vue/valid-v-pre': 'off',
        'vue/valid-v-show': 'off',
        'vue/valid-v-text': 'off',
        'vue/attribute-hymentation': 'off',
        'vue/html-end-tags': 'off',
        'vue/html-indent': 'off',
        'vue/mustache-interpolation-spacing': 'off',
        'vue/v-bind-style': 'off',
        'vue/v-on-style': 'off',
        'vue/attributes-order': 'off',
        'vue/html-quotes': 'off',
        'vue/no-confusing-v-for-v-if': 'off',
        'vue/this-in-template': 'off',
        'vue/no-multi-spaces': 'off'
      }
    }
  ]
};