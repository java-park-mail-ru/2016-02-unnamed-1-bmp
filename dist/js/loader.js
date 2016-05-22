define(function(require) {
  var loaderView = require('views/loader');

  var loader = function(cb) {
    loaderView.show(function() {
      cb(loaderView.hide.bind(loaderView));
    });
  };

  return loader;
});
