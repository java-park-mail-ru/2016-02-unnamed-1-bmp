define(function(require) {
  var Backbone = require('backbone');
  var _ = require('underscore');
  var GameFieldShootResult = require('game/game-field-shoot-result');
  var GameFieldShip = require('game/game-field-ship');

  var GameField = Backbone.Model.extend({
    initialize: function(props) {
      this.props = props;
      this.ships = [];
      this.shoots = [];
    },

    getProperties: function() {
      return this.props;
    },

    getShips: function() {
      return this.ships;
    },

    addShip: function(ship) {
      if(this.isValidShip(ship)) {
        this.ships.push(ship);
        return true;
      }
      return false;
    },

    clearShips: function() {
      this.ships = [];
    },

    removeShip: function(x, y) {
      for(var i = 0; i < this.ships.length; i++) {
        if(this.ships[i].getX() == x && this.ships[i].getY() == y) {
          this.ships.splice(i, 1);
          return true;
        }
      }
      return false;
    },

    rotateShip: function(x, y) {
      var key = -1;
      for(var i = 0; i < this.ships.length; i++) {
        if(this.ships[i].getX() == x && this.ships[i].getY() == y) {
          key = i;
          break;
        }
      }

      if(key > -1) {
        var ship = this.ships[key];

        this.removeShip(ship.getX(), ship.getY());
        var newShip = new GameFieldShip(ship.getX(), ship.getY(), ship.getLength(), !ship.isVertical());

        if(this.addShip(newShip)) {
          return true;
        }

        this.addShip(ship);
      }
      return false;
    },

    isValidShip: function(ship) {
      return ship.isValidForGameFieldProperties(this.props)
              && this.countShips(ship.getLength()) < this.props.getShips(ship.getLength())
              && _.filter(this.ships, function(curShip) {
                return curShip.intersects(ship);
              }).length === 0;
    },

    countShips: function(length) {
      return _.filter(this.ships, function(ship) {
        return ship.getLength() == length;
      }).length;
    },

    shoot: function(x, y) {
      for(var i = 0; i < this.ships.length; i++) {
        var ship = this.ships[i];

        try {
          if(ship.shoot(x, y)) {
            var state = ship.isKilled() ? GameFieldShootResult.STATE_KILLED : GameFieldShootResult.STATE_WOUND;
            var result = new GameFieldShootResult(x, y, state, ship);
            this.shoots.push(result);
            return result;
          }
        }
        catch(e) {
          var result = new GameFieldShootResult(x, y, GameFieldShootResult.STATE_ALREADY, ship);
          this.shoots.push(result);
          return result;
        }
      }

      var result = new GameFieldShootResult(x, y, GameFieldShootResult.STATE_MISS);
      this.shoots.push(result);
      return result;
    },

    getShoots: function() {
      return this.shoots;
    },

    isKilled: function() {
      return _.filter(this.ships, function(ship) {
        return !ship.isKilled();
      }).length === 0;
    },

    isValid: function() {
      for (var i = 1; i <= this.props.getMaxDeck(); i++) {
        if (this.props.getShips(i) != this.countShips(i)) {
          return false;
        }
      }
      return true;
    }
  });

  GameField.generateRandomField = function(props) {
    var maxDeck = props.getMaxDeck();
    var size = props.getSize();

    var field = new GameField(props);
    var busyCells = {};

    var cellKey = function(x, y) {
      return String(x) + '-' + String(y);
    };

    for(var decks = maxDeck; decks > 0; decks--) {
      var maxShips = props.getShips(decks);
      for(var ships = 0; ships < maxShips; ships++) {
        while(true) {
          var x = Math.floor(Math.random() * size) + 1;
          var y = Math.floor(Math.random() * size) + 1;

          if(busyCells[cellKey(x, y)] !== undefined) {
            continue;
          }

          var isVertical = Math.random() > 0.49;

          var ship;
          if(field.addShip(ship = new GameFieldShip(x, y, decks, isVertical))
            || field.addShip(ship = new GameFieldShip(x, y, decks, !isVertical))) {
            _.each(_.union(ship.getDecks(), ship.getNearDecks(props)), function(deck) {
              busyCells[cellKey(deck.getX(), deck.getY())] = true;
            });
            break;
          }
        }
      }
    }

    return field;
  };

  return GameField;
});

