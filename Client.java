package work;

import message.*;

import java.io.*;
import java.net.Socket;
import java.security.*;

public class Client {
    public static byte[] generateRandom(int length) throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return bytes;
    }

    public static String convertBytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte temp : bytes) {
            result.append(String.format("%02x", temp));
        }
        return result.toString();
    }

    public static void main(String[] args) throws Exception {

        Socket serverSocket = new Socket("localhost", 15573);
        OutputStream out = serverSocket.getOutputStream();
        BufferedInputStream in = new BufferedInputStream(serverSocket.getInputStream());

        byte[] client_hello, client_certificate, client_finished;
        //client_hello消息、client_certificate消息、client_finished消息
        byte[] server_hello, server_certificate, server_finished;
        //server_hello消息, server_certificate消息, server_finished消息
        byte[] master_secret, signiture, encryptedSharedSecret;

        while (true) {
            try {
                //创建client_hello消息对象·····
                Message message_client_hello = new Message(messageTypes.client_hello);
                message_client_hello.getMessageType().random = generateRandom(32);
                client_hello = message_client_hello.getMessageType().random;
                out.write(client_hello);
                out.flush();
                System.out.println(convertBytesToHex(client_hello));

                //接收从服务器传输的server_hello消息并保存在byte数组中
                server_hello = new byte[32];
                in.read(server_hello);
                System.out.println(convertBytesToHex(server_hello));

                /*
                接收server_certificate
                 */
                server_certificate = new byte[256];
                in.read(server_certificate);
                System.out.println(convertBytesToHex(server_certificate));

                //生成client_certificate·····
                Message message_client_certificate = new Message(messageTypes.client_certificate);
                message_client_certificate.getMessageType().certificate = generateRandom(256);//客户端的RSA公钥，验证certificate_verify
                client_certificate = message_client_certificate.getMessageType().certificate;
                out.write(client_certificate);
                out.flush();
                System.out.println(convertBytesToHex(client_certificate));

                //生成共享主秘密master_secret(ClientKeyExchange)
                Message message_client_key_exchange = new Message(messageTypes.client_key_exchange);
                message_client_key_exchange.getMessageType().encryptedSharedSecret = generateRandom(48);
                master_secret = message_client_key_exchange.getMessageType().encryptedSharedSecret;
                //对master_secret(48位)进行加密，存入encryptedSharedSecret
                /*
                此处填写对master_secret进行加密的步骤
                 */
                encryptedSharedSecret = master_secret;//这一步是为了保证程序运转不报错，无实际意义，在添加加密步骤后替换或删除
                //同时encryptedSharedSecret不一定是48位，依照编写的加密算法进行适当的调整
                out.write(encryptedSharedSecret);
                out.flush();
                System.out.println(convertBytesToHex(encryptedSharedSecret));


                //生成certificate_verify消息，客户端使用客户端的RSA私钥对client_hello数组和server_hello数组中的消息连接后的byte数组进行签名，这里得到的签名结果长度是多少我也不清楚了，暂时用的256，如果有问题可以在MessageType.java中class certificate_verify中的数组长度进行更改，并告诉我在我的本地电脑上进行更改
                Message message_certificate_verify = new Message(messageTypes.server_certificate);
/*                message_certificate_verify.getMessageType().signiture=
                  这一段在前一句加上编写的签名算法，signiture用于存储结果
 */

                signiture = message_certificate_verify.getMessageType().signiture;
                out.write(signiture);
                out.flush();
                System.out.println(convertBytesToHex(signiture));

                //接收server_finished
                server_finished = new byte[32];
                in.read(server_finished);
                System.out.println(convertBytesToHex(server_finished));

                //创建client_finished
                Message message_client_finished = new Message(messageTypes.client_finished);
                /*message_server_finished.getMessageType().message_MAC=
                 这一段在前一句加上编写的HMAC算法，server_finished用于存储结果
                 */
                client_finished = message_client_finished.getMessageType().message_MAC;
                out.write(client_finished);
                out.flush();
                System.out.println(convertBytesToHex(client_finished));

//                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
