/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 25/06/2024
* Ultima alteracao.: 29/06/2024
* Nome.............: PrincipalControllerCliente.java
* Funcao...........: Eh responsavel por lidar com eventos gerados pelos componentes da GUI LayoutCliente.fxml.
Eh tambem responsavel por controlar eventos ocorridos nas classes pertencenetes ao pacote modelo, assim como por
atualizar a GUI com dados do programa e o programa com dados advindos da GUI.  
*************************************************************** */
package controle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import modelo.ClienteTCP;
import modelo.ClienteUDP;

public class PrincipalControllerCliente implements Initializable {

  @FXML
  private Group telaInicial;

  @FXML
  private Button botaoComecar;

  @FXML
  private TextField textEndereco;

  @FXML
  private TextField textEntrar;

  @FXML
  private TextField textNome;

  @FXML
  private TextField textPorta;

  @FXML
  private Label NomeGrupo;

  @FXML
  private VBox vboxMensagens;

  @FXML
  private VBox vboxGrupos;

  @FXML
  private ScrollPane scroolMensagens;

  @FXML
  private ScrollPane ScrollGrupos;

  @FXML
  private ImageView enviar2;

  @FXML
  private ImageView lupa2;

  @FXML
  private TextField textDigitar;

  @FXML
  private ImageView sair2;

  @FXML
  private TextField textPesquisa;

  @FXML
  private Label txtParticipantes;

  @FXML
  private Button btnEnviarMensagem;

  @FXML
  private Button btnSair;

  @FXML
  private Button btnLupa;

  private ClienteTCP clienteTCP;
  private ClienteUDP clienteUDP;
  private String hostname;
  private int tcpPort;
  private int udpPort = 6790;
  private String nome;
  private String grupoAtual;
  private Label grupoSelecionado;
  private Map<String, List<String>> mapaMensagensGrupo = new HashMap<>();

  @FXML
  void btnHelpT1(MouseEvent event) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("SINGLE MESSAGE EXCHANGE");
    alert.setHeaderText("AJUDA");
    alert.setContentText(" Preencha todos os campos e clique no botao comecar");

    // Definindo a altura e a largura da caixa de alerta
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.setPrefSize(300, 300); // Defina a largura e a altura desejadas

