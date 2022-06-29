package minesweeper.server;

public class ByeRequest implements RequestMessage {
    private final Message action = RequestMessage.Message.BYE;
    private final int id;
    
    public ByeRequest(int id) {
        this.id = id;
    }
    
    @Override
    public Message getMessage() {
        return action;
    }
    
    @Override
    public int getId() {
        return id;
    }
}
