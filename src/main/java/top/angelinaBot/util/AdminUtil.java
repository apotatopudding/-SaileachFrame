package top.angelinaBot.util;

import java.util.ArrayList;
import java.util.List;


public class AdminUtil {

    public static List<Long> adminList;

    public static void setAdminList(String owner, String[] admin) {
        List<Long> adminList = new ArrayList<>();
        for (String s :admin){
            if (!s.equals("")) adminList.add(Long.valueOf(s));
        }
        if (!owner.equals("") && !adminList.contains(Long.valueOf(owner))){
            adminList.add(Long.valueOf(owner));
        }
        AdminUtil.adminList = adminList;
    }

    public static boolean getAdmin(Long qq) {
        return adminList.contains(qq);
    }

}
