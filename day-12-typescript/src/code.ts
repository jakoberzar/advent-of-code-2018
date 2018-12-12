import * as _ from "lodash";
import { simple, input } from "./input";

type transformations = _.Dictionary<{
    from: string;
    to: string;
}>;

function parseInput(input: string) {
    const trimmed = input.trim();
    const lines = trimmed.replace("initial state: ", "").split("\n");
    const transformations = _.drop(lines, 2)
        .map(line => {
            const split = line.split(" => ");
            return {
                from: split[0],
                to: split[1]
            }
        }).filter(x => x.to == "#");

    return {
        initial: lines[0],
        transformations: _.keyBy(transformations, 'from')
    }
}

function growPlants(initialState: string, growMap: transformations, maxGeneration = 20) {
    let [ state, plantIndex ] = appendPots(initialState, 0);

    for (let generation = 1; generation <= maxGeneration; generation++) {

        let newState = "..";
        for (let idx = 2; idx < state.length - 2; idx++) {
            const substate = state.substr(idx - 2, 5);
            newState += growMap[substate] ? growMap[substate].to : ".";
        }

        const [ appendedState, newIndex ] = appendPots(newState, plantIndex);
        console.log(`Generation ${generation}, Index: ${newIndex}, State: ${appendedState}`);

        if (appendedState != state) {
            state = appendedState;
            plantIndex = newIndex;
        } else {
            const indexDiff = newIndex - plantIndex;
            const predictedIndex = newIndex + indexDiff * (maxGeneration - generation);
            return sumPlants(appendedState, predictedIndex);
        }
    }
    return sumPlants(state, plantIndex);
}

function appendPots(state: string, plantIndex: number): [string, number] {
    const first = state.indexOf("#");
    const last = state.lastIndexOf("#");
    const newState = state.substring(first, last + 1);
    return ["..." + newState + "...", plantIndex + first - 3];
}

function sumPlants(state: string, startIndex: number): number {
    return state.split("").reduce((acc, val, idx) => {
        return val == "." ? acc : acc + (idx + startIndex)
    }, 0);
}

function main() {
    const {initial, transformations } = parseInput(input);
    main1(initial, transformations);
    main2(initial, transformations);
}

function main1(initialState: string, growMap: transformations) {
    const grown = growPlants(initialState, growMap);
    console.log(grown);
}

function main2(initialState: string, growMap: transformations) {
    const grown = growPlants(initialState, growMap, 50000000000);
    console.log(grown);
}

document.querySelector("#btn").addEventListener("click", main);

main();