//inject.js与content_script通信
function send_data(data){
    //val1:传递的信息对象
    //val2:接受信息的窗口源（origin）协议+域名+端口号，如果设置为” * “表示不限制窗口源，向所有窗口源发送。
    var time = Date.now();
	window.postMessage({"data": data,"timestamp":time}, '*');

}


(function() {
    'use strict';
    //存放测量结果
    var stats = { gpu: [] }
    //stats统计更新频率
    var updateCount = 10;
    var count = 0;

    //清空stats中的数据
    var clearStatsData = () => {
        count = 0;
        stats.gpu = []
    }
    //存储查询的对象
    var querySet = {
        //可用查询列表
        availableList: [],
        //正在使用的查询列表
        usingList: []
    }
    //计算数组平均值的函数
    const calcArrayAverage = arr => arr.reduce((accumulator, currentValue) => accumulator + currentValue, 0) / arr.length;

    window.onload = function(){
		//获取gl上下文
		var _canvas = document.getElementsByClassName("MyME0d widget-scene-canvas")[0]
		//webgl
		//var _gl =  _canvas.getContext("webgl")
		//webgl2
		var _gl2 = _canvas.getContext("webgl2")
		console.log(_gl2)
		if(_gl2){
			const ext = _gl2.getExtension('EXT_disjoint_timer_query_webgl2')
			var tmp = window._.NSb
			var _render = tmp.prototype.render
			tmp.prototype.render = function(){
				const disjoint = _gl2.getParameter(ext.GPU_DISJOINT_EXT);
				// 检查 GPU 是否一致
				if (disjoint) {
					//重新测量,并删除所有正在使用的查询
					querySet.usingList.forEach(query => { _gl2.deleteQuery(query) })
					querySet.usingList = []
					clearStatsData()
				} else {
					//获取正在使用的查询
					const usingQuery = querySet.usingList.length ? querySet.usingList[0] : null;
					if (usingQuery) {
						//是否可以获取查询结果
						const available = _gl2.getQueryParameter(usingQuery, _gl2.QUERY_RESULT_AVAILABLE);
						if (available) {
							//获取查询结果(经过了多少纳秒)
							const timeElapsed = _gl2.getQueryParameter(usingQuery, _gl2.QUERY_RESULT);
							// 转换为毫秒并添加到列表
							const t = timeElapsed / (1000 * 1000)
							//console.log(t+" ms")
							stats.gpu.push(t)
							// 将使用过的查询移至可用列表
							querySet.availableList.push(querySet.usingList.shift())
						}
					}
				}
				// 从列表中获取可用查询（如果没有，则创建一个新查询）
				const availableQuery = querySet.availableList.length ? querySet.availableList.shift() : _gl2.createQuery();
				// GPU 执行时间测量开始
				_gl2.beginQuery(ext.TIME_ELAPSED_EXT, availableQuery)
				//Jr函数的调用
				let res = _render.apply(this, arguments);
				// GPU执行时间测量结束
				_gl2.endQuery(ext.TIME_ELAPSED_EXT);
				// 使转到您的查询列表
				querySet.usingList.push(availableQuery)
				count += 1
				if (count === updateCount) {
					//更新信息显示 每取10次求均值再发送回后台
					let avg_gpu_time = calcArrayAverage(stats.gpu)
					console.log(avg_gpu_time + "ms")
					send_data(avg_gpu_time)
					clearStatsData()
				}
				return res;
			}
			}
		
	}
})();