describe('Order App', function() {

    it('should redirect index.html to index.html#/login', function () {
        browser.get('/index.html');
        browser.getLocationAbsUrl().then(function (url) {
            expect(url).toEqual('/login');
        });
    });
});