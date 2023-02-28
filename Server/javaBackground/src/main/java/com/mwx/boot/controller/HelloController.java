package com.mwx.boot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mwx.boot.pojo.RT;
import com.mwx.boot.service.impl.TimeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.*;

/**
 * @Author: mwx
 * @Description: TODO
 * @Date: 2022/04/23/11:14
 */

//所有响应直接以json格式返回到浏览器
@RestController
public class HelloController {

    @Autowired
    TimeServiceImpl timeService;

    private static int count = 0;
    private static Pattern IS_NUM = compile("^(-|\\+)?\\d+(\\.\\d+)?$");

    private static  String DateFormat = "yyyy-MM-dd HH:mm:ss SSS";
    SimpleDateFormat sdf = new SimpleDateFormat(DateFormat);


    /**
     * 接收前端发回的数据
     * @param data
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/data")
    public void webglData(@RequestBody String data) throws JsonProcessingException  {
        System.out.println(data+" ms");
    }


    /**
      * baiduMaps
      * @param paramsMap
     */
    @CrossOrigin(origins = {"https://map.baidu.com","https://maps.baidu.com"})
    @RequestMapping(value = "/time",method = RequestMethod.POST)
    public void baiduMapHookData(@RequestBody Map<String, Object> paramsMap){
        //接收到 time series signal 之后处理
        //对传递过来的数据进行线封装，并送到timeService.handleRT进行处理
        double t = Double.parseDouble(paramsMap.get("data").toString());
        long tt = (long) paramsMap.get("timestamp");
        RT rt = new RT(tt,t);
        timeService.handleRT(rt);
    }


    /**
     * auto search
     * 尝试接收脚本 发回的时间序列数据
     * @param map
     */
    @PostMapping(value = "/cannot")
    public void lock(@RequestParam Map<String, Object> map){
        String msg = map.get("data").toString();
        int len = msg.length();
        String text = msg.substring(1,len - 1);
        if("refresh".equals(text)){
            System.out.println("=================refresh==================");
        }else{
            System.out.println("target: "+text);
            System.out.println("=================第"+ count +"次==================");
            count++;
        }
    }


    /**
     * auto search
     * 尝试接收selenium 自动化脚本发回的数据
     * @param map
     */
    @PostMapping(value = "/cannot1")
    public void lock1(@RequestParam Map<String, Object> map){
        String msg = map.get("data").toString();
        if("refresh".equals(msg)){
            System.out.println("=================refresh==================");
        }else{
            System.out.println("target: "+msg);
            System.out.println("=================第"+ count +"次==================");
            count++;
        }
    }

    /**
     * auto search
     * 油猴或selenium 脚本 发送当前搜索的地点
     * @param map
     */
    @PostMapping(value = "/city")
    public void lockcity(@RequestParam Map<String, Object> map){
        String msg = map.get("data").toString();
        int len = msg.length();
        String text = msg.substring(1,len - 1);
        System.out.println("===============target: "+text);
    }

    /**
     * 判断字符串是否为数字
     * @param str
     * @return
     */
    public boolean isNumeric(String str){
        //匹配正数，负数 或小数
        Matcher isNum2 = IS_NUM.matcher(str);
        if(isNum2.matches()){
            return true;
        }else{
            return false;
        }
    }


    /**
     * 接收插件发回的数据                    高德地图
     * @param request
     */
    @CrossOrigin(origins = {"https://ditu.amap.com","https://www.amap.com"})
    @RequestMapping(value = "/gaodeTime")
    public void gaodeMapHookData(HttpServletRequest request){
        if (isNumeric(request.getParameter("data"))){
            double t = Double.parseDouble(request.getParameter("data").toString());
            long tt = Long.parseLong(request.getParameter("timestamp").toString());
            RT rt = new RT(tt,t);
            timeService.handleRT(rt);

        }
    }


    /**
     * 接收插件发回的数据                    谷歌地图
     * @param request
     */
    @CrossOrigin
    @RequestMapping(value = "/googleTime")
    public void googleMapHookData(HttpServletRequest request){
        if (isNumeric(request.getParameter("data"))){
            double t = Double.parseDouble(request.getParameter("data"));
            long tt = Long.parseLong(request.getParameter("timestamp"));
            RT rt = new RT(tt,t);
            timeService.handleRT(rt);
        }

    }

}