/**
 * The space class reads the file that contains projects and 
 * chooses the best plan so it covers the given start and 
 * end time and splits the time required to do all the 
 * projects to two nearly equal groups.
 */
package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Soheil Hamidi
 * @studentNumber 10162318
 */
public class Space {

	/**
	 * The main method calls readFile to read the required file.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		readFile();
	}

	/**
	 *  The readFile method Reads the required file and stores it 
	 *  in a list and passes the list to makePlan method.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	private static void readFile() throws FileNotFoundException {

		ArrayList<Integer> fullPlan = new ArrayList<>();
		Scanner scanner = new Scanner(new File("SpaceStation.txt"));
		String[] plan = null;

		while (scanner.hasNext()) {
			plan = scanner.next().split(" ");
			for (int i = 0; i < plan.length; i++) {
				fullPlan.add(Integer.parseInt(plan[i]));
			}
		}
		scanner.close();

		makePlan(fullPlan);
	}

	/**
	 * The makePlan method splits the content of the list to make a
	 * new list of only projects(containing project number, start
	 * time, finish time and duration) and start time, finish time 
	 * and number of projects. The method then passes these data
	 * to goGreedy, to make the best possible solution. The method 
	 * then sends the sorted result from greedy algorithm and 
	 * partitioning to printPlan to print the result.
	 * 
	 * @param fullPlan
	 */
	@SuppressWarnings("unchecked")
	private static void makePlan(ArrayList<Integer> fullPlan) {

		int start, finish, totalProjectNum;
		ArrayList<Integer> project = new ArrayList<>();
		ArrayList<ArrayList<Integer>> allProjects = new ArrayList<>();
		ArrayList<ArrayList<Integer>> myPlan = new ArrayList<>();

		start = fullPlan.get(0);
		fullPlan.remove(0);
		finish = fullPlan.get(0);
		fullPlan.remove(0);
		totalProjectNum = fullPlan.get(0);
		fullPlan.remove(0);

		for (int i = 0; i < fullPlan.size(); i += 3) {
			for (int j = 0; j < 3; j++) {
				project.add(fullPlan.get(i + j));
			}
			allProjects.add((ArrayList<Integer>) project.clone());
			project.clear();
		}

		allProjects = quicksort(allProjects, 1, "asc");

		for (int i = 0; i < allProjects.size(); i++) {
			allProjects.get(i).add(
					allProjects.get(i).get(2) - allProjects.get(i).get(1));
		}

		if (allProjects.size() == totalProjectNum) {
			myPlan = goGreedy(start, finish, allProjects, myPlan);
		}

		printPlan(myPlan, partition(myPlan));
	}

	/**
	 * The goGreedy method uses greedy algorithm to choose a best
	 * plan. It starts by checking if project list is not empty
	 * then it gets the start and finish time of the first project
	 * (the project list is sorted by start time) it checks if the
	 * project's finish time is less then start time, if true removes
	 * the project from the list and calls it self again, else make a
	 * temporary list and store all the projects that have start time
	 * less than equal to our start time and finish time of greater then
	 * start and finish time of less than our finish time. Now we make 
	 * our greedy choice by sorting the list by their finish time(large to small)
	 * so we know that we are choosing the project that goes the farthest from our
	 * start time the algorithm also checks if we have two efficient projects that have
	 * the same finish time to sort them by their duration(weight)(small to large)
	 * and choose the less duration one and it changes the our start time to the new
	 * efficient project finish time and calls it self again. If it reaches to the 
	 * project with finish time greater than our finish time it stores the project 
	 * and ends the method. All the projects that are efficient will be store in 
	 * a myPlan list.
	 * 
	 * @param start
	 * @param finish
	 * @param allProjects
	 * @param myPlan
	 * @return
	 */
	private static ArrayList<ArrayList<Integer>> goGreedy(int start,
			int finish, ArrayList<ArrayList<Integer>> allProjects,
			ArrayList<ArrayList<Integer>> myPlan) {

		if (!allProjects.isEmpty()) {
			int projectStartTime = allProjects.get(0).get(1);
			int projectFinishTime = allProjects.get(0).get(2);

			if (finish != 0) {
				if (projectFinishTime <= start) {
					allProjects.remove(0);
					goGreedy(start, finish, allProjects, myPlan);
				} else if (projectStartTime <= start
						&& projectFinishTime > start
						&& projectFinishTime < finish) {

					ArrayList<ArrayList<Integer>> temp = new ArrayList<>();

					while (allProjects.get(0).get(1) <= start) {
						temp.add(allProjects.get(0));
						allProjects.remove(0);
					}

					if (temp.size() > 1
							&& (temp.get(0).get(2) == temp.get(1).get(2))) {
						temp = quicksort(temp, 3, "asc");
						myPlan.add(temp.get(0));
						start = temp.get(0).get(2);
						goGreedy(start, finish, allProjects, myPlan);
					} else {
						temp = quicksort(temp, 2, "des");
						myPlan.add(temp.get(0));
						start = temp.get(0).get(2);
						goGreedy(start, finish, allProjects, myPlan);
					}
				} else {
					allProjects = quicksort(allProjects, 3, "asc");
					finish = 0;
					myPlan.add(allProjects.get(0));
					allProjects.remove(0);
					goGreedy(start, finish, allProjects, myPlan);

				}
			}
		}

		return myPlan;
	}

