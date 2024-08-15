/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 08/05/2024
* Ultima alteracao.: 10/05/2024
* Nome.............: Pacote.java
* Funcao...........: Eh responsavel pela criacao e instancia e caminho percorrido pelo menor caminho 
*************************************************************** */
package modelo;

import controle.PrincipalController;

import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

public class Pacote extends Thread {

  private PrincipalController pc;
  private int origem;
  private int destino;
  private AnchorPane pane;
  private double xOrigem;
  private double yOrigem;
  private double xDestino;
  private double yDestino;

  public Pacote(PrincipalController pc, int origem, int destino, int ttl, Polyline poly) {

    this.pc = pc;
    this.origem = origem;
    this.destino = destino;
    this.pane = pc.getAcPane();
    ObservableList<Double> pontosxy = poly.getPoints();
    if (origem > destino) {
      this.xOrigem = pontosxy.get(2);
      this.yOrigem = pontosxy.get(3);
      this.xDestino = pontosxy.get(0);
      this.yDestino = pontosxy.get(1);
    } else {
      this.xOrigem = pontosxy.get(0);
      this.yOrigem = pontosxy.get(1);
      this.xDestino = pontosxy.get(2);
      this.yDestino = pontosxy.get(3);
    }

  }

  public void run() { // inicia a thread
    enviarMensagemTela(origem, destino, xOrigem, yOrigem, xDestino, yDestino);

  }

  public void enviarMensagemTela(int origem, int destino, double xOrigem, double yOrigem, double xDestino,
      double yDestino) { // realiza a movimentacao dos pacotes pelo caminho
    Platform.runLater(() -> {
      // Cria o caminho usando os pontos iniciais e finais dos polylines
      Path path = new Path();
      MoveTo moveTo = new MoveTo(xOrigem, yOrigem);
      LineTo lineTo = new LineTo(xDestino, yDestino);
      path.getElements().addAll(moveTo, lineTo);

      // Configura a transicao de caminho
      PathTransition pathTransition = new PathTransition();
      pathTransition.setPath(path);
      pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
      pathTransition.setCycleCount(0);
      pathTransition.setAutoReverse(false);
      pathTransition.setDuration(new Duration(1500)); // Define a duracao da animacao

      // Cria a imagem a ser animada
      ImageView imageView = new ImageView(new Image("visao/imagens/carta.png"));
      imageView.setFitWidth(28);
      imageView.setFitHeight(17);
      pathTransition.setNode(imageView);

      // Remove a ImageView do AnchorPane apos a transicao terminar
      pathTransition.setOnFinished(event -> {
        pane.getChildren().remove(imageView);
        // pc.getRoteadores().get(destino - 1).tipoVersao(ttl, origem);
      });

      // Adiciona a ImageView ao AnchorPane e inicia a animacao
      pane.getChildren().addAll(imageView);
      pathTransition.play();
    });
  }

}
