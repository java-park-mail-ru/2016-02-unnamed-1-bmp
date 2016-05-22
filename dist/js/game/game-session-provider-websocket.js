define(function(require) {
  var Backbone = require('backbone');
  var _ = require('underscore');

  var GameSessionProviderWebSocket = Backbone.Model.extend({
    initialize: function(props) {
      this.props = props;
    },

    connect: function() {
      try {
        this.ws = new WebSocket('ws://' + window.location.host + '/gameplay');
        this.ws.onopen = this.onOpen.bind(this);
        this.ws.onclose = this.onClose.bind(this);
        this.ws.onmessage = this.onMessage.bind(this);
      }
      catch(e) {
        this.trigger('connection', {
          open: false
        });
      }
    },

    disconnect: function() {
      if(this.isOpen()) {
        this.ws.close();
      }
    },

    isOpen: function() {
      return this.ws && this.ws.readyState == WebSocket.OPEN;
    },

    isClosed: function() {
      return !this.ws || this.ws.readyState == WebSocket.CLOSED;
    },

    onOpen: function() {
      this.trigger('connection', {
        open: true
      });
    },

    onClose: function() {
      this.trigger('connection', {
        open: false
      });
    },

    onMessage: function(e) {
      var data = JSON.parse(e.data);

      switch(data.type) {
          case 'game_init':
              this.trigger('game_init', { success: data.ok });
              break;
          case 'game_start':
              var info = { opponentName: data.opponentName };
              if(data.id !== undefined) {
                info.id = data.id;
              }
              this.trigger('game_start', info);
              break;
          case 'game_turn':
              this.trigger('game_turn', { isMine: data.ok });
              break;
          case 'game_over':
              this.trigger('game_over', { win: data.ok, score: data.score });
              break;
          case 'shoot_result':
              var info = {
                state: data.status,
                x: data.x,
                y: data.y
              };
              if(data.status == 'killed') {
                info.ship = [data.startX, data.startY, data.length, data.isVertical];
              }
              this.trigger('shoot_result', info);
              break;
          case 'error':
              this.trigger('error', { text: data.error });
              break;
          case 'game_status':
              var info = {
                exists: data.ok,
                started: !!data.started,
                id: data.id,
                ships: data.ships,
                shoots: data.shoots,
                opponentName: data.opponentName,
                opponentShips: data.opponentShips,
                opponentShoots: data.opponentShoots
              };
              this.trigger('game_status', info);
              break;
          case 'opponent_online':
              this.trigger('opponent_online', { isOnline: data.ok });
              break;
          case 'game_too_long':
              this.trigger('game_too_long');
              break;
      };
    },

    send: function(action, data) {
      if(this.isOpen()) {
        data = data || {};
        this.ws.send(
          JSON.stringify(_.extend(data, { action: action }))
        );
        return true;
      }
      return false;
    },

    requestStatus: function() {
      this.send('getGameStatus');
    },

    requestInit: function(ships, mode, id) {
      var info = {
        mode: mode,
        ships: _.map(ships, function(ship) {
          if(!_.isArray(ship)) {
            return [ship.getX(), ship.getY(), ship.getLength(), ship.isVertical()];
          }
          return ship;
        })
      };
      if(id) { info.id = id; }

      this.send('initNewGame', info);
    },

    requestShoot: function(x, y) {
      this.send('shoot', {
        x: x,
        y: y
      });
    },

    requestGiveUp: function() {
      this.send('giveUp');
    }
  });

  GameSessionProviderWebSocket.getModes = function() {
    return {
      bot: {
        text: 'Бот онлайн',
        desciption: ''
      },
      random: {
        text: 'Случайный',
        description: ''
      },
      friend: {
        text: 'С другом',
        description: ''
      }
    };
  };

  return GameSessionProviderWebSocket;
});
