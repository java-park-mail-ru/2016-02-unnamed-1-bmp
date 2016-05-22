define(function(require) {
  var $ = require('jquery');
  var Backbone = require('backbone');

  var Session = Backbone.Model.extend({
    defaults: {
      auth: false
    },

    url: '/api/session',

    initialize: function() {
    },

    validate: function(attrs, options) {
      var errors = [];

      _.each(['login', 'password'], function(v) {
        if(attrs[v] === undefined) {
          attrs[v] =  '';
        }
        attrs[v] = $.trim(attrs[v]);
      });

      if(attrs.login === '') {
        errors.push({
          field: 'login',
          error: 'Логин не указан'
        });
      }

      if(attrs.password.length == 0) {
        errors.push({
          field: 'password',
          error: 'Пароль не указан'
        });
      }

      if(errors.length) {
        return errors;
      }
    },

    check: function() {
      this.set('id', null);
      this.fetch({
        success: (function(obj, result) {
          this.set('auth', true);
          this.trigger('auth', {
            result: true,
            id: result.id
          });
        }).bind(this),
        error: (function(obj, result) {
          this.set('auth', false);
        }).bind(this)
      });
    },

    isAuthorized: function() {
      return !!this.get('auth');
    },

    tryLogin: function(login, password) {
      if(!this.isNew()) {
        return;
      }

      this.save({
        login: login,
        password: password
      }, {
        success: (function(obj, result) {
          this.set('auth', true);
          this.trigger('auth', {
            result: true,
            id: result.id
          });
        }).bind(this),
        error: (function(obj, result) {
          this.set('auth', false);
          this.trigger('auth', {
            result: false,
            error: result.responseJSON
          });
        }).bind(this)
      });
    },

    logout: function() {
      this.destroy({
        success: (function(data) {
          this.set('auth', false);
          this.trigger('logout');
        }).bind(this)
      });
    }
  });

  return Session;
});

