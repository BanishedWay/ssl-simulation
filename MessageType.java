package message;

public class MessageType {
    public byte[] random = null;
    public byte[] cipherSuite = null;
    public byte[] certificate = null;
    public byte[] signiture = null;
    public byte[] encryptedSharedSecret = null;
    public byte[] message_MAC = null;
    int length;

    public MessageType() {
        length = 0;
    }

    public int getLength() {
        return length;
    }
}

class client_hello extends MessageType {

    client_hello() {
        super();
        random = new byte[32];
        length = 32;
    }
}

class server_hello extends MessageType {
    byte cipherSuite;

    server_hello() {
        super();
        random = new byte[32];
        length = 32;
    }
}

class server_certificate extends MessageType {

    server_certificate() {
        super();
        certificate = new byte[256];
        length = 256;
    }
}

class client_certificate extends MessageType {

    client_certificate() {
        super();
        certificate = new byte[256];
        length = 256;
    }
}

class certificate_verify extends MessageType {

    certificate_verify() {
        super();
        signiture = new byte[256];
        length = 256;
    }
}

class client_key_exchange extends MessageType {

    client_key_exchange() {
        super();
        encryptedSharedSecret = new byte[48];
        length = 48;
    }
}

class server_finished extends MessageType {

    server_finished() {
        super();
        message_MAC = new byte[32];
        length = 32;
    }
}

class client_finished extends MessageType {

    client_finished() {
        super();
        message_MAC = new byte[32];
        length = 32;
    }
}

class error_message extends MessageType {
    byte body;
    int length;

    error_message() {
        super();
    }
}