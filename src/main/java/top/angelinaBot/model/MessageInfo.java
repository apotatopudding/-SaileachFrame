package top.angelinaBot.model;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.IMirai;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;
//import net.mamoe.mirai.internal.message.OnlineFriendImage;
//import net.mamoe.mirai.internal.message.OnlineGroupImage;
import net.mamoe.mirai.message.data.*;
import org.apache.logging.log4j.message.SimpleMessage;

import java.lang.reflect.InvocationTargetException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * qq消息格式化Bean
 **/
public class MessageInfo {
    //登录qq
    private Long loginQq;
    //原文字消息
    private String text;
    //文字消息的第一节
    private String keyword;
    //文字消息的参数
    private List<String> args = new ArrayList<>();
    //发送人qq
    private Long qq;
    //发送人昵称
    private String name;
    //群号
    private Long groupId;
    //群名
    private String groupName;
    //群主
    private String groupOwnerName;
    //群列表（速度较慢）
    private ContactList<NormalMember> memberList;
    //图片url集合
    private List<String> imgUrlList = new ArrayList<>();
    //图片类型集合
    private List<ImageType> imgTypeList = new ArrayList<>();
    //是否被呼叫
    private Boolean isCallMe = false;
    //艾特了哪些人
    private List<Long> atQQList = new ArrayList<>();
    //发送时间戳
    private Integer time;
    //消息缩略字符串
    private String eventString;
    //接收到的事件
    private EventEnum event;
    //是否要发送消息
    private Boolean isReplay = true;
    //用户权限
    private MemberPermission userAdmin;
    //获取机器人权限
    private MemberPermission botPermission;
    //获取json项目
    private String JSONObjectCTS;
    //获取json数组
    private String JSONObjectCTMC;
    //获取bot名字
    private String botName;
    //消息链本体
    private MessageChain chain;

    public MessageInfo() {
    }

    /**
     * 根据Mirai的事件构建Message，方便后续调用
     * @param event Mirai事件
     * @param botNames 机器人名称
     */
    public MessageInfo(GroupMessageEvent event, String[] botNames){
        // Mirai 的事件内容封装
        this.loginQq = event.getBot().getId();
        this.qq = event.getSender().getId();
        this.name = event.getSenderName();
        this.groupId = event.getSubject().getId();
        this.groupName = event.getSubject().getName();
        this.groupOwnerName = event.getSubject().getOwner().getNameCard();
        this.time = event.getTime();
        this.userAdmin = event.getSender().getPermission();
        this.botPermission = event.getGroup().getBotPermission();

        //获取消息体
        this.chain = event.getMessage();
        this.eventString = this.chain.toString();
        this.JSONObjectCTS = this.chain.contentToString();
        this.JSONObjectCTMC = this.chain.serializeToMiraiCode();

        for (SingleMessage o: chain){
            if (o instanceof At) {
                //艾特消息内容
                this.atQQList.add(((At) o).getTarget());
                if (((At) o).getTarget() == this.loginQq){
                    //如果被艾特则视为被呼叫
                    this.isCallMe = true;
                }
            } else if (o instanceof PlainText) {
                //文字消息内容
                this.text = ((PlainText) o).getContent().trim();
                String[] orders = this.text.split("\\s+");
                if (orders.length > 0) {
                    this.keyword = orders[0];
                    this.args = Arrays.asList(orders);
                    for (String name: botNames){
                        if (orders[0].startsWith(name)){
                            this.isCallMe = true;
                            this.botName = name;
                            this.keyword = this.keyword.replace(name, "");
                            break;
                        }
                    }
                }
            } else if (o instanceof Image){
                //图片消息内容
                this.imgUrlList.add(this.imageUrl((Image) o));
                this.imgTypeList.add(((Image) o).getImageType());
            }
        }

        /*
        for (Object o: chain){
            if (o instanceof At) {
                //消息艾特内容
                this.atQQList.add(((At) o).getTarget());
                if (((At) o).getTarget() == this.loginQq){
                    //如果被艾特则视为被呼叫
                    this.isCallMe = true;
                }
            } else if (o instanceof PlainText) {
                //消息文字内容
                this.text = ((PlainText) o).getContent().trim();
                String[] orders = this.text.split("\\s+");
                if (orders.length > 0) {
                    this.keyword = orders[0];
                    this.args = Arrays.asList(orders);
                    for (String name: botNames){
                        if (orders[0].startsWith(name)){
                            this.isCallMe = true;
                            this.botName = name;
                            this.keyword = this.keyword.replace(name, "");
                            break;
                        }
                    }
                }
            } else if (o instanceof OnlineGroupImage){ // 编译器有可能因为无法识别Kotlin的class而报红，问题不大能通过编译
                //消息图片内容
                this.imgUrlList.add(((OnlineGroupImage) o).getOriginUrl());
                this.imgTypeList.add(((OnlineGroupImage) o).getImageType());
            }
        }
        */
    }

