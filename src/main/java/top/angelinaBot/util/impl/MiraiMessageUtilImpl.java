package top.angelinaBot.util.impl;


import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.dao.EnableMapper;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.MiraiFrameUtil;
import top.angelinaBot.util.SendMessageUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Mirai发送消息封装方法
 */
@Component("mirai")
@Slf4j
public class MiraiMessageUtilImpl implements SendMessageUtil {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private EnableMapper enableMapper;
    /**
     * 发送群消息方法
     * @param replayInfo 发送消息的结构封装
     */
    @Override
    public void sendGroupMsg(ReplayInfo replayInfo) {
        if (replayInfo == null){
            return;
        }
        activityMapper.sendMessage();
        //解析replayInfo的资源
        String replayMessage = replayInfo.getReplayMessage();
        List<InputStream> replayImgList = replayInfo.getReplayImg();
        InputStream replayAudio = replayInfo.getReplayAudio();
        String kick = replayInfo.getKick();
        Long AT = replayInfo.getAT();
        Integer muted = replayInfo.getMuted();
        Long nudged = replayInfo.getNudged();
        Boolean isMutedAll = replayInfo.getMutedAll();
        Boolean permission = replayInfo.getPermission();
        Integer diceNum = replayInfo.getDice();
        Integer recallTime = replayInfo.getRecallTime();
        Integer quitTime = replayInfo.getQuitTime();

        List<ExternalResource> imgResource = getExternalResource(replayImgList);
        ExternalResource audioResource = null;
        if (replayAudio != null) {
            try {
                audioResource = ExternalResource.create(replayAudio);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (Long groupId : replayInfo.getGroupId()) {
            //如果对象不是关闭群对象，启用发送
            if (this.enableMapper.canUseGroup(groupId, 0) != 1) {
                //获取登录bot
                Bot bot = Bot.getInstance(MiraiFrameUtil.messageIdMap.get(groupId));
                //获取群对象
                Group group = bot.getGroupOrFail(groupId);
                try {
                    MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
                    //@，文字和图像任意出现则创建消息链
                    if (replayMessage != null || AT != null || replayImgList.size() > 0) {

                        //当存在@内容，加入@内容
                        if (AT != null) {
                            messageChainBuilder.append(new At(AT));
                        }
                        //当存在文字内容，加入文字内容
                        if (replayMessage != null) {
                            messageChainBuilder.append(new PlainText(replayMessage));
                        }
                        //分为有图和无图的情况，有图的时候，发送完成消息链以后，需要再次去关闭list中的外部资源，防止内存溢出
                        if (replayImgList.size() > 0) {
                            for (ExternalResource replayImg : imgResource) {
                                messageChainBuilder.append(group.uploadImage(replayImg));
                            }
                            //构建消息链并发送
                            MessageChain chain = messageChainBuilder.build();
                            if (recallTime != null && this.enableMapper.canUseRecall(groupId, 1) == 1) {
                                MessageReceipt<Group> receipt = group.sendMessage(chain);
                                receipt.recallIn(recallTime * 1000);
                            } else group.sendMessage(chain);

                        } else {
                            //构建消息链并发送
                            MessageChain chain = messageChainBuilder.build();
                            group.sendMessage(chain);
                            log.info("发送消息" + replayInfo.getReplayMessage());
                        }
                    }

                    if (kick != null) {
                        //踢出群
                        group.getOrFail(replayInfo.getQq()).kick("");
                    }

                    if (muted != null) {
                        //禁言muted分钟
                        group.getOrFail(replayInfo.getQq()).mute(muted);
                    }

                    if (nudged != null) {
                        //获取戳一戳的群对象
                        Member member = group.getOrFail(nudged);
                        //戳一戳
                        member.nudge().sendTo(group);
                    }

                    if (isMutedAll) {
                        //全体禁言功能开启
                        group.getSettings().setMuteAll(true);
                    }

                    //发送语音
                    if (audioResource != null) {
                        group.sendMessage(group.uploadAudio(audioResource));
                    }

                    if (permission) {
                        MemberPermission memberPermission = group.getBotPermission();
                        String s = memberPermission.getLevel() + "";
                        group.sendMessage(s);
                    }

                    if (diceNum != null) {
                        Dice dice = new Dice(diceNum);
                        group.sendMessage(dice);
                    }

                    if (quitTime != null) {
                        //延时退群
                        if (quitTime > 0) Thread.sleep(quitTime * 1000);
                        group.quit();
                    }

                    log.info("发送消息成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("发送消息失败");
                }
            }
            try {
                Thread.sleep(new Random().nextInt(10)*100);
            }catch (InterruptedException e){
                log.error(e.toString());
            }
        }
        for (ExternalResource replayImg : imgResource) {
            try {
                replayImg.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (audioResource != null) {
            try {
                audioResource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendFriendMsg(ReplayInfo replayInfo) {
        activityMapper.sendMessage();
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<ExternalResource> imgResource = getExternalResource(replayInfo.getReplayImg());
        //获取登录bot
        Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
        //获取用户对象
        User user = bot.getFriendOrFail(replayInfo.getQq());

        //文字和图像任意出现则创建消息链
        if (replayMessage != null || imgResource.size() > 0) {
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            //当存在文字内容，加入文字内容
            if (replayMessage != null) {
                messageChainBuilder.append(new PlainText(replayMessage));
            }
            //当存在图片，加入图片的内容
            if (imgResource.size() > 0) {
                for (ExternalResource replayImg : imgResource) {
                    messageChainBuilder.append(user.uploadImage(replayImg));
                }
            }
            //构建消息链并发送
            MessageChain chain = messageChainBuilder.build();
            user.sendMessage(chain);
        }
        log.info("发送朋友私聊消息" + replayInfo.getReplayMessage());
    }

    @Override
    public void sendStrangerMsg(ReplayInfo replayInfo) {
        activityMapper.sendMessage();
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<ExternalResource> imgResource = getExternalResource(replayInfo.getReplayImg());

        //获取登录bot
        Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
        //获取陌生人对象
        Stranger stranger = bot.getStrangerOrFail(replayInfo.getQq());

        //文字和图像任意出现则创建消息链
        if (replayMessage != null || imgResource.size() > 0){
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            //当存在文字内容，加入文字内容
            if (replayMessage != null) {
                messageChainBuilder.append(new PlainText(replayMessage));
            }
            //当存在图片，加入图片的内容
            if (imgResource.size() > 0) {
                for (ExternalResource replayImg : imgResource) {
                    messageChainBuilder.append(stranger.uploadImage(replayImg));
                }
            }
            //构建消息链并发送
            MessageChain chain = messageChainBuilder.build();
            stranger.sendMessage(chain);
        }
        log.info("发送陌生人私聊消息" + replayInfo.getReplayMessage());
    }

    @Override
    public void sendGroupTempMsg(ReplayInfo replayInfo) {
        activityMapper.sendMessage();
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<ExternalResource> imgResource = getExternalResource(replayInfo.getReplayImg());

        //获取登录bot
        Bot bot = Bot.getInstance(MiraiFrameUtil.messageIdMap.get(replayInfo.getGroupId().get(0)));
        //获取群对象
        Group group = bot.getGroupOrFail(replayInfo.getGroupId().get(0));
        //获取成员对象
        Member member = group.getOrFail(replayInfo.getQq());

        //文字和图像任意出现则创建消息链
        if (replayMessage != null || imgResource.size() > 0){
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            //当存在文字内容，加入文字内容
            if (replayMessage != null) {
                messageChainBuilder.append(new PlainText(replayMessage));
            }
            //当存在图片，加入图片的内容
            if (imgResource.size() > 0) {
                for (ExternalResource replayImg : imgResource) {
                    messageChainBuilder.append(member.uploadImage(replayImg));
                }
            }
            //构建消息链并发送
            MessageChain chain = messageChainBuilder.build();
            member.sendMessage(chain);
        }
        log.info("发送临时会话私聊消息" + replayInfo.getReplayMessage());
    }

    private List<ExternalResource> getExternalResource(List<InputStream> replayImgList) {
        List<ExternalResource> imgResource = new ArrayList<>();
        if (replayImgList.size() > 0) {
            for (InputStream in: replayImgList) {
                try {
                    ExternalResource externalResource = ExternalResource.create(in);
                    imgResource.add(externalResource);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return imgResource;
    }
}
