import os
import GraphAnalysis
from ClusteringMethod import ClusteringMethod

def main():
    application_name = "jfreechart"
    analysis_folder = f"../data/{application_name}/analysis/"
    output_folder = f"../data/{application_name}/gexf/"

    # Ensure the output folder exists
    os.makedirs(output_folder, exist_ok=True)

    # Iterate over all .json files in the analysis folder
    for file_name in os.listdir(analysis_folder):
        if file_name.endswith("Graph.json"):
            graph_name = os.path.splitext(file_name)[0]  # Get the file name without extension
            input_path = os.path.join(analysis_folder, file_name)
            output_path = os.path.join(output_folder, f"{graph_name}.gexf")

            # Perform the graph analysis and export
            graph_analysis = GraphAnalysis.GraphAnalysis(input_path, ClusteringMethod.LOUVAIN)
            graph_analysis.export_graph_to_gephi(output_path)
            print(f"Exported {file_name} to {output_path}")

if __name__ == "__main__":
    main()