# Robot-Navigation-Visualizer
This is made as an assignment for Introduction to Artificial Intelligence at Swinburne University of Technology. The aim of this assignment is to learn and develop a search algorithm for problem solving. In this case, the problem that will be solved is a map with starting points and goals and walls in between. The goal of the program is to find the path from starting point to end point.

To start this program please run search.bat on command prompt and insert arguments. The available arguments are shown below.

## Feature
The main feature of this program is to parse a text file into a map with walls and finding solution from the starting robot position to the end goal. The user can run the program from command line by running the batch file with the following command:
```
search [map text file name] [search method]
```

It assumes that all input is true and valid. It parses text file with the following format:
```
[5,11] // The size of the map grid n x m [n,m]
(0,1) // Starting position of robot (x,y)
(10,3) // End goal position (x,y)
(2,0,2,2) // Wall with x and y position and its width and height (x,y,width,height)
(8,0,1,2) // Another wall
… // More wall
```

This specific text file would generate a map from Figure 1. After generating the map from the text file, it would check if the search method is available or not. If it’s not available, it will print out error message. Once a search method is selected, the program will start searching for a path from the starting position to the end position. When a solution is found, it will output the following message:
[map text file name] [node count] [path solution]
The path solution is the direction of the robot needs to take to get to the goal. It will print out in the following format:
```
up; left; down; right; ...
```

If there’s no solution, it will print out as such.

## Graphic Visualizer
This program provides a GUI (Graphic User Interface) Visualizer that shows how the search algorithm. The visualizer shows the entire map with each cell coloured depending on their type. This is the list of cell type and their corresponding colour:
•	Robot: red
•	Goal: blue
•	Visited: dark green
•	Frontier: orange
•	Result: yellow
To access this feature, run the following command:
```
search [map text file name] [search method] -gui
```

As the algorithm is sensing the surrounding area, it will start to create frontier in visitable cells. This will colour the cell that is connected to the robot (and goal in the case of Bidirectional Search Algorithm) as frontier. Depending on the algorithm, a cell is selected from the frontier and will be visited. It will be coloured accordingly. Once a solution is found, it will draw the resulting path from the start position to goal position.
In this visualizer, it runs separately from the command line other than parsing the map text file, hence it ignores the selected search algorithm method. Instead, the user can select the search algorithm from a combo box. The user can also set the delay using the delay slider or text field (from 0 to 1000ms) to delay for each of the action of the search method, such as adding frontier, visiting a cell, or drawing the resultant path to better understand how the search algorithm behave.
 
## Maze Generator

The program has a maze generator that generates random mazes for testing purpose with randomised starting robot position and goal position and can have the maze size specified. It uses Randomised Prim’s Algorithm to generate the maze. To use this feature, use the following command:
```
search generate [m-size] [n-size] [search method]
```

To visualize it to GUI, use the following command:
```
search generate [m-size] [n-size] [search method] -gui
```

The maze generator functionality can also be used to generate test cases by creating new maps and test all search algorithm on each map and output the results to a CSV file. This method is using 4 threads to generate for quicker result, making it very CPU intensive if generating large grids. To use this functionality, use the following command:
```
search generate [m-size] [n-size] [number of test map] [output file name]
```
After reviewing the program and result, which will be discussed later, there is a limitation of the maze generator. Randomised Prim’s Algorithm generally generate maze with one specific path from the start to goal rather than having more open-ended solutions. This results in the test cases to have either little or no significant statistical difference of the number of node in the path taken between the robot and goal. In the future, different maze generation algorithms shall be implemented as additional options for more testing.
