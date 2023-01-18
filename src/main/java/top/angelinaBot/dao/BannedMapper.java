package top.angelinaBot.dao;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannedMapper {
    //查询群组聊天
    @Select({"select muted_word from a_banned_keyword ;"})
    List<String> getMuted();
}
