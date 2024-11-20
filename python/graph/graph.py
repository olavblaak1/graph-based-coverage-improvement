from clustering.cluster_graph import ClusterGraph
from graph.node import Node
from graph.edge import Edge

import networkx as nx
import json
import logging 

# This code was taken from the original thesis code by Charles Sys, at https://github.com/syscharles/master_thesis_implementation
class Graph:
    def __init__(self, nodes: list[Node], edges: list[Edge]) -> None:
        self.nodes = nodes
        self.edges = edges

    def add_node(self, node: Node) -> None:
        self.nodes.append(node)

    def add_edge(self, edge: Edge) -> None:
        if edge.source not in [n.name for n in self.nodes]:
            raise Exception(f'Graph does not have the source node {edge.departure} in its nodes.')
        if edge.destination not in [n.name for n in self.nodes]:
            raise Exception(f'Graph does not have the destination node {edge.destination} in its nodes.')        
        self.edges.append(edge)

    def count_isolated_nodes(self):
        isolated_nodes = [node for node in self.nodes if self.is_isolated(node)]
        return len(isolated_nodes)
    
    def is_isolated(self, node):
        for edge in self.edges:
            if edge.source == node or edge.destination == node:
                return False
        return True

    def average_edges(self):
        edges_count = {node: 0 for node in self.nodes}
        for edge in self.edges:
            edges_count[edge.destination] += 1
        return sum(edges_count.values()) / len(self.nodes)

    def median_incoming_edges_per_node(self):
        incoming_edges_counts = self.all_incoming_edges_per_node()
        return self.calculate_median(incoming_edges_counts)

    def median_outgoing_edges_per_node(self):
        outgoing_edges_counts = self.all_outgoing_edges_per_node()
        return self.calculate_median(outgoing_edges_counts)

    def all_incoming_edges_per_node(self):
        incoming_edges = {node: 0 for node in self.nodes}
        for edge in self.edges:
            incoming_edges[edge.destination] += 1
        return list(incoming_edges.values())

    def all_outgoing_edges_per_node(self):
        outgoing_edges = {node: 0 for node in self.nodes}
        for edge in self.edges:
            outgoing_edges[edge.source] += 1
        return list(outgoing_edges.values())

    def calculate_median(self, data):
        data.sort()
        n = len(data)
        if n % 2 == 1:
            return data[n // 2]
        else:
            mid1 = data[n // 2 - 1]
            mid2 = data[n // 2]
            return (mid1 + mid2) / 2

    def get_node_by_name(self, name: str) -> Node:
        for node in self.nodes:
            if node.name == name:
                return node
        logging.warning(f'Node with name {name} was not found.')
        return None

    def __str__(self):
        return f'Graph with {len(self.nodes)} nodes and {len(self.edges)} edges.'
    
    def serialize(self):
        graph_data = {
        'nodes': [node.serialize() for node in self.nodes],
        'edges': [edge.serialize() for edge in self.edges]
        }
        return json.dumps(graph_data, indent=4)
    
    def find_inter_cluster_edges(self, clusters : ClusterGraph):
        inter_cluster_edges = list()
        for edge in self.edges:
            src_cluster = clusters.get_cluster(edge.source)
            dest_cluster = clusters.get_cluster(edge.destination)
            if src_cluster != dest_cluster:
                inter_cluster_edges.append(edge)
        return inter_cluster_edges
    
    def get_networkx_graph(self):
        nx_graph = nx.MultiDiGraph()
        for node in self.nodes:
            nx_graph.add_node(node.name, **vars(node))
        for edge in self.edges:
            nx_graph.add_edge(edge.source.name, edge.destination.name, **vars(edge))
        return nx_graph