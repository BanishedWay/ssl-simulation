package work;

import message.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;

public class Server {
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

    private static int port = 15573;

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(15573);
        System.out.println("等待连接....");
        Socket clientSocket = serverSocket.accept();


//        BufferedReader in =new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true);
        OutputStream out = clientSocket.getOutputStream();
        BufferedInputStream in = new BufferedInputStream(clientSocket.getInputStream());

        byte[] client_hello, client_certificate, client_finished;
        //client_hello消息、client_certificate消息、client_finished消息
        byte[] server_hello, server_certificate, server_finished;
        //server_hello消息, server_certificate消息, server_finished消息
        byte[] master_secret, signiture, encryptedSharedSecret;

        while (true) {
            try {
                //接收client_hello消息
                client_hello = new byte[32];
                in.read(client_hello);
                System.out.println(convertBytesToHex(client_hello));

                //创建server_client对象并发送32字节随机数数组给客户端
                Message message_server_hello = new Message(messageTypes.server_hello);
                message_server_hello.getMessageType().random = generateRandom(32);
                server_hello = message_server_hello.getMessageType().random;
                out.write(server_hello);
                out.flush();
                System.out.println(convertBytesToHex(server_hello));

                /*
                传输server_certificate
                 */
                Message message_server_certificate = new Message(messageTypes.server_certificate);
                message_server_certificate.getMessageType().certificate = generateRandom(256);//此处为传输的用户验证RSA公钥，可以使用随机数生成器生成，也可以使用编写的RSA算法中设定的密钥，用于加密共享秘密，即master_secret
                server_certificate = message_server_certificate.getMessageType().certificate;
                out.write(server_certificate);
                out.flush();
                System.out.println(convertBytesToHex(server_certificate));

                /*接收client_certificate、client_certificate_verify、server_key_exchange
                 */
                //接收client_certificate
                client_certificate = new byte[256];
                in.read(client_certificate);
                System.out.println(convertBytesToHex(client_certificate));

                //接收server_key_exchange
                encryptedSharedSecret = new byte[48];
                in.read(encryptedSharedSecret);
                System.out.println(convertBytesToHex(encryptedSharedSecret));
                master_secret = encryptedSharedSecret;
                /*
                在这里添加解密算法调用，对encryptedSharedSecret进行解密
                得到master_secret，并删去或替换上一条语句。
                 */

                //接收client_certificate_verify
                signiture = new byte[32];
                in.read(signiture);
                System.out.println(convertBytesToHex(signiture));
                /*
                对签名进行验证···
                 */

                //创建server_finished(32位)
                Message message_server_finished = new Message(messageTypes.server_finished);
                /*message_server_finished.getMessageType().message_MAC=
                 这一段在前一句加上编写的HMAC算法，server_finished用于存储结果
                 */
                server_finished = message_server_finished.getMessageType().message_MAC;
                out.write(server_finished);
                out.flush();
                System.out.println(convertBytesToHex(server_finished));

                //接收client_finished
                client_finished = new byte[32];
                in.read(client_finished);
                System.out.println(convertBytesToHex(client_finished));

                /*
                握手协议结束
                通过编写的HMAC协议计算得到会话密钥，进入记录层协议开展信息交互
                 */

//                System.exit(0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
