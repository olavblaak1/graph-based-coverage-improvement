from enum import Enum

class ClusteringMethod(Enum):
    LOUVAIN = "louvain"
    GIRVAN_NEWMAN = "girvan_newman"