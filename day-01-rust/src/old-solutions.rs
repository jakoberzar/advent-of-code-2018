use std::fs::File;
use std::io::prelude::*;
use std::collections::HashMap;

fn old_solution() {
    let mut content = String::new();

    let mut file = File::open("input.txt").expect("Could not open the file!");

    file.read_to_string(&mut content)
        .expect("Could not read the string!");

    let content: Vec<&str> = content.as_str().trim().split("\r\n").collect();
    let result1 = content.iter().fold(0, |acc, &x| {
        let mut chars = x.trim().chars();
        let first_char = chars.next().expect("Not even one character in string!");
        let rest: i32 = chars.as_str().parse().expect("Given string is not a number!");
        match first_char {
            '-' => acc - rest,
            _ => acc + rest
        }
    });

    println!("{}", result1);

    let mut map: HashMap<i32, bool> = HashMap::new();

    let mut result2 = (0, false);
    while let (_, false) = result2 {
        result2 = content.iter().fold((result2.0, false), |(acc, found), &x| {
            if found {
                return (acc, true);
            }

            let mut chars = x.trim().chars();
            let first_char = chars.next().expect("Not even one character in string!");
            let rest: i32 = chars.as_str().parse().expect("Given string is not a number!");
            let new_acc = match first_char {
                '-' => acc - rest,
                _ => acc + rest
            };

            let found2 = map.insert(new_acc, true) != None;

            (new_acc, found2)
        });
    }

    println!("{}", result2.0);
}