    private String imageUrl(Image image){ return Image.queryUrl(image); }

    public MessageInfo(FriendMessageEvent event, String[] botNames) {
        this.loginQq = event.getBot().getId();
        this.qq = event.getSender().getId();
        this.name = event.getSenderName();
        this.time = event.getTime();
        //获取消息体
        MessageChain chain = event.getMessage();
        this.eventString = chain.toString();
        for (SingleMessage o: chain){
            if (o instanceof At) {
                //消息艾特内容
                this.atQQList.add(((At) o).getTarget());
                if (((At) o).getTarget() == this.loginQq){
                    //如果被艾特则视为被呼叫
                    this.isCallMe = true;
                }
            } else if (o instanceof PlainText) {
                //消息文字内容
                this.text = ((PlainText) o).getContent().trim();

                String[] orders = this.text.split("\\s+");
                if (orders.length > 0) {
                    this.keyword = orders[0];
                    this.args = Arrays.asList(orders);
                    this.isCallMe = true;
                    for (String name: botNames){
                        if (orders[0].startsWith(name)){
                            this.keyword = this.keyword.replace(name, "");
                            break;
                        }
                    }
                }
            } else if (o instanceof Image){
                //消息图片内容
                this.imgUrlList.add(Image.queryUrl((Image) o));
                this.imgTypeList.add(((Image) o).getImageType());
            }
        }
    }

    public MessageInfo(StrangerMessageEvent event, String[] botNames) {
        this.loginQq = event.getBot().getId();
        this.qq = event.getSender().getId();
        this.name = event.getSenderName();
        this.time = event.getTime();
        //获取消息体
        MessageChain chain = event.getMessage();
        this.eventString = chain.toString();
        for (Object o: chain){
            if (o instanceof At) {
                //消息艾特内容
                this.atQQList.add(((At) o).getTarget());
                if (((At) o).getTarget() == this.loginQq){
                    //如果被艾特则视为被呼叫
                    this.isCallMe = true;
                }
            } else if (o instanceof PlainText) {
                //消息文字内容
                this.text = ((PlainText) o).getContent().trim();

                String[] orders = this.text.split("\\s+");
                if (orders.length > 0) {
                    this.keyword = orders[0];
                    this.args = Arrays.asList(orders);
                    this.isCallMe = true;
                    for (String name: botNames){
                        if (orders[0].startsWith(name)){
                            this.keyword = this.keyword.replace(name, "");
                            break;
                        }
                    }
                }
            }
        }
    }
    public MessageInfo(GroupTempMessageEvent event, String[] botNames) {
        this.loginQq = event.getBot().getId();
        this.qq = event.getSender().getId();
        this.groupId = event.getGroup().getId();
        this.name = event.getSenderName();
        this.time = event.getTime();
        //获取消息体
        MessageChain chain = event.getMessage();
        this.eventString = chain.toString();
        for (Object o: chain){
            if (o instanceof At) {
                //消息艾特内容
                this.atQQList.add(((At) o).getTarget());
                if (((At) o).getTarget() == this.loginQq){
                    //如果被艾特则视为被呼叫
                    this.isCallMe = true;
                }
            } else if (o instanceof PlainText) {
                //消息文字内容
                this.text = ((PlainText) o).getContent().trim();

                String[] orders = this.text.split("\\s+");
                if (orders.length > 0) {
                    this.keyword = orders[0];
                    this.args = Arrays.asList(orders);
                    this.isCallMe = true;
                    for (String name: botNames){
                        if (orders[0].startsWith(name)){
                            this.keyword = this.keyword.replace(name, "");
                            break;
                        }
                    }
                }
            } else if (o instanceof Image){
            //消息图片内容
                this.imgUrlList.add(Image.queryUrl((Image) o));
                this.imgTypeList.add(((Image) o).getImageType());
        }
        }
    }

