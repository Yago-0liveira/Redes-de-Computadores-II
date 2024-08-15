/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 08/05/2024
* Ultima alteracao.: 11/05/2024
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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;
import modelo.AlgoritmoDijkstra;
import modelo.Pacote;
import modelo.SubRede;

public class PrincipalController implements Initializable {

  @FXML
  private AnchorPane acPane;

  @FXML
  private Button btnComecar;

  @FXML
  private Button btnRestart;

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
  private TextField textReceptor;

  @FXML
  private TextField textTransmissor;

  @FXML
  private ToggleGroup vgrupo;

  RadioButton radio;

  private SubRede subRede;
  private Pacote pacote;
  private AlgoritmoDijkstra dijkstra;
  private int trans;
  private int recep;
  private int ttl;
  private int numNos;
  ArrayList<Polyline> polylist = new ArrayList<>();
  public Map<Integer, Map<Integer, Integer>> adjacencias = new HashMap<>();
  ArrayList<SubRede> roteadores = new ArrayList<>();

  @FXML
  void btComecar(MouseEvent event) { // botao que da inicio ao programa
    transmissor();
    receptor();

    // Verifica se os valores necessarios foram definidos corretamente
    if (getTrans() != 0 && getRecep() != 0) {
      // Executar o algoritmo de Dijkstra para encontrar o caminho mais curto
      Map<Integer, Integer> distancias = dijkstra.dijkstra(adjacencias, getTrans());

      // Verificar se o destino e alcancavel a partir da origem
      if (distancias.get(getRecep()) != Integer.MAX_VALUE) {
        // Caminho mais curto encontrado, continuar com o programa

        // Obter o caminho mais curto do algoritmo de Dijkstra
        List<Integer> caminhoMaisCurto = obterCaminhoMaisCurto(distancias, getRecep());

        // Animar a transicao de cor dos polylines do caminho mais curto
        animaPolylineCor(caminhoMaisCurto, 0);
      } else {
        // Destino inalcancavel a partir da origem, exibir alerta
        showCaixaAlertaDestinoInalcancavel();
      }
    } else {
      showCaixaAlertaComecar();
    }
    textTransmissor.clear();
    textReceptor.clear();

  }

  @FXML
  void btnRestart(MouseEvent event) { // responsavel pela reinicializacao do programa
    // Limpar os campos de texto
    textTransmissor.clear();
    textReceptor.clear();

    // Tornar o botao "Comecar" visivel novamente
    btnComecar.setVisible(true);

    // Remover os polylines com animacao de cor
    for (Polyline polyline : polylist) {
      polyline.setStroke(Color.WHITE);
    }

    // Remover todos os labels adicionados dinamicamente
    acPane.getChildren().removeIf(node -> node instanceof Label);

    // Remover todos os polylines adicionados dinamicamente
    acPane.getChildren().removeAll(polylist);

    // Reiniciar a visibilidade das ImageView
    for (int i = 1; i <= 12; i++) {
      ImageView imageView = getImageView(i);
      if (imageView != null) {
        imageView.setVisible(false);
      }
    }

    // Limpar a lista de polylines
    polylist.clear();

    // Limpar as estruturas de dados relacionadas ao grafo
    adjacencias.clear();
    roteadores.clear();

    // Ler novamente o grafo do arquivo inicial
    lerGrafo("backbone.txt");

    // Configurar novamente a visibilidade das rotas e conectar os nos com polylines
    configurarVisibilidadeRotas(this);
    conectarNosComPolyline();
  }

  private void animaPolylineCor(List<Integer> caminho, int index) {
    if (index < caminho.size() - 1) {
      int origem = caminho.get(index);
      int destino = caminho.get(index + 1);
      Polyline caminhoPolyline = roteadores.get(origem - 1).hashMap.get(destino);

      // Criar a transicao de cor para o polyline atual
      Timeline timeline = new Timeline();
      KeyValue keyValue = new KeyValue(caminhoPolyline.strokeProperty(), Color.RED);
      KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), keyValue);
      timeline.getKeyFrames().add(keyFrame);
      timeline.play();

