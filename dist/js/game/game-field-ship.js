define(function(require) {
  var Backbone = require('backbone');

  var GameFieldShipDeck = require('game/game-field-ship-deck');

  var GameFieldShipException = function(text) {
    this.text = text;
  };

  var GameFieldShip = Backbone.Model.extend({
    initialize: function(x, y, length, isVertical) {
      this.set('x', x);
      this.set('y', y);
      this.set('length', length);
      this.set('isVertical', isVertical);

      this.decks = [];

      for(var i = 0; i < length; i++) {
        this.decks.push(true);
      }
    },

    getX: function() {
      return this.get('x');
    },

    getY: function() {
      return this.get('y');
    },

    getLength: function() {
      return this.get('length');
    },

    isVertical: function() {
      return this.get('isVertical');
    },

    isKilled: function() {
      return this.decks.indexOf(true) < 0;
    },

    containsDeck: function(x, y) {
      if(this.isVertical()) {
        return this.getX() == x
                && y >= this.getY()
                && y < this.getY() + this.getLength();
      }

      return this.getY() == y
              && x >= this.getX()
              && x < this.getX() + this.getLength();
    },

    intersects: function(ship) {
      var minX = this.getX() - 1;
      var minY = this.getY() - 1;
      var lengthX = this.isVertical() ? 3 : this.getLength() + 2;
      var lengthY = this.isVertical() ? this.getLength() + 2 : 3;
      var maxX = minX + lengthX - 1;
      var maxY = minY + lengthY - 1;

      for(var i = 0; i < ship.getLength(); i++) {
        var curX = ship.isVertical() ? ship.getX() : ship.getX() + i;
        var curY = ship.isVertical() ? ship.getY() + i : ship.getY();

        var betweenX = minX <= curX && maxX >= curX;
        var betweenY = minY <= curY && maxY >= curY;

        if (betweenX && betweenY) {
          return true;
        }
      }

      return false;
    },

    shoot: function(x, y) {
      if(this.containsDeck(x, y)) {
        var key = (this.isVertical() ? y - this.getY() : x - this.getX());
        if(this.decks[key] === false) {
          throw new GameFieldShipException('The deck is already shot');
        }

        this.decks[key] = false;

        return true;
      }

      return false;
    },

    isValidForGameFieldProperties: function(properties) {
      return this.get('length') <= properties.getMaxDeck()
          && this.get('x') > 0
          && this.get('x') <= properties.getSize()
          && this.get('y') > 0
          && this.get('y') <= properties.getSize()
          && (this.isVertical() ? this.getY() : this.getX()) + this.get('length') - 1 <= properties.getSize();
    },

    getDecks: function() {
      var result = [];
      for(var i = 0; i < this.getLength(); i++) {
        var curX = this.isVertical() ? this.getX() : this.getX() + i;
        var curY = this.isVertical() ? this.getY() + i : this.getY();
        result.push(new GameFieldShipDeck(curX, curY));
      }
      return result;
    },

    getNearDecks: function(properties) {
      var result = [];

      if(this.isVertical()) {
        for(var y = this.getY() - 1; y <= this.getY() + this.getLength(); y++) {
          if(y > 0 && y <= properties.getSize()) {
            if(this.getX() > 1) {
              result.push(new GameFieldShipDeck(this.getX() - 1, y));
            }
            if(this.getX() < properties.getSize()) {
              result.push(new GameFieldShipDeck(this.getX() + 1, y));
            }
            if(y == this.getY() - 1 || y == this.getY() + this.getLength()) {
              result.push(new GameFieldShipDeck(this.getX(), y));
            }
          }
        }
      } else {
        for(var x = this.getX() - 1; x <= this.getX() + this.getLength(); x++) {
          if(x > 0 && x <= properties.getSize()) {
            if(this.getY() > 1) {
              result.push(new GameFieldShipDeck(x, this.getY() - 1));
            }
            if(this.getY() < properties.getSize()) {
              result.push(new GameFieldShipDeck(x, this.getY() + 1));
            }
            if(x == this.getX() - 1 || x == this.getX() + this.getLength()) {
              result.push(new GameFieldShipDeck(x, this.getY()));
            }
          }
        }
      }
      return result;
    }
  });

  return GameFieldShip;
});

