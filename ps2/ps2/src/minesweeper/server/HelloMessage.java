package minesweeper.server;

public class HelloMessage implements ReplyMessage {
    private int players;
    private int columns;
    private int rows;
    
    public HelloMessage(int players, int columns, int rows) {
        this.players = players;
        this.columns = columns;
        this.rows = rows;
    }
    
    @Override
    public Message getMessage() {
        return ReplyMessage.Message.HELLO;
    }

    @Override
    public String getContent() {
        return "Welcome to Minesweeper. Players: " + String.valueOf(players) + " including you. Board: " + 
                String.valueOf(columns) + " columns by " + String.valueOf(rows) + " rows. Type 'help' for help.";
    }

}
