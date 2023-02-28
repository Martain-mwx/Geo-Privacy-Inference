//执行注入inject.js的代码，content_script.js 与页面是线程隔离的 window 对象都不一样 
//只能向页面直接注入inject.js代码，然后在inject.js中实现hook操作
jsPath =  'js/inject1.js';
var temp = document.createElement('script');
temp.setAttribute('type', 'text/javascript');
// 获得的地址类似：chrome-extension://ihcokhadfjfchaeagdoclpnjdiokfakg/js/inject.js
temp.src = chrome.runtime.getURL(jsPath);
document.head.appendChild(temp);


//inject.js与content_script.js之间的通信
window.addEventListener("message",function(e){
    send_data(e.data);
});




var url = "http://localhost:8080/googleTime";

window.onload  = function(){
    //当按下搜索按钮，或在输入框按下enter键则发送一个时间戳标记到后台
    var btn =  document.getElementById('searchbox-searchbutton')
    if(btn){
        btn.onclick = function(){
            send(url)
        }
    }

    var input = document.getElementById("searchboxinput")
    if(input){
        input.addEventListener('keydown',function(event){
            if(event.key == 'Enter'){
                send(url)
            }
        })
    }
}

//向后台发送标记
function send(url){
    let xhr = createXHR();  //实例化XMLHttpRequest 对象
    let time  = Date.now();
    console.log(time +"ms ：搜索按钮被点击"  )
    let data = -1.0
    xhr.open ("GET", url + "?data="+ data + "&timestamp=" + time);  //建立连接
    xhr.setRequestHeader ('Content-type', 'application/json');  //设置为表单方式提交
    xhr.send();  //发送请求
}




//xhr web worker线程向后台发送数据
function createXHR () {
    var XHR = [  //兼容不同浏览器和版本得创建函数数组
        function () { return new XMLHttpRequest () },
        function () { return new ActiveXObject ("Msxml2.XMLHTTP") },
        function () { return new ActiveXObject ("Msxml3.XMLHTTP") },
        function () { return new ActiveXObject ("Microsoft.XMLHTTP") }
    ];
    var xhr = null;
    //尝试调用函数，如果成功则返回XMLHttpRequest对象，否则继续尝试
    for (var i = 0; i < XHR.length; i ++) {
        try {
            xhr = XHR[i]();
        } catch(e) {
            continue  //如果发生异常，则继续下一个函数调用
        }
        break;  //如果成功，则中止循环
    }
    return xhr;  //返回对象实例
}


function send_data(param) {
    let xhr = createXHR();  //实例化XMLHttpRequest 对象
    let url = "http://localhost:8080/googleTime";
    xhr.open ("GET", url+"?data="+param.data+"&timestamp="+param.timestamp);  //建立连接
    xhr.setRequestHeader ('Content-type', 'application/json');  //设置为表单方式提交
    xhr.setRequestHeader ('Access-Control-Allow-Origin', '*');  //设置为表单方式提交
    xhr.send();  //发送请求
}

