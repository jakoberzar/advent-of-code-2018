// TODO:
// - Improve the clearness of the code
// - Reduce the amount of duplication
// - Change the architecture of classes used

using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.Linq;

namespace Day_04
{
    class Program
    {
        static void Main(string[] args)
        {
            var lines = getInput("input.txt");
            List<Watch> watches = parseInput(lines);
            star1(watches);
            star2(watches);
        }

        static void star1(List<Watch> watches) {
            var sleepsMost = watches
                .GroupBy(watch => watch.Guard)
                .Select(group => {
                    int sum = 0;
                    for (int i = 0; i < group.Count(); i += 2) {
                        sum += group.ElementAt(i + 1).Time.Item2 - group.ElementAt(i).Time.Item2;
                    }
                    return (group.Key, sum);
                }).OrderByDescending(item => item.sum)
                .First()
                .Key;

            var guardsWatches = watches.Where(watch => watch.Guard == sleepsMost);
            int minute = getMostSleptMinute(guardsWatches).minute;

            Console.WriteLine("Guard is {0}, minute is {1}, product is {2}", sleepsMost, minute, sleepsMost * minute);
        }

        static void star2(List<Watch> watches) {
            var (guard, (minute, amount)) = watches
                .GroupBy(watch => watch.Guard)
                .Select(group => (guard: group.Key, frequentMinute: getMostSleptMinute(group)))
                .OrderByDescending(item => item.frequentMinute.amount)
                .First();

            Console.WriteLine("Guard is {0}, minute is {1}, product is {2}", guard, minute, guard * minute);
        }

        static string[] getInput(string filename) {
            return System.IO.File.ReadAllLines(filename);
        }

        static List<Watch> parseInput(string[] lines) {
            Array.Sort(lines);
            var rx = new Regex(@"\[(\d+)-(\d+)-(\d+) (\d+):(\d+)\] (?<action>\w+) #?(?<id>\d+)?");
            var watches = new List<Watch>();

            int guard = 0;
            foreach (string line in lines)
            {
                var matches = rx.Matches(line);
                if (matches.Count == 0) continue;
                var groups = matches[0].Groups;
                var date = (Int32.Parse(groups[1].Value), Int32.Parse(groups[2].Value), Int32.Parse(groups[3].Value));
                var time = (Int32.Parse(groups[4].Value), Int32.Parse(groups[5].Value));
                string action = groups["action"].Value;

                if (action == "Guard") {
                    guard = Int32.Parse(groups["id"].Value);
                    continue;
                }

                watches.Add(new Watch(date, time, guard, action));
            }
            return watches;
        }

        static (int minute, int amount) getMostSleptMinute(IEnumerable<Watch> watches) {
            var sleepCombinations = new List<(Watch, Watch)>();
            for (int i = 0; i < watches.Count(); i += 2) {
                sleepCombinations.Add((watches.ElementAt(i), watches.ElementAt(i + 1)));
            }

            var sleepMinutes = new int[60];
            foreach (var (timeFell, timeWoke) in sleepCombinations)
            {
                for (int i = timeFell.Time.Item2; i < timeWoke.Time.Item2; i++) {
                    sleepMinutes[i] += 1;
                }
            }

            int max = sleepMinutes.Max();
            int minute = 0;
            while (sleepMinutes[minute] != max) minute++;
            return (minute, max);
        }

    }

    public enum Action {
        FallsAsleep,
        WakesUp
    }

    class Watch
    {
        public (int, int, int) Date;
        public (int, int) Time;
        public int Guard;
        public Action SleepAction;

        public Watch((int, int, int) date, (int, int) time, int guard, string action) {
            Date = date;
            Time = time;
            Guard = guard;
            SleepAction = action == "falls" ? Action.FallsAsleep : Action.WakesUp;
        }
    }
}
