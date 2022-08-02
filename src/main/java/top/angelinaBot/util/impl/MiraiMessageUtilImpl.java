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
import top.angelinaBot.util.SendMessageUtil;

import java.util.List;

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
        activityMapper.sendMessage();
        try {
            //获取登录bot
            Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
            //获取群对象
            Group group = bot.getGroupOrFail(replayInfo.getGroupId());
            //如果对象是关闭群对象，清空所有消息
            if (this.enableMapper.canUseGroup(replayInfo.getGroupId(), 0) == 1){
                replayInfo=new ReplayInfo();
            }
            //解析replayInfo
            String replayMessage = replayInfo.getReplayMessage();
            List<ExternalResource> replayImgList = replayInfo.getReplayImg();
            List<ExternalResource> replayAudioList = replayInfo.getReplayAudio();
            String kick = replayInfo.getKick();
            Long AT = replayInfo.getAT();
            Integer muted = replayInfo.getMuted();
            Long nudged = replayInfo.getNudged();
            Boolean isMutedAll = replayInfo.getMutedAll();
            Boolean permission = replayInfo.getPermission();
            Dice dice = replayInfo.getDice();
            Integer recallTime = replayInfo.getRecallTime();


            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            //@，文字和图像任意出现则创建消息链
            if (replayMessage != null || AT != null || replayImgList.size() > 0) {

                //当存在@内容，加入@内容
                if (AT != null) {
                    messageChainBuilder.append(new At(AT));
                }
                //当存在文字内容，加入文字内容
                if (replayMessage != null) {
                    if (replayMessage.contains("杰哥口我") || replayMessage.contains("洁哥口我") || replayMessage.contains("安洁莉娜口我")) {
                        replayMessage = "达咩";
                    }
                    messageChainBuilder.append(new PlainText(replayMessage));
                }
                //分为有图和无图的情况，有图的时候，发送完成消息链以后，需要再次去关闭list中的外部资源，防止内存溢出
                if (replayImgList.size() > 0) {
                    for (ExternalResource replayImg : replayImgList) {
                        messageChainBuilder.append(group.uploadImage(replayImg));
                    }
                    //构建消息链并发送
                    MessageChain chain = messageChainBuilder.build();
                    if (recallTime != null) {
                        MessageReceipt<Group> receipt = group.sendMessage(chain);
                        receipt.recallIn(recallTime * 1000);
                    } else group.sendMessage(chain);
                    for (ExternalResource replayImg : replayImgList) {
                        replayImg.close();
                    }
                } else {
                    //构建消息链并发送
                    MessageChain chain = messageChainBuilder.build();
                    group.sendMessage(chain);
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

            if (replayAudioList.size() > 0) {
                //发送语音
                for (ExternalResource replayAudio : replayAudioList) {
                    group.sendMessage(group.uploadAudio(replayAudio));
                    replayAudio.close();
                }
            }

            if (permission) {
                MemberPermission memberPermission = group.getBotPermission();
                String s = memberPermission.getLevel() + "";
                group.sendMessage(s);
            }

            if (dice != null) {
                group.sendMessage(dice);
            }

            log.info("发送消息" + replayInfo.getReplayMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送消息失败");
        }
    }

    @Override
    public void sendFriendMsg(ReplayInfo replayInfo) {
        activityMapper.sendMessage();
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<ExternalResource> replayImgList = replayInfo.getReplayImg();

        //获取登录bot
        Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
        //获取用户对象
        User user = bot.getFriendOrFail(replayInfo.getQq());

        //文字和图像任意出现则创建消息链
        if (replayMessage != null || replayImgList.size() > 0) {
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            //当存在文字内容，加入文字内容
            if (replayMessage != null) {
                messageChainBuilder.append(new PlainText(replayMessage));
            }
            //当存在图片，加入图片的内容
            if (replayImgList.size() > 0) {
                for (ExternalResource replayImg : replayImgList) {
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
        List<ExternalResource> replayImgList = replayInfo.getReplayImg();

        //获取登录bot
        Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
        //获取陌生人对象
        Stranger stranger = bot.getStrangerOrFail(replayInfo.getQq());

        //文字和图像任意出现则创建消息链
        if (replayMessage != null || replayImgList.size() > 0){
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            //当存在文字内容，加入文字内容
            if (replayMessage != null) {
                messageChainBuilder.append(new PlainText(replayMessage));
            }
            //当存在图片，加入图片的内容
            if (replayImgList.size() > 0) {
                for (ExternalResource replayImg : replayImgList) {
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
        List<ExternalResource> replayImgList = replayInfo.getReplayImg();

        //获取登录bot
        Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
        //获取群对象
        Group group = bot.getGroupOrFail(replayInfo.getGroupId());
        //获取成员对象
        Member member = group.getOrFail(replayInfo.getQq());

        //文字和图像任意出现则创建消息链
        if (replayMessage != null || replayImgList.size() > 0){
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            //当存在文字内容，加入文字内容
            if (replayMessage != null) {
                messageChainBuilder.append(new PlainText(replayMessage));
            }
            //当存在图片，加入图片的内容
            if (replayImgList.size() > 0) {
                for (ExternalResource replayImg : replayImgList) {
                    messageChainBuilder.append(member.uploadImage(replayImg));
                }
            }
            //构建消息链并发送
            MessageChain chain = messageChainBuilder.build();
            member.sendMessage(chain);
        }
        log.info("发送临时会话私聊消息" + replayInfo.getReplayMessage());
    }
}
