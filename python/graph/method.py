# This code was taken from the original thesis code by Charles Sys, at https://github.com/syscharles/master_thesis_implementation
class Method:
    def __init__(self, name, parameters, return_type, declaring_class, signature) -> None:
        if name is None:
            raise Exception('Name of a method must not be undefined.')
        self.name = name
        self.parameters = parameters
        self.return_type = return_type
        self.declaring_class = declaring_class
        self.signature = signature

    def __eq__(self, other):
        if not isinstance(other, Method):
            return NotImplemented
        return (self.name, tuple(self.parameters), self.return_type, self.declaring_class) == \
               (other.name, tuple(other.parameters), other.return_type, other.declaring_class)

    def __str__(self):
        params = ', '.join(self.parameters)
        return f'{self.name}({params}): {self.return_type} @ {self.declaring_class}'
    
    def serialize(self):
        return {
        "return_type": self.return_type,
        "method_name": self.name,
        "declaring_class": self.declaring_class,
        "arguments": [{"type": param["type"], "value": param["name"]} for param in self.parameters],
        "method_signature": self.signature
    }

    @staticmethod
    def from_dict(method_data):
        method_name = method_data.get('method_name', '')
        method_return_type = method_data.get('return_type', '')
        method_arguments = [{'type': arg.get('type', ''), 'name': arg.get('value', '')} for arg in method_data.get('arguments', [])]
        method_declaring_class = method_data.get('declaring_class', '')
        method_signature = method_data.get('method_signature', '')

        return Method(method_name, method_arguments, method_return_type, method_declaring_class, method_signature)