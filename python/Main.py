import GraphAnalysis

from ClusteringMethod import ClusteringMethod


def main():
    graphName = "uncoveredGraph"
    graph_analysis = GraphAnalysis.GraphAnalysis("../data/jfreechart/analysis/" + graphName + ".json", ClusteringMethod.LOUVAIN)
    graph_analysis.export_graph_to_gephi("../data/jfreechart/gexf/" + graphName + ".gexf")

    #graph_analysis = GraphAnalysis.GraphAnalysis("../data/joda-time/analysis/coverageGraph.json", ClusteringMethod.GIRVAN_NEWMAN)
    #graph_analysis.export_graph_with_clusters_to_gephi("../data/joda-time/clusters/graph_with_girvan_clusters.gexf")


if __name__ == "__main__":
    main()