    public MessageInfo(BotJoinGroupEvent event){
        this.loginQq = event.getBot().getId();
        this.groupId = event.getGroup().getId();
        this.event = EventEnum.BotJoinGroupEvent;
        this.memberList = event.getGroup().getMembers();
    }

    public boolean isReplay() {
        return isReplay;
    }

    public void setReplay(boolean replay) {
        isReplay = replay;
    }

    public EventEnum getEvent() {
        return event;
    }

    public void setEvent(EventEnum event) {
        this.event = event;
    }

    public List<ImageType> getImgTypeList() {
        return imgTypeList;
    }

    public void setImgTypeList(List<ImageType> imgTypeList) {
        this.imgTypeList = imgTypeList;
    }

    public String getEventString() {
        return eventString;
    }

    public void setEventString(String eventString) {
        this.eventString = eventString;
    }

    public Long getLoginQq() {
        return loginQq;
    }

    public void setLoginQq(Long loginQq) {
        this.loginQq = loginQq;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getQq() {
        return qq;
    }

    public void setQq(Long qq) {
        this.qq = qq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getGroupId() {
        return groupId;
    }

    public String getGroupName() { return groupName; }

    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getGroupOwnerName() { return groupOwnerName; }

    public void setGroupOwnerName(String groupOwnerName) { this.groupOwnerName = groupOwnerName; }

    public ContactList<NormalMember> getMemberList() { return memberList; }

    public void setMemberList(ContactList<NormalMember> memberList) { this.memberList = memberList; }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Boolean getCallMe() {
        return isCallMe;
    }

    public void setCallMe(Boolean callMe) {
        isCallMe = callMe;
    }

    public List<Long> getAtQQList() {
        return atQQList;
    }

    public void setAtQQList(List<Long> atQQList) {
        this.atQQList = atQQList;
    }

    public List<String> getImgUrlList() {
        return imgUrlList;
    }

    public void setImgUrlList(List<String> imgUrlList) {
        this.imgUrlList = imgUrlList;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Boolean getReplay() {
        return isReplay;
    }

    public MemberPermission getUserAdmin() {
        return userAdmin;
    }

    public void setUserAdmin(MemberPermission userAdmin) {
        this.userAdmin = userAdmin;
    }

    public void setReplay(Boolean replay) {
        isReplay = replay;
    }

    public String getJSONObjectCTS() { return JSONObjectCTS; }

    public void setJSONObjectCTS(String JSONObjectCTS) { this.JSONObjectCTS = JSONObjectCTS; }

    public String getJSONObjectCTMC() { return JSONObjectCTMC; }

    public void setJSONObjectCTMC(String JSONObjectCTMC) { this.JSONObjectCTMC = JSONObjectCTMC; }

    public String getBotName() { return botName; }

    public void setBotName(String botName) { this.botName = botName; }

    public MemberPermission getBotPermission() { return botPermission; }

    public void setBotPermission(MemberPermission botPermission) { this.botPermission = botPermission; }

    public MessageChain getChain() { return chain; }

    public void setChain(MessageChain chain) { this.chain = chain; }
}
