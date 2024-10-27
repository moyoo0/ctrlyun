package com.wu.service.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wu.service.entity.*;
import com.wu.service.service.FileService;
import com.wu.service.utils.AES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wu.service.utils.AES;
import com.wu.service.mapper.FileMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wu.service.mapper.UserGroupMapper;
import com.wu.service.utils.R;
import com.wu.service.mapper.GroupMemMapper;
import com.wu.service.mapper.GroupRequestMapper;
import com.wu.service.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/edugro/group")
@CrossOrigin
public class UserGroupController {

    @Autowired
    private FileService service;

    @Autowired
    private UserService memberService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserGroupMapper groupMapper;

    @Autowired
    private GroupMemMapper groupMemMapper;

    @Autowired
    private GroupRequestMapper groupRequestMapper;

    @Autowired
    private FileMapper fileMapper;

    @ApiOperation(value = "创建群组")
    @PostMapping("createGroup")
    public R createGroup(@RequestBody Map<String, String> requestData) {
        // 根据groupID查询记录

        String groupID = requestData.get("groupID");
        String uid = requestData.get("uid");
        QueryWrapper<UserGroup> Wrapper = new QueryWrapper<>();
        Wrapper.eq("group_id", groupID);
        int count = groupMapper.selectCount(Wrapper);
        if (count != 0) {
            // 如果已存在相同的groupID记录，返回一个包含错误消息的R.error()
            return R.error().message("组号已存在");
        }
        System.out.println("54654654645");
        // 如果不存在相同的groupID记录，插入新记录
        UserGroup newGroup = new UserGroup();
        newGroup.setGroupId(groupID);
        newGroup.setMasterId(uid);
        groupMapper.insert(newGroup);
        GroupMem groupmem = new GroupMem();
        groupmem.setGroupId(groupID);
        groupmem.setMemId(uid);
        QueryWrapper<GroupMem> wrapper = new QueryWrapper<>();
        wrapper.eq("record", 1);
        int exit = groupMemMapper.selectCount(wrapper);
        int currentRecord = 0;
        if (exit == 0) {
        } else {
            currentRecord = groupMemMapper.selectMaxValueOfColumn();
        }
        groupmem.setRecord(currentRecord + 1);
        groupMemMapper.insert(groupmem);
        // 返回R.ok()
        return R.ok().message("群组创建成功");
    }

    @ApiOperation(value = "加入群组")
    @PostMapping("joinGroup")
    public R joinGroup(@RequestBody Map<String, String> requestData) {
        // 根据groupID查询记录

        String groupID = requestData.get("groupID");
        String uid = requestData.get("uid");
        QueryWrapper<GroupMem> wrapper = new QueryWrapper<>();
        wrapper.eq("group_id", groupID);
        wrapper.eq("mem_id", uid);
        int count1 = groupMemMapper.selectCount(wrapper);
        QueryWrapper<GroupRequest> Wrapper = new QueryWrapper<>();
        Wrapper.eq("group_id", groupID);
        Wrapper.eq("user_id", uid);
        QueryWrapper<UserGroup> Wrapper1 = new QueryWrapper<>();
        Wrapper1.eq("group_id", groupID);
        int count2 = groupMapper.selectCount(Wrapper1);
        int count = groupRequestMapper.selectCount(Wrapper);
        if (count1 != 0) {
            return R.error().message("您已经进群了");
        } else if (count != 0) {
            // 如果已存在相同的groupID记录，返回一个包含错误消息的R.error()
            return R.error().message("您已经申请过了");
        } else if (count2 == 0) {
            return R.error().message("该群不存在");
        } else {
            GroupRequest request = new GroupRequest();
            request.setGroupId(groupID);
            request.setUserId(uid);
            request.setStatus("success");
            int exit = groupMemMapper.selectMaxValueOfColumn();
            int currentRecord = 0;
            if (exit == 0) {
            } else {
                currentRecord = groupMemMapper.selectMaxValueOfColumn();
            }
            request.setRequestId(currentRecord + 1);
            groupRequestMapper.insert(request);
            // 返回R.ok()
            return R.ok().message("加群申请发送成功");
        }
    }

