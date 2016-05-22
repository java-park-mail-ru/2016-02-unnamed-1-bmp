define(function(require) {
  var Color = function(r, g, b, alpha) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.alpha = alpha;
  };

  Color.prototype = {
    getRGBa: function() {
      return 'rgba(' + [this.r, this.g, this.b, this.alpha].join(',') + ')';
    }
  };

  return Color;
});
