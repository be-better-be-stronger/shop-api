module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'type-enum': [2, 'always', ['feat', 'fix', 'refactor', 'perf', 'test', 'docs', 'style', 'chore']],
    'scope-enum': [
      2,
      'always',
      ['auth', 'security', 'product', 'category', 'cart', 'order', 'common', 'config', 'exception', 'validation', 'db', 'build', 'ci'],
    ],
    'scope-empty': [2, 'never'],
    'subject-empty': [2, 'never'],
    'subject-full-stop': [2, 'never', '.'],
    'header-max-length': [2, 'always', 100],
  },
};
