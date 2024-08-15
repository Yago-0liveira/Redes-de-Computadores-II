/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 08/05/2024
* Ultima alteracao.: 09/05/2024
* Nome.............: SubRede.java
* Funcao...........: Eh responsavel pela adicao das conexoes entre os nos.
*************************************************************** */

package modelo;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.shape.Polyline;

import controle.PrincipalController;

public class SubRede {

  private PrincipalController pc;
  public Map<Integer, Polyline> hashMap = new HashMap<>();
  int id;

  public SubRede(PrincipalController pc, int id) {
    this.pc = pc;
    this.id = id;
  }

  public void adiconarconexao(Polyline poly, int receptor) { // Adicionar uma conexao ao mapa de conexoes
    hashMap.put(receptor, poly);
  }
}
