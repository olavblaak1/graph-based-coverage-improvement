from enum import Enum, auto

class ClusteringAlgorithm(Enum):
    LOUVAIN = auto()
    GIRVAN_NEWMAN = auto()
    LEIDEN = auto()