/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 09/04/2024
* Ultima alteracao.: 09/04/2024
* Nome.............: AlgoritmoDijkstra.java
* Funcao...........: Eh responsavel pelo metodo que ira definir o caminho mais curto entre os nos do backbone.
*************************************************************** */
package modelo;

import java.util.*;

import controle.PrincipalController;

public class AlgoritmoDijkstra {
  private PrincipalController pc;

  public AlgoritmoDijkstra(PrincipalController principalController) {
    this.pc = principalController;
  }

  public static Map<Integer, Integer> dijkstra(Map<Integer, Map<Integer, Integer>> adjacencias, int origem) {
    Map<Integer, Integer> distancias = new HashMap<>();
    PriorityQueue<Node> filaPrioridade = new PriorityQueue<>(Comparator.comparingInt(n -> n.distancia));

    // Inicializacao das distancias como infinito e adicao da origem a fila de
    // prioridade
    for (int no : adjacencias.keySet()) {
      distancias.put(no, Integer.MAX_VALUE);
    }
    distancias.put(origem, 0);
    filaPrioridade.add(new Node(origem, 0));

    // Execucao do algoritmo de Dijkstra
    while (!filaPrioridade.isEmpty()) {
      int u = filaPrioridade.poll().id;

      for (Map.Entry<Integer, Integer> vizinho : adjacencias.get(u).entrySet()) {
        int v = vizinho.getKey();
        int peso = vizinho.getValue();

        // Relaxamento da aresta
        if (distancias.get(u) != Integer.MAX_VALUE && distancias.get(u) + peso < distancias.get(v)) {
          distancias.put(v, distancias.get(u) + peso);
          filaPrioridade.add(new Node(v, distancias.get(v)));
        }
      }
    }

    return distancias;
  }

  static class Node {
    int id;
    int distancia;

    Node(int id, int distancia) {
      this.id = id;
      this.distancia = distancia;
    }
  }
}
