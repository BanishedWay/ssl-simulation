package message;

public class Message {
    public MessageType messageType;

    public Message(messageTypes messageTypes) {
        switch (messageTypes) {
            case client_hello:
                messageType = new client_hello();
                break;
            case server_hello:
                messageType = new server_hello();
            case server_certificate:
                messageType = new server_certificate();
            case client_certificate:
                messageType=new client_certificate();
            case certificate_verify:
                messageType=new certificate_verify();
            case client_key_exchange:
                messageType=new client_key_exchange();
            case server_finished:
                messageType=new server_finished();
            case client_finished:
                messageType=new client_finished();
        }
    }

    public MessageType getMessageType(){
        return messageType;
    }
}
