define(function(require) {
  var Backbone = require('backbone');

  var Score = Backbone.Model.extend({
    defaults: {
      'name': '',
      'score': 0
    },
    initialize: function() {
    }
  });

  return Score;
});
