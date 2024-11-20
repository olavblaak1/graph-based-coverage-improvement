from analysis_manager import AnalysisManager
from clustering.clustering_algorithm import ClusteringAlgorithm

def main():
    '''
    This runs the analysis on the real-world framework Joda-time.
    '''
    data_path = './data/test_application'
    clustering_algorithm = ClusteringAlgorithm.LOUVAIN
    analysis_manager = AnalysisManager(data_path, clustering_algorithm)
    analysis_manager.run_analysis()
    
if __name__ == '__main__':
    main()