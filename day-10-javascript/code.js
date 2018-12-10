let seconds = 0;
let points;

function parseInput(input) {
    return input.trim().split('\n').map(line => {
        const matches = new RegExp(/position=< ?(-?\d+),  ?(-?\d+)> velocity=< ?(-?\d+),  ?(-?\d+)>/, "g").exec(line);
        return {
            position: {
                x: parseInt(matches[1]),
                y: parseInt(matches[2]),
            },
            velocity: {
                x: parseInt(matches[3]),
                y: parseInt(matches[4]),
            }
        }
    });
}

function getBounds(points) {
    return points.reduce(([xLow, xHigh, yLow, yHigh], point) => {
        return [
            Math.min(xLow, point.position.x),
            Math.max(xHigh, point.position.x),
            Math.min(yLow, point.position.y),
            Math.max(yHigh, point.position.y)
        ]
    }, [Number.MAX_VALUE, Number.MIN_VALUE, Number.MAX_VALUE, Number.MIN_VALUE]);
}

function getTable(points, [xLow, xHigh, yLow, yHigh]) {
    const arr = generate2DArray(yHigh - yLow + 1, xHigh - xLow + 1, "_");
    points.forEach(point => {
        arr[point.position.y - yLow][point.position.x - xLow] = "#";
    });
    return arr;
}

function drawTable(table) {
    const out = document.querySelector("#out");
    let html = `Time: ${seconds}s<br>`;
    for (let row = 0; row < table.length; row++) {
        for (let col = 0; col < table[row].length; col++) {
            html += table[row][col];
        }
        html += "<br>";
    }
    out.innerHTML = html;
}

function updatePoints(points) {
    for (let i = 0; i < points.length; i++) {
        points[i].position.x += points[i].velocity.x;
        points[i].position.y += points[i].velocity.y;
    }
}

function updateTable() {
    updatePoints(points);
    const bounds = getBounds(points);
    const table = getTable(points, bounds);
    seconds++;
    drawTable(table);
}

function findTheSecond() {
    let bounds = getBounds(points);
    let [xLow, xHigh, yLow, yHigh] = bounds;
    let newBounds = getBounds(points);
    let [xLowN, xHighN, yLowN, yHighN] = newBounds;

    // Loop while the bounds are getting smaller
    while (xLowN >= xLow || yLowN >= yLow || xHighN <= xHigh || yHighN <= yHigh) {
        bounds = newBounds;
        [xLow, xHigh, yLow, yHigh] = bounds;

        updatePoints(points);
        newBounds = getBounds(points);
        [xLowN, xHighN, yLowN, yHighN] = newBounds;
        seconds++;
    }

    // Go one second back
    for (let i = 0; i < points.length; i++) {
        points[i].position.x -= points[i].velocity.x;
        points[i].position.y -= points[i].velocity.y;
    }

    return --seconds;
}

function main1() {
    points = parseInput(input2);
    let second = findTheSecond();
    let bounds = getBounds(points);
    const table = getTable(points, bounds);
    drawTable(table);
    console.log("Found at second " + second);
}

main1();
