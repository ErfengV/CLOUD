package cloud.demo.util;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @program: demo
 * @description:
 * @author: ErFeng_V
 * @create: 2021-06-08 19:29
 */
public class FileUtil {
    public static String getFileName() {
        Date d=new Date();
        DateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(d)+".zip";
    }

    public static ResponseEntity<byte[]> downLoadDirectory(byte[] bs, String fileName) {
        //http的响应协议
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.add("Cache-Control","no-cache,no-store,must-revalidate");
        //文件的下载方式
        httpHeaders.add("Content-Disposition",String.format("attachment; filename=\"%s\"",fileName));
        httpHeaders.add("Pragma","no-cache");
        httpHeaders.add("Expires","0");
        httpHeaders.add("Content-Language","UTF-8");
        return ResponseEntity.ok().headers(httpHeaders).contentLength(bs.length)
                .contentType(MediaType.parseMediaType("application/octet-stream")).body(bs);
    }

    public static ResponseEntity<InputStreamResource> downLoadFile(InputStream in, String fileName) {
        //http的响应协议
        try {
            byte[] testBytes=new byte[in.available()];

        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.add("Cache-Control","no-cache,no-store,must-revalidate");
        //文件的下载方式
        httpHeaders.add("Content-Disposition",String.format("attachment; filename=\"%s\"",fileName));
        httpHeaders.add("Pragma","no-cache");
        httpHeaders.add("Expires","0");
        httpHeaders.add("Content-Language","UTF-8");
        return ResponseEntity.ok().headers(httpHeaders).contentLength(testBytes.length)
                .contentType(MediaType.parseMediaType("application/octet-stream")).body(new InputStreamResource(in));
        } catch (IOException e) {
        e.printStackTrace();
        }
        return null;
    }
}
