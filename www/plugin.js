
var exec = require('cordova/exec');

var PLUGIN_NAME = 'BubblePlugin';

var BubblePlugin = {
  echo: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'echo', [phrase]);
  },
  initializeBubble: function(cb) {
    exec(cb, null, PLUGIN_NAME, 'initializeBubble', []);
  },
  onClick: function(cb) {
    exec(cb, null, PLUGIN_NAME, 'onClick', []);
  },
  addBubble: function(cb) {
    exec(cb, null, PLUGIN_NAME, 'addBubble', []);
  },
  removeBubble: function(cb) {
    exec(cb, null, PLUGIN_NAME, 'removeBubble', []);
  },
  updateNotificacao: function(string,cb) {
    exec(cb, null, PLUGIN_NAME, 'updateNotificacao', [string]);
  },
  getDate: function(cb) {
    exec(cb, null, PLUGIN_NAME, 'getDate', []);
  }
};

module.exports = BubblePlugin;
