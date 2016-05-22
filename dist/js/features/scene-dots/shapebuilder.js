define(function(require) {
  var DotState = require('./dotstate');

  var ShapeProcessor = function() {
    this.gap = 13;
    this.fontSize = 500;
    this.fontFamily = 'Avenir, Helvetica Neue, Helvetica, Arial, sans-serif';

    this.canvas = document.createElement('canvas');
    this.context = this.canvas.getContext('2d');

    this.canvas.width = Math.floor(window.innerWidth / this.gap) * this.gap;
    this.canvas.height = Math.floor(window.innerHeight / this.gap) * this.gap;
    this.context.fillStyle = 'black';
    this.context.textBaseline = 'middle';
    this.context.textAlign = 'center';
  };

  ShapeProcessor.prototype = {
    process: function() {
      var pixels = this.context.getImageData(0, 0, this.canvas.width, this.canvas.height).data;
      var dots = [];

      var x = 0,
          y = 0,
          width = 0,
          height = 0;

      var fx = this.canvas.width,
          fy = this.canvas.height;

      for (var point = 0; point < pixels.length; point += (4 * this.gap)) {
        if (pixels[point + 3] > 0) {
          dots.push(
            new DotState({
              x: x,
              y: y
            })
          );
          width = Math.max(x, width);
          height = Math.max(y, height);
          fx = Math.min(x, fx);
          fy = Math.min(y, fy);
        }

        x += this.gap;

        if (x >= this.canvas.width) {
          x = 0;
          y += this.gap;
          point += this.gap * 4 * this.canvas.width;
        }
      }

      return {
        dots: dots,
        width: width + fx,
        height: height + fy
      };
    },

    setFontSize: function(size) {
      this.context.font = 'bold ' + size + 'px ' + this.fontFamily;
    },

    clear: function() {
      this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
    },

    circle: function(d) {
      var r = Math.max(0, d) / 2;
      this.clear();
      this.context.beginPath();
      this.context.arc(r * this.gap, r * this.gap, r * this.gap, 0, 2 * Math.PI, false);
      this.context.fill();
      this.context.closePath();

      return this.process();
    },

    rectangle: function(width, height) {
      var dots = [];
      width *= this.gap;
      height *= this.gap;

      for (var y = 0; y < height; y += this.gap) {
        for (var x = 0; x < width; x += this.gap) {
          dots.push(
            new DotState({
              x: x,
              y: y,
            })
          );
        }
      }

      return {
        dots: dots,
        width: width,
        height: height
      };
    },

    word: function(word) {
      var isNumber = function(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
      };

      this.setFontSize(this.fontSize);
      var fontSize = Math.min(
        this.fontSize,
        (this.canvas.width / this.context.measureText(word).width) * 0.8 * this.fontSize,
        (this.canvas.height / this.fontSize) * (isNumber(word) ? 1 : 0.45) * this.fontSize
      );
      this.setFontSize(fontSize);

      this.clear();
      this.context.fillText(word, this.canvas.width / 2, this.canvas.height / 2);

      return this.process();
    }
  };

  var shapeBuilder = function(method, methodArgs) {
    return function() {
      var processor = new ShapeProcessor();

      if(typeof processor[method] == 'function') {
        return processor[method].apply(processor, methodArgs);
      }

      return null;
    };
  };

  return shapeBuilder;
});
