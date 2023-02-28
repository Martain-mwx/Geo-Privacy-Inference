import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mwx.boot.pojo.RT;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: mwx
 * @Description: TODO
 * @Date: 2022/05/07/22:39
 */

public class test {
    //构造一个post请求，把List<RT> 发送到python server上

    /**
     *
     */
    @Test
    public  void sendPost() {
        List<RT> rts = new ArrayList<>();
        //先搜端州，后搜海珠（2组）
        double[] list = new double[]{
                -1,
                3.9883169, 6.72977, 6.899557, 7.4926608, 7.6314683, 8.0102025, 6.6571816, 6.0281974, 7.594942, 4.471192, 5.4784523,
                6.1846398, 3.7206713, 1.7927597, 2.2022336, 3.8292651, 4.119921, 7.9578067, 2.6754837, 2.987338, 6.4451916, 3.6658327, 4.2144682,
                4.0322913, 4.3435723, 4.0303482, 4.9570829, 4.6861712, 7.6059578, 7.7287129, 8.1133328, 10.4080148, 11.4915358, 8.5086296,
                7.1436349, 7.4046139, 7.3876764, 7.2157912, 7.9849108, 8.8940514, 10.9013329, 10.297291, 10.7222286,
                -1,
                10.9211086, 9.4626033, 12.1894211, 11.4402856, 9.903968, 5.8618276, 7.6770151, 8.0727021, 5.2529161, 6.7532494,
                9.2843742, 10.6074266, 8.8065566, 4.3965203, 3.5576557, 5.8654317, 8.9738795, 5.7745047,5.292562, 7.5739213,
                5.5002442, 5.9281608, 9.26389, 11.1461971, 13.1713742, 14.87314, 12.5930669, 12.2236349, 12.3987649, 12.660166, 6.9167077,
                6.452114, 8.7790983, 9.9489734, 4.5597079, 5.664369, 6.1101972, 8.065562, 8.7494734, 9.2131453, 6.516916, 5.6672651, 8.1213794,
                8.7652651, 8.5005826, 8.1957026, 8.9116972, 8.8794786, 8.8013378, 8.5286296, 8.5346607, 8.5511921
        };

        //在按enter键过快，有可能会出现此情况
        double[] list1 = new double[]{3.9883169, 6.72977,
                -1,
                3.9883169, 6.72977, 6.899557, 7.4926608, 7.6314683, 8.0102025, 6.6571816, 6.0281974, 7.594942, 4.471192, 5.4784523,
                6.1846398, 3.7206713, 1.7927597, 2.2022336, 3.8292651, 4.119921, 7.9578067, 2.6754837, 2.987338, 6.4451916, 3.6658327, 4.2144682,
                4.0322913, 4.3435723, 4.0303482, 4.9570829, 4.6861712, 7.6059578, 7.7287129, 8.1133328, 10.4080148, 11.4915358, 8.5086296,
                7.1436349, 7.4046139, 7.3876764, 7.2157912, 7.9849108, 8.8940514, 10.9013329, 10.297291, 10.7222286,
                -1,-1,
                10.9211086, 9.4626033, 12.1894211, 11.4402856, 9.903968, 5.8618276, 7.6770151, 8.0727021, 5.2529161, 6.7532494,
                9.2843742, 10.6074266, 8.8065566, 4.3965203, 3.5576557, 5.8654317, 8.9738795, 5.7745047,5.292562, 7.5739213,
                5.5002442, 5.9281608, 9.26389, 11.1461971, 13.1713742, 14.87314, 12.5930669, 12.2236349, 12.3987649, 12.660166, 6.9167077,
                6.452114, 8.7790983, 9.9489734, 4.5597079, 5.664369, 6.1101972, 8.065562, 8.7494734, 9.2131453, 6.516916, 5.6672651, 8.1213794,
                8.7652651, 8.5005826, 8.1957026, 8.9116972, 8.8794786, 8.8013378, 8.5286296, 8.5346607, 8.5511921
        };

        long a = 1663834231123L;
        for (int i = 0; i < list1.length; i++) {
            rts.add(new RT(a,list1[i]));
        }

        //long b = 1663834231290L;
        //rts.add(new RT(a,7.8349733));
        //rts.add(new RT(b,7.858504399999999));

        String url = "http://127.0.0.1:5000/rtlist";

        //参数对应想要保存的实体类
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(rts));
        System.out.println("发送post请求");
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        System.out.println(jsonArray);
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            //设置通用的请求属性
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept","*/*");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //1. 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            //请求参数
            out.print(jsonArray.toString());
            //flush输出流的缓冲
            out.flush();
            if (conn.getResponseCode() == 200) {
                in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                result = in.readLine();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


    }

}
