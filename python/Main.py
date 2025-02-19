import GraphAnalysis

from ClusteringMethod import ClusteringMethod


def main():
    graph_analysis = GraphAnalysis.GraphAnalysis("../data/joda-time/analysis/coverageGraph.json", ClusteringMethod.LOUVAIN)
    graph_analysis.export_graph_with_clusters_to_gephi("../data/joda-time/clusters/graph_with_louvain_clusters.gexf")

    graph_analysis = GraphAnalysis.GraphAnalysis("../data/joda-time/analysis/coverageGraph.json", ClusteringMethod.GIRVAN_NEWMAN)
    graph_analysis.export_graph_with_clusters_to_gephi("../data/joda-time/clusters/graph_with_girvan_clusters.gexf")


if __name__ == "__main__":
    main()
