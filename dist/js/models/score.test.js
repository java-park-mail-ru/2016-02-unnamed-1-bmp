define(function(require) {
  var Backbone = require('backbone');
  QUnit.module('models/score');

  QUnit.test('Разные варианты создания объектов Score', function() {
    var Score = require('models/score');

    var toTest = [
      [{ name: 'English Name', score: 99 }, 'Имя по-английски'],
      [{ name: 'Русское имя', score: 779 }, 'Имя по-русски'],
      [{}, 'Без параметров'],
      [{ name: 'Name' }, 'Без очков'],
      [{ score: 46 }, 'Без имени'],
      [{ name: 'Negative', score: -45 }, 'С отрицательными очками']
    ];

    _.each(toTest, function(item) {
      var obj = new Score(item[0]);
      QUnit.ok(obj instanceof Backbone.Model, item[1]);
    });
  });
});
