function getCity(url){
    let xhr = new XMLHttpRequest () ; 
    //从中国网站获取根据ip获取定位的接口 
    xhr.open ("GET", url);  
    xhr.send();  
    var msg;
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4 && xhr.status == 200) {
            msg = getIPAddr(xhr.responseText)

            sendMsgToContent(msg, function(response)
            {
                console.log('来自content的回复：'+response);
            });
        }
    }
    return msg;
}


function getIPAddr(res){
	res = res.toString()
	var resText = res.replace("if(window.IPCallBack) {IPCallBack(", "").trim();
    resText = resText.substring(0,resText.length - 3)
    let msg = JSON.parse(resText)
    console.log(msg)
    console.log(JSON.stringify(msg))
	return msg;
}

//popup.js发送msg到contentScript.js
function sendMsgToContent(message,callback){
    chrome.tabs.query({active:true,currentWindow:true},function(tabs){
        chrome.tabs.sendMessage(tabs[0].id, message, function(response)
		{
			if(callback) callback(response);
		});
    })
}

var url = "https://whois.pconline.com.cn/ipJson.jsp";
var btn = document.getElementById("ipLocation")
btn.onclick = function(){
    let msg = getCity(url)
}
