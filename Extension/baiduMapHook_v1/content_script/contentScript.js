//执行注入inject.js的代码，content_script.js 与页面是线程隔离的 window 对象都不一样 
//只能向页面直接注入inject.js代码，然后在inject.js中实现hook操作
jsPath =  'js/inject.js';
var temp = document.createElement('script');
temp.setAttribute('type', 'text/javascript');
// 获得的地址类似：chrome-extension://ihcokhadfjfchaeagdoclpnjdiokfakg/js/inject.js
temp.src = chrome.runtime.getURL(jsPath);
document.head.appendChild(temp);

//popup.js与content_script通信
chrome.runtime.onMessage.addListener(function(request, sender, sendResponse){
    console.log(request)
	sendResponse('got your message');
});


//inject.js与content_script.js之间的通信
window.addEventListener("message",function(e){
    send_data(e.data);
});


var url = "http://localhost:8080/time";

window.onload = function () {
    //当按下搜索按钮，或在输入框按下enter键则发送一个时间戳标记到后台
    var btn = document.getElementById('search-button')
    if(btn){
        btn.onclick = function(){
            send(url)
        }
    }
    var input = document.getElementById('sole-input')
    if(input){
        input.addEventListener('keyup',function(event){
            if(event.key == 'Enter'){
                send(url)
            }
        })
    }
}

//向后台发送标记
function send(url){
    let xhr = createXHR();  
    xhr.open ("POST", url);  
    xhr.setRequestHeader ('Content-type', 'application/json');  
    var time  = Date.now();
    console.log(time +" ：搜索按钮被点击"  )
    var data = -1.0;
    xhr.send(JSON.stringify({"data": data, "timestamp": time}));  
}


//xhr web worker线程向后台发送数据
function createXHR () {
    var XHR = [  
        function () { return new XMLHttpRequest () },
        function () { return new ActiveXObject ("Msxml2.XMLHTTP") },
        function () { return new ActiveXObject ("Msxml3.XMLHTTP") },
        function () { return new ActiveXObject ("Microsoft.XMLHTTP") }
    ];
    var xhr = null;
    for (var i = 0; i < XHR.length; i ++) {
        try {
            xhr = XHR[i]();
        } catch(e) {
            continue  
        }
        break;  
    }
    return xhr; 
}


function send_data(param) {
    let xhr = createXHR();  
    let url = "http://localhost:8080/time";
    xhr.open ("POST", url); 
    xhr.setRequestHeader ('Content-type', 'application/json'); 
    //console.log(param)
    xhr.send(JSON.stringify(param)); 
}