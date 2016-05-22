define(function(require) {
  var Backbone = require('backbone');
  var _ = require('underscore');
  var sinon = require('sinon');

  var manager = require('views/manager');
  var app = require('app');

  var View = Backbone.View.extend({
    show: function() {
      this.trigger('show');
    },
    hide: function() {
      this.trigger('hide');
    }
  });

  var view1 = new View();
  var view2 = new View();
  var view3 = new View();

  var ViewWithAuth = View.extend({
    onAuth: function(data) {
      this.trigger('onAuth', data);
    }
  });

  var view4 = new ViewWithAuth();
  var view5 = new ViewWithAuth();
  var view6 = new ViewWithAuth();

  var ViewEmpty = Backbone.View.extend({
  });

  var view7 = new ViewEmpty();

  var allViews = [view1, view2, view3, view4, view5, view6, view7];

  _.each(allViews, function(view) {
    manager.addView(view);
  });


  QUnit.module('views/manager');

  QUnit.test('Добавление вьюх в менеджер', function() {
    var i = 1;
    _.each(allViews, function(view) {
      QUnit.ok(_.contains(manager.views, view), 'Вьюха #' + i.toString() + ' добавилась');
      i++;
    });

    QUnit.equal(manager.views.length, allViews.length, 'Все вьюхи добавились в менеджер');
  });

  QUnit.test('Отображение/скрытие вьюх', function() {
    var spy1 = sinon.spy();
    view1.on('hide', spy1);
    var spy1Show = sinon.spy();
    view1.on('show', spy1Show);

    var spy2 = sinon.spy();
    view2.on('hide', spy2);
    var spy3 = sinon.spy();
    view3.on('hide', spy3);
    var spy4 = sinon.spy();
    view4.on('hide', spy4);
    var spy5 = sinon.spy();
    view5.on('hide', spy5);
    var spy6 = sinon.spy();
    view6.on('hide', spy6);

    view1.show();

    QUnit.ok(spy1Show.calledOnce, 'Вьюха #1 затриггерила show');
    QUnit.ok(!spy1.called, 'Вьюха #1 не была скрыта (hide)');
    QUnit.ok(spy2.calledOnce, 'Вьюха #2 была скрыта (hide)');
    QUnit.ok(spy3.calledOnce, 'Вьюха #3 была скрыта (hide)');
    QUnit.ok(spy4.calledOnce, 'Вьюха #4 была скрыта (hide)');
    QUnit.ok(spy5.calledOnce, 'Вьюха #5 была скрыта (hide)');
    QUnit.ok(spy6.calledOnce, 'Вьюха #6 была скрыта (hide)');
  });

  QUnit.test('Прокидывание события auth из app', function() {
    var spy4 = sinon.spy();
    view4.on('onAuth', spy4);

    var spy5 = sinon.spy();
    view5.on('onAuth', spy5);

    var spy6 = sinon.spy();
    view6.on('onAuth', spy6);

    app.trigger('auth');

    QUnit.ok(spy4.calledOnce, 'Для вьюхи #4 было прокинуто событие auth');
    QUnit.ok(spy5.calledOnce, 'Для вьюхи #5 было прокинуто событие auth');
    QUnit.ok(spy6.calledOnce, 'Для вьюхи #6 было прокинуто событие auth');
  });
});
