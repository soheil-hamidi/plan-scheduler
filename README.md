# plan-scheduler
Greedy algorithm to choose a best plan from schedule given with a help of quick sort.

Input and Output:

The input to the problem should be in a *.txt format. In the text file, the first line gives starting time (S) and ending time (F), the second line gives the number of projects, and each subsequent line gives the project number, and start and finish time of a potential project.

Input for the example problem given above would be:

3 10
8
1 2 5
2 1 2
3 3 4
4 5 9
5 6 8
6 7 9
7 7 13
8 4 9

Output consists of the selected projects as a list, then divided into two sets with total times as
equal as possible. For the example, it should look something like this:

Selected Projects: 1 4 7
Group 1 Projects: 1 4 Total Time = 7
Group 2 Projects: 7 Total Time = 6
