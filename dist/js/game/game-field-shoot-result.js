define(function(require) {
  var Backbone = require('backbone');

  var GameFieldShootResult = Backbone.Model.extend({
    initialize: function(x, y, state, ship) {
      this.set('x', x);
      this.set('y', y);
      this.set('state', state);

      if(ship !== undefined) {
        this.set('ship', ship);
      }
    },

    getX: function() {
      return this.get('x');
    },

    getY: function() {
      return this.get('y');
    },

    getShip: function() {
      return this.get('ship');
    },

    isMiss: function() {
      return this.get('state') == GameFieldShootResult.STATE_MISS;
    },

    isAlready: function() {
      return this.get('state') == GameFieldShootResult.STATE_ALREADY;
    },

    isWound: function() {
      return this.get('state') == GameFieldShootResult.STATE_WOUND;
    },

    isKilled: function() {
      return this.get('state') == GameFieldShootResult.STATE_KILLED;
    }
  });

  GameFieldShootResult.STATE_MISS = 'miss';
  GameFieldShootResult.STATE_ALREADY = 'already';
  GameFieldShootResult.STATE_WOUND = 'wound';
  GameFieldShootResult.STATE_KILLED = 'killed';

  return GameFieldShootResult;
});
