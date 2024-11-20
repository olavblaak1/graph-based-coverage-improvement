# This code was taken from the original thesis code by Charles Sys, at https://github.com/syscharles/master_thesis_implementation
class Attribute:
    def __init__(self, name, typeAtt) -> None:
        if name is None:
            raise Exception('Name of an attribute must not be undefined.')
        if typeAtt is None:
            raise Exception('Type of an attribute must not be undefined.')
        self.name = name
        self.type = typeAtt

    def __eq__(self, other):
        if not isinstance(other, Attribute):
            return NotImplemented
        return (self.name, self.type) == (other.name, other.type)

    def __str__(self):
        return f'{self.name}: {self.type}'
            