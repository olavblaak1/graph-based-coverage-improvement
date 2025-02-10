import math


def get_node_color(node):
    # Define a color gradient from red (low rank) to green (high rank)
    if node['covered']:
        return '#00ff00'
    else:
        return '#ff0000'


def get_edge_color(edge):
    if edge['covered']:
        return '#00ff00'
    else:
        return '#ff0000'


def get_edge_style(edge_type):
    edge_styles = {
        "METHOD_CALL": "dashed",
        "INHERITANCE": "dashed",
        "FIELD": "dotted",
        "METHOD_OWN": "solid"
    }
    return edge_styles.get(edge_type, 'solid')