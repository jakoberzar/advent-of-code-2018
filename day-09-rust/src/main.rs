use std::cmp;
use std::collections::VecDeque;

fn main() {
    // star1(9, 25); // Simple input
    // star1(10, 1618); // Simple input
    // star1(13, 7999); // Simple input
    // star1(17, 1104); // Simple input
    // star1(21, 6111); // Simple input
    // star1(30, 5807); // Simple input
    star1(446, 71522); // Actual input
    star2(446, 71522 * 100);
}

fn star1(players_amount: usize, last_marble: usize) {
    let mut table: Vec<usize> = Vec::with_capacity(last_marble);
    let mut player_score = vec![0; players_amount];
    table.push(0);

    let mut current_idx = 0;
    for marble in 1..=last_marble {
        if marble % 23 != 0 {
            current_idx = (current_idx + 2) % table.len();
            table.insert(current_idx, marble);
        } else {
            current_idx = (current_idx + table.len() - 7) % table.len();
            let removed = table.remove(current_idx);
            let current_player = marble % players_amount;
            player_score[current_player] += marble + removed;
        }
    }

    let highest = player_score.iter().fold(0, |acc, &x| cmp::max(acc, x));
    println!("{}", highest);
}

fn star2(players_amount: usize, last_marble: usize) {
    let mut table: VecDeque<usize> = VecDeque::new();
    let mut player_score = vec![0; players_amount];
    table.push_back(0);

    for marble in 1..=last_marble {
        if marble % 23 != 0 {
            let head = table.pop_front().unwrap();
            table.push_back(head);
            table.push_back(marble);
        } else {
            for _ in 0..7 {
                let back = table.pop_back().unwrap();
                table.push_front(back);
            }
            let removed = table.pop_back().unwrap();
            let head = table.pop_front().unwrap();
            table.push_back(head);

            let current_player = marble % players_amount;
            player_score[current_player] += marble + removed;
        }
    }

    let highest = player_score.iter().fold(0, |acc, &x| cmp::max(acc, x));
    println!("{}", highest);
}
