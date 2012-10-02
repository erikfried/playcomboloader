/*global YUI*/
/**

 **/
YUI.add('moduleB', function (Y) {
    "use strict";
    var print = function (text, where) {
        var textNode = document.createElement('p');
        textNode.innerHTML = text;
        document.body.appendChild(textNode);
        console.log(text)
    }
    print("Loaded module B", "content")

    Y.namespace('b').print = print;

}, '0.0.1', {requires:['moduleB-1']});