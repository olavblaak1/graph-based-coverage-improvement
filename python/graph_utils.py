import math


def get_node_color(node_name, normalized_rank):
    # Define a color gradient from red (low rank) to green (high rank)
    low_color = (0, 255, 0)  # Green
    high_color = (255, 0, 0)  # Red

    # Apply exponential scaling to the normalized rank
    scaled_rank = math.pow(normalized_rank, 0.3)

    # Interpolate between the low and high colors
    color = (
        int(low_color[0] + (high_color[0] - low_color[0]) * scaled_rank),
        int(low_color[1] + (high_color[1] - low_color[1]) * scaled_rank),
        int(low_color[2] + (high_color[2] - low_color[2]) * scaled_rank),
    )

    # Convert the color to a hex string
    return '#{:02x}{:02x}{:02x}'.format(*color)
    return 
    

def get_edge_color(edge):
    if edge['covered']:
        return "green"
    else:
        return "black"

def get_edge_style(edge_type):
    edge_styles = {
        "METHOD_CALL": "solid",
        "Inheritance": "dashed",
        "Field": "dotted"
    }
    return edge_styles.get(edge_type, 'solid')