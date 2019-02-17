package A_auth_certif.objects;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public interface MyAPI {

     static String getMac() {
        InetAddress ip;
        String mac_str = "";
        try {

            ip = InetAddress.getLocalHost();
            // System.out.println("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            // System.out.print("Current MAC address : ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            // System.out.println(sb.toString());
            mac_str = sb.toString();

        } catch (UnknownHostException | SocketException e) {

            e.printStackTrace();

        }

         return mac_str;
    }
}
