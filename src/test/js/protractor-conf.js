exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',
    chromeOnly: true,
    directConnect: true,
    specs: [
        'e2e/*.js'
    ],
    baseUrl: 'http://localhost:8088/'
};
