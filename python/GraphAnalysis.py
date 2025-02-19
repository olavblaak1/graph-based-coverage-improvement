import json
import networkx as nx
import numpy as np

from ClusteringMethod import ClusteringMethod


class GraphAnalysis:
    def __init__(self, graph_file, clustering_method):
        json_graph = self.load_graph(graph_file)
        self.graph = self.create_graph(json_graph)
        self.algorithm = clustering_method
        self.clusters = None

    def load_graph(self, graph_file):
        with open(graph_file) as f:
            graph = json.load(f)
        return graph

    def create_graph(self, json_graph):
        g = nx.DiGraph()
        for node in json_graph["nodes"]:
            g.add_node(node["id"], **node)
        for edge in json_graph["edges"]:
            g.add_edge(edge["sourceID"], edge["destinationID"], **edge)
        return g

    def get_clusters(self):
        if self.clusters is None:
            if self.algorithm == ClusteringMethod.LOUVAIN:
                self.clusters = nx.community.louvain_communities(self.graph, weight='weight')
            elif self.algorithm == ClusteringMethod.GIRVAN_NEWMAN:
                self.clusters = [list(cluster) for cluster in next(nx.community.girvan_newman(self.graph))]
                modularity = nx.community.modularity(self.graph, self.clusters)
                print(f'Modularity of the clusters is: {modularity}')
        return self.clusters

    def get_clusters_string(self):
        clusters = self.get_clusters()
        return "Clusters:\n" + "\n".join([str(cluster) for cluster in clusters])

    def get_cluster_sizes(self):
        clusters = self.get_clusters()
        return [len(cluster) for cluster in clusters]

    def get_cluster_sizes_distribution(self):
        cluster_sizes = self.get_cluster_sizes()
        return np.histogram(cluster_sizes, bins=np.arange(0, max(cluster_sizes) + 1))

    def get_cluster_sizes_distribution_string(self):
        cluster_sizes_distribution = self.get_cluster_sizes_distribution()
        return "Cluster sizes distribution:\n" + "\n".join([str(cluster_sizes_distribution[1][i]) + ": " + str(cluster_sizes_distribution[0][i]) for i in range(len(cluster_sizes_distribution[0]))])

    def get_cluster_sizes_distribution_string_to_file(self, filename):
        cluster_sizes_distribution_string = self.get_cluster_sizes_distribution_string()
        with open(filename, "w") as f:
            f.write(cluster_sizes_distribution_string)

    def get_cluster_sizes_distribution_string_to_console(self):
        print(self.get_cluster_sizes_distribution_string())

    def get_cluster_sizes_distribution_string_to_console_and_file(self, filename):
        self.get_cluster_sizes_distribution_string_to_console()
        self.get_cluster_sizes_distribution_string_to_file(filename)


    def annotate_graph_with_clusters(self):
        clusters = self.get_clusters()
        for cluster_id, cluster in enumerate(clusters):
            for node in cluster:
                self.graph.nodes[node]['cluster'] = cluster_id

    def export_graph_with_clusters_to_gephi(self, filename):
        self.annotate_graph_with_clusters()
        nx.write_gexf(self.graph, filename)