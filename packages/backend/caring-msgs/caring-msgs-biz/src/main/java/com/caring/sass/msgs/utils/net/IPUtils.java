//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.caring.sass.msgs.utils.net;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IPUtils {
    public IPUtils() {
    }

    public static List<String> doman2ip(String doman) throws Exception {
        ArrayList<String> list = new ArrayList();
        InetAddress[] addresses = InetAddress.getAllByName(doman);
        Arrays.stream(addresses).forEach((line) -> {
            list.add(line.getHostAddress());
        });
        return list;
    }

    public static void main(String[] args) throws Exception {
        List<String> list = doman2ip("tracker.xiaoqiedu.cn");
        System.out.println((String)list.get(0));
    }
}
