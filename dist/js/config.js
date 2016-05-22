require.config({
  urlArgs: "_=" + (new Date()).getTime(),
  baseUrl: "js",
  paths: {
    jquery: "vendor/jquery",
    underscore: "vendor/underscore",
    backbone: "vendor/backbone",
    alertify: "vendor/alertify.min",
    qunit: "vendor/qunit-1.21.0",
    sinon: "vendor/sinon-1.17.3"
  },
  shim: {
    'backbone': {
      deps: ['underscore', 'jquery'],
      exports: 'Backbone'
    },
    'underscore': {
      exports: '_'
    },
    'alertify': {
      exports: 'alertify'
    }
  }
});
