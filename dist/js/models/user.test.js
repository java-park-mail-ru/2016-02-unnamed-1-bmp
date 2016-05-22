define(function(require) {
  var Backbone = require('backbone');
  var User = require('models/user');
  QUnit.module('models/user');

  QUnit.test('Создание объекта User', function() {
    var u = new User({ login: 'le-me', email: 'ya@ya.ru' });

    QUnit.ok(u instanceof Backbone.Model, 'User со всеми заполненными полями');
  });

  QUnit.test('Валидация User', function() {
    var toTest = [
      [{}, 'Все поля пустые', ['login', 'email']],
      [{ login: '' }, 'Пустой login', ['login']],
      [{ login: '      ' }, 'login из пробелов', ['login']],
      [{ email: '' }, 'Пустой email', ['email']],
      [{ email: 'le-meyandex.ru' }, 'Email без собачки', ['email']],
      [{ login: '123', password: '123', email: 'le-me@ya.ru' }, 'Короткий пароль', ['password']],
      [{ login: '', password: '' }, 'Пустой пароль и email', ['password', 'email']],
      [{ login: 'my-login', email: 'le-me@yandex.ru' }, 'Валидный кейс без пароля', []],
      [{ login: 'my-login', email: 'le-me@yandex.ru', password: 'trusting' }, 'Валидный кейс с паролем', []],
      [{ isAnonymous: true }, 'Все поля пустые (аноним)', []],
      [{ isAnonymous: true, login: '' }, 'Пустой логин (аноним)', []],
      [{ isAnonymous: true, login: 'le-me' }, 'С логином (аноним)', []]
    ];

    _.each(toTest, function(item) {
      var uData = item[0],
          comment = item[1],
          errorFieldExpect = item[2] || [];

      var u = new User();
      var errorReal = u.validate(uData);

      if(errorReal === undefined) {
        // ошибки нет
        QUnit.ok(errorFieldExpect.length === 0, comment + ': без ошибок');
      }
      else
      {
        // поля с ошибками
        var errorFields = errorReal.map(function(error) {
          return error.field;
        });

        // убедимся, что валидация вернула ошибку в каждом из ожидаемых полей
        _.each(errorFieldExpect, function(expect) {
          QUnit.ok(errorFields.indexOf(expect) !== -1, comment + ': ошибка в поле ' + expect);
        });
      }
    });
  });
});
