// 创建一个Panel
chrome.devtools.panels.create(
  'ChromeDevToolsExtensionExample',
  // iconPath
  null,
  // pagePath
  'panel.html'
);

const log = (...args) => chrome.devtools.inspectedWindow.eval(
  'console.log(' + JSON.stringify(args) +')'
  );


chrome.devtools.network.onRequestFinished.addListener((result) =>{
    //log(result.response);
});

