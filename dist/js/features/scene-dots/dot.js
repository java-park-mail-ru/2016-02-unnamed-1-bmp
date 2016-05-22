define(function (require) {
  var DotState = require('./dotstate');
  var DotConst = require('./dotconst');
  var Color = require('./color');

  var Dot = function(x, y, drawer) {
    this.drawer = drawer;
    this.state = new DotState({
      x: x,
      y: y,
      radius: DotConst.RADIUS_DEFAULT,
      alpha: DotConst.ALPHA_DEFAULT
    });

    this.speed = DotConst.SPEED_DEFAULT;
    this.static = true;

    this.nextState = this.state.clone();
    this.nextStates = [];
  };

  Dot.prototype = {
    getColor: function() {
      return new Color(255, 255, 255, this.state.alpha);
    },

    getState: function() {
      return this.state;
    },

    __draw: function() {
      this.drawer.drawDot(this);
    },

    __doStep: function(state) {
      var diff = this.state.diff(state);
      var speed = this.speed * diff.distance;

      if(diff.distance > 1) {
        this.state.x -= (diff.x / diff.distance) * speed;
        this.state.y -= (diff.y / diff.distance) * speed;
      }

      this.state.alpha = Math.max(0.1, this.state.alpha - (diff.alpha * DotConst.SPEED_DEFAULT_ALPHA));
      this.state.radius = Math.max(0.1, this.state.radius - (diff.radius * DotConst.SPEED_DEFAULT_RADIUS));

      return !(diff.distance > 1 || Math.abs(diff.alpha) > 0.5 || Math.abs(diff.radius) > 1); // returns if we should go to next state
    },

    // move near dot static position
    __twitch: function() {
      this.state.x -= Math.sin(Math.random() * 3.1418);
      this.state.y -= Math.sin(Math.random() * 3.1418);
    },

    // move randomly
    __random: function() {
      this.queueState(
        new DotState({
          x: this.state.x + (Math.random() * 50) - 25,
          y: this.state.y + (Math.random() * 50) - 25
        })
      );
    },

    __recompute: function() {
      if (this.__doStep(this.nextState)) {
        var nextState = this.nextStates.shift();

        if(nextState) {
          this.nextState.set(nextState);
        }
        else if(this.static) {
          this.__twitch();
        } else {
          this.__random();
        }
      }
    },

    queueState: function(nextState) {
      nextState.defaults(this.state);
      var diff = this.state.diff(nextState);
      if(diff.distance > 1 || Math.abs(diff.alpha) > 0.5 || Math.abs(diff.radius) > 1) {
        this.nextStates.push(nextState);
      }
    },

    render: function() {
      this.__recompute();
      this.__draw();
    }
  };

  return Dot;
});
