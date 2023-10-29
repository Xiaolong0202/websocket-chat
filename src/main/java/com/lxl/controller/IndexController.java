package com.lxl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @Author LiuXiaolong
 * @Description test-websocket
 * @DateTime 2023/10/29  20:00
 **/


@Controller
public class IndexController {
    @RequestMapping("/chat")
    public String index(Model model) throws SocketException {
        List<String> localIps = getLocalIps();
        System.out.println(localIps);
        model.addAttribute("serverIp",localIps.get(0));
        return "chatView";
    }

    public List<String> getLocalIps() throws SocketException {
            List<String> ipList = new ArrayList<>();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address && !"127.0.0.1".equals(inetAddress.getHostAddress())) {
                        ipList.add(inetAddress.getHostAddress());
                    }
                }
            }
         return ipList;
    }
}
