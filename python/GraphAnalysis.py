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

    def export_graph_to_gephi(self, filename):
        nx.write_gexf(self.graph, filename)