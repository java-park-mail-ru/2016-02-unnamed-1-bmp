define(function(require) {
  var $ = require('jquery');

  var Drawer = function(elem) {
    if($(elem).length == 0) {
      return;
    }

    this.canvas = $(elem)[0];
    this.context = this.canvas.getContext('2d');

    window.addEventListener('resize', this.onResize.bind(this));
    this.onResize();

    this.requestFrame = window.requestAnimationFrame       ||
                        window.webkitRequestAnimationFrame ||
                        window.mozRequestAnimationFrame    ||
                        window.oRequestAnimationFrame      ||
                        window.msRequestAnimationFrame     ||
                        function(callback) {
                          window.setTimeout(callback, 1000 / 60);
                        };
  };

  Drawer.prototype = {
    clearFrame: function() {
      this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
    },

    getArea: function() {
      return {
        width: this.canvas.width,
        height: this.canvas.height
      };
    },

    onResize: function() {
      this.canvas.width = window.innerWidth;
      this.canvas.height = window.innerHeight;
    },

    setLoopFunction: function(loopFunction) {
      if(typeof loopFunction === 'function') {
        this.loopFunction = loopFunction;
      }
    },

    render: function() {
      this.clearFrame();
      if(typeof this.loopFunction === 'function') {
        this.loopFunction.call(this);
      }
    },

    loop: function() {
      this.render();
      this.requestFrame.call(window, this.loop.bind(this));
    },

    drawDot: function(dot) {
      var dotState = dot.getState();
      this.context.fillStyle = dot.getColor().getRGBa();
      this.context.beginPath();
      this.context.arc(dotState.x, dotState.y, dotState.radius, 0, 2 * Math.PI, true);
      this.context.closePath();
      this.context.fill();
    }
  };

  return Drawer;
});
