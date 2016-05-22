define(function(require) {
  var Backbone = require('backbone');
  var Session = require('models/session');
  QUnit.module('models/session');

  QUnit.test('Создание объекта Session', function() {
    var s = new Session();

    QUnit.ok(s instanceof Backbone.Model);
  });

  QUnit.test('Валидация Session', function() {
    var toTest = [
      [{}, 'Все поля пустые', ['login', 'password']],
      [{ login: '' }, 'Пустой login', ['login']],
      [{ login: '      ' }, 'login из пробелов', ['login']],
      [{ password: '' }, 'Пустой пароль', ['password']],
      [{ login: 'my-login', password: 'pass' }, 'Валидный кейс', []]
    ];

    _.each(toTest, function(item) {
      var sData = item[0],
          comment = item[1],
          errorFieldExpect = item[2] || [];

      var s = new Session();
      var errorReal = s.validate(sData);

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
