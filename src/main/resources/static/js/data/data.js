angular.module('data', ['ngResource']).factory(
    'data',

    ['$resource', function($resource) {

        var items={};
        var groups={};

        var data = {

            test : 'test',

            getItems : function() {
                if (Object.keys(items).length === 0)
                {
                    var Items = $resource('orders/items');
                    items = Items.query();
                    console.log('items loaded');
                }
                return items;
            },

            getGroups : function() {
                if (Object.keys(groups).length === 0)
                {
                    var Groups = $resource('orders/item_groups');
                    groups = Groups.query();
                    console.log('groups loaded');
                }
                return groups;
            },

            getItemInfo : function() {
                console.log('some itemInfo loaded');
                return $resource('/orders/items/:itemId/info');
            }

            };

        return data;

    }]);
