define(function(require) {
  var Backbone = require('backbone');

  var GameFieldShipDeck = Backbone.Model.extend({
    initialize: function(x, y) {
      this.set('x', x);
      this.set('y', y);
    },

    getX: function() {
      return this.get('x');
    },

    getY: function() {
      return this.get('y');
    },

    isValidForGameFieldProperties: function(properties) {
      return this.get('x') > 0
          && this.get('x') <= properties.getSize()
          && this.get('y') > 0
          && this.get('y') <= properties.getSize();
    }
  });

  return GameFieldShipDeck;
});
