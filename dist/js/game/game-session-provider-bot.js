define(function(require) {
  var Backbone = require('backbone');
  var _ = require('underscore');

  var cache = require('cache');

  var getFromCache = function() {
    return cache.get('game-session-provider-bot-status');
  };

  var saveToCache = function(data) {
    cache.set('game-session-provider-bot-status', data);
  };

  var GameSessionProviderBot = Backbone.Model.extend({
    initialize: function(props) {
      this.props = props;
    },

    connect: function() {
      this.trigger('connection', {
        open: true
      });
    },

    disconnect: function() {
      this.trigger('connection', {
        open: false
      });
    },

    requestStatus: function() {
      var data = getFromCache();
      var exists = !!data;
      var info = {
        exists: exists,
        started: exists,
        id: exists ? 0 : undefined,
        ships: exists ? data.ships : undefined,
        shoots: exists ? data.shoots : undefined,
        opponentName: exists ? data.opponentName : undefined,
        opponentShips: exists ? data.opponentShips : undefined,
        opponentShoots: exists ? data.opponentShoots : undefined
      };

      this.trigger('game_status', info);
    },

    requestInit: function(ships, mode, id) {
    },

    requestShoot: function(x, y) {
    },

    requestGiveUp: function() {
    }
  });

  GameSessionProviderBot.getModes = function() {
    return {
      bot: {
        text: 'Бот офлайн',
        description: ''
      }
    };
  };

  return GameSessionProviderBot;
});
