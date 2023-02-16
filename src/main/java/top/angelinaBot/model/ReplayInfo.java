package top.angelinaBot.model;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.Dice;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * 回复消息结构化Bean
 **/
@Slf4j
public class ReplayInfo {
    //登录QQ
    Long loginQQ;
    //qq
    Long qq;
    //昵称
    String name;
    //待发送的目标群号
    List<Long> groupId = new ArrayList<>();
    //文字内容
    String replayMessage;
    //图片内容
    List<InputStream> replayImg = new ArrayList<>();
    //语音内容
    InputStream replayAudio;
    //踢出群
    Long AT;
    //获取@的人
    String kick;
    //禁言
    Integer muted;
    //禁言全体
    Boolean isMutedAll = false;
    //戳一戳
    Long nudged;
    //查询机器人权限
    Boolean permission = false;
    //发送一个随机骰子消息
    Integer dice;
    //撤回时间（目前仅设置给带图消息）
    Integer recallTime;
    //退群延时
    Integer quitTime;

    public ReplayInfo(MessageInfo messageInfo) {
        this.loginQQ = messageInfo.getLoginQq();
        if(messageInfo.getGroupId()!=null) this.setGroupId(messageInfo.getGroupId());
        this.qq = messageInfo.getQq();
        this.name = messageInfo.getName();
    }

    public ReplayInfo() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLoginQQ() {
        return loginQQ;
    }

    public void setLoginQQ(Long loginQQ) {
        this.loginQQ = loginQQ;
    }

    public Long getQq() {
        return qq;
    }

    public void setQq(Long qq) {
        this.qq = qq;
    }

    public List<Long> getGroupId() { return groupId; }

    public void setGroupId(Long groupId) {
        this.groupId.clear();
        this.groupId.add(groupId);
    }

    public void addGroupId(Long groupId) { this.groupId.add(groupId); }

    public void setGroupId(List<Long> groupIds) { this.groupId.addAll(groupIds); }

    public Long getAT() { return AT; }

    public void setAT(Long AT) { this.AT = AT; }

    public String getKick() {
        return kick;
    }

    public void setKick(String kick) {
        this.kick = kick;
    }

    public Integer getMuted() {
        return muted;
    }

    public void setMuted(Integer muted) {
        this.muted = muted;
    }

    public Boolean getMutedAll() { return isMutedAll; }

    public void setMutedAll(Boolean mutedAll) { isMutedAll = mutedAll; }

    public Long getNudged() { return nudged; }

    public void setNudged(Long nudged) { this.nudged = nudged; }

    public String getReplayMessage() {
        return replayMessage;
    }

    public void setReplayMessage(String replayMessage) {
        this.replayMessage = replayMessage;
    }

    public Boolean getPermission() { return permission; }

    public void setPermission(Boolean permission) { this.permission = permission; }

    public Integer getDice() { return dice; }

    public void setDice(Integer dice) { this.dice = dice; }

    public Integer getRecallTime() { return recallTime; }

    public void setRecallTime(Integer recallTime) { this.recallTime = recallTime; }

    public Integer getQuitTime() { return quitTime; }

    public void setQuitTime(Integer quitTime) { this.quitTime = quitTime; }

    /**
     * 获取ReplayInfo的图片集合
     * @return 返回图片的输入流集合
     */
    public List<InputStream> getReplayImg() { return replayImg; }

    /**
     * 以BufferImage格式插入图片
     * @param bufferedImage 图片BufferedImage
     */
    public void setReplayImg(BufferedImage bufferedImage) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
            ImageIO.write(bufferedImage, "jpg", os);
            InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
            replayImg.add(inputStream);
            inputStream.close();
        } catch (IOException e) {
            log.error("BufferImage读取IO流失败");
        }
    }

    public void setReplayImg(BufferedImage bufferedImage,String format) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
            ImageIO.write(bufferedImage, format, os);
            InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
            replayImg.add(inputStream);
            inputStream.close();
        } catch (IOException e) {
            log.error("BufferImage读取IO流失败");
        }
    }

    /**
     * 以文件格式插入图片
     * @param file 文件File
     */
    public void setReplayImg(File file) {
        try {
            InputStream inputStream = Files.newInputStream(file.toPath());
            replayImg.add(inputStream);
        } catch (IOException e) {
            log.error("File读取IO流失败");
        }
    }

    /**
     * 以文件流形式插入图片
     * @param inputStream 输入图片的图片流
     */
    public void setReplayImg(InputStream inputStream) {
            replayImg.add(inputStream);
    }

    /**
     * 以url形式插入图片
     * @param url 图片url
     */
    public void setReplayImg(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection httpUrl = (HttpURLConnection) u.openConnection();
            httpUrl.connect();
            InputStream inputStream = httpUrl.getInputStream();
            replayImg.add(inputStream);
        } catch (IOException e) {
            log.error("读取图片URL失败");
        }
    }

    /**
     * 获取ReplayInfo的语音集合
     * @return 返回语音的输入流集合
     */
    public InputStream getReplayAudio() {
        return replayAudio;
    }

    /**
     * 以文件格式插入语音
     * @param file 文件File
     */
    public void setReplayAudio(File file) {
        try {
            this.replayAudio = Files.newInputStream(file.toPath());
        } catch (IOException e) {
            log.error("File读取IO流失败");
        }
    }

    public void setReplayAudio(String replayAudio) {
        try {
            this.replayAudio = Files.newInputStream(Paths.get(replayAudio));
        } catch (IOException e) {
            log.error("File读取IO流失败");
        }
    }

}

