import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    public static List<int[]> adjacencyMatrixToEdgeList(int[][] adjMatrix) {
        List<int[]> edgeList = new ArrayList<>();
        int vertices = adjMatrix.length;

        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {
                if (adjMatrix[i][j] == 1) {
                    edgeList.add(new int[]{i, j});
                }
            }
        }

        return edgeList;
    }

    public static Map<Integer, List<Integer>> edgeListToAdjacencyList(List<int[]> edgeList) {
        Map<Integer, List<Integer>> adjList = new HashMap<>();

        for (int[] edge : edgeList) {
            int start = edge[0];
            int end = edge[1];

            adjList.putIfAbsent(start, new ArrayList<>());
            adjList.get(start).add(end);

            adjList.putIfAbsent(end, new ArrayList<>());
            adjList.get(end).add(start);
        }

        return adjList;
    }

    public static int[][] adjacencyMatrixToIncidenceMatrix(int[][] adjMatrix) {
        int vertices = adjMatrix.length;
        List<int[]> edges = new ArrayList<>();

        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {
                if (adjMatrix[i][j] == 1) {
                    edges.add(new int[]{i, j});
                }
            }
        }

        int[][] incMatrix = new int[vertices][edges.size()];
        for (int k = 0; k < edges.size(); k++) {
            int[] edge = edges.get(k);
            incMatrix[edge[0]][k] = 1;
            incMatrix[edge[1]][k] = 1;
        }

        return incMatrix;
    }

    public static List<Integer> bfs(Map<Integer, List<Integer>> graph, int startVertex) {
        List<Integer> visited = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(startVertex);

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            if (!visited.contains(vertex)) {
                visited.add(vertex);
                queue.addAll(graph.get(vertex));
            }
        }

        return visited;
    }

    public static List<Integer> dfs(Map<Integer, List<Integer>> graph, int startVertex) {
        List<Integer> visited = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        stack.push(startVertex);

        while (!stack.isEmpty()) {
            int vertex = stack.pop();
            if (!visited.contains(vertex)) {
                visited.add(vertex);


                List<Integer> neighbors = new ArrayList<>(graph.getOrDefault(vertex, new ArrayList<>()));
                neighbors.sort(Collections.reverseOrder());
                for (int neighbor : neighbors) {
                    stack.push(neighbor);
                }
            }
        }

        return visited;
    }

    public static void main(String[] args) {
        int[][] adjMatrix;

        try {
            adjMatrix = readMatrixFromFile("matrix.txt");
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
            return;
        }

        int[][] incMatrix = adjacencyMatrixToIncidenceMatrix(adjMatrix);

        List<int[]> edgeList = adjacencyMatrixToEdgeList(adjMatrix);

        Map<Integer, List<Integer>> adjList = edgeListToAdjacencyList(edgeList);

        System.out.println("Матрица инцидентности:");
        for (int[] row : incMatrix) {
            System.out.println(Arrays.toString(row));
        }

        System.out.println("\nСписок инцидентных рёбер для каждой вершины:");
        Map<Integer, List<String>> vertexToEdges = new HashMap<>();


        for (int[] edge : edgeList) {
            int start = edge[0];
            int end = edge[1];


            vertexToEdges.putIfAbsent(start, new ArrayList<>());
            vertexToEdges.get(start).add("(" + start + "," + end + ")");


            vertexToEdges.putIfAbsent(end, new ArrayList<>());
            vertexToEdges.get(end).add("(" + start + "," + end + ")");
        }


        for (Map.Entry<Integer, List<String>> entry : vertexToEdges.entrySet()) {
            System.out.println(entry.getKey() + ": " + String.join("  ", entry.getValue()));
        }


        System.out.println("\nСписок смежных вершин:");
        for (var entry : adjList.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }


        Scanner scanner = new Scanner(System.in);
        System.out.print("\nВведите начальную вершину для обходов: ");
        int startVertex = scanner.nextInt();


        List<Integer> bfsResult = bfs(adjList, startVertex);
        List<Integer> dfsResult = dfs(adjList, startVertex);


        System.out.println("\nОбход в ширину (BFS): " + bfsResult);
        System.out.println("Обход в глубину (DFS): " + dfsResult);

        scanner.close();
    }

    public static int[][] readMatrixFromFile(String fileName) throws IOException {
        List<int[]> matrix = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                String[] parts = line.split("\\s+");
                try {
                    int[] row = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();
                    matrix.add(row);
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка в строке: \"" + line + "\". Проверьте формат данных.");
                    throw e;
                }
            }
        }

        reader.close();


        int rowCount = matrix.size();
        for (int[] row : matrix) {
            if (row.length != rowCount) {
                throw new IOException("Матрица должна быть квадратной, но обнаружено несоответствие.");
            }
        }

        return matrix.toArray(new int[0][]);
    }
}
