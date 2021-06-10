package cloud.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: cloud
 * @description: 文件
 * @author: ErFeng_V
 * @create: 2021-06-05 13:41
 */
@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class HdfsFile {


    //文件名称
    private String name;
    //文件大小
    private Long fileSize;
    //文件路径
    private String path;
    //是否为文件夹
    private Boolean isDirectory;
    //修改时间
    private String modification_time;
    //上次访问时间
    private String access_time;
    //所有者
    private String owner;
    private String group;
    private String permission;

    private String color;
    private String []paths;


}
