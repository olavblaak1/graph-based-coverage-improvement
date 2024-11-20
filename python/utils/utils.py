import json
import os
import logging

def load_json_data(filepath):
    '''
    Load JSON data from a file. If the file does not exist or an error occurs,
    log the error and return None.
    '''
    if not os.path.exists(filepath):
        logging.error(f"File does not exist: {filepath}")
        return None

    try:
        with open(filepath, 'r') as file:
            return json.load(file)
    except json.JSONDecodeError as e:
        logging.error(f"Error decoding JSON from {filepath}. Error: {e}")
    except Exception as e:
        logging.error(f"Error loading JSON data from {filepath}. Error: {e}")
    
    return None

def check_or_create_path(directory):
    if not os.path.exists(directory):
        try:
            os.makedirs(directory)
            logging.warning(f"Directory {directory} was created as it did not exist.")
        except OSError as e:
            logging.error(f"Failed to create directory {directory}. Error: {e}")
            return None
        