      // Chamada recursiva para o proximo polyline
      timeline.setOnFinished(event -> animaPolylineCor(caminho, index + 1));
    }
  }

  private List<Integer> obterCaminhoMaisCurto(Map<Integer, Integer> distancias, int destino) {
    List<Integer> caminho = new ArrayList<>();
    caminho.add(destino);

    int noAtual = destino;
    while (distancias.get(noAtual) != 0) {
      for (Map.Entry<Integer, Integer> vizinho : adjacencias.get(noAtual).entrySet()) {
        int vizinhoNo = vizinho.getKey();
        int pesoAresta = vizinho.getValue();
        if (distancias.get(noAtual) - pesoAresta == distancias.get(vizinhoNo)) {
          caminho.add(0, vizinhoNo);
          noAtual = vizinhoNo;
          break;
        }
      }
    }

    return caminho;
  }

  private void enviarPacoteAoLongoDoCaminho(List<Integer> caminho) {
    // Comecamos do segundo no, ja que o primeiro e a origem
    for (int i = 1; i < caminho.size(); i++) {
      int origem = caminho.get(i - 1); // No atual e o no de origem
      int destino = caminho.get(i); // Proximo no e o no de destino

      // Obtemos a Polyline correspondente ao segmento do caminho
      Polyline caminhoPolyline = roteadores.get(origem - 1).hashMap.get(destino);

      // Criamos o pacote e o enviamos para o proximo no no caminho
      Pacote pacote = new Pacote(this, origem, destino, ttl, caminhoPolyline);
      pacote.start();

    }
  }

  public void transmissor() { // pega o valor do textransmissor
    String transmissor = textTransmissor.getText();
    if (transmissor.matches("\\d+")) { // Verifica se a string contem apenas digitos
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
    if (receptor.matches("\\d+")) { // Verifica se a string contem apenas digitos
      int parcial = Integer.parseInt(receptor);
      if (parcial >= 1 && parcial <= 12) {
        recep = parcial;
      } else {
      }
    } else {
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) { // configuracoes setadas no inicio do programa
    dijkstra = new AlgoritmoDijkstra(this);
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

    textReceptor.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.isEmpty() && newValue.equals(textTransmissor.getText())) {
        btnComecar.setVisible(false);
        showCaixaAlerta();
      } else {
        btnComecar.setVisible(true);
      }
    });

    textTransmissor.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.isEmpty() && newValue.equals(textReceptor.getText())) {
        btnComecar.setVisible(false);
        showCaixaAlerta();
      } else {
        btnComecar.setVisible(true);
      }
    });
    configurarVisibilidadeRotas(this);
    conectarNosComPolyline();
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
    return -1; // Retorna -1 se nao encontrar correspondencia
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

  public void conectarNosComPolyline() {
    Set<String> conexoesAdicionadas = new HashSet<>();
    for (int i = 1; i <= numNos; i++) {
      final int noAtual = i; // Variavel local final para uso dentro da expressao lambda
      Map<Integer, Integer> conexoes = adjacencias.get(noAtual); // Usando noAtual em vez de i
      if (conexoes != null) {
        for (Map.Entry<Integer, Integer> entry : conexoes.entrySet()) {
          int destino = entry.getKey();
          int peso = entry.getValue();
          String chaveConexao1 = noAtual + "-" + destino;
          String chaveConexao2 = destino + "-" + noAtual;
          if (conexoesAdicionadas.contains(chaveConexao1) || conexoesAdicionadas.contains(chaveConexao2)) {
            continue;
          }
          conexoesAdicionadas.add(chaveConexao1);
          conexoesAdicionadas.add(chaveConexao2);
          double xOrigem = getImageViewX(noAtual);
          double yOrigem = getImageViewY(noAtual);
          double xDestino = getImageViewX(destino);
          double yDestino = getImageViewY(destino);
          Polyline polyline = new Polyline(xOrigem, yOrigem, xDestino, yDestino);
          polylist.add(polyline);
          roteadores.get(noAtual - 1).adiconarconexao(polyline, destino);
          roteadores.get(destino - 1).adiconarconexao(polyline, noAtual);
          adicionarPolyline(polyline, xOrigem, yOrigem, xDestino, yDestino);

          polyline.setStrokeWidth(2);
          polyline.setStroke(Color.WHITE);

          // Calcular ponto medio para posicionar o label
          double labelX = (xOrigem + xDestino) / 2;
          double labelY = (yOrigem + yDestino - 6) / 2;

          // Verificar se um label ja foi instanciado proximo a estas coordenadas
          for (Node node : acPane.getChildren()) {
            if (node instanceof Label) {
              Label label = (Label) node;
              double labelXDiff = Math.abs(labelX - label.getLayoutX());
              double labelYDiff = Math.abs(labelY - label.getLayoutY());
              if (labelXDiff < 20 && labelYDiff < 20) { // Se estiverem muito proximos
                // Afastar os labels para mais perto de um dos nos
                labelX += 30;
                labelY += 10;
              }
            }
          }

          // Criar o label com o peso da aresta
          Label labelPeso = new Label(String.valueOf(peso));
          labelPeso.setLayoutX(labelX);
          labelPeso.setLayoutY(labelY);

          // Adicionar listener de evento de mouse para exibir os nos referentes ao peso
          labelPeso.setOnMouseEntered(e -> exibirNosReferentes(labelPeso, noAtual, destino));

          // Adicionar o label ao AnchorPane
          acPane.getChildren().add(labelPeso);
        }
      }
    }
  }

  private void exibirNosReferentes(Label labelPeso, int noOrigem, int noDestino) {
    // Criar um label para exibir os nos referentes ao peso
    Label nosLabel = new Label("Nos: " + noOrigem + " - " + noDestino);
    nosLabel.setStyle("-fx-background-color: white; -fx-padding: 5px;");
    nosLabel.setLayoutX(labelPeso.getLayoutX());
    nosLabel.setLayoutY(labelPeso.getLayoutY() + 20); // Posicionar abaixo do peso
    // Adicionar o label ao AnchorPane
    acPane.getChildren().add(nosLabel);

    // Adicionar listener de evento de mouse para remover o label quando o mouse
    // sair
    labelPeso.setOnMouseExited(e -> acPane.getChildren().remove(nosLabel));
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

  private void showCaixaAlertaDestinoInalcancavel() {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("Aviso");
    alert.setHeaderText("Destino inalcancavel!");
    alert.setContentText("O destino nao pode ser alcancado a partir da origem especificada.");

    alert.showAndWait();
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

  public int getTrans() {
    return trans;
  }

  public int getRecep() {
    return recep;
  }

  public AnchorPane getAcPane() {
    return acPane;
  }

  public ArrayList<SubRede> getRoteadores() {
    return roteadores;
  }

}
