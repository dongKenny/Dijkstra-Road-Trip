# assignment2

This repo is for CS245 Data Structures and Algorithms with David-Guy Brizan.

Given two files, one with roads between cities/states with the distance and time to travel between them, and the other with attractions/their locations,
we parse these files and store them in a Graph. From there, we enter a start location, end location, and a list of attractions to visit in the route
function to use a modified Dijkstra's algorithm to find the shortest path from the start, all of the chosen attractions, and the end location.

The end output is a List<String> of all City State locations in order, along with the distance travelled in miles and the time taken in minutes.
