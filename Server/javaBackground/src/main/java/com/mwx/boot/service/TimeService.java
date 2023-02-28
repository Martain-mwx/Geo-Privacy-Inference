package com.mwx.boot.service;

import com.mwx.boot.pojo.RT;
import org.apache.tomcat.util.net.AprEndpoint;

import java.util.List;

public interface TimeService {

    //处理传递来的时间数据
    void handleRT(RT rt);

    //滑动窗口  训练数据，窗口宽度，滑动步长，起始位置，输出数量
    void rollingWindow(List<RT> train,int width, int step ,int start,int out);

    void sendPost(List<RT> list);

}
