package com.wu.service.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wu.service.entity.*;
import com.wu.service.mapper.FileMapper;
import com.wu.service.mapper.GroupMemMapper;
import com.wu.service.mapper.UserGroupMapper;
import com.wu.service.service.FileService;
import com.wu.service.service.UserService;
import com.wu.service.service.UserDirService;
import com.wu.service.utils.AES;
import com.wu.service.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.http.MediaType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


@RestController
@RequestMapping("/educenter/file")
@CrossOrigin
public class FileController {
    @Autowired
    private UserGroupMapper groupMapper;
    @Autowired
    private FileService service;

    @Autowired
    private UserService memberService;

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private UserDirService userDirService;
    @Autowired
    private UserService userService;

    //根据名字模糊查询文件
    @ApiOperation(value = "根据名字模糊查询文件")
    @PostMapping("findFile/{memid}/{name}")
    public R findFile(@PathVariable String name, @PathVariable String memid) {
        List<File> fileList = service.getFindFile(memid, name);
        System.out.println(fileList);
        UserDir userDir = userDirService.getUserDir(memid);
        TreeNode treeNode = JSON.parseObject(userDir.getMemDir(), new TypeReference<TreeNode>() {
        });
        List list=new ArrayList();
        findTreeNode(treeNode, name,list);
        //System.out.println(list);
        return R.ok().data("fileList", fileList).data("list",list);
    }

