package top.angelinaBot.dao;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface EnableMapper {

    //开关某群组聊天，默认开启
    @Insert({"insert into a_group_close (group_id, group_close)\n" +
            " VALUES (#{groupId}, #{groupClose})\n" +
            " ON CONFLICT(group_id)\n" +
            " DO update set\n" +
            " group_id = #{groupId}, group_close = #{groupClose};"})
    void closeGroup(@Param("groupId") Long groupId, @Param("groupClose") Integer groupClose);

    //开关Bilibili解析，默认关闭
    @Insert({"insert into a_group_close (group_id, bilibili_close)\n" +
            " VALUES (#{groupId}, #{bilibiliClose})\n" +
            " ON CONFLICT(group_id)\n" +
            " DO update set\n" +
            " group_id = #{groupId}, bilibili_close = #{bilibiliClose};"})
    void closeBilibili(@Param("groupId") Long groupId, @Param("bilibiliClose") Integer bilibiliClose);

    //开关截图解析，默认关闭
    @Insert({"insert into a_group_close (group_id, dhash_close)\n" +
            " VALUES (#{groupId}, #{dHashClose})\n" +
            " ON CONFLICT(group_id)\n" +
            " DO update set\n" +
            " group_id = #{groupId}, dhash_close = #{dHashClose};"})
    void closeDHash(@Param("groupId") Long groupId, @Param("dHashClose") Integer dHashClose);

    @Insert({"insert into a_group_close (group_id, recall_close)\n" +
            " VALUES (#{groupId}, #{recallClose})\n" +
            " ON CONFLICT(group_id)\n" +
            " DO update set\n" +
            " group_id = #{groupId}, recall_close = #{recallClose};"})
    void closeRecall(@Param("groupId") Long groupId, @Param("recallClose") Integer recallClose);


    //查询群组聊天关闭情况
    @Select({"select count(group_id) from a_group_close where group_id=#{groupId} and group_close=#{groupClose};"})
    Integer canUseGroup(@Param("groupId") Long groupId, @Param("groupClose") Integer groupClose);

    //查询Bilibili解析关闭情况
    @Select({"select count(group_id) from a_group_close where group_id=#{groupId} and bilibili_close=#{bilibiliClose};"})
    Integer canUseBilibili(@Param("groupId") Long groupId, @Param("bilibiliClose") Integer bilibiliClose);

    //查询截图解析关闭情况
    @Select({"select count(group_id) from a_group_close where group_id=#{groupId} and dhash_close=#{dHashClose};"})
    Integer canUseDHash(@Param("groupId") Long groupId, @Param("dHashClose") Integer dHashClose);

    //查询自动撤回是否开启
    @Select({"select count(group_id) from a_group_close where group_id=#{groupId} and recall_close=#{recallClose};"})
    Integer canUseRecall(@Param("groupId") Long groupId, @Param("recallClose") Integer recallClose);

}
