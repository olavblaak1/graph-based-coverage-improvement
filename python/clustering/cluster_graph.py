import json
import logging
from clustering.cluster import Cluster
from graph.edge import Edge

class ClusterGraph:
    def __init__(self, clusters : list[Cluster]):
        self.clusters: list[Cluster] = clusters
        self.inter_cluster_edges: list[Edge] = list()

    def __str__(self):
        return f'Clusters Information: {len(self.clusters)} clusters, {len(self.inter_cluster_edges)} inter-cluster edges'

    def __eq__(self, other):
        if not isinstance(other, ClusterGraph):
            return NotImplemented
        return set(self.clusters) == set(other.clusters)
    def __hash__(self):
            return hash((self.clusters))
    
    def get_cluster(self, node_name : str) -> Cluster:    
        for cluster in self.clusters:
            for node in cluster.nodes:
                if node.name == node_name:
                    return cluster
        logging.warning(f'Cluster of node {node_name} not found.')
        return None
    

    def serialize(self):
        '''Serialize a ClusterGraph object into a dictionary.'''
        clusters_data= {
            'clusters': [cluster.serialize() for cluster in self.clusters],
            'inter_cluster_edges': [edge.serialize() for edge in self.inter_cluster_edges]
        }
        return json.dumps(clusters_data, indent=4)