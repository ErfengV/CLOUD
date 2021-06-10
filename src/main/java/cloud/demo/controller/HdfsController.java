package cloud.demo.controller;


import cloud.demo.bean.HdfsFile;
import cloud.demo.service.HdfsService;
import cloud.demo.util.RedisUtil;
import com.ac.comehome.entity.Result;
import com.ac.comehome.entity.ResultError;
import com.ac.comehome.enums.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;



/**
 * @program: comehome
 * @description:
 * @author: ErFeng_V
 * @create: 2021-05-30 19:59
 */
@RestController
@CrossOrigin
public class HdfsController {

    @Autowired
    private HdfsService hdfsService;


    /**
     * 获取用户目录下或指定目录下的文件
     * @param session
     * @param path
     * @return
     */
    @RequestMapping(value = "getFile", method = RequestMethod.POST)
    public Result gstAllFile(HttpSession session,String path) {
        String username= (String) RedisUtil.get(session.getId());
            List<HdfsFile> list = hdfsService.query(username, path);
            if (list.size() < 1) {
                HdfsFile hdfsFile = new HdfsFile();
                hdfsFile.setName("这是一个空文件夹");
                hdfsFile.setIsDirectory(false);
                hdfsFile.setFileSize(null);
                hdfsFile.setPath(path);
                hdfsFile.setPaths(hdfsFile.getPath().split("/"));
                hdfsFile.setAccess_time(" ");
                list.add(hdfsFile);
                //7101
                //throw new ResultError(ResultCode.FILE_NOT_FOUND);
                return Result.failure(ResultCode.FILE_NOT_FOUND,list);
            }
            return Result.success(list);
    }

    /**
     * 新建文件夹
     *
     * @param session
     * @param dirPath 带路径的文件名
     * @return
     */
    @RequestMapping(value = "newDir", method = {RequestMethod.GET, RequestMethod.POST})
    public Result newDir(HttpSession session, String dirPath,String dirName) {
        String name = (String) RedisUtil.get(session.getId());
        System.out.println(dirPath);
        if (!hdfsService.mkdir(name, dirPath+"/"+dirName)) {
            throw new IllegalArgumentException("创建失败");
        }
        return Result.success();
    }


    /**
     * 删除文件
     * @param session
     * @param path
     * @return
     */
    @RequestMapping(value = "delFile", method = {RequestMethod.GET, RequestMethod.POST})
    public Result delFile(HttpSession session, String path){
        String name = (String) RedisUtil.get(session.getId());
        if (!hdfsService.delete(name,path)){
            throw new ResultError(ResultCode.COMMON_ERROR);
        }
        return Result.success();
    }

    @RequestMapping(value = "/changeFileName", method = {RequestMethod.GET, RequestMethod.POST})
    public Result changeFileName(HttpSession session, String path, String oldName, String newName) {

        if (path == null || oldName == null || newName == null) {
            throw new ResultError(ResultCode.COMMON_PARAMS_INVALID);
        }
        String name = (String) RedisUtil.get(session.getId());
        System.out.println(name + " " + path + " " + oldName + " " + newName);
        if (!hdfsService.changeFileName(name, path, oldName, newName)) {
            throw new ResultError(ResultCode.COMMON_ERROR);
        }
        return Result.success();
    }

    @PostMapping("/getHdfsInfo")
    public Result getHdfsInfo(){
        Map<String,String> info= hdfsService.getConfigurationInfoAsMap();
        return Result.success(info);
    }

}
