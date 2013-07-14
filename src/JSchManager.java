
/*
 * JSchManager
 * Simmple SSH connection
 * for command line commands
 * 
 * 
 * 
 */
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ahmedtalha
 */
public class JSchManager {

    static Session session = null;

    public JSchManager() {
    }

    public void createSSHConnection(String userName, String psw, String host, int port) {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(userName, host, port);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(psw);
            session.connect();
        } catch (JSchException ex) {
            Logger.getLogger(JSchManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void execConsoleCommand(String command) {
        InputStream in = null;
        Channel channel = null;
        try {

            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            channel.setInputStream(null);
            in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];

            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    System.out.println(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(JSchManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSchException ex) {
            Logger.getLogger(JSchManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(JSchManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        channel.disconnect();
    }

    public void closeSSHConnection() {

        session.disconnect();
    }
}
