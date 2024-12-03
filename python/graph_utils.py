def get_node_color(node_name):
    return "black"

def get_edge_color(edge_type):
    edge_colors = {
        "METHOD_CALL": "black",
        "Inheritance": "blue",
        "Field": "green"
    }
    return edge_colors.get(edge_type, 'gray')

def get_edge_style(edge_type):
    edge_styles = {
        "METHOD_CALL": "solid",
        "Inheritance": "dashed",
        "Field": "dotted"
    }
    return edge_styles.get(edge_type, 'solid')