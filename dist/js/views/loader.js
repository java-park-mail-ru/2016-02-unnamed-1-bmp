define(function(require) {
  var Backbone = require('backbone');

  var LoaderView = Backbone.View.extend({
    initialize: function() {
      this.$el = $('.page-loader');
    },

    show: function(cb, duration) {
      this.$el.fadeIn({
        duration: duration || 500,
        complete: cb || function() {}
      });
    },

    hide: function(cb, duration) {
      this.$el.fadeOut({
        duration: duration || 500,
        complete: cb || function() {}
      });
    }
  });

  return new LoaderView();
});
