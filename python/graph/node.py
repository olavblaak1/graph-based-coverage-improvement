# This code was taken from the original thesis code by Charles Sys, at https://github.com/syscharles/master_thesis_implementation

class Node:
    def __init__(self, name) -> None:
        if name is None:
            raise Exception('Name of a node (class) must not be undefined.')
        self.name = name

    def __eq__(self, other):
        if isinstance(other, str):
            return self.name == other
        if not isinstance(other, Node):
            return NotImplemented
        return self.name == other.name

    def __hash__(self):
        return hash(self.name)

    def __str__(self):
        return f'Node: {self.name}'
    
    def serialize(self):
        return {
        'name': self.name
    }
