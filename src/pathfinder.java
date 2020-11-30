import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class pathfinder extends Graph{
    Hashtable<String, String> allAttractions; //<K,V> == <Attraction, Location>
    Hashtable<String, Boolean> visited; //<K,V> == <Location, True/False>, "Known"
    Hashtable<String, String> previous; //<K,V> == <Location, Previous Location> "Path"
    Hashtable<String, Integer> distance;//<K,V> == <Location, Distance relative to start> "Cost"
    HashSet<String> cities; //List of all cities used for the visited iterator
    Graph graph; //Holds the Edges/Vertices and the Adjacency List
    int milesTravelled; //Total distance travelled
    int timeTaken; //Total time taken

    public pathfinder() {
        allAttractions = new Hashtable<>(143);
        visited = new Hashtable<>();
        previous = new Hashtable<>();
        distance = new Hashtable<>();
        graph = new Graph();
        cities = new HashSet<>();
        milesTravelled = 0;
        timeTaken = 0;
    }

    List<String> route(String start_location, String end_location, List<String> attractions) {
        /* The path will hold the final route from start_location to attractions to end_location
           I initialize the visited hashtable with false values for each city
           I initialize the distances for each city by setting their values to essentially infinity
           Introduce the start_location as a 0 vertex
           Use Dijkstra's to visit each city/state.

           Initial Dijkstra's algorithm --> Repeat Dijkstra's + add distance travelled
         */

        ArrayList<String> path = new ArrayList<>();

        graph.addEdge(start_location, start_location, 0, 0);

        for (String city : cities) {
            if (city != null) {
                visited.put(city, false);
                distance.put(city, Integer.MAX_VALUE);
            }
        }

        distance.put(start_location, 0);

        for (String city : cities) {
            while (!visited.get(city)) {
                String vertex = least_cost_unknown_vertex();
                known(vertex);
                for (String v : graph.adjacencyList.get(vertex)) {
                    int weight = edge_weight(vertex, v);
                    if (distance.get(v) > distance.get(vertex) + weight && !v.equals(vertex)) { //Vertex's cost greater, update
                        distance.put(v, distance.get(vertex) + weight);
                        previous.put(v, vertex);
                    }
                }
            }
        }

        /* I take the distances of each attraction location relative to the initial start location
           From there, I place them in an array and as keys in a Hashtable
           attractionRank contains distances, and then they are sorted to form the closest attractions
           I convert these distances back to the String representation of the city/state in the Hashtable
           Add these Strings into attractionsRanked to have the names of the cities in order
           I follow the closest attraction to the next closest attraction from start_location to end_location
           Add each edge weight to the milesTravelled data member
         */

        ArrayList<Integer> attractionRank = new ArrayList<>();
        ArrayList<String> attractionsRanked = new ArrayList<>();
        Hashtable<Integer, String> attractionConverter = new Hashtable<>();

        for (String attraction : attractions) {
            attractionRank.add(distance.get(allAttractions.get(attraction)));
            attractionConverter.put(distance.get(allAttractions.get(attraction)), attraction);
        }
        Collections.sort(attractionRank);

        for (int rank : attractionRank) {
            attractionsRanked.add(allAttractions.get(attractionConverter.get(rank)));
        }

        attractionsRanked.add(0, start_location);

        //Visits the end location at the end if the attraction is present there
        if (attractionsRanked.contains(end_location)) {
            attractionsRanked.remove(end_location);
            attractionsRanked.add(end_location);
        }

        Stack stitch = new Stack();

        for (int i = 0; i < attractionsRanked.size()-1; i++) {
            String current = attractionsRanked.get(i);
            String nextVertex = attractionsRanked.get(i + 1);
            String nextVertexTemp = attractionsRanked.get(i + 1);

            stitch.add(nextVertex);
            while (!current.equals(nextVertex)) {
                String prevCity = previous.get(nextVertex);

                milesTravelled += edge_weight(nextVertex, prevCity);
                timeTaken += edge_time(nextVertex, prevCity);
                stitch.add(prevCity);
                nextVertex = prevCity;
            }

            while (!stitch.isEmpty()) {
                path.add((String) stitch.pop());
            }

            /* Each time I finish adding the path of an attraction to an attraction,
               I reset the visited/previous/distance Hashtables and calculate each distance relative
               to the starting attraction city again to be able to path to the cities,
               otherwise I would end up pathing to start_location and hitting a null.
             */

            visited = new Hashtable<>();
            previous = new Hashtable<>();
            distance = new Hashtable<>();

            for (String city : cities) {
                if (city != null) {
                    visited.put(city, false);
                    distance.put(city, Integer.MAX_VALUE);
                }
            }

            distance.put(nextVertexTemp, 0);

            for (String city : cities) {
                while (!visited.get(city)) {
                    String vertex = least_cost_unknown_vertex();
                    known(vertex);
                    for (String v : graph.adjacencyList.get(vertex)) {
                        int weight = edge_weight(vertex, v);
                        if (distance.get(v) > distance.get(vertex) + weight && !v.equals(vertex)) {
                            distance.put(v, distance.get(vertex) + weight);
                            previous.put(v, vertex);
                        }
                    }
                }
            }
        }

        return path;
    }

    private String least_cost_unknown_vertex() {
        /* The first iteration will take the 0-cost node for start location
           Subsequent ones will properly update to the node which has less distance
           And has not been visited yet (false)
         */

        String vertex = "";
        int min = Integer.MAX_VALUE;

        for (String city : cities) {
            if (!visited.get(city) && distance.get(city) <= min) {
                min = distance.get(city);
                vertex = city;
            }
        }
        return vertex;
    }

    private void known(String v) {
        if (v != null) {
            visited.put(v, true);
        }
    }

    public int edge_weight(String v1, String v2) {
        /* The weight of focus is distance between locations
           All of the roads represent an edge since they have
           a vertex and a second vertex connected by distance.
           All of these edges are stored in the Graph, and each
           edge in the graph is checked to return the weight between
           the two vertices, regardless of the order they occur in the edge.

           e.g. Redding CA --> Medford OR edge or Medford OR --> Redding CA
         */
        int weight = 0;
        for (Edge edge : graph.edges) {
            if (edge.first.equals(v1) && edge.second.equals(v2)) {
                return edge.weight;
            }
            else if (edge.first.equals(v2) && edge.second.equals(v1)) {
                return edge.weight;
            }
        }
        return weight;
    }

    public int edge_time(String v1, String v2) {
        /* This is not necessary for the final determination.
           I am including this because the data was stored and
           should be used to provide info
         */
        int time = 0;
        for (Edge edge : graph.edges) {
            if (edge.first.equals(v1) && edge.second.equals(v2)) {
                return edge.minutes;
            }
            else if (edge.first.equals(v2) && edge.second.equals(v1)) {
                return edge.minutes;
            }
        }
        return time;
    }

    public void print(List<String> path) {
        System.out.println(path.toString());
        System.out.println("The total distance travelled: " + milesTravelled + " miles");
        System.out.println("The time taken for the trip: " + timeTaken + " minutes");
    }

    public static void main(String args[]) throws FileNotFoundException {
        pathfinder pf = new pathfinder();

        String fileName = "roads.csv";
        String fileName2 = "attractions.csv";

        File[] files = new File[]{new File(fileName), new File(fileName2)};

        /* Add the files to an array of files
           Iterate through the array of files to use the same Scanner

           Delimit the roads with \r, replace newlines which appear at beginning of certain ones
           Split the line into the first location, second location, miles, and minutes
           Add them to the graph as a weighted edge
           Add both cities to the cities Hashset to have all the unique String names

           Delimit attractions with \n
           Skip the first line which has the headers for the csv
           Split the line into the attraction and location
           Add them to the Hashtable in <Attraction, Location> order

         */

        int fileCount = 0;
        for (File f : files) {
            Scanner scanner = new Scanner(f);
            if (fileCount == 0) {
                scanner.useDelimiter("\r");
                while (scanner.hasNextLine() && scanner.hasNext()) {
                    String line = scanner.next();
                    if (line.strip().length() == 0) { //Extra line at end is empty
                        break;
                    }
                    String[] roadData = line.split(",");
                    String start = roadData[0].replace("\n", "");
                    String end = roadData[1];
                    int miles = Integer.parseInt(roadData[2]);
                    if (roadData[3].equals("10a")) {
                        roadData[3] = "101";
                    }
                    int minutes = Integer.parseInt(roadData[3]);
                    if (start != null && end != null) {
                        pf.graph.addEdge(start, end, miles, minutes);
                        pf.cities.add(start);
                        pf.cities.add(end);
                    }
                }
                fileCount++;
            }
            else {
                scanner.useDelimiter("\n");
                int lineNumber = 0;
                while (scanner.hasNextLine() && scanner.hasNext()) {
                    String line = scanner.next();
                    if (lineNumber == 0) {  //Skip the 0th line of Attraction Location
                        lineNumber++;
                    }
                    else {
                        String[] attractionData = line.split(",");
                        String interest = attractionData[0];
                        String location = attractionData[1];
                        pf.allAttractions.put(interest, location);
                    }
                }
            }
        }

        List<String> attractions = new ArrayList<>();
        attractions.add("USS Midway Museum");
        attractions.add("The Alamo Mission");
        attractions.add("Pike Place Market");
        attractions.add("Statue of Liberty");
        attractions.add("Portland City Tour");
        attractions.add("Alcatraz");
        List<String> path = pf.route("Redding CA", "San Francisco CA", attractions);
        pf.print(path);

    }


}
