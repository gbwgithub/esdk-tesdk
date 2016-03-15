package com.huawei.esdk.log4Android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Log4Android {
	
	 private static Log4Android instance = null;
	 
	 private static Context context;
	
	 public static Log4Android getInstance()
	    {
	        if(instance == null)
	            instance = new Log4Android();
	        return instance;
	    }

	    private Log4Android()
	    {
	    }
	    

		public static void setContext(Context context) {
			Log4Android.context = context;
		}

		public static boolean isWIFIConnect()
	    {
	    	if(context==null){
	    		Log.d("Log4Android", "context is null!");
	    		return false;
	    	}
	        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo info = cm.getActiveNetworkInfo();
	        if(info == null)
	            return false;
	        if(info.getType() == 1)
	            return info.isConnected();
	        else
	            return false;
	    }
	    
	

		/**
	     * 初始化
	     * 
	     * @param product 产品名字
	     * @param fileContents 配置文件内容
	     * @param logLevel 对应interface,operate,run 日志级别取值【0,1,2,3】
		 * 默认写了{0,0,0}
	     * @param logPath 日志保存路径
	     * @return
	     */
	    public native int logInit(String product, String fileContents, int[] logLevel, String logPath);
	    
	    /**
	     * 去初始化
	     * 
	     * @param product
	     * @return
	     */
	    public native int logUnInit(String product);
	    
	    /**
	     * 接口日志类接口  informational级别
	     * 
	     * @param product 产品名字
	     * @param interfaceType 接口类型   1北向2南向
	     * @param protocolType 协议类型
	     * @param interfaceName 接口名称
	     * @param sourceAddr 源端设备，客户端API类为空
	     * @param targetAddr 宿端设备，客户端API类为空
	     * @param transactionID 唯一标识接口消息所属事务，不存在时为空
	     * @param reqTime 请求时间
	     * @param respTime 应答时间
	     * @param resultCode 接口返回的结果码
	     * @param params 请求响应参数
	     */
	    public native void logInterfaceInfo(String product, String interfaceType, String protocolType, String interfaceName
	    		, String sourceAddr,String targetAddr,String transactionID, String reqTime
	    		, String respTime,String resultCode, String params);
	    
	    /**
	     * 接口日志类接口  error级别
	     * 
	     * @param product 产品名字
	     * @param interfaceType 接口类型   1北向2南向
	     * @param protocolType 协议类型
	     * @param interfaceName 接口名称
	     * @param sourceAddr 源端设备，客户端API类为空
	     * @param targetAddr 宿端设备，客户端API类为空
	     * @param transactionID 唯一标识接口消息所属事务，不存在时为空
	     * @param reqTime 请求时间
	     * @param respTime 应答时间
	     * @param resultCode 接口返回的结果码
	     * @param params 请求响应参数
	     */
	    public native void logInterfaceError(String product, String interfaceType, String protocolType, String interfaceName
	    		, String sourceAddr,String targetAddr,String transactionID, String reqTime
	    		, String respTime,String resultCode, String params);
	    
	    
	    /**
	     * 操作类日志接口
	     * 
	     * @param product 产品名字
	     * @param moduleName 内部模块名称.暂时分为：login、config、log、version
	     * @param userName 操作用户名
	     * @param clientFlag 操作客户端标识，一般为客户端ip
	     * @param resultCode 操作结果码
	     * @param keyInfo 
	     * @param params
	     */
	    private native void logOperateDebug(String product, String moduleName, String userName, String clientFlag
	    		, String resultCode, String keyInfo, String params);
	    public native void logOperateInfo(String product, String moduleName, String userName, String clientFlag
	    		, String resultCode, String keyInfo, String params);
	    public native void logOperateWarn(String product, String moduleName, String userName, String clientFlag
	    		, String resultCode, String keyInfo, String params);
	    public native void logOperateError(String product, String moduleName, String userName, String clientFlag
	    		, String resultCode, String keyInfo, String params);

	    /**
	     * 运行类日志接口
	     * 
	     * @param product
	     * @param params
	     */
	    public native void logRunDebug(String product, String params);

	    public native void logRunInfo(String product, String params);

	    public native void logRunWarn(String product, String params);

	    public native void logRunError(String product, String params);
	    
	    
	    
	    /**
	     * 设置日志参数和上报策略接口
	     * 
	     * @param period
	     * @param uploadFlag
	     * @param serverPath
	     */
	    public native void setSendLogStrategy(int period, int uploadFlag, String serverPath);
	    
	    /**
	     * 
	     * @param product
	     */
	    public native void initMobileLog(String product);
	    

	    public native void setCallBackMethod();
	    
	    /**
	     * 
	     * @param product 产品名字
	     * @param logSize logSize[0]接口日志大小，logSize[1]操作日志大小，logSize[2]运行日志大小
	     * @param logNum logNum[0]接口日志数量，logNum[1]操作日志数量，logNum[2]运行日志数量
	     * @param serverPath 日志服务IP或域名：端口（如esdk-log.huawei.com:80）
	     * @param logUploadUrl 上传URL（如/esdkom/log/upload）
	     * @return
	     */
	    public native int setLogProperty(String product,int [] logSize
	    		,int [] logNum,String serverPath,String logUploadUrl);
	   
	    
	    /**
	     * 将InputStreamToByte[]
	     * @param is
	     * @return
	     * @throws IOException
	     */
	    public static byte[] InputStreamToByte(InputStream is){
	        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
	        int ch;
	        byte imgdata[] = null;
	        try {
				while ((ch = is.read()) != -1) {
				    bytestream.write(ch);
				}
				imgdata = bytestream.toByteArray();
		        bytestream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        return imgdata;
	    }
	    
	  
}