    @ApiOperation(value = "显示群组")
    @PostMapping("performGroup")
    public R performGroup(@RequestParam("uid") String uid) throws Exception{
        List<GroupMem> groupList = groupMemMapper.selectGroupByID(uid);
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("id",uid);
        User user = userService.getOne(wrapper);
        System.out.println(groupList);
        return R.ok().data("list", groupList);
    }

    @ApiOperation(value = "显示消息")
    @PostMapping("performMessage")
    public R performMessage(@RequestParam("masterid") String MasterID) {
        String[] groupIDList = groupMapper.selectGroupID(MasterID);
        // 创建一个用于存储请求的列表
        List<GroupRequest> requestList = new ArrayList<>();

        // 遍历 group_id 列表，查询每个 group_id 对应的请求并将结果添加到列表中
        for (String groupID : groupIDList) {
            List<GroupRequest> groupRequests = groupRequestMapper.selectGroupByID(groupID);
            if (groupRequests != null && !groupRequests.isEmpty()) {
                requestList.addAll(groupRequests);
            }
        }
        System.out.println(requestList);
        return R.ok().data("list", requestList);
    }


    @ApiOperation(value = "处理消息")
    @PostMapping("handleMessage")
    public R handleMessage(@RequestBody Map<String, String> requestData) {
     String groupId = requestData.get("groupId");
     String userId = requestData.get("userId");
     QueryWrapper<GroupRequest> Wrapper = new QueryWrapper<>();
     Wrapper.eq("group_id", groupId);
     Wrapper.eq("user_id", userId);
     int count = groupRequestMapper.delete(Wrapper);

     if (count>0) {
         GroupMem groupmem = new GroupMem();
         groupmem.setGroupId(groupId);
         groupmem.setMemId(userId);
         QueryWrapper<GroupMem> wrapper = new QueryWrapper<>();
         wrapper.eq("record", 1);
         int exit = groupMemMapper.selectCount(wrapper);
         int currentRecord = 0;
         if (exit == 0) {
         } else {
             currentRecord = groupMemMapper.selectMaxValueOfColumn();
         }
         groupmem.setRecord(currentRecord + 1);
         groupMemMapper.insert(groupmem);
         return R.ok();
     }
     return R.error();
    }

    @ApiOperation(value = "拒绝消息")
    @PostMapping("handleRejectMessage")
    public R handleRejectMessage(@RequestBody Map<String, String> requestData) {
        String groupId = requestData.get("groupId");
        String userId = requestData.get("userId");
        QueryWrapper<GroupRequest> Wrapper = new QueryWrapper<>();
        Wrapper.eq("group_id", groupId);
        Wrapper.eq("user_id", userId);
        int count = groupRequestMapper.delete(Wrapper);

        if (count>0) {
            return R.ok();
        }
        return R.error();
    }
    @ApiOperation(value = "查询群组当前的文件")
    @PostMapping("getGroupFiles/{id}")
    public R setGroupStruct(@RequestBody String userDir, @PathVariable String id) throws Exception {
        QueryWrapper<UserGroup> wrapper=new QueryWrapper<>();
        wrapper.eq("group_id",id);
        UserGroup groupMem = groupMapper.selectOne(wrapper);
        String uid = groupMem.getMasterId();
        QueryWrapper<User> wrapper1=new QueryWrapper<>();
        wrapper.eq("id",uid);
        System.out.println(uid);
        User user = userService.getOne(wrapper1);
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
        return R.ok().data("files", files).data("key",temKey);
    }

    @ApiOperation(value = "查询群组当前的文件")
    @PostMapping("getAccount/{id}")
    public R getAccount(@RequestBody String userDir, @PathVariable String id) throws Exception {
        QueryWrapper<UserGroup> wrapper=new QueryWrapper<>();
        wrapper.eq("group_id",id);
        UserGroup groupMem = groupMapper.selectOne(wrapper);
        String uid = groupMem.getMasterId();
        QueryWrapper<User> wrapper1=new QueryWrapper<>();
        wrapper.eq("id",uid);
        System.out.println(uid);
        User user = userService.getOne(wrapper1);
        String account = user.getAccount();
        return R.ok().data("account",account);
    }
}

