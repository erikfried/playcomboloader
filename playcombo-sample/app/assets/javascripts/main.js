var YUI_config = {
    filter: 'min',
    groups : {
        vk : {
            combine : true,
            //base : '/assets/', //used when combine == false
            comboBase : '/yui-combo/1?',
            root : 'javascripts',
            modules: {
                'moduleA': {
                    path: '/moduleA.js'
                },
                'moduleB': {
                    path: '/moduleB.js',
                    requires: ['moduleB-1'] //Transitive dependency
                },
                'moduleB-1': {
                    path: '/moduleB-1.js',
                    requires: []
                }
            }
        }
    }
};

YUI().use('moduleA', 'moduleB', function (Y) {
        Y.log("Callback", "INFO", "main.js:26");
});