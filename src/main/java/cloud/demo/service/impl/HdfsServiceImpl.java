package cloud.demo.service.impl;

import cloud.demo.bean.HdfsFile;
import cloud.demo.service.HdfsService;
import cloud.demo.util.FileUtil;
import cloud.demo.util.TimeUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @program: cloud
 * @description:
 * @author: ErFeng_V
 * @create: 2021-06-05 13:43
 */
@Repository
public class HdfsServiceImpl implements HdfsService {
    // HDFS文件系统服务器的地址以及端口
    private String HDFS_PATH = "hdfs://node1:9000";
    // HDFS文件系统的操作对象
    private FileSystem fileSystem = null;
    // 配置对象
    private Configuration configuration = null;
    private final int bufferSize=1024*1024*64;

    public HdfsServiceImpl() {
        configuration = new Configuration();
        configuration.setBoolean("fs.hdfs.impl.disable.cache", true);
        // 第一参数是服务器的URI，第二个参数是配置对象，第三个参数是文件系统的用户名
        try {
            fileSystem = FileSystem.get(new URI(HDFS_PATH), configuration, "root");
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }
    }



    public void setHDFS_PATH(String HDFS_PATH) {
        this.HDFS_PATH = HDFS_PATH;
    }

    @Override
    public void mkdir(String fileName) {
        // 需要传递一个Path对象
        boolean result = false;
        try {
            result = fileSystem.mkdirs(new Path("/" + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }
    @Override
    public boolean delete(String name, String dlPath) {
        Path path;
        if (name == null || dlPath == null || "".equals(dlPath) || dlPath.endsWith("undefined")) {
            return false;
        } else {
            path = new Path("/" + name + "/" + dlPath);
        }
        // 第二个参数指定是否要递归删除，false=否，true=是
        try {
            fileSystem.delete(path, true);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<HdfsFile> query(String name, String pathS) {
        Path path;
        if (pathS == null || pathS.equals("")) {
            path = new Path("/" + name);
        } else {
            path = new Path("/" + name + "/" + pathS);
        }

        FileStatus[] fileStatuses;
        List<HdfsFile> list = new ArrayList<>();
        try {
            fileStatuses = fileSystem.listStatus(path);
            int colorindex=0;
            String []color={"default","success",
                    "warning",
                    "info",
                    "danger",
                    "active"
            };
            for (FileStatus fileStatus : fileStatuses) {
                HdfsFile hfs = new HdfsFile();
                //获取文件名
                String fullfilepath=String.valueOf(fileStatus.getPath());
                String filepath=fullfilepath.substring(fullfilepath.indexOf(name)+name.length(),fullfilepath.lastIndexOf("/")+1);
                hfs.setPath(filepath);
                hfs.setName(fullfilepath.substring(fullfilepath.lastIndexOf("/")+1));
                //获取文件是否为文件夹
                hfs.setIsDirectory(fileStatus.isDirectory());
                //文件上次修改时间
                hfs.setModification_time(TimeUtil.timeToString(fileStatus.getModificationTime()));
                hfs.setAccess_time(TimeUtil.timeToString(fileStatus.getAccessTime()));
                //获取文件实际大小
                hfs.setFileSize((fileSystem.getContentSummary(fileStatus.getPath()).getSpaceConsumed()) / 3);
                hfs.setOwner(fileStatus.getOwner());
                hfs.setPermission(fileStatus.getPermission()+"");
                //hfs.setBlocksize(fileStatus.getBlockSize());
                hfs.setColor(color[(++colorindex)%color.length]);
                hfs.setPaths(hfs.getPath().split("/"));
                list.add(hfs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;

    }

    /**
     * 重命名
     * @param uname
     * @param path
     * @param oldName
     * @param newName
     * @return
     */
    @Override
    public boolean changeFileName(String uname, String path, String oldName, String newName) {

        Path oldPath = new Path("/" + uname + "/" + path + "/" + oldName);
        Path newPath = new Path("/" + uname + "/" + path + "/" + newName);
        System.out.println(oldPath);
        System.out.println(newPath);
        // 第一个参数是原文件的名称，第二个则是新的名称
        try {
            fileSystem.rename(oldPath, newPath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 新建文件夹
     * 新建用户目录下的文件夹
     *
     * @param name
     * @param pathS
     * @return
     */
    @Override
    public boolean mkdir(String name, String pathS) {
        Path path;
        if (pathS == null || pathS.equals("") || pathS.endsWith("undefined")) {
            path = new Path("/" + name);
        } else {
            path = new Path("/" + name + "/" + pathS);
        }
        boolean result = false;
        try {
            result = fileSystem.mkdirs(path);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void createFile(String currentPath, MultipartFile file)  {
        try {
            if(StringUtils.isEmpty(currentPath) || null==file.getBytes()){
                return;
            }
            String fileName=file.getOriginalFilename();
            FileSystem fs=fileSystem;
            Path newPath=null;
            if("/".equals(currentPath)){
                newPath=new Path(currentPath+fileName);
            }else{
                newPath=new Path(currentPath+"/"+fileName);
            }
            //打开一个输入流
            FSDataOutputStream outputStream=fs.create(newPath);
            outputStream.write(file.getBytes());
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResponseEntity<byte[]> downLoadDirectory(String path, String fileName) {
        //1.获取对象
        //字节数组输出流 (内存)
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        //压缩流
        ZipOutputStream zos=new ZipOutputStream(out);

        byte[] bs=null;
        try {
            compress(path,zos,fileSystem);
            zos.close();
            bs=out.toByteArray();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cloud.demo.util.FileUtil.downLoadDirectory(bs, cloud.demo.util.FileUtil.getFileName());


    }

    //压缩
    private void compress(String path, ZipOutputStream zos, FileSystem fileSystem) throws IOException {
        FileStatus[] fileStatuses=fileSystem.listStatus(new Path(path));
        String []strs=path.split("/");

        String lastName=strs[strs.length-1];
        for(int i=0;i<fileStatuses.length;i++){
            String name=fileStatuses[i].getPath().toString();
            name=name.substring(name.indexOf("/"+lastName));
            if(fileStatuses[i].isFile()){
                Path p=fileStatuses[i].getPath();
                FSDataInputStream inputStream=fileSystem.open(p);
                zos.putNextEntry(new ZipEntry(name.substring(1)));
                IOUtils.copyBytes(inputStream,zos,this.bufferSize);
                inputStream.close();
            }else {
                zos.putNextEntry(new ZipEntry(fileStatuses[i].getPath().getName()+"/"));
                compress(fileStatuses[i].getPath().toString(),zos,fileSystem);
            }
        }
    }

    @Override
    public ResponseEntity<InputStreamResource> downLoadFile(String path, String fileName) {
        Path p=new Path(path);
        try {
            FSDataInputStream inputStream=fileSystem.open(p);
            return FileUtil.downLoadFile(inputStream,fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
       return null;
    }

    /**
     * 获取hadoop的详细信息
     * @return
     */
    @Override
    public Map<String, String> getConfigurationInfoAsMap() {
        Configuration conf=fileSystem.getConf();
        Iterator<Map.Entry<String,String>> ite=conf.iterator();

        Map<String,String> map=new HashMap<>();
        while (ite.hasNext()){
            Map.Entry<String,String> entry=ite.next();
            map.put(entry.getKey(),entry.getValue());
        }
        return map;
    }
}
