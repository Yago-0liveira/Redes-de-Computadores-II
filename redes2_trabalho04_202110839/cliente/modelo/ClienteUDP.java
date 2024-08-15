/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 25/06/2024
* Ultima alteracao.: 28/06/2024
* Nome.............: ClienteUDP.java
* Funcao...........: Eh responsavel por enviar a mensagem da aplicacao
*************************************************************** */
package modelo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClienteUDP {
  private String hostname;
  private int port;

  public ClienteUDP(String hostname, int port) {
    this.hostname = hostname;
    this.port = port;
  }

  public void sendMessage(String groupName, String username, String message) throws IOException {
    DatagramSocket socket = new DatagramSocket();
    String sendData = "SEND:" + groupName + ":" + username + ":" + message;
    byte[] buffer = sendData.getBytes();
    InetAddress address = InetAddress.getByName(hostname);
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
    socket.send(packet);
    socket.close();
  }
}
