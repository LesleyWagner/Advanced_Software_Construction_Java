package minesweeper.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import minesweeper.Board;

/**
 * Plays a game of Minesweeper with the supplied board on a new thread.
 * Receives requests to interact on the blocking queue requests and 
 * gives replies to clients on the blocking queue replies.
 */
public class MinesweeperGameRunnable implements Runnable {
    private final Board board;
    private final boolean debug;
    /** Requests by MinesweeperRunnable instances to access the board. 
     *  Shared with all minesweeperRunnable instances.
     */
    private final BlockingQueue<RequestMessage> requests;   
    /** List of blocking queues indexed by client id.
     *  Shared with all minesweeperRunnable instances.
     */
    private final List<BlockingQueue<ReplyMessage>> replies;
    /** number of connected clients */
    private int players;
    
    public MinesweeperGameRunnable(Board board, boolean debug, BlockingQueue<RequestMessage> requests) {
        this.board = board;
        this.debug = debug;
        this.requests = requests;
        this.replies = new ArrayList<>();
        this.players = 0;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                handleRequest(requests.take());
            }
            catch (InterruptedException ie) {
                ie.printStackTrace();     
            }
        }
    }

    /**
     * Performing requested operations and optionally gives a reply to a client.
     * 
     * @param request to perform an operation
     */
    private void handleRequest(RequestMessage request) {
        if (request instanceof PlayerRequest) { 
            PlayerRequest playerRequest = (PlayerRequest)request;
            BlockingQueue<ReplyMessage> queue = playerRequest.getQueue();
            replies.add(queue); 
            players += 1;

            try {
                queue.put(new HelloMessage(players, board.columns, board.rows));      
            } 
            catch (InterruptedException e) {
                e.printStackTrace();
            }          
        }
        else if (request.getMessage().equals(RequestMessage.Message.BYE)) {
            replies.set(request.getId(), null);
            players -= 1;        }
        else {            
            ReplyMessage reply;
            if (request.getMessage().equals(RequestMessage.Message.LOOK)) {
                reply = new BoardReply(board.toString());
            }
            else if (request.getMessage().equals(RequestMessage.Message.DIG)) {  
                BoardRequest boardRequest = (BoardRequest)request;
                if (board.dig(boardRequest.getX(), boardRequest.getY())) {
                    reply = new BoomReply(debug);
                }
                else {
                    reply = new BoardReply(board.toString());
                }               
            }
            else if (request.getMessage().equals(RequestMessage.Message.FLAG)) {
                BoardRequest boardRequest = (BoardRequest)request;
                board.flag(boardRequest.getX(), boardRequest.getY());
                reply = new BoardReply(board.toString());
            }
            else {
                BoardRequest boardRequest = (BoardRequest)request;
                board.deflag(boardRequest.getX(), boardRequest.getY());
                reply = new BoardReply(board.toString());
            }
            try {
                replies.get(request.getId()).put(reply);
            } 
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
