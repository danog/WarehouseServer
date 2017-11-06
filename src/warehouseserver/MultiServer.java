/* 
 * Copyright (C) 2017 Daniil Gentili
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package warehouseserver;

import Main.Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import static java.util.concurrent.Executors.newCachedThreadPool;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Daniil Gentili
 */
public class MultiServer {
    private final Server warehouse;
    private final ServerSocket socketServer;
    private final ExecutorService pool;
    MultiServer(Integer port, Server server) throws IOException {
        this.warehouse = server;
        this.pool = newCachedThreadPool();
        this.socketServer = new ServerSocket(port);
        
    }
    public void run() {
        try {
            while (true) {
                pool.submit(new ConnectionRunnable(socketServer.accept(), warehouse));
            }
        } catch (IOException ex) {
            Logger.getLogger(MultiServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        pool.shutdown();
        try {
            socketServer.close();
        } catch (IOException ex) {
            Logger.getLogger(MultiServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
