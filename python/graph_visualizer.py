from pyvis.network import Network
from graph_utils import get_node_color, get_edge_color, get_edge_style

class GraphVisualizer:
    def __init__(self):
        self.net = Network(directed=True, notebook=True, height="800px", width="100%")
        self.directed_edge_count = {}


    def add_nodes(self, nodes):
        for node in nodes:
            node_name = node['name']
            color = get_node_color(node_name)
            self.net.add_node(node_name, label=node_name, color=color)

    def add_edges(self, edges):
        for edge in edges:
            source = edge['source']['name']
            destination = edge['destination']['name'] # This is only necessary when were looking at method nodes...
            

            edge_type = edge['type']
            color = get_edge_color(edge_type)
            style = get_edge_style(edge_type)

            # Count the number of edges between the same nodes
            edge_key = (source, destination)


            if edge_key not in self.directed_edge_count:
                self.directed_edge_count[edge_key] = 0
            
            #if self.edge_count[edge_key] < 7:
            self.directed_edge_count[edge_key] += 1

            # Offset the edges to make them distinguishable
            offset = self.directed_edge_count[edge_key] * 0.009

            if source == "org.joda.time.DateTimeConstants" or destination == "org.joda.time.DateTimeConstants":
                print(source)

            try:
                self.net.add_edge(source, destination, color=color, dashes=(style == 'dashed'), width=2, physics=True, smooth={'type': 'CurvedCCW', 'roundness': offset})
            except AssertionError: # At least one of the nodes are not modelled right now (e.g. classes of a library)
                print("Edge not added: " + source + " -> " + destination)

    def set_options(self):
        self.net.set_edge_smooth('dynamic')
        #self.net.show_buttons(filter_=['physics'])
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
                "gravitationalConstant": -10000000,
                "centralGravity": 0.3,
                "springLength": 200,
                "damping": 0.4
                },
                "minVelocity": 0.75
            }
        }
        """
        self.net.set_options(net_options)

    def visualize(self, graph_data, output_file):
        self.add_nodes(graph_data['nodes'])
        self.add_edges(graph_data['edges'])

        print("Amount of isolated nodes: " + str(self.count_isolated_nodes()))

        self.set_options()
        self.net.show(output_file)


    def count_isolated_nodes(self):
        isolated_nodes = 0
        adj_list = self.net.get_adj_list()
        for node in self.net.nodes:
            node_id = node['id']

            if len(adj_list[node_id]) == 0:
                incoming_edges = 0
                for other_node_id in adj_list:
                    if node_id in adj_list[other_node_id]:
                        incoming_edges += 1

                if incoming_edges == 0:
                    print("Isolated node: " + node_id)
                    isolated_nodes += 1

        return isolated_nodes