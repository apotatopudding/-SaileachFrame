package top.angelinaBot.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMapper {

    @Insert("insert into a_group_func_close (group_id, func_name) values (#{groupId}, #{funcName});")
    void closeFunction(@Param("groupId") Long groupId, @Param("funcName") String funcName);

    @Insert("insert into a_group_func_close (group_id, func_name) values (999999999, #{funcName});")
    void closeAllFunction(@Param("funcName") String funcName);

    @Delete("delete from a_group_func_close where group_id=#{groupId} and func_name=#{funcName};")
    void openFunction(@Param("groupId") Long groupId, @Param("funcName") String funcName);

    @Delete("delete from a_group_func_close where group_id=999999999 and func_name=#{funcName};")
    void openAllFunction(@Param("funcName") String funcName);

    @Select("select count(group_id) from a_group_func_close where group_id=#{groupId} and func_name=#{funcName};")
    Integer canUseFunction(@Param("groupId") Long groupId, @Param("funcName") String funcName);

    @Select("select func_name from a_group_func_close where group_id=#{groupId};")
    List<String> getCloseFunction(@Param("groupId") Long groupId);

    @Select("CREATE TABLE IF NOT EXISTS `a_group_func_close`  (\n" +
            "        `group_id` bigInt(255) NOT NULL,\n" +
            "        `func_name` varchar(6) NOT NULL DEFAULT CURRENT_TIMESTAMP\n" +
            "        );")
    void initAdminTable();

}
