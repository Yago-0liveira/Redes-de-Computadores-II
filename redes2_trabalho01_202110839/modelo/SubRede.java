/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 22/04/2024
* Ultima alteracao.: 28/04/2024
* Nome.............: Pacote.java
* Funcao...........: Eh responsavel pela entrega dos pacotes dada a versao do algoritmo escolhido pelo usuario.
*************************************************************** */

package modelo;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.shape.Polyline;

import controle.PrincipalController;

public class SubRede {

  private PrincipalController pc;
  Map<Integer, Polyline> hashMap = new HashMap<>();
  int id;

  public SubRede(PrincipalController pc, int id) {
    this.pc = pc;
    this.id = id;
  }

  public void adiconarconexao(Polyline poly, int receptor) { // Adicionar uma conexao ao mapa de conexoes
    hashMap.put(receptor, poly);
  }

  /*
   * public void metdo() {
   * for (Map.Entry<Integer, Polyline> entry : hashMap.entrySet()) {
   * Integer chave = entry.getKey();
   * Polyline polyline = entry.getValue();
   * // System.out.println(" roteador:" + id + "Chave: " + chave + ", Valor: " +
   * polyline);
   * }
   * }
   */

  public void enviarPacote(int ttl, int transmissor) { // envia o pacote para os nos vizinhos
    for (Map.Entry<Integer, Polyline> entry : hashMap.entrySet()) {
      Pacote pacote = new Pacote(pc, id, entry.getKey(), ttl, entry.getValue());
      pacote.start();
      pc.pacotesGerados++;
      pc.valorNatela(pc.pacotesGerados);
      // System.out.println("pacotes gerados: " + pc.pacotesGerados);

    }

  }

  public void tipoVersao(int ttl, int trans) { // switch para o tipo de versao do algoritmo que o usuario for escolher
    int versao = pc.getTipoCodificacao();
    switch (versao) {
      case 0:
        enviarPacote(-1, id);
      case 1:
        for (Map.Entry<Integer, Polyline> entry : hashMap.entrySet()) {
          if (trans != entry.getKey()) {
            Pacote pacote = new Pacote(pc, id, entry.getKey(), ttl, entry.getValue());
            pacote.start();
            pc.pacotesGerados++;
            pc.valorNatela(pc.pacotesGerados);

          }
        }
        break;
      case 2:
        for (Map.Entry<Integer, Polyline> entry : hashMap.entrySet()) {
          if (ttl > 0) {
            if (trans != entry.getKey()) {
              Pacote pacote = new Pacote(pc, id, entry.getKey(), ttl - 1, entry.getValue());
              pacote.start();
              pc.pacotesGerados++;
              pc.valorNatela(pc.pacotesGerados);

            }
          }
        }

        break;

      case 3:
        for (Map.Entry<Integer, Polyline> entry : hashMap.entrySet()) {
          if (ttl > 0) {
            if (trans != entry.getKey()) {
              Pacote pacote = new Pacote(pc, id, entry.getKey(), ttl - 1, entry.getValue());
              pacote.start();
              pc.pacotesGerados++;
              pc.valorNatela(pc.pacotesGerados);

            }
          }
        }
        break;
      default:
        break;
    }

  }

}
