define(function (require) {
  var Backbone = require('backbone');
  var Score = require('models/score');

  var Scores = Backbone.Collection.extend({
    model: Score,
    comparator: function (item) {
      return -item.get("score");
    }
  });
  var collection = new Scores([
    {name: "Alex", score: 1000000},
    {name: "Mark", score: 1},
    {name: "Leo", score: 100},
    {name: "Jude", score: 1300},
    {name: "Klara", score: 123443},
    {name: "Murmur", score: 10430},
    {name: "Smurfik", score: 3535},
    {name: "Gremlin", score: 5432},
    {name: "Xrushka", score: 224423},
    {name: "Enrique", score: 12}
  ]);
  return collection;
});