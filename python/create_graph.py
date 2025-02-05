import json
import os
import sys
from graph_visualizer import GraphVisualizer

def load_graph_from_json(file_path):
    with open(file_path, 'r') as file:
        graph_data = json.load(file)
    return graph_data

def main():
    if len(sys.argv) != 3:
        print("Usage: python create_graph.py <system_name> <graph_name>")
        sys.exit(1)

    system_name = sys.argv[1]
    graph_name = sys.argv[2]
    graph_file = os.path.join('data', system_name, 'analysis', graph_name)
    output_dir = os.path.join('data', system_name, 'plots')
    output_file = os.path.join(output_dir, 'graph.html')

    if not os.path.exists(graph_file):
        print(f"Error: {graph_file} does not exist.")
        sys.exit(1)

    # Ensure the output directory exists
    os.makedirs(output_dir, exist_ok=True)

    graph_data = load_graph_from_json(graph_file)
    visualizer = GraphVisualizer()
    visualizer.visualize(graph_data, output_file)

    print(f"Graph has been saved to {output_file}")

if __name__ == "__main__":
    main()