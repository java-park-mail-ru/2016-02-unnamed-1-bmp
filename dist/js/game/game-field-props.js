define(function(require) {
  var data = {
    "10x10": {
      size: 10,
      maxdeck: 4,
      ship4: 1,
      ship3: 2,
      ship2: 3,
      ship1: 4
    }
  };

  var gameFieldPropertiesCreator = function(gameMode) {
    var fieldProps = {
      getSize: function() {
        return data[gameMode].size;
      },

      getMaxDeck: function() {
        return data[gameMode].maxdeck;
      },

      getShips: function(decks) {
        var count = data[gameMode][('ship' + decks)];
        return count === undefined ? 0 : count;
      }
    };

    return fieldProps;
  };

  var instances = {};

  var GameFieldProperties = {
    getProperties: function(gameMode) {
      gameMode = gameMode || '10x10';

      if(instances[gameMode] === undefined) {
        instances[gameMode] = gameFieldPropertiesCreator(gameMode);
      }

      return instances[gameMode];
    }
  };

  return GameFieldProperties;
});