	/**
	 * The partition method makes two groups of projects that have nearly
	 * equal number of time spent in them by calculating sum of all the times
	 * dividing it by two, sorting the list by their duration(small to large)
	 * and adding them from beginning of the list to the point where it is less
	 * than sum divided by two(limit).
	 * 
	 * @param myPlan
	 * @return
	 */
	private static ArrayList<Integer> partition(
			ArrayList<ArrayList<Integer>> myPlan) {
		int total = 0, sum = 0, limit = 0;
		ArrayList<ArrayList<Integer>> firstGroup = new ArrayList<>();
		ArrayList<Integer> partition = new ArrayList<>();

		myPlan = quicksort(myPlan, 3, "asc");

		for (int i = 0; i < myPlan.size(); i++) {
			total += myPlan.get(i).get(3);
		}

		limit = total / 2;

		for (int i = 0; i < myPlan.size(); i++) {
			if (sum <= limit) {
				sum += myPlan.get(i).get(3);
				firstGroup.add(myPlan.get(i));
			}
		}

		for (int i = 0; i < firstGroup.size(); i++) {
			partition.add(firstGroup.get(i).get(0));
		}

		return partition;
	}

	/**
	 * The printPlan method prints the given plan and groups.
	 * 
	 * @param myPlan
	 * @param firstPartition
	 */
	private static void printPlan(ArrayList<ArrayList<Integer>> myPlan,
			ArrayList<Integer> firstPartition) {

		int total1 = 0, total2 = 0;

		System.out.print("Selected Projects:\t");
		for (int i = 0; i < myPlan.size(); i++) {
			System.out.print(myPlan.get(i).get(0) + "\t");
		}
		System.out.println("\n");

		System.out.print("Group 1 Projects:\t");
		for (int i = 0; i < firstPartition.size(); i++) {
			for (int j = 0; j < myPlan.size(); j++) {
				if (firstPartition.get(i) == myPlan.get(j).get(0)) {
					System.out.print(myPlan.get(j).get(0) + "\t");
					total1 += myPlan.get(j).get(3);
					myPlan.remove(j);
				}
			}
		}
		System.out.print("Total Time = " + total1 + "\n");

		System.out.print("Group 2 Projects:\t");
		for (int i = 0; i < myPlan.size(); i++) {
			System.out.print(myPlan.get(i).get(0) + "\t");
			total2 += myPlan.get(i).get(3);
		}
		System.out.print("Total Time = " + total2);
	}

	/**
	 * The quick sort method is optimized to do sorting on the specific element 
	 * in the list by giving its index in ascending or descending order.
	 * 
	 * @param plan
	 * @param index
	 * @param start
	 * @return
	 */
	private static ArrayList<ArrayList<Integer>> quicksort(
			ArrayList<ArrayList<Integer>> plan, int index, String start) {

		if ((plan.size()) <= 1) {
			return plan;
		}

		int middle = ((int) Math.ceil((double) plan.size() / 2));
		ArrayList<Integer> pivot = plan.get(middle);
		ArrayList<ArrayList<Integer>> less = new ArrayList<>();
		ArrayList<ArrayList<Integer>> greater = new ArrayList<>();

		for (int i = 0; i < plan.size(); i++) {
			if (start.equalsIgnoreCase("asc")) {
				if (plan.get(i).get(index) <= pivot.get(index)) {
					if (i == middle) {
						continue;
					}
					less.add(plan.get(i));
				} else {
					greater.add(plan.get(i));
				}
			} else if (start.equalsIgnoreCase("des")) {
				if (plan.get(i).get(index) >= pivot.get(index)) {
					if (i == middle) {
						continue;
					}
					less.add(plan.get(i));
				} else {
					greater.add(plan.get(i));
				}
			}
		}

		return concatenate(quicksort(less, index, start), pivot,
				quicksort(greater, index, start));
	}

	/**
	 * The concatenate method is used by quick sort method to join two lists.
	 * 
	 * @param less
	 * @param pivot
	 * @param greater
	 * @return
	 */
	private static ArrayList<ArrayList<Integer>> concatenate(
			ArrayList<ArrayList<Integer>> less, ArrayList<Integer> pivot,
			ArrayList<ArrayList<Integer>> greater) {

		ArrayList<ArrayList<Integer>> list = new ArrayList<>();

		for (int i = 0; i < less.size(); i++) {
			list.add(less.get(i));
		}

		list.add(pivot);

		for (int i = 0; i < greater.size(); i++) {
			list.add(greater.get(i));
		}

		return list;
	}
}

/**
 * In put
 * 
20	100
40
1	58	74
2	12	18
3	75	85
4	48	61
5	93	111
6	84	97
7	1	3
8	58	68
9	43	62
10	40	51
11	28	47
12	6	14
13	59	69
14	47	54
15	18	32
16	9	18
17	90	95
18	87	93
19	35	53
20	38	58
21	78	86
22	18	19
23	97	116
24	57	60
25	50	52
26	57	58
27	53	60
28	49	54
29	76	94
30	15	28
31	34	42
32	86	99
33	82	93
34	70	73
35	54	74
36	51	58
37	23	40
38	70	86
39	49	58
40	22	25

 */

/**
 * Out put
 * 
 * Selected Projects:	15	11	9	35	38	32	5	
 * 
 * Group 1 Projects:	32	15	38	5	Total Time = 61
 * Group 2 Projects:	11	9	35	Total Time = 58
 */