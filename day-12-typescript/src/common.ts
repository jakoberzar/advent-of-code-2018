export function print2D(param: any[][], handle = 'table2DPrint') {
    let str = '<table>';
    for (let i = 0; i < param.length; i++) {
        str += '<tr>';
        for (let j = 0; j < param[i].length; j++) {
            str += '<td>' + param[i][j] + '</td>';
        }
        str += '</tr>';
    }
    str += '</table>';
    document.getElementById(handle).innerHTML = str;
}

export function generate2DArray<T>(size1: number, size2: number, value: T): T[][] {
    let newField = [];
    for (let i = 0; i < size1; i++) {
        const newRow = [];
        for (let j = 0; j < size2; j++) newRow[j] = value;
        newField[i] = newRow;
    }
    return newField;
}

// NOT SO COMMON
export function getNeighbourSum(y: number, x: number, arr: number[]): number {
    let sum = 0;
    let lowY = y - 1 < 0 ? 0 : y - 1;
    let lowX = x - 1 < 0 ? 0 : x - 1;
    let highY = y + 1 >= arr.length ? arr.length - 1 : y + 1;
    let highX = x + 1 >= arr.length ? arr.length - 1 : x + 1;
    for (let row = lowY; row <= highY; row++) {
        for (let col = lowX; col <= highX; col++) {
            sum += arr[row][col];
        }
    }
    return sum;
}