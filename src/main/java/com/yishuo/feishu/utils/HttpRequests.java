package com.yishuo.feishu.utils;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

import java.io.*;

/**
 * post请求
 **/
public class HttpRequests {

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url      发送请求的 URL
     * @param jsonData 请求参数，请求参数应该是Json格式字符串的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String requests(String url, String jsonData, String bearer) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            HttpClient client = new HttpClient(); // 客户端实例化
            PostMethod postMethod = new PostMethod(url); // 请求方法post，可以将请求路径传入构造参数中

            postMethod.addRequestHeader("Content-type", "application/json; charset=utf-8");
            postMethod.addRequestHeader("Authorization", bearer);
            byte[] requestBytes = jsonData.getBytes("utf-8"); // 将参数转为二进制流

            InputStream inputStream = new ByteArrayInputStream(requestBytes, 0, requestBytes.length);
            // 请求体
            RequestEntity requestEntity = new InputStreamRequestEntity(inputStream, requestBytes.length, "application/json; charset=utf-8");
            postMethod.setRequestEntity(requestEntity); // 将参数放入请求体
            int i = client.executeMethod(postMethod); // 执行方法
            System.out.println("请求状态" + i);

            // 这里因该有判断的，根据请求状态判断请求是否成功，然后根据第三方接口返回的数据格式，解析出我们需要的数据
            byte[] responseBody = postMethod.getResponseBody(); // 得到相应数据
            String s = new String(responseBody);
            result = s;
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }

        // 使用finally块来关闭输出流、输入流
        finally {
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

        return result;
    }
}
