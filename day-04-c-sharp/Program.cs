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
            List<Nap> naps = parseInput(lines);
            Dictionary<int, List<Nap>> guardsNaps = groupNapsByGuards(naps);

            star1(guardsNaps);
            star2(guardsNaps);
        }

        static void star1(Dictionary<int, List<Nap>> guardsNaps)
        {
            int sleepyGuard = guardsNaps
                .OrderByDescending(entry => entry.Value.Sum(nap => nap.Length))
                .First()
                .Key;

            int minute = getMostSleptMinute(guardsNaps[sleepyGuard]).minute;

            Console.WriteLine("Guard is {0}, minute is {1}, product is {2}", sleepyGuard, minute, sleepyGuard * minute);
        }

        static void star2(Dictionary<int, List<Nap>> guardsNaps)
        {
            var (guard, (minute, amount)) = guardsNaps
                .Select(group => (guard: group.Key, frequentMinute: getMostSleptMinute(group.Value)))
                .OrderByDescending(item => item.frequentMinute.amount)
                .First();

            Console.WriteLine("Guard is {0}, minute is {1}, product is {2}", guard, minute, guard * minute);
        }

        static string[] getInput(string filename)
        {
            return System.IO.File.ReadAllLines(filename).ToArray();
        }

        static List<Nap> parseInput(string[] lines)
        {
            Array.Sort(lines);
            var naps = new List<Nap>();

            int? guard = null;
            for (int i = 0; i < lines.Length; i++)
            {
                var (start, action, id) = parseLine(lines[i]);

                if (action == "Guard")
                {
                    guard = id;
                    continue;
                }

                var (end, _, _) = parseLine(lines[++i]);

                naps.Add(new Nap(start, end, guard ?? 0));
            }

            return naps;
        }

        static Dictionary<int, List<Nap>> groupNapsByGuards(List<Nap> naps)
        {
            return naps
                .GroupBy(nap => nap.Guard)
                .ToDictionary(group => group.Key, group => group.ToList());
        }

        static (DateTime date, string action, int? id) parseLine(string line)
        {
            var rx = new Regex(@"\[(\d+)-(\d+)-(\d+) (\d+):(\d+)\] (?<action>\w+) #?(?<id>\d+)?");
            var matches = rx.Matches(line);
            var groups = matches[0].Groups;

            int[] nums = groups
                .Skip(1)
                .Take(5)
                .Select(group => Int32.Parse(group.Value))
                .ToArray();
            DateTime date = new DateTime(nums[0], nums[1], nums[2], nums[3], nums[4], 0);

            string action = groups["action"].Value;

            int id;
            bool hasId = Int32.TryParse(groups["id"]?.Value, out id);

            return (date, action, hasId ? (int?)id : null);
        }

        static (int minute, int amount) getMostSleptMinute(IEnumerable<Nap> naps)
        {
            var sleepMinutes = new int[60];
            foreach (var nap in naps)
            {
                foreach (var i in nap.getMinuteRange())
                {
                    sleepMinutes[i] += 1;
                }
            }

            int max = sleepMinutes.Max();
            int minute = 0;
            while (sleepMinutes[minute] != max) minute++;
            return (minute, max);
        }
    }

    class Nap
    {
        public readonly DateTime Start, End;
        public readonly int Guard;
        public int Length
        {
            get { return End.Minute - Start.Minute; }
        }

        public Nap(DateTime start, DateTime end, int guard)
        {
            Start = start;
            End = end;
            Guard = guard;
        }

        public IEnumerable<int> getMinuteRange()
        {
            return Enumerable.Range(Start.Minute, End.Minute - Start.Minute);
        }
    }
}
