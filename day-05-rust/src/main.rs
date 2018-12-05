use std::cmp;
use std::collections::HashSet;
use std::iter::FromIterator;

const INPUT: &str = include_str!("../input.txt");

fn main() {
    star1(INPUT);
    star2(INPUT);
}

fn star1(input: &str) {
    let result = minify(input);
    println!("Result for star 1 is {}, len is {}", result, result.len());
}

fn star2(input: &str) {
    let minified = minify(input);
    let letters = get_letters(&minified);
    let lowest = letters.iter().fold(minified.len(), |acc, &x| {
        let new_str = remove_letter_from_string(&minified, x);
        let minified = minify(&new_str);
        cmp::min(acc, minified.len())
    });
    println!("The lowest possible is {}", lowest);
}

fn minify(input: &str) -> String {
    let mut stack: Vec<char> = Vec::with_capacity(input.len());
    for c1 in input.chars() {
        match stack.last() {
            Some(&c2) => {
                if c2 != c1 && c2.to_ascii_lowercase() == c1.to_ascii_lowercase() {
                    stack.pop();
                } else {
                    stack.push(c1);
                }
            }
            None => stack.push(c1),
        }
    }
    String::from_iter(stack.into_iter())
}

fn get_letters(input: &str) -> Vec<char> {
    let mut set: HashSet<char> = HashSet::new();
    String::from(input).to_lowercase().chars().for_each(|x| {
        set.insert(x);
    });
    let mut result = Vec::from_iter(set.into_iter());
    result.sort();
    result.to_vec()
}

fn remove_letter_from_string(input: &str, letter_low: char) -> String {
    let mut new_str = String::with_capacity(input.len());
    let letter_up = letter_low.to_uppercase().next().unwrap();
    input.chars().for_each(|x| {
        if x != letter_low && x != letter_up {
            new_str.push(x);
        }
    });
    new_str
}

fn _old_minify(input: &str) -> String {
    let mut idx = 0;
    let mut s = String::from(input);
    while idx < s.len() - 1 {
        let (current, next) = _old_get_nth_chars(&s, idx);
        if current != next && current.to_lowercase().next() == next.to_lowercase().next() {
            let moved = s;
            s = String::with_capacity(moved.len());
            s.push_str(&moved[.. idx]);
            s.push_str(&moved[idx + 2..]);
            idx = if idx > 0 { idx - 1 } else { 0 };
        } else {
            idx += 1;
        }
    }
    s
}

fn _old_get_nth_chars(s: &str, idx: usize) -> (char, char) {
    let mut chars = s.chars();
    let current = chars.nth(idx).expect("Could not get char idx");
    let next = chars.next().expect("Could not get char idx + 1");
    (current, next)
}