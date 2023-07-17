function ConstructDate(date) {

    if (date === undefined || date === {}) {
        return new Date();
    }

    const dateArray = date.toString().split("-");

    if (dateArray.length < 2) {
        return new Date();
    }

    if (dateArray[1].substring(0, 1) === "0") {
        dateArray[1] = dateArray[1].substring(1, 2);
    }
    if (dateArray[2].substring(0, 1) === "0") {
        dateArray[2] = dateArray[2].substring(1, 2);
    } 

    return new Date(parseInt(dateArray[0]), parseInt(dateArray[1])-1, parseInt(dateArray[2]));
}

export default ConstructDate;