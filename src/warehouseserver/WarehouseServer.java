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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Daniil Gentili
 */
public class WarehouseServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new MultiServer(9090, new Server("/root/NetBeansProjects/WarehouseServer/src/warehouseserver/warehouse.txt")).run();
        } catch (IOException ex) {
            Logger.getLogger(WarehouseServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
