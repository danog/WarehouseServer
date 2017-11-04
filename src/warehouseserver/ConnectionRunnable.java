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

import Main.ClientException;
import Main.Server;
import Payloads.ServerException;
import Payloads.RequestPayload;
import Payloads.ResponsePayload;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Daniil Gentili
 */
public class ConnectionRunnable implements Runnable {

    BufferedReader input;
    BufferedWriter output;
    Socket client;
    Server warehouse;
    Boolean run = true;
    String path = "/root/NetBeansProjects/WarehouseServer/src/warehouseserver/warehouse.txt";

    public ConnectionRunnable(Socket client, Server warehouse) throws IOException {
        this.client = client;
        this.warehouse = warehouse;
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
    }

    @Override
    public void run() {
        String buffer = "";
        while (run) {
            try {
                buffer = this.handlePayload(new RequestPayload(input)).toString();
            } catch (IOException ex) {
                Logger.getLogger(ConnectionRunnable.class.getName()).log(Level.SEVERE, null, ex);
                buffer = new ResponsePayload(500, "Internal server error").toString();
            } catch (ServerException e) {
                buffer = e.getPayload().toString();
            }
            try {
                output.write(buffer);
                output.flush();
            } catch (IOException ex) {
                Logger.getLogger(ConnectionRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ResponsePayload handlePayload(RequestPayload request) throws ServerException, IOException {
        if (!"HTTP".equals(request.getProtocol())) {
            throw new ServerException(request, 400, "Bad request", "Wrong protocol");
        }
        run = request.shouldKeepAlive();

        switch (request.getMethod()) {
            case "GET":
                if (!"/".equals(request.getURI())) {
                    throw new ServerException(request, 404, "File not found", "Database not found");
                }
                return new ResponsePayload(request, 200, "OK", getDatabase());
            case "POST":
                if (!"/".equals(request.getURI())) {
                    System.out.println(String.format("%s spent %s", client.getInetAddress().getCanonicalHostName(), request.getPayload()));
                    return new ResponsePayload(request, 200, "OK");
                } else if ("/".equals(request.getURI())) {
                    try {
                        return new ResponsePayload(request, 200, "OK", updateDatabase(request.getPayload()));
                    } catch (ClientException ex) {
                        throw new ServerException(request, 500, ex.getMessage(), getDatabase());
                    }
                } else {
                    throw new ServerException(request, 404, "File not found", "Database not found");
                }
            default:
                throw new ServerException(request, 400, "Bad Request", "Bad HTTP method");

        }

    }

    synchronized private String updateDatabase(String input) throws ClientException, IOException {
        warehouse.checkout(input);
        return warehouse.getWarehouse().getPayload();
    }

    synchronized private String getDatabase() {
        return warehouse.getWarehouse().getPayload();
    }

}
