/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 22/04/2024
* Ultima alteracao.: 29/04/202
* Nome.............: PrincipalController.java
* Funcao...........: Eh responsavel por lidar com eventos gerados pelos componentes da GUI LayoutPrincipal.fxml.
Eh tambem responsavel por controlar eventos ocorridos nas classes pertencenetes ao pacote modelo, assim como por
atualizar a GUI com dados do programa e o programa com dados advindos da GUI.  
*************************************************************** */

package controle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polyline;

import modelo.Pacote;
import modelo.SubRede;

public class PrincipalController implements Initializable {

  @FXML
  private AnchorPane acPane;

  @FXML
  private RadioButton btV1;

  @FXML
  private RadioButton btV2;

  @FXML
  private RadioButton btV3;

  @FXML
  private RadioButton btV4;

  @FXML
  private Button btncomecar;

  @FXML
  private ImageView textoVersoes;

  @FXML
  private ImageView imageTtl;

  @FXML
  private ImageView rot1;

  @FXML
  private ImageView rot2;

  @FXML
  private ImageView rot3;

  @FXML
  private ImageView rot4;

  @FXML
  private ImageView rot5;

  @FXML
  private ImageView rot6;

  @FXML
  private ImageView rot7;

  @FXML
  private ImageView rot8;

  @FXML
  private ImageView rot9;

  @FXML
  private ImageView rot10;

  @FXML
  private ImageView rot11;

  @FXML
  private ImageView rot12;

  @FXML
  private Label Contador;

  @FXML
  private TextField textTlt;

  @FXML
  private TextField textReceptor;

  @FXML
  private TextField textTransmissor;

  @FXML
  private ToggleGroup vgrupo;

  RadioButton radio;

  private SubRede subRede;
  private Pacote pacote;
  private int tipoCodificacao;
  private int trans;
  private int recep;
  private int ttl;
  private double xori;
  private double yori;
  private double xdes;
  private double ydes;
  private int numNos;
  ArrayList<Polyline> polylist = new ArrayList<>();
  private Map<Integer, Map<Integer, Integer>> adjacencias = new HashMap<>();
  ArrayList<SubRede> roteadores = new ArrayList<>();
  public static int pacotesGerados = 0; // Contador de pacotes gerados

  @FXML
  void btComecar(MouseEvent event) { // botao que da inicio ao programa
    tipoVersao();
    transmissor();
    receptor();

    // Verifica se os valores necessarios foram definidos corretamente
    if (getTrans() != 0 && getRecep() != 0) {
      // Cria um novo Pacote com os valores fornecidos e inicia a thread

      // Configura a visibilidade das rotas na interface grafica
      configurarVisibilidadeRotas(this);
      conectarNosComPolyline();
      roteadores.get(trans - 1).tipoVersao(ttl, trans);

    } else {
      showCaixaAlertaComecar();
    }
  }

  public void valorNatela(int valor) { // metodo responsavel pelo contador

    Platform.runLater(() -> {
      Contador.setText(valor + "");
    });
  }

  @FXML
  void maisInfo(MouseEvent event) { // referente ao botao help
    if (textoVersoes.isVisible()) { // Se o texto estiver visível
      textoVersoes.setVisible(false); // Oculta o texto
    } else {
      textoVersoes.setVisible(true); // Torna o texto visível
    }
  }

  public void transmissor() { // pega o valor do textransmissor
    String transmissor = textTransmissor.getText();
    if (transmissor.matches("\\d+")) { // Verifica se a string contem apenas dígitos
      int parcial = Integer.parseInt(transmissor);

      if (parcial >= 1 && parcial <= 12) {
        trans = parcial;
      } else {

      }
    } else {

    }
  }

  public void receptor() { // pega o valor do textreceptor
    String receptor = textReceptor.getText();
    if (receptor.matches("\\d+")) { // Verifica se a string contem apenas dígitos
      int parcial = Integer.parseInt(receptor);

      if (parcial >= 1 && parcial <= 12) {
        recep = parcial;
      } else {
      }
    } else {
    }
  }

  public void valorTtl() { // pega o valor do ttl
    String recebettl = textTlt.getText();
    if (recebettl.matches("\\d+")) { // Verifica se a string contem apenas dígitos
      int parcial = Integer.parseInt(recebettl);

      if (parcial >= 1 && parcial <= 1000) {
        ttl = parcial;
      } else {
      }
    } else {
    }
  }

