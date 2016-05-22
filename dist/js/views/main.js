define(function(require) {
  var Backbone = require('backbone');
  var template = require('templates/main');

  var MainView = Backbone.View.extend({
    initialize: function() {
      this.template = template;

      this.links = {
        game: {
          url: '#game',
          modifier: 'play',
          text: 'Играть'
        },
        scoreboard: {
          url: '#scoreboard',
          modifier: 'records',
          text: 'Рекорды'
        },
        rules: {
          url: '#rules',
          modifier: 'rules',
          text: 'Правила'
        }
      };
    },

    render: function() {
      var html = this.template({
        links: _.map(this.links, function(link, key) {
          return _.extend(link, { key: key });
        })
      });
      this.$el.html(html);
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

  return MainView;
});
