package minesweeper.server;

public interface ReplyMessage {
    public static enum Message { BOARD, BOOM, HELLO };
    
    public Message getMessage();
    
    public String getContent();
}
