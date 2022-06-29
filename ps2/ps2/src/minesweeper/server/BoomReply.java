package minesweeper.server;

public class BoomReply implements ReplyMessage {
    public boolean debug;
    
    public BoomReply(boolean flag) {
        this.debug = flag;
    }

    @Override
    public Message getMessage() {
        return ReplyMessage.Message.BOOM;
    }

    @Override
    public String getContent() {
        return "BOOM!";
    }
}
