function parseInput(input) {
    return input.trim().split(' ').map(x => parseInt(x));
}

// Returns the node and the index after this element in array
function makeTree(input, startIdx) {
    if (startIdx >= input - 1) return undefined // Need at least two elements for each node

    const amountChildren = input[startIdx];
    const amountMetadata = input[startIdx + 1];

    const children = [];
    let metadataIndex = startIdx + 2;
    for (let i = 0; i < amountChildren; i++) {
        // Parse the child; get the child and resulting index
        const childData = makeTree(input, metadataIndex);
        metadataIndex = childData.newIndex;
        children.push(childData.node);
    }

    const metadata = [];
    for (let i = 0; i < amountMetadata; i++) {
        metadata.push(input[metadataIndex + i]);
    }
    metadataIndex += amountMetadata;

    return {
        node: new TreeNode(children, metadata),
        newIndex: metadataIndex
    }
}

function main1() {
    const nums = parseInput(input2);
    const root = makeTree(nums, 0).node;
    const sum = root.sumMetadata();
    console.log(sum);
}


function main2() {
    const nums = parseInput(input2);
    const root = makeTree(nums, 0).node;
    const sum = root.nodeValue();
    console.log(sum);
}

class TreeNode {
    constructor(children, metadata) {
        this.children = children;
        this.metadata = metadata;
    }

    // For star 1
    sumMetadata() {
        return _.sum(this.metadata) + _.sum(this.children.map(child => child.sumMetadata()))

    }

    // For star 2
    nodeValue() {
        if (this.children == 0) {
            return _.sum(this.metadata);
        } else {
            return this.metadata.reduce((acc, idx) => {
                const i = idx - 1;
                if (i < 0 || i >= this.children.length) return acc;
                else return acc + this.children[i].nodeValue();
            }, 0);
        }
    }
}

main2();