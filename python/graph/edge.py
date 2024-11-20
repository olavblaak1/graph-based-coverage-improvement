from graph.node import Node

# This code was taken from the original thesis code by Charles Sys, at https://github.com/syscharles/master_thesis_implementation
class Edge:
    def __init__(self, source, destination, method, source_method, weight=1) -> None:
        if not isinstance(source, Node):
            raise Exception('Departure node of an edge must be of type Node.')
        if not isinstance(destination, Node):
            raise Exception('Destination node of an edge must be of type Node.')
        if method is None:
            raise Exception('method must not be null')
        if source_method is None:
            raise Exception('source_method must not be null')
        self.source = source
        self.destination = destination
        self.method = method
        self.source_method = source_method
        self.weight = weight

    def __eq__(self, other):
        if not isinstance(other, Edge):
            return NotImplemented
        return (self.source, self.destination) == \
               (other.source, other.destination)

    def __hash__(self):
        return hash((self.source, self.destination, self.weight))

    def __str__(self):
        return f'Edge: {self.source} -> {self.destination}, Weight: {self.weight}, Method: {self.method}'
    
    def serialize(self):
        edge_data = {
            'source': self.source.name,
            'destination': self.destination.name,
        }
        if self.method is not None:
            edge_data['link_method'] = self.method.serialize()
        if self.source_method is not None:
            edge_data['source_method'] = self.source_method.serialize()
        return edge_data
