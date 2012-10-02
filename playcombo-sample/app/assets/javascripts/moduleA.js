/*global YUI*/
/**

 **/
YUI.add('moduleA', function (Y) {
    "use strict";
    var print = function (text, where) {
        var textNode = document.createElement('p');
        textNode.innerHTML = text;
        document.body.appendChild(textNode);
        console.log(text)
    }
    print("Loaded module A...", "content")

    Y.namespace('a').print = print;

}, '0.0.1', {requires:[/* module dependencies */]});