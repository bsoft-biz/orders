angular.module('data', ['ngResource']).factory(
    'data',

    ['$resource', function($resource) {

        var items={};
        var itemsInfo={};
        var groups={};
        var title="Заявки ТХК";

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

            getItemsInfo : function() {
                if (Object.keys(itemsInfo).length === 0)
                {
                    var ItemsInfo = $resource('orders/items_info');
                    itemsInfo = ItemsInfo.query();
                    console.log('itemsInfo loaded');
                }
                return itemsInfo;
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
                return $resource('orders/items/:itemId/info');
            },
            
            getTitle: function() { return title; },
            
            setTitle: function(newTitle) { title = newTitle }

            };

        return data;

    }]);
