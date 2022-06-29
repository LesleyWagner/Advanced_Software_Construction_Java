package minesweeper.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * Minesweeper Runnable. For every incoming client connection
 * a new thread is created with a Minesweeper Runnable object, 
 * and the run method is immediately called. The run method handles 
 * the connection between the server and that client.
 */
public class MinesweeperRunnable implements Runnable {
    private final Socket socket;
    private final BlockingQueue<RequestMessage> requests;
    private final BlockingQueue<ReplyMessage> replies;
    private final int id;
    
    public MinesweeperRunnable(Socket socket, BlockingQueue<RequestMessage> requests, BlockingQueue<ReplyMessage> replies, int id) {
        this.socket = socket;
        this.requests = requests;
        this.replies = replies;
        this.id = id;
    }
    
    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            out.println(replies.take().getContent()); // hello message
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                String output = handleRequest(line);
                if (output.equals("bye")) {
                    break;
                }
                else if (output.equals("BOOM! over")) {
                    requests.put(new ByeRequest(id));
                    out.println("BOOM!");
                    break;
                }
                else if (!output.equals("no message")) {
                    out.println(output);
                }
            }
        }  
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            try {
                socket.close();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handler for client input, performing requested operations and returning an output message.
     * 
     * @param input message from client
     * @return message to client, or "no message" if none
     */
    private String handleRequest(String input) throws InterruptedException {
        String regex = "(look)|(help)|(bye)|"
                     + "(dig -?\\d+ -?\\d+)|(flag -?\\d+ -?\\d+)|(deflag -?\\d+ -?\\d+)";
        if ( ! input.matches(regex)) {
            return "no message";
        }
        String[] tokens = input.split(" ");
        if (tokens[0].equals("look")) {   
            requests.put(new LookRequest(id));
            return getReply();
        } 
        else if (tokens[0].equals("help")) {
            return "You can send look, dig, flag, deflag commands to interact with the board; "
                    + "a help command to get a help message or a bye command to terminate the connection.";
        } 
        else if (tokens[0].equals("bye")) {
            requests.put(new ByeRequest(id));
            return "bye";
        } 
        else {
            int x = Integer.parseInt(tokens[1]);
            int y = Integer.parseInt(tokens[2]);
            if (tokens[0].equals("dig")) {
                requests.put(new BoardRequest(RequestMessage.Message.DIG, x, y, id));
                return getReply();
            } 
            else if (tokens[0].equals("flag")) {
                requests.put(new BoardRequest(RequestMessage.Message.FLAG, x, y, id));
                return getReply();
            } 
            else if (tokens[0].equals("deflag")) {
                requests.put(new BoardRequest(RequestMessage.Message.DEFLAG, x, y, id));
                return getReply();
            }
        }
        // Should never get here
        throw new UnsupportedOperationException();
    }
    
    /**
     * Waits for reply on the queue to arrive and returns the content of the reply.
     * 
     * @return content of the reply
     */    
    private String getReply() throws InterruptedException {
        ReplyMessage reply = replies.take();
        String content = reply.getContent();
        if (reply.getMessage().equals(ReplyMessage.Message.BOOM)) {
            BoomReply boom = (BoomReply)reply;
            if (!boom.debug) {
                content += " over";
            }
        }
        return content;
    }
}
