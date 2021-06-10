package cloud.demo.controller;


import cloud.demo.service.HdfsService;
import cloud.demo.util.RedisUtil;
import com.ac.comehome.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;


/**
 * @author 帅气的二峰
 */
@Controller
@RequestMapping("/file")
@CrossOrigin
public class FileController {
    @Autowired
    private HdfsService hdfsService;

    /**
     * 上传文件
     * @param file
     * @param currentPath
     * @return
     */
    @RequestMapping(value = "/uploadData.action")
    @ResponseBody
    public Result uploadData(MultipartFile file,
                             @RequestParam("currentPath") String currentPath,
                             HttpSession session){

        String name= (String) RedisUtil.get(session.getId());
        System.out.println(currentPath+"上传的当前文件夹");
        hdfsService.createFile("/"+name+currentPath,file);
        return Result.success();
    }

    /**
     * 下载目录
     * @param path
     * @param fileName
     * @return
     */
    @RequestMapping(value = "/downLoadDirectory.action",method = RequestMethod.GET)
    public ResponseEntity<byte[]> downLoadDirectory(@RequestParam("path") String path,
                                                    @RequestParam("fileName") String fileName,
                                                    HttpSession session){
        String name= (String) RedisUtil.get(session.getId());
        System.out.println("这是下载操作===="+path+":"+fileName);
        ResponseEntity<byte[]> result=null;
        result= hdfsService.downLoadDirectory("/"+name+path+"/"+fileName,fileName);
        return result;
    }
    @RequestMapping(value = "/downLoadFile.action",method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downLoadFile(@RequestParam("path") String path,
                                                            @RequestParam("fileName") String fileName,
                                                            HttpSession session){
        String name= (String) RedisUtil.get(session.getId());
        ResponseEntity<InputStreamResource> result=null;
        result= hdfsService.downLoadFile("/"+name+path+"/"+fileName,fileName);
        return result;
    }
}
























