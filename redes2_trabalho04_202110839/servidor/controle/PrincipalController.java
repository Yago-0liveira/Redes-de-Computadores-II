/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 25/06/2024
* Ultima alteracao.: 27/06/2024
* Nome.............: PrincipalController.java
* Funcao...........: Eh responsavel por lidar com eventos gerados pelos componentes da GUI LayoutPrincipal.fxml.
Eh tambem responsavel por controlar eventos ocorridos nas classes pertencenetes ao pacote modelo, assim como por
atualizar a GUI com dados do programa e o programa com dados advindos da GUI.  
*************************************************************** */

package controle;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import modelo.Servidor;

public class PrincipalController implements Initializable {

  @FXML
  private Button btnComecar;

  @FXML
  private Label labelEndereco;

  @FXML
  private Label labelPorta;

  @FXML
  private ImageView servidorConec;

  @FXML
  private ImageView servidorDesc;

  private Servidor servidor;
  String portaReal;
  int porta = 6789;
  int udpPorta = 6790;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    iniciar();
    servidorConec.setVisible(false);
  }

  @FXML
  void btnHelp(MouseEvent event) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("SINGLE MESSAGE EXCHANGE");
    alert.setHeaderText("AJUDA");
    alert.setContentText(" Clique no botao conectar");

    // Definindo a altura e a largura da caixa de alerta
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.setPrefSize(300, 300); // Defina a largura e a altura desejadas

    alert.showAndWait();
  }

  @FXML
  void ativaServer(MouseEvent event) {
    btnComecar.setVisible(false);
    servidorConec.setVisible(true);
    servidorDesc.setVisible(false);

    Platform.runLater(() -> {
      String endereco = servidor.getEnderecosRede();
      portaReal = Integer.toString(porta);
      if (endereco != null && !endereco.isEmpty()) {
        labelEndereco.setText(endereco);
        labelPorta.setText(portaReal);
      } else {
        labelEndereco.setText("Nenhum endereco de rede encontrado.");
      }
    });
  }

  public void iniciar() {
    servidor = new Servidor(porta, udpPorta);
    // Cria uma nova thread para executar o servidor
    Thread serverThread = new Thread(() -> {
      servidor.execute();
    });
    serverThread.setDaemon(true); // Define a thread como daemon para que nao impeca a JVM de encerrar
    serverThread.start();
  }
}
