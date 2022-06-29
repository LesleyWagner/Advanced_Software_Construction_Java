package minesweeper.server;

public class BoardReply implements ReplyMessage {
    private String content;
    
    public BoardReply(String board) {
        this.content = board;
    }

    @Override
    public Message getMessage() {
        return ReplyMessage.Message.BOARD;
    }

    @Override
    public String getContent() {
        return content;
    }
}
