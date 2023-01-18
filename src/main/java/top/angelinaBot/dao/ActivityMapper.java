package top.angelinaBot.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityMapper {

    @Insert("insert into a_activity (type) values (0);")
    void getGroupMessage();

    @Insert("insert into a_activity (type) values (1);")
    void getFriendMessage();

    @Insert("insert into a_activity (type) values (2);")
    void getEventMessage();

    @Insert("insert into a_activity (type) values (3);")
    void sendMessage();

    @Select("CREATE TABLE IF NOT EXISTS `a_activity`  (\n" +
            "        `type` int(255) NOT NULL,\n" +
            "        `time` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP\n" +
            "        );")
    void initActivityTable();

//    @Delete("delete from a_activity where time = ; datetime('now','-1 day');")
//    void clearActivity();
}
