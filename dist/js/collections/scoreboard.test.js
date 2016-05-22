define(function(require) {
  var Backbone = require('backbone');
  var ScoreBoard = require('collections/scoreboard');
  QUnit.module('collections/scoreboard');

  var rand = function(min, max) {
    return Math.floor(Math.random() * (max - min + 1) + min);
  };

  QUnit.test('Сортировка моделей в коллекции ScoreBoard', function() {
    var collection = new ScoreBoard();
    var rangeMax = 10000;
    var countTests = 50;

    for(var i = 0; i < countTests; i++) {
      collection.add({
        name: 'Player #' + (i + 1),
        score: rand(-rangeMax, rangeMax)
      });
    }

    var last = rangeMax + 1;
    _.each(collection.toJSON(), function(item) {
      QUnit.ok(item.score <= last, item.score.toString() + ' <= ' + last);
      last = item.score;
    });
  });
});
