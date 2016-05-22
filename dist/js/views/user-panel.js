define(function(require) {
  var Backbone = require('backbone');
  var app = require('app');

  var template = require('templates/user-panel');

  var UserPanel = Backbone.View.extend({
    initialize: function() {
      this.template = template;
      this.$el = $('.user-panel');
      this.toShow = false;
    },

    render: function() {
      var authData = app.getAuthData();
      var html = this.template({
        isAuth: authData.isAuth,
        login: authData.user.get('login'),
        score: authData.user.get('score')
      });
      this.$el.html(html);
    },

    show: function() {
      this.toShow = true;
      this.render();
      this.$el.fadeIn(400);
    },

    hide: function() {
      this.toShow = false;
      this.$el.fadeOut(400);
    },

    onChange: function() {
      if(this.toShow) {
        this.$el.fadeOut(100, function() {
          this.render();
          this.show();
        }.bind(this));
      }
    }
  });

  var instance = new UserPanel();
  instance.listenTo(app.getUser(), 'change', instance.onChange.bind(instance));

  return instance;
});
