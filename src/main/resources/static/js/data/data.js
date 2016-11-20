angular.module('data', ['ngResource']).factory('data',DataFactory);

DataFactory.$inject=['$resource','$translate','$http'];

function DataFactory($resource, $translate, $http) {

    var userSettings={};
    var items={};
    var itemsInfo={};
    var groups={};
    var poses={};
    var title;

    var data = {

        test : 'test',

        getUserSettingsPromise : function() {
            if (Object.keys(userSettings).length === 0)
            {
                return $http.get('users/userSettings/').then(function (response) {
                    userSettings=response.data;
                    console.log('userSettings is loaded');
                });
            }
        },

        getUserSettings : function() {
            this.getUserSettingsPromise();
            return userSettings;
        },
        getItems : function() {
            if (Object.keys(items).length === 0)
            {
                var Items = $resource('orders/items');
                items = Items.query();
                console.log('items are loaded');
            }
            return items;
        },

        getItemsInfo : function() {
            if (Object.keys(itemsInfo).length === 0)
            {
                var ItemsInfo = $resource('orders/items_info');
                itemsInfo = ItemsInfo.query();
                console.log('itemsInfo are loaded');
            }
            return itemsInfo;
        },

        getGroups : function() {
            if (Object.keys(groups).length === 0)
            {
                var Groups = $resource('orders/item_groups');
                groups = Groups.query();
                console.log('groups are loaded');
            }
            return groups;
        },

        getPoses : function() {
            if (Object.keys(poses).length === 0)
            {
                var Posess = $resource('users/userPoses');
                poses = Posess.query();
                console.log('poses are loaded');
            }
            return poses;
        },

        getItemInfo : function() {
            console.log('some itemInfo will be loaded');
            return $resource('orders/items/:itemId/info');
        },

        getTitle: function() { return title; },

        setTitle: function(newTitleId, params) {
            $translate(newTitleId, params).then(function (headline) {
                title = headline;
            }, function (translationId) {
                title = translationId;
            });
        }
    };

    return data;
}
