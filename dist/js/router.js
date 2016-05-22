define(function (require) {
  var Backbone = require('backbone');
  var _ = require('underscore');
  var app = require('app');
  var alertify = require('alertify');
  var MainView = require('views/main'),
      ScoreboardView = require('views/scoreboard'),
      GameView = require('views/game'),
      UserView = require('views/user'),
      GameStartView = require('views/game-start');

  var GameSessionProvider = require('game/game-session-provider');

  var viewManager = require('views/manager');

  var mainView = new MainView();
  var scoreboardView = new ScoreboardView();
  var gameView = new GameView();
  var userView = new UserView();
  var gameStartView = new GameStartView();

  _.each([mainView,
         scoreboardView,
         gameView,
         userView,
         gameStartView
  ], function(view) {
           viewManager.addView(view);
  });

  var loader = require('loader');

  var Router = Backbone.Router.extend({
    routes: {
      'scoreboard': 'scoreboardAction',
      'user/:tab': 'userAction',
      'game': 'gameAction',
      'game/start': 'gameStartAction',
      '*default':   'defaultAction',
    },

    go: function(where) {
      return this.navigate(where, { trigger: true });
    },

    goSilent: function(where) {
      return this.navigate(where, { trigger: false });
    },

    defaultAction: function() {
      mainView.show(loader);
    },

    scoreboardAction: function() {
      scoreboardView.show(loader);
    },

    userAction: function(tab) {
      if(app.getAuthData().isAuth) {
        this.go('');
        return;
      }
      userView.show(loader);
      userView.tab(tab);
    },

    gameAction: function() {
      if(!app.getAuthData().isAuth) {
        this.go('user/login');
        return;
      }

      var continueOldGame = function(data) {
        alertify.confirm(
          'Незаконченная игра',
          'У вас есть незаконченная игра. Продолжить ее?',
          function() {
            GameSessionProvider.getExisting(data.existingProviders.pop(), function(data) {
              console.log(data);
            });
          }, function() {});
      }.bind(this);

      loader(function(hider) {
        GameSessionProvider.checkExisting(function(data) {
          if(data.exists) {
            hider(function() {
              continueOldGame(data);
            });
          }
          else {
            this.go('game/start');
          }
        }.bind(this));
      }.bind(this));
    },

    gameStartAction: function() {
      if(!app.getAuthData().isAuth) {
        this.go('user/login');
        return;
      }
      gameStartView.setProps(GameSessionProvider.getProps());
      gameStartView.show(loader);
    }
  });

  return new Router();
});