  public void tipoVersao() { // //versao do algoritmo
    radio = (RadioButton) vgrupo.getSelectedToggle();
    setTipoCodificacao(-1);

    if (radio == btV1) {
      setTipoCodificacao(0);
    }

    if (radio == btV2) {
      setTipoCodificacao(1);
    }

    if (radio == btV3) {
      setTipoCodificacao(2);
      valorTtl();
    }

    if (radio == btV4) {
      setTipoCodificacao(3);
      valorTtl();
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) { // configuracoes setadas no inicio do programa
    lerGrafo("backbone.txt");
    rot1.setVisible(false);
    rot2.setVisible(false);
    rot3.setVisible(false);
    rot4.setVisible(false);
    rot5.setVisible(false);
    rot6.setVisible(false);
    rot7.setVisible(false);
    rot8.setVisible(false);
    rot9.setVisible(false);
    rot10.setVisible(false);
    rot11.setVisible(false);
    rot12.setVisible(false);

    textoVersoes.setVisible(false);

    textTlt.setVisible(false);
    imageTtl.setVisible(false);

    btV3.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) { // Verifica se o botao foi selecionado
        imageTtl.setVisible(true);
        textTlt.setVisible(true);
      } else { // O botao foi desmarcado
        imageTtl.setVisible(false);
        textTlt.setVisible(false);
      }
    });

    btV4.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) { // Verifica se o botao foi selecionado
        imageTtl.setVisible(true);
        textTlt.setVisible(true);
      } else { // O botao foi desmarcado
        imageTtl.setVisible(false);
        textTlt.setVisible(false);
      }
    });

    textReceptor.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.equals(textTransmissor.getText())) {
        btncomecar.setVisible(false);
        showCaixaAlerta();
      } else {
        btncomecar.setVisible(true);
      }
    });

    textTransmissor.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.equals(textReceptor.getText())) {
        btncomecar.setVisible(false);
        showCaixaAlerta();
      } else {
        btncomecar.setVisible(true);
      }
    });
  }

  private void showCaixaAlerta() { // metodo da caixa de alerta texto
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle("Aviso");
    alert.setHeaderText("envio incoerente!");
    alert.setContentText("o transmissor e o receptor sao iguais!   ");

    alert.showAndWait();
  }

  private void showCaixaAlertaComecar() { // metodo da caixa de alerta texto
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle("Aviso");
    alert.setHeaderText("preencha os campos!");
    alert.setContentText("os campos nao foram preenchidos corretamente  ");

    alert.showAndWait();
  }

  // Metodo para obter a coordenada x de uma ImageView
  public double getImageViewX(int numeroRot) {
    ImageView imageView = getImageView(numeroRot);
    return imageView.getLayoutX() + imageView.getBoundsInParent().getWidth() / 2;
  }

  // Metodo para obter a coordenada y de uma ImageView
  public double getImageViewY(int numeroRot) {
    ImageView imageView = getImageView(numeroRot);
    return imageView.getLayoutY() + imageView.getBoundsInParent().getHeight() / 2;
  }

  // Metodo para obter a ImageView com base no numero do no
  private ImageView getImageView(int numeroRot) {
    switch (numeroRot) {
      case 1:
        return rot1;
      case 2:
        return rot2;
      case 3:
        return rot3;
      case 4:
        return rot4;
      case 5:
        return rot5;
      case 6:
        return rot6;
      case 7:
        return rot7;
      case 8:
        return rot8;
      case 9:
        return rot9;
      case 10:
        return rot10;
      case 11:
        return rot11;
      case 12:
        return rot12;
      default:
        return null;
    }
  }

  public void adicionarPolyline(Polyline polyline, double xOrigem, double yOrigem, double xDestino, double yDestino) {
    // Adiciona o Polyline ao layout (por exemplo, um Pane)
    acPane.getChildren().add(polyline);
    // Imprime as coordenadas do ponto inicial (no de origem) e do ponto final (no
    // de destino)
    /*
     * System.out.println(
     * "Ponto inicial (no " + pontoCorrespondente(xOrigem, yOrigem) + "): (" +
     * xOrigem + ", " + yOrigem + ")");
     * System.out.println("Ponto final (no " + pontoCorrespondente(xDestino,
     * yDestino) + "): (" + xDestino + ", "
     * + yDestino + ")");
     */
    xori = xOrigem;
    yori = yOrigem;
    xdes = xDestino;
    ydes = yDestino;

  }

  // Metodo para determinar o numero do no correspondente com base nas coordenadas
  // x e y
  private int pontoCorrespondente(double x, double y) {
    // Loop sobre as ImageView para determinar o numero do no correspondente
    for (int i = 1; i <= 12; i++) {
      ImageView imageView = getImageView(i);
      if (imageView != null) {
        double layoutX = imageView.getLayoutX() + imageView.getBoundsInParent().getWidth() / 2;
        double layoutY = imageView.getLayoutY() + imageView.getBoundsInParent().getHeight() / 2;
        if (layoutX == x && layoutY == y) {
          return i;
        }
      }
    }
    return -1; // Retorna -1 se nao encontrar correspondência
  }

  public void configurarVisibilidadeRotas(PrincipalController controller) { // torna visivel as rotas
    try {
      // Loop sobre o numero de nos
      for (int i = 1; i <= numNos; i++) {
        // Obtem o nome do no correspondente
        String nomeNo = "rot" + i;
        SubRede x = new SubRede(controller, i);
        roteadores.add(x);

        // Obtem o campo correspondente na classe PrincipalController usando reflection
        Field field = PrincipalController.class.getDeclaredField(nomeNo);
        field.setAccessible(true); // Permitir acesso aos campos privados
        ImageView imageView = (ImageView) field.get(controller); // Obter a ImageView correspondente

        // Define a visibilidade da ImageView como true
        imageView.setVisible(true);
      }
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public void conectarNosComPolyline() { // faz a conexao dos nos
    // Criar um conjunto para rastrear as conexoes ja adicionadas
    Set<String> conexoesAdicionadas = new HashSet<>();

    // Loop sobre as adjacências para adicionar as conexoes ao Polyline
    for (int i = 1; i <= numNos; i++) {
      Map<Integer, Integer> conexoes = adjacencias.get(i);
      if (conexoes != null) {
        for (Map.Entry<Integer, Integer> entry : conexoes.entrySet()) {
          int destino = entry.getKey();

          // Verifica se a conexao ja foi adicionada
          String chaveConexao1 = i + "-" + destino;
          // System.out.println("trasmissor: " + i + "receptor: " + destino);
          String chaveConexao2 = destino + "-" + i;
          if (conexoesAdicionadas.contains(chaveConexao1) || conexoesAdicionadas.contains(chaveConexao2)) {
            // Se a conexao ja foi adicionada, pula para a proxima iteracao
            continue;
          }

          // Adiciona a conexao ao conjunto de conexoes adicionadas
          conexoesAdicionadas.add(chaveConexao1);
          conexoesAdicionadas.add(chaveConexao2);

          // Obter as coordenadas x e y do no de origem
          double xOrigem = getImageViewX(i);
          double yOrigem = getImageViewY(i);

          // Obter as coordenadas x e y do no de destino
          double xDestino = getImageViewX(destino);
          double yDestino = getImageViewY(destino);

          // Criar o Polyline e adicionar as coordenadas x e y
          Polyline polyline = new Polyline(xOrigem, yOrigem, xDestino, yDestino);
          polylist.add(polyline);
          roteadores.get(i - 1).adiconarconexao(polyline, destino);
          roteadores.get(destino - 1).adiconarconexao(polyline, i);
          // Adicionar o Polyline ao painel raiz (ou a qualquer outro painel relevante)
          adicionarPolyline(polyline, xOrigem, yOrigem, xDestino, yDestino);
        }
      }
    }

  }

  public void lerGrafo(String nomeArquivo) { // leitura do backbone para pegar os dados
    try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
      String linha = br.readLine();
      if (linha == null || linha.trim().isEmpty()) {
        System.out.println("Arquivo vazio ou mal formatado.");
        return;
      }
      linha = linha.trim().replace(";", "");
      numNos = Integer.parseInt(linha);

      for (int i = 1; i <= numNos; i++) {
        adjacencias.put(i, new HashMap<>());
      }

      while ((linha = br.readLine()) != null) {
        linha = linha.trim();
        if (linha.isEmpty()) {
          continue;
        }
        String[] partes = linha.split(";");
        int origem = Integer.parseInt(partes[0]);
        int destino = Integer.parseInt(partes[1]);
        int peso = Integer.parseInt(partes[2]);
        adicionarAresta(origem, destino, peso);
      }
    } catch (IOException | NumberFormatException e) {
      e.printStackTrace();
    }
  }

  public void adicionarAresta(int origem, int destino, int peso) {
    adjacencias.putIfAbsent(origem, new HashMap<>());
    adjacencias.putIfAbsent(destino, new HashMap<>());
    adjacencias.get(origem).put(destino, peso);
    adjacencias.get(destino).put(origem, peso); // Arestas bidirecionais
  }

  public int getNumNos() {
    return numNos;
  }

  public void setTipoCodificacao(int codificacao) { // metodo de definicao do tipo de codificao
    this.tipoCodificacao = codificacao;
  }

  public int getTipoCodificacao() { // metodo de recebimento do tipo de codificacao
    return tipoCodificacao;
  }

  public int getTrans() {
    return trans;
  }

  public int getRecep() {
    return recep;
  }

  public int getTtl() {
    return ttl;
  }

  public AnchorPane getAcPane() {
    return acPane;
  }

  public double getXdes() {
    return xdes;
  }

  public double getYDes() {
    return ydes;
  }

  public double getXori() {
    return xori;
  }

  public double getYori() {
    return yori;
  }

  public ArrayList<SubRede> getRoteadores() {
    return roteadores;
  }

}
