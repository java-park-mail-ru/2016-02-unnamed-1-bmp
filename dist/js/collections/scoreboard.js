define(function(require) {
  var Backbone = require('backbone');
  var model = require('models/score');

  var ScoreBoard = Backbone.Collection.extend({
    model: model,
    url: '/api/scoreboard',

    comparator: function(item) {
      return -item.get('score');
    },

    getTop: function() {
      return this.fetch();
    }
  });

  return ScoreBoard;
});
