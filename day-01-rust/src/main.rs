use std::collections::HashMap;

const INPUT: &str = include_str!(".\\..\\input.txt");

fn main() {
    star1(INPUT);
    star2(INPUT);
}

fn star1(input: &str) {
    let result: i32 = get_changes(input).iter().sum();
    println!("Star 1 result: {}", result);
}

fn get_changes(input: &str) -> Vec<i32> {
    input
        .trim()
        .lines()
        .map(|x| x.parse::<i32>().expect("String is not a number!"))
        .collect()
}

fn star2(input: &str) {
    let changes = get_changes(input);
    let mut frequency_map: HashMap<i32, bool> = HashMap::new();

    let mut frequency = 0;
    for change in changes.iter().cycle() {
        frequency += change;

        if frequency_map.insert(frequency, true) != None {
            break;
        }
    }

    println!("Star 2 result: {}", frequency);
}

fn _star2_old_fold(input: &str) {
    let changes = get_changes(input);
    let mut map: HashMap<i32, bool> = HashMap::new();

    let mut result = (0, false);
    while let (_, false) = result {
        result = changes.iter().fold((result.0, false), |(acc, found), &x| {
            if found {
                return (acc, true);
            }

            let new_acc = acc + x;
            let found = map.insert(new_acc, true) != None;
            (new_acc, found)
        });
    }

    println!("Star 2 result: {}", result.0);
}
