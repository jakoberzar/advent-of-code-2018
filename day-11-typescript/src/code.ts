import * as _ from "lodash";
import { print2D, generate2DArray } from "./common";

let gridSize = 300;

function parseInput(input) {
    return input.trim().split('\n').map(line => parseInt(line));
}

function calculateCell(x: number, y: number, serial: number): number {
    const rackID = x + 10;
    let powerLevel = rackID * y;
    powerLevel += serial;
    powerLevel *= rackID;
    powerLevel = Math.floor((powerLevel % 1000) / 100);
    powerLevel -= 5;
    return powerLevel;
}

function generateGrid(serial: number): number[][] {
    const array = []
    for (let y = 0; y < gridSize; y++) {
        const row = new Int32Array(gridSize);
        for (let x = 0; x < gridSize; x++) {
            row[x] = calculateCell(x + 1, y + 1, serial);
        }
        array.push(row);
    }
    return array;
}

function getCell(x: number, y: number, grid: number[][]): number {
    return grid[y - 1][x - 1];
}

function calculateSquares(grid: number[][], squareSize = 3): number[][] {
    function sumColumn(x: number, y: number) {
        let sum = 0;
        for (let i = y; i < y + squareSize; i++) {
            sum += grid[i][x];
        }
        return sum;
    }
    function sumRow(x: number, y: number) {
        let sum = 0;
        for (let i = x; i < x + squareSize; i++) {
            sum += grid[y][i];
        }
        return sum;
    }

    const arraySize = gridSize - squareSize + 1;
    const cols = generate2DArray(arraySize, gridSize, 0);
    const rows = generate2DArray(gridSize, arraySize, 0);
    for (let y = 0; y < gridSize; y++) {
        for (let x = 0; x < gridSize; x++) {
            if (y < arraySize) cols[y][x] = sumColumn(x, y);
            if (x < arraySize) rows[y][x] = sumRow(x, y);
        }
    }

    const squares = generate2DArray(arraySize, arraySize, 0);
    for (let x = 0; x < squareSize; x++) {
        squares[0][0] += cols[0][x];
    }
    for (let y = 0; y < arraySize; y++) {
        if (y > 0) {
            squares[y][0] = squares[y - 1][0] - rows[y - 1][0] + rows[y + squareSize - 1][0];
        }
        for (let x = 1; x < arraySize; x++) {
            squares[y][x] = squares[y][x - 1] - cols[y][x - 1] + cols[y][x + squareSize - 1];
        }
    }

    return squares;
}

function calculateSquaresOld(grid: number[][], squareSize = 3): number[][] {
    const arraySize = gridSize - squareSize + 1;
    const array = []
    for (let y = 0; y < arraySize; y++) {
        const row = new Int32Array(arraySize);
        for (let x = 0; x < arraySize; x++) {
            let sum = 0;
            for (let iy = 0; iy < squareSize; iy++) {
                for (let ix = 0; ix < squareSize; ix++) {
                    sum += grid[y + iy][x + ix];
                }
            }
            row[x] = sum;
        }
        array.push(row);
    }
    return array;
}

function getMax(grid: number[][]): number {
    return _.max(grid.map(row => _.max(row)));
}

function findCoordinates(grid: number[][], value: number): [number, number] {
    for (let y = 0; y < grid.length; y++) {
        for (let x = 0; x < grid[y].length; x++) {
            if (grid[y][x] == value) return [x + 1, y + 1];
        }
    }
}

function findBestCoordsAndSize(grid: number[][]): number[] {
    let currentMax = Number.MIN_VALUE;
    let maxCoords = [0, 0];
    let maxSize = 0;
    for (let size = 1; size <= gridSize; size++) {
        const squares = calculateSquares(grid, size);
        const maxVal = getMax(squares);
        console.log(`Size ${size} max is ${maxVal}`)
        if (maxVal > currentMax) {
            currentMax = maxVal;
            maxCoords = findCoordinates(squares, maxVal);
            maxSize = size;
        }
    }

    maxCoords.push(maxSize);
    return maxCoords;
}

function main1() {
    const squares = calculateSquares(generateGrid(4172));
    const maxVal = getMax(squares);
    const coords = findCoordinates(squares, maxVal);
    console.log(`Found max ${maxVal} at ${coords}`);
}

function main2() {
    const grid = generateGrid(4172);
    const best = findBestCoordsAndSize(grid);
    console.log(`Found best at ${best}`);
}


// Testing would be better done with unit tests :)
function tests1() {
    console.log("Cells:");
    console.log(getCell(3, 5, generateGrid(8)));
    console.log(getCell(122, 79, generateGrid(57)));
    console.log(getCell(217, 196, generateGrid(39)));
    console.log(getCell(101, 153, generateGrid(71)));
    console.log("Square grid:");
    let squares = calculateSquares(generateGrid(18));
    let maxVal = getMax(squares);
    let coords = findCoordinates(squares, maxVal);
    console.log(`Found max ${maxVal} at ${coords}`);
    squares = calculateSquares(generateGrid(42));
    maxVal = getMax(squares);
    coords = findCoordinates(squares, maxVal);
    console.log(`Found max ${maxVal} at ${coords}`);
}

function tests2() {
    let grid = generateGrid(18);
    let best = findBestCoordsAndSize(grid);
    console.log(`Found best at ${best}`);
    grid = generateGrid(42);
    best = findBestCoordsAndSize(grid);
    console.log(`Found best at ${best}`);
}

function simpleTest() {
    gridSize = 10;
    const arr = generate2DArray(10, 10, 1);
    for (let y = 0; y < arr.length; y++) {
        for (let x = 0; x < arr[y].length; x++) {
            arr[y][x] = y + x;
        }
    }
    print2D(arr);
    let squares = calculateSquares(arr, 2);
    print2D(squares, 'table2DPrint2');
    let best = findBestCoordsAndSize(arr);
    console.log(best);
}

main2();
