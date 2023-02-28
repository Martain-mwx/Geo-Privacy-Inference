package com.mwx.boot.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mwx.boot.pojo.RT;
import com.mwx.boot.service.TimeService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class TimeServiceImpl implements TimeService {

    // MapperFacade对象注入
    @Autowired
    private MapperFacade mapperFacade;

    static boolean clickedSequence = false;
    static List<RT> list = new ArrayList<>();

    //计算两个时间戳之差，返回秒数
    public long  getTimeDiff(long t1,long t2){
        return (t2 - t1) / 1000;
    }

    //计算List<RT>中rt的和
    public double calAvg(List<RT> list){
        return list.stream().mapToDouble(RT::getRenderTime).sum();
    }


    @Override
    public void handleRT(RT rt) {
        //根据时间戳进行初步分割
        //判断当前rt的时间戳是否和list中最后一个rt的时间戳之差超过n秒
        double n = 2.8;
        if( !list.isEmpty() && getTimeDiff(list.get(list.size() - 1).getEpochSecond(),rt.getEpochSecond()) > n){
            //需要分割
            //对上一个list的处理 该序列中是否有整体搜索标记，并把整体标记清除
            if(clickedSequence == true){
                //判断上一个序列是否被标记 被标记则发送，并还原标记
                System.out.println("=============上个序列send===================");
                // 发送到python进行下一步处理
                List<RT> newList = mapperFacade.mapAsList(list, RT.class);
                clickedSequence = false;
                list.clear();
                sendPost(newList);
            }else{
                //没有被标记，则不发送该序列到python后台进行处理
                System.out.println("=============上个序列not send===============");
                list.clear();
            }
            //需要if判断当前新元素是否带有标记  即判断rt的renderTime值为-1，做整体标记
            //对当前元素的处理, 加入新元素
            if(rt.getRenderTime() == -1){
                System.out.println("======== clicked =======");
                clickedSequence = true;
            }
        }else{
            //没超过，或者是第一个元素    需要判断当前新元素是否带有标记  即判断rt的renderTime值为-1，做整体标记
            if(rt.getRenderTime() == -1){
                //标志着有点击事件的发生，当前序列被标记
                System.out.println("======== clicked =======");
                clickedSequence = true;
            }
        }
        list.add(rt);
        //System.out.println(rt.getEpochSecond() + " , " + rt.getRenderTime());
        System.out.println(rt.getRenderTime());
    }

    @Override
    public void rollingWindow(List<RT> train, int width, int step, int start, int out) {
        
    }

    /**
     * 向python后台发送初次分割的时间序列
     * @param list
     */
    @Override
    public void sendPost(List<RT> list) {
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(list));
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        //指定IP与端口号
        String url  = "http://127.0.0.1:5000/rtlist";
        try {
            URL realUrl = new URL(url);
            //打开和URL之间的连接
            HttpURLConnection conn =(HttpURLConnection)  realUrl.openConnection();
            //设置通用的请求属性
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept","*/*");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
            // 发送POST请求必须设置如下
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //1. 获取URLConnection对象对应的输出流
            if(jsonArray != null){
                out = new PrintWriter(conn.getOutputStream());
                //请求参数
                out.print(jsonArray.toString());
                //flush输出流的缓冲
                out.flush();
                out.close();
            }
            if (conn.getResponseCode() == 200) {
                in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                result = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
