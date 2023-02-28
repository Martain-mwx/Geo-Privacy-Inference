# -*- coding:utf-8 -*-
import baiduHandle
import gaodeHandle
import googleHandle
from flask import Flask, request, jsonify
from flask_cors import *

app = Flask(__name__)
# 解决跨域请求资源被拦截问题
CORS(app, supports_credentials=True, resources=r"/*")
import warnings
warnings.filterwarnings("ignore")


def ndarray2str(list):
    str1 = ""
    for i in list:
        str1 += "," + i[0]
    return str1[1:]


# python 接收post请求 每一个post请求都是List<RT>
@app.route('/BaiduList', methods=['POST', 'GET'])
def resultBaidu():
    print("baiduMap")
    print(request.json)
    res = baiduHandle.handleBaiduRTList(request.json)
    str2 = ndarray2str(res)
    return str2


@app.route('/GaodeList', methods=['POST', 'GET'])
def resultGaode():
    print("gaodeMap")
    print(request.json)
    res = gaodeHandle.handleGaodeRTList(request.json)
    str2 = ndarray2str(res)
    return str2


@app.route('/GoogleList', methods=['POST', 'GET'])
def resultGoogle():
    print("googleMap")
    print(request.json)
    googleHandle.handleGoogleRTList(request.json)
    return jsonify({'status': 'success'})


if __name__ == "__main__":
    # 设置为“0.0.0.0”以使服务器在外部可用
    # host = "0.0.0.0"  port = 5000
    app.run()
