/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 25/06/2024
* Ultima alteracao.: 28/06/2024
* Nome.............: ClienteTCP.java
* Funcao...........: Eh responsavel por estabelecer conexao, entrar e sair de grupos. 
*************************************************************** */
package modelo;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ClienteTCP {
  private String hostname;
  private int port;
  private Socket socket;
  private PrintWriter writer;
  private BufferedReader reader;

  public ClienteTCP(String hostname, int port) {
    this.hostname = hostname;
    this.port = port;
  }

  public void connect() throws IOException {
    socket = new Socket(hostname, port);
    writer = new PrintWriter(socket.getOutputStream(), true);
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
  }

  public void joinGroup(String groupName, String username) {
    writer.println("JOIN:" + groupName + ":" + username);
  }

  public void leaveGroup(String groupName, String username) {
    writer.println("LEAVE:" + groupName + ":" + username);
  }

  public void close() throws IOException {
    if (socket != null) {
      socket.close();
    }
  }

  public void startListening(Consumer<String> messageCallback) {
    new Thread(() -> {
      try {
        String serverMessage;
        while ((serverMessage = reader.readLine()) != null) {
          messageCallback.accept(serverMessage);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }

}
