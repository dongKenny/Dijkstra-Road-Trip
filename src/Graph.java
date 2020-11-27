import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Graph {
        Hashtable<String, List<String>> adjacencyList;
        List<Edge> edges;

        public Graph() {
            adjacencyList = new Hashtable<>();
            edges = new ArrayList<>();
        }

        public class Edge {
            String first;
            String second;
            int weight;
            int minutes;

            public Edge (String first, String second, int dist, int time) {
                this.first = first;
                this.second = second;
                weight = dist;
                minutes = time;
            }
        }

        public void addVertex(String location) {
            adjacencyList.putIfAbsent(location, new ArrayList<>());
        }

        public void addEdge(String start, String end, int dist, int time) {
            addVertex(start);
            addVertex(end);
            adjacencyList.get(start).add(end);
            adjacencyList.get(end).add(start);
            edges.add(new Edge(start, end, dist, time));
        }


        @Override
        public String toString () {
            String result = "";
            for (Edge edge : edges) {
                result += edge.first + " -> " + edge.second + "\t" + edge.weight + "\n";
            }
            return result;
        }


    }