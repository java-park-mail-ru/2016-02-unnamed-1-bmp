define(function(require) {
  var Backbone = require('backbone');
  var _ = require('underscore');

  var cache = require('cache');
  var viewManager = require('views/manager');
  var GameView = require('views/game');
  var loader = require('loader');

  var alertify = require('alertify');

  var GameField = require('game/game-field');
  var GameFieldShip = require('game/game-field-ship');
  var GameSessionProvider = require('game/game-session-provider');

  var template = require('templates/game-start');
  var fieldTemplate = require('templates/game-field');

  var GameStartView = Backbone.View.extend({
    initialize: function() {
      this.template = template;
      this.fieldTemplate = fieldTemplate;
    },

    events: {
      'click .js-ship': 'onSelectShip',
      'mouseover .game-field__cell': 'onMouseOverCell',
      'mouseout .game-field__cell': 'onMouseOutCell',
      'click .game-field__cell': 'onClickCell',
      'click .js-button-ready': 'onClickReady',
      'click .js-button-clear': 'onClickClear',
      'click .js-ship-rotate': 'onRotateShip',
      'click .js-mode': 'onSelectMode'
    },

    setProps: function(props) {
      this.props = props;
      this.field = new GameField(props);
    },

    render: function() {
      var ships = [];
      for(var i = 1; i <= this.props.getMaxDeck(); i++) {
        ships.push({ decks: i, count: this.props.getShips(i) });
      }

      var html = template({
        field: this.fieldTemplate({
          size: this.props.getSize()
        }),
        ships: ships,
        modes: GameSessionProvider.getModes()
      });
      this.$el.html(html);

      this.$el.find('.js-mode').first().click();

      this.$buttonReady = this.$el.find('.js-button-ready');

      this.resetFromCache();
    },

    resetFromCache: function() {
      var ships = cache.get('game-start-ships');
      if(ships) {
        _.each(ships, function(ship) {
          var $ship = $('.game-field-creator .game-field__ship_' + ship[2] + ':not(.game-field__ship_left)').first().removeClass('js-ship');
          var clone = $ship.clone().addClass('js-ship-rotate game-field__ship_field').toggleClass('game-field__ship_vertical', ship[3]);
          $ship.addClass('game-field__ship_left');
          var $cell = $('.game-field__cell[data-x=' + ship[0] + '][data-y=' + ship[1] + ']');
          $cell.append(clone);
          this.field.addShip(new GameFieldShip(ship[0], ship[1], ship[2], ship[3]));
        }.bind(this));
        if(this.field.isValid()) {
          this.$buttonReady.prop('disabled', false);
        }
      }
    },

    resetCache: function() {
      cache.remove('game-start-ships');
    },

    saveToCache: function() {
      var ships = _.map(this.field.getShips(), function(ship) {
        return [ship.getX(), ship.getY(), ship.getLength(), ship.isVertical()];
      });
      cache.set('game-start-ships', ships);
    },

    show: function(loader) {
      loader(function(cb) {
        this.trigger('show');
        this.render();
        this.$el.show();
        cb();
      }.bind(this));
    },

    hide: function() {
      this.$el.hide();
    },

    onSelectShip: function(e) {
      var selectedClass = 'game-field__ship_selected';
      var leftClass = 'game-field__ship_left';
      var $ship = $(e.target);

      if($ship.hasClass(leftClass)) {
        return;
      }

      if(this.selectedShip) {
        this.selectedShip.removeClass(selectedClass);
      }

      $ship.toggleClass(selectedClass);
      this.selectedShip = $ship.hasClass(selectedClass) ? $ship : null;
    },

    onMouseOverCell: function(e) {
      var overClass = 'game-field__cell_over';
      var $cell = $(e.target);
      this.$el.find('.game-field__cell[data-x=' + $cell.data('x') + '], .game-field__cell[data-y=' + $cell.data('y') + ']').addClass(overClass);
    },

    onMouseOutCell: function(e) {
      var overClass = 'game-field__cell_over';
      var $cell = $(e.target);
      this.$el.find('.game-field__cell[data-x=' + $cell.data('x') + '], .game-field__cell[data-y=' + $cell.data('y') + ']').removeClass(overClass);
    },

    onClickCell: function(e) {
      var selectedClass = 'game-field__ship_selected';
      var leftClass = 'game-field__ship_left';
      var fieldClass = 'game-field__ship_field';
      var $cell = $(e.target);

      if(!$cell.is('.game-field__cell')) {
        return;
      }

      if(this.selectedShip) {
        var clone = $(this.selectedShip).clone().removeClass('js-ship').addClass('js-ship-rotate');
        clone.removeClass(selectedClass);

        if(this.field.addShip(new GameFieldShip(
          $cell.data('x'), $cell.data('y'), clone.data('decks'), false))
          ) {
            this.$el.find('.game-field__cell[data-x=' + $cell.data('x') + '][data-y=' + $cell.data('y') + ']').append(clone.addClass(fieldClass));
            this.selectedShip.removeClass(selectedClass).addClass(leftClass);
            this.selectedShip = null;
            this.saveToCache();

            if(this.field.isValid()) {
              this.$buttonReady.prop('disabled', false);
            }
          }
      }
    },

    onClickReady: function() {
      if(this.field.isValid()) {
        this.resetCache();

        GameSessionProvider.once('game_init', function(data) {
          if(!data.success) {
            alertify.alert('Ошибка', 'Не удалось создать игру');
            return;
          }
          var gameView = new GameView(data.session);
          viewManager.addView(gameView);
          gameView.show(loader);
        });
        GameSessionProvider.init(this.activeMode.data('provider'), this.activeMode.data('mode'), this.field.getShips());
      }
    },

    onClickClear: function() {
      this.field.clearShips();
      this.$el.find('.game-field .game-field__ship').remove();
      this.$el.find('.game-field-creator__ships .game-field__ship_left').addClass('js-ship').removeClass('game-field__ship_left');
      this.resetCache();
      this.$buttonReady.prop('disabled', true);
    },

    onRotateShip: function(e) {
      var rotateClass = 'game-field__ship_vertical';
      var $ship = $(e.target);
      var $cell = $ship.parent('[data-x]');

      if(this.field.rotateShip($cell.data('x'), $cell.data('y'))) {
        $ship.toggleClass(rotateClass);
        this.saveToCache();
      }
    },

    onSelectMode: function(e) {
      e.preventDefault();
      var $mode = $(e.target);
      this.$el.find('.game-field-creator__mode_active').removeClass('game-field-creator__mode_active');
      $mode.addClass('game-field-creator__mode_active');
      this.activeMode = $mode;
      return false;
    }
  });

  return GameStartView;
});
