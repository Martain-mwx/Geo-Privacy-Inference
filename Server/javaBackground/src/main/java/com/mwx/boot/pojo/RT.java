package com.mwx.boot.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable; // 仅是简单的标识一个类的对象可序列化 （如想用套接字在网络上传送对象的时候）

@Data  //提供类的get、set、equals、hashCode、toString等方法
@AllArgsConstructor //类的全参构造
@NoArgsConstructor //类的无参构造
public class RT implements Serializable {

    private long epochSecond;
    private double renderTime;
    //版本号，在反序列化期间用于验证序列化对象的发送方和接收方是否为该对象加载了与序列化兼容的类。
    private static final long serialVersionUID = -5240152692961888097L;

}
