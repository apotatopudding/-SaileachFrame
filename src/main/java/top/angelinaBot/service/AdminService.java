package top.angelinaBot.service;

import net.mamoe.mirai.contact.MemberPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.container.AngelinaContainer;
import top.angelinaBot.dao.AdminMapper;
import top.angelinaBot.dao.EnableMapper;
import top.angelinaBot.model.EventEnum;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.SendMessageUtil;
import top.angelinaBot.util.AdminUtil;

import java.util.List;
import java.util.Set;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private EnableMapper enableMapper;

    @Autowired
    private SendMessageUtil sendMessageUtil;

    @AngelinaGroup(keyWords = {"关闭"}, description = "关闭洁哥的某个功能", sort = "框架基础功能")
    public ReplayInfo closeFunc(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getUserAdmin().equals(MemberPermission.MEMBER)) {
            replayInfo.setReplayMessage("仅本群群主及管理员有权限对功能进行操作");
            return replayInfo;
        }
        if (messageInfo.getArgs().size() > 2) {
            if (!AdminUtil.getAdmin(messageInfo.getQq())) {
                replayInfo.setReplayMessage("仅超级管理员有权限对功能进行操作");
                return replayInfo;
            }else if(messageInfo.getArgs().get(2).equals("全体")){
                String key = messageInfo.getArgs().get(1);
                if (key.equals("开启") || key.equals("打开") || key.equals("关闭")) {
                    replayInfo.setReplayMessage("您不能对基本操作进行更改");
                    return replayInfo;
                }
                if (AngelinaContainer.groupMap.containsKey(key)) {
                    adminMapper.closeAllFunction(AngelinaContainer.groupMap.get(key).getName());
                    replayInfo.setReplayMessage("关闭全体功能 " + key + "成功");
                    return replayInfo;
                } else {
                    Set<EventEnum> eventEnums = AngelinaContainer.eventMap.keySet();
                    for (EventEnum e: eventEnums) {
                        if (key.equals(e.getEventName())) {
                            adminMapper.closeAllFunction(AngelinaContainer.eventMap.get(e).getName());
                            replayInfo.setReplayMessage("关闭全体事件 " + key + "成功");
                            return replayInfo;
                        }
                    }
                }
            }
        }else if (messageInfo.getArgs().size()>1){
            String key = messageInfo.getArgs().get(1);
            if (key.equals("开启") || key.equals("打开") || key.equals("关闭")) {
                replayInfo.setReplayMessage("您不能对基本操作进行更改");
                return replayInfo;
            }
            if (AngelinaContainer.groupMap.containsKey(key)) {
                adminMapper.closeFunction(messageInfo.getGroupId(), AngelinaContainer.groupMap.get(key).getName());
                replayInfo.setReplayMessage("关闭功能 " + key + "成功");
                return replayInfo;
            } else {
                Set<EventEnum> eventEnums = AngelinaContainer.eventMap.keySet();
                for (EventEnum e: eventEnums) {
                    if (key.equals(e.getEventName())) {
                        adminMapper.closeFunction(messageInfo.getGroupId(), AngelinaContainer.eventMap.get(e).getName());
                        replayInfo.setReplayMessage("关闭事件 " + key + "成功");
                        return replayInfo;
                    }
                }
            }
        }
        replayInfo.setReplayMessage("未找到该功能，关闭失败");
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"开启", "打开"}, description = "打开洁哥的某个功能", sort = "框架基础功能")
    public ReplayInfo openFunc(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getUserAdmin().equals(MemberPermission.MEMBER)) {
            replayInfo.setReplayMessage("仅本群群主及管理员有权限对功能进行操作");
            return replayInfo;
        }
        if (messageInfo.getArgs().size() > 2) {
            if (!AdminUtil.getAdmin(messageInfo.getQq())) {
                replayInfo.setReplayMessage("仅超级管理员有权限对功能进行操作");
                return replayInfo;
            }else if (messageInfo.getArgs().get(2).equals("全体")){
                String key = messageInfo.getArgs().get(1);
                if (key.equals("开启") || key.equals("打开") || key.equals("关闭")) {
                    replayInfo.setReplayMessage("您不能对基本操作进行更改");
                    return replayInfo;
                }
                if (AngelinaContainer.groupMap.containsKey(key)) {
                    adminMapper.openAllFunction(AngelinaContainer.groupMap.get(key).getName());
                    replayInfo.setReplayMessage("打开全体功能 " + key + "成功");
                    return replayInfo;
                } else {
                    Set<EventEnum> eventEnums = AngelinaContainer.eventMap.keySet();
                    for (EventEnum e: eventEnums) {
                        if (key.equals(e.getEventName())) {
                            adminMapper.openAllFunction(AngelinaContainer.eventMap.get(e).getName());
                            replayInfo.setReplayMessage("打开全体事件 " + key + "成功");
                            return replayInfo;
                        }
                    }
                }
            }
        }else if (messageInfo.getArgs().size()>1){
            String key = messageInfo.getArgs().get(1);
            if (key.equals("开启") || key.equals("打开") || key.equals("关闭")) {
                replayInfo.setReplayMessage("您不能对基本操作进行更改");
                return replayInfo;
            }
            if (AngelinaContainer.groupMap.containsKey(key)) {
                adminMapper.openFunction(messageInfo.getGroupId(), AngelinaContainer.groupMap.get(key).getName());
                replayInfo.setReplayMessage("打开功能 " + key + "成功");
                return replayInfo;
            } else {
                Set<EventEnum> eventEnums = AngelinaContainer.eventMap.keySet();
                for (EventEnum e: eventEnums) {
                    if (key.equals(e.getEventName())) {
                        adminMapper.openFunction(messageInfo.getGroupId(), AngelinaContainer.eventMap.get(e).getName());
                        replayInfo.setReplayMessage("打开事件 " + key + "成功");
                        return replayInfo;
                    }
                }
            }
        }
        replayInfo.setReplayMessage("未找到该功能，打开失败");
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"已关闭", "功能开关"}, description = "查看洁哥当前已关闭的功能", sort = "框架基础功能")
    public ReplayInfo funcList(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        List<String> closeFunction = adminMapper.getCloseFunction(messageInfo.getGroupId());
        if (closeFunction.size() == 0) {
            replayInfo.setReplayMessage("当前本群功能均已开启");
            return replayInfo;
        }
        StringBuilder sb = new StringBuilder();
        for (String s: closeFunction) {
            sb.append(s).append("   ").append("关闭").append("\n");
        }
        replayInfo.setReplayMessage(sb.toString());
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"群组关闭"},description = "关闭某个群组所有接收消息（默认是开启的）", sort = "框架基础功能", funcClass = "权限功能")
    public ReplayInfo closeGroup(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getUserAdmin().equals(MemberPermission.MEMBER)) {
            replayInfo.setReplayMessage("仅本群群主及管理员有权限对功能进行操作");
        } else {
            replayInfo.setReplayMessage("关闭成功");
            sendMessageUtil.sendGroupMsg(replayInfo);
            replayInfo.setReplayMessage(null);
            this.enableMapper.closeGroup(messageInfo.getGroupId(), 0);
        }
        return replayInfo;
    }

    //（暂时用不着，未配置管理群组的时候使用）@AngelinaGroup(keyWords = {"群组开启"},description = "开启某个群组所有接收消息（默认是开启的）")
    public ReplayInfo openGroup(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getUserAdmin().equals(MemberPermission.MEMBER)) {
            replayInfo.setReplayMessage("仅本群群主及管理员有权限对功能进行操作");
        } else {
            if(this.enableMapper.canUseGroup(messageInfo.getGroupId(),0)==1){
                this.enableMapper.closeGroup(messageInfo.getGroupId(), 1);
                replayInfo.setReplayMessage("开启成功");
            }else if(this.enableMapper.canUseGroup(messageInfo.getGroupId(),1)==1){
                replayInfo.setReplayMessage("请不要重复开启");
            }else {
                this.enableMapper.closeGroup(messageInfo.getGroupId(), 1);
                replayInfo.setReplayMessage("开启成功");
            }
        }
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"关闭破站解析"},description = "关闭小破站的地址解析（默认是关闭的）", sort = "框架基础功能")
    public ReplayInfo closeBilibili(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getUserAdmin().equals(MemberPermission.MEMBER)) {
            replayInfo.setReplayMessage("仅本群群主及管理员有权限对功能进行操作");
        } else {
            if(this.enableMapper.canUseBilibili(messageInfo.getGroupId(),0)==0){
                if(this.enableMapper.canUseBilibili(messageInfo.getGroupId(),1)==0) replayInfo.setReplayMessage("请不要重复关闭");
                else {
                    this.enableMapper.closeBilibili(messageInfo.getGroupId(), 0);
                    replayInfo.setReplayMessage("关闭成功");
                }
            }else replayInfo.setReplayMessage("请不要重复关闭");
        }
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"开启破站解析"},description = "开启小破站的地址解析（默认是关闭的）", sort = "框架基础功能")
    public ReplayInfo openBilibili(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getUserAdmin().equals(MemberPermission.MEMBER)) {
            replayInfo.setReplayMessage("仅本群群主及管理员有权限对功能进行操作");
        } else {
            if(this.enableMapper.canUseBilibili(messageInfo.getGroupId(),1)==0){
                this.enableMapper.closeBilibili(messageInfo.getGroupId(), 1);
                replayInfo.setReplayMessage("开启成功");
            }else replayInfo.setReplayMessage("请不要重复开启");
        }
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"关闭截图解析"},description = "关闭公招截图自动识别（默认是开启的）", sort = "框架基础功能")
    public ReplayInfo closeDHash(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getUserAdmin().equals(MemberPermission.MEMBER)) {
            replayInfo.setReplayMessage("仅本群群主及管理员有权限对功能进行操作");
        } else {
            if(this.enableMapper.canUseDHash(messageInfo.getGroupId(),0)==0){
                this.enableMapper.closeDHash(messageInfo.getGroupId(), 0);
                replayInfo.setReplayMessage("关闭成功");
            }else replayInfo.setReplayMessage("请不要重复关闭");
        }
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"开启截图解析"},description = "开启公招截图自动识别（默认是开启的）", sort = "框架基础功能")
    public ReplayInfo openDHash(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getUserAdmin().equals(MemberPermission.MEMBER)) {
            replayInfo.setReplayMessage("仅本群群主及管理员有权限对功能进行操作");
        } else {
            if(this.enableMapper.canUseDHash(messageInfo.getGroupId(),1)==0){
                if (this.enableMapper.canUseDHash(messageInfo.getGroupId(),0)==0) replayInfo.setReplayMessage("请不要重复开启");
                else {
                    this.enableMapper.closeDHash(messageInfo.getGroupId(), 1);
                    replayInfo.setReplayMessage("开启成功");
                }
            }else replayInfo.setReplayMessage("请不要重复开启");
        }
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"开启自动撤回"},description = "开启对图片消息自动撤回（默认是关闭的）", sort = "框架基础功能", funcClass = "权限功能")
    public ReplayInfo openRecall(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (!AdminUtil.getAdmin(messageInfo.getQq())) {
            replayInfo.setReplayMessage("权限不足，需要超级管理员权限");
        } else {
            if(this.enableMapper.canUseRecall(messageInfo.getGroupId(),1)==0){
                this.enableMapper.closeRecall(messageInfo.getGroupId(), 1);
                replayInfo.setReplayMessage("开启成功");
            }else replayInfo.setReplayMessage("请不要重复开启");
        }
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"关闭自动撤回"},description = "关闭对图片消息自动撤回（默认是关闭的）", sort = "框架基础功能", funcClass = "权限功能")
    public ReplayInfo closeRecall(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (!AdminUtil.getAdmin(messageInfo.getQq())) {
            replayInfo.setReplayMessage("权限不足，需要超级管理员权限");
        }else {
            if(this.enableMapper.canUseRecall(messageInfo.getGroupId(),0)==0){
                if (this.enableMapper.canUseRecall(messageInfo.getGroupId(),1)==0) {
                    replayInfo.setReplayMessage("请不要重复关闭");
                }else {
                    this.enableMapper.closeRecall(messageInfo.getGroupId(), 0);
                    replayInfo.setReplayMessage("关闭成功");
                }
            }else replayInfo.setReplayMessage("请不要重复关闭");
        }
        return replayInfo;
    }

}
