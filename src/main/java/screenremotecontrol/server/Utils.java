package screenremotecontrol.server;

import java.util.Random;

/**
 * 工具类
 *
 * @Author lrh 2020/9/21 15:30
 */
public class Utils {
    /**
     * 生成客户端唯一的名称标识
     * @Author lrh 2020/9/21 15:34
     */
    public static String getClientName(String clientIp){
        return clientIp.replaceAll("[/.:]","");
    }

    /**   
     * 生成随机的客户端名称，两位随机字母+七位随机数字
     * @Author lrh 2020/9/22 9:50
     */
    public static synchronized String getRandomName(){
        String[] ss ={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            buffer.append(ss[new Random().nextInt(ss.length)]);
        }
        for (int i = 0; i < 7; i++){
            buffer.append(new Random().nextInt(10));
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
//        String clientName = getClientName("/127.0.0.1:62847");
//        System.out.println(clientName);
        String randomName = getRandomName();
        System.out.println(randomName);
    }
}