    alert.showAndWait();
  }

  @FXML
  void btnHelpT2(MouseEvent event) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("SINGLE MESSAGE EXCHANGE");
    alert.setHeaderText("AJUDA");
    alert.setContentText(
        "1. Para criar ou entrar num grupo, digite o nome desejado na barra de pequisa no canto esquerdo superior e confirme clicando na lupa.\n\n"
            +
            "2. Para sair de um grupo, clique no botao/icone de sair, localizado na parte superior a direita da tela.\n\n"
            +
            "3. Para enviar uma mensagem digite o texto no campo 'digite sua mensagem' e clique no botao enviar (seta para cima), localizados no canto inferior da tela.\n\n"
            +
            "4. Para navegar entre seus grupos, basta clicar sobre o nome deles.\n\n"
            +
            "5. Para mais informacoes sobre as funcionalidades, basta passar o mouse sobre botoes/barras e aguardar, textos explicativos irao aparecer.");

    // Definindo a altura e a largura da caixa de alerta
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.setPrefSize(500, 400); // Defina a largura e a altura desejadas

    alert.showAndWait();
  }

  @FXML
  void comecarBotao(MouseEvent event) {
    nome = textNome.getText();
    hostname = textEndereco.getText();
    if (!nome.isEmpty() && !hostname.isEmpty()) {

      try {
        tcpPort = Integer.parseInt(textPorta.getText());
      } catch (NumberFormatException e) {
        System.out.println("Por favor, insira um numero valido para a porta.");
        return;
      }

      try {
        clienteTCP = new ClienteTCP(hostname, tcpPort);
        clienteUDP = new ClienteUDP(hostname, udpPort);

        clienteTCP.connect();
        clienteTCP.startListening(this::atualizarMensagens);

        ((Node) telaInicial).setVisible(false);
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Nao foi possivel conectar ao servidor TCP.");
      }
    } else {
      System.out.println("preencha todos os campos antes de continuar!");
    }
  }

  private void atualizarMensagens(String mensagem) {
    Platform.runLater(() -> {
      Label novaMensagem = new Label(mensagem);
      novaMensagem.setWrapText(true); // Permitir quebra de linha
      novaMensagem.setMaxWidth(400);
      novaMensagem.setStyle(
          " -fx-text-fill: white;  -fx-font-family: impact;  -fx-font-size: 20px; -fx-background-radius: 15px; -fx-padding: 5px;");
      vboxMensagens.setSpacing(10);
      vboxMensagens.getChildren().add(novaMensagem);

      // Salva a mensagem no mapa
      if (grupoAtual != null) {
        mapaMensagensGrupo.computeIfAbsent(grupoAtual, k -> new ArrayList<>()).add(mensagem);
      }
    });
  }

  @FXML
  void btnEnviarMensagem(MouseEvent event) {
    String mensagem = textDigitar.getText();
    if (!mensagem.trim().isEmpty()) {
      textDigitar.clear();
      try {
        clienteUDP.sendMessage(grupoAtual, nome, mensagem);
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Nao foi possivel enviar a mensagem via UDP.");
      }
    }
  }

  @FXML
  void lupa(MouseEvent event) { // entrar no grupo
    String grupo = textPesquisa.getText();
    if (!nome.isEmpty() && !grupo.isEmpty()) {
      if (grupoAtual != null) {
        clienteTCP.leaveGroup(grupoAtual, nome);
      }
      clienteTCP.joinGroup(grupo, nome);
      adicionarGrupoAoVBox(grupo);
      atualizarChatDoGrupo(grupo);
      textPesquisa.clear();

    }
  }

  @FXML
  void sairDoGrupo(MouseEvent event) { // sair do grupo
    if (!nome.isEmpty() && grupoAtual != null) {
      clienteTCP.leaveGroup(grupoAtual, nome);
      removerGrupoDoVBox(grupoAtual);

      // Remove as mensagens do grupo atual do mapa
      mapaMensagensGrupo.remove(grupoAtual);

      vboxMensagens.getChildren().clear();
      adicionarMensagemAoChat("Voce saiu do grupo " + grupoAtual);
      grupoAtual = null;
      NomeGrupo.setText("Selecione um grupo");
    }
  }

  private void adicionarGrupoAoVBox(String grupo) { // adicionar grupos na tabela de grupos
    // Verifica se o grupo ja existe no VBox
    if (vboxGrupos.getChildren().stream()
        .anyMatch(node -> node instanceof Label && ((Label) node).getText().equals(grupo))) {
      System.out.println("Grupo ja existe.");
      return; // Nao adiciona o grupo se ja existir
    }

    Label novoGrupo = new Label(grupo);
    novoGrupo.setStyle(
        "-fx-text-fill: white;  -fx-font-family: impact;  -fx-font-size: 20px; -fx-background-radius: 15px;");
    novoGrupo.setOnMouseClicked(event -> {
      if (grupoAtual != null) {
        clienteTCP.leaveGroup(grupoAtual, nome);
        removerEstiloSelecionado(grupoAtual);
      }
      clienteTCP.joinGroup(grupo, nome);
      atualizarChatDoGrupo(grupo);
    });

    Separator separador = new Separator();
    separador.setStyle("-fx-background-color: #0a74cc;");
    separador.setPrefWidth(200);
    VBox.setMargin(separador, new Insets(5, 0, 5, 0));

    vboxGrupos.setSpacing(10);
    vboxGrupos.getChildren().addAll(novoGrupo, separador);
  }

  private void removerEstiloSelecionado(String grupo) {
    for (Node node : vboxGrupos.getChildren()) {
      if (node instanceof Label && ((Label) node).getText().equals(grupo)) {
        node.getStyleClass().remove("selected-group");
      }
    }
  }

  private void removerGrupoDoVBox(String grupo) { // remove o grupo da lista de grupos
    for (int i = 0; i < vboxGrupos.getChildren().size(); i++) {
      Node node = vboxGrupos.getChildren().get(i);
      if (node instanceof Label && ((Label) node).getText().equals(grupo)) {
        vboxGrupos.getChildren().remove(i); // Remove o Label
        if (i < vboxGrupos.getChildren().size() && vboxGrupos.getChildren().get(i) instanceof Separator) {
          vboxGrupos.getChildren().remove(i); // Remove o Separator apos o Label
        }
        break;
      }
    }
  }

  private void atualizarChatDoGrupo(String grupo) { // chat do grupo aualizado quando mandar mensagem
    grupoAtual = grupo;
    if (grupoSelecionado != null) {
      grupoSelecionado.getStyleClass().remove("selected-group");
    }
    grupoSelecionado = (Label) vboxGrupos.getChildren().stream()
        .filter(node -> node instanceof Label && ((Label) node).getText().equals(grupo))
        .findFirst()
        .orElse(null);
    if (grupoSelecionado != null) {
      grupoSelecionado.getStyleClass().add("selected-group");
    }
    vboxMensagens.getChildren().clear();
    NomeGrupo.setText("Chat: " + grupo);
    adicionarMensagemAoChat("Chat do grupo " + grupo);

    // Carrega as mensagens do grupo
    List<String> mensagens = mapaMensagensGrupo.getOrDefault(grupo, new ArrayList<>());
    for (String mensagem : mensagens) {
      adicionarMensagemAoChat(mensagem);
    }
  }

  private void adicionarMensagemAoChat(String mensagem) { // adiciona mensagem no chat
    Label novaMensagem = new Label(mensagem);
    novaMensagem.setWrapText(true); // Permitir quebra de linha
    novaMensagem.setMaxWidth(450);
    novaMensagem.setStyle(
        "-fx-text-fill: white;  -fx-font-family: impact;  -fx-font-size: 20px; -fx-background-radius: 15px;");

    vboxMensagens.setSpacing(10);
    vboxMensagens.getChildren().add(novaMensagem);
  }

  private void mouseEntrouLupa(MouseEvent event) {
    lupa2.setVisible(true);
  }

  private void mouseSaiuLupa(MouseEvent event) {
    lupa2.setVisible(false);
  }

  private void mouseEntrouSairGrupo(MouseEvent event) {
    sair2.setVisible(true);
  }

  private void mouseSaiuSairGrupo(MouseEvent event) {
    sair2.setVisible(false);
  }

  private void mouseEntrouEnviar(MouseEvent event) {
    enviar2.setVisible(true);
  }

  private void mouseSaiuEnviar(MouseEvent event) {
    enviar2.setVisible(false);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    enviar2.setVisible(false);
    lupa2.setVisible(false);
    sair2.setVisible(false);

    btnEnviarMensagem.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntrouEnviar);
    btnEnviarMensagem.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseSaiuEnviar);
    btnLupa.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntrouLupa);
    btnLupa.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseSaiuLupa);
    btnSair.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntrouSairGrupo);
    btnSair.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseSaiuSairGrupo);

    limitarCaracteres(textPesquisa, 20);
    limitarCaracteres(textNome, 20);
  }

  private void limitarCaracteres(TextField textField, int maxLength) {
    TextFormatter<String> formatter = new TextFormatter<>(change -> {
      if (change.getControlNewText().length() > maxLength) {
        return null;
      } else {
        return change;
      }
    });
    textField.setTextFormatter(formatter);
  }
}
