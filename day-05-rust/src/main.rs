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
    let mut lowest = minified.len();
    for &letter in letters.iter() {
        let new_str = remove_letter_from_string(&minified, letter);
        let minified = minify(&new_str);
        lowest = cmp::min(lowest, minified.len());
    }
    println!("The lowest possible is {}", lowest);
}

fn minify(input: &str) -> String {
    let mut idx = 0;
    let mut s = String::from(input);
    while idx < s.len() - 1 {
        let cloned = s.clone();
        let mut chars = cloned.chars();
        let current = chars.nth(idx).expect("Could not get char idx");
        let next = chars.next().expect("Could not get char idx + 1");
        // println!("s: {}, current: {}, next: {}, idx: {}", s, current, next, idx);
        if current != next && current.to_lowercase().next() == next.to_lowercase().next() {
            let bytes = cloned.as_bytes();
            let mut new_bytes: Vec<u8> = Vec::new();
            new_bytes.extend_from_slice(&bytes[..idx]);
            new_bytes.extend_from_slice(&bytes[idx + 2..]);
            s = String::from_utf8(new_bytes).expect("Bytes are not an ok string");
            idx = if idx > 0 { idx - 1 } else { 0 };
        } else {
            idx += 1;
        }
    }
    s
}

fn get_letters(input: &str) -> Vec<char> {
    let mut set: HashSet<char> = HashSet::new();
    String::from(input).to_lowercase().chars().for_each(|x| {
        set.insert(x);
    });
    let mut result = Vec::from_iter(set.into_iter());
    let result = result.as_mut_slice();
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
