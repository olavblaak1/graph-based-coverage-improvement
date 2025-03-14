package com.kuleuven.MissingTestIdentification;

import com.kuleuven.Graph.Graph.RankedSharedPath;

public class InverseDijkstra {


    /*
    * Check if the path is eligible for the inverse Dijkstra algorithm
    *
    * path1.getDistance() < path2.getDistance()
    *       =>
    *       path2.getMultipliedDistance() * path1.getDistance() >= path1.getMultipliedDistance() * path2.getDistance()
     */
    public static boolean isEligible(RankedSharedPath path1, RankedSharedPath path2) {
        return !(path1.getDistance() <= path2.getDistance())
                ||
                (path2.getReciprocalDistance()  <= path1.getReciprocalDistance());
    }
}
