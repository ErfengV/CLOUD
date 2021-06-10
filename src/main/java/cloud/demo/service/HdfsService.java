package cloud.demo.service;

import cloud.demo.bean.HdfsFile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @program: demo
 * @description:
 * @author: ErFeng_V
 * @create: 2021-06-10 12:45
 */
public interface HdfsService {
    void mkdir(String fileName);

    boolean delete(String name, String dlPath);

    List<HdfsFile> query(String name, String pathS);

    boolean changeFileName(String uname, String path, String oldName, String newName);

    boolean mkdir(String name, String pathS);

    void createFile(String currentPath, MultipartFile file);

    ResponseEntity<byte[]> downLoadDirectory(String path, String fileName);

    ResponseEntity<InputStreamResource> downLoadFile(String path, String fileName);

    Map<String, String> getConfigurationInfoAsMap();
}