    private static void findTreeNode(TreeNode treeNode, String name,List list1) {
        //System.out.println(treeNode);
        //System.out.println(list1);
        List<TreeNode> list=treeNode.getChildrenList();
        //System.out.println(list);
        if (list==null || list.isEmpty()){
            return;
        }
        else {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().indexOf(name)>-1){
                    //System.out.println(list.get(i).getChildrenList());
                    list1.add(list.get(i));
                    List<TreeNode> list2=list.get(i).getChildrenList();
                    System.out.println(list2);
                  if (list2.size()>=1){
                      findTreeNode(list.get(i),name,list1);
                  }
                }else{
                    findTreeNode(list.get(i),name,list1);
                }
            }
        }

    }

    //添加文件的接口方法
    @ApiOperation(value = "添加文件的信息到数据库")
    @PostMapping("addFile")
    public R addFile(@RequestBody File file) throws Exception {
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("id",file.getMemId());
        User user = userService.getOne(wrapper);
        String temKey = user.getSessionkey();
        System.out.println(temKey);
        file.setUrl(AES.decrypt(file.getUrl(),temKey));
        file.setName(AES.decrypt(file.getName(),temKey));
        file.setType(AES.decrypt(file.getType(),temKey));
        file.setFDir(AES.decrypt(file.getFDir(),temKey));
        file.setFiletype(AES.decrypt(file.getFiletype(),temKey));

        boolean save = service.save(file);

        file.setUrl(AES.encrypt(file.getUrl(),temKey));
        file.setName(AES.encrypt(file.getName(),temKey));
        file.setType((AES.encrypt(file.getType(),temKey)));
        file.setFDir(AES.encrypt(file.getFDir(),temKey));
        file.setFiletype(AES.decrypt(file.getFiletype(),temKey));

        if (save) {
            return R.ok().data("file", file);
        } else {
            return R.error();
        }

    }

    @ApiOperation(value = "添加文件的信息到数据库")
    @PostMapping("addGroupFile")
    public R addGroupFile(@RequestBody File file) throws Exception {
        QueryWrapper<UserGroup> wrapper1=new QueryWrapper<>();
        wrapper1.eq("group_id",file.getMemId());
        UserGroup groupMem = groupMapper.selectOne(wrapper1);
        String uid = groupMem.getMasterId();
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("id",uid);
        User user = userService.getOne(wrapper);
        String temKey = user.getSessionkey();
        System.out.println(temKey);
        file.setUrl(AES.decrypt(file.getUrl(),temKey));
        file.setName(AES.decrypt(file.getName(),temKey));
        file.setType(AES.decrypt(file.getType(),temKey));
        file.setFDir(AES.decrypt(file.getFDir(),temKey));
        file.setFiletype(AES.decrypt(file.getFiletype(),temKey));

        boolean save = service.save(file);

        file.setUrl(AES.encrypt(file.getUrl(),temKey));
        file.setName(AES.encrypt(file.getName(),temKey));
        file.setType((AES.encrypt(file.getType(),temKey)));
        file.setFDir(AES.encrypt(file.getFDir(),temKey));
        file.setFiletype(AES.decrypt(file.getFiletype(),temKey));

        if (save) {
            return R.ok().data("file", file);
        } else {
            return R.error();
        }

    }
    @ApiOperation(value = "群组添加文件的信息到数据库")
    @PostMapping("groupAddFile")
    public R groupAddFile(@RequestBody File file) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        fileMapper.insert(file);
        return R.ok().data("file",file);
    }
    //查询当前用户下的所有文件
    @ApiOperation(value = "查询当前用户下的所有文件")
    @GetMapping("getAllFileInfo/{memId}")
    public R getAllFileInfo(@PathVariable String memId) {
        //System.out.println(memId);
        List<File> fileList = service.getAllFileInfo(memId);
        //System.out.println(files);
        return R.ok().data("fileList", fileList);
    }

    //根据文件id查询文件具体信息
    @ApiOperation(value = "根据文件id查询文件具体信息")
    @GetMapping("getFileInfo/{id}")
    public R getfileInfo(@PathVariable String id) {
        //System.out.println(memId);
        List<File> files = service.getFileInfo(id);
        //System.out.println(files);
        return R.ok().data("file", files);
    }

    //文件重命名
    @ApiOperation(value = "文件重命名")
    @PostMapping("updateFile/{id}/{name}")
    public R updateFile(@PathVariable String id, @PathVariable String name) {
        QueryWrapper<File> wrapper=new QueryWrapper<>();
        wrapper.eq("id",id);
        File one = service.getOne(wrapper);
        File file = new File();
        file.setId(id);
        file.setName(name);
        file.setSize(one.getSize());
        boolean update = service.updateById(file);
        if (update) {
            return R.ok();
        } else {
            return R.error();
        }
    }
    //文件下载
    @ApiOperation(value = "文件下载")
    @PostMapping("/downloadFile")
    public ResponseEntity<InputStreamResource> downloadFiles(@RequestParam("id") String[] id) throws IOException {
        File file = service.getFiles(id[0]);
        String filepath = file.getUrl();
        String name = file.getName();
        String type = file.getType();

        String filename = name + "." + type;

        // 使用 Java NIO 获取文件长度
        long contentLength = Files.size(Paths.get(filepath));

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

        // 创建 InputStream
        InputStream inputStream = Files.newInputStream(Paths.get(filepath));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType); // 设置媒体类型
        headers.setContentDispositionFormData("attachment", filename); // 设置文件名
        headers.setContentLength(contentLength); // 设置 Content-Length 头
        System.out.println(headers);
        // 返回 ResponseEntity
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(mediaType)
                .body(new InputStreamResource(inputStream));

    }

    private String getFileExtensionFromType(String type) {
        if (type == null || type.isEmpty()) {
            return "";
        }

        // 假设 type 是以点号分隔的文件后缀（例如：".txt"）
        int lastDotIndex = type.lastIndexOf('.');
        if (lastDotIndex >= 0 && lastDotIndex < type.length() - 1) {
            return type.substring(lastDotIndex + 1);
        } else {
            // 如果找不到点号或点号在字符串末尾，则返回空字符串或默认后缀
            return "";
        }
    }

    //文件收藏
    @ApiOperation(value = "文件收藏")
    @PostMapping("collectFile")
    public R collectionFile(@RequestParam("id") String[] id) {
        boolean flag = false;
        for (int i = 0; i < id.length; i++) {
            System.out.println(id[i]);
            File file = new File();
            file.setId(id[i]);
            file.setCollection(1);
            boolean update = service.updateById(file);
            if (update) {
                flag = true;
            }
        }
        if (flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    //文件收藏
    @ApiOperation(value = "取消文件收藏")
    @PostMapping("cancelCollection")
    public R cancelCollection(@RequestParam("id") String[] id) {
        boolean flag = false;
        for (int i = 0; i < id.length; i++) {
            System.out.println(id[i]);
            File file = new File();
            file.setId(id[i]);
            file.setCollection(0);
            boolean update = service.updateById(file);
            if (update) {
                flag = true;
            }
        }
        if (flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    //查询用户当前的文件
    @ApiOperation(value = "查询用户当前的文件")
    @PostMapping("getCurDirFiles/{id}")
    public R setDirStruct(@RequestBody String userDir, @PathVariable String id) throws Exception {

        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("id",id);
        User user = userService.getOne(wrapper);
        String temKey = user.getSessionkey();

        List<File> files = service.getCurFiles(userDir, id);

        for(File s :files) {
            System.out.println(s);
            s.setUrl(AES.encrypt(s.getUrl(),temKey));
            s.setName(AES.encrypt(s.getName(),temKey));
            s.setType(AES.encrypt(s.getType(),temKey));
            s.setFDir(AES.encrypt(s.getFDir(),temKey));
            s.setFiletype(AES.encrypt(s.getFiletype(),temKey));
        }

        return R.ok().data("files", files);
    }


    @ApiOperation(value = "查询用户当前的文件")
    @PostMapping("getCurDirFiles1/{id}")
    public R setDirStruct1(@RequestBody String userDir, @PathVariable String id) throws Exception {

        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("id",id);
        User user = userService.getOne(wrapper);


        List<File> files = service.getCurFiles(userDir, id);

        return R.ok().data("files", files);
    }
    //查询回收站中的文件
    @ApiOperation(value = "查询回收站中的文件")
    @PostMapping("getDeleteFiles/{id}")
    public R setDelStruct(@RequestBody String userDir, @PathVariable String id) {
        List<File> files = service.getDeleteFiles(userDir, id);
        System.out.println(files);
        return R.ok().data("files", files);
    }


    //根据当前路径查询所有文件
    @ApiOperation(value = "多选文件移动")
    @PostMapping("fileMove")
    public R fileMove(@RequestBody String movingPath, @RequestParam("id") String[] id) {
        boolean flag = false;
        for (int i = 0; i < id.length; i++) {
            System.out.println(id[i]);
            File file = new File();
            file.setId(id[i]);
            file.setFDir(movingPath);
            boolean update = service.updateById(file);
            if (update) {
                flag = true;
            }
        }
        if (flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    //查询用户当前的文件
    @ApiOperation(value = "查询用户的文件数目")
    @PostMapping("getFileNum/{id}")
    public R getFileNum(@RequestBody String userDir, @PathVariable String id) {
        List<File> files = service.getCurFiles(userDir, id);
        System.out.println(files.size());
        return R.ok().data("fileNum", files.size());
    }

}

