package minesweeper.server;

public class LookRequest implements RequestMessage {
    private final Message action = RequestMessage.Message.LOOK;
    private final int id;
    
    public LookRequest(int id) {
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
