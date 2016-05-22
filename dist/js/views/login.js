define(function(require) {
  var Backbone = require('backbone');
  var template = require('templates/login');
  var app = require('app');

  var LoginView = Backbone.View.extend({
    events: {
      'submit .login-form': 'submit'
    },

    expectAuth: false,

    initialize: function() {
      this.template = template;
      this.render();
      this.inputs = {
        'password': this.$el.find('#js-login-form__password'),
        'login': this.$el.find('#js-login-form__login')
      };
      this.errorFields = {
        'password': this.$el.find('.js-login-form__password-error'),
        'login': this.$el.find('.js-login-form__login-error')
      };
      this.button = this.$el.find('.js-login-form__button');
    },

    render: function() {
      var html = this.template();
      this.$el.html(html);
    },

    clear: function() {
      _.each(this.inputs, function(item) {
        item.val('');
      });
    },

    clearErrors: function() {
      _.each(this.errorFields, function(item) {
        item.text('');
      });

      _.each(this.inputs, function(item) {
        item.parent().removeClass('login-form__field_error');
      });
    },

    handleError: function(error) {
      if(this.errorFields[error.field] !== undefined) {
        this.errorFields[error.field].text(error.error);
        this.inputs[error.field].parent().addClass('login-form__field_error');
      }
    },

    submit: function(e) {
      e.preventDefault();

      var uData = {
        login: this.inputs.login.val(),
        password: this.inputs.password.val()
      };

      this.clearErrors();

      var session = app.getSession();
      var errors = session.validate(uData);

      if(errors != undefined && errors.length) {
        _.each(errors, (function(error) {
          this.handleError(error);
        }).bind(this));
      }
      else
      {
        this.button.prop('disabled', true);

        session.once('auth', (function(result) {
          this.button.prop('disabled', false);
          if(!result.result) {
            this.handleError(result.error);
          }
          else {
            this.expectAuth = true;
          }
        }).bind(this));

        session.tryLogin(uData.login, uData.password);
      }
      return false;
    },

    show: function() {
      this.trigger('show');
      this.delegateEvents();
      this.$el.show();
    },

    hide: function() {
      this.$el.hide();
    },

    onAuth: function(result) {
      var router = require('router');
      if(result.isAuth && this.expectAuth) {
        this.expectAuth = false;
        this.clear();
        router.go('');
      }
    }
  });

  return LoginView;
});
