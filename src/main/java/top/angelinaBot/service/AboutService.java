package top.angelinaBot.service;

import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;

@Service
public class AboutService {

    @AngelinaGroup(keyWords = {"关于"}, description = "框架地址源码", sort = "框架基础功能")
    public ReplayInfo getAbout(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        replayInfo.setReplayMessage("框架已做自定义改编，框架项目地址：https://github.com/apotatopudding/SaileachFrame" +
                "原洁哥框架项目地址:https://github.com/Strelizia02/AngelinaFrame");
        return replayInfo;
    }
}
