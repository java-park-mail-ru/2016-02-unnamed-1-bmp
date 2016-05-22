define(function(require) {
  var Backbone = require('backbone');
  var _ = require('underscore');

  var GameUser = Backbone.Model.extend({
    initialize: function(name, field) {
      this.set('name', name);
      this.field = field;
    },

    getName: function() {
      return this.get('name');
    },

    getField: function() {
      return this.field;
    }
  });

  return GameUser;
});
