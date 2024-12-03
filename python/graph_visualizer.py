from pyvis.network import Network
from graph_utils import get_node_color, get_edge_color, get_edge_style

class GraphVisualizer:
    def __init__(self):
        self.net = Network(directed=True, notebook=True, height="800px", width="100%")
        self.edge_count = {}

    def add_nodes(self, nodes):
        for node in nodes:
            node_name = node['name']
            color = get_node_color(node_name)
            self.net.add_node(node_name, label=node_name, color=color)

    def add_edges(self, edges):
        for edge in edges:
            source = edge['source']
            destination = edge['destination']
            print("Source: " + source)
            print("Destination: " + destination)
            print("Type: " + edge['type'])

            edge_type = edge['type']
            color = get_edge_color(edge_type)
            style = get_edge_style(edge_type)

            # Count the number of edges between the same nodes
            edge_key = (source, destination)
            if edge_key not in self.edge_count:
                self.edge_count[edge_key] = 0
            self.edge_count[edge_key] += 1

            # Offset the edges to make them distinguishable
            offset = self.edge_count[edge_key] * 0.1

            try:
                self.net.add_edge(source, destination, color=color, dashes=(style == 'dashed'), width=4, physics=False, smooth={'type': 'curvedCCW', 'roundness': offset})
            except AssertionError: # At least one of the nodes are not modelled right now (e.g. classes of a library)
                continue

    def set_options(self):
        self.net.set_edge_smooth('dynamic')
        net_options = """
        {
            "nodes": {
                "font": {
                "size": 75,
                "face": "sans-serif"
                }
            },
            "edges": {
                "width": 8,
                "arrows": {
                "to": {
                    "enabled": true,
                    "scaleFactor": 2.5
                }
                }
            },
            "physics": {
                "barnesHut": {
                "gravitationalConstant": -80000,
                "centralGravity": 0.3,
                "springLength": 95,
                "damping": 0.09
                },
                "minVelocity": 0.75
            }
        }
        """
        self.net.set_options(net_options)

    def visualize(self, graph_data, output_file):
        self.add_nodes(graph_data['nodes'])
        self.add_edges(graph_data['edges'])
        self.set_options()
        self.net.show(output_file)