define(function(require) {
  var Backbone = require('backbone');
  var _ = require('underscore');

  var template = require('templates/game');
  var fieldTemplate = require('templates/game-field');

  var GameView = Backbone.View.extend({
    initialize: function(gameSession) {
      this.template = template;
      this.fieldTemplate = fieldTemplate;
      this.gameSession = gameSession;
      // TODO: event subscribe
    },

    render: function() {
      var html = this.template({
        field1: this.fieldTemplate({
          size: 10
        }),
        field2: this.fieldTemplate({
          size: 10
        })
      });

      this.$el.html(html);

      this.$field1 = this.$el.find('.js-field1');
      this.$field2 = this.$el.find('.js-field2');
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
    }
  });

  return GameView;
});

