define(function(require) {
  var Backbone = require('backbone');
  var _ = require('underscore');

  var app = require('app');

  var GameSessionProviderWebSocket = require('game/game-session-provider-websocket');
  var GameSessionProviderBot = require('game/game-session-provider-bot');

  var GameSession = require('game/game-session');
  var GameUser = require('game/game-user');
  var GameField = require('game/game-field');
  var GameFieldShip = require('game/game-field-ship');
  var GameFieldShipDeck = require('game/game-field-ship-deck');

  var GameSessionProperties = require('game/game-field-props');
  var props = GameSessionProperties.getProperties();

  var GameSessionProvider = Backbone.Model.extend({
    initialize: function(props) {
      this.props = props;
      this.providers = {
        websocket: GameSessionProviderWebSocket,
        bot: GameSessionProviderBot
      };

      this.exists = false;
      this.existingProviders = [];
    },

    getModes: function() {
      var res = [];
      _.each(this.providers, function(provider, key) {
        _.each(provider.getModes(), function(mode, modeKey) {
          res.push(_.extend(mode, {provider: key, mode: modeKey}));
        });
      });
      return res;
    },

    getProps: function() {
      return this.props;
    },

    checkExisting: function(cb) {
      var checked = [];
      var exists = [];

      var checkAll = function() {
        if(checked.length === _.keys(this.providers).length) {
          this.exists = exists.length > 0;
          this.existingProviders = exists;

          if(typeof cb == 'function') {
            this.once('checkedExisting', cb);
          }

          this.trigger('checkedExisting', {
            exists: this.exists,
            existingProviders: this.existingProviders
          });
        }
      }.bind(this);

      _.each(this.providers, function(providerClass, key) {
        var provider = new providerClass(props);

        var onGameStatus = function(data) {
          checked.push(key);
          if(data.exists) {
            exists.push(key);
          }

          provider.disconnect();
          checkAll();
        }.bind(this);

        var onConnected = function(data) {
          if(!data.open) {
            checked.push(key);
            return;
          }

          provider.once('game_status', onGameStatus);
          provider.requestStatus();
        }.bind(this);

        provider.once('connection', onConnected);
        provider.connect();
      }.bind(this));
    },

    init: function(provider, mode, ships, id) {
      var session = new GameSession(this.props);

      var currentField = new GameField(this.props);
      for(var i = 0; i < ships.length; i++) {
        var ship = ships[i];
        if(_.isArray(ship)) {
          var ship = new GameFieldShip(ships[i][0], ships[i][1], ships[i][2], ships[i][3]);
        }
        if(!currentField.addShip(ship)) {
          return false;
        }
      }

      var providerCreator = this.providers[provider];
      if(!providerCreator) {
        return false;
      }
      var provider = new providerCreator();
      provider.connect();
      provider.once('game_init', function(initData) {
        this.trigger('game_init', {
          session: session,
          success: initData.success
        });
      }.bind(this));
      provider.once('connection', function() {
        provider.requestInit(ships, mode, id);
      });

      return true;
    },

    getExisting: function(provider, cb) {
      var providerCreator = this.providers[provider];
      if(!providerCreator) {
        return false;
      }
      var provider = new providerCreator();
      provider.connect();
      provider.once('game_status', function(initData) {
        var session = new GameSession(this.props);
        // TODO: info about existing game

        if(typeof cb == 'function') {
          this.once('game_continue', cb);
        }
        this.trigger('game_continue', {
          session: session
        });
      }.bind(this));
      provider.once('connection', function() {
        provider.requestStatus();
      });

      return true;
    }
  });

  return new GameSessionProvider(props);
});